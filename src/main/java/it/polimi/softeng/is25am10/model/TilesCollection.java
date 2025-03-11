package it.polimi.softeng.is25am10.model;

import java.util.*;

/**
 * Represents the entire set of tiles used for one game.
 * Stores the tiles in 2 lists: {@code tiles} for the face-down ones,
 * {@code seen} for the face-up ones.
 */
public class TilesCollection {
    private final List<Tile> tiles;
    private final List<Tile> seen;

    /**
     * Creates a deck of tiles for a match and shuffles it.
     */
    TilesCollection() {
        tiles = new ArrayList<>();
        seen = new ArrayList<>();

        tiles.addAll(Arrays.asList(PIPES));
        tiles.addAll(Arrays.asList(DRILLS));
        tiles.addAll(Arrays.asList(D_DRILLS));
        tiles.addAll(Arrays.asList(ROCKET));
        tiles.addAll(Arrays.asList(D_ROCKET));
        tiles.addAll(Arrays.asList(HOUSE));
        tiles.addAll(Arrays.asList(B_BOX_3));
        tiles.addAll(Arrays.asList(B_BOX_2));
        tiles.addAll(Arrays.asList(R_BOX_2));
        tiles.addAll(Arrays.asList(R_BOX_1));
        tiles.addAll(Arrays.asList(P_ADDON));
        tiles.addAll(Arrays.asList(B_ADDON));
        tiles.addAll(Arrays.asList(BATTERY_3));
        tiles.addAll(Arrays.asList(BATTERY_2));
        tiles.addAll(Arrays.asList(SHIELD));

        Collections.shuffle(tiles);
    }

    /**
     * Retrieves a new tile from the face-down tiles by taking the first one
     * in the {@code tiles} list and removing it from the list.
     * Checks if the list is empty and returns an {@code EMPTY_TILE} in that case.
     * @return tile at the head of the list or {@code EMPTY_TILE}
     */
    public Tile getNew(){
        if(tiles.isEmpty())
            return Tile.EMPTY_TILE;

        Tile tile = tiles.getFirst();
        tiles.removeFirst();

        return tile;
    }

    /**
     * Retrieves the list containing all the face-up tiles.
     * @return The seen list.
     */
    public List<Tile> getSeen() {
        return seen;
    }

    /**
     * Retrieves a single tile from the face-up tiles, removing it from the face-up tiles.
     * Uses remove method from the interface Collections.
     *
     * @param tile the tile to be retrieved from the seen list.
     * @return parameter {@code tile} or {@code EMPTY_LIST}.
     */
    public Tile getFromSeen(Tile tile){
        if(!seen.remove(tile))
            return Tile.EMPTY_TILE;

        return tile;
    }

    /**
     * Adds a tile to the face-up tiles.
     * @param tile the tile to be added to the seen list.
     */
    public void give(Tile tile){
        seen.add(tile);
    }

    /*
        Initialization of all the tiles:
        The sides are stored in a clockwise order.
        "north-east-south-west"
     */
    private static Tile[] PIPES = new Tile[]{
        new Tile(Tile.Type.PIPES, "uouo"),
        new Tile(Tile.Type.PIPES, "ouus"),
        new Tile(Tile.Type.PIPES, "usuo"),
        new Tile(Tile.Type.PIPES, "tuut"),
        new Tile(Tile.Type.PIPES, "utus"),
        new Tile(Tile.Type.PIPES, "suut"),
        new Tile(Tile.Type.PIPES, "uout"),
        new Tile(Tile.Type.PIPES, "tuuo")
    };

    // stored with the gun facing north
    private static Tile[] DRILLS = new Tile[]{
        new Tile(Tile.Type.DRILLS, "ssts"),
        new Tile(Tile.Type.DRILLS, "sost"),
        new Tile(Tile.Type.DRILLS, "stss"),
        new Tile(Tile.Type.DRILLS, "stss"),
        new Tile(Tile.Type.DRILLS, "ssto"),
        new Tile(Tile.Type.DRILLS, "stuo"),
        new Tile(Tile.Type.DRILLS, "sttt"),
        new Tile(Tile.Type.DRILLS, "stsu"),
        new Tile(Tile.Type.DRILLS, "sstu"),
        new Tile(Tile.Type.DRILLS, "ssst"),
        new Tile(Tile.Type.DRILLS, "stss"),
        new Tile(Tile.Type.DRILLS, "ssuo"),
        new Tile(Tile.Type.DRILLS, "suos"),
        new Tile(Tile.Type.DRILLS, "sots"),
        new Tile(Tile.Type.DRILLS, "ssos"),
        new Tile(Tile.Type.DRILLS, "sooo"),
        new Tile(Tile.Type.DRILLS, "suso"),
        new Tile(Tile.Type.DRILLS, "stus"),
        new Tile(Tile.Type.DRILLS, "ssst"),
        new Tile(Tile.Type.DRILLS, "ssut"),
        new Tile(Tile.Type.DRILLS, "sous"),
        new Tile(Tile.Type.DRILLS, "ssts"),
        new Tile(Tile.Type.DRILLS, "ssso"),
        new Tile(Tile.Type.DRILLS, "sout"),
        new Tile(Tile.Type.DRILLS, "ssos"),
    };

    // stored with the gun facing north
    private static Tile[] D_DRILLS = new Tile[]{
        new Tile(Tile.Type.D_ROCKET, "ssts"),
        new Tile(Tile.Type.D_ROCKET, "soto"),
        new Tile(Tile.Type.D_ROCKET, "sssu"),
        new Tile(Tile.Type.D_ROCKET, "suts"),
        new Tile(Tile.Type.D_ROCKET, "ssos"),
        new Tile(Tile.Type.D_ROCKET, "stso"),
        new Tile(Tile.Type.D_ROCKET, "suss"),
        new Tile(Tile.Type.D_ROCKET, "stot"),
        new Tile(Tile.Type.D_ROCKET, "ssou"),
        new Tile(Tile.Type.D_ROCKET, "sous"),
        new Tile(Tile.Type.D_ROCKET, "ssut"),
    };

    // stored with the propeller facing south
    private static Tile[] ROCKET = new Tile[]{
        new Tile(Tile.Type.ROCKET, "osss"),
        new Tile(Tile.Type.ROCKET, "osss"),
        new Tile(Tile.Type.ROCKET, "usso"),
        new Tile(Tile.Type.ROCKET, "utso"),
        new Tile(Tile.Type.ROCKET, "utss"),
        new Tile(Tile.Type.ROCKET, "sssu"),
        new Tile(Tile.Type.ROCKET, "ossu"),
        new Tile(Tile.Type.ROCKET, "tsss"),
        new Tile(Tile.Type.ROCKET, "suso"),
        new Tile(Tile.Type.ROCKET, "tsss"),
        new Tile(Tile.Type.ROCKET, "stsu"),
        new Tile(Tile.Type.ROCKET, "ooss"),
        new Tile(Tile.Type.ROCKET, "tsst"),
        new Tile(Tile.Type.ROCKET, "sssu"),
        new Tile(Tile.Type.ROCKET, "toso"),
        new Tile(Tile.Type.ROCKET, "uost"),
        new Tile(Tile.Type.ROCKET, "tuss"),
        new Tile(Tile.Type.ROCKET, "suss"),
        new Tile(Tile.Type.ROCKET, "suss"),
        new Tile(Tile.Type.ROCKET, "sost"),
        new Tile(Tile.Type.ROCKET, "otst")
    };

    // stored with the propeller facing south
    private static Tile[] D_ROCKET = new Tile[]{
        new Tile(Tile.Type.D_ROCKET, "uoss"),
        new Tile(Tile.Type.D_ROCKET, "ooso"),
        new Tile(Tile.Type.D_ROCKET, "tsss"),
        new Tile(Tile.Type.D_ROCKET, "ttst"),
        new Tile(Tile.Type.D_ROCKET, "usst"),
        new Tile(Tile.Type.D_ROCKET, "tssu"),
        new Tile(Tile.Type.D_ROCKET, "susu"),
        new Tile(Tile.Type.D_ROCKET, "osss"),
        new Tile(Tile.Type.D_ROCKET, "ouss"),
    };

    private static Tile[] HOUSE = new Tile[]{
        new Tile(Tile.Type.HOUSE, "soto"),
        new Tile(Tile.Type.HOUSE, "toto"),
        new Tile(Tile.Type.HOUSE, "stot"),
        new Tile(Tile.Type.HOUSE, "stus"),
        new Tile(Tile.Type.HOUSE, "tsut"),
        new Tile(Tile.Type.HOUSE, "stuo"),
        new Tile(Tile.Type.HOUSE, "souo"),
        new Tile(Tile.Type.HOUSE, "sout"),
        new Tile(Tile.Type.HOUSE, "ooto"),
        new Tile(Tile.Type.HOUSE, "stut"),
        new Tile(Tile.Type.HOUSE, "sttt"),
        new Tile(Tile.Type.HOUSE, "soou"),
        new Tile(Tile.Type.HOUSE, "toot"),
        new Tile(Tile.Type.HOUSE, "tsus"),
        new Tile(Tile.Type.HOUSE, "tsso"),
        new Tile(Tile.Type.HOUSE, "suso"),
        new Tile(Tile.Type.HOUSE, "uoss"),
    };

    private static Tile[] B_BOX_3 = new Tile[]{
        new Tile(Tile.Type.B_BOX_3, "oost"),
        new Tile(Tile.Type.B_BOX_3, "ssts"),
        new Tile(Tile.Type.B_BOX_3, "otts"),
        new Tile(Tile.Type.B_BOX_3, "soso"),
        new Tile(Tile.Type.B_BOX_3, "soss"),
        new Tile(Tile.Type.B_BOX_3, "tsts"),
    };

    private static Tile[] B_BOX_2 = new Tile[]{
        new Tile(Tile.Type.B_BOX_2, "osus"),
        new Tile(Tile.Type.B_BOX_2, "ssus"),
        new Tile(Tile.Type.B_BOX_2, "ssus"),
        new Tile(Tile.Type.B_BOX_2, "usts"),
        new Tile(Tile.Type.B_BOX_2, "sotu"),
        new Tile(Tile.Type.B_BOX_2, "usto"),
        new Tile(Tile.Type.B_BOX_2, "totu"),
        new Tile(Tile.Type.B_BOX_2, "otou"),
        new Tile(Tile.Type.B_BOX_2, "ussu"),
    };

    private static Tile[] R_BOX_2 = new Tile[]{
        new Tile(Tile.Type.R_BOX_2, "ssos"),
        new Tile(Tile.Type.R_BOX_2, "tsss"),
        new Tile(Tile.Type.R_BOX_2, "osto"),
    };

    private static Tile[] R_BOX_1 = new Tile[]{
        new Tile(Tile.Type.R_BOX_1, "stou"),
        new Tile(Tile.Type.R_BOX_1, "ooou"),
        new Tile(Tile.Type.R_BOX_1, "usus"),
        new Tile(Tile.Type.R_BOX_1, "ussu"),
        new Tile(Tile.Type.R_BOX_1, "osut"),
        new Tile(Tile.Type.R_BOX_1, "ttut"),
    };

    // Alien add-ons for purple aliens
    private static Tile[] P_ADDON = new Tile[]{
        new Tile(Tile.Type.P_ADDON, "tstt"),
        new Tile(Tile.Type.P_ADDON, "ssut"),
        new Tile(Tile.Type.P_ADDON, "sstu"),
        new Tile(Tile.Type.P_ADDON, "usss"),
        new Tile(Tile.Type.P_ADDON, "sosu"),
        new Tile(Tile.Type.P_ADDON, "otst"),
    };

    // Alien add-ons for brown aliens
    private static Tile[] B_ADDON = new Tile[]{
        new Tile(Tile.Type.B_ADDON, "sooo"),
        new Tile(Tile.Type.B_ADDON, "toso"),
        new Tile(Tile.Type.B_ADDON, "usso"),
        new Tile(Tile.Type.B_ADDON, "stsu"),
        new Tile(Tile.Type.B_ADDON, "ssuo"),
        new Tile(Tile.Type.B_ADDON, "ssus"),
    };

    // Power centers with 3 slots
    private static Tile[] BATTERY_3 = new Tile[]{
        new Tile(Tile.Type.BATTERY_3, "sost"),
        new Tile(Tile.Type.BATTERY_3, "toss"),
        new Tile(Tile.Type.BATTERY_3, "tsss"),
        new Tile(Tile.Type.BATTERY_3, "osss"),
        new Tile(Tile.Type.BATTERY_3, "stto"),
        new Tile(Tile.Type.BATTERY_3, "otso"),
    };

    // Power centers with 2 slots
    private static Tile[] BATTERY_2 = new Tile[]{
        new Tile(Tile.Type.BATTERY_2, "otsu"),
        new Tile(Tile.Type.BATTERY_2, "ussu"),
        new Tile(Tile.Type.BATTERY_2, "tosu"),
        new Tile(Tile.Type.BATTERY_2, "susu"),
        new Tile(Tile.Type.BATTERY_2, "uttt"),
        new Tile(Tile.Type.BATTERY_2, "ssus"),
        new Tile(Tile.Type.BATTERY_2, "ssus"),
        new Tile(Tile.Type.BATTERY_2, "utss"),
        new Tile(Tile.Type.BATTERY_2, "ouss"),
        new Tile(Tile.Type.BATTERY_2, "toto"),
        new Tile(Tile.Type.BATTERY_2, "uooo"),
    };

    // stored with the shield facing south-east
    private static Tile[] SHIELD = new Tile[]{
        new Tile(Tile.Type.SHIELD, "usso"),
        new Tile(Tile.Type.SHIELD, "osou"),
        new Tile(Tile.Type.SHIELD, "ooso"),
        new Tile(Tile.Type.SHIELD, "toto"),
        new Tile(Tile.Type.SHIELD, "tstt"),
        new Tile(Tile.Type.SHIELD, "ustt"),
        new Tile(Tile.Type.SHIELD, "tssu"),
        new Tile(Tile.Type.SHIELD, "otot"),
    };
}
