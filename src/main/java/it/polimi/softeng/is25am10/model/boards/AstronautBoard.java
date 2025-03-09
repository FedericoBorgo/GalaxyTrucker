package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;

/**
 * This class offers the capacity to change the number of astronauts held on a tile
 */
public class AstronautBoard extends ElementsBoard{
    public AstronautBoard(TilesBoard board) {
        super(board);
    }

    @Override
    public boolean check(int x, int y, int qty) {
        return Tile.house(board.getTile(x, y).getData()) && (get(x, y) + qty <= 2);
    }
}
