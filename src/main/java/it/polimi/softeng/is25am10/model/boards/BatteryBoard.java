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
    public boolean check(Coordinate c, int qty) {
        Tile t = board.getTile(c).getData();
        return Tile.battery(t) &&
                (qty + get(c) <= (t.getType() == TilesType.BATTERY_2 ? 2 : 3));
    }
}
