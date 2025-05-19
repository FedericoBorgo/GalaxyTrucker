package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;

import java.io.Serializable;
import java.util.*;

/**
 * Used to contains all the cards relative to a single game.
 * It has visible cards during the ship building (total 9)
 * and the 3 not visible.
 * The Card is played inside this class.
 * All the Card's method should be called by the Deck and not by the player.
 */
public class Deck extends Card implements Serializable {
    /// the list of the cards in a single game.
    private final List<Card> deck;
    /// the 9 visible cards during the building
    private final Card[][] visible;
    /// drawn card
    private Card selectedCard;

    private List<Player> players;
    private final FlightBoard flightBoard;

    /**
     * The constructor builds all the types of cards.
     * Then shuffles them, takes 12 of them and shuffles again.
     *
     * @param model model used by the cards to get removed items
     * @param board flight board to move the Pawns.
     */
    public Deck(Model model, FlightBoard board){
        super(null, false, null, -1, null);
        List<Card> cards = new ArrayList<>();
        deck = new ArrayList<>();
        visible = new Card[3][3];
        selectedCard = null;
        flightBoard = board;

        cards.addAll(Stardust.construct(board));
        cards.addAll(Epidemic.construct(board));
        cards.addAll(Space.construct(model, board));
        cards.addAll(Planets.construct(board));
        cards.addAll(AbandonedShip.construct(model, board));
        cards.addAll(Station.construct(board));
        cards.addAll(Meteors.construct(model, board));
        cards.addAll(Pirates.construct(model,board));
        cards.addAll(Slavers.construct(model,board));
        cards.addAll(Smugglers.construct(model,board));
        cards.addAll(Warzone.construct(model,board));

        Collections.shuffle(cards, new Random());

        // 9 visible cards
        for (int i = 0; i < visible.length; i++) {
            for (int j = 0; j < visible[i].length; j++) {
                visible[i][j] = cards.removeFirst();
                deck.add(visible[i][j]);
            }
        }

        // 3 more
        deck.add(cards.removeFirst());
        deck.add(cards.removeFirst());
        deck.add(cards.removeFirst());

        Collections.shuffle(deck);
    }

    /**
     * Get the visible cards during the ship building
     * @return 9 cards
     */
    public Card[][] getVisible() {
        return visible;
    }

    /**
     * Draw a card from a deck.
     * @param players the cards that do not require any input
     * automatically put the player in the ready status.
     * @return the drew card
     */
    public Card draw(List<Player> players){
        this.players = players;
        return selectedCard = deck.removeFirst();
    }

    /**
     * Plays the specific method from the subclass and checks the number of astronauts.
     * @return ok if its accepted, err if not
     */
    @Override
    public Result<CardOutput> play(){
        Result<CardOutput> res = selectedCard.play();

        if(res.isOk()){
            players.forEach(player -> {
                player.getBoard().removeIllegals();
               if(player.getBoard().getAstronaut().getTotal() == 0){
                   flightBoard.quit(player.getPawn());
               }
            });
        }

        return res;
    }

    /**
     * Forwards player input to the active card.
     * @param player the player that executes the action
     * @param input
     * @return
     */
    @Override
    public Result<CardInput> set(Player player, CardInput input){
        return selectedCard.set(player, input);
    }

    /**
     * Check if the card is ready to be played.
     * @return true if ready, false otherwise
     */
    @Override
    public boolean ready(){
        return selectedCard.ready();
    }

    /**
     * Get the data of the card.
     * @return the data
     */
    @Override
    public CardData getData(){
        if(selectedCard == null)
            return null;
        return selectedCard.getData();
    }

    /**
     * Get the registered players
     * @return the flight board
     */
    @Override
    public List<Player> getRegistered(){
        return selectedCard.getRegistered();
    }

    public void debug_setCards(List<Card> cards){
        deck.clear();
        deck.addAll(cards);
    }

    /**
     * The method ensures that two decks are considered equal only when they represent the exact
     * same game state: same cards, same visible cards, same selected card, same players, and same flight board.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Deck deck1 = (Deck) o;
        return Objects.equals(deck, deck1.deck) && Objects.deepEquals(visible, deck1.visible) && Objects.equals(selectedCard, deck1.selectedCard) && Objects.equals(players, deck1.players) && Objects.equals(flightBoard, deck1.flightBoard);
    }
}
