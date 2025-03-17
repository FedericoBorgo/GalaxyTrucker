package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Generic class used by all types of space.json.
 * The necessary info about every card is if its
 * need an input by the player (needInput) and
 * some data (getData). We also need to know the
 * type of the card (Type) in some cases.
 */
public abstract class Card implements Serializable {
    /**
     * Does the player need to declare something?
     */
    public final boolean needInput;

    protected final FlightBoard board;

    /**
     * When a player is ready o has declared their input,
     * we can set the player as "registered" it means that
     * he has already given the input.
     */
    protected final Map<FlightBoard.Pawn, Player> registered;
    protected final Model model;

    /**
     * ID unique to every card.
     * Even same card type has different IDs.
     */
    public final int id;

    public enum Type{
        EPIDEMIC, METEOR, PLANETS, SHIP, SPACE, STARDUST, STATION;
    }

    protected final Type type;

    /**
     * Create the card by giving the model to get player data, such as
     * the number of removed battery, astronaut or goods.
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
     * Check if the player already set their input.
     * @param player
     * @return
     */
    public boolean isRegistered(Player player) {
        return registered.containsValue(player);
    }

    /**
     * Check if the given player is the one that should declare the next
     * input.
     * @param player
     * @return true if he's him, false if not
     */
    protected boolean isCorrectOrder(Player player) {
        return registered.keySet().containsAll(board.getOrder().subList(0, board.getOrder().indexOf(player.getPawn())));
    }

    /**
     * Check if all the player gave their input
     * @return
     */
    protected boolean allRegistered(){
        return registered.size() == board.getOrder().size();
    }

    /**
     * Register a player as already given the input.
     * @param player
     */
    public void register(Player player){
        registered.put(player.getPawn(), player);
    }

    /**
     * Standardisation of a player choice.
     * @param json
     * @return
     */
    protected boolean getChoice(JSONObject json){
        return json.getBoolean("choice");
    }

    /**
     * Give the player input to the card. If the player does not have
     * to give any input, someone still need to give the card all
     * the players using this method.
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
     * @return
     */
    public abstract boolean ready();

    /**
     * Get some specific data about the card.
     * @return
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return needInput == card.needInput && id == card.id && type == card.type;
    }
}
