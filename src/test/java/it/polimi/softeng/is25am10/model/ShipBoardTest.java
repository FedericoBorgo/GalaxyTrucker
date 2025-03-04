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
        assertTrue(result.isErr());
        assertEquals("occupied tile", result.getReason());

        result = shipBoard.setTile(0, 0, tile, 'n');
        assertTrue(result.isErr());
        assertEquals("cant place out of bound", result.getReason());

        result = shipBoard.setTile(2, 2, tile, 'n');
        assertTrue(result.isOk());
        assertEquals(TilesType.ROCKET, shipBoard.getTile(2, 2).getData().getType());
        assertEquals('n', shipBoard.getOri(2, 2).getData());

        result = shipBoard.setTile(0, 2, tile, 'n');
        assertTrue(result.isErr());
    }

    @Test
    void testGetTile() {
        Result<Tile> result;

        result = shipBoard.getTile(3, 2);
        assertTrue(result.isOk());
        assertEquals(TilesType.C_HOUSE, result.getData().getType());

        result = shipBoard.getTile(0, 0);
        assertTrue(result.isErr());

        result = shipBoard.getTile(-1, 0);
        assertTrue(result.isErr());
    }

    @Test
    void testBook(){
        Tile tile1 = new Tile(TilesType.ROCKET, "otus");
        Tile tile2 = new Tile(TilesType.DRILLS, "otus");
        Tile tile3 = new Tile(TilesType.HOUSE, "otus");

        Result<Tile> result;

        result = shipBoard.bookTile(tile1);
        assertTrue(result.isOk());
        assertTrue(shipBoard.getBooked().contains(tile1));

        result = shipBoard.bookTile(tile1);
        assertTrue(result.isErr());
        assertEquals("already booked", result.getReason());

        result = shipBoard.bookTile(tile2);
        assertTrue(result.isOk());
        assertTrue(shipBoard.getBooked().contains(tile2));

        result = shipBoard.bookTile(tile3);
        assertTrue(result.isErr());
        assertEquals("booked tiles full", result.getReason());
    }

    @Test
    void testUseBookedTile(){
        Tile tile = new Tile(TilesType.ROCKET, "otus");
        Result<Tile> result;

        shipBoard.bookTile(tile);
        result = shipBoard.useBookedTile(tile, 'n',2, 2);

        assertTrue(result.isOk());
        assertFalse(shipBoard.getBooked().contains(tile));

        result = shipBoard.getTile(2, 2);
        assertTrue(result.isOk());
        assertEquals(TilesType.ROCKET, result.getData().getType());
    }

}