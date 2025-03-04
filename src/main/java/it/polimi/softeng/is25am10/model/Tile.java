package it.polimi.softeng.is25am10.model;

public class Tile {
    public static final Tile WALL_TILE = new Tile(TilesType.WALL, "ssss");
    public static final Tile EMPY_TILE = new Tile(TilesType.EMPTY, "ssss");

    private TilesType type;
    private ConnectorType[] connectors;

    // Constructor Method
    public Tile(TilesType type, String connectors) {
        this.type = type;
        this.connectors = new ConnectorType[4];//we have 4 types of connector

        for (int i = 0; i < connectors.length(); i++) {
            this.connectors[i] = ConnectorType.fromChar(connectors.charAt(i));
        }
    }

    // Return the tile's type
    public TilesType getType() {
        return type;
    }

    // Return the connector's type
    public ConnectorType[] getConnectors() {
        return connectors;
    }
}
