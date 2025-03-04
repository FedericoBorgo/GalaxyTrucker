package it.polimi.softeng.is25am10.model;

import java.util.*;

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

    public GoodsBoard(ShipBoard board, char color) { super(board);
        this.box = switch (color){
            case 'r' -> RED_BOX;
            case 'y' -> YELLOW_BOX;
            case 'b' -> BLUE_BOX;
            case 'g' -> GREEN_BOX;
            default -> null;
        };
    }

    private int getNBox(int x, int y){
        int total = 0;

        for(ElementsBoard b: other)
            total += b.get(x, y);
        return total;
    }

    @Override
    public Result<Integer> put(int x, int y, int qty) {
        Result<Tile> res = board.getTile(x, y);

        if(res.isErr())
            return Result.err(res.getReason());

        TilesType tile = res.getData().getType();

        if(!box.contains(tile))
            return Result.err("cant place here");

        int sum = getNBox(x, y) + get(x, y) + qty;

        if(sum > MAX_VALUE.get(tile))
            return Result.err("too many boxes");

        set(x, y, get(x, y) + qty);

        return Result.ok(sum);
    }
}
