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

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Stores all the information of a single game including the players and all their data,
 * the tiles for that game and the flightboard.
 */
public class Model {
    public static class RemovedItems{
        public int battery;
        public int guys;
        public int goods;

        public RemovedItems(){
            this.battery = 0;
            this.guys = 0;
            this.goods = 0;
        }
    }

    public enum Status{
        JOINING,
        BUILDING,
        CHECKING,
        ALIEN,
        DRAW,
        WAITING,
        ENDED;
    }

    private Status status;
    private Status prevStatus;
    private final Map<String, Player> players;
    private final TilesCollection tiles;
    private final FlightBoard flight;
    private final List<Pawn> unusedPawns;
    private final Map<Player, RemovedItems> removedItems;
    private final Map<Player, Map<Tile.Rotation, Integer>> drillsToUse;
    private final Deck deck;
    private final Map<String, Player> quitters;
    private final int nPlayers;
    private int countPlayers;

    //Constructs a new Match instance
    public Model(int nPlayers) {
        this.nPlayers = nPlayers;
        quitters = new HashMap<>();
        players = new HashMap<>();
        tiles = new TilesCollection();
        flight = new FlightBoard();
        unusedPawns = new ArrayList<>(List.of(Pawn.values()));
        removedItems = new HashMap<>();
        drillsToUse = new HashMap<>();
        deck = new Deck(this, flight);
        status = Status.JOINING;
        prevStatus = null;
        countPlayers = 0;
    }

    public Result<Pawn> addPlayer(String name) {
        if(status != Status.JOINING)
            return Result.err("to JOINING status");

        if(players.containsKey(name))
            return Result.err("player already connected");

        if(countPlayers >= nPlayers)
            return Result.err("too many players");

        countPlayers++;
        Pawn pawn = unusedPawns.removeFirst();
        Player p = new Player(name, pawn);
        players.put(name, p);
        removedItems.put(p, new RemovedItems());
        return Result.ok(pawn);
    }

    public Result<String> startGame(){
        if(status != Status.JOINING)
            return Result.err("not JOINING status");
        if(countPlayers != nPlayers)
            return Result.err("waiting for players");

        prevStatus = Status.JOINING;
        status = Status.BUILDING;
        countPlayers = 0;
        return Result.ok("started");
    }

    private Player get(String name){
        return players.get(name);
    }

    private ShipBoard ship(String name){
        return get(name).getBoard();
    }

    public synchronized Result<Integer> moveTimer(){
        if(status != Status.BUILDING)
            return Result.err("not BUILDING status");
        flight.moveTimer();
        return Result.ok(flight.getTimer());
    }

    public CompressedFlightBoard getFlight() {
        return flight.compress();
    }

    public synchronized Result<String> setReady(String name){
        if(status != Status.BUILDING)
            return Result.err("not BUILDING status");

        flight.setRocketReady(players.get(name).getPawn());
        countPlayers++;

        if(countPlayers == nPlayers){
            prevStatus = Status.BUILDING;
            countPlayers = 0;

            if(!allShipOk())
                status = Status.CHECKING;
            else
                status = Status.ALIEN;
        }

        return Result.ok("");
    }

    public synchronized void quit(String name){
        quitters.put(name, get(name));
        flight.quit(players.get(name).getPawn());
        players.remove(name);

        if(players.isEmpty())
            status = Status.ENDED;
    }


    //player building board section
    public synchronized Result<Tile> setTile(String name, Coordinate c, Tile t, Tile.Rotation rotation){
        if(status != Status.BUILDING)
            return Result.err("not BUILDING status");
        return ship(name).getTiles().setTile(c, t, rotation);
    }

    public synchronized Result<Tile> bookTile(String name, Tile t){
        if(status != Status.BUILDING)
            return Result.err("not BUILDING status");
        return ship(name).getTiles().bookTile(t);
    }

    public synchronized Result<Tile> useBookedTile(String name, Tile t, Tile.Rotation rotation, Coordinate c){
        if(status != Status.BUILDING)
            return Result.err("not BUILDING status");
        return ship(name).getTiles().useBookedTile(t, rotation, c);
    }

    public synchronized List<Tile> getBooked(String name) {
        return ship(name).getTiles().getBooked();
    }

    public synchronized Result<String> remove(String name, Coordinate c){
        if(status != Status.CHECKING)
            return Result.err("not CHECKING status");

        ship(name).getTiles().remove(c);
        ship(name).removeIllegals();

        if(allShipOk()){
            if(prevStatus == Status.BUILDING){
                status = Status.ALIEN;
            }
            else if(prevStatus == Status.WAITING){
                status = Status.DRAW;
            }

            prevStatus = Status.CHECKING;
        }

        return Result.ok("");
    }

    public synchronized Result<Set<Coordinate>> checkShip(String name){
        if(status != Status.CHECKING)
            return Result.err("not CHECKING status");

        return Result.ok(ship(name).getTiles().isOK());
    }

    private boolean allShipOk(){
        AtomicBoolean ok = new AtomicBoolean();
        ok.set(true);

        players.forEach((n, p) -> {
            if(!checkShip(n).getData().isEmpty())
                ok.set(false);
        });

        return ok.get();
    }

    public CompressedShipBoard getShip(String name){
        return ship(name).compress();
    }

    public synchronized Result<String> init(String name, Optional<Coordinate> purple, Optional<Coordinate> brown){
        if(status != Status.ALIEN)
            return Result.err("not ALIEN status");

        if(ship(name).getAstronaut().get(new Coordinate(3, 2)) == 2)
            return Result.err("player already declared");
        ship(name).init(purple, brown);
        countPlayers++;

        if(countPlayers == nPlayers){
            prevStatus = Status.ALIEN;
            status = Status.DRAW;
        }
        return Result.ok("");
    }
    //end player building methods








    //player methods
    public synchronized List<GoodsBoard.Type> getReward(String name){
        return get(name).getGoodsReward();
    }

    public synchronized Result<Integer> placeReward(String name, GoodsBoard.Type t, Coordinate c){
        return get(name).placeReward(t, c);
    }

    public int getCash(String name){
        return get(name).getCash();
    }

    public synchronized Result<Integer> drop(String name, Coordinate c){
        if(status != Status.WAITING)
            return Result.err("not WAITING status");

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

    public synchronized Result<Integer> drop(String name, Coordinate c, GoodsBoard.Type t){
        if(status != Status.WAITING)
            return Result.err("not WAITING status");

        if(deck.getRegistered().contains(get(name)))
            return Result.err("player already registered");

        Result<Integer> res = ship(name).getGoods(t).remove(c, 1);

        if(res.isOk())
            removedItems.get(get(name)).goods++;
        return res;
    }

    public RemovedItems getRemovedItems(Player player){
        return removedItems.get(player);
    }

    public RemovedItems getRemovedItems(String name){
        return getRemovedItems(get(name));
    }

    public synchronized Result<String> setDrillsToUse(String name, Map<Tile.Rotation, Integer> map){
        if(status != Status.WAITING)
            return Result.err("not WAITING status");

        if(deck.getRegistered().contains(get(name)))
            return Result.err("player already registered");
        drillsToUse.put(get(name), map);
        return Result.ok("");
    }

    public Map<Tile.Rotation, Integer> getDrillsToUse(String name){
        return getDrillsToUse(get(name));
    }

    public Map<Tile.Rotation, Integer> getDrillsToUse(Player p){
        return drillsToUse.getOrDefault(p, null);
    }
    //end










    //tiles section
    public synchronized Result<Tile> drawTile(){
        if(status != Status.BUILDING)
            return Result.err("not BUILDING status");
        return Result.ok(tiles.getNew());
    }

    public synchronized Result<List<Tile>> getSeenTiles(){
        if(status != Status.BUILDING)
            return Result.err("not BUILDING status");
        return Result.ok(tiles.getSeen());
    }

    public synchronized Result<String> giveTile(Tile t){
        if(status != Status.BUILDING)
            return Result.err("not BUILDING status");
        tiles.give(t);
        return Result.ok("");
    }

    public synchronized Result<Tile> getTileFromSeen(Tile t){
        if(status != Status.BUILDING)
            return Result.err("not BUILDING status");
        return Result.ok(tiles.getFromSeen(t));
    }
    //end tiles section


    //cards
    public synchronized Result<Card> drawCard(String name){
        if(status != Status.DRAW)
            return Result.err("not DRAW status");
        if(flight.getOrder().getFirst() != get(name).getPawn())
            return Result.err("only the leader can draw");

        Card c = deck.draw(new ArrayList<>(players.values()));
        prevStatus = Status.DRAW;

        if(c == null)
            status = Status.ENDED;
        else
            status = Status.WAITING;

        return Result.ok(c);
    }

    public synchronized Result<String> setInput(String name, JSONObject json){
        if(status != Status.WAITING)
            return Result.err("not WAITING status");
        Result<String> res = deck.set(get(name), json);

        if(deck.ready())
            res = deck.play();
        return res;
    }

    public boolean cardReady(){
        return deck.ready();
    }

    public synchronized Result<JSONObject> getCardData(){
        if(status != Status.WAITING)
            return Result.err("not WAITING status");
        return Result.ok(deck.getData());
    }

    public synchronized Result<String> playCard(){
        if(status != Status.WAITING)
            return Result.err("not WAITING status");

        Result<String> res = deck.play();

        if(res.isOk()){
            List<Pawn> quitted = new ArrayList<>(flight.getQuitters());
            quitted.removeAll(quitters.values().stream().map(Player::getPawn).toList());

            quitted.forEach(pawn -> {
                players.forEach((name, player) -> {
                    if(pawn == player.getPawn())
                        quit(name);
                });
            });

            prevStatus = Status.WAITING;
            if(allShipOk())
                status = Status.DRAW;
            else
                status = Status.CHECKING;
        }

        return res;
    }

    public synchronized Result<Card[][]> getVisible(){
        if(status != Status.BUILDING)
            return Result.err("not BUILDING status");
        return Result.ok(deck.getVisible());
    }
    //end cards section
}