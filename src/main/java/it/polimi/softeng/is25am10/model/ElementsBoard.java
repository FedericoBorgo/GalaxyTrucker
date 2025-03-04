package it.polimi.softeng.is25am10.model;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ElementsBoard {
    // Positions is a map storing how many units of a certain type are present in each tile (with defaultValue=0)
    protected final Map<Pair<Integer, Integer>, Integer> positions;
    // Total stores the total number of units of a certain type are present on the board
    protected int total;
    // The matrix containing all the tiles (including empty spaces)
    protected final ShipBoard board;
    // List of other boards containing units which interact with the units we are working on
    protected List<ElementsBoard> other;

    // Constructor method
    public ElementsBoard(ShipBoard board) {
        this.board = board;
        total = 0;
        positions = new HashMap<>();
    }
    // Get methods
    public Map<Pair<Integer, Integer>, Integer> getPositions() {
        return positions;
    }
    public int getTotal() {
        return total;
    }
    public int get(int x, int y){
        return positions.getOrDefault(new Pair<>(x, y), 0);
    }

    // Set new value in a certain position (accounting for total)
    protected void set(int x, int y, int value) {
        total += (value - get(x, y));
        positions.put(new Pair<>(x, y), value);
    }

    // Remove units from a position
    public Result<Integer> remove(int x, int y, int qty){
        int value = get(x, y);

        if(value < qty)
            return new Result<>(false, null, "not enough items in board");

        total -= qty;
        set(x, y, value - qty);

        return new Result<>(true, value - qty, null);
    }
    // Move units from one position to another
    public Result<Integer> move(int fromx, int fromy, int tox, int toy, int qty) {
        Result<Integer> resRemove = remove(fromx, fromy, qty);

        if(!resRemove.isAccepted())
            return resRemove;

        Result<Integer> resMove = put(tox, toy, qty);

        if(!resMove.isAccepted())
            put(fromx, fromy, qty);

        return resMove;
    }
    // Set the list of other boards needed for the operation of this board
    public void setBoards(List<ElementsBoard> other){
        this.other = other;
    }

    // Abstract methods
    public abstract Result<Integer> put(int x, int y, int qty);
}
