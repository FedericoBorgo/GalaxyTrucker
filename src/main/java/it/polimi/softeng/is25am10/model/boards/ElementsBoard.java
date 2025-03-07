package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used as a superclass by the classes AstronautBoard, AlienBoard, BatteryBoard and GoodsBoard.
 * It provides a framework for those classes which place units above certain tiles.
 * Contains some functions such as constructor, get methods, and some basic management such as
 * removing units or moving units between tiles.
 */
public abstract class ElementsBoard {
    protected final Map<Pair<Integer, Integer>, Integer> positions;
    protected int total;
    // The matrix containing all the tiles (including empty spaces); board is associated with a specific player
    protected final TilesBoard board;
    // List of other boards containing units which interact with the units we are working on
    protected List<ElementsBoard> other;

    /**
     * Constructor method that can be used in the subclasses.
     * @param board The TilesBoard of a certain player
     */
    public ElementsBoard(TilesBoard board) {
        this.board = board;
        total = 0;
        positions = new HashMap<>();
    }
    // Get methods

    /**
     * Returns {@code positions}, a map storing how many units of a certain type
     * are present in each tile (with defaultValue=0 for each tile)
     * @return {@code positions}
     */
    public Map<Pair<Integer, Integer>, Integer> getPositions() {
        return positions;
    }

    /**
     * {@code total} stores the total number of units of a certain type that are present on the board
     * @return {@code total}
     */
    public int getTotal() {
        return total;
    }

    /**
     * Retrieves the number of units on a certain tile, specified though the coordinates {@code x} and {@code y}.
     * Uses the method getOrDefault of the Map class.
     * @param x the x-coordinate of the tile to retrieve
     * @param y the y-coordinate of the tile to retrieve
     * @return number of units on a certain tile
     */
    public int get(int x, int y){
        return positions.getOrDefault(new Pair<>(x, y), 0);
    }

    /**
     * Sets the number of units on a certain tile, specified though the coordinates
     * {@code x} and {@code y} to {@code value}. It modifies the total number of units based
     * on the previous number of units on that tile.
     * @param x the x-coordinate of the tile to retrieve
     * @param y the y-coordinate of the tile to retrieve
     * @param value the new number of units on the tile
     */
    protected void set(int x, int y, int value) {
        total += (value - get(x, y));
        positions.put(new Pair<>(x, y), value);
    }

    // Remove units from a position

    /**
     * Removes units from a certain tile, specified though the coordinates {@code x}
     * and {@code y}, by the quantity {@code qty}. Uses the get and set methods from this class.
     * Checks if there are enough units to be removed on the tile
     * @param x the x-coordinate of the tile to retrieve
     * @param y the y-coordinate of the tile to retrieve
     * @param qty the number of units to be removed from the tile.
     * @return a {@code Result} containing the number of units remaining on the tile,
     * or an error {@code Result} with a message explaining why the operation failed
     */
    public Result<Integer> remove(int x, int y, int qty){
        int value = get(x, y);

        if(value < qty)
            return Result.err("not enough items in board");

        set(x, y, value - qty);

        return Result.ok(value - qty);
    }

    /**
     * Move units from one position to another. Does not change the total number of units on the board.
     * Uses the remove and put method from this class. Put is implemented in the subclasses.
     * Result.data should not be accessed.
     * @param fromx the x-coordinate of the tile the units are from
     * @param fromy the y-coordinate of the tile the units are from
     * @param tox the x-coordinate of the tile the units are being moved to
     * @param toy the y-coordinate of the tile the units are being moved to
     * @param qty the number of units being moved
     * @return a successful {@code Result} or an error {@code Result} with a message explaining
     * why the operation failed
     */
    public Result<Integer> move(int fromx, int fromy, int tox, int toy, int qty) {
        Result<Integer> res = remove(fromx, fromy, qty);

        if(res.isErr())
            return res;

        res = put(tox, toy, qty);

        if(res.isErr())
            put(fromx, fromy, qty);

        return Result.ok(qty);
    }

    /**
     * Sets the list of other boards needed for the operation of this board.
     * Not all subclasses use or even need this method, but some do.
     * @param other List of other boards that interact with this instance of board.
     */
    public void setOthers(List<ElementsBoard> other){
        this.other = other;
    }

    /**
     * Adds a number {@code qty} of units to the tile specified though the coordinates {@code x}
     * and {@code y}. The method is implemented in the subclasses.
     * Result.data should not be accessed.
     * @param x the x-coordinate of the tile to retrieve
     * @param y the y-coordinate of the tile to retrieve
     * @param qty the number of units to be added to the tile
     * @return a successful {@code Result} or an error {@code Result} with a message explaining
     * why the operation failed
     */
    public abstract Result<Integer> put(int x, int y, int qty);
}
