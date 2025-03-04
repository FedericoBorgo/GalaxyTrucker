package it.polimi.softeng.is25am10.model;

public class BatteryBoard extends ElementsBoard {
    public BatteryBoard(ShipBoard board) {
        super(board);
    }

    @Override
    public Result<Integer> put(int x, int y, int qty) {

        // Get Tile from ShipBoard and do an out-of-bound check
        Result<Tile> resBoard = board.getTile(x, y);
        if(!resBoard.isAccepted())
            return new Result<>(false,null,resBoard.getReason());
        Tile tile = board.getTile(x, y).getData();

        // Check tile type
        if(tile.getType() != TilesType.BATTERY_2 && tile.getType() != TilesType.BATTERY_3)
            return new Result<>(false, null, "tile is not a Battery Tile");

        // Add batteries
        int nBatteries = get(x, y) + qty;
        // Check qty to be added
        if ((tile.getType() == TilesType.BATTERY_2 && nBatteries > 2) || (tile.getType() == TilesType.BATTERY_3 && nBatteries > 3))
            return new Result<>(false,null, "too many batteries");
        set(x, y, qty + get(x, y));
        return new Result<>(true, nBatteries, null);
    }
}
