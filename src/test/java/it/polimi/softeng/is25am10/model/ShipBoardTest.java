package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShipBoardTest {
    private ShipBoard shipBoard;

    @BeforeEach
    void initialize() {
        shipBoard = new ShipBoard();
    }

    @Test
    void testSetTile() {
        Tile tile = new Tile(TilesType.ROCKET, "otus");
        Result<Tile> result;

        result = shipBoard.setTile(3, 2, tile, 'n');
        assertFalse(result.isAccepted());
        assertEquals("occupied tile", result.getReason());

        result = shipBoard.setTile(0, 0, tile, 'n');
        assertFalse(result.isAccepted());
        assertEquals("cant place out of bound", result.getReason());

        result = shipBoard.setTile(2, 2, tile, 'n');
        assertTrue(result.isAccepted());
        assertEquals(TilesType.ROCKET, shipBoard.getTile(2, 2).getData().getType());

        result = shipBoard.setTile(0, 2, tile, 'n');
        assertFalse(result.isAccepted());
    }

    @Test
    void testGetTile() {
        Result<Tile> result;

        result = shipBoard.getTile(3, 2);
        assertTrue(result.isAccepted());
        assertEquals(TilesType.C_HOUSE, result.getData().getType());

        result = shipBoard.getTile(0, 0);
        assertFalse(result.isAccepted());

        result = shipBoard.getTile(0, 1);
        assertFalse(result.isAccepted());
    }

    @Test
    void testBook(){
        Tile tile1 = new Tile(TilesType.ROCKET, "otus");
        Tile tile2 = new Tile(TilesType.DRILLS, "otus");
        Tile tile3 = new Tile(TilesType.HOUSE, "otus");

        Result<Tile> result;

        result = shipBoard.bookTile(tile1);
        assertTrue(result.isAccepted());
        assertTrue(shipBoard.getBooked().contains(tile1));

        result = shipBoard.bookTile(tile1);
        assertFalse(result.isAccepted());
        assertEquals("already booked", result.getReason());

        result = shipBoard.bookTile(tile2);
        assertTrue(result.isAccepted());
        assertTrue(shipBoard.getBooked().contains(tile2));

        result = shipBoard.bookTile(tile3);
        assertFalse(result.isAccepted());
        assertEquals("booked tiles full", result.getReason());
    }

    @Test
    void testUseBookedTile(){
        Tile tile = new Tile(TilesType.ROCKET, "otus");
        Result<Tile> result;

        shipBoard.bookTile(tile);
        result = shipBoard.useBookedTile(tile, 'n',2, 2);

        assertTrue(result.isAccepted());
        assertFalse(shipBoard.getBooked().contains(tile));

        result = shipBoard.getTile(2, 2);
        assertTrue(result.isAccepted());
        assertEquals(TilesType.ROCKET, result.getData().getType());
    }

}