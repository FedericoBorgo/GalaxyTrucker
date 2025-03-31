package it.polimi.softeng.is25am10.model.boards;

import com.googlecode.lanterna.TextColor;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;

class ElementsPlaceholder extends ElementsBoard {

    public ElementsPlaceholder(TilesBoard board) {
        super(board);
    }
    @Override
    public boolean check(Coordinate c, int qty) {
        return Tile.real(board.getTile(c).getData());
    }

    @Override
    public TextColor.ANSI getColor() {
        return null;
    }
}
