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
    private int getNBox(Coordinate c){
        int total = 0;

        for(ElementsBoard b: other)
            total += b.get(c);
        return total;
    }

    @Override
    public boolean check(Coordinate c, int qty) {
        TilesType tile = board.getTile(c).getData().getType();
        return box.contains(tile) && (getNBox(c) + get(c) + qty) <= MAX_VALUE.get(tile);
    }
}
