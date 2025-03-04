package it.polimi.softeng.is25am10.model;

import java.util.*;
import java.util.ArrayList;

public class AlienBoard extends ElementsBoard{
    private final TilesType type;

    public AlienBoard(ShipBoard board, char alienType) {
        super(board);
        this.type = alienType == 'p'? TilesType.P_ADDON : TilesType.B_ADDON;
    }

    private boolean thereIsAddon(int x, int y){
        List<Result<Tile>> tiles = new ArrayList<>();

        tiles.add(board.getTile(x-1, y));
        tiles.add(board.getTile(x+1, y));
        tiles.add(board.getTile(x, y-1));
        tiles.add(board.getTile(x, y+1));

        for(Result<Tile> result: tiles)
            if(result.isOk() && result.getData().getType() == type)
                return true;

        return false;
    }

    @Override
    public Result<Integer> put(int x, int y, int qty) {
        Result<Tile> res = board.getTile(x, y);

        // out of bound
        if(res.isErr())
            return Result.err(res.getReason());

        // not a house
        if(res.getData().getType() != TilesType.HOUSE)
            return Result.err("cant place in not a house");

        if(!thereIsAddon(x, y))
            return Result.err("there is not addon");

        // there is another alien or astronaut?
        for(ElementsBoard b: other)
            if(b.get(x, y) > 0)
                return Result.err("occupied");

        // there is already an alien? or too many alien
        if(get(x, y) > 0 || qty > 1)
            return Result.err("too many aliens");

        set(x, y, 1);
        return Result.ok(1);
    }
}
