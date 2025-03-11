package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorTypeTest {

    @Test
    void connectorTypeTest(){
        assertEquals('o', Tile.ConnectorType.ONE_PIPE.toChar());
        assertEquals(Tile.ConnectorType.UNIVERSAL, Tile.ConnectorType.fromChar('u'));
    }

    @Test
    void testConnectable(){
        assertTrue(Tile.ConnectorType.SMOOTH.connectable(Tile.ConnectorType.SMOOTH));
        assertTrue(Tile.ConnectorType.UNIVERSAL.connectable(Tile.ConnectorType.UNIVERSAL));
        assertTrue(Tile.ConnectorType.ONE_PIPE.connectable(Tile.ConnectorType.ONE_PIPE));
        assertTrue(Tile.ConnectorType.TWO_PIPE.connectable(Tile.ConnectorType.TWO_PIPE));
        assertTrue(Tile.ConnectorType.UNIVERSAL.connectable(Tile.ConnectorType.ONE_PIPE));
        assertTrue(Tile.ConnectorType.UNIVERSAL.connectable(Tile.ConnectorType.TWO_PIPE));

        assertFalse(Tile.ConnectorType.ONE_PIPE.connectable(Tile.ConnectorType.SMOOTH));
        assertFalse(Tile.ConnectorType.TWO_PIPE.connectable(Tile.ConnectorType.SMOOTH));
        assertFalse(Tile.ConnectorType.UNIVERSAL.connectable(Tile.ConnectorType.SMOOTH));

        assertFalse(Tile.ConnectorType.ONE_PIPE.connectable(Tile.ConnectorType.TWO_PIPE));
    }
}