package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorTypeTest {

    @Test
    void connectorTypeTest(){
        assertEquals('o', ConnectorType.ONE_PIPE.toChar());
        assertEquals(ConnectorType.UNIVERSAL, ConnectorType.fromChar('u'));
    }
}