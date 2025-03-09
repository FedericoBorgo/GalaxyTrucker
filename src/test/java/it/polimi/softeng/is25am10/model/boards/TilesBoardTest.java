package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.TilesType;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

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
    void testDrillsRocket(){
        tilesBoard.setTile(3, 1, new Tile(TilesType.DRILLS, "sstu"), 3);
        tilesBoard.setTile(3, 3, new Tile(TilesType.ROCKET, "suss"), 3);

        tilesBoard.setTile(2, 2, new Tile(TilesType.PIPES, "tusu"), 0);
        tilesBoard.setTile(2, 1, new Tile(TilesType.D_DRILLS, "sssu"), 3);
        tilesBoard.setTile(2, 3, new Tile(TilesType.SHIELD, "ssou"), 0);
        tilesBoard.setTile(2, 4, new Tile(TilesType.ROCKET, "otst"), 0);

        tilesBoard.setTile(1, 2, new Tile(TilesType.BATTERY_2, "otot"), 0);
        tilesBoard.setTile(1, 3, new Tile(TilesType.D_ROCKET, "uoss"), 0);
        tilesBoard.setTile(1, 4, new Tile(TilesType.P_ADDON, "sttt"), 0);

        tilesBoard.setTile(0, 2, new Tile(TilesType.DRILLS, "stuo"), 0);
        tilesBoard.setTile(0, 3, new Tile(TilesType.B_BOX_2, "usos"), 0);
        tilesBoard.setTile(0, 4, new Tile(TilesType.PIPES, "ouou"), 0);

        Set<Pair<Integer, Integer>> result = tilesBoard.isOK();
        assertFalse(result.isEmpty());
        assertTrue(result.contains(new Pair<>(3, 1)));
        assertTrue(result.contains(new Pair<>(3, 3)));
        assertTrue(result.contains(new Pair<>(1, 3)));
    }

    @Test
    void testConnectors(){
        tilesBoard.setTile(3, 1, new Tile(TilesType.DRILLS, "stuo"), 0);
        tilesBoard.setTile(3, 3, new Tile(TilesType.PIPES, "tusu"), 0);

        tilesBoard.setTile(2, 2, new Tile(TilesType.B_BOX_2, "usos"), 0);
        tilesBoard.setTile(2, 3, new Tile(TilesType.ROCKET, "otst"), 0);

        tilesBoard.setTile(4, 1, new Tile(TilesType.PIPES, "uouo"), 0);
        tilesBoard.setTile(4, 2, new Tile(TilesType.P_ADDON, "tstt"), 0);
        tilesBoard.setTile(4, 3, new Tile(TilesType.DRILLS, "sstu"), 1);

        Set<Pair<Integer, Integer>> result = tilesBoard.isOK();
        assertFalse(result.isEmpty());
        assertTrue(result.contains(new Pair<>(3, 1)));
        assertTrue(result.contains(new Pair<>(2, 2)));
    }

    @Test
    void testUnreachable(){
        tilesBoard.setTile(2, 2, new Tile(TilesType.DRILLS, "suso"), 0);

        tilesBoard.setTile(1, 2, new Tile(TilesType.HOUSE, "ouos"), 0);
        tilesBoard.setTile(1, 1, new Tile(TilesType.DRILLS, "ssos"), 0);
        tilesBoard.setTile(1, 3, new Tile(TilesType.ROCKET, "osss"), 0);

        tilesBoard.setTile(3, 3, new Tile(TilesType.R_BOX_1, "tutt"), 0);

        tilesBoard.setTile(4, 2, new Tile(TilesType.BATTERY_2, "ssut"), 0);
        tilesBoard.setTile(4, 3, new Tile(TilesType.ROCKET, "ossu"), 0);

        tilesBoard.remove(2, 2);

        Set<Pair<Integer, Integer>> result = tilesBoard.isOK();
        assertFalse(result.isEmpty());
        assertTrue(result.contains(new Pair<>(-1, -1)));
    }

}