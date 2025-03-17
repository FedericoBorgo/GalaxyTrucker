package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Used to contains all the cards relative to a single game.
 * It has visible cards during the ship building (total 9)
 * and the 3 not visible.
 * The Card is played inside this class.
 * All the Card's method should be called by the Deck and not by the player.
 */
public class Deck {
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
        List<Card> cards = new ArrayList<>();
        deck = new ArrayList<>();
        visible = new Card[3][3];
        selectedCard = null;
        flightBoard = board;

        //build all the cards type
        cards.addAll(Epidemic.construct(board));
        cards.addAll(Meteors.construct(board));
        cards.addAll(Planets.construct(board));
        cards.addAll(AbandonedShip.construct(model, board));
        cards.addAll(Space.construct(model, board));
        cards.addAll(Stardust.construct(board));
        cards.addAll(Station.construct(board));

        Collections.shuffle(cards);

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
        selectedCard = deck.removeFirst();
        this.players = players;

        if(selectedCard != null && !selectedCard.needInput){
            players.forEach(player -> {
               selectedCard.set(player, null);
            });
        }

        return selectedCard;
    }

    /**
     * Plays the specific method from the subclass and checks the number of astronauts.
     * @return
     */
    public Result<JSONObject> play(){
        Result<JSONObject> res = selectedCard.play();

        if(res.isOk()){
            players.forEach(player -> {
               if(player.getBoard().getAstronaut().getTotal() == 0){
                   flightBoard.quit(player.getPawn());
               }
            });
        }

        return res;
    }

    public Result<JSONObject> set(Player player, JSONObject json){
        return selectedCard.set(player, json);
    }

    public boolean ready(){
        return selectedCard.ready();
    }

    public JSONObject getData(){
        return selectedCard.getData();
    }

    public List<Player> getRegistered(){
        return selectedCard.getRegistered();
    }
}
