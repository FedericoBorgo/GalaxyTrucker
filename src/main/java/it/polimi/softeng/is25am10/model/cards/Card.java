package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic class used by all types of cards, it contains the necessary info about every card:
 * if it needs an input by the player (needInput) and some data (getData).
 * We also need to know the type of the card (Type) in some cases.
 */

public abstract class Card implements Serializable {
    /**
     * Does the player need to declare something?
     */
    public final boolean needInput;

    protected final FlightBoard flight;
    protected final Model model;

    // ID unique to every card, even if they share the same type they have different IDs
    public final int id;

    // When a player is ready or has declared their input, we can set the player
    // as "registered", which means that they have already given the input.
    protected final Map<FlightBoard.Pawn, Player> registered = new HashMap<>();

    public enum Type{
        EPIDEMIC, METEORS, PLANETS, AB_SHIP, OPEN_SPACE, STARDUST, STATION, PIRATES, SMUGGLERS, SLAVERS, WAR_ZONE
    }

    protected final Type type;

    /**
     * Creates the card by giving the model from which to get player data, such as
     * the number of removed batteries, astronauts or goods.
     *
     * @param model where to get the player data
     * @param needInput does the player need to give some input?
     * @param flight flight board of the game
     * @param id unique identification of the card
     * @param type of the card
     */
    public Card(Model model, boolean needInput, FlightBoard flight, int id, Type type) {
        this.needInput = needInput;
        this.flight = flight;
        this.model = model;
        this.type = type;
        this.id = id;
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
    protected boolean unexpected(Player player) {
        return !registered.keySet().containsAll(flight.getOrder().subList(0, flight.getOrder().indexOf(player.getPawn())));
    }

    /**
     * Check if all the players gave their input
     * @return true if all players did, false otherwise
     */
    protected boolean allRegistered(){
        return registered.size() == flight.getOrder().size();
    }

    /**
     * Register a player as having already given the input.
     * @param player to register
     */
    public void register(Player player){
        registered.put(player.getPawn(), player);
    }

    /**
     * Give the player's input to the card. If the player does not need to give any input,
     * this method can still be used to give all the players to the card.
     *
     * @param player the player that execute the action
     * @param input this is dependent of every card
     * @return ok if the input is accepted, err if not
     */
    public abstract Result<CardInput> set(Player player, CardInput input);

    /**
     * Apply the actions of a card. It is dependent of the specific card.
     * @return ok if succeeded, err if not
     */
    public abstract Result<CardOutput> play();

    /**
     * Check if a card is ready to be played.
     * @return true if ready, false otherwise
     */
    public abstract boolean ready();

    /**
     * Get some specific data about the card.
     * @return the data
     */
    public abstract CardData getData();

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

    /**
     * Checks if two cards are equal.
     * @param o object to compare
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return needInput == card.needInput && id == card.id && type == card.type;
    }
}
