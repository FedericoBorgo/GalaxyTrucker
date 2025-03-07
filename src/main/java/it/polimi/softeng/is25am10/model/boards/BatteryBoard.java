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

    /**
     * Places a specified quantity {@code qty} of batteries at the given coordinates on the board.
     * The method verifies that the tile can hold batteries and ensures
     * the quantity does not exceed the allowed limit based on the tile's type.
     *
     * @param x the x-coordinate of the tile where the batteries are to be added
     * @param y the y-coordinate of the tile where the batteries are to be added
     * @param qty the quantity of batteries to place on the tile
     * @return a Result containing the updated total number of batteries at the specified location
     *         if the operation is successful, or an error result if the operation fails
     */
    @Override
    public Result<Integer> put(int x, int y, int qty) {

        // Get Tile from TilesBoard and do an out-of-bound check
        Result<Tile> res = board.getTile(x, y);
        if(res.isErr())
            return Result.err(res.getReason());

        Tile tile = board.getTile(x, y).getData();

        // Check tile type
        if(tile.getType() != TilesType.BATTERY_2 && tile.getType() != TilesType.BATTERY_3)
            return Result.err("tile is not a Battery Tile");

        // Add batteries
        int nBatteries = get(x, y) + qty;
        int max = tile.getType() == TilesType.BATTERY_2 ? 2 : 3;
        // Check qty to be added
        if (nBatteries > max)
            return Result.err("too many batteries");

        set(x, y, qty + get(x, y));
        return Result.ok(nBatteries);
    }
}
