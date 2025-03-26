package it.polimi.softeng.is25am10.model.boards;

import it.polimi.softeng.is25am10.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ShipBoardTest {
    ShipBoard board;

    @BeforeEach
    void setUp() {
        board = new ShipBoard();
    }

    @Test
    void testEpidemic(){
        TilesBoard tile = board.getTiles();
        tile.setTile(new Coordinate(2, 2), new Tile(Tile.Type.HOUSE, "ouos"), Tile.Rotation.NONE);
        tile.setTile(new Coordinate(2, 3), new Tile(Tile.Type.HOUSE, "usst"), Tile.Rotation.NONE);
        tile.setTile(new Coordinate(1, 3), new Tile(Tile.Type.P_ADDON, "stus"), Tile.Rotation.NONE);
        tile.setTile(new Coordinate(4, 2), new Tile(Tile.Type.ENGINE, "stsu"), Tile.Rotation.NONE);
        tile.setTile(new Coordinate(5, 2), new Tile(Tile.Type.HOUSE, "ottt"), Tile.Rotation.NONE);
        tile.setTile(new Coordinate(6, 2), new Tile(Tile.Type.HOUSE, "ssot"), Tile.Rotation.NONE);
        tile.setTile(new Coordinate(6, 3), new Tile(Tile.Type.HOUSE, "usoo"), Tile.Rotation.NONE);


        board.getPurple().put(new Coordinate(2, 3), 1);
        board.getAstronaut().put(new Coordinate(2, 2), 2);
        board.getAstronaut().put(new Coordinate(3, 2), 2);
        board.getAstronaut().put(new Coordinate(5, 2), 2);
        board.getAstronaut().put(new Coordinate(6, 2), 2);
        board.getAstronaut().put(new Coordinate(6, 3), 2);

        board.epidemic();

        assertEquals(0, board.getPurple().get(new Coordinate(2, 3)));
        assertEquals(1, board.getAstronaut().get(new Coordinate(3,2)));
        assertEquals(1, board.getAstronaut().get(new Coordinate(5,2)));
        assertEquals(1, board.getAstronaut().get(new Coordinate(6,2)));
        assertEquals(1, board.getAstronaut().get(new Coordinate(6,3)));
    }

    @Test
    void testInit(){
        TilesBoard tile = board.getTiles();
        tile.setTile(new Coordinate(2, 2), new Tile(Tile.Type.HOUSE, "ouos"), Tile.Rotation.NONE);
        tile.setTile(new Coordinate(2, 3), new Tile(Tile.Type.HOUSE, "usst"), Tile.Rotation.NONE);
        tile.setTile(new Coordinate(1, 3), new Tile(Tile.Type.P_ADDON, "stus"), Tile.Rotation.NONE);
        tile.setTile(new Coordinate(4, 2), new Tile(Tile.Type.ENGINE, "stsu"), Tile.Rotation.NONE);
        tile.setTile(new Coordinate(5, 2), new Tile(Tile.Type.HOUSE, "ottt"), Tile.Rotation.NONE);
        tile.setTile(new Coordinate(6, 2), new Tile(Tile.Type.HOUSE, "ssot"), Tile.Rotation.NONE);
        tile.setTile(new Coordinate(6, 3), new Tile(Tile.Type.HOUSE, "usoo"), Tile.Rotation.NONE);

        tile.setTile(new Coordinate(3, 3), new Tile(Tile.Type.BATTERY_3, "usss"), Tile.Rotation.NONE);

        board.init(Optional.of(new Coordinate(2, 3)), Optional.empty());

        assertEquals(1, board.getPurple().get(new Coordinate(2, 3)));
        assertEquals(2, board.getAstronaut().get(new Coordinate(2,2)));
        assertEquals(2, board.getAstronaut().get(new Coordinate(3,2)));
        assertEquals(2, board.getAstronaut().get(new Coordinate(5,2)));
        assertEquals(2, board.getAstronaut().get(new Coordinate(6,2)));
        assertEquals(2, board.getAstronaut().get(new Coordinate(6,3)));
    }

}