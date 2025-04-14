package it.polimi.softeng.is25am10.model;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a tile used within the game structure. A tile has a specific type
 * identified by a {@code TilesType} and an array of four connectors described
 * by {@code ConnectorType}s. The methods in this class reveal the nature of a tile: its type
 * and the connectors that it exposes.
 */

public class Tile implements Serializable {
    private final Type type;
    private final Map<Side, ConnectorType> connectors;

    public enum Side{
        UP, RIGHT, DOWN, LEFT;
        public static final Side[] order = {UP, RIGHT, DOWN, LEFT};

        public String getName(){
            return switch (this){
                case UP -> "Sopra";
                case RIGHT -> "Destra";
                case DOWN -> "Sotto";
                case LEFT -> "Sinistra";
            };
        }
    }

    public enum Rotation{
        CLOCK, INV, DOUBLE, NONE;

        public Side toSide(){
            return switch (this){
                case CLOCK -> Side.RIGHT;
                case INV -> Side.LEFT;
                case DOUBLE -> Side.DOWN;
                case NONE -> Side.UP;
            };
        }
    }

    /**
     * This class enumerates the types of tiles.
     */
    public enum Type {
        PIPES,
        CANNON,
        D_CANNON,
        ENGINE,
        D_ENGINE,
        HOUSE,
        C_HOUSE,
        B_BOX_3,
        B_BOX_2,
        R_BOX_2,
        R_BOX_1,
        P_ADDON,
        B_ADDON,
        BATTERY_3,
        BATTERY_2,
        SHIELD,
        EMPTY,
        WALL
    }

    /**
     * Constructs a Tile with a specified type and connector configuration.
     * It uses {@code ConnectorType.fromChar()} for the conversion from {@code String} to {@code ConnectorType}.
     *
     * @param type the type of the tile, represented by a {@code TilesType} enum.
     * @param connectors a string of length 4 representing the types of connectors on the tile.
     */
    public Tile(Type type, String connectors) {
        this.type = type;
        HashMap<Side, ConnectorType> map = new HashMap<>();

        Arrays.stream(Side.order).forEach(side -> map.put(side, ConnectorType.fromChar(connectors.charAt(side.ordinal()))));

        this.connectors = Collections.unmodifiableMap(map);
    }

    /**
     * Constructs a Tile with a specified type and connector configuration.
     * @param type the type of the tile, represented by a {@code TilesType} enum.
     * @param connectors a map of sides and connectorTypes
     */
    public Tile(Type type, Map<Side, ConnectorType> connectors) {
        this.type = type;
        this.connectors = Collections.unmodifiableMap(connectors);
    }

    /**
     * Retrieves the type of the tile.
     *
     * @return the {@code TilesType} representing the type of the tile.
     */
    public Type getType() {
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
     * @return  true if it's real false if its placeholder
     */
    static public boolean real(Tile t){
        return t.type != Type.WALL && t.type != Type.EMPTY;
    }

    /**
     * Check if a tile is an engine or a double engine
     * @param t the tile to check
     * @return true if it's an engine false otherwise
     */
    static public boolean engine(Tile t){
        return t.type == Type.ENGINE || t.type == Type.D_ENGINE;
    }

    /**
     * Check if a tile is a cannon or a double cannon
     * @param t the tile to check
     * @return true if it's a cannon false otherwise
     */
    static public boolean cannon(Tile t){
        return t.type == Type.CANNON || t.type == Type.D_CANNON;
    }

    /**
     * Check if a tile can contain members of the crew
     * @param t the tile to check
     * @return true if it can, false otherwise
     */
    static public boolean house(Tile t){
        return t.type == Type.HOUSE || t.type == Type.C_HOUSE;
    }

    /**
     * Check if a tile is an addon
     * @param t the tile to check
     * @return true if it can, false otherwise
     */
    static public boolean addon(Tile t){
        return t.type == Type.B_ADDON || t.type == Type.P_ADDON;
    }

    /**
     * Check if a tile can contain batteries
     * @param t the tile to check
     * @return true if it can, false otherwise
     */
    static public boolean battery(Tile t){
        return t.type == Type.BATTERY_2 || t.type == Type.BATTERY_3;
    }

    static public boolean box(Tile t){
        return t.type == Type.B_BOX_3 || t.type == Type.B_BOX_2 || t.type == Type.R_BOX_2 || t.type == Type.R_BOX_1;
    }

    public enum ConnectorType {
        ONE_PIPE,
        TWO_PIPE,
        UNIVERSAL,
        SMOOTH;

        /**
         * Converts a character into the corresponding {@code ConnectorType}.
         *
         * @param c the character to be converted, which represents a connector type.
         * @return the {@code ConnectorType} corresponding to the specified character,
         *         or {@code null} if the character does not match any valid connector type.
         */
        public static ConnectorType fromChar(char c) {
            return switch (c) {
                case 'o', '1' -> ONE_PIPE;
                case 't', '2' -> TWO_PIPE;
                case 'u', '3' -> UNIVERSAL;
                case 's', '0' -> SMOOTH;
                default -> throw new IllegalStateException("Unexpected ConnectorType "+ c);
            };
        }

        /**
         * Converts the current ConnectorType instance to its corresponding character.
         *
         * @return a character representing the ConnectorType:
         */
        public char toChar() {
            return switch (this) {
                case ONE_PIPE -> 'o';
                case TWO_PIPE -> 't';
                case UNIVERSAL -> 'u';
                case SMOOTH -> 's';
            };
        }

        /**
         * Check if two connector are compatible.
         *
         * @param other the other tile to be checked
         * @return true if they are connectable, false otherwise
         */
        public boolean connectable(ConnectorType other){
            if(this == SMOOTH){
                return other == SMOOTH;
            }

            if(other == SMOOTH){
                return false;
            }

            if(this == UNIVERSAL || other == UNIVERSAL){
                return true;
            }

            if(this == ONE_PIPE && other == ONE_PIPE){
                return true;
            }

            return this == TWO_PIPE && other == TWO_PIPE;
        }

        public boolean isConnector(){
            return this != SMOOTH;
        }
    }

    /**
     * Sides covered by the shield. Does not check if the tile is a shield tile.
     * @param rotation of the tile
     * @return the 2 sides that are covered by this rotation of the shield
     */
    static public List<Side> shieldCoverage(Rotation rotation){
        return switch (rotation){
            case NONE -> Arrays.asList(Side.UP, Side.RIGHT);
            case CLOCK -> Arrays.asList(Side.RIGHT, Side.DOWN);
            case DOUBLE -> Arrays.asList(Side.DOWN, Side.LEFT);
            case INV -> Arrays.asList(Side.LEFT, Side.UP);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return type == tile.type && Objects.equals(connectors, tile.connectors);
    }
}

