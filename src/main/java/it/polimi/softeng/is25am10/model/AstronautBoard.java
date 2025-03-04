package it.polimi.softeng.is25am10.model;

public class AstronautBoard extends ElementsBoard{
    public AstronautBoard(ShipBoard board) {
        super(board);
    }

    @Override
    public Result<Integer> put(int x, int y, int qty) {
        Result<Tile> resBoard = board.getTile(x, y);

        // out of bound
        if(!resBoard.isOk())
            return new Result<>(false, null, resBoard.getReason());

        TilesType type = resBoard.getData().getType();

        // not a house
        if(type != TilesType.HOUSE && type != TilesType.C_HOUSE)
            return new Result<>(false, null, "cant place here");

        // there is an alien?
        for(ElementsBoard b: other)
            if(b.get(x, y) > 0)
                return new Result<>(false, null, "occupied by alien");

        int astronauts = get(x, y) + qty;

        if(astronauts > 2)
            return new Result<>(false, null, "too many astronauts");

        set(x, y, astronauts);

        return new Result<>(true, astronauts, null);
    }
}
