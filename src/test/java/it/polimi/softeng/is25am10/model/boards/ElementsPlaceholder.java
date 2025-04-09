package it.polimi.softeng.is25am10.model.boards;

import com.googlecode.lanterna.TextColor;
import it.polimi.softeng.is25am10.model.Tile;

/**
 * Just a placeholder
 */
class ElementsPlaceholder extends ElementsBoard {

    public ElementsPlaceholder(TilesBoard tiles) {
        super(tiles);
    }
    @Override
    public boolean check(Coordinate c, int qty) {
        return Tile.real(tiles.getTile(c).getData());
    }

    @Override
    public TextColor.ANSI getColor() {
        return null;
    }
}
