package it.polimi.softeng.is25am10.model;

class ElementsPlaceholder extends ElementsBoard {

    public ElementsPlaceholder(ShipBoard board) {
        super(board);
    }

    @Override
    public Result<Integer> put(int x, int y, int qty) {
        set(x, y, qty);
        return new Result<>(true, qty, null);
    }
}
