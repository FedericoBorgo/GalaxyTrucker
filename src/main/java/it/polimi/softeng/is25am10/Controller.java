package it.polimi.softeng.is25am10;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.State;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardInput;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    /**
     * Associate players name to a model.
     */
    private final Map<String, Model> games = new ConcurrentHashMap<>();

    /**
     * Associate a player name to their corresponding callbacks.
     */
    private transient Map<String, Callback> callbacks;

    /**
     * Store the disconnected players
     */
    private final Map<Model, HashSet<String>> disconnected = new ConcurrentHashMap<>();

    /**
     * Method to call when the game state changed.
     */
    private transient BiConsumer<Model, State.Type> stateEvent;

    ///  starting game
    private Model starting = null;

    public static boolean ready = false;

    private transient ExecutorService execService = null;

    public static void main(String[] args) throws IOException {
        if(args.length > 0)
            Logger.SILENCE = Boolean.parseBoolean(args[0]);

        // read from file or create a new Controller
        load(1234, 1235, 1236);
        Logger.serverLog("controller started");

        ready = true;

        if(args.length == 0){
            // wait for the stop signal
            Scanner scanner = new Scanner(System.in);

            while(!scanner.nextLine().equals("stop"));

            // delete che tmp files
            File file = new File("controller.bin");
            file.delete();

            // terminate all the threads and quit
            System.exit(0);
        }
    }

    void loadEvent(){
        // create the event notifier
        stateEvent = (m, state) -> {
            Logger.modelLog(m.hashCode(), "state changed to: " +state.toString());
            pushState(m);


            execService.execute(() -> pushFlight(m));
            execService.execute(() -> pushPlayers(m));

            if(state == State.Type.DRAW_CARD) {
                execService.execute(() -> pushDropped(m));
                execService.execute(() -> pushCannons(m));
            }

            if(state == State.Type.BUILDING)
                starting = null;

            if(state == State.Type.PAY_DEBT)
                execService.execute(() -> pushDropped(m));
        };
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

        // open RMI server
        Registry registry = LocateRegistry.createRegistry(rmiPort);
        registry.rebind("controller", this);
        // open SOCKET server
        new SocketListener(this, socketPort1, socketPort2);

        ping();

        // ping the player every 500ms
        new Timer("PING_TIMER").scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ping();
            }
        }, 0, 500);

        // run a controller backup every 5s
        new Timer("BACKUP_TIMER").scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //backup();
            }
        }, 0, 5000);

        // notify the clock for every game and player every 500ms
        new Timer("PUSH_SECONDS_TIMER").scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                games.values().forEach(m -> {
                    if(m.getState() == State.Type.BUILDING)
                        pushSecondsLeft(m);
                });
            }
        }, 0, 500);

        new Timer("AUTOMATIC_INPUT_TIMER").scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handleAutomaticInput();
            }
        }, 0, 2000);

        execService = Executors.newFixedThreadPool(8);
    }

    private Controller(int rmiPort, int socketPort1, int socketPort2) throws IOException {
        super();
        loadEvent();
        loadController(rmiPort, socketPort1, socketPort2);
    }

    /**
     * Set the callback object to call when an event occur.
     *
     * @param name of the player associated to the callback
     * @param callback to call
     */
    public void setCallback(String name, Callback callback) {
        synchronized (callback){
            Model m = games.getOrDefault(name, null);

            if(m != null)
                if(!disconnected.get(m).contains(name))
                    return;

            callbacks.put(name, callback);
        }
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

            callbacks.get(name).pushModel(m);
            disconnected.get(m).remove(name);
            pushPlayers(m);
            m.removeIgnore(name);

            if(m.nPlayers - disconnected.get(m).size() >= 2
                    && m.getState() == State.Type.PAUSED)
                m.resume();

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method used to add a player to a match. If there are no available matches it creates a new one.
     * @param name of the player calling the method
     * @return pawn assigned to the player
     */
    public Result<FlightBoard.Pawn> join(String name){
        synchronized (games){
            //in case of disconnected player
            if(games.containsKey(name)) {
                Model m = games.get(name);

                if(!disconnected.get(m).contains(name)) {
                    Logger.serverLog("rifiutato: " +name + ", giocatore già connesso");
                    return Result.err("giocatore già connesso");
                }

                rejoined(name);
                Logger.playerLog(m.hashCode(), name, "reconnected");
                return Result.ok(m.get(name).getPawn());
            }

            // no game is starting
            if(starting == null) {
                starting = new Model(askHowManyPlayers(name), stateEvent);
                disconnected.put(starting, new HashSet<>());
            }

            Model temp = starting;
            games.put(name, starting);
            Logger.playerLog(starting.hashCode(), name, "joined");
            Result<FlightBoard.Pawn> pawn = starting.addPlayer(name);

            if(pawn.isOk())
                execService.execute(() -> pushPlayers(temp));

            return pawn;
        }
    }

    /*
     * Returns the model associated with the player {@code name}.
     * @param name of the player calling the method
     * @return associated model
     */
    private Model getModel(String name){
        return games.get(name);
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
        Method method;

        // get the method name-arguments
        try {
            method = Callback.class.getMethod(ClientInterface.getCallerName(), ClientInterface.getClasses(args));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        Set<String> dis = disconnected.get(m);

        // call the event for every player connected to this model
        m.getPlayers().forEach((name, _) -> {
            if(dis.contains(name))
                return;
            try {
                method.invoke(callbacks.get(name), args);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException | NullPointerException e) {
                // the player is unreachable
                if (error != null)
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
        notifyPlayers(m, null, m.getCardData());
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
     */
    public void waitFor(Model m) {
        notifyPlayers(m, null, m.getNextToPlay(), m.getPlayers().get(m.getNextToPlay()));
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
    public void pushPlayers(Model m) {
        notifyPlayers(m, null, m.getPlayers(), m.getQuit(), disconnected.get(m));
    }

    /**
     * Ask a player how many players should this match contains.
     *
     * @param name player to ask
     * @return the number of given players
     */
    public int askHowManyPlayers(String name){
        try {
            return 2;
            //return callbacks.get(name).askHowManyPlayers();
        } catch (Exception _) {
            return 2;
        }
    }

    /**
     * Sends to the player how many seconds remains for the clock.
     * @param m model to notify
     */
    public void pushSecondsLeft(Model m) {
        notifyPlayers(m, null, m.getSecondsLeft());
    }

    public void pushDropped(Model m){
        m.getPlayers().forEach((name, _) -> {
            if(disconnected.get(m).contains(name))
                return;

            try {
                callbacks.get(name).pushDropped(m.getRemoved(name));
            } catch (Exception _) {}
        });
    }

    public void pushCannons(Model m){
        m.getPlayers().forEach((name, _) -> {
            if(disconnected.get(m).contains(name))
                return;

            try {
                callbacks.get(name).pushCannons(m.getCannonsToUse(name));
            } catch (Exception _) {}
        });
    }

    /**
     * Check if players are unreachable.
     */
    public void ping(){
        for(Model m: games.values()) {
            notifyPlayers(m, (name) -> {
                if(!disconnected.get(m).contains(name)) {
                    disconnected.get(m).add(name);
                    m.ignoreCheck(name);
                    pushPlayers(m);
                    Logger.playerLog(m.hashCode(), name, "disconnected");
                }

                if (disconnected.get(m).size() >= m.nPlayers - 1)
                    m.pause();
            });
        }
    }

    private void handleAutomaticInput(){
        games.values().forEach(m -> {
            disconnected.get(m).forEach(name -> {
                if (m.getState() == State.Type.ALIEN_INPUT) {
                    if (m.init(name, Optional.empty(), Optional.empty()).isOk())
                        Logger.playerLog(m.hashCode(), name, "player unreachable, setting default aliens");
                } else if (m.getState() == State.Type.WAITING_INPUT &&
                        name.equals(m.getNextToPlay())) {
                    Logger.playerLog(m.hashCode(), name, "player unreachable, setting default card input");
                    setInput(name, CardInput.disconnected());
                }
                else if(m.getState() == State.Type.DRAW_CARD &&
                        m.getFlight().getOrder().getFirst().equals(m.getPlayers().get(name))) {
                    drawCard(name);
                }
            });
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
            controller.loadEvent();
            //init the games
            controller.games.values().forEach(model -> {
                model.loadTimer();
                model.setEvent(controller.stateEvent);
            });
            controller.loadController(rmiPort, socketPort1, socketPort2);
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
            execService.execute(() -> pushFlight(m));

        return result;
    }

    @Override
    public Result<String> setReady(String name) {
        return getModel(name).setReady(name);
    }

    @Override
    public Result<String> quit(String name) {
        Result<String> res = getModel(name).quit(name);
        if(res.isOk()) {
            execService.execute(() -> pushPlayers(getModel(name)));
            execService.execute(() -> pushFlight(getModel(name)));
        }
        return res;
    }

    @Override
    public Result<Tile> setTile(String name, Coordinate c, Tile t, Tile.Rotation rotation) {
        Result<Tile> res = getModel(name).setTile(name, c, t, rotation);

        if(res.isOk()) {
            try {
                callbacks.get(name).placeTile(c, t, rotation);
            } catch (RemoteException _) {}
        }
        return res;
    }

    @Override
    public Result<Tile> bookTile(String name, Tile t) {
        Result<Tile> res = getModel(name).bookTile(name, t);

        if(res.isOk()) {
            try {
                callbacks.get(name).bookedTile(t);
            } catch (RemoteException _) {}
        }
        return res;
    }

    @Override
    public Result<Tile> useBookedTile(String name, Tile t, Tile.Rotation rotation, Coordinate c) {
        Result<Tile> res = getModel(name).useBookedTile(name, t, rotation, c);

        if(res.isOk()) {
            try {
                callbacks.get(name).placeTile(c, t, rotation);
            } catch (RemoteException _) {}
        }

        return res;
    }

    public Result<Tile> placeOpenTile(String name, Tile t, Tile.Rotation rotation, Coordinate c){
        Result<Tile> res = getTileFromSeen(name, t);

        if(res.isErr())
            return res;

        res = setTile(name, c, t, rotation);

        if(res.isErr())
            execService.execute(() -> giveTile(name, t));

        return res;
    }

    @Override
    public Result<String> remove(String name, Coordinate c) {
        Result<String> res = getModel(name).remove(name, c);

        if(res.isOk()) {
            try {
                callbacks.get(name).removed(c);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        return res;
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
    public Result<String> dropReward(String name){
        return getModel(name).dropReward(name);
    }

    @Override
    public int getCash(String name) {
        return getModel(name).getCash(name);
    }

    @Override
    public Result<Integer> drop(String name, Coordinate c) {
        Result<Integer> res = getModel(name).drop(name, c);

        if(res.isOk()) {
            try {
                callbacks.get(name).pushDropped(getModel(name).getRemoved(name));
            } catch (RemoteException _) {}
        }
        return res;
    }

    @Override
    public Result<Integer> drop(String name, Coordinate c, GoodsBoard.Type t) {
        Result<Integer> res = getModel(name).drop(name, c, t);

        if(res.isOk()) {
            try {
                callbacks.get(name).pushDropped(getModel(name).getRemoved(name));
            } catch (RemoteException _) {}
        }
        return res;
    }

    @Override
    public Result<String> increaseCannon(String name, Tile.Rotation r, Integer count) {
        Result<String> res = getModel(name).increaseCannon(name, r, count);

        if(res.isOk()) {
            try {
                callbacks.get(name).pushCannons(getModel(name).getCannonsToUse(name));
            } catch (RemoteException _) {}
        }

        return res;
    }

    @Override
    public Result<Tile> drawTile(String name) {
        return getModel(name).drawTile(name);
    }

    /**
     * Retrieves the list containing all the face-up tiles. Calls {@code getSeenTiles()} in {@code Model} class.
     * @param name of the player calling the method
     * @return The seen list.
     */
    @Override
    public Result<List<Tile>> getSeenTiles(String name) {
        return Result.ok(getModel(name).getSeenTiles());
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

        execService.execute(() -> gaveTile(getModel(name), t));
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

        execService.execute(() -> gotTile(getModel(name), t));
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
        if(res.isOk()) {
            execService.execute(() -> pushCardData(getModel(name)));
            execService.execute(() -> waitFor(getModel(name)));
        }

        return res;
    }

    /**
     * Method used to set the player input for the preparation of the card (so that it can be played afterward).
     * Calls {@code setInput(name, json)} in {@code Model} class. Checks if the player already sent the input.
     * @param name of the player calling the method
     * @param input contains the data and instructions of the player
     * @return ok if succeeded, err if not
     */
    @Override
    public Result<CardInput> setInput(String name, CardInput input) {
        Result<CardInput> res = getModel(name).setInput(name, input);

        if(res.isOk()) {
            if(getModel(name).getChanges() == null){
                execService.execute(() -> pushCardData(getModel(name)));
                execService.execute(() -> waitFor(getModel(name)));
            }
            else
                execService.execute(() -> pushCardChanges(getModel(name)));

        }

        return res;
    }

    /**
     * Method used to get the specific data of a card. Calls {@code getCardData()} in {@code Model} class.
     * @param name of the player calling the method
     * @return data of the card
     */
    @Override
    public Result<CardData> getCardData(String name) {
        return Result.ok(getModel(name).getCardData());
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

    @Override
    public int getEnginePower(String name) {
        return getModel(name).getEnginePower(name);
    }

    @Override
    public double getCannonPower(String name) {
        return getModel(name).getCannonPower(name);
    }
}
