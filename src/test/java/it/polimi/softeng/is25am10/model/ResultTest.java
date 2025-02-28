package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {
    @Test
    void resultTest(){
        Result<String> result = new Result<>(true, "test", "reason");
        assertNotNull(result);
        assertTrue(result.isAccepted());
        assertEquals("test", result.getData());
        assertEquals("reason", result.getReason());
    }
}