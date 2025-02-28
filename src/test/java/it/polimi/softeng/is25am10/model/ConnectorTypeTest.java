package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorTypeTest {

    @Test
    void connectorTypeTest(){
        ConnectorType t1 = ConnectorType.ONE_PIPE;
        ConnectorType t2 = ConnectorType.TWO_PIPE;
        assertNotNull(t1);
        assertEquals(t1, ConnectorType.ONE_PIPE);
        assertEquals(t2, ConnectorType.TWO_PIPE);
        assertNotEquals(t1, t2);
        assertEquals('o', t1.toChar());
        assertEquals(ConnectorType.UNIVERSAL, ConnectorType.fromChar('u'));
    }
}