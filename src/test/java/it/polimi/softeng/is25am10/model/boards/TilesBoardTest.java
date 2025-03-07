package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.TilesType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TilesBoardTest {
    private TilesBoard tilesBoard;

    @BeforeEach
    void initialize() {
        tilesBoard = new TilesBoard();
    }

    @Test
    void testSetTile() {
        Tile tile = new Tile(TilesType.ROCKET, "otus");
        Result<Tile> result;

        result = tilesBoard.setTile(3, 2, tile, 0);
        assertTrue(result.isErr());
        assertEquals("occupied tile", result.getReason());

        result = tilesBoard.setTile(0, 0, tile, 0);
        assertTrue(result.isErr());
        assertEquals("cant place out of bound", result.getReason());

        result = tilesBoard.setTile(2, 2, tile, 0);
        assertTrue(result.isOk());
        assertEquals(TilesType.ROCKET, tilesBoard.getTile(2, 2).getData().getType());
        assertEquals(0, tilesBoard.getOri(2, 2).getData());

        result = tilesBoard.setTile(0, 2, tile, 0);
        assertTrue(result.isErr());
    }

    @Test
    void testGetTile() {
        Result<Tile> result;

        result = tilesBoard.getTile(3, 2);
        assertTrue(result.isOk());
        assertEquals(TilesType.C_HOUSE, result.getData().getType());

        result = tilesBoard.getTile(0, 0);
        assertTrue(result.isErr());

        result = tilesBoard.getTile(-1, 0);
        assertTrue(result.isErr());
    }

    @Test
    void testBook(){
        Tile tile1 = new Tile(TilesType.ROCKET, "otus");
        Tile tile2 = new Tile(TilesType.DRILLS, "otus");
        Tile tile3 = new Tile(TilesType.HOUSE, "otus");

        Result<Tile> result;

        result = tilesBoard.bookTile(tile1);
        assertTrue(result.isOk());
        assertTrue(tilesBoard.getBooked().contains(tile1));

        result = tilesBoard.bookTile(tile1);
        assertTrue(result.isErr());
        assertEquals("already booked", result.getReason());

        result = tilesBoard.bookTile(tile2);
        assertTrue(result.isOk());
        assertTrue(tilesBoard.getBooked().contains(tile2));

        result = tilesBoard.bookTile(tile3);
        assertTrue(result.isErr());
        assertEquals("booked tiles full", result.getReason());
    }

    @Test
    void testUseBookedTile(){
        Tile tile = new Tile(TilesType.ROCKET, "otus");
        Result<Tile> result;

        tilesBoard.bookTile(tile);
        result = tilesBoard.useBookedTile(tile, 0,2, 2);

        assertTrue(result.isOk());
        assertFalse(tilesBoard.getBooked().contains(tile));

        result = tilesBoard.getTile(2, 2);
        assertTrue(result.isOk());
        assertEquals(TilesType.ROCKET, result.getData().getType());
    }

    @Test
    void testIsOk(){
        tilesBoard.setTile(2, 2, new Tile(TilesType.HOUSE, "uuuu"), 0);
        assertTrue(tilesBoard.isOK());
        tilesBoard.setTile(1, 2, new Tile(TilesType.HOUSE, "suuu"), 1);
        assertFalse(tilesBoard.isOK());
        tilesBoard.remove(1, 2);
        tilesBoard.setTile(2, 1, new Tile(TilesType.HOUSE, "osss"), 2);
        assertTrue(tilesBoard.isOK());
        tilesBoard.remove(2, 1);
        tilesBoard.setTile(2, 1, new Tile(TilesType.HOUSE, "osss"), 1);
        assertFalse(tilesBoard.isOK());
    }

}