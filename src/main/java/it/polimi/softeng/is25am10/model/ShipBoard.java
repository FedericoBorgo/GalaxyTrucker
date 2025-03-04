package it.polimi.softeng.is25am10.model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ShipBoard {
    // coordinates of implacable tiles
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

    public ShipBoard(){
        board = new Tile[BOARD_WIDTH][BOARD_HEIGHT];
        orientation = new char[BOARD_WIDTH][BOARD_HEIGHT];
        trashed = new ArrayList<>();
        booked = new ArrayList<>();

        for(int i = 0; i < BOARD_WIDTH; i++)
            for(int j = 0; j < BOARD_HEIGHT; j++)
                set(i, j, Tile.EMPY_TILE, 'n');

        // fill the implacable spaces with WALL, the other are empty
        for(Pair<Integer, Integer> coord : WALL_POSITION)
            set(coord.getKey(), coord.getValue(), Tile.WALL_TILE, ' ');

        // the start of building a ship
        set(3, 2, new Tile(TilesType.C_HOUSE, "uuuu"), 'n');
    }

    private boolean set(int x, int y, Tile tile, char ori){
        if(x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT)
            return false;

        orientation[x][y] = ori;
        board[x][y] = tile;
        return true;
    }

    private Tile get(int x, int y){
        if(x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT)
            return null;

        return board[x][y];
    }

    private boolean checkNear(int x, int y){
        List<Tile> around = new ArrayList<>();

        // get the 4 adjacent tiles
        around.add(get(x+1, y));
        around.add(get(x-1, y));
        around.add(get(x, y+1));
        around.add(get(x, y-1));

        // check if at least one is not a wall or an empty space.
        for(Tile tile: around)
            if(tile != null && tile != Tile.WALL_TILE && tile != Tile.EMPY_TILE)
                return true;

        return false;
    }

    public Result<Tile> setTile(int x, int y, Tile t, char ori){
        Tile result = get(x, y);

        if(result == null || result == Tile.WALL_TILE)
            return Result.err("cant place out of bound");

        if(result != Tile.EMPY_TILE)
            return Result.err("occupied tile");

        // there is a tile near?
        if(!checkNear(x, y))
            return Result.err("cant be placed in the void");

        set(x, y, t, ori);
        return Result.ok(t);
    }

    public Result<Tile> getTile(int x, int y) {
        Tile t = get(x, y);

        if(t == null || t == Tile.WALL_TILE)
            return Result.err("out of bound");

        return Result.ok(t);
    }

    public Result<Character> getOri(int x, int y) {
        return (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT)?
                Result.err("out of bound") :
                Result.ok(orientation[x][y]);
    }

    public Result<Tile> bookTile(Tile t){
        // check if there is space left and if its not already present
        if(booked.size() >= 2)
            return Result.err("booked tiles full");

        if(booked.contains(t))
            return Result.err("already booked");

        booked.add(t);
        return Result.ok(t);
    }

    public Result<Tile> useBookedTile(Tile t, char ori, int x, int y){
        if(!booked.contains(t))
            return Result.err("not booked");

        Result<Tile> result = setTile(x, y, t, ori);

        if(result.isOk())
            booked.remove(t);

        return result;
    }

    public List<Tile> getBooked() {
        return booked;
    }
}
