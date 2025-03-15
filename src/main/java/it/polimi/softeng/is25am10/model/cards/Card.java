package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Card {
    private final boolean needPlayerChoice;
    protected final FlightBoard board;
    protected final Map<FlightBoard.RocketPawn, Player> registered;
    protected final Model model;
    public final int ID;

    public enum InputType {
        BOOLEAN,
        COORD_PAIR,
        PLANET
    }

    public Card(Model model, boolean needPlayerChoice, FlightBoard board, int id) {
        this.needPlayerChoice = needPlayerChoice;
        this.board = board;
        this.model = model;
        ID = id;
        registered = new HashMap<>();
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

    protected Map<Tile.Rotation, Integer> getDrillsToActivate(JSONObject json){
        Map<Tile.Rotation, Integer> drills = new HashMap<>();

        for(Tile.Rotation r: Tile.Rotation.values()){
            drills.put(r, json.getInt(r.name()));
        }
        return drills;
    }

    protected int getNecessaryBattery(Map<Tile.Rotation, Integer> drills){
        AtomicInteger count = new AtomicInteger(0);
        drills.forEach((r, val) -> {
            count.addAndGet(val);
        });
        return count.get();
    }

    protected int getRocketToActivate(JSONObject json){
        return json.getInt("d_rocket");
    }

    protected boolean getChoice(JSONObject json){
        return json.getBoolean("choice");
    }


    public abstract Result<String> set(Player player, JSONObject json);

    public abstract Result<String> play();

    public abstract boolean ready();

    public abstract JSONObject getData();
}
