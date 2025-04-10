package it.polimi.softeng.is25am10;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardInput;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.rmi.RMIInterface;
import it.polimi.softeng.is25am10.network.socket.SocketListener;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
public class Controller extends UnicastRemoteObject implements RMIInterface, Serializable {
    private final Map<String, Model> players = new ConcurrentHashMap<>();
    private final Map<Model, List<String>> games = new ConcurrentHashMap<>();
    private transient Map<String, Callback> callbacks;
    private transient BiConsumer<Model, Model.State.Type> stateEvent;

    private Model starting = null;
    private Model.State.Type prev = null;

    public static void main(String[] args) throws IOException {
        if(args.length > 0)
            Logger.SILENCE = Boolean.parseBoolean(args[0]);

        // read from file or create a new Controller
        load(1234, 1235, 1236);
        Logger.serverLog("controller started");

        // wait for the stop signal
        Scanner scanner = new Scanner(System.in);
        while(!scanner.nextLine().equals("stop"));

        // delete che tmp files
        File file = new File("controller.bin");
        file.delete();

        // terminate all the threads and quit
        System.exit(0);
    }

    /**
     * Loads the current controller and open RMI and SOCKET server
     * at the corresponding port.
     *
     * @param rmiPort port
     * @param socketPort1 port
     * @param socketPort2 port
     * @throws IOException if an error occurs
     */
    void loadController(int rmiPort, int socketPort1, int socketPort2) throws IOException {
        callbacks = new ConcurrentHashMap<>();

        // create the event notifier
        stateEvent = (m, state) -> {
            Logger.modelLog(m.hashCode(), "state changed to: " +state.toString());
            pushState(m);
            pushFlight(m);

            if(prev == Model.State.Type.WAITING_INPUT)
                pushCardChanges(m);

            if(state == Model.State.Type.BUILDING)
                starting = null;

            prev = state;
        };

        // open RMI server
        Registry registry = LocateRegistry.createRegistry(rmiPort);
        registry.rebind("controller", this);
        // open SOCKET server
        new SocketListener(this, socketPort1, socketPort2);

        // responsible to ping every player every 1 second
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

        // responsible to save the current game to the file
        new Thread(() -> {
            while(true){
                backup();

                try {
                    Thread.sleep(20000);
                }
                catch(InterruptedException e){
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private Controller(int rmiPort, int socketPort1, int socketPort2) throws IOException {
        super();
        loadController(rmiPort, socketPort1, socketPort2);
    }

    /**
     * Set the callback object to call when an event occur.
     *
     * @param name of the player associated to the callback
     * @param callback to call
     */
    public synchronized void setCallback(String name, Callback callback) {
        callbacks.put(name, callback);
    }

    /**
     * A player rejoined in the game.
     * These methods dump the game to the player.
     *
     * @param name of the rejoined player
     */
    private void rejoined(String name) {
        try {
            Model m = getModel(name);
            Callback callback = callbacks.get(name);

            // push the current state of the game
            callback.pushFlight(m.getFlight());
            callback.pushBoard(m.ship(name));
            callback.pushState(m.getState());
            callback.setPlayers(m.getPlayers());

            //dumps all the booked tiles
            if(m.getState() == Model.State.Type.BUILDING){
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
    }

    /**
     * Method used to add a player to a match. If there are no available matches it creates a new one.
     * @param name of the player calling the method
     * @return pawn assigned to the player
     */
    public synchronized Result<FlightBoard.Pawn> join(String name){
        //in case of disconnected player
        if(players.containsKey(name)) {
            rejoined(name);
            Logger.playerLog(getModel(name).hashCode(), name, "reconnected");
            return Result.ok(getModel(name).get(name).getPawn());
        }

        // no game is starting
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

    /*
     * Returns the model associated with the player {@code name}.
     * @param name of the player calling the method
     * @return associated model
     */
    private Model getModel(String name){
        return players.get(name);
    }

    /**
     * Execute a call back. It gets the caller method name to
     * determinate the method to call for the callback.
     *
     * @param m model to notify
     * @param error consumer to call in ase of errors while calling the player
     * @param args arguments gave to the player
     */
    private void notifyPlayers(Model m, Consumer<String> error , Object... args){
        // get the caller name
        String methodName = Thread.currentThread()
                .getStackTrace()[2]
                .getMethodName();

        // get the arguments types
        Class<?>[] types = Arrays.stream(args)
                .map(Object::getClass)
                .toArray(Class<?>[]::new);

        Method method;

        // get the method name-arguments
        try {
            method = Callback.class.getMethod(methodName, types);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // call the event for every player connected to this model
        m.getPlayers().forEach((name, _) -> {
            try {
                method.invoke(callbacks.get(name), args);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }catch(InvocationTargetException | NullPointerException e){
                // the player is unreachable
                if(error != null)
                    error.accept(name);
            }
        });
    }

    /**
     * Send the new state event to every player connected to the game
     * @param m model to notify
     */
    public void pushState(Model m) {
        notifyPlayers(m, null, m.getState());
    }

    /**
     * Send the card data to the players
     * @param m model to notify
     */
    public void pushCardData(Model m) {
        notifyPlayers(m, null, m.getCardData().getData());
    }

    /**
     * Send the card changes to the players
     * @param m model to notify
     */
    public void pushCardChanges(Model m) {
        notifyPlayers(m, null, m.getChanges());
    }

    /**
     * Notify the player to give the card input
     * @param name player to notify
     */
    public void askForInput(String name) {
        try {
            callbacks.get(name).askForInput();
        } catch (Exception _) {
            setInput(name, CardInput.disconnected());
        }
    }

    /**
     * Notify every player that a new tile has been given
     * @param m model to notify
     * @param t gave tile
     */
    public void gaveTile(Model m, Tile t) {
        notifyPlayers(m, null, t);
    }

    /**
     * Notify every player that a tile has been taken.
     *
     * @param m model to notify
     * @param t taken tile
     */
    public void gotTile(Model m, Tile t) {
        notifyPlayers(m, null, t);
    }

    /**
     * Update the flight of every player of the model
     *
     * @param m model to notify
     */
    public void pushFlight(Model m) {
        notifyPlayers(m, null, m.getFlight());
    }

    /**
     * Push the players list to every client.
     *
     * @param m model to notify
     */
    public void setPlayers(Model m) {
        notifyPlayers(m, null, m.getPlayers());
    }

    /**
     * Ask a player how many players should this match contains.
     *
     * @param name player to ask
     * @return the number of given players
     */
    public int askHowManyPlayers(String name){
        try {
            return callbacks.get(name).askHowManyPlayers();
        } catch (Exception _) {
            return 2;
        }
    }

    public void ping(){
        for(Model m: games.keySet())
            notifyPlayers(m, (name) ->{
                if(m.getState() == Model.State.Type.ALIEN_INPUT) {
                    if(m.init(name, Optional.empty(), Optional.empty()).isOk())
                        Logger.playerLog(m.hashCode(), name, "player unreachable, setting default aliens");
                }

                else if(m.getState() == Model.State.Type.WAITING_INPUT &&
                        name.equals(m.getNextToPlay())) {
                    Logger.playerLog(m.hashCode(), name, "player unreachable, setting default card input");
                    setInput(name, CardInput.disconnected());
                }
            });
    }

    /**
     * Store the current controller to the disk.
     */
    private void backup() {
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("controller.bin"));
            oos.writeObject(this);
            oos.close();
            Logger.serverLog("backup done");
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Load the controller from the disk if its present or create a new one
     * if there is no running game in the disk.
     *
     * @param rmiPort port
     * @param socketPort1 port
     * @param socketPort2 port
     */
    private static void load(int rmiPort, int socketPort1, int socketPort2) throws IOException {
        try {
            // read the file from the disk
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("controller.bin"));
            Controller controller = (Controller) ois.readObject();
            ois.close();

            controller.loadController(rmiPort, socketPort1, socketPort2);
            //init the games
            controller.games.keySet().forEach(model -> {
                model.loadTimer();
                model.setEvent(controller.stateEvent);
            });
        } catch (IOException | ClassNotFoundException e) {
            //no file or some error, create a new controller
            new Controller(rmiPort, socketPort1, socketPort2);
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
    public Result<Tile> bookTile(String name, Tile t) {
        return getModel(name).bookTile(name, t);
    }

    @Override
    public Result<Tile> useBookedTile(String name, Tile t, Tile.Rotation rotation, Coordinate c) {
        return getModel(name).useBookedTile(name, t, rotation, c);
    }

    @Override
    public Result<String> remove(String name, Coordinate c) {
        return getModel(name).remove(name, c);
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
    public Result<Card> drawCard(String name) {
        Result<Card> res = getModel(name).drawCard(name);
        if(res.isOk()){
            pushCardData(getModel(name));

            if(res.getData().needInput)
                askForInput(getModel(name).getNextToPlay());
        }
        return res;
    }

    /**
     * Method used to set the player input for the preparation of the card (so that it can be played afterward).
     * Calls {@code setInput(name, json)} in {@code Model} class. Checks if the player already sent the input.
     * @param name of the player calling the method
     * @param input contains the data and instructions of the player
     * @return
     */
    @Override
    public Result<CardInput> setInput(String name, CardInput input) {
        Result<CardInput> res = getModel(name).setInput(name, input);

        if(res.isOk() && getModel(name).getChanges() == null)
            askForInput(getModel(name).getNextToPlay());

        return res;
    }

    /**
     * Method used to get the specific data of a card. Calls {@code getCardData()} in {@code Model} class.
     * @param name of the player calling the method
     * @return data of the card
     */
    @Override
    public Result<CardData> getCardData(String name) {
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
