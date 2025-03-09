package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;

class ElementsPlaceholder extends ElementsBoard {

    public ElementsPlaceholder(TilesBoard board) {
        super(board);
    }

    @Override
    public Result<Integer> put(int x, int y, int qty) {
        set(x, y, qty);
        return Result.ok(qty);
    }

    @Override
    public boolean check(int x, int y, int qty) {
        return Tile.real(board.getTile(x, y).getData());
    }
}
