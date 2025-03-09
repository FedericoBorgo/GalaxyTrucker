package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.TilesType;

import java.util.*;
import java.util.ArrayList;

/**
 * This class offers the capacity to change the number of aliens held on a tile
 */
public class AlienBoard extends ElementsBoard{
    private final TilesType type;

    public AlienBoard(TilesBoard board, char alienType) {
        super(board);
        this.type = alienType == 'p'? TilesType.P_ADDON : TilesType.B_ADDON;
    }

    /**
     * Checks if there is an addon of the specified type in the tiles adjacent to the given coordinates.
     *
     * @param x the x-coordinate of the tile to check
     * @param y the y-coordinate of the tile to check
     * @return true if an adjacent tile contains an addon of the specified type, false otherwise
     */
    private boolean thereIsAddon(int x, int y){
        List<Result<Tile>> tiles = new ArrayList<>();

        tiles.add(board.getTile(x-1, y));
        tiles.add(board.getTile(x+1, y));
        tiles.add(board.getTile(x, y-1));
        tiles.add(board.getTile(x, y+1));

        for(Result<Tile> result: tiles)
            if(result.isOk() && result.getData().getType() == type)
                return true;

        return false;
    }

    @Override
    public boolean check(int x, int y, int qty) {
        return board.getTile(x, y).getData().getType() == TilesType.HOUSE
                && thereIsAddon(x, y) && qty <= 1 && (get(x, y) + qty <= 1);
    }
}
