package it.polimi.softeng.is25am10.model.boards;

import com.googlecode.lanterna.TextColor;
import it.polimi.softeng.is25am10.model.Tile;

import static com.googlecode.lanterna.TextColor.ANSI.WHITE;

/**
 * This class offers the capacity to change the number of astronauts held on a tile.
 * It tracks the position of every astronaut and their corresponding amount for each
 * tile.
 */
public class AstronautBoard extends ElementsBoard{

    /**
     * Create the astronaut container layer. It can only store aliens.
     *
     * @param tiles board used to store the tiles, it will be used to check if
     *              astronauts can be placed in the corresponding coordinate.
     */
    public AstronautBoard(TilesBoard tiles) {
        super(tiles);
    }

    /**
     * Check if astronauts can be placed to the corresponding coordinate.
     * It checks if the coordinate contains a house and if the total number
     * of astronauts in the house does not exceed 2.
     *
     * @param c where to place the astronauts
     * @param qty how many astronauts
     * @return true if the astronauts are placeable here or false if not-
     */
    @Override
    public boolean check(Coordinate c, int qty) {
        return Tile.house(tiles.getTile(c).getData()) && (get(c) + qty <= 2);
    }

    /**
     * Get the ANSI color for drawing the astronauts to the terminal.
     *
     * @return WHITE
     */
    @Override
    public TextColor.ANSI getColor() {
        return WHITE;
    }
}
