package it.polimi.softeng.is25am10.model;

import java.util.List;

public abstract class ElementsBoard {
    protected final ShipContainer<Integer> location;
    protected int total;
    protected final ShipBoard board;
    protected List<ElementsBoard> other;

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

    public void setBoards(List<ElementsBoard> other){
        this.other = other;
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

    public Result<Integer> move(int fromx, int fromy, int tox, int toy, int qty) {
        Result<Integer> resRemove = remove(fromx, fromy, qty);

        if(!resRemove.isAccepted())
            return resRemove;

        Result<Integer> resMove = put(tox, toy, qty);

        if(!resMove.isAccepted())
            put(fromx, fromy, qty);

        return resMove;
    }

    public Result<Integer> get(int x, int y){
        return location.get(x, y);
    }

    public abstract Result<Integer> put(int x, int y, int qty);
}
