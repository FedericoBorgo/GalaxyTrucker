package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Projectile;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TilesBoardTest {
    private TilesBoard tilesBoard;

    @BeforeEach
    void initialize() {
        tilesBoard = new TilesBoard();
    }

    @Test
    void testSetTile() {
        Tile tile = new Tile(Tile.Type.ENGINE, "otus");
        Result<Tile> result;

        result = tilesBoard.setTile(new Coordinate(3, 2), tile, Tile.Rotation.NONE);
        assertTrue(result.isErr());
        assertEquals("occupied tile", result.getReason());

        result = tilesBoard.setTile(new Coordinate(0, 0), tile, Tile.Rotation.NONE);
        assertTrue(result.isErr());
        assertEquals("cant place out of bound", result.getReason());

        result = tilesBoard.setTile(new Coordinate(2, 2), tile, Tile.Rotation.NONE);
        assertTrue(result.isOk());
        assertEquals(Tile.Type.ENGINE, tilesBoard.getTile(new Coordinate(2, 2)).getData().getType());
        assertEquals(Tile.Rotation.NONE, tilesBoard.getRotation(new Coordinate(2, 2)));

        result = tilesBoard.setTile(new Coordinate(0, 2), tile, Tile.Rotation.NONE);
        assertTrue(result.isErr());
    }

    @Test
    void testGetTile() {
        Result<Tile> result;

        result = tilesBoard.getTile(new Coordinate(3, 2));
        assertTrue(result.isOk());
        assertEquals(Tile.Type.C_HOUSE, result.getData().getType());

        result = tilesBoard.getTile(new Coordinate(0, 0));
        assertTrue(result.isErr());
    }

    @Test
    void testBook(){
        Tile tile1 = new Tile(Tile.Type.ENGINE, "otus");
        Tile tile2 = new Tile(Tile.Type.CANNON, "otus");
        Tile tile3 = new Tile(Tile.Type.HOUSE, "otus");

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
        Tile tile = new Tile(Tile.Type.ENGINE, "otus");
        Result<Tile> result;

        tilesBoard.bookTile(tile);
        result = tilesBoard.useBookedTile(tile, Tile.Rotation.NONE,new Coordinate(2, 2));

        assertTrue(result.isOk());
        assertFalse(tilesBoard.getBooked().contains(tile));

        result = tilesBoard.getTile(new Coordinate(2, 2));
        assertTrue(result.isOk());
        assertEquals(Tile.Type.ENGINE, result.getData().getType());
    }

    @Test
    void testCannonEngine(){
        tilesBoard.setTile(new Coordinate(3, 1), new Tile(Tile.Type.CANNON, "sstu"), Tile.Rotation.INV);
        tilesBoard.setTile(new Coordinate(3, 3), new Tile(Tile.Type.ENGINE, "suss"), Tile.Rotation.INV);

        tilesBoard.setTile(new Coordinate(2, 2), new Tile(Tile.Type.PIPES, "tusu"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(2, 1), new Tile(Tile.Type.D_CANNON, "sssu"), Tile.Rotation.INV);
        tilesBoard.setTile(new Coordinate(2, 3), new Tile(Tile.Type.SHIELD, "ssou"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(2, 4), new Tile(Tile.Type.ENGINE, "otst"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(1, 2), new Tile(Tile.Type.BATTERY_2, "otot"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(1, 3), new Tile(Tile.Type.D_ENGINE, "uoss"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(1, 4), new Tile(Tile.Type.P_ADDON, "sttt"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(0, 2), new Tile(Tile.Type.CANNON, "stuo"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(0, 3), new Tile(Tile.Type.B_BOX_2, "usos"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(0, 4), new Tile(Tile.Type.PIPES, "ouou"), Tile.Rotation.NONE);

        Set<Coordinate> result = tilesBoard.isOK();
        assertFalse(result.isEmpty());
        assertEquals(3, result.size());
        assertTrue(result.contains(new Coordinate(3, 1)));
        assertTrue(result.contains(new Coordinate(3, 3)));
        assertTrue(result.contains(new Coordinate(1, 3)));
    }

    @Test
    void testConnectors(){
        tilesBoard.setTile(new Coordinate(3, 1), new Tile(Tile.Type.CANNON, "stuo"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(3, 3), new Tile(Tile.Type.PIPES, "tusu"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(2, 2), new Tile(Tile.Type.B_BOX_2, "usos"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(2, 3), new Tile(Tile.Type.ENGINE, "otst"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(4, 1), new Tile(Tile.Type.PIPES, "uouo"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(4, 2), new Tile(Tile.Type.P_ADDON, "tstt"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(4, 3), new Tile(Tile.Type.CANNON, "sstu"), Tile.Rotation.CLOCK);

        Set<Coordinate> result = tilesBoard.isOK();
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.contains(new Coordinate(3, 1)));
        assertTrue(result.contains(new Coordinate(2, 2)));
    }

    @Test
    void testUnreachable(){
        tilesBoard.setTile(new Coordinate(2, 2), new Tile(Tile.Type.CANNON, "suso"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(1, 2), new Tile(Tile.Type.HOUSE, "ouos"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(1, 1), new Tile(Tile.Type.CANNON, "ssos"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(1, 3), new Tile(Tile.Type.ENGINE, "osss"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(3, 3), new Tile(Tile.Type.R_BOX_1, "tutt"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(4, 2), new Tile(Tile.Type.BATTERY_2, "ssut"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(4, 3), new Tile(Tile.Type.ENGINE, "ossu"), Tile.Rotation.NONE);

        tilesBoard.remove(new Coordinate(2, 2));

        Set<Coordinate> result = tilesBoard.isOK();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.contains(new Coordinate(0, 0)));
    }

    @Test
    void checkExposedConnectors(){
        tilesBoard.setTile(new Coordinate(3, 1), new Tile(Tile.Type.CANNON, "sstu"), Tile.Rotation.INV);
        tilesBoard.setTile(new Coordinate(3, 3), new Tile(Tile.Type.ENGINE, "suss"), Tile.Rotation.INV);

        tilesBoard.setTile(new Coordinate(2, 2), new Tile(Tile.Type.PIPES, "tusu"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(2, 1), new Tile(Tile.Type.D_CANNON, "sssu"), Tile.Rotation.INV);
        tilesBoard.setTile(new Coordinate(2, 3), new Tile(Tile.Type.SHIELD, "ssou"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(2, 4), new Tile(Tile.Type.ENGINE, "otst"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(1, 2), new Tile(Tile.Type.BATTERY_2, "otot"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(1, 3), new Tile(Tile.Type.D_ENGINE, "uoss"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(1, 4), new Tile(Tile.Type.P_ADDON, "sttt"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(0, 2), new Tile(Tile.Type.CANNON, "stuo"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(0, 3), new Tile(Tile.Type.B_BOX_2, "usos"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(0, 4), new Tile(Tile.Type.PIPES, "ouou"), Tile.Rotation.NONE);

        assertEquals(8, tilesBoard.countExposedConnectors());
    }

    @Test
    void testCountCannon(){
        tilesBoard.setTile(new Coordinate(3, 1), new Tile(Tile.Type.CANNON, "sstu"), Tile.Rotation.INV);
        tilesBoard.setTile(new Coordinate(3, 3), new Tile(Tile.Type.ENGINE, "suss"), Tile.Rotation.INV);

        tilesBoard.setTile(new Coordinate(2, 2), new Tile(Tile.Type.PIPES, "tusu"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(2, 1), new Tile(Tile.Type.D_CANNON, "sssu"), Tile.Rotation.INV);
        tilesBoard.setTile(new Coordinate(2, 3), new Tile(Tile.Type.SHIELD, "ssou"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(2, 4), new Tile(Tile.Type.ENGINE, "otst"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(1, 2), new Tile(Tile.Type.BATTERY_2, "otot"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(1, 3), new Tile(Tile.Type.D_ENGINE, "uoss"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(1, 4), new Tile(Tile.Type.P_ADDON, "sttt"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(0, 2), new Tile(Tile.Type.CANNON, "stuo"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(0, 3), new Tile(Tile.Type.B_BOX_2, "usos"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(0, 4), new Tile(Tile.Type.PIPES, "ouou"), Tile.Rotation.NONE);

        Map<Tile.Rotation, Integer> battery = new HashMap<>();
        battery.put(Tile.Rotation.INV, 1);
        assertEquals(2.5, tilesBoard.countCannonsPower(battery));
    }

    @Test
    void testCountEngine(){
        tilesBoard.setTile(new Coordinate(3, 1), new Tile(Tile.Type.CANNON, "sstu"), Tile.Rotation.INV);
        tilesBoard.setTile(new Coordinate(3, 3), new Tile(Tile.Type.ENGINE, "suss"), Tile.Rotation.INV);

        tilesBoard.setTile(new Coordinate(2, 2), new Tile(Tile.Type.PIPES, "tusu"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(2, 1), new Tile(Tile.Type.D_CANNON, "sssu"), Tile.Rotation.INV);
        tilesBoard.setTile(new Coordinate(2, 3), new Tile(Tile.Type.SHIELD, "ssou"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(2, 4), new Tile(Tile.Type.ENGINE, "otst"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(1, 2), new Tile(Tile.Type.BATTERY_2, "otot"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(1, 3), new Tile(Tile.Type.D_ENGINE, "uoss"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(1, 4), new Tile(Tile.Type.P_ADDON, "sttt"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(0, 2), new Tile(Tile.Type.CANNON, "stuo"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(0, 3), new Tile(Tile.Type.B_BOX_2, "usos"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(0, 4), new Tile(Tile.Type.PIPES, "ouou"), Tile.Rotation.NONE);

        assertEquals(3, tilesBoard.countEnginePower(1));
    }


    void testHit(){
        tilesBoard.setTile(new Coordinate(3, 1), new Tile(Tile.Type.CANNON, "sstu"), Tile.Rotation.INV);
        tilesBoard.setTile(new Coordinate(3, 3), new Tile(Tile.Type.ENGINE, "suss"), Tile.Rotation.INV);

        tilesBoard.setTile(new Coordinate(2, 2), new Tile(Tile.Type.PIPES, "tusu"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(2, 1), new Tile(Tile.Type.D_CANNON, "sssu"), Tile.Rotation.INV);
        tilesBoard.setTile(new Coordinate(2, 3), new Tile(Tile.Type.SHIELD, "ssou"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(2, 4), new Tile(Tile.Type.ENGINE, "otst"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(1, 2), new Tile(Tile.Type.BATTERY_2, "otot"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(1, 3), new Tile(Tile.Type.D_ENGINE, "uoss"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(1, 4), new Tile(Tile.Type.P_ADDON, "sttt"), Tile.Rotation.NONE);

        tilesBoard.setTile(new Coordinate(0, 2), new Tile(Tile.Type.CANNON, "stuo"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(0, 3), new Tile(Tile.Type.B_BOX_2, "usos"), Tile.Rotation.NONE);
        tilesBoard.setTile(new Coordinate(0, 4), new Tile(Tile.Type.PIPES, "ouou"), Tile.Rotation.NONE);

        Optional<Coordinate> res;

        res = tilesBoard.hit(new Projectile(Projectile.Type.BIG_ASTEROID, Tile.Side.LEFT, 1, 0), true);
        assertTrue(res.isEmpty());
        res = tilesBoard.hit(new Projectile(Projectile.Type.BIG_FIRE, Tile.Side.LEFT, 1, 0), true);
        assertTrue(res.isPresent());
        assertEquals(new Coordinate(2, 1), res.get());
        res = tilesBoard.hit(new Projectile(Projectile.Type.SMALL_ASTEROID, Tile.Side.UP, 1, 0), true);
        assertTrue(res.isEmpty());
        res = tilesBoard.hit(new Projectile(Projectile.Type.SMALL_ASTEROID, Tile.Side.UP, 1, 0), false);
        assertTrue(res.isPresent());
        assertEquals(new Coordinate(1, 2), res.get());
    }
}