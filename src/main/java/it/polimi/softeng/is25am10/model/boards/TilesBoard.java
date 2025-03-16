package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Projectile;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * TilesBoard Represents a ship board where tiles can be placed, booked, (or trashed).
 * It offers methods to place a tile, to get it, to read its orientation,
 * to manage the booking process of tiles (and to manage trashed tiles). To be added.
 */
public class TilesBoard {
    // coordinates of unplaceable tiles
    private static final Coordinate[] WALL_POSITION = new Coordinate[]{
            new Coordinate(0, 0),
            new Coordinate(1, 0),
            new Coordinate(3, 0),
            new Coordinate(5, 0),
            new Coordinate(6, 0),
            new Coordinate(0, 1),
            new Coordinate(6, 1),
            new Coordinate(3, 4),
    };

    public static final int BOARD_WIDTH = 7;
    public static final int BOARD_HEIGHT = 5;

    private final Tile[][] board;
    private final Tile.Rotation[][] rotation;
    private final List<Tile> booked;
    private final List<Tile> trashed;

    /**
     * Initializes {@code TilesBoard} with {@code EMPTY_TILE} and {@code WALL_TILE}
     * Places the Central housing unit on the board
     */
    public TilesBoard() {
        board = new Tile[BOARD_WIDTH][BOARD_HEIGHT];
        rotation = new Tile.Rotation[BOARD_WIDTH][BOARD_HEIGHT];
        trashed = new ArrayList<>();
        booked = new ArrayList<>();

        // fill with empty tiles (on which can be placed the game tiles)
        Coordinate.forEach(c -> set(c, Tile.EMPTY_TILE, Tile.Rotation.NONE));

        // fill the implacable spaces with WALL
        for (Coordinate c : WALL_POSITION)
            set(c, Tile.WALL_TILE, Tile.Rotation.NONE);

        // the start of building a ship
        set(new Coordinate(3, 2), new Tile(Tile.Type.C_HOUSE, "uuuu"), Tile.Rotation.NONE);
    }

    //Places a tile at the specified coordinates on the board with a given orientation.
    private void set(Coordinate c, Tile tile, Tile.Rotation rotation) {
        this.rotation[c.x()][c.y()] = rotation;
        board[c.x()][c.y()] = tile;
    }

    // Retrieves a tile from the board at the specified coordinates.
    // If the specified coordinates are out of bounds, returns null.
    private Tile get(Coordinate c) {
        return board[c.x()][c.y()];
    }

    public Tile[][] getBoard() {
        return board;
    }

    public Tile.Rotation[][] getRotation() {
        return rotation;
    }

    public List<Tile> getTrashed() {
        return trashed;
    }


    // Checks if there is at least one tile adjacent to the specified position
    // that is not a wall or an empty space.
    private boolean checkNear(Coordinate c) {
        List<Tile> around = new ArrayList<>();

        // get the 4 adjacent tiles
        try {
            around.add(get(c.left()));
        } catch (IOException _) {
        }
        try {
            around.add(get(c.right()));
        } catch (IOException _) {
        }
        try {
            around.add(get(c.up()));
        } catch (IOException _) {
        }
        try {
            around.add(get(c.down()));
        } catch (IOException _) {
        }

        // check if at least one is not a wall or an empty space.
        for (Tile tile : around)
            if (tile != null && Tile.real(tile))
                return true;

        return false;
    }

    /**
     * Attempts to place a tile at the specified coordinates on the board with a given orientation.
     * Checks if the target position is valid (not out of bound), if the position is free and
     * if there are tiles nearby (tiles can't be placed in the void, they need an adjacent tile).
     *
     * @param c        the coordinate
     * @param t        the tile to be placed
     * @param rotation the rotation of the tile
     * @return a Result object containing the placed tile if the placement is successful,
     * or an error Result with a reason explaining why the placement failed
     */
    public Result<Tile> setTile(Coordinate c, Tile t, Tile.Rotation rotation) {
        Tile result = get(c);

        if (result == null || result == Tile.WALL_TILE)
            return Result.err("cant place out of bound");

        if (result != Tile.EMPTY_TILE)
            return Result.err("occupied tile");

        // is there a tile nearby?
        if (!checkNear(c))
            return Result.err("cant be placed in the void");

        set(c, t, rotation);
        return Result.ok(t);
    }

    /**
     * Retrieves the tile located at the specified coordinates on the board.
     * If the coordinates are out of bounds or the tile is a wall, an error result is returned.
     *
     * @param c the coordinate
     * @return a Result containing the tile if the coordinates are valid and the tile is not a wall,
     * or an error result with a relevant message if the retrieval fails
     */
    public Result<Tile> getTile(Coordinate c) {
        Tile t = get(c);

        if (t == null || t == Tile.WALL_TILE)
            return Result.err("out of bound");

        return Result.ok(t);
    }

    /**
     * Retrieves the rotation of the tile located at the specified coordinates on the board.
     * If the coordinates are out of bounds, an error result is returned.
     *
     * @param c the coordinate
     * @return a Result containing the rotation of the tile if the coordinates are valid,
     * or an error Result with a message indicating the coordinates are out of bounds
     */
    public Tile.Rotation getRotation(Coordinate c) {
        return rotation[c.x()][c.y()];
    }

    /**
     * Attempts to book a tile for later use. A maximum of 2 tiles can stay booked
     * at the same time.
     * Checks if there is space left (less than 2 booked tiles) and if {@code t} is not already booked.
     *
     * @param t the tile to be booked
     * @return a {@code Result} containing the booked tile if successful, or an error
     * {@code Result} with a message explaining why the booking failed
     */
    public Result<Tile> bookTile(Tile t) {
        if (booked.size() >= 2)
            return Result.err("booked tiles full");

        if (booked.contains(t))
            return Result.err("already booked");

        booked.add(t);
        return Result.ok(t);
    }

    /**
     * Attempts to use a booked tile {@code t} by placing it on the board at the specified
     * coordinates with the given orientation. If the tile is successfully placed,
     * it is removed from the booked tiles list.
     * Checks if {@code t} is in the booked list using the contains method in the Collections interface.
     *
     * @param t        the tile to be placed
     * @param rotation the orientation of the tile
     * @param c        the coordinate
     * @return a {@code Result} containing the placed tile if successful, or an
     * error {@code Result} with a message explaining why the operation
     * failed
     */
    public Result<Tile> useBookedTile(Tile t, Tile.Rotation rotation, Coordinate c) {
        if (!booked.contains(t))
            return Result.err("not booked");

        Result<Tile> result = setTile(c, t, rotation);

        if (result.isOk())
            booked.remove(t);

        return result;
    }

    /**
     * Retrieves the list of tiles currently booked on the board.
     *
     * @return a list of tiles that are booked
     */
    public List<Tile> getBooked() {
        return booked;
    }

    /**
     * Remove a tile
     *
     * @param c
     */
    public void remove(Coordinate c) {
        trashed.add(board[c.x()][c.y()]);
        board[c.x()][c.y()] = Tile.EMPTY_TILE;
        rotation[c.x()][c.y()] = Tile.Rotation.NONE;
    }

    /**
     * Check if the board is a correct board.
     * The combination Pair(-1, -1) in the return means
     * that the board contains disconnected groups of tiles.
     * The player must choose one by removing the tiles.
     *
     * @return the set of wrong tiles at the specified coordinate
     */
    public Set<Coordinate> isOK() {
        Set<Coordinate> result = new HashSet<>();
        checkConnectors(result);
        checkTiles(result);
        checkUnreachable(result);
        return result;
    }

    /**
     * From a specified coordinate, if the corresponding tiles is already
     * seen do nothing. Otherwise, it marks and seen and call itself around
     * the tile.
     *
     * @param marked
     * @param c
     */
    private void mark(boolean[][] marked, Coordinate c) {
        if (marked[c.x()][c.y()] || !Tile.real(board[c.x()][c.y()]))
            return;

        marked[c.x()][c.y()] = true;

        try {
            mark(marked, c.left());
        } catch (IOException _) {
        }
        try {
            mark(marked, c.right());
        } catch (IOException _) {
        }
        try {
            mark(marked, c.up());
        } catch (IOException _) {
        }
        try {
            mark(marked, c.down());
        } catch (IOException _) {
        }
    }

    private void checkUnreachable(Set<Coordinate> result) {
        boolean[][] marked = new boolean[BOARD_WIDTH][BOARD_HEIGHT];

        for (boolean[] m : marked)
            Arrays.fill(m, false);

        Coordinate.forEachUntil(c -> {
            if (!Tile.real(get(c)))
                return true;

            mark(marked, c);
            return false;
        });

        Coordinate.forEachUntil(c -> {
            if (Tile.real(get(c)) && !marked[c.x()][c.y()]) {
                result.add(new Coordinate(0, 0));
                return false;
            }
            return true;
        });

    }

    private void checkTiles(Set<Coordinate> result) {
        Coordinate.forEach(c -> {
            try {
                if (Tile.rocket(get(c))) {
                    if (getRotation(c) != Tile.Rotation.NONE || Tile.real(get(c.down())))
                        result.add(c);
                } else if (Tile.drills(get(c))) {
                    Tile t = switch (getRotation(c)) {
                        case NONE -> get(c.up());
                        case CLOCK -> get(c.right());
                        case DOUBLE -> get(c.down());
                        case INV -> get(c.left());
                    };

                    if (Tile.real(t))
                        result.add(c);
                }
            } catch (IOException _) {
            }
            ;
        });
    }

    private void checkConnectors(Set<Coordinate> result) {
        Coordinate.forEach(c -> {
            try {
                Tile downTile = get(c.down());
                Tile.ConnectorType upper;
                Tile.ConnectorType lower;

                if (Tile.real(get(c)) && Tile.real(downTile)) {
                    upper = Tile.getSide(get(c), getRotation(c), Tile.Side.DOWN);
                    lower = Tile.getSide(downTile, getRotation(c.down()), Tile.Side.UP);

                    if (!upper.connectable(lower))
                        result.add(c);
                }
            } catch (IOException _) {
            }
            ;

            try {
                Tile rightTile = get(c.right());
                Tile.ConnectorType lefter;
                Tile.ConnectorType righter;
                if (Tile.real(get(c)) && Tile.real(rightTile)) {
                    lefter = Tile.getSide(get(c), getRotation(c), Tile.Side.RIGHT);
                    righter = Tile.getSide(rightTile, getRotation(c.right()), Tile.Side.LEFT);

                    if (!lefter.connectable(righter))
                        result.add(c);
                }
            } catch (IOException _) {
            }
            ;

        });
    }

    public int countExposedConnectors() {
        AtomicInteger count = new AtomicInteger();
        List<Pair<Coordinate, Tile.Side>> debug = new ArrayList<>();

        Coordinate.forEach(c -> {
            if (!Tile.real(get(c)))
                return;

            Set<Tile.Side> check = new HashSet<>();

            //if the near tiles are not real (WALL or EMPTY) or
            // does not exist(IOException) check if the connector is
            //smooth
            try {
                if (!Tile.real(get(c.left())))
                    check.add(Tile.Side.LEFT);
            } catch (IOException e) {
                check.add(Tile.Side.LEFT);
            }

            try {
                if (!Tile.real(get(c.right())))
                    check.add(Tile.Side.RIGHT);
            } catch (IOException e) {
                check.add(Tile.Side.RIGHT);
            }

            try {
                if (!Tile.real(get(c.up())))
                    check.add(Tile.Side.UP);
            } catch (IOException e) {
                check.add(Tile.Side.UP);
            }

            try {
                if (!Tile.real(get(c.down())))
                    check.add(Tile.Side.DOWN);
            } catch (IOException e) {
                check.add(Tile.Side.DOWN);
            }


            for (Tile.Side s : check) {
                if (Tile.ConnectorType.SMOOTH !=
                        Tile.getSide(get(c), getRotation(c), s)) {
                    count.incrementAndGet();
                    debug.add(new Pair<>(c, s));
                }
            }
        });

        return count.get();
    }

    public double countDrillsPower(Map<Tile.Rotation, Integer> count) {
        AtomicInteger drills = new AtomicInteger();
        AtomicInteger d_drills = new AtomicInteger();

        Coordinate.forEach(c -> {
            Tile.Rotation rotation = getRotation(c);

            if (get(c).getType() == Tile.Type.DRILLS){
                if (rotation == Tile.Rotation.NONE)
                    drills.addAndGet(2);
                else
                    drills.addAndGet(1);
            }
            else if(get(c).getType() == Tile.Type.D_DRILLS){
                if(count.getOrDefault(rotation, 0) > 0){
                    count.put(rotation, count.get(rotation) - 1);

                    if (rotation == Tile.Rotation.NONE)
                        d_drills.addAndGet(2);
                    else
                        d_drills.addAndGet(1);
                }
            }
        });

        return drills.get()/2.0 + d_drills.get();
    }

    public int countRocketPower(int count) {
        AtomicInteger power = new AtomicInteger();
        AtomicInteger atomicCount = new AtomicInteger();
        atomicCount.set(count);

        Coordinate.forEach(c -> {
            if (get(c).getType() == Tile.Type.ROCKET && getRotation(c) == Tile.Rotation.NONE)
                power.getAndIncrement();
            else if(get(c).getType() == Tile.Type.D_ROCKET && atomicCount.get() > 0){
                power.addAndGet(2);
                atomicCount.addAndGet(-1);
            }
        });

        return power.get();
    }

    private boolean doesShieldsCover(Tile.Side side){
        AtomicBoolean covered = new AtomicBoolean();
        covered.set(false);

        Coordinate.forEachUntil(c -> {
            if(get(c).getType() == Tile.Type.SHIELD &&
                    Tile.shieldCoverage(getRotation(c)).contains(side)){
                covered.set(true);
                return false;
            }
            return true;
        });
        return covered.get();
    }

    private boolean doesDrillsCover(Tile.Side side, int where, boolean useBattery){
        AtomicBoolean cover = new AtomicBoolean();
        cover.set(false);

        Predicate<Coordinate> checkDrill = c -> {
            if (getRotation(c).toSide() == side &&
                    (get(c).getType() == Tile.Type.DRILLS ||
                            (useBattery && get(c).getType() == Tile.Type.D_DRILLS))) {
                cover.set(true);
                return false;
            }
            return true;
        };

        switch(side){
            case UP:
                Coordinate.forEachUntil(where, checkDrill);
                break;
            case DOWN:
                Coordinate.forEachUntil(where - 1, checkDrill);
                Coordinate.forEachUntil(where, checkDrill);
                Coordinate.forEachUntil(where + 1, checkDrill);
                break;
            case LEFT, RIGHT:
                Coordinate.forEachUntil(checkDrill, where - 1);
                Coordinate.forEachUntil(checkDrill, where);
                Coordinate.forEachUntil(checkDrill, where + 1);
                break;
        }

        return cover.get();
    }

    private Optional<Coordinate> whatWillHit(Projectile p){
        Coordinate c = null;
        switch(p.getSide()){
            case UP -> {
                for(int i = 0; i < BOARD_HEIGHT; i++){
                    if(Tile.real(board[p.getWhere()][i])){
                        c = new Coordinate(p.getWhere(), i);
                        break;
                    }
                }
            }
            case RIGHT -> {
                for(int i = BOARD_WIDTH-1; i >= 0; i--){
                    if(Tile.real(board[i][p.getWhere()])){
                        c = new Coordinate(i, p.getWhere());
                        break;
                    }
                }
            }
            case DOWN -> {
                for(int i = BOARD_HEIGHT-1; i >= 0; i--){
                    if(Tile.real(board[p.getWhere()][i])){
                        c = new Coordinate(p.getWhere(), i);
                        break;
                    }
                }
            }
            case LEFT -> {
                for(int i = 0; i < BOARD_WIDTH; i++){
                    if(Tile.real(board[i][p.getWhere()])){
                        c = new Coordinate(i, p.getWhere());
                        break;
                    }
                }
            }
        }

        return c == null? Optional.empty() : Optional.of(c);
    }

    public Optional<Coordinate> hit(Projectile p, boolean useBattery){
        boolean saved = false;
        Optional<Coordinate> pos = whatWillHit(p);

        if(pos.isEmpty())
            saved = true;
        else if(p.getType() == Projectile.Type.SMALL_ASTEROID && Tile.ConnectorType.SMOOTH == Tile.getSide(get(pos.get()), getRotation(pos.get()), p.getSide()))
            saved = true;
        else if(p.getType().stoppedBy() == Tile.Type.SHIELD && useBattery && doesShieldsCover(p.getSide()))
            saved = true;
        else if(p.getType().stoppedBy() == Tile.Type.DRILLS && doesDrillsCover(p.getSide(), p.getWhere(), useBattery))
            saved = true;

        if(!saved){
            remove(pos.get());
            return pos;
        }

        return Optional.empty();
    }
}

