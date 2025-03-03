package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FlightBoardTest {
    private FlightBoard flightBoard;

    @BeforeEach
    void initialize(){ flightBoard = new FlightBoard();}

    @Test
    void testSetRocketReady() {
        // success
        RocketPawn rocket1 = RocketPawn.YELLOW;
        RocketPawn rocket2 = RocketPawn.RED;
        RocketPawn rocket3 = RocketPawn.BLUE;

        Result<String> result1 = flightBoard.setRocketReady(rocket1);
        assertTrue(result1.isAccepted());
        assertEquals("Rocket YELLOW is ready", result1.getData());
        assertEquals(6, findRocketPosition(rocket1));

       Result<String> result2 = flightBoard.setRocketReady(rocket2);
       assertTrue(result2.isAccepted());
       assertEquals("Rocket RED is ready", result2.getData());
       assertEquals(3, findRocketPosition(rocket2));

       // duplicate
        Result<String> result3 = flightBoard.setRocketReady(rocket3);
        assertTrue(result3.isAccepted());

        Result<String> result4 = flightBoard.setRocketReady(rocket3);
        assertFalse(result4.isAccepted());
        assertEquals("Rocket is already on the board", result4.getReason());

    }

    @Test
    void testMoveRocket() {
        RocketPawn rocket1 = RocketPawn.YELLOW;
        RocketPawn rocket2 = RocketPawn.RED;

        flightBoard.setRocketReady(rocket1);

        //forward steps
        int preIndexY = findRocketPosition(rocket1);
        flightBoard.moveRocket(rocket1, 3);
        int postIndexY = findRocketPosition(rocket1);
        assertEquals((preIndexY + 3) % 24, postIndexY);

        //backwards steps
        flightBoard.moveRocket(rocket1, -2);
        int newIndexY = findRocketPosition(rocket1);
        assertEquals((postIndexY - 2 + 24) % 24, newIndexY);

        // circular array
        flightBoard.moveRocket(rocket1, 25);
        int newIndex = findRocketPosition(rocket1);
        assertEquals((newIndexY + 25) % 24, newIndex);

        // avoids occupied cells
        flightBoard.setRocketReady(rocket2);
        flightBoard.moveRocket(rocket2, 2);
        flightBoard.moveRocket(rocket1, 1);
        int newIndexYellow = findRocketPosition(rocket1);
        assertNotEquals(findRocketPosition(rocket2), newIndexYellow);

        //rocket not on board
        flightBoard.moveRocket(RocketPawn.BLUE, 5);
        assertEquals(-1, findRocketPosition(RocketPawn.BLUE));

    }

    @Test
    void testGetHourglassesPosition() {
        flightBoard.moveHourglasses();
        assertEquals(1, flightBoard.getHourglassesPosition());
        flightBoard.moveHourglasses();
        assertEquals(2,flightBoard.getHourglassesPosition());
        flightBoard.moveHourglasses();
        assertEquals(2,flightBoard.getHourglassesPosition());

    }

    @Test
    void testGetLaps(){
        RocketPawn rocket1 = RocketPawn.YELLOW;
        RocketPawn rocket2 = RocketPawn.RED;

        // rocket not on the board
        Result<Integer> result = flightBoard.getLaps(rocket1);
        assertFalse(result.isAccepted());
        assertNull(result.getData());
        assertEquals("Rocket is not on the board", result.getReason());

        // zero laps
        flightBoard.setRocketReady(rocket1);
        Result<Integer> result1 = flightBoard.getLaps(rocket1);
        assertTrue(result1.isAccepted());
        assertEquals(0, result1.getData());

        // multiple laps
        flightBoard.moveRocket(rocket1, 24);
        Result<Integer> result2 = flightBoard.getLaps(rocket1);
        assertTrue(result2.isAccepted());
        assertEquals(1, result2.getData());

        //multiple rockets
        flightBoard.setRocketReady(rocket2);
        flightBoard.moveRocket(rocket2, 25);

        Result<Integer> result3 = flightBoard.getLaps(rocket1);
        Result<Integer> result4 = flightBoard.getLaps(rocket2);
        assertTrue(result3.isAccepted());
        assertEquals(1, result3.getData());
        assertTrue(result4.isAccepted());
        assertEquals(1, result4.getData());

    }

    @Test
    void testGetPosition() {

        flightBoard.setRocketReady(RocketPawn.YELLOW);
        RocketPawn[] positions = flightBoard.getPositions();
        assertTrue(Arrays.asList(positions).contains(RocketPawn.YELLOW));

    }

    // to find the position of the rocket
    private int findRocketPosition(RocketPawn pawn) {
        RocketPawn[] positions = flightBoard.getPositions();
        for (int i = 0; i < positions.length; i++) {
            if (positions[i] == pawn) {
                return i;
            }
        }
        return -1; // if not found
    }
}