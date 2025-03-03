package it.polimi.softeng.is25am10.model;

public class AstronautBoard extends ElementsBoard{
    public AstronautBoard(ShipBoard board) {
        super(board);
    }

    @Override
    public Result<Integer> put(int x, int y, int qty) {
        return null;
    }
}
