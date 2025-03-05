package it.polimi.softeng.is25am10.model;

import java.util.*;
import java.util.ArrayList;

public class AlienBoard extends ElementsBoard{
    private final TilesType type;

    public AlienBoard(ShipBoard board, char alienType) {
        super(board);
        this.type = alienType == 'p'? TilesType.P_ADDON : TilesType.B_ADDON;
    }

    /**
     * Checks if there is an addon of the specified type in the tiles adjacent to the given coordinates.
     *
     * @param x the x-coordinate of the tile to check
     * @param y the y-coordinate of the tile to check
     * @return true if an adjacent tile contains an addon of the specified type, false otherwise
     */
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

    /**
     * Places an amount of aliens on the specified coordinates of the board if the placement conditions are met.
     *
     * @param x the x-coordinate on the board where the entity is to be placed
     * @param y the y-coordinate on the board where the entity is to be placed
     * @param qty the quantity of the entity to be placed at the specified coordinates
     * @return a {@code Result<Integer>} object indicating success (1) or failure with a relevant error message
     */
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

        // is there another alien or astronaut?
        for(ElementsBoard b: other)
            if(b.get(x, y) > 0)
                return Result.err("occupied");

        // is an alien already there? or, are there too many aliens?
        if(get(x, y) > 0 || qty > 1)
            return Result.err("too many aliens");

        set(x, y, 1);
        return Result.ok(1);
    }
}
