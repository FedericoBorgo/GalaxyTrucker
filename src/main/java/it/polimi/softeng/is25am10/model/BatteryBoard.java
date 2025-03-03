package it.polimi.softeng.is25am10.model;

public class BatteryBoard extends ElementsBoard{
    public BatteryBoard(ShipBoard board) {
        super(board);
    }

    @Override
    public Result<Integer> put(int x, int y, int qty) {
        Result<Tile> resTile = board.getTile(x, y);

        if(!resTile.isAccepted())
            return new Result<>(false, null, resTile.getReason());

        TilesType tileType = resTile.getData().getType();

        if(tileType != TilesType.BATTERY_2 && tileType != TilesType.BATTERY_3)
            return new Result<>(false, null, "cant place in a non battery tile");

        int maxQty = tileType == TilesType.BATTERY_2 ? 2 : 3;
        int value = location.get(x, y).getData();

        if(value + qty > maxQty)
            return new Result<>(false, null, "too many batteries");

        total += qty;
        return location.set(x, y, qty + value);
    }
}
