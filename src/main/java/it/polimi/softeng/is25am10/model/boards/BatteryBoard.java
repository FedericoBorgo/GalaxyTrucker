package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Tile;

import static com.googlecode.lanterna.TextColor.ANSI;

/**
 * This class offers the capacity to change the number of units held in a battery tile.
 * It checks if a tile can contain 2 o 3 batteries.
 */
public class BatteryBoard extends ElementsBoard {

    /**
     * Construct the battery container layout. It can only store
     * batteries.
     *
     * @param tiles board used to store the tiles, it will be used to check
     *              if a coordinate contains a battery container tile type.
     */
    public BatteryBoard(TilesBoard tiles) {
        super(tiles);
    }

    /**
     * Check if the batteries can be placed at the corresponding coordinate.
     * It checks if the container size is 3 or 2.
     * It counts the total amount of batteries in the tile. If exceed the
     * container limit does not accept the placement.
     *
     * @param c where to place the batteries
     * @param qty how many batteries
     * @return true if the batteries are playable, false if not.
     */
    @Override
    public boolean check(Coordinate c, int qty) {
        Tile t = tiles.getTile(c).getData();
        return Tile.battery(t) &&
                (qty + get(c) <= (t.getType() == Tile.Type.BATTERY_2 ? 2 : 3));
    }

    /**
     * Get the ansi color for the batteries. Used for terminal drawing.
     *
     * @return GREEN
     */
    @Override
    public ANSI getColor() {
        return ANSI.GREEN_BRIGHT;
    }
}
