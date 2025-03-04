package it.polimi.softeng.is25am10.model;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ElementsBoard {
    protected final Map<Pair<Integer, Integer>, Integer> positions;
    protected int total;
    protected final ShipBoard board;
    protected List<ElementsBoard> other;

    public ElementsBoard(ShipBoard board) {
        this.board = board;
        total = 0;
        positions = new HashMap<>();
    }

    public Map<Pair<Integer, Integer>, Integer> getPositions() {
        return positions;
    }

    public int get(int x, int y){
        return positions.getOrDefault(new Pair<>(x, y), 0);
    }

    protected void set(int x, int y, int value) {
        total += (value - get(x, y));
        positions.put(new Pair<>(x, y), value);
    }

    public int getTotal() {
        return total;
    }

    public void setBoards(List<ElementsBoard> other){
        this.other = other;
    }

    public Result<Integer> remove(int x, int y, int qty){
        int value = get(x, y);

        if(value < qty)
            return new Result<>(false, null, "not enough items in board");

        set(x, y, value - qty);

        return new Result<>(true, value - qty, null);
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

    public abstract Result<Integer> put(int x, int y, int qty);
}
