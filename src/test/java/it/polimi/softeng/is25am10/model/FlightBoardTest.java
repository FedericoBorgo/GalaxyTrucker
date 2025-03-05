package it.polimi.softeng.is25am10.model;

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

        flightBoard.setRocketReady(RocketPawn.RED);
        flightBoard.setRocketReady(RocketPawn.BLUE);
        flightBoard.setRocketReady(RocketPawn.GREEN);
        flightBoard.setRocketReady(RocketPawn.YELLOW);
    }

    @Test
    void testSetRocketReady() {
        List<RocketPawn> order = flightBoard.getOrder();
        List<Integer> offset = flightBoard.getOffset();

        assertEquals(RocketPawn.RED, order.get(0));
        assertEquals(RocketPawn.BLUE, order.get(1));
        assertEquals(RocketPawn.GREEN, order.get(2));
        assertEquals(RocketPawn.YELLOW, order.get(3));

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
        flightBoard.moveRocket(RocketPawn.YELLOW, 4);
        assertEquals(Arrays.asList(RocketPawn.YELLOW, RocketPawn.RED,
                        RocketPawn.BLUE,RocketPawn.GREEN), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-1,-4,-6), flightBoard.getOffset());
        assertEquals(7, flightBoard.getLeaderPosition());

        // Y R B G
        flightBoard.moveRocket(RocketPawn.YELLOW, 4);
        assertEquals(Arrays.asList(RocketPawn.YELLOW, RocketPawn.RED,
                        RocketPawn.BLUE, RocketPawn.GREEN), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-5,-8,-10), flightBoard.getOffset());
        assertEquals(11, flightBoard.getLeaderPosition());

        // Y R B G
        flightBoard.moveRocket(RocketPawn.YELLOW, -4);
        assertEquals(Arrays.asList(RocketPawn.YELLOW, RocketPawn.RED,
                            RocketPawn.BLUE, RocketPawn.GREEN), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-1,-4,-6), flightBoard.getOffset());
        assertEquals(7, flightBoard.getLeaderPosition());

        // R Y B G
        flightBoard.moveRocket(RocketPawn.YELLOW, -1);
        assertEquals(Arrays.asList(RocketPawn.RED, RocketPawn.YELLOW,
                RocketPawn.BLUE, RocketPawn.GREEN), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-1,-3,-5), flightBoard.getOffset());
        assertEquals(6, flightBoard.getLeaderPosition());

        // R B G Y
        flightBoard.moveRocket(RocketPawn.YELLOW, -3);
        assertEquals(Arrays.asList(RocketPawn.RED, RocketPawn.BLUE,
                RocketPawn.GREEN, RocketPawn.YELLOW), flightBoard.getOrder());
        assertEquals(Arrays.asList(0,-3,-5,-6), flightBoard.getOffset());
        assertEquals(6, flightBoard.getLeaderPosition());
    }
}