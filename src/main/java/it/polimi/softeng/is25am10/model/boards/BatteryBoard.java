package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.TilesType;

/**
 * This class offers the capacity to change the number of units held in a battery tile.
 */

public class BatteryBoard extends ElementsBoard {

    public BatteryBoard(TilesBoard board) {
        super(board);
    }

    @Override
    public boolean check(int x, int y, int qty) {
        Tile t = board.getTile(x, y).getData();
        return Tile.battery(t) &&
                (qty + get(x, y) <= (t.getType() == TilesType.BATTERY_2 ? 2 : 3));
    }
}
