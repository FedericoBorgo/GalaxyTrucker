package it.polimi.softeng.is25am10.model;

import java.util.*;
import java.util.ArrayList;

public class AlienBoard extends ElementsBoard{
    private final char alienType;

    public AlienBoard(ShipBoard board, char alienType) {
        super(board);
        this.alienType = alienType;
    }

    private boolean thereIsAddon(int x, int y){
        TilesType type = alienType == 'p'? TilesType.P_ADDON : TilesType.B_ADDON;
        List<Result<Tile>> tiles = new ArrayList<>();

        tiles.add(board.getTile(x-1, y));
        tiles.add(board.getTile(x+1, y));
        tiles.add(board.getTile(x, y-1));
        tiles.add(board.getTile(x, y+1));

        for(Result<Tile> result: tiles)
            if(result.isAccepted() && result.getData().getType() == type)
                return true;

        return false;
    }

    @Override
    public Result<Integer> put(int x, int y, int qty) {
        Result<Tile> resBoard = board.getTile(x, y);

        // out of bound
        if(!resBoard.isAccepted())
            return new Result<>(false, null, resBoard.getReason());

        TilesType type = resBoard.getData().getType();

        // not a house
        if(type != TilesType.HOUSE)
            return new Result<>(false, null, "cant place in not a house");

        if(!thereIsAddon(x, y))
            return new Result<>(false, null, "there is not addon");

        // there is an other alien or astronaut?
        for(ElementsBoard b: other)
            if(b.get(x, y) > 0)
                return new Result<>(false, null, "occupied");

        // there is already an alien? or too many alien
        if(get(x, y) > 0 || qty > 1)
            return new Result<>(false, null, "too many aliens");

        set(x, y, 1);
        return new Result<>(true, 1, null);
    }
}
