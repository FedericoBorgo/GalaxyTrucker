package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateTest {
    @Test
    void testConstructor(){
        Coordinate c;

        try{
            // this is an invalid coordinate pair
            // should throw IndexOutOfBoundsException
            new Coordinate(-1,0);
            fail();
        }catch (IndexOutOfBoundsException _){}

        c = new Coordinate(3,2);
        assertEquals(3, c.x());
        assertEquals(2, c.y());
    }

    @Test
    void testNear(){
        Coordinate c = new Coordinate(3,2);

        try{
            // every near coordinate should exist
            // should not throw IOException
            assertEquals(new Coordinate(2, 2), c.left());
            assertEquals(new Coordinate(4, 2), c.right());
            assertEquals(new Coordinate(3, 1), c.up());
            assertEquals(new Coordinate(3, 3), c.down());
        }catch(IOException _){
            fail();
        }
    }

    @Test
    void testException(){
        try{
            // the left coordinate does not  exist
            // should throw IOException
            new Coordinate(0, 0).left();
            fail();
        }catch (IOException _){
        }
    }

    @Test
    void testFromString(){
        Result<Coordinate> result = Coordinate.fromString("x1y1");
        assertTrue(result.isOk());
        result = Coordinate.fromString("x1y9");
        assertTrue(result.isErr());
    }
}