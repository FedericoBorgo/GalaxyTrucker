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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private final Map<String, Model> players = new ConcurrentHashMap<>();
    private final Map<Model, List<String>> games = new ConcurrentHashMap<>();
    private final Map<String, Callback> callbacks = new ConcurrentHashMap<>();

    private Model starting = null;

    private final BiConsumer<Model, Model.State.Type> stateEvent = (m, state) -> {
        Logger.modelLog(m.hashCode(), "state changed to: " +state.toString());
        pushState(m);
        pushFlight(m);

        if(state == Model.State.Type.BUILDING)
            starting = null;

    };

    // Constructor method
    public Controller(int port) throws RemoteException {
        super();
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind("controller", this);
        Logger.serverLog("controller started");

        new Thread(() -> {
            while(true){
                ping();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public synchronized void setCallback(String name, Callback callback) {
        callbacks.put(name, callback);
    }

    /**
     * Method used to add a player to a match. If there are no available matches it creates a new one.
     * @param name of the player calling the method
     * @return pawn assigned to the player
     */
    public synchronized Result<FlightBoard.Pawn> join(String name){
        //in case of disconnected player
        if(players.containsKey(name)) {
            Callback callback = callbacks.get(name);
            Model m = players.get(name);
            try {
                callback.pushFlight(m.getFlight());
                callback.pushBoard(m.ship(name));
                callback.pushState(m.getStatus());
                callback.setPlayers(m.getPlayers());

                if(m.getStatus() == Model.State.Type.BUILDING){
                    m.getSeenTiles().getData().forEach(t -> {
                        try {
                            callback.gaveTile(t);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

            Logger.playerLog(m.hashCode(), name, "reconnected");

            return Result.ok(m.get(name).getPawn());
        }

        if(starting == null) {
            starting = new Model(askHowManyPlayers(name), stateEvent);
            games.put(starting, Collections.synchronizedList(new ArrayList<>()));
        }

        Model temp = starting;
        games.get(starting).add(name);
        players.put(name, starting);
        Logger.playerLog(starting.hashCode(), name, "joined");
        Result<FlightBoard.Pawn> pawn = starting.addPlayer(name);

        if(pawn.isOk())
            setPlayers(temp);

        return pawn;
    }

    // Private methods BEGIN

    /*
     * Returns the model associated with the player {@code name}.
     * @param name of the player calling the method
     * @return associated model
     */
    private Model getModel(String name){
        return players.get(name);
    }

    private void notifyPlayers(Model m, Consumer<String> error , Object... args){
        String methodName = Thread.currentThread()
                .getStackTrace()[2]
                .getMethodName();

        Class<?>[] types = Arrays.stream(args)
                .map(Object::getClass)
                .toArray(Class<?>[]::new);

        Method method;

        try {
            method = Callback.class.getMethod(methodName, types);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        m.getPlayers().forEach((name, pawn) -> {
            try {
                method.invoke(callbacks.get(name), args);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }catch(InvocationTargetException e){
                if(error != null)
                    error.accept(name);
            }
        });
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

        if(result.isOk())
            pushFlight(m);

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
    public ShipBoard getShip(String name) {
        return getModel(name).ship(name);
    }

    @Override
    public Result<String> init(String name, Result<Coordinate> purple, Result<Coordinate> brown) {
        return getModel(name).init(name, purple.isOk()? Optional.of(purple.getData()) : Optional.empty(),
                brown.isOk()? Optional.of(brown.getData()) : Optional.empty());
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
        Result<String> res = getModel(name).giveTile(t);
        if(res.isErr())
            return Result.err("errore");

        gaveTile(getModel(name), t);
        return res;
    }

    /**
     * Method used to get a certain tile from the face-up tiles. Calls {@code getTileFromSeen(t)} in {@code Model} class.
     * @param name of the player calling the method
     * @param t the chosen tile
     * @return {@code t} from the face-up tiles
     */
    @Override
    public Result<Tile> getTileFromSeen(String name, Tile t) {
        Result<Tile> res = getModel(name).getTileFromSeen(t);
        if(res.isErr())
            return Result.err("errore");

        gotTile(getModel(name), t);
        return res;
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
            pushCard(getModel(name));

            if(res.getData().needInput()){
                askForInput(getModel(name).getNextToPlay());
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
    public Result<String> setInput(String name, String json) {
        Result<JSONObject> res = getModel(name).setInput(name, new JSONObject(json));

        if(res.isOk() && !res.getData().has("played"))
            askForInput(getModel(name).getNextToPlay());

        return res.isOk()? Result.ok(res.getData().toString()) : Result.err(res.getReason());
    }

    /**
     * Method used to get the specific data of a card. Calls {@code getCardData()} in {@code Model} class.
     * @param name of the player calling the method
     * @return data of the card
     */
    @Override
    public Result<String> getCardData(String name) {
        Result<JSONObject> res = getModel(name).getCardData();
        return res.isOk()? Result.ok(res.getData().toString()) : Result.err(res.getReason());
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

    public void setPlayers(Model m) {
        notifyPlayers(m, null, m.getPlayers());
    }

    public int askHowManyPlayers(String name){
        try {
            return callbacks.get(name).askHowManyPlayers();
        } catch (Exception _) {
            return 2;
        }
    }

    public void pushState(Model m) {
        notifyPlayers(m, null, m.getStatus());
    }

    public void pushCard(Model card) {

    }

    public void pushCardChanges(Model m) {

    }

    public void askForInput(String name) {
        try {
            callbacks.get(name).askForInput();
        } catch (Exception _) {
            getModel(name).setInput(name, new JSONObject());
        }
    }

    public void gaveTile(Model m, Tile t) {
        notifyPlayers(m, null, t);
    }

    public void gotTile(Model m, Tile t) {
        notifyPlayers(m, null, t);
    }

    public void pushBoard(ShipBoard board) {

    }

    public void pushFlight(Model m) {
        notifyPlayers(m, null, m.getFlight());
    }

    public void ping(){
        for(Model m: games.keySet())
            notifyPlayers(m, (name) ->{
                if(m.getStatus() == Model.State.Type.ALIEN_INPUT) {
                    if(m.init(name, Optional.empty(), Optional.empty()).isOk())
                        Logger.playerLog(m.hashCode(), name, "player unreachable, setting default aliens");
                }

                else if(m.getStatus() == Model.State.Type.WAITING_INPUT &&
                        name.equals(m.getNextToPlay())) {
                    Logger.playerLog(m.hashCode(), name, "player unreachable, setting default card input");
                    m.setInput(name, new JSONObject());
                }
            });
    }
}
