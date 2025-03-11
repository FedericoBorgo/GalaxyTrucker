package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import javafx.util.Pair;

import java.util.ArrayList;
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
    protected final Map<Coordinate, Integer> positions;
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
        other = new ArrayList<>();
    }
    // Get methods

    /**
     * Returns {@code positions}, a map storing how many units of a certain type
     * are present in each tile (with defaultValue=0 for each tile)
     * @return {@code positions}
     */
    public Map<Coordinate, Integer> getPositions() {
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
     * @param c
     * @return number of units on a certain tile
     */
    public int get(Coordinate c){
        return positions.getOrDefault(c, 0);
    }

    /**
     * Sets the number of units on a certain tile, specified though the coordinates
     * {@code x} and {@code y} to {@code value}. It modifies the total number of units based
     * on the previous number of units on that tile.
     * @param c
     * @param value the new number of units on the tile
     */
    protected void set(Coordinate c, int value) {
        total += (value - get(c));

        if(value == 0)
            positions.remove(c);

        positions.put(c, value);
    }

    // Remove units from a position

    /**
     * Removes units from a certain tile, specified though the coordinates {@code x}
     * and {@code y}, by the quantity {@code qty}. Uses the get and set methods from this class.
     * Checks if there are enough units to be removed on the tile
     * @param c
     * @param qty the number of units to be removed from the tile.
     * @return a {@code Result} containing the number of units remaining on the tile,
     * or an error {@code Result} with a message explaining why the operation failed
     */
    public Result<Integer> remove(Coordinate c, int qty){
        int value = get(c);

        if(value < qty)
            return Result.err("not enough items in board");

        set(c, value - qty);

        return Result.ok(value - qty);
    }

    /**
     * Move units from one position to another. Does not change the total number of units on the board.
     * Uses the remove and put method from this class. Put is implemented in the subclasses.
     * Result.data should not be accessed.
     * @param fromc
     * @param toc
     * @param qty the number of units being moved
     * @return a successful {@code Result} or an error {@code Result} with a message explaining
     * why the operation failed
     */
    public Result<Integer> move(Coordinate fromc, Coordinate toc, int qty) {
        Result<Integer> res = remove(fromc, qty);

        if(res.isErr())
            return res;

        res = put(toc, qty);

        if(res.isErr())
            put(fromc, qty);

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
     * Remove all the qty in a list of positions. If some remove fails, undo
     * all the changes and return err.
     *
     * @param positions the list of the coordinates and qty
     * @return
     */
    public Result<Integer> remove(List<Pair<Coordinate, Integer>> positions) {
        Result<Integer> res;
        int total = 0;

        if(!checkPresence(positions))
            return Result.err("not enough items in the specified positions");

        for(Pair<Coordinate, Integer> p : positions){
            res = remove(p.getKey(), p.getValue());
            total += p.getValue();

            if(res.isErr())
                throw new IllegalStateException("remove and checkPresence do not return the same value");
        }

        return Result.ok(total);
    }

    /**
     * Check if there are enough items in the specified position.
     * @param positions the list of positions and values
     * @return true if there are enough, false if not.
     */
    public boolean checkPresence(List<Pair<Coordinate, Integer>> positions){
        for(Pair<Coordinate, Integer> p : positions){
            if(get(p.getKey()) > p.getValue())
                return false;
        }
        return true;
    }

    /**
     * Adds a number {@code qty} of units to the tile specified though the coordinates {@code x}
     * and {@code y}.
     * Result.data should not be accessed.
     * @param c
     * @param qty the number of units to be added to the tile
     * @return a successful {@code Result} or an error {@code Result} with a message explaining
     * why the operation failed
     */
    public Result<Integer> put(Coordinate c, int qty){
        Result<Tile> resBoard = board.getTile(c);

        // out of bound
        if(resBoard.isErr())
            return Result.err(resBoard.getReason());

        for(ElementsBoard b: other)
            if(b.get(c) > 0)
                return Result.err("occupied by others");

        if(!check(c, qty))
            return Result.err("cant place here");

        set(c, get(c) + qty);
        return Result.ok(get(c));
    }


    /**
     * Remove all the illegals elements in a board.
     *
     * @return the list f removed elements
     */
    public List<Coordinate> removeIllegals() {
        List<Coordinate> toRemove = new ArrayList<>();
        positions.forEach((c, qty) -> {

            if(!check(c, 0))
                toRemove.add(c);
        });

        toRemove.forEach(positions::remove);
        return toRemove;
    }

    /**
     * Check if a quantity is placeable in a specified coordinate.
     * This depends on the specified pawn. The check is implemented in
     * subclasses.
     * @param c
     * @param qty
     * @return
     */
    public abstract boolean check(Coordinate c, int qty);
}
