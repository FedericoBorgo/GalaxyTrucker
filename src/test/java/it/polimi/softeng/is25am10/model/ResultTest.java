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

    @Test
    void testIfPresent() {
        Result<String> result = Result.ok("ok");
        StringBuilder sb = new StringBuilder();
        result.ifPresent(sb::append);
        assertEquals("ok", sb.toString());

        Result<String> errorResult = Result.err("error");
        sb.setLength(0); // Clear the StringBuilder
        errorResult.ifPresent(sb::append);
        assertEquals("", sb.toString()); // Should not append anything
    }

    @Test
    void testIfNotPresent() {
        Result<String> result = Result.err("error");
        StringBuilder sb = new StringBuilder();
        result.ifNotPresent(() -> sb.append("not present"));
        assertEquals("not present", sb.toString());

        Result<String> okResult = Result.ok("ok");
        sb.setLength(0); // Clear the StringBuilder
        okResult.ifNotPresent(() -> sb.append("not present"));
        assertEquals("", sb.toString()); // Should not append anything
    }

    @Test
    void testUnwrap() {
        Result<String> result = Result.ok("ok");
        assertEquals("default message", result.unwrap("default message"));

        Result<String> errorResult = Result.err("error");
        assertEquals("error", errorResult.unwrap("default message"));
    }
}