package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlienBoardTest {
    TilesBoard tiles;
    AlienBoard alien;
    List<ElementsBoard> other;

    @BeforeEach
    void setUp() {
        tiles = new TilesBoard();
        //yx0      1     2        3
        //1              HOUSE(a)
        //2 ENGINE ADDON HOUSE    C_HOUSE
        //3 HOUSE
        tiles.setTile(new Coordinate(2, 2), new Tile(Tile.Type.HOUSE, "uuuu"), Tile.Rotation.NONE);
        tiles.setTile(new Coordinate(2, 1), new Tile(Tile.Type.HOUSE, "uuuu"), Tile.Rotation.NONE);
        tiles.setTile(new Coordinate(1, 2), new Tile(Tile.Type.P_ADDON, "uuuu"), Tile.Rotation.NONE);
        tiles.setTile(new Coordinate(0, 2), new Tile(Tile.Type.ENGINE, "uuuu"), Tile.Rotation.NONE);
        tiles.setTile(new Coordinate(0, 3), new Tile(Tile.Type.HOUSE, "uuuu"), Tile.Rotation.NONE);

        // place an astronaut in the house at x2y1
        ElementsPlaceholder astronaut = new ElementsPlaceholder(tiles);
        astronaut.set(new Coordinate(2, 1), 1);
        other = new ArrayList<>();
        other.add(astronaut);

        // create the alien layer
        alien = new AlienBoard(tiles, AlienBoard.Type.PURPLE);
        alien.setOthers(other);
    }

    @Test
    void testPut(){
        // cant place an alien on top of an astronaut
        Result<Integer> res = alien.put(new Coordinate(2, 1), 1);
        assertTrue(res.isErr());

        // can be placed here, there is an addon
        // there is no other alien
        // there is no astronaut
        res = alien.put(new Coordinate(2, 2), 1);
        assertEquals(1, alien.get(new Coordinate(2, 2)));
        assertTrue(res.isOk());

        // there is already an alien there
        res = alien.put(new Coordinate(2, 2), 1);
        assertEquals(1, alien.get(new Coordinate(2, 2)));
        assertTrue(res.isErr());

        // this is not a house
        res = alien.put(new Coordinate(0, 2), 1);
        assertEquals(0, alien.get(new Coordinate(0, 2)));
        assertTrue(res.isErr());

        // there is no addon near here
        res = alien.put(new Coordinate(0, 3), 1);
        assertEquals(0, alien.get(new Coordinate(0, 3)));
        assertTrue(res.isErr());
    }
}