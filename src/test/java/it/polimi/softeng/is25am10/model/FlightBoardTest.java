package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlightBoardTest {
    private FlightBoard flightBoard;

    @BeforeEach
    void initialize(){ flightBoard = new FlightBoard();}

    @Test
    void testSetRocketReady() {

        Result<String> result = flightBoard.setRocketReady(RocketPawn.RED);
        assertTrue(result.isAccepted());
        assertEquals("Rocket RED is ready", result.getData());
        assertNull(result.getReason());

        for(int i = 0; i < 24; i++){
            flightBoard.setRocketReady(RocketPawn.RED);
        }
        Result<String> result2 = flightBoard.setRocketReady(RocketPawn.YELLOW);
        assertFalse(result2.isAccepted());
        assertNull(result2.getData());
        assertEquals("No available positions", result2.getReason());

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