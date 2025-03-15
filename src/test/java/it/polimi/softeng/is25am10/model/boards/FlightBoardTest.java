package it.polimi.softeng.is25am10.model.boards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class FlightBoardTest {
    FlightBoard flightBoard;

    @BeforeEach
    void setUp() {
        flightBoard = new FlightBoard();

        flightBoard.setRocketReady(FlightBoard.Pawn.RED);
        flightBoard.setRocketReady(FlightBoard.Pawn.BLUE);
        flightBoard.setRocketReady(FlightBoard.Pawn.GREEN);
        flightBoard.setRocketReady(FlightBoard.Pawn.YELLOW);
    }

    @Test
    void testSetRocketReady() {
        List<FlightBoard.Pawn> order = flightBoard.getOrder();
        List<Integer> offset = flightBoard.getOffset();

        assertEquals(FlightBoard.Pawn.RED, order.get(0));
        assertEquals(FlightBoard.Pawn.BLUE, order.get(1));
        assertEquals(FlightBoard.Pawn.GREEN, order.get(2));
        assertEquals(FlightBoard.Pawn.YELLOW, order.get(3));

        assertEquals(0, offset.get(0));
        assertEquals(-3, offset.get(1));
        assertEquals(-5, offset.get(2));
        assertEquals(-6, offset.get(3));

        assertEquals(4, offset.size());
        assertEquals(4, order.size());

        assertEquals(6, flightBoard.getLeaderPosition());
    }

    @Test
    void testMove(){

        // Y R B G
        flightBoard.moveRocket(FlightBoard.Pawn.YELLOW, 4);
        assertEquals(Arrays.asList(FlightBoard.Pawn.YELLOW, FlightBoard.Pawn.RED,
                        FlightBoard.Pawn.BLUE, FlightBoard.Pawn.GREEN), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-1,-4,-6), flightBoard.getOffset());
        assertEquals(7, flightBoard.getLeaderPosition());

        // Y R B G
        flightBoard.moveRocket(FlightBoard.Pawn.YELLOW, 4);
        assertEquals(Arrays.asList(FlightBoard.Pawn.YELLOW, FlightBoard.Pawn.RED,
                        FlightBoard.Pawn.BLUE, FlightBoard.Pawn.GREEN), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-5,-8,-10), flightBoard.getOffset());
        assertEquals(11, flightBoard.getLeaderPosition());

        // Y R B G
        flightBoard.moveRocket(FlightBoard.Pawn.YELLOW, -4);
        assertEquals(Arrays.asList(FlightBoard.Pawn.YELLOW, FlightBoard.Pawn.RED,
                            FlightBoard.Pawn.BLUE, FlightBoard.Pawn.GREEN), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-1,-4,-6), flightBoard.getOffset());
        assertEquals(7, flightBoard.getLeaderPosition());

        // R Y B G
        flightBoard.moveRocket(FlightBoard.Pawn.YELLOW, -1);
        assertEquals(Arrays.asList(FlightBoard.Pawn.RED, FlightBoard.Pawn.YELLOW,
                FlightBoard.Pawn.BLUE, FlightBoard.Pawn.GREEN), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-1,-3,-5), flightBoard.getOffset());
        assertEquals(6, flightBoard.getLeaderPosition());

        // R B G Y
        flightBoard.moveRocket(FlightBoard.Pawn.YELLOW, -3);
        assertEquals(Arrays.asList(FlightBoard.Pawn.RED, FlightBoard.Pawn.BLUE,
                FlightBoard.Pawn.GREEN, FlightBoard.Pawn.YELLOW), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-3,-5,-6), flightBoard.getOffset());
        assertEquals(6, flightBoard.getLeaderPosition());
    }

    @Test
    void testQuitByDupe(){
        flightBoard.moveRocket(FlightBoard.Pawn.RED, 19);
        List<FlightBoard.Pawn> quitters = flightBoard.getQuitters();
        assertTrue(quitters.contains(FlightBoard.Pawn.YELLOW));
        assertTrue(quitters.contains(FlightBoard.Pawn.GREEN));
        flightBoard.quit(FlightBoard.Pawn.RED);
        quitters = flightBoard.getQuitters();
        assertTrue(quitters.contains(FlightBoard.Pawn.RED));
        assertEquals(0, flightBoard.getOrder().indexOf(FlightBoard.Pawn.BLUE));
        assertEquals(0, flightBoard.getOffset().getFirst());
    }
}