package it.polimi.softeng.is25am10.model;

public class BatteryBoard extends ElementsBoard{
    public BatteryBoard(ShipBoard board) {
        super(board);
    }

    @Override
    public Result<Integer> put(int x, int y, int qty) {
        return null;
    }
}
