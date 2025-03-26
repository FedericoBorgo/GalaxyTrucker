package it.polimi.softeng.is25am10;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.network.EventNotifier;
import it.polimi.softeng.is25am10.network.PlayerControls;
import org.json.JSONObject;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Controller implements PlayerControls {
    private final Map<String, Model> nameToGame;
    private final Map<Model, List<String>> gameToPlayers;
    private final Map<String, EventNotifier> nameToNotifier;
    private Model starting;
    private final List<String> joining;

    private BiConsumer<Model, Model.State.Type> stateChanged = new BiConsumer<Model, Model.State.Type>() {
        @Override
        public void accept(Model model, Model.State.Type type) {
            forEveryOne(model, (n) -> {
                n.notifyState(type);
            });

            if(type == Model.State.Type.BUILDING){
                gameToPlayers.put(starting, joining);
                joining.clear();
                starting = null;
            }

            if(type == Model.State.Type.ALIEN)
                updatePosition(model);
        }
    };

    public Controller(){
        nameToGame = new HashMap<>();
        gameToPlayers = new HashMap<>();
        nameToNotifier = new HashMap<>();
        joining = new ArrayList<>();
        starting = null;
    }

    public synchronized void join(String name, EventNotifier n){
        nameToNotifier.put(name, n);

        if(nameToGame.containsKey(name))
            return;

        if(starting == null)
            starting = new Model(n.askHowManyPlayers(), stateChanged);

        starting.addPlayer(name);
        joining.add(name);
        nameToGame.put(name, starting);

        forEveryOneExept(starting, name, (player) -> {
            player.joinedPlayer(name);
        });
    }

    private Model get(String name){
        return nameToGame.get(name);
    }

    private void forEveryOneExept(Model m, String name, Consumer<EventNotifier> caller){
        gameToPlayers.get(m).forEach(playerName -> {
            if(playerName.equals(name))
                return;

            caller.accept(nameToNotifier.get(playerName));
        });
    }

    private void forEveryOne(Model m, Consumer<EventNotifier> caller){
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

    // player interaction

    @Override
    public Result<Integer> moveTimer(String name) {
        Model m = get(name);
        Result<Integer> result = m.moveTimer(name);

        if(result.isOk())
            forEveryOneExept(m, name, EventNotifier::movedTimer);

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
