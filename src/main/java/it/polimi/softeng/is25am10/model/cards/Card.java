package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

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

    public boolean isRegistered(Player player) {
        return registered.containsValue(player);
    }

    protected boolean isCorrectOrder(Player player) {
        return registered.keySet().containsAll(board.getOrder().subList(0, board.getOrder().indexOf(player.getPawn())));
    }

    protected boolean allRegistered(){
        return registered.size() == board.getOrder().size();
    }

    public void register(Player player){
        registered.put(player.getPawn(), player);
    }

    public boolean needPlayerChoice() {
        return needPlayerChoice;
    }

    public abstract Result<Object> set(Player player, JSONObject json);

    public abstract Result<Object> play();
}
