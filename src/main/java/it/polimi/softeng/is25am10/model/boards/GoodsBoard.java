package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.TilesType;

import java.util.*;

/**
 * This class offers the capacity to change the number of goods held on a tile.
 * It manages all the 4 possible colors of blocks in a single class.
 */
public class GoodsBoard extends ElementsBoard{
    private static final List<TilesType> BLUE_BOX;
    private static final List<TilesType> RED_BOX;
    private static final List<TilesType> YELLOW_BOX;
    private static final List<TilesType> GREEN_BOX;
    private static final Map<TilesType, Integer> MAX_VALUE;

    private final List<TilesType> box;

    static {
        BLUE_BOX = new ArrayList<>();
        RED_BOX = new ArrayList<>();
        YELLOW_BOX = new ArrayList<>();
        GREEN_BOX = new ArrayList<>();
        MAX_VALUE = new HashMap<>();

        BLUE_BOX.add(TilesType.B_BOX_3);
        BLUE_BOX.add(TilesType.B_BOX_2);

        RED_BOX.add(TilesType.R_BOX_1);
        RED_BOX.add(TilesType.R_BOX_2);

        YELLOW_BOX.add(TilesType.B_BOX_3);
        YELLOW_BOX.add(TilesType.B_BOX_2);

        GREEN_BOX.add(TilesType.R_BOX_1);
        GREEN_BOX.add(TilesType.R_BOX_2);
        GREEN_BOX.add(TilesType.B_BOX_3);
        GREEN_BOX.add(TilesType.B_BOX_2);

        MAX_VALUE.put(TilesType.B_BOX_2, 2);
        MAX_VALUE.put(TilesType.B_BOX_3, 3);
        MAX_VALUE.put(TilesType.R_BOX_1, 1);
        MAX_VALUE.put(TilesType.R_BOX_2, 2);
    }

    public GoodsBoard(TilesBoard board, char color) { super(board);
        this.box = switch (color){
            case 'r' -> RED_BOX;
            case 'y' -> YELLOW_BOX;
            case 'b' -> BLUE_BOX;
            case 'g' -> GREEN_BOX;
            default -> null;
        };
    }

    // Returns the number of boxes of other colors on this tile
    private int getNBox(int x, int y){
        int total = 0;

        for(ElementsBoard b: other)
            total += b.get(x, y);
        return total;
    }

    /**
     * Places a specified quantity {@code qty} of goods at the given coordinates on the board.
     * The method verifies that the tile can hold goods of that type, and ensures the quantity
     * does not exceed the allowed limit of the tile by checking for the presence of other
     * goods on this tile through the {@code other} list.
     *
     * @param x the x-coordinate on the board where the entity is to be placed
     * @param y the y-coordinate on the board where the entity is to be placed
     * @param qty the quantity of the entity to be placed at the specified coordinates
     * @return a {@code Result} containing the updated number of goods at the specified location
     * if successful, or an error message if the placement fails
     */
    @Override
    public Result<Integer> put(int x, int y, int qty) {
        Result<Tile> res = board.getTile(x, y);

        // out of bounds or wall
        if(res.isErr())
            return Result.err(res.getReason());

        // checks if this is the right board (4 different boards for 4 different colors
        TilesType tile = res.getData().getType();
        if(!box.contains(tile))
            return Result.err("cant place here");

        // Checks if the boxes fit on the tile
        int sum = getNBox(x, y) + get(x, y) + qty;
        if(sum > MAX_VALUE.get(tile))
            return Result.err("too many boxes");

        set(x, y, get(x, y) + qty);
        return Result.ok(sum);
    }
}
