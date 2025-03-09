package it.polimi.softeng.is25am10.model;

import java.util.*;

/**
 * Represents a tile used within the game structure. A tile has a specific type
 * identified by a {@code TilesType} and an array of four connectors described
 * by {@code ConnectorType}s. The methods in this class reveal the nature of a tile: its type
 * and the connectors that it exposes.
 */
public class Tile {
    public enum Side{
        UP, RIGHT, DOWN, LEFT;
        public static final Side[] order = {UP, RIGHT, DOWN, LEFT};
    }

    public enum Rotation{
        CLOCK, INV, DOUBLE, NONE;
    }

    public static final Tile WALL_TILE = new Tile(TilesType.WALL, "ssss");
    public static final Tile EMPTY_TILE = new Tile(TilesType.EMPTY, "ssss");

    private final TilesType type;
    private final Map<Side, ConnectorType> connectors;

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
        HashMap<Side, ConnectorType> map = new HashMap<>();

        Arrays.stream(Side.order).forEach(side -> {
            map.put(side, ConnectorType.fromChar(connectors.charAt(side.ordinal())));
        });

        this.connectors = Collections.unmodifiableMap(map);
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
     * @return The map of {@code ConnectorType} representing the connectors of the tile.
     */
    public Map<Side, ConnectorType> getConnectors() {
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
    static public ConnectorType getSide(Tile t, Rotation rotation, Side side){
        HashMap<Side, ConnectorType> map = new HashMap<>(t.connectors);
        ConnectorType temp;

        if(rotation == Rotation.DOUBLE){
            temp = map.get(Side.UP);
            map.put(Side.UP, map.get(Side.DOWN));
            map.put(Side.DOWN, temp);
            temp = map.get(Side.LEFT);
            map.put(Side.LEFT, map.get(Side.RIGHT));
            map.put(Side.RIGHT, temp);
        } else if (rotation == Rotation.CLOCK) {
            temp = map.get(Side.UP);
            map.put(Side.UP, map.get(Side.LEFT));
            map.put(Side.LEFT, map.get(Side.DOWN));
            map.put(Side.DOWN, map.get(Side.RIGHT));
            map.put(Side.RIGHT, temp);
        } else if (rotation == Rotation.INV) {
            temp = map.get(Side.UP);
            map.put(Side.UP, map.get(Side.RIGHT));
            map.put(Side.RIGHT, map.get(Side.DOWN));
            map.put(Side.DOWN, map.get(Side.LEFT));
            map.put(Side.LEFT, temp);
        }

        return map.get(side);
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

    static public boolean house(Tile t){
        return t.type == TilesType.HOUSE || t.type == TilesType.C_HOUSE;
    }

    static public boolean battery(Tile t){
        return t.type == TilesType.BATTERY_2 || t.type == TilesType.BATTERY_3;
    }
}

