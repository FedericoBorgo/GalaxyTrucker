package it.polimi.softeng.is25am10.model.boards;

import com.googlecode.lanterna.TextColor;
import it.polimi.softeng.is25am10.gui.Building;
import it.polimi.softeng.is25am10.gui.Launcher;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class offers the capacity to change the number of aliens held on a tile.
 * The maximum number of aliens in a board is only 1.
 */
public class AlienBoard extends ElementsBoard{
    private final Tile.Type type;

    /**
     * The type of alien stored inside this layer.
     * The aliens can be Purple or brown.
     */
    public enum Type{
        PURPLE, BROWN
    }

    /**
     * Create the Alien Layer for a single type of alien.
     *
     * @param tiles tiles placement to check if the alien will be placed in
     *              their corresponding tile-coordinate.
     * @param type  of this alien layer.
     */
    public AlienBoard(TilesBoard tiles, Type type) {
        super(tiles);
        this.type = switch (type){
            case PURPLE -> Tile.Type.P_ADDON;
            case BROWN -> Tile.Type.B_ADDON;
        };
    }

    /**
     * Checks if there is an addon of the specified type in the tiles adjacent to the given coordinates.
     *
     * @param c central coordinate
     * @return true if an adjacent tile contains an addon of the specified type, false otherwise
     */
    public static boolean thereIsAddon(Coordinate c, TilesBoard tiles, Tile.Type type){
        List<Result<Tile>> adjacent = new ArrayList<>();

        // get the adjacent tile (if possible).
        // If an adjacent coordinate does not exist, IOException is thrown
        // and the tile is not added to the list of adjacent tiles
        try{adjacent.add(tiles.getTile(c.left()));}catch(IOException _){}
        try{adjacent.add(tiles.getTile(c.right()));}catch(IOException _){}
        try{adjacent.add(tiles.getTile(c.up()));}catch(IOException _){}
        try{adjacent.add(tiles.getTile(c.down()));}catch(IOException _){}


        // check if there is an addon near
        for(Result<Tile> result: adjacent)
            if(result.isOk() && result.getData().getType() == type)
                return true;

        return false;
    }

    /**
     * Check if the Alien can be placed at the corresponding coordinate.
     * It checks if there is an addon of the corresponding color near
     * and if there is only 1 alien of this color in the entire board.
     *
     * @param c the coordinate to check if the alien can be placed here.
     * @param qty how many aliens, only 1 can be placed per alien.
     * @return true if the alien is playable, false if not.
     */
    @Override
    public boolean cantPlace(Coordinate c, int qty) {
        return tiles.getTile(c).getData().getType() != Tile.Type.HOUSE
                || !thereIsAddon(c, tiles, type) || qty > 1 || (get(c) + qty > 1) || getTotal() + qty > 1;
    }

    /**
     * Get the ansi color of the corresponding alien, used for terminal drawing.
     * @return the ansi color in base of the alien type.
     */
    public TextColor.ANSI getColor(){
        return type == Tile.Type.B_ADDON ? TextColor.ANSI.YELLOW : TextColor.ANSI.MAGENTA;
    }

    public void placeAlien(Building b, Coordinate c, ImageView view){
        if(!cantPlace(c, 1)){
            b.dragSuccess.set(true);

            if(type == Tile.Type.P_ADDON){
                b.pAlienView.setVisible(false);
                b.purple = Result.ok(c);
                view.setImage(Launcher.getImage("/gui/textures/purple.png"));
            }
            else{
                b.bAlienView.setVisible(false);
                b.brown = Result.ok(c);
                view.setImage(Launcher.getImage("/gui/textures/brown.png"));
            }
        }
    }
}
