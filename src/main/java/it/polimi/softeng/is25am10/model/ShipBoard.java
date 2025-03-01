package it.polimi.softeng.is25am10.model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ShipBoard {

    private static final Tile WALL_TILE = new Tile(TilesType.WALL, "ssss");
    private static final Tile EMPY_TILE = new Tile(TilesType.EMPTY, "ssss");

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

    private final ShipContainer<Tile> board;
    private final ShipContainer<Character> orientation;
    private final List<Tile> booked;
    private final List<Tile> trashed;

    public ShipBoard(){
        board = new ShipContainer<>(EMPY_TILE);
        orientation = new ShipContainer<>('n');
        trashed = new ArrayList<>();
        booked = new ArrayList<>();

        for(Pair<Integer, Integer> position : WALL_POSITION)
            board.set(position.getKey(), position.getValue(), WALL_TILE);

        board.set(3, 2, new Tile(TilesType.C_HOUSE, "uuuu"));
    }

    private boolean checkNear(int x, int y){
        List<Result<Tile>> around = new ArrayList<>();

        around.add(board.get(x+1, y));
        around.add(board.get(x-1, y));
        around.add(board.get(x, y+1));
        around.add(board.get(x, y-1));

        for(Result<Tile> result : around){
            if(result.isAccepted() && result.getData() != WALL_TILE && result.getData() != EMPY_TILE)
                return true;
        }

        return false;
    }

    public Result<Tile> setTile(int x, int y, Tile t, char ori){
        Result<Tile> result = board.get(x, y);

        if(!result.isAccepted())
            return result;

        Tile prev = result.getData();

        if(prev == WALL_TILE)
            return new Result<>(false, null, "cant place out of bound");

        if(prev != EMPY_TILE)
            return new Result<>(false, null, "occupied tile");

        if(!checkNear(x, y))
            return new Result<>(false, null, "cant be placed in the void");

        orientation.set(x, y, ori);
        return board.set(x, y, t);
    }

    public Result<Tile> getTile(int x, int y) {
        return board.get(x, y);
    }

    public Result<Character> getOrientation(int x, int y) {
        return orientation.get(x, y);
    }

    public Result<Tile> bookTile(Tile t){
        if(booked.size() >= 2)
            return new Result<>(false, null, "booked tiles full");

        if(booked.contains(t))
            return new Result<>(false, null, "already booked");

        booked.add(t);
        return new Result<>(true, t, null);
    }

    public Result<Tile> useBookedTile(Tile t, char ori, int x, int y){
        if(!booked.contains(t))
            return new Result<>(false, null, "not booked");

        Result<Tile> result = setTile(x, y, t, ori);

        if(result.isAccepted())
            booked.remove(t);

        return result;
    }

    public List<Tile> getBooked() {
        return booked;
    }
}
