package it.polimi.softeng.is25am10.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a tile used within the game structure. A tile has a specific type
 * identified by a {@code TilesType} and an array of four connectors described
 * by {@code ConnectorType}s. The methods in this class reveal the nature of a tile: its type
 * and the connectors that it exposes.
 */
public class Tile {
    public static final Tile WALL_TILE = new Tile(TilesType.WALL, "ssss");
    public static final Tile EMPTY_TILE = new Tile(TilesType.EMPTY, "ssss");

    private TilesType type;
    private ConnectorType[] connectors;

    /**
     * Constructs a Tile with a specified type and connector configuration.
     *
     * @param type the type of the tile, represented by a {@code TilesType} enum.
     * @param connectors a string representing the types of connectors on the tile.
     * It is assumed to have a length of 4, with each character corresponding
     * to a specific {@code ConnectorType}.
     */
    public Tile(TilesType type, String connectors) {
        this.type = type;
        this.connectors = new ConnectorType[4]; //we have 4 types of connectors

        for (int i = 0; i < connectors.length(); i++) {
            this.connectors[i] = ConnectorType.fromChar(connectors.charAt(i));
        }
    }

    /**
     * Retrieves the type of the tile.
     *
     * @return the {@code TilesType} representing the type of the tile.
     */
    public TilesType getType() {
        return type;
    }

    /**
     * Retrieves the array of connector types associated with the tile.
     *
     * @return an array of {@code ConnectorType} representing the connectors of the tile.
     */
    public ConnectorType[] getConnectors() {
        return connectors;
    }

    /**
     * Get the side of a tile rotated clockwise n times.
     *
     * @param t the tile
     * @param rotation the number of rotation to make before getting the side
     * @param side the side to get
     * @return the rotated side
     */
    static public ConnectorType getSide(Tile t, int rotation, int side){
        List<ConnectorType> connectors = new ArrayList<>(Arrays.asList(t.getConnectors()));

        for(int i = 0; i < rotation; i++)
            connectors.addFirst(connectors.removeLast());

        return connectors.get(side);
    }

    /**
     * Check if a tile is not a placeholder.
     *
     * @param t the tile to check
     * @return  true if its real false if its placeholder
     */
    static public boolean real(Tile t){
        return t.type != TilesType.WALL && t.type != TilesType.EMPTY;
    }

    static public boolean rocket(Tile t){
        return t.type == TilesType.ROCKET || t.type == TilesType.D_ROCKET;
    }

    static public boolean drills(Tile t){
        return t.type == TilesType.DRILLS || t.type == TilesType.D_DRILLS;
    }
}

