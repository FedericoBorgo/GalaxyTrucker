package it.polimi.softeng.is25am10.model;

/**
 * AWAITING REVISION
 */
public class AstronautBoard extends ElementsBoard{
    public AstronautBoard(ShipBoard board) {
        super(board);
    }

    /**
     * Places a specified quantity of astronauts at the given coordinates on the board.
     *
     * @param x the x-coordinate where the astronauts are to be placed
     * @param y the y-coordinate where the astronauts are to be placed
     * @param qty the number of astronauts to be placed
     * @return a {@code Result} containing the updated number of astronauts at the specified location if successful,
     *         or an error message if the placement astronauts fails
     */
    @Override
    public Result<Integer> put(int x, int y, int qty) {
        Result<Tile> resBoard = board.getTile(x, y);

        // out of bound
        if(resBoard.isErr())
            return Result.err(resBoard.getReason());

        TilesType type = resBoard.getData().getType();
        // not a house
        if(type != TilesType.HOUSE && type != TilesType.C_HOUSE)
            return Result.err("cant place here");

        // is an alien there?
        for(ElementsBoard b: other)
            if(b.get(x, y) > 0)
                return Result.err("occupied by alien");

        int astronauts = get(x, y) + qty;

        if(astronauts > 2)
            return Result.err("too many astronauts");

        set(x, y, astronauts);

        return Result.ok(astronauts);
    }
}
