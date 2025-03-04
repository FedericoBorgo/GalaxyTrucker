package it.polimi.softeng.is25am10.model;

public class BatteryBoard extends ElementsBoard {
    public BatteryBoard(ShipBoard board) {
        super(board);
    }

    @Override
    public Result<Integer> put(int x, int y, int qty) {

        // Get Tile from ShipBoard and do an out-of-bound check
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
