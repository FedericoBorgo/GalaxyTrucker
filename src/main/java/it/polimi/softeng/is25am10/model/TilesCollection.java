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
        new Tile(TilesType.PIPES, "uouo"),
        new Tile(TilesType.PIPES, "ouus"),
        new Tile(TilesType.PIPES, "usuo"),
        new Tile(TilesType.PIPES, "tuut"),
        new Tile(TilesType.PIPES, "utus"),
        new Tile(TilesType.PIPES, "suut"),
        new Tile(TilesType.PIPES, "uout"),
        new Tile(TilesType.PIPES, "tuuo")
    };

    // stored with the gun facing north
    private static Tile[] DRILLS = new Tile[]{
        new Tile(TilesType.DRILLS, "ssts"),
        new Tile(TilesType.DRILLS, "sost"),
        new Tile(TilesType.DRILLS, "stss"),
        new Tile(TilesType.DRILLS, "stss"),
        new Tile(TilesType.DRILLS, "ssto"),
        new Tile(TilesType.DRILLS, "stuo"),
        new Tile(TilesType.DRILLS, "sttt"),
        new Tile(TilesType.DRILLS, "stsu"),
        new Tile(TilesType.DRILLS, "sstu"),
        new Tile(TilesType.DRILLS, "ssst"),
        new Tile(TilesType.DRILLS, "stss"),
        new Tile(TilesType.DRILLS, "ssuo"),
        new Tile(TilesType.DRILLS, "suos"),
        new Tile(TilesType.DRILLS, "sots"),
        new Tile(TilesType.DRILLS, "ssos"),
        new Tile(TilesType.DRILLS, "sooo"),
        new Tile(TilesType.DRILLS, "suso"),
        new Tile(TilesType.DRILLS, "stus"),
        new Tile(TilesType.DRILLS, "ssst"),
        new Tile(TilesType.DRILLS, "ssut"),
        new Tile(TilesType.DRILLS, "sous"),
        new Tile(TilesType.DRILLS, "ssts"),
        new Tile(TilesType.DRILLS, "ssso"),
        new Tile(TilesType.DRILLS, "sout"),
        new Tile(TilesType.DRILLS, "ssos"),
    };

    // stored with the gun facing north
    private static Tile[] D_DRILLS = new Tile[]{
        new Tile(TilesType.D_ROCKET, "ssts"),
        new Tile(TilesType.D_ROCKET, "soto"),
        new Tile(TilesType.D_ROCKET, "sssu"),
        new Tile(TilesType.D_ROCKET, "suts"),
        new Tile(TilesType.D_ROCKET, "ssos"),
        new Tile(TilesType.D_ROCKET, "stso"),
        new Tile(TilesType.D_ROCKET, "suss"),
        new Tile(TilesType.D_ROCKET, "stot"),
        new Tile(TilesType.D_ROCKET, "ssou"),
        new Tile(TilesType.D_ROCKET, "sous"),
        new Tile(TilesType.D_ROCKET, "ssut"),
    };

    // stored with the propeller facing south
    private static Tile[] ROCKET = new Tile[]{
        new Tile(TilesType.ROCKET, "osss"),
        new Tile(TilesType.ROCKET, "osss"),
        new Tile(TilesType.ROCKET, "usso"),
        new Tile(TilesType.ROCKET, "utso"),
        new Tile(TilesType.ROCKET, "utss"),
        new Tile(TilesType.ROCKET, "sssu"),
        new Tile(TilesType.ROCKET, "ossu"),
        new Tile(TilesType.ROCKET, "tsss"),
        new Tile(TilesType.ROCKET, "suso"),
        new Tile(TilesType.ROCKET, "tsss"),
        new Tile(TilesType.ROCKET, "stsu"),
        new Tile(TilesType.ROCKET, "ooss"),
        new Tile(TilesType.ROCKET, "tsst"),
        new Tile(TilesType.ROCKET, "sssu"),
        new Tile(TilesType.ROCKET, "toso"),
        new Tile(TilesType.ROCKET, "uost"),
        new Tile(TilesType.ROCKET, "tuss"),
        new Tile(TilesType.ROCKET, "suss"),
        new Tile(TilesType.ROCKET, "suss"),
        new Tile(TilesType.ROCKET, "sost"),
        new Tile(TilesType.ROCKET, "otst")
    };

    // stored with the propeller facing south
    private static Tile[] D_ROCKET = new Tile[]{
        new Tile(TilesType.D_ROCKET, "uoss"),
        new Tile(TilesType.D_ROCKET, "ooso"),
        new Tile(TilesType.D_ROCKET, "tsss"),
        new Tile(TilesType.D_ROCKET, "ttst"),
        new Tile(TilesType.D_ROCKET, "usst"),
        new Tile(TilesType.D_ROCKET, "tssu"),
        new Tile(TilesType.D_ROCKET, "susu"),
        new Tile(TilesType.D_ROCKET, "osss"),
        new Tile(TilesType.D_ROCKET, "ouss"),
    };

    private static Tile[] HOUSE = new Tile[]{
        new Tile(TilesType.HOUSE, "sowo"),
        new Tile(TilesType.HOUSE, "toto"),
        new Tile(TilesType.HOUSE, "stot"),
        new Tile(TilesType.HOUSE, "stus"),
        new Tile(TilesType.HOUSE, "tsut"),
        new Tile(TilesType.HOUSE, "stuo"),
        new Tile(TilesType.HOUSE, "souo"),
        new Tile(TilesType.HOUSE, "sout"),
        new Tile(TilesType.HOUSE, "ooto"),
        new Tile(TilesType.HOUSE, "stut"),
        new Tile(TilesType.HOUSE, "sttt"),
        new Tile(TilesType.HOUSE, "soou"),
        new Tile(TilesType.HOUSE, "toot"),
        new Tile(TilesType.HOUSE, "tsus"),
        new Tile(TilesType.HOUSE, "tsso"),
        new Tile(TilesType.HOUSE, "suso"),
        new Tile(TilesType.HOUSE, "uoss"),
    };

    private static Tile[] B_BOX_3 = new Tile[]{
        new Tile(TilesType.B_BOX_3, "oost"),
        new Tile(TilesType.B_BOX_3, "ssts"),
        new Tile(TilesType.B_BOX_3, "otts"),
        new Tile(TilesType.B_BOX_3, "soso"),
        new Tile(TilesType.B_BOX_3, "soss"),
        new Tile(TilesType.B_BOX_3, "tsts"),
    };

    private static Tile[] B_BOX_2 = new Tile[]{
        new Tile(TilesType.B_BOX_2, "osus"),
        new Tile(TilesType.B_BOX_2, "ssus"),
        new Tile(TilesType.B_BOX_2, "ssus"),
        new Tile(TilesType.B_BOX_2, "usts"),
        new Tile(TilesType.B_BOX_2, "sotu"),
        new Tile(TilesType.B_BOX_2, "usto"),
        new Tile(TilesType.B_BOX_2, "totu"),
        new Tile(TilesType.B_BOX_2, "otou"),
        new Tile(TilesType.B_BOX_2, "ussu"),
    };

    private static Tile[] R_BOX_2 = new Tile[]{
        new Tile(TilesType.R_BOX_2, "ssos"),
        new Tile(TilesType.R_BOX_2, "tsss"),
        new Tile(TilesType.R_BOX_2, "osto"),
    };

    private static Tile[] R_BOX_1 = new Tile[]{
        new Tile(TilesType.R_BOX_1, "stou"),
        new Tile(TilesType.R_BOX_1, "ooou"),
        new Tile(TilesType.R_BOX_1, "usus"),
        new Tile(TilesType.R_BOX_1, "ussu"),
        new Tile(TilesType.R_BOX_1, "osut"),
        new Tile(TilesType.R_BOX_1, "ttut"),
    };

    // Alien add-ons for purple aliens
    private static Tile[] P_ADDON = new Tile[]{
        new Tile(TilesType.P_ADDON, "tstt"),
        new Tile(TilesType.P_ADDON, "ssut"),
        new Tile(TilesType.P_ADDON, "sstu"),
        new Tile(TilesType.P_ADDON, "usss"),
        new Tile(TilesType.P_ADDON, "sosu"),
        new Tile(TilesType.P_ADDON, "otst"),
    };

    // Alien add-ons for brown aliens
    private static Tile[] B_ADDON = new Tile[]{
        new Tile(TilesType.B_ADDON, "sooo"),
        new Tile(TilesType.B_ADDON, "toso"),
        new Tile(TilesType.B_ADDON, "usso"),
        new Tile(TilesType.B_ADDON, "stsu"),
        new Tile(TilesType.B_ADDON, "ssuo"),
        new Tile(TilesType.B_ADDON, "ssus"),
    };

    // Power centers with 3 slots
    private static Tile[] BATTERY_3 = new Tile[]{
        new Tile(TilesType.BATTERY_3, "sost"),
        new Tile(TilesType.BATTERY_3, "toss"),
        new Tile(TilesType.BATTERY_3, "tsss"),
        new Tile(TilesType.BATTERY_3, "osss"),
        new Tile(TilesType.BATTERY_3, "stto"),
        new Tile(TilesType.BATTERY_3, "otso"),
    };

    // Power centers with 2 slots
    private static Tile[] BATTERY_2 = new Tile[]{
        new Tile(TilesType.BATTERY_2, "otsu"),
        new Tile(TilesType.BATTERY_2, "ussu"),
        new Tile(TilesType.BATTERY_2, "tosu"),
        new Tile(TilesType.BATTERY_2, "susu"),
        new Tile(TilesType.BATTERY_2, "uttt"),
        new Tile(TilesType.BATTERY_2, "ssus"),
        new Tile(TilesType.BATTERY_2, "ssus"),
        new Tile(TilesType.BATTERY_2, "utss"),
        new Tile(TilesType.BATTERY_2, "ouss"),
        new Tile(TilesType.BATTERY_2, "toto"),
        new Tile(TilesType.BATTERY_2, "uooo"),
    };

    // stored with the shield facing south-east
    private static Tile[] SHIELD = new Tile[]{
        new Tile(TilesType.SHIELD, "usso"),
        new Tile(TilesType.SHIELD, "osou"),
        new Tile(TilesType.SHIELD, "ooso"),
        new Tile(TilesType.SHIELD, "toto"),
        new Tile(TilesType.SHIELD, "tstt"),
        new Tile(TilesType.SHIELD, "ustt"),
        new Tile(TilesType.SHIELD, "tssu"),
        new Tile(TilesType.SHIELD, "otot"),
    };
}
