package it.polimi.softeng.is25am10.model;

public class AlienBoard extends ElementsBoard{
    public AlienBoard(ShipBoard board) {
        super(board);
    }

    @Override
    public Result<Integer> put(int x, int y, int qty) {
        return null;
    }

    @Override
    public Result<Integer> move(int fromx, int fromy, int tox, int toy, int qty) {
        return null;
    }
}
