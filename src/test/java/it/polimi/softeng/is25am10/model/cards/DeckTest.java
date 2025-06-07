package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {

    Model testModel;
    FlightBoard testFlightBoard;

    @Test
    public void testDeck() {
        // Deck creation
        testModel = new Model(2, (model, type) -> {});
        testFlightBoard = new FlightBoard();
        Deck testDeck = new Deck(testModel, testFlightBoard);
        assertNotNull(testDeck, "Deck should not be null after creation");
        assertNotNull(testDeck.getVisible(), "Deck's internal card list should not be null");

        // Players creation
        Player player1 = new Player("Player1", FlightBoard.Pawn.YELLOW);
        Player player2 = new Player("Player2", FlightBoard.Pawn.BLUE);
        List<Player> players = new java.util.ArrayList<>();
        players.add(player1);
        players.add(player2);

        // Debug with space
        List<Card> testCards = new ArrayList<>();
        testCards.addAll(Space.construct(testModel, testFlightBoard));
        testDeck.debug_setCards(testCards);

        Card drawnCard = testDeck.draw(players);
        assertNotNull(drawnCard, "Drawn card should not be null");
        testDeck.getRegistered();

        testDeck.set(player1, CardInput.disconnected());
        boolean ready = testDeck.ready();
        CardData cardDataTest = testDeck.getData();
        cardDataTest.toString();
        testDeck.play();

        Deck testDeck2 = new Deck(testModel, testFlightBoard);
        assertFalse(testDeck2.equals(testDeck), "Decks with different cards should not be equal");

        // Debug stardust
        testCards.clear();
        testCards.addAll(Stardust.construct(testFlightBoard));
        testDeck.debug_setCards(testCards);
        testDeck.draw(players);

        testDeck.set(player1, CardInput.disconnected());
        testDeck.ready();
        cardDataTest = testDeck.getData();
        cardDataTest.toString();
        testDeck.play();

        // Debug station
        testCards.clear();
        testCards.addAll(Station.construct(testFlightBoard));
        testDeck.debug_setCards(testCards);
        testDeck.draw(players);

        testDeck.set(player1, CardInput.disconnected());
        testDeck.ready();
        cardDataTest = testDeck.getData();
        cardDataTest.toString();
        testDeck.play();

        // Debug planets
        testCards.clear();
        testCards.addAll(Planets.construct(testFlightBoard));
        testDeck.debug_setCards(testCards);
        testDeck.draw(players);
        cardDataTest = testDeck.getData();
        cardDataTest.toString();
        testDeck.set(player1, CardInput.disconnected());
        testDeck.ready();

        testDeck.play();

        // Debug meteors
        testCards.clear();
        testCards.addAll(Meteors.construct(testModel, testFlightBoard));
        testDeck.debug_setCards(testCards);
        testDeck.draw(players);

        testDeck.set(player1, CardInput.disconnected());
        testDeck.ready();
        cardDataTest = testDeck.getData();
        cardDataTest.toString();
        testDeck.play();

        // Debug epidemic
        testCards.clear();
        testCards.addAll(Epidemic.construct(testFlightBoard));
        testDeck.debug_setCards(testCards);
        testDeck.draw(players);

        testDeck.set(player1, CardInput.disconnected());
        testDeck.ready();
        cardDataTest = testDeck.getData();
        cardDataTest.toString();
        testDeck.play();

        // Debug abandonedShip
        testCards.clear();
        testCards.addAll(AbandonedShip.construct(testModel, testFlightBoard));
        testDeck.debug_setCards(testCards);
        testDeck.draw(players);

        testDeck.set(player1, CardInput.disconnected());
        testDeck.ready();
        cardDataTest = testDeck.getData();
        cardDataTest.toString();
        testDeck.play();

        // Debug pirates
        testCards.clear();
        testCards.addAll(Pirates.construct(testModel, testFlightBoard));
        testDeck.debug_setCards(testCards);
        drawnCard = testDeck.draw(players);
        Pirates piratesCard = (Pirates) drawnCard;
        piratesCard.rollDice();

        testDeck.set(player1, CardInput.disconnected());
        testDeck.ready();
        cardDataTest = testDeck.getData();
        cardDataTest.toString();
        testDeck.play();


        // Debug slavers
        testCards.clear();
        testCards.addAll(Slavers.construct(testModel, testFlightBoard));
        testDeck.debug_setCards(testCards);
        testDeck.draw(players);

        testDeck.set(player1, CardInput.disconnected());
        testDeck.ready();
        cardDataTest = testDeck.getData();
        cardDataTest.toString();
        testDeck.play();

        // Debug smugglers
        testCards.clear();
        testCards.addAll(Smugglers.construct(testModel, testFlightBoard));
        testDeck.debug_setCards(testCards);
        testDeck.draw(players);

        testDeck.set(player1, CardInput.disconnected());
        testDeck.ready();
        cardDataTest = testDeck.getData();
        cardDataTest.toString();
        testDeck.play();

        // Debug warzone
        testCards.clear();
        testCards.addAll(Warzone.construct(testModel, testFlightBoard));
        testDeck.debug_setCards(testCards);
        testDeck.draw(players);

        testDeck.set(player1, CardInput.disconnected());
        testDeck.ready();
        cardDataTest = testDeck.getData();
        cardDataTest.toString();
        testDeck.play();

        CardOutput cardOutput = new CardOutput();
        cardOutput.addDestroyed("test", new Coordinate(1, 1));
    }
}
