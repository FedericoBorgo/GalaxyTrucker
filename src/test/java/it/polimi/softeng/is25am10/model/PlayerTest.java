package it.polimi.softeng.is25am10.model;

import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PlayerTest {
    private String name = "TestPlayer1";
    Player player1 = new Player(name, FlightBoard.Pawn.RED);
    private String name2 = "TestPlayer2";
    Player player2 = new Player(name2, FlightBoard.Pawn.BLUE);

    @Test
    public void testPlayerCreation() {
        assert player1.getName().equals(name);
        assert player2.getName().equals(name2);
        assert player1.getPawn() == FlightBoard.Pawn.RED;
        assert player2.getPawn() == FlightBoard.Pawn.BLUE;
    }

    @Test
    public void testPlayerCash() {
        assert player1.getCash() == 0; // Default cash should be 0
        assert player2.getCash() == 0; // Default cash should be 0
    }

    @Test
    public void testPlayerBoard() {
        assert player1.getBoard() != null; // Board should be initialized
        assert player2.getBoard() != null; // Board should be initialized
        assert player1.getGoodsReward() != null;
        player1.setGoodsReward(List.of(new GoodsBoard.Type[]{GoodsBoard.Type.RED, GoodsBoard.Type.BLUE}));
        assert player1.placeReward(GoodsBoard.Type.RED, new Coordinate(0, 0)) != null;
        assert player1.placeReward(GoodsBoard.Type.GREEN, new Coordinate(0, 0)) != null;
        player2.giveCash(100);
        assert player2.getCash() == 100; // Cash should be updated correctly
        assert player1.equals(player1);
        assert !player1.equals(player2);
    }
}
