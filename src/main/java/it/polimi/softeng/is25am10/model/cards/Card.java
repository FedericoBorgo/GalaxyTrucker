package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Card {
    private final boolean needPlayerChoice;
    protected FlightBoard board;
    protected Map<FlightBoard.RocketPawn, Player> registered;
    public final int ID;

    public enum InputType {
        BOOLEAN,
        COORD_PAIR,
        PLANET
    }

    public Card(boolean needPlayerChoice, int id) {
        this.needPlayerChoice = needPlayerChoice;
        ID = id;
        registered = new HashMap<>();
    }

    public void setBoard(FlightBoard board) {
        this.board = board;
    }

    /**
     * Check if the player already set their input.
     *
     * @param player
     * @return
     */
    public boolean isRegistered(Player player) {
        return registered.containsValue(player);
    }

    /**
     * Check if the given player is the one that should declare the next
     * input.
     *
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

    public boolean needPlayerChoice() {
        return needPlayerChoice;
    }

    public abstract Result<Object> set(Player player, JSONObject json);

    public abstract Result<Object> play();

    public abstract boolean ready();

    public abstract JSONObject getData();
}
