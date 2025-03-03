package it.polimi.softeng.is25am10.model;

public class Tile {
    public static final Tile WALL_TILE = new Tile(TilesType.WALL, "ssss");
    public static final Tile EMPY_TILE = new Tile(TilesType.EMPTY, "ssss");

    private TilesType tileType;
    private ConnectorType[] connectorType;

    // Constructor Method
    public Tile(TilesType tileType, String connectors) {
        this.tileType = tileType;
        this.connectorType = new ConnectorType[4];//we have 4 types of connector

        for (int i = 0; i < connectors.length(); i++) {
            this.connectorType[i] = ConnectorType.fromChar(connectors.charAt(i));
        }
    }

    // Return the tile's type
    public TilesType getType() {
        return tileType;
    }

    // Return the connector's type
    public ConnectorType[] getConnectorType() {
        return connectorType;
    }
}
