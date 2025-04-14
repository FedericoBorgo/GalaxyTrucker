package it.polimi.softeng.is25am10.model.boards;

import com.googlecode.lanterna.TextColor;
import it.polimi.softeng.is25am10.model.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class offers the capacity to change the number of goods held on a tile.
 * It manages all the 4 possible colors of blocks in a single class.
 */
public class GoodsBoard extends ElementsBoard{
    private static final List<Tile.Type> BLUE_BOX;
    private static final List<Tile.Type> RED_BOX;
    private static final List<Tile.Type> YELLOW_BOX;
    private static final List<Tile.Type> GREEN_BOX;
    private static final Map<Tile.Type, Integer> MAX_VALUE;
    public final Type type;

    /**
     * Type of the box container layout.
     */
    public enum Type{
        BLUE, RED, YELLOW, GREEN;

        public TextColor.ANSI getColor(){
            return switch (this){
                case BLUE ->TextColor.ANSI.BLUE_BRIGHT;
                case RED -> TextColor.ANSI.RED_BRIGHT;
                case YELLOW -> TextColor.ANSI.YELLOW_BRIGHT;
                case GREEN -> TextColor.ANSI.GREEN_BRIGHT;
            };
        }

        public String getName(){
            return switch (this){
                case BLUE -> "Blu";
                case RED -> "Rosso";
                case YELLOW -> "Giallo";
                case GREEN -> "Verde";
            };
        }
    }

    /**
     * Corresponding color to this box container layout-
     */
    private final TextColor.ANSI color;

    private final List<Tile.Type> box;

    static {
        BLUE_BOX = new ArrayList<>();
        RED_BOX = new ArrayList<>();
        YELLOW_BOX = new ArrayList<>();
        GREEN_BOX = new ArrayList<>();
        MAX_VALUE = new HashMap<>();

        BLUE_BOX.add(Tile.Type.B_BOX_3);
        BLUE_BOX.add(Tile.Type.B_BOX_2);

        RED_BOX.add(Tile.Type.R_BOX_1);
        RED_BOX.add(Tile.Type.R_BOX_2);

        YELLOW_BOX.add(Tile.Type.B_BOX_3);
        YELLOW_BOX.add(Tile.Type.B_BOX_2);

        GREEN_BOX.add(Tile.Type.R_BOX_1);
        GREEN_BOX.add(Tile.Type.R_BOX_2);
        GREEN_BOX.add(Tile.Type.B_BOX_3);
        GREEN_BOX.add(Tile.Type.B_BOX_2);

        MAX_VALUE.put(Tile.Type.B_BOX_2, 2);
        MAX_VALUE.put(Tile.Type.B_BOX_3, 3);
        MAX_VALUE.put(Tile.Type.R_BOX_1, 1);
        MAX_VALUE.put(Tile.Type.R_BOX_2, 2);
    }

    public GoodsBoard(TilesBoard board, Type color) {
        super(board, true);
        type = color;
        this.box = switch (color){
            case RED -> RED_BOX;
            case YELLOW -> YELLOW_BOX;
            case BLUE -> BLUE_BOX;
            case GREEN -> GREEN_BOX;
        };
        this.color = switch(color){
            case BLUE -> TextColor.ANSI.BLUE;
            case RED -> TextColor.ANSI.RED;
            case YELLOW -> TextColor.ANSI.YELLOW;
            case GREEN -> TextColor.ANSI.GREEN;
        };
    }

    // Returns the number of boxes of other colors on this tile
    private int getNBox(Coordinate c){
        int total = 0;

        for(ElementsBoard b: other)
            total += b.get(c);
        return total;
    }

    /**
     * Check if a good is placeable in this box.
     * It checks if the color is correct and if there are enough
     * spaces inside this tile.
     *
     * @param c coordinate to place the box
     * @param qty how many box to place
     * @return true if the box is placeable here, false if not
     */
    @Override
    public boolean cantPlace(Coordinate c, int qty) {
        Tile.Type tile = tiles.getTile(c).getData().getType();
        return !box.contains(tile) || (getNBox(c) + get(c) + qty) > MAX_VALUE.get(tile);
    }

    @Override
    public TextColor.ANSI getColor() {
        return color;
    }
}
