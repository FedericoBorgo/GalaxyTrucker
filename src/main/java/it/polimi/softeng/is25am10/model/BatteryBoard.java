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

        // Add batteries
        if (tile.getType() == TilesType.BATTERY_2) {
            set(x, y, 2 + get(x, y)); // get(x, y) should always be 0
        } else if (tile.getType() == TilesType.BATTERY_3) {
            set(x, y, 3 + get(x, y)); // get(x, y) should always be 0
        } else {
            return new Result<>(false, null, "tile is not a Battery Tile");
        }
        return null;
    }
}
