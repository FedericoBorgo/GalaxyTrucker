package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorTypeTest {

    @Test
    void connectorTypeTest(){
        assertEquals('o', ConnectorType.ONE_PIPE.toChar());
        assertEquals(ConnectorType.UNIVERSAL, ConnectorType.fromChar('u'));
    }

    @Test
    void testConnectable(){
        assertTrue(ConnectorType.SMOOTH.connectable(ConnectorType.SMOOTH));
        assertTrue(ConnectorType.UNIVERSAL.connectable(ConnectorType.UNIVERSAL));
        assertTrue(ConnectorType.ONE_PIPE.connectable(ConnectorType.ONE_PIPE));
        assertTrue(ConnectorType.TWO_PIPE.connectable(ConnectorType.TWO_PIPE));
        assertTrue(ConnectorType.UNIVERSAL.connectable(ConnectorType.ONE_PIPE));
        assertTrue(ConnectorType.UNIVERSAL.connectable(ConnectorType.TWO_PIPE));

        assertFalse(ConnectorType.ONE_PIPE.connectable(ConnectorType.SMOOTH));
        assertFalse(ConnectorType.TWO_PIPE.connectable(ConnectorType.SMOOTH));
        assertFalse(ConnectorType.UNIVERSAL.connectable(ConnectorType.SMOOTH));

        assertFalse(ConnectorType.ONE_PIPE.connectable(ConnectorType.TWO_PIPE));
    }
}