package it.polimi.softeng.is25am10;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.rmi.RMIInterface;
import org.json.JSONObject;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This class gives an interface between the model and the view. It shows available methods to the clients
 * which won't affect directly the model, but this class will call the methods of the model.
 * Most of the methods can be called on different models, each match has its own model,
 * this is why each of them has the param {@code name} to identify the specific model through the
 * name of the player calling the method.
 */
public class Controller extends UnicastRemoteObject implements RMIInterface {
    private final Map<String, Model> nameToGame = new HashMap<>();
    private final Map<Model, List<String>> gameToPlayers = new HashMap<>();
    private final Map<Model, Integer> modelID = new HashMap<>();

    private final Map<String, Callback> nameToCallback = new HashMap<>();
    private Model starting = null;
    private int counter = 0;

    private BiConsumer<Model, Model.State.Type> stateChanged = (model, type) -> {
        Logger.modelLog(getID(model), "state changed to: " +type.toString());

        if(type == Model.State.Type.BUILDING)
            starting = null;

        forEveryOne(model, (n) -> {
            try {
                n.notifyState(type);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        if(type == Model.State.Type.ALIEN_INPUT)
            updatePosition(model);

        if(type == Model.State.Type.DRAW_CARD){
            forEveryOne(model, player ->{
                try {
                    player.pushCardChanges(model.getChanges());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    };

    // Constructor method
    public Controller(int port) throws RemoteException {
        super();
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind("controller", this);
        Logger.serverLog("controller started");
    }

    public synchronized void setCallback(String name, Callback callback) {
        nameToCallback.put(name, callback);
    }

    /**
     * Method used to add a player to a match. If there are no available matches it creates a new one.
     * @param name of the player calling the method
     * @return pawn assigned to the player
     */
    public synchronized Result<FlightBoard.Pawn> join(String name){
        if(nameToGame.containsKey(name))
            return Result.err("already joined");

        if(starting == null) {
            try {
                starting = new Model(nameToCallback.get(name).askHowManyPlayers(), stateChanged);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            modelID.put(starting, counter++);
            gameToPlayers.put(starting, new ArrayList<>());
        }

        Model temp = starting;
        gameToPlayers.get(starting).add(name);
        nameToGame.put(name, starting);
        Logger.playerLog(getID(starting), name, "joined");
        Result<FlightBoard.Pawn> pawn = starting.addPlayer(name);

        if(pawn.isOk()){
            forEveryOne(temp, caller -> {
                try {
                    caller.setPlayers(temp.getPlayers());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return pawn;
    }

    // Private methods BEGIN

    /*
     * Returns the model associated with the player {@code name}.
     * @param name of the player calling the method
     * @return associated model
     */
    private Model getModel(String name){
        return nameToGame.get(name);
    }

    private void forEveryOneExcept(Model m, String name, Consumer<Callback> caller){
        gameToPlayers.get(m).forEach(playerName -> {
            if(playerName.equals(name))
                return;

            caller.accept(nameToCallback.get(playerName));
        });
    }

    private void forEveryOne(Model m, Consumer<Callback> caller){
        gameToPlayers.get(m).forEach(playerName -> {
            caller.accept(nameToCallback.get(playerName));
        });
    }

    private void updatePosition(Model model){
        List<FlightBoard.Pawn> order = model.getFlight().order;
        List<Integer> offset = model.getFlight().offset;

        forEveryOne(model, (n) -> {
            try {
                n.pushPositions(order, offset);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private int getID(Model model){
        return modelID.get(model);
    }

    // Private methods END

    public static boolean isRMI(){
        try {
            RemoteServer.getClientHost();
            return true;
        } catch (ServerNotActiveException e) {
            return false;
        }
    }

    // Player interaction

    /**
     * Calls the method moveTimer of the model; checks if the
     * @param name of the player calling the method
     * @return the position of the timer
     */
    @Override
    public Result<Integer> moveTimer(String name) {
        Model m = getModel(name);
        Result<Integer> result = m.moveTimer();

        if(result.isOk()) {
            forEveryOneExcept(m, name, p -> {
                try {
                    p.movedTimer();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
            Logger.playerLog(getID(m), name,"moved timer");
        }

        return result;
    }

    @Override
    public Result<String> setReady(String name) {
        return getModel(name).setReady(name);
    }

    @Override
    public Result<String> quit(String name) {
        return getModel(name).quit(name);
    }

    @Override
    public Result<Tile> setTile(String name, Coordinate c, Tile t, Tile.Rotation rotation) {
        return getModel(name).setTile(name, c, t, rotation);
    }

    @Override
    public Result<Tile> getTile(String name, Coordinate c) {
        return getModel(name).getTile(name, c);
    }

    @Override
    public Tile.Rotation getRotation(String name, Coordinate c) {
        return getModel(name).getRotation(name, c);
    }

    @Override
    public Result<Tile> bookTile(String name, Tile t) {
        return getModel(name).bookTile(name, t);
    }

    @Override
    public Result<Tile> useBookedTile(String name, Tile t, Tile.Rotation rotation, Coordinate c) {
        return getModel(name).useBookedTile(name, t, rotation, c);
    }

    @Override
    public List<Tile> getBooked(String name) {
        return getModel(name).getBooked(name);
    }

    @Override
    public Result<String> remove(String name, Coordinate c) {
        return getModel(name).remove(name, c);
    }

    @Override
    public Set<Coordinate> checkShip(String name) {
        return getModel(name).checkShip(name);
    }

    @Override
    public ShipBoard.CompressedShipBoard getShip(String name) {
        return getModel(name).getShip(name);
    }

    @Override
    public Result<String> init(String name, Optional<Coordinate> purple, Optional<Coordinate> brown) {
        return getModel(name).init(name, purple, brown);
    }

    @Override
    public List<GoodsBoard.Type> getReward(String name) {
        return getModel(name).getReward(name);
    }

    @Override
    public Result<Integer> placeReward(String name, GoodsBoard.Type t, Coordinate c) {
        return getModel(name).placeReward(name, t, c);
    }

    @Override
    public int getCash(String name) {
        return getModel(name).getCash(name);
    }

    @Override
    public Result<Integer> drop(String name, Coordinate c) {
        return getModel(name).drop(name, c);
    }

    @Override
    public Result<Integer> drop(String name, Coordinate c, GoodsBoard.Type t) {
        return getModel(name).drop(name, c, t);
    }

    @Override
    public Result<String> setCannonsToUse(String name, Map<Tile.Rotation, Integer> map) {
        return getModel(name).setCannonsToUse(name, map);
    }

    @Override
    public Result<Tile> drawTile(String name) {
        return getModel(name).drawTile();
    }

    /**
     * Retrieves the list containing all the face-up tiles. Calls {@code getSeenTiles()} in {@code Model} class.
     * @param name of the player calling the method
     * @return The seen list.
     */
    @Override
    public Result<List<Tile>> getSeenTiles(String name) {
        return getModel(name).getSeenTiles();
    }

    /**
     * Method used to add a tile to the face-up tiles. Calls {@code giveTile()} in {@code Model} class.
     * @param name of the player calling the method
     * @param t the tile to be added
     * @return {@code Result} of the operation
     */
    @Override
    public Result<String> giveTile(String name, Tile t) {
        return getModel(name).giveTile(t);
    }

    /**
     * Method used to get a certain tile from the face-up tiles. Calls {@code getTileFromSeen(t)} in {@code Model} class.
     * @param name of the player calling the method
     * @param t the chosen tile
     * @return {@code t} from the face-up tiles
     */
    @Override
    public Result<Tile> getTileFromSeen(String name, Tile t) {
        return getModel(name).getTileFromSeen(t);
    }

    /**
     * Method used by the leader to draw a card. Can only be used if the model is in the DRAW state.
     * Calls {@code drawCard()} in {@code Model} class.
     * @param name of the specific model, the leader can be identified by the model.
     * @return the drawn card
     */
    @Override
    public Result<Card.CompressedCard> drawCard(String name) {
        Result<Card.CompressedCard> res = getModel(name).drawCard(name);
        if(res.isOk()){
            forEveryOneExcept(getModel(name), name, player ->{
                try {
                    player.pushCard(res.getData());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });

            if(res.getData().needInput()){
                try {
                    nameToCallback.get(getModel(name).getNextToPlay()).askForInput();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return res;
    }

    /**
     * Method used to set the player input for the preparation of the card (so that it can be played afterward).
     * Calls {@code setInput(name, json)} in {@code Model} class. Checks if the player already sent the input.
     * @param name of the player calling the method
     * @param json contains the data and instructions of the player
     * @return
     */
    @Override
    public Result<JSONObject> setInput(String name, JSONObject json) {
        Result<JSONObject> res = getModel(name).setInput(name, json);
        if(res.isOk()){
            JSONObject obj = res.getData();
            if(!obj.has("played")){
                try {
                    nameToCallback.get(getModel(name).getNextToPlay()).askForInput();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return getModel(name).setInput(name, json);
    }

    /**
     * Method used to get the specific data of a card. Calls {@code getCardData()} in {@code Model} class.
     * @param name of the player calling the method
     * @return data of the card
     */
    @Override
    public Result<JSONObject> getCardData(String name) {
        return getModel(name).getCardData();
    }

    /**
     * Method used to get the piles of visible cards. Calls {@code getVisible()} in {@code Model} class.
     * @param name of the player calling the method
     * @return 3 piles of visible cards
     */
    @Override
    public Result<Card[][]> getVisible(String name) {
        return getModel(name).getVisible();
    }
}
