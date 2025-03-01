package it.polimi.softeng.is25am10.model;

public abstract class ElementsBoard {
    private final ShipContainer<Integer> location;
    private int total;
    protected final ShipBoard board;

    public ElementsBoard(ShipBoard board) {
        this.board = board;
        total = 0;
        location = new ShipContainer<>(0);
    }

    public ShipContainer<Integer> getLocation() {
        return location;
    }

    public int getTotal() {
        return total;
    }

    public Result<Integer> remove(int x, int y, int qty){
        Result<Integer> response = location.get(x, y);

        if(!response.isAccepted())
            return response;

        if(response.getData() < qty)
            return new Result<>(false, null, "not enough items in board");

        total -= qty;
        return location.set(x, y, response.getData() - qty);
    }

    public abstract Result<Integer> put(int x, int y, int qty);

    public abstract Result<Integer> move(int fromx, int fromy, int tox, int toy, int qty);
}
