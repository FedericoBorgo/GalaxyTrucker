package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {

    Model testModel;
    FlightBoard testFlightBoard;

    @Test
    public void testDeck() {
        // Initialize the model and flight board for testing
        testModel = new Model(2, (model, type) -> {});
        testFlightBoard = new FlightBoard();

        // Create a new Deck instance
        Deck testDeck = new Deck(testModel, testFlightBoard);


        // Assertions
        assertNotNull(testDeck, "Deck should not be null after creation");
        assertNotNull(testDeck.getVisible(), "Deck's internal card list should not be null");

        // test draw method
        Player player1 = new Player("Player1", FlightBoard.Pawn.YELLOW);
        Player player2 = new Player("Player2", FlightBoard.Pawn.BLUE);
        List<Player> players = new java.util.ArrayList<>();
        players.add(player1);
        players.add(player2);
        Card drawnCard = testDeck.draw(players);
        assertNotNull(drawnCard, "Drawn card should not be null");

        // test various method --> can be done better with debug_cards
        testDeck.set(player1, CardInput.disconnected());
        boolean ready = testDeck.ready();
        testDeck.getData();
        testDeck.getRegistered();
        testDeck.play();

        Deck testDeck2 = new Deck(testModel, testFlightBoard);
        assertFalse(testDeck2.equals(testDeck), "Decks with different cards should not be equal");
    }
}
