package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic class used by all types of cards, it contains the necessary info about every card:
 * if it needs an input by the player (needInput) and some data (getData).
 * We also need to know the type of the card (Type) in some cases.
 */
public abstract class Card {

    protected final FlightBoard board;
    protected final Model model;

    // ID unique to every card, even if they share the same type they have different IDs
    public final int id;

    // Does the player need to declare something?
    public final boolean needInput;

    // When a player is ready or has declared their input, we can set the player
    // as "registered", which means that they have already given the input.
    protected final Map<FlightBoard.Pawn, Player> registered;

    public enum Type{
        EPIDEMIC, METEORS, PLANETS, AB_SHIP, OPEN_SPACE, STARDUST, STATION
    }

    protected final Type type;

    /**
     * Creates the card by giving the model from which to get player data, such as
     * the number of removed batteries, astronauts or goods.
     *
     * @param model where to get the player data
     * @param needInput does the player need to give some input?
     * @param board flight board of the game
     * @param id unique identification of the card
     * @param type of the card
     */
    public Card(Model model, boolean needInput, FlightBoard board, int id, Type type) {
        this.needInput = needInput;
        this.board = board;
        this.model = model;
        this.type = type;
        this.id = id;
        registered = new HashMap<>();
    }

    /**
     * Checks if the player has already set their input.
     * @param player to check
     * @return true if registered, false otherwise
     */
    public boolean isRegistered(Player player) {
        return registered.containsValue(player);
    }

    /**
     * Check if the given player is the one that should declare the next input.
     * @param player to check
     * @return true if he's him, false if not
     */
    protected boolean isCorrectOrder(Player player) {
        return registered.keySet().containsAll(board.getOrder().subList(0, board.getOrder().indexOf(player.getPawn())));
    }

    /**
     * Check if all the players gave their input
     * @return true if all players did, false otherwise
     */
    protected boolean allRegistered(){
        return registered.size() == board.getOrder().size();
    }

    /**
     * Register a player as having already given the input.
     * @param player to register
     */
    public void register(Player player){
        registered.put(player.getPawn(), player);
    }

    /**
     * Standardization of a player choice
     * @param json from player
     * @return player choice
     */
    protected boolean getChoice(JSONObject json){
        return json.getBoolean("choice");
    }

    /**
     * Give the player's input to the card. If the player does not need to give any input,
     * this method can still be used to give all the players to the card.
     *
     * @param player the player that execute the action
     * @param json this is dependent of every card
     * @return ok if the input is accepted, err if not
     */
    public abstract Result<JSONObject> set(Player player, JSONObject json);

    /**
     * Apply the actions of a card. It is dependent of the specific card.
     * @return ok if succeeded, err if not
     */
    public abstract Result<JSONObject> play();

    /**
     * Check if a card is ready to be played.
     * @return true if ready, false otherwise
     */
    public abstract boolean ready();

    /**
     * Get some specific data about the card.
     * @return the data
     */
    public abstract JSONObject getData();

    public static String dump(InputStream stream){
        try {
            return new String(stream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get registered players.
     * @return the already registered players.
     */
    public List<Player> getRegistered(){
        return new ArrayList<>(registered.values());
    }

    protected static JSONObject genAccepted(){
        JSONObject accepted = new JSONObject();
        accepted.put("accepted", true);
        return accepted;
    }
}
