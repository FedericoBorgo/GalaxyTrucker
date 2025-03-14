package it.polimi.softeng.is25am10.model;

import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard.RocketPawn;
import it.polimi.softeng.is25am10.model.boards.FlightBoard.CompressedFlightBoard;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard.CompressedShipBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;

import java.util.*;

/**
 * Stores all the information of a single game including the players and all their data,
 * the tiles for that game and the flightboard.
 */

public class Model {
    private final Map<String, Player> players;
    private TilesCollection tiles;
    private FlightBoard flight;
    private List<RocketPawn> unusedPawns;
    private final boolean disableChecks;

    //Constructs a new Match instance
    public Model(boolean disableChecks, boolean disableChecks1) {
        this.disableChecks = disableChecks1;
        players = new HashMap<>();
        tiles = new TilesCollection();
        flight = new FlightBoard();
        unusedPawns = new ArrayList<>(List.of(RocketPawn.values()));
    }

    public Result<RocketPawn> addPlayer(String name) {
        if(players.containsKey(name))
            return Result.err("player already connected");

        RocketPawn pawn = unusedPawns.removeFirst();
        Player p = new Player(pawn);
        players.put(name, p);
        return Result.ok(pawn);
    }

    private Player get(String name){
        return players.get(name);
    }

    private ShipBoard ship(String name){
        return get(name).getBoard();
    }

    public int moveTimer(){
        flight.moveTimer();
        return flight.getTimer();
    }

    public CompressedFlightBoard getFlight() {
        return flight.compress();
    }

    public void setReady(String name){
        //TODO where to put the aliens
        flight.setRocketReady(players.get(name).getPawn());
    }

    //player building board section
    public Result<Tile> setTile(String name, Coordinate c, Tile t, Tile.Rotation rotation){
        //TODO disableCheck
        return ship(name).getTiles().setTile(c, t, rotation);
    }

    public Result<Tile> bookTile(String name, Tile t){
        return ship(name).getTiles().bookTile(t);
    }

    public Result<Tile> useBookedTile(String name, Tile t, Tile.Rotation rotation, Coordinate c){
        return ship(name).getTiles().useBookedTile(t, rotation, c);
    }

    public List<Tile> getBooked(String name) {
        return ship(name).getTiles().getBooked();
    }

    public void remove(String name, Coordinate c){
        ship(name).getTiles().remove(c);
        ship(name).removeIllegals();
    }

    public Set<Coordinate> checkShip(String name){
        return ship(name).getTiles().isOK();
    }

    public CompressedShipBoard getShip(String name){
        return ship(name).compress();
    }
    //end player building methods

    //player methods
    public List<GoodsBoard.Type> getReward(String name){
        return get(name).getGoodsReward();
    }

    public Result<Integer> placeReward(String name, GoodsBoard.Type t, Coordinate c){
        return disableChecks? ship(name).getGoods(t).put(c, 1) :
                                get(name).placeReward(t, c);
    }

    public int getCash(String name){
        return get(name).getCash();
    }

    //tiles section
    public Tile drawTile(){
        return tiles.getNew();
    }

    public List<Tile> getSeenTiles(){
        return tiles.getSeen();
    }

    public void giveTile(Tile t){
        tiles.give(t);
    }

    public Tile getTileFromSeen(Tile t){
        return tiles.getFromSeen(t);
    }
    //end tiles section
}