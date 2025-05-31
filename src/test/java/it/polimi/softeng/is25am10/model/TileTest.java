package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {
    @Test
    void testTile(){
        Tile tile = new Tile(Tile.Type.ENGINE, "suto");

        assertEquals(Tile.Type.ENGINE, tile.getType());

        Map<Tile.Side, Tile.ConnectorType> connectors = tile.getConnectors();
        assertEquals(Tile.ConnectorType.SMOOTH, connectors.get(Tile.Side.UP));
        assertEquals(Tile.ConnectorType.UNIVERSAL, connectors.get(Tile.Side.RIGHT));
        assertEquals(Tile.ConnectorType.TWO_PIPE, connectors.get(Tile.Side.DOWN));
        assertEquals(Tile.ConnectorType.ONE_PIPE, connectors.get(Tile.Side.LEFT));

        assertEquals(Tile.Side.UP.getName(), "Sopra");
        for(Tile.Side side : Tile.Side.values()) {
            assertNotNull(side.getName());
            assertTrue(side.getName().length() > 0);
        }

        Tile.Rotation rot0 = Tile.Rotation.fromInt(0);
        assertEquals(Tile.Rotation.NONE, rot0);
        assertEquals(0, rot0.toInt());
        Tile.Rotation rot1 = Tile.Rotation.fromInt(1);
        assertEquals(Tile.Rotation.CLOCK, rot1);
        assertEquals(1, rot1.toInt());
        Tile.Rotation rot2 = Tile.Rotation.fromInt(2);
        assertEquals(Tile.Rotation.DOUBLE, rot2);
        assertEquals(2, rot2.toInt());
        Tile.Rotation rot3 = Tile.Rotation.fromInt(3);
        assertEquals(Tile.Rotation.INV, rot3);
        assertEquals(3, rot3.toInt());


        assertEquals(Tile.Side.UP, Tile.Rotation.NONE.toSide());
        assertEquals(Tile.Side.RIGHT, Tile.Rotation.CLOCK.toSide());
        assertEquals(Tile.Side.DOWN, Tile.Rotation.DOUBLE.toSide());
        assertEquals(Tile.Side.LEFT, Tile.Rotation.INV.toSide());

        Tile t = new Tile(Tile.Type.ENGINE, tile.getConnectors());
        assertEquals(tile, t);
        assertEquals("0321", tile.connectorsToInt());

        Tile.ConnectorType c = Tile.getSide(tile, Tile.Rotation.INV, Tile.Side.UP);
        assertEquals(Tile.ConnectorType.UNIVERSAL, c);
        c = Tile.getSide(tile, Tile.Rotation.CLOCK, Tile.Side.UP);
        assertEquals(Tile.ConnectorType.ONE_PIPE, c);
         c = Tile.getSide(tile, Tile.Rotation.DOUBLE, Tile.Side.UP);
        assertEquals(Tile.ConnectorType.TWO_PIPE, c);

        assertTrue(Tile.real(tile));
        Tile wall = new Tile(Tile.Type.WALL, "ssss");
        assertFalse(Tile.real(wall));
        assertTrue(Tile.engine(tile));
        assertFalse(Tile.engine(wall));
        assertFalse(Tile.cannon(tile));
        assertFalse(Tile.house(tile));
        assertFalse(Tile.addon(tile));
        assertFalse(Tile.battery(tile));
        assertFalse(Tile.box(tile));

        for(Tile.ConnectorType connectorType : connectors.values()) {
            if(connectorType == Tile.ConnectorType.SMOOTH) {
                assertEquals('s', connectorType.toChar());
            } else if (connectorType == Tile.ConnectorType.ONE_PIPE) {
                assertEquals('o', connectorType.toChar());
            } else if (connectorType == Tile.ConnectorType.TWO_PIPE) {
                assertEquals('t', connectorType.toChar());
            } else if (connectorType == Tile.ConnectorType.UNIVERSAL) {
                assertEquals('u', connectorType.toChar());
            } else {
                fail("Unexpected connector type: " + connectorType);
            }
        }

        assertFalse(connectors.get(Tile.Side.UP).isConnector());
        assertTrue(connectors.get(Tile.Side.DOWN).isConnector());
        assertTrue(Tile.ConnectorType.SMOOTH.connectable(Tile.ConnectorType.SMOOTH));
        assertTrue(Tile.ConnectorType.ONE_PIPE.connectable(Tile.ConnectorType.ONE_PIPE));
        assertTrue(Tile.ConnectorType.TWO_PIPE.connectable(Tile.ConnectorType.TWO_PIPE));
        assertFalse(Tile.ConnectorType.SMOOTH.connectable(Tile.ConnectorType.UNIVERSAL));
        assertTrue(Tile.ConnectorType.UNIVERSAL.connectable(Tile.ConnectorType.UNIVERSAL));
        assertFalse(Tile.ConnectorType.UNIVERSAL.connectable(Tile.ConnectorType.SMOOTH));
        assertTrue(Tile.ConnectorType.UNIVERSAL.connectable(Tile.ConnectorType.ONE_PIPE));;;
        assertTrue(Tile.ConnectorType.UNIVERSAL.connectable(Tile.ConnectorType.TWO_PIPE));
        assertFalse(Tile.ConnectorType.ONE_PIPE.connectable(Tile.ConnectorType.SMOOTH));
        assertFalse(Tile.ConnectorType.TWO_PIPE.connectable(Tile.ConnectorType.SMOOTH));

        assertFalse(Tile.ConnectorType.ONE_PIPE.connectable(Tile.ConnectorType.TWO_PIPE));
        assertEquals("ENGINE 0321",tile.toString());

        Tile shield = new Tile(Tile.Type.SHIELD, "ssss");
        assertEquals(Arrays.asList(Tile.Side.UP, Tile.Side.RIGHT), Tile.shieldCoverage(Tile.Rotation.NONE));
        assertEquals(Arrays.asList(Tile.Side.RIGHT, Tile.Side.DOWN), Tile.shieldCoverage(Tile.Rotation.CLOCK));
        assertEquals(Arrays.asList(Tile.Side.DOWN, Tile.Side.LEFT), Tile.shieldCoverage(Tile.Rotation.DOUBLE));
        assertEquals(Arrays.asList(Tile.Side.LEFT, Tile.Side.UP), Tile.shieldCoverage(Tile.Rotation.INV));
    }
}