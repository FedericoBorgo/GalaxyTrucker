package it.polimi.softeng.is25am10;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.network.ClientToServer;
import it.polimi.softeng.is25am10.network.Placeholder;
import it.polimi.softeng.is25am10.network.ServerToClient;
import org.json.JSONObject;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Controller extends UnicastRemoteObject implements ClientToServer {
    private final Map<String, Model> nameToGame = new HashMap<>();
    private final Map<Model, List<String>> gameToPlayers = new HashMap<>();
    private final Map<Model, Integer> modelID = new HashMap<>();

    private final Map<String, ServerToClient> nameToNotifier = new HashMap<>();
    private Model starting = null;
    private int counter = 0;

    private BiConsumer<Model, Model.State.Type> stateChanged = (model, type) -> {
        Logger.modelLog(getID(model), "state changed to: " +type.toString());

        if(type == Model.State.Type.BUILDING)
            starting = null;

        forEveryOne(model, (n) -> {
            n.notifyState(type);
        });

        if(type == Model.State.Type.ALIEN)
            updatePosition(model);
    };

    public Controller() throws RemoteException {
        super();

        Registry registry = LocateRegistry.createRegistry(1234);
        try {
            registry.rebind("controller", this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void join(String name){
        nameToNotifier.put(name, new Placeholder());

        if(nameToGame.containsKey(name))
            return;

        if(starting == null) {
            starting = new Model(new Placeholder().askHowManyPlayers(), stateChanged);
            modelID.put(starting, counter++);
            gameToPlayers.put(starting, new ArrayList<>());
        }

        forEveryOneExept(starting, name, caller -> {
            caller.joinedPlayer(name);
        });

        gameToPlayers.get(starting).add(name);
        nameToGame.put(name, starting);
        Logger.playerLog(getID(starting), name, "joined");
        starting.addPlayer(name);
    }

    private Model get(String name){
        return nameToGame.get(name);
    }

    private void forEveryOneExept(Model m, String name, Consumer<ServerToClient> caller){
        gameToPlayers.get(m).forEach(playerName -> {
            if(playerName.equals(name))
                return;

            caller.accept(nameToNotifier.get(playerName));
        });
    }

    private void forEveryOne(Model m, Consumer<ServerToClient> caller){
        gameToPlayers.get(m).forEach(playerName -> {
            caller.accept(nameToNotifier.get(playerName));
        });
    }

    private void updatePosition(Model model){
        List<FlightBoard.Pawn> order = model.getFlight().order;
        List<Integer> offset = model.getFlight().offset;

        forEveryOne(model, (n) -> {
            n.pushPositions(order, offset);
        });
    }

    private int getID(Model model){
        return modelID.get(model);
    }

    // player interaction

    @Override
    public Result<Integer> moveTimer(String name) {
        Model m = get(name);
        Result<Integer> result = m.moveTimer(name);

        if(result.isOk())
            forEveryOneExept(m, name, ServerToClient::movedTimer);

        return result;
    }

    @Override
    public Result<String> setReady(String name) {
        return get(name).setReady(name);
    }

    @Override
    public Result<String> quit(String name) {
        return get(name).quit(name);
    }

    @Override
    public Result<Tile> setTile(String name, Coordinate c, Tile t, Tile.Rotation rotation) {
        return get(name).setTile(name, c, t, rotation);
    }

    @Override
    public Result<Tile> getTile(String name, Coordinate c) {
        return get(name).getTile(name, c);
    }

    @Override
    public Tile.Rotation getRotation(String name, Coordinate c) {
        return get(name).getRotation(name, c);
    }

    @Override
    public Result<Tile> bookTile(String name, Tile t) {
        return get(name).bookTile(name, t);
    }

    @Override
    public Result<Tile> useBookedTile(String name, Tile t, Tile.Rotation rotation, Coordinate c) {
        return get(name).useBookedTile(name, t, rotation, c);
    }

    @Override
    public List<Tile> getBooked(String name) {
        return get(name).getBooked(name);
    }

    @Override
    public Result<String> remove(String name, Coordinate c) {
        return get(name).remove(name, c);
    }

    @Override
    public Set<Coordinate> checkShip(String name) {
        return get(name).checkShip(name);
    }

    @Override
    public ShipBoard.CompressedShipBoard getShip(String name) {
        return get(name).getShip(name);
    }

    @Override
    public Result<String> init(String name, Optional<Coordinate> purple, Optional<Coordinate> brown) {
        return get(name).init(name, purple, brown);
    }

    @Override
    public List<GoodsBoard.Type> getReward(String name) {
        return get(name).getReward(name);
    }

    @Override
    public Result<Integer> placeReward(String name, GoodsBoard.Type t, Coordinate c) {
        return get(name).placeReward(name, t, c);
    }

    @Override
    public int getCash(String name) {
        return get(name).getCash(name);
    }

    @Override
    public Result<Integer> drop(String name, Coordinate c) {
        return get(name).drop(name, c);
    }

    @Override
    public Result<Integer> drop(String name, Coordinate c, GoodsBoard.Type t) {
        return get(name).drop(name, c, t);
    }

    @Override
    public Result<String> setCannonsToUse(String name, Map<Tile.Rotation, Integer> map) {
        return get(name).setCannonsToUse(name, map);
    }

    @Override
    public Result<Tile> drawTile(String name) {
        return get(name).drawTile(name);
    }

    @Override
    public Result<List<Tile>> getSeenTiles(String name) {
        return get(name).getSeenTiles(name);
    }

    @Override
    public Result<String> giveTile(String name, Tile t) {
        return get(name).giveTile(name, t);
    }

    @Override
    public Result<Tile> getTileFromSeen(String name, Tile t) {
        return get(name).getTileFromSeen(name, t);
    }

    @Override
    public Result<Card.CompressedCard> drawCard(String name) {
        Result<Card.CompressedCard> res = get(name).drawCard(name);
        if(res.isOk()){
            forEveryOneExept(get(name), name, player ->{
                player.pushCard(res.getData());
            });
        }
        return res;
    }

    @Override
    public Result<JSONObject> setInput(String name, JSONObject json) {
        Result<JSONObject> res = get(name).setInput(name, json);
        if(res.isOk()){
            JSONObject obj = res.getData();
            if(obj.has("played"))
                forEveryOne(get(name), player ->{
                    player.pushCardChanges(obj);
                });
        }
        return get(name).setInput(name, json);
    }

    @Override
    public Result<JSONObject> getCardData(String name) {
        return get(name).getCardData(name);
    }

    @Override
    public Result<Card[][]> getVisible(String name) {
        return get(name).getVisible(name);
    }
}
