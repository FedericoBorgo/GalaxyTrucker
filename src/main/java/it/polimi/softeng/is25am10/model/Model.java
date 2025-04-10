package it.polimi.softeng.is25am10.model;

import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.FlightBoard.Pawn;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

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
    public static class Removed implements Serializable{
        public int battery;
        public int guys;
        public int goods;

        public Removed(){
            reset();
        }
        
        public void reset(){
            this.battery = 0;
            this.guys = 0;
            this.goods = 0;
        }

        public boolean isDebt(){
            return battery < 0 || guys < 0 || goods < 0;
        }

        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Removed that = (Removed) o;
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
            ALIEN_INPUT,
            DRAW_CARD,
            //waiting player input
            WAITING_INPUT,
            ENDED,
            PAUSED,
            PAY_DEBT
        }
        private Type prev;
        private Type curr;
        private final Model m;
        private transient BiConsumer<Model, Type> notify;

        public State(Type curr, BiConsumer<Model, Type> notify, Model m){
            prev = null;
            this.curr = curr;
            this.m = m;
            this.notify = notify;
        }

        public void setNotify(BiConsumer<Model, Type> notify){
            this.notify = notify;
        }

        public void next(Type next){
            prev = curr;
            curr = next;
            notify.accept(m, next);
        }
        
        private Type get(){
            return curr;
        }
        
        private Type getPrev(){
            return prev;
        }
    }

    //Tiles, flight board, deck: game specific
    private final TilesCollection tiles = new TilesCollection();
    private final FlightBoard flight = new FlightBoard();
    private final Deck deck;

    //Data about the single player.
    private final Map<String, Player> players = new HashMap<>();
    private final Map<Player, Removed> removed = new HashMap<>();
    private final Map<Player, Map<Tile.Rotation, Integer>> cannonsToUse = new HashMap<>();
    private final Map<String, Player> quitters = new HashMap<>();

    //Current state of the game
    private final State state;

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
    public static final long TIMER_DELAY = 1000L;

    /**
     * Builds a new Model with the number of required players.
     * @param nPlayers number of required players
     */
    public Model(int nPlayers, BiConsumer<Model, State.Type> notify) {
        this.nPlayers = nPlayers;
        deck = new Deck(this, flight);
        this.state = new State(State.Type.JOINING, notify, this);
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
    public State.Type getState() {
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

        countPlayers++;
        //associate name -> player
        players.put(name, new Player(name, unusedPawns.removeFirst()));
        //associate player -> removed items
        removed.put(get(name), new Removed());

        if(countPlayers == nPlayers){
            // start the game
            state.next(State.Type.BUILDING);
            countPlayers = 0;
            timer.schedule(task, TIMER_DELAY);
        }

        return Result.ok(get(name).getPawn());
    }

    /// name -> player
    public Player get(String name){
        return players.get(name);
    }
    /// name -> player -> ship
    public ShipBoard ship(String name){
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
        if(canMove.get()) {
            if(flight.getTimer() == 2)
                players.forEach((p,_)-> setReady(p));
            else {
                flight.moveTimer();
                loadTimer();
                timer.schedule(task, TIMER_DELAY);
            }
            canMove.set(false);
        }
        else
            return Result.err("clessidra non ancora esaurita");

        return Result.ok(flight.getTimer());
    }

    /// get the flight board
    public FlightBoard getFlight() {
        return flight;
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
                state.next(State.Type.ALIEN_INPUT);
        }

        return Result.ok("");
    }

    /// quit: ignoring the state
    private void quitIgnore(String name){
        if(quitters.containsKey(name))
            return;

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
        if(state.get() != State.Type.DRAW_CARD)
            return Result.err("can't quit, not in the DRAW phase");
        quitIgnore(name);
        return Result.ok("");
    }

    /// place tile
    public synchronized Result<Tile> setTile(String name, Coordinate c, Tile t, Tile.Rotation rotation){
        if(state.get() != State.Type.BUILDING)
            return Result.err("not BUILDING state");
        if(flight.getOrder().contains(get(name).getPawn()))
            return Result.err("player già pronto");
        return ship(name).getTiles().setTile(c, t, rotation);
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

    /// remove a tile
    public synchronized Result<String> remove(String name, Coordinate c){
        if(state.get() != State.Type.CHECKING)
            return Result.err("not CHECKING state");

        ship(name).getTiles().remove(c);
        ship(name).removeIllegals();

        //if every board is ok, move the state
        if(allShipOk()){
            if(state.getPrev() == State.Type.BUILDING)
                state.next(State.Type.ALIEN_INPUT);
            else if(state.getPrev() == State.Type.WAITING_INPUT)
                state.next(State.Type.DRAW_CARD);
        }

        return Result.ok("");
    }

    /**
     * Check if every ship is ok.
     * @return true if yes, false if not
     */
    private boolean allShipOk(){
        AtomicBoolean ok = new AtomicBoolean();
        ok.set(true);

        players.forEach((_, p) -> {
            if(!p.getBoard().getTiles().isOK().isEmpty())
                ok.set(false);
        });

        return ok.get();
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
        if (state.get() != State.Type.ALIEN_INPUT)
            return Result.err("not ALIEN state");

        if (ship(name).getAstronaut().get(new Coordinate(3, 2)) == 2)
            return Result.err("player already declared");
        ship(name).init(purple, brown);
        countPlayers++;

        if (countPlayers == nPlayers)
            state.next(State.Type.DRAW_CARD);
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
     * Removes one element on the coordinate {@code c}. It automatically finds if the coordinate
     * holds an astronaut, battery or an alien. The corresponding counter is incremented for every type of element.
     * This can be called only in a WAITING state.
     * A player that already set the card input, can't execute this anymore.
     *
     * @param name name of the player
     * @param c coordinate to remove the single element
     * @return ok if its accepted, err if not
     */
    public synchronized Result<Integer> drop(String name, Coordinate c){
        if(state.get() != State.Type.WAITING_INPUT && state.get() != State.Type.PAY_DEBT)
            return Result.err("not WAITING state");

        if(deck.getRegistered().contains(get(name)))
            return Result.err("player already registered");

        ShipBoard ship = ship(name);
        Player p = get(name);

        if(ship.getBattery().get(c) > 0){
            ship.getBattery().remove(c, 1);
            removed.get(p).battery++;
        }
        else if(ship.getAstronaut().get(c) > 0){
            ship.getAstronaut().remove(c, 1);
            removed.get(p).guys++;
        }
        else if(ship.getBrown().get(c) > 0){
            ship.getBrown().remove(c, 1);
            removed.get(p).guys++;
        }
        else if(ship.getPurple().get(c) > 0){
            ship.getPurple().remove(c, 1);
            removed.get(p).guys++;
        }
        else
            return Result.err("no one removed");

        updateDebt();

        return Result.ok(1);
    }

    /**
     * Same drop but for goods.
     *
     * @param name name of the player
     * @param c coordinate to remove the single element
     * @param t type of goods to remove
     * @return ok if its accepted, err if not
     */
    public synchronized Result<Integer> drop(String name, Coordinate c, GoodsBoard.Type t){
        if(state.get() != State.Type.WAITING_INPUT && state.get() != State.Type.PAY_DEBT)
            return Result.err("not WAITING state");

        if(deck.getRegistered().contains(get(name)))
            return Result.err("player already registered");

        Result<Integer> res = ship(name).getGoods(t).remove(c, 1);

        updateDebt();

        if(res.isOk())
            removed.get(get(name)).goods++;
        return res;
    }

    private void updateDebt() {
        if(state.get() == State.Type.PAY_DEBT && !someoneDebt()){
            state.next(State.Type.WAITING_INPUT);

            removed.forEach((_, item) -> item.reset());

            if(allShipOk())
                state.next(State.Type.DRAW_CARD);
            else
                state.next(State.Type.CHECKING);
        }
    }

    /// removed items
    public Removed getRemoved(Player player){
        return removed.get(player);
    }

    /**
     * Configure how many cannons use to shoot.
     * Can be called only in a waiting state.
     *
     * @param name name of the player
     * @param map witch cannons activate to shoot
     * @return ok if succeeded, err if not
     */
    public synchronized Result<String> setCannonsToUse(String name, Map<Tile.Rotation, Integer> map){
        if(state.get() != State.Type.WAITING_INPUT)
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

    /**
     * Needed for checking that the player used enough batteries to activate the cannons
     * @param name of the player
     * @return number of batteries required for activating all the cannons
     */
    public int batteryForCannon(String name){
        AtomicInteger total = new AtomicInteger();
        total.set(0);

        cannonsToUse.get(get(name)).forEach((_, val) -> total.addAndGet(val));

        return total.get();
    }

    //tiles section
    public synchronized Result<Tile> drawTile(){
        if(state.get() != State.Type.BUILDING)
            return Result.err("not BUILDING state");
        return Result.ok(tiles.getNew());
    }

    /**
     * Retrieves the list containing all the face-up tiles.
     * @return The seen list.
     */
    public synchronized Result<List<Tile>> getSeenTiles(){
        if(state.get() != State.Type.BUILDING)
            return Result.err("not BUILDING state");
        return Result.ok(tiles.getSeen());
    }

    /**
     * Adds a tile to the face-up tiles.
     * @param t the tile to be added to the seen list.
     * @return ok if succeeded, err if not
     */
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
        if(state.get() != State.Type.DRAW_CARD)
            return Result.err("not DRAW state");
        if(flight.getOrder().getFirst() != get(name).getPawn())
            return Result.err("only the leader can draw");

        Card c = deck.draw(new ArrayList<>(players.values()));

        if(c == null) {
            state.next(State.Type.ENDED);
            return Result.ok(null);
        }

        state.next(State.Type.WAITING_INPUT);
        changes = null;

        if(!c.needInput){
            players.forEach((p, _) -> setInput(p, null));
        }

        return Result.ok(c);
    }

    private CardOutput changes = null;

    /**
     * Give the player's input to the drawn card.
     * Can be called only in the WAITING state.
     *
     * @param name of the player
     * @param input player input
     * @return ok if succeeded, err if not
     */
    public synchronized Result<CardInput> setInput(String name, CardInput input){
        if(state.get() != State.Type.WAITING_INPUT)
            return Result.err("not WAITING state");
        Result<CardInput> res = deck.set(get(name), input);

        if(deck.ready())
            changes = playCard().getData();

        return res;
    }

    public synchronized CardOutput getChanges(){
        return changes;
    }

    public String getNextToPlay(){
        List<Pawn> order = new ArrayList<>(flight.getOrder());
        order.removeAll(deck.getRegistered()
                .stream()
                .map(Player::getPawn)
                .toList());
        Pawn next = order.getFirst();
        AtomicReference<String> nextToPlay = new AtomicReference<>("");
        players.forEach((name, player) -> {
            if(player.getPawn() == next)
                nextToPlay.set(name);
        });
        return nextToPlay.get();
    }

    /**
     * Get the temporary data about the drawn card.
     * @return the card data
     */
    public synchronized Result<CardData> getCardData(){
        if(state.get() != State.Type.WAITING_INPUT)
            return Result.err("not WAITING state");
        return Result.ok(deck.getData());
    }

    private boolean someoneDebt(){
        AtomicBoolean debt = new AtomicBoolean(false);

        removed.values().forEach(rm -> {
            if(rm.isDebt())
                debt.set(true);
        });

        return debt.get();
    }

    private Result<CardOutput> playCard(){
        if(state.get() != State.Type.WAITING_INPUT)
            return Result.err("not WAITING state");

        Result<CardOutput> res = deck.play();

        if(res.isOk()){
            List<Pawn> rm = new ArrayList<>(flight.getQuitters());

            players.values()
                    .stream()
                    .filter(p -> rm.contains(p.getPawn()))
                    .forEach(p -> quitIgnore(p.getName()));


            if(someoneDebt())
                state.next(State.Type.PAY_DEBT);
            else{
                removed.forEach((_, item) -> item.reset());

                if(allShipOk())
                    state.next(State.Type.DRAW_CARD);
                else
                    state.next(State.Type.CHECKING);
            }
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

    public void debug_setCards(List<Card> cards){
        deck.debug_setCards(cards);
    }

    public FlightBoard debug_getFlightBoard(){
        return flight;
    }

    /**
     * Store the current state of the game to a file.
     *
     * @param filename name of the file
     * @return ok if its stored, err if not
     */
    public Result<String> store(String filename) {
        if(state.get() != State.Type.DRAW_CARD)
            return Result.err("not DRAW state");
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(this);
            oos.close();
        }catch(IOException e){
            return Result.err(e.getMessage());
        }

        return Result.ok(filename);
    }

    /**
     * Load the state of a game from a file.
     *
     * @param filename name of the file
     * @param notifier function to call when the state has changed
     * @return loaded model
     * @throws IOException if it can load the model from the file
     */
    static public Model load(String filename, BiConsumer<Model, State.Type> notifier) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
        Model model = (Model) ois.readObject();
        model.loadTimer();
        model.state.setNotify(notifier);
        ois.close();
        return model;
    }

    public HashMap<String, Pawn> getPlayers(){
        HashMap<String, Pawn> p = new HashMap<>();
        players.forEach((name, player) -> p.put(name, player.getPawn()));

        return p;
    }
}