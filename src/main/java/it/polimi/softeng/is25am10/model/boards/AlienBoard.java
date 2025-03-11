package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;

import java.io.IOException;
import java.util.*;
import java.util.ArrayList;

/**
 * This class offers the capacity to change the number of aliens held on a tile
 */
public class AlienBoard extends ElementsBoard{
    private final Tile.Type type;

    public enum Type{
        PURPLE, BROWN;
    }

    public AlienBoard(TilesBoard board, Type type) {
        super(board);
        this.type = switch (type){
            case PURPLE -> Tile.Type.P_ADDON;
            case BROWN -> Tile.Type.B_ADDON;
        };
    }

    /**
     * Checks if there is an addon of the specified type in the tiles adjacent to the given coordinates.
     *
     * @param c
     * @return true if an adjacent tile contains an addon of the specified type, false otherwise
     */
    private boolean thereIsAddon(Coordinate c){
        List<Result<Tile>> tiles = new ArrayList<>();

        try{tiles.add(board.getTile(c.left()));}catch(IOException _){}
        try{tiles.add(board.getTile(c.right()));}catch(IOException _){}
        try{tiles.add(board.getTile(c.up()));}catch(IOException _){}
        try{tiles.add(board.getTile(c.down()));}catch(IOException _){}


        for(Result<Tile> result: tiles)
            if(result.isOk() && result.getData().getType() == type)
                return true;

        return false;
    }

    @Override
    public boolean check(Coordinate c, int qty) {
        return board.getTile(c).getData().getType() == Tile.Type.HOUSE
                && thereIsAddon(c) && qty <= 1 && (get(c) + qty <= 1);
    }
}
