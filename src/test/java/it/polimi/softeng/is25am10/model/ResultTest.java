package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {
    @Test
    void testOk(){
        Result<String> result = Result.ok("ok");
        assertTrue(result.isOk());
        assertEquals("ok", result.getData());
    }

    @Test
    void testErr(){
        Result<String> result = Result.err("err");
        assertTrue(result.isErr());
        assertEquals("err", result.getReason());
    }

    @Test
    void testException(){
        Result<String> result = Result.err("err");
        assertThrowsExactly(NoSuchElementException.class, result::getData);
    }
}