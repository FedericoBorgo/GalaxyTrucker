package it.polimi.softeng.is25am10.model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * ShipBoard Represents a ship board where tiles can be placed, booked, (or trashed).
 * It offers methods to place a tile, to get it, to read its orientation,
 * to manage the booking process of tiles (and to manage trashed tiles). To be added.
 */
public class ShipBoard {
    // coordinates of unplaceable tiles
    private static final Pair<Integer, Integer>[] WALL_POSITION = new Pair[]{
            new Pair<>(0, 0),
            new Pair<>(1, 0),
            new Pair<>(3, 0),
            new Pair<>(5, 0),
            new Pair<>(6, 0),
            new Pair<>(0, 1),
            new Pair<>(6, 1),
            new Pair<>(3, 4),
    };

    public static final int BOARD_WIDTH = 7;
    public static final int BOARD_HEIGHT = 5;

    private Tile[][] board;
    private char[][] orientation;
    private final List<Tile> booked;
    private final List<Tile> trashed;

    /**
     * Initializes {@code ShipBoard} with {@code EMPTY_TILE} and {@code WALL_TILE}
     * Places the Central housing unit on the board
     *
     */
    public ShipBoard(){
        board = new Tile[BOARD_WIDTH][BOARD_HEIGHT];
        orientation = new char[BOARD_WIDTH][BOARD_HEIGHT];
        trashed = new ArrayList<>();
        booked = new ArrayList<>();

        // fill with empty tiles (on which can be placed the game tiles)
        for(int i = 0; i < BOARD_WIDTH; i++)
            for(int j = 0; j < BOARD_HEIGHT; j++)
                set(i, j, Tile.EMPTY_TILE, 'n');

        // fill the implacable spaces with WALL
        for(Pair<Integer, Integer> coord : WALL_POSITION)
            set(coord.getKey(), coord.getValue(), Tile.WALL_TILE, ' ');

        // the start of building a ship
        set(3, 2, new Tile(TilesType.C_HOUSE, "uuuu"), 'n');
    }

    //Places a tile at the specified coordinates on the board with a given orientation.
    private void set(int x, int y, Tile tile, char ori){
        orientation[x][y] = ori;
        board[x][y] = tile;
    }

    // Retrieves a tile from the board at the specified coordinates.
    // If the specified coordinates are out of bounds, returns null.
    private Tile get(int x, int y){
        if(x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT)
            return null;

        return board[x][y];
    }


    // Checks if there is at least one tile adjacent to the specified position
    // that is not a wall or an empty space.
    private boolean checkNear(int x, int y){
        List<Tile> around = new ArrayList<>();

        // get the 4 adjacent tiles
        around.add(get(x+1, y));
        around.add(get(x-1, y));
        around.add(get(x, y+1));
        around.add(get(x, y-1));

        // check if at least one is not a wall or an empty space.
        for(Tile tile: around)
            if(tile != null && tile != Tile.WALL_TILE && tile != Tile.EMPTY_TILE)
                return true;

        return false;
    }

    /**
     * Attempts to place a tile at the specified coordinates on the board with a given orientation.
     * Checks if the target position is valid (not out of bound), if the position is free and
     * if there are tiles nearby (tiles can't be placed in the void, they need an adjacent tile).
     *
     * @param x the x-coordinate where the tile is to be placed
     * @param y the y-coordinate where the tile is to be placed
     * @param t the tile to be placed
     * @param ori the orientation of the tile
     * @return a Result object containing the placed tile if the placement is successful,
     *         or an error Result with a reason explaining why the placement failed
     */
    public Result<Tile> setTile(int x, int y, Tile t, char ori){
        Tile result = get(x, y);

        if(result == null || result == Tile.WALL_TILE)
            return Result.err("cant place out of bound");

        if(result != Tile.EMPTY_TILE)
            return Result.err("occupied tile");

        // is there a tile nearby?
        if(!checkNear(x, y))
            return Result.err("cant be placed in the void");

        set(x, y, t, ori);
        return Result.ok(t);
    }

    /**
     * Retrieves the tile located at the specified coordinates on the board.
     * If the coordinates are out of bounds or the tile is a wall, an error result is returned.
     *
     * @param x the x-coordinate of the tile to retrieve
     * @param y the y-coordinate of the tile to retrieve
     * @return a Result containing the tile if the coordinates are valid and the tile is not a wall,
     *         or an error result with a relevant message if the retrieval fails
     */
    public Result<Tile> getTile(int x, int y) {
        Tile t = get(x, y);

        if(t == null || t == Tile.WALL_TILE)
            return Result.err("out of bound");

        return Result.ok(t);
    }

    /**
     * Retrieves the orientation of the tile located at the specified coordinates on the board.
     * If the coordinates are out of bounds, an error result is returned.
     *
     * @param x the x-coordinate of the tile
     * @param y the y-coordinate of the tile
     * @return a Result containing the orientation character of the tile if the coordinates are valid,
     *         or an error Result with a message indicating the coordinates are out of bounds
     */
    public Result<Character> getOri(int x, int y) {
        return (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT)?
                Result.err("out of bound") :
                Result.ok(orientation[x][y]);
    }

    /**
     * Attempts to book a tile for later use. A maximum of 2 tiles can stay booked
     * at the same time.
     * Checks if there is space left (less than 2 booked tiles) and if {@code t} is not already booked.
     *
     * @param t the tile to be booked
     * @return a {@code Result} containing the booked tile if successful, or an error
     *         {@code Result} with a message explaining why the booking failed
     */
    public Result<Tile> bookTile(Tile t) {
        if(booked.size() >= 2)
            return Result.err("booked tiles full");

        if(booked.contains(t))
            return Result.err("already booked");

        booked.add(t);
        return Result.ok(t);
    }

    /**
     * Attempts to use a booked tile {@code t} by placing it on the board at the specified
     * coordinates with the given orientation. If the tile is successfully placed,
     * it is removed from the booked tiles list.
     * Checks if {@code t} is in the booked list using the contains method in the Collections interface.
     *
     * @param t   the tile to be placed
     * @param ori the orientation of the tile
     * @param x   the x-coordinate where the tile is to be placed
     * @param y   the y-coordinate where the tile is to be placed
     * @return a {@code Result} containing the placed tile if successful, or an
     *         error {@code Result} with a message explaining why the operation
     *         failed
     */
    public Result<Tile> useBookedTile(Tile t, char ori, int x, int y){
        if(!booked.contains(t))
            return Result.err("not booked");

        Result<Tile> result = setTile(x, y, t, ori);

        if(result.isOk())
            booked.remove(t);

        return result;
    }

    /**
     * Retrieves the list of tiles currently booked on the board.
     *
     * @return a list of tiles that are booked
     */
    public List<Tile> getBooked() {
        return booked;
    }
}
