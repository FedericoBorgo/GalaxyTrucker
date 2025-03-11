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

        flightBoard.setRocketReady(FlightBoard.RocketPawn.RED);
        flightBoard.setRocketReady(FlightBoard.RocketPawn.BLUE);
        flightBoard.setRocketReady(FlightBoard.RocketPawn.GREEN);
        flightBoard.setRocketReady(FlightBoard.RocketPawn.YELLOW);
    }

    @Test
    void testSetRocketReady() {
        List<FlightBoard.RocketPawn> order = flightBoard.getOrder();
        List<Integer> offset = flightBoard.getOffset();

        assertEquals(FlightBoard.RocketPawn.RED, order.get(0));
        assertEquals(FlightBoard.RocketPawn.BLUE, order.get(1));
        assertEquals(FlightBoard.RocketPawn.GREEN, order.get(2));
        assertEquals(FlightBoard.RocketPawn.YELLOW, order.get(3));

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
        flightBoard.moveRocket(FlightBoard.RocketPawn.YELLOW, 4);
        assertEquals(Arrays.asList(FlightBoard.RocketPawn.YELLOW, FlightBoard.RocketPawn.RED,
                        FlightBoard.RocketPawn.BLUE, FlightBoard.RocketPawn.GREEN), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-1,-4,-6), flightBoard.getOffset());
        assertEquals(7, flightBoard.getLeaderPosition());

        // Y R B G
        flightBoard.moveRocket(FlightBoard.RocketPawn.YELLOW, 4);
        assertEquals(Arrays.asList(FlightBoard.RocketPawn.YELLOW, FlightBoard.RocketPawn.RED,
                        FlightBoard.RocketPawn.BLUE, FlightBoard.RocketPawn.GREEN), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-5,-8,-10), flightBoard.getOffset());
        assertEquals(11, flightBoard.getLeaderPosition());

        // Y R B G
        flightBoard.moveRocket(FlightBoard.RocketPawn.YELLOW, -4);
        assertEquals(Arrays.asList(FlightBoard.RocketPawn.YELLOW, FlightBoard.RocketPawn.RED,
                            FlightBoard.RocketPawn.BLUE, FlightBoard.RocketPawn.GREEN), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-1,-4,-6), flightBoard.getOffset());
        assertEquals(7, flightBoard.getLeaderPosition());

        // R Y B G
        flightBoard.moveRocket(FlightBoard.RocketPawn.YELLOW, -1);
        assertEquals(Arrays.asList(FlightBoard.RocketPawn.RED, FlightBoard.RocketPawn.YELLOW,
                FlightBoard.RocketPawn.BLUE, FlightBoard.RocketPawn.GREEN), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-1,-3,-5), flightBoard.getOffset());
        assertEquals(6, flightBoard.getLeaderPosition());

        // R B G Y
        flightBoard.moveRocket(FlightBoard.RocketPawn.YELLOW, -3);
        assertEquals(Arrays.asList(FlightBoard.RocketPawn.RED, FlightBoard.RocketPawn.BLUE,
                FlightBoard.RocketPawn.GREEN, FlightBoard.RocketPawn.YELLOW), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-3,-5,-6), flightBoard.getOffset());
        assertEquals(6, flightBoard.getLeaderPosition());
    }
}