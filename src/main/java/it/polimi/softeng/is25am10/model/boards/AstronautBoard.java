package it.polimi.softeng.is25am10.model.boards;

import com.googlecode.lanterna.TextColor;
import it.polimi.softeng.is25am10.model.Tile;

import static com.googlecode.lanterna.TextColor.ANSI.WHITE;

/**
 * This class offers the capacity to change the number of astronauts held on a tile
 */
public class AstronautBoard extends ElementsBoard{
    public AstronautBoard(TilesBoard board) {
        super(board);
    }

    @Override
    public boolean check(Coordinate c, int qty) {
        return Tile.house(board.getTile(c).getData()) && (get(c) + qty <= 2);
    }

    @Override
    public TextColor.ANSI getColor() {
        return WHITE;
    }
}
