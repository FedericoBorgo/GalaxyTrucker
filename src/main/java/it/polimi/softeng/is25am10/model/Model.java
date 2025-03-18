package it.polimi.softeng.is25am10.model;

import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.FlightBoard.CompressedFlightBoard;
import it.polimi.softeng.is25am10.model.boards.FlightBoard.Pawn;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard.CompressedShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.model.cards.Deck;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is responsible for the state and evolution of the game.
 * Every possible game evolution is contained in this class.
 * The game has an internal Status.
 * Only the methods corresponding to the current state can be called.
 * <br/>
 * JOINING PHASE:
 * This is the initial phase, where the players are waiting for the match to start.
 * In this phase players can join, but can't do anything else.
 * After every player has joined, the timer starts and the ship building phase begins.
 * <br/>
 * BUILDING PHASE:
 * In this phase the only methods that can be called are the ones that change the state of the ship.
 * The player can draw tiles, place them, book tiles and so on.
 * This phase ends when the timer ends or every player declared that they finished.
 * After that every player need to declare where to put (if he can) the aliens.
 * <br/>
 * CARDS PHASE:
 * Now the leader can draw a card. The ship cannot be modified anymore.
 * When a card is drawn, every player need to declare their choices.
 * In this phase, players can also "drop" elements from their ship, such as
 * astronauts, batteries, goods or aliens.
 * When something is dropped, it increments a counter, which will be used
 * inside the card to determinate the required input from the player.
 * After every player input, the card is automatically activated and the
 * reward is given to the player.
 * If a player reaches 0 crew they are automatically removed from the game.
 * This phase repeats until the cards are finished or if there are no player left.
 * It is possible that at the end of a card, it's required to correct the ship.
 */
public class Model implements Serializable {
    /**
     * Counter for the removed items.
     */
    public static class RemovedItems implements Serializable{
        public int battery;
        public int guys;
        public int goods;

        public RemovedItems(){
            reset();
        }
        
        public void reset(){
            this.battery = 0;
            this.guys = 0;
            this.goods = 0;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            RemovedItems that = (RemovedItems) o;
            return battery == that.battery && guys == that.guys && goods == that.goods;
        }
    }

    /**
     * Keeps track of the current state of the game.
     */
    public static class State implements Serializable{
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return prev == state.prev && curr == state.curr;
        }

        public enum Type{
            JOINING,
            BUILDING,
            //checking the board and fixing
            CHECKING,
            //setting where to put the alien
            ALIEN,
            DRAW,
            //waiting player input
            WAITING,
            ENDED,
            PAUSED
        }
        private Type prev;
        private Type curr;
        
        public State(Type curr){
            prev = null;
            this.curr = curr;
        }
        
        public void next(Type next){
            prev = curr;
            curr = next;
        }
        
        public Type get(){
            return curr;
        }
        
        public Type getPrev(){
            return prev;
        }
    }

    //Tiles, flight board, deck: game specific
    private final TilesCollection tiles = new TilesCollection();
    private final FlightBoard flight = new FlightBoard();
    private final Deck deck;

    //Data about the single player.
    private final Map<String, Player> players = new HashMap<>();
    private final Map<Player, RemovedItems> removedItems = new HashMap<>();
    private final Map<Player, Map<Tile.Rotation, Integer>> cannonsToUse = new HashMap<>();
    private final Map<String, Player> quitters = new HashMap<>();

    //Current state of the game
    private final State state = new State(State.Type.JOINING);

    //pawns still not assigned to a player
    private final List<Pawn> unusedPawns = new ArrayList<>(List.of(Pawn.values()));

    //number of players
    private final int nPlayers;
    private int countPlayers = 0;

    /**
     * Used for the timer
     */
    private final AtomicBoolean canMove = new AtomicBoolean(false);
    private transient Timer timer;
    private transient TimerTask task;

    /**
     * Builds a new Model with the number of required players.
     * @param nPlayers number of required players
     */
    public Model(int nPlayers) {
        this.nPlayers = nPlayers;
        deck = new Deck(this, flight);
        loadTimer();
    }

    private void loadTimer(){
        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                canMove.set(true);
                if(flight.getTimer() == 2){
                    moveTimer();
                }
        }
        };
    }

    /**
     * @return get the current state of the game
     */
    public State.Type getStatus() {
        return state.get();
    }

    /**
     * Add a player to the game.
     * Can be called only during the JOINING state.
     * @param name nickname
     * @return the pawn that has been assigned to the game
     */
    public Result<Pawn> addPlayer(String name) {
        if(state.get() != State.Type.JOINING)
            return Result.err("to JOINING state");

        if(players.containsKey(name))
            return Result.err("player already connected");

        if(countPlayers >= nPlayers)
            return Result.err("too many players");

        countPlayers++;
        //associate name -> player
        players.put(name, new Player(name, unusedPawns.removeFirst()));
        //associate player -> removed items
        removedItems.put(get(name), new RemovedItems());
        return Result.ok(get(name).getPawn());
    }

    /**
     * Start the game. Move the state from JOINING to BUILDING.
     * If not all players joined, it fails.
     * @return ok if it started, err if not
     */
    public Result<String> startGame(){
        if(state.get() != State.Type.JOINING)
            return Result.err("not JOINING state");
        if(countPlayers != nPlayers)
            return Result.err("waiting for players");

        state.next(State.Type.BUILDING);
        countPlayers = 0;
        timer.schedule(task, 90000L);
        return Result.ok("");
    }

    /// name -> player
    private Player get(String name){
        return players.get(name);
    }
    /// name -> player -> ship
    private ShipBoard ship(String name){
        return get(name).getBoard();
    }

    /**
     * Move the position of the timer only if it ended.
     * If the timer finish in the last spot all the players
     * are set automatically to ready.
     * Can be called only in BUILDING state.
     * It eventually moves the BUILDING state to CHECKING or ALIEN.
     * @return the position of the timer.
     */
    public synchronized Result<Integer> moveTimer(){
        if(state.get() != State.Type.BUILDING)
            return Result.err("not BUILDING state");
        if(canMove.get())
            flight.moveTimer();

        if(flight.getTimer() == 2){
            players.forEach((name,v)-> setReady(name));
        }
        else
            timer.schedule(task, 90000L);

        return Result.ok(flight.getTimer());
    }

    /// get the flight board
    public CompressedFlightBoard getFlight() {
        return flight.compress();
    }

    /**
     * Set a player ready after he finished building their own ship.
     * Can be called only during BUILDING.
     * Moves the BUILDING state to CHECKING or ALIEN.
     * @param name the name of the player that finished.
     * @return err in case of fail
     */
    public synchronized Result<String> setReady(String name){
        if(state.get() != State.Type.BUILDING)
            return Result.err("not BUILDING state");

        if(flight.getOrder().contains(get(name).getPawn()))
            return Result.err("player already ready");

        flight.setRocketReady(players.get(name).getPawn());
        countPlayers++;

        if(countPlayers == nPlayers){
            countPlayers = 0;

            if(!allShipOk())
                state.next(State.Type.CHECKING);
            else
                state.next(State.Type.ALIEN);
        }

        return Result.ok("");
    }

    /// quit: ignoring the state
    private void quitIgnore(String name){
        quitters.put(name, get(name));
        flight.quit(players.get(name).getPawn());
        players.remove(name);

        if(players.isEmpty())
            state.next(State.Type.ENDED);
    }

    /**
     * A player can decide, if they want, to quit the flight.
     * It can only be done in the DRAW state.
     * @param name the name of the player that wants to quit
     * @return err if it fails
     */
    public synchronized Result<String> quit(String name){
        if(state.get() != State.Type.DRAW)
            return Result.err("can't quit, not in the DRAW phase");
        quitIgnore(name);
        return Result.ok("");
    }

    /// place tile
    public synchronized Result<Tile> setTile(String name, Coordinate c, Tile t, Tile.Rotation rotation){
        if(state.get() != State.Type.BUILDING)
            return Result.err("not BUILDING state");
        return ship(name).getTiles().setTile(c, t, rotation);
    }

    /// get tile
    public Result<Tile> getTile(String name, Coordinate c){
        return ship(name).getTiles().getTile(c);
    }

    /// get rotation
    public Tile.Rotation getRotation(String name, Coordinate c){
        return ship(name).getTiles().getRotation(c);
    }

    /// book tile
    public synchronized Result<Tile> bookTile(String name, Tile t){
        if(state.get() != State.Type.BUILDING)
            return Result.err("not BUILDING state");
        return ship(name).getTiles().bookTile(t);
    }

    /// use a booked tile
    public synchronized Result<Tile> useBookedTile(String name, Tile t, Tile.Rotation rotation, Coordinate c){
        if(state.get() != State.Type.BUILDING)
            return Result.err("not BUILDING state");
        return ship(name).getTiles().useBookedTile(t, rotation, c);
    }

    /// get booked tiles
    public synchronized List<Tile> getBooked(String name) {
        return ship(name).getTiles().getBooked();
    }

    /// remove a tile
    public synchronized Result<String> remove(String name, Coordinate c){
        if(state.get() != State.Type.CHECKING)
            return Result.err("not CHECKING state");

        ship(name).getTiles().remove(c);
        ship(name).removeIllegals();

        //if every board is ok, move the state
        if(allShipOk()){
            if(state.getPrev() == State.Type.BUILDING)
                state.next(State.Type.ALIEN);
            else if(state.getPrev() == State.Type.WAITING)
                state.next(State.Type.DRAW);
        }

        return Result.ok("");
    }

    public synchronized Set<Coordinate> checkShip(String name){
        return ship(name).getTiles().isOK();
    }

    /**
     * Check if every ship is ok.
     * @return true if yes, false if not
     */
    private boolean allShipOk(){
        AtomicBoolean ok = new AtomicBoolean();
        ok.set(true);

        players.forEach((n, p) -> {
            if(!checkShip(n).isEmpty())
                ok.set(false);
        });

        return ok.get();
    }

    /// compress ship
    public CompressedShipBoard getShip(String name){
        return ship(name).compress();
    }

    /**
     * The player declare where to put the aliens.
     * Can be called only in the ALIEN state.
     * @param name name of the player that wants to place aliens
     * @param purple coordinate
     * @param brown coordinate
     * @return result
     */
    public synchronized Result<String> init(String name, Optional<Coordinate> purple, Optional<Coordinate> brown) {
        if (state.get() != State.Type.ALIEN)
            return Result.err("not ALIEN state");

        if (ship(name).getAstronaut().get(new Coordinate(3, 2)) == 2)
            return Result.err("player already declared");
        ship(name).init(purple, brown);
        countPlayers++;

        if (countPlayers == nPlayers)
            state.next(State.Type.DRAW);
        return Result.ok("");
    }

    //TODO reward state
    public synchronized List<GoodsBoard.Type> getReward(String name){
        return get(name).getGoodsReward();
    }

    public synchronized Result<Integer> placeReward(String name, GoodsBoard.Type t, Coordinate c){
        return get(name).placeReward(t, c);
    }

    public int getCash(String name){
        return get(name).getCash();
    }

    /**
     * Use an item in the specified place. It automatically finds
     * if the coordinate holds an astronaut, battery or an alien.
     * The corresponding counter is incremented for every type of element.
     * This can be called only in a WAITING state.
     * A player that already set the card input, can't execute this anymore.
     *
     * @param name name of the player
     * @param c coordinate to remove the single element
     * @return ok if its accepted, err if not
     */
    public synchronized Result<Integer> drop(String name, Coordinate c){
        if(state.get() != State.Type.WAITING)
            return Result.err("not WAITING state");

        if(deck.getRegistered().contains(get(name)))
            return Result.err("player already registered");

        ShipBoard ship = ship(name);
        Player p = get(name);

        if(ship.getBattery().get(c) > 0){
            ship.getBattery().remove(c, 1);
            removedItems.get(p).battery++;
        }
        else if(ship.getAstronaut().get(c) > 0){
            ship.getAstronaut().remove(c, 1);
            removedItems.get(p).guys++;
        }
        else if(ship.getBrown().get(c) > 0){
            ship.getBrown().remove(c, 1);
            removedItems.get(p).guys++;
        }
        else if(ship.getPurple().get(c) > 0){
            ship.getPurple().remove(c, 1);
            removedItems.get(p).guys++;
        }
        else
            return Result.err("no one removed");

        return Result.ok(1);
    }

    /**
     * Same drop but for goods.
     *
     * @param name
     * @param c
     * @param t
     * @return
     */
    public synchronized Result<Integer> drop(String name, Coordinate c, GoodsBoard.Type t){
        if(state.get() != State.Type.WAITING)
            return Result.err("not WAITING state");

        if(deck.getRegistered().contains(get(name)))
            return Result.err("player already registered");

        Result<Integer> res = ship(name).getGoods(t).remove(c, 1);

        if(res.isOk())
            removedItems.get(get(name)).goods++;
        return res;
    }

    /// removed items
    public RemovedItems getRemovedItems(Player player){
        return removedItems.get(player);
    }

    /**
     * Configure how many cannons use to shoot.
     * Can be called only in a waiting state.
     *
     * @param name name of the player
     * @param map witch cannons activate to shoot
     * @return
     */
    public synchronized Result<String> setCannonsToUse(String name, Map<Tile.Rotation, Integer> map){
        if(state.get() != State.Type.WAITING)
            return Result.err("not WAITING state");

        if(deck.getRegistered().contains(get(name)))
            return Result.err("player already registered");

        cannonsToUse.put(get(name), map);
        return Result.ok("");
    }

    /// cannons
    public Map<Tile.Rotation, Integer> getCannonsToUse(Player p){
        return cannonsToUse.getOrDefault(p, null);
    }

    public int batteryRequiredForCannon(String name){
        AtomicInteger total = new AtomicInteger();
        total.set(0);

        cannonsToUse.get(get(name)).forEach((_, val) -> {
            total.addAndGet(val);
        });

        return total.get();
    }

    //tiles section
    public synchronized Result<Tile> drawTile(){
        if(state.get() != State.Type.BUILDING)
            return Result.err("not BUILDING state");
        return Result.ok(tiles.getNew());
    }

    public synchronized Result<List<Tile>> getSeenTiles(){
        if(state.get() != State.Type.BUILDING)
            return Result.err("not BUILDING state");
        return Result.ok(tiles.getSeen());
    }

    public synchronized Result<String> giveTile(Tile t){
        if(state.get() != State.Type.BUILDING)
            return Result.err("not BUILDING state");
        tiles.give(t);
        return Result.ok("");
    }

    public synchronized Result<Tile> getTileFromSeen(Tile t){
        if(state.get() != State.Type.BUILDING)
            return Result.err("not BUILDING state");
        return Result.ok(tiles.getFromSeen(t));
    }
    //end tiles section

    /**
     * Draw a card from the deck.
     * Can be called only in a DRAW state.
     * Only the leader can call this method.
     *
     * @param name name of the leader
     * @return the card
     */
    public synchronized Result<Card> drawCard(String name){
        if(state.get() != State.Type.DRAW)
            return Result.err("not DRAW state");
        if(flight.getOrder().getFirst() != get(name).getPawn())
            return Result.err("only the leader can draw");

        Card c = deck.draw(new ArrayList<>(players.values()));

        if(c == null)
            state.next(State.Type.ENDED);
        else
            state.next(State.Type.WAITING);

        return Result.ok(c);
    }

    /**
     * Give the player's input to the drawn card.
     * Can be called only in the WAITING state.
     *
     * @param name of the player
     * @param json player input
     * @return
     */
    public synchronized Result<JSONObject> setInput(String name, JSONObject json){
        if(state.get() != State.Type.WAITING)
            return Result.err("not WAITING state");
        Result<JSONObject> res = deck.set(get(name), json);

        if(deck.ready()) {
            res = playCard();
            res.getData().put("accepted", true);
            res.getData().put("played", true);
        }

        return res;
    }

    /**
     * Get the temporary data about the drawn card.
     * @return
     */
    public synchronized Result<JSONObject> getCardData(){
        if(state.get() != State.Type.WAITING)
            return Result.err("not WAITING state");
        return Result.ok(deck.getData());
    }

    private Result<JSONObject> playCard(){
        if(state.get() != State.Type.WAITING)
            return Result.err("not WAITING state");

        Result<JSONObject> res = deck.play();

        if(res.isOk()){
            removedItems.forEach((name, item) -> {
                item.reset();
            });
            
            List<Pawn> quitted = new ArrayList<>(flight.getQuitters());
            quitted.removeAll(quitters.values().stream().map(Player::getPawn).toList());

            quitted.forEach(pawn -> {
                players.forEach((name, player) -> {
                    if(pawn == player.getPawn())
                        quitIgnore(name);
                });
            });

            if(allShipOk())
                state.next(State.Type.DRAW);
            else
                state.next(State.Type.CHECKING);
        }

        return res;
    }

    public synchronized Result<Card[][]> getVisible(){
        if(state.get() != State.Type.BUILDING)
            return Result.err("not BUILDING state");
        return Result.ok(deck.getVisible());
    }
    //end cards section

    public void pause(){
        state.next(State.Type.PAUSED);
    }

    public void resume(){
        state.next(state.getPrev());
    }

    /**
     * Store the current state of the game to a file.
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public Result<String> store(String filename) throws IOException {
        if(state.get() != State.Type.DRAW)
            return Result.err("not DRAW state");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
        oos.writeObject(this);
        oos.close();
        return Result.ok(filename);
    }

    /**
     * Load the state of a game from a file.
     *
     * @param filename
     * @return
     * @throws IOException
     */
    static public Model load(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
        Model model = (Model) ois.readObject();
        model.loadTimer();
        ois.close();
        return model;
    }

    /// AUTOGENERATED
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        // causes overflow TODO fix
        // causes overflow
        return nPlayers == model.nPlayers && countPlayers == model.countPlayers &&
                Objects.equals(tiles, model.tiles) &&
                Objects.equals(flight, model.flight) &&
                Objects.equals(players, model.players) &&
                Objects.equals(quitters, model.quitters) &&
                Objects.equals(state, model.state) &&
                Objects.equals(unusedPawns, model.unusedPawns) &&
                Objects.equals(canMove.get(), model.canMove.get()) &&
                Objects.equals(deck, model.deck);
    }

    /// AUTOGENERATED
    @Override
    public int hashCode() {
        return Objects.hash(tiles, flight, deck, players, removedItems, cannonsToUse, quitters, state, unusedPawns, nPlayers, countPlayers, canMove, timer, task);
    }
}