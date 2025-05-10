package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;

import java.io.IOException;
import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Record used to store all the coordinates of the ship board.
 * If a coordinate is not valid, it throws a IndexOutOfBoundsException.
 *
 * @param x x coordinate
 * @param y y coordinate
 */
public record Coordinate(int x, int y) implements Serializable {
    /**
     * Check if a pair of (x, y) is a valid coordinate.
     * It checks if it is not  out of bound.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return true if the coordinate is invalid, false if not
     */
    static public boolean isInvalid(int x, int y) {
        return x < 0 || x >= TilesBoard.BOARD_WIDTH || y < 0 || y >= TilesBoard.BOARD_HEIGHT;
    }

    /**
     * Build a coordinate from a pair of raw coordinate.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @throws IndexOutOfBoundsException if the coordinate pair is not valid
     */
    public Coordinate {
        if (isInvalid(x, y))
            throw new IndexOutOfBoundsException();
    }

    /**
     * Get the left coordinate position.
     *
     * @return new left coordinate
     * @throws IOException if it is not valid
     */
    public Coordinate left() throws IOException {
        if (isInvalid(x - 1, y))
            throw new IOException("left coordinate out of bounds");
        return new Coordinate(x - 1, y);
    }

    /**
     * Get the right coordinate position.
     *
     * @return new right coordinate
     * @throws IOException if it is not valid
     */
    public Coordinate right() throws IOException {
        if (isInvalid(x + 1, y))
            throw new IOException("right coordinate out of bounds");
        return new Coordinate(x + 1, y);
    }

    /**
     * Get the up coordinate position.
     *
     * @return new up coordinate
     * @throws IOException if it is not valid
     */
    public Coordinate up() throws IOException {
        if (isInvalid(x, y - 1))
            throw new IOException("up coordinate out of bounds");
        return new Coordinate(x, y - 1);
    }

    /**
     * Get the down coordinate position.
     *
     * @return new down coordinate
     * @throws IOException if it is not valid
     */
    public Coordinate down() throws IOException {
        if (isInvalid(x, y + 1))
            throw new IOException("down coordinate out of bounds");
        return new Coordinate(x, y + 1);
    }

    /**
     * Execute the consumer for every single coordinate.
     * Used to iterate for every coordinate in the board.
     *
     * @param consumer to call for every coordinate.
     */
    static public void forEach(Consumer<Coordinate> consumer) {
        for (int i = 0; i < TilesBoard.BOARD_WIDTH; i++)
            for (int j = 0; j < TilesBoard.BOARD_HEIGHT; j++)
                consumer.accept(new Coordinate(i, j));
    }

    /**
     * Execute the predicate for every single coordinate until
     * it returns false.
     * Used to iterate for every coordinate in the board.
     *
     * @param predicate to call for every coordinate
     */
    static public void forEachUntil(Predicate<Coordinate> predicate) {
        boolean ok = true;
        for (int i = 0; i < TilesBoard.BOARD_WIDTH && ok; i++)
            for (int j = 0; j < TilesBoard.BOARD_HEIGHT && ok; j++)
                ok = predicate.test(new Coordinate(i, j));
    }

    /**
     * Execute the predicate for every single coordinate of a colum until
     * it returns false.
     * Used to iterate for every coordinate in the board.
     *
     * @param x the colum to iterate
     * @param predicate to call for every coordinate
     */
    static public void forEachUntil(int x, Predicate<Coordinate> predicate) {
        boolean ok = true;
        if (x < 0 || x >= TilesBoard.BOARD_WIDTH)
            return;
        for (int j = 0; j < TilesBoard.BOARD_HEIGHT && ok; j++)
            ok = predicate.test(new Coordinate(x, j));
    }

    /**
     * Execute the predicate for every single coordinate of a row until
     * it returns false.
     * Used to iterate for every coordinate in the board.
     *
     * @param y the row to iterate
     * @param predicate to call for every coordinate
     */
    static public void forEachUntil(Predicate<Coordinate> predicate, int y) {
        boolean ok = true;
        if (y < 0 || y >= TilesBoard.BOARD_HEIGHT)
            return;
        for (int i = 0; i < TilesBoard.BOARD_WIDTH && ok; i++)
            ok = predicate.test(new Coordinate(i, y));
    }

    /**
     * Convert a string to a Coordinate. If the conversion fails, it returns
     * an empty result.
     * The coordinate must have this format: x0y0 to x6y4.
     *
     * @param s to convert
     * @return ok(coordinate) it the conversion succeeded, err if not
     */
    static public Result<Coordinate> fromString(String s) {
        if (s.length() != 4 ||
            s.charAt(0) != 'x' ||
            s.charAt(2) != 'y' ||
            !Character.isDigit(s.charAt(1)) ||
            !Character.isDigit(s.charAt(3))
        )
            return Result.err("string is not a coordinate");

        int x = s.charAt(1) - '0';
        int y = s.charAt(3) - '0';

        if(isInvalid(x, y))
            return Result.err("out of bound");

        return Result.ok(new Coordinate(x, y));
    }

    /**
     * Convert this coordinate to the corresponding string.
     *
     * @return the converted string
     */
    @Override
    public String toString(){
        return "x" + x + "y" + y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return equals(that.x, that.y);
    }

    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }
}
