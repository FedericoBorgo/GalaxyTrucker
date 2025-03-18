package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AbandonedShip extends Card {
    private final int cashReward;
    private final int daysLost;
    private final int astronautCost;
    private boolean someoneAccepted;
    private Player descendingPlayer;

    public AbandonedShip(Model model, FlightBoard board, int id, int astronaut, int cash, int days) {
        super(model, true, board, id, Type.AB_SHIP);
       this.cashReward = cash;
       this.daysLost = days;
       this.astronautCost = astronaut;
    }

    @Override
    public Result<JSONObject> set(Player player, JSONObject json) {
        //begin
        //this section is the same for almost every card.
        if(isRegistered(player))
            return Result.err("player already registered");

        if(!isCorrectOrder(player)){
            return Result.err("player choice is not in order");
        }
        //end

        if(!someoneAccepted && getChoice(json)){
            if(model.getRemovedItems(player).guys >= astronautCost){
                someoneAccepted = true;
                descendingPlayer = player;
            }
        }

        register(player);
        return Result.ok(genAccepted());
    }

    @Override
    public Result<JSONObject> play() {
        // common part
        if(!ready())
            return Result.err("not all players declared their decision");

        JSONObject result = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject moved = new JSONObject();

        if(someoneAccepted){
            descendingPlayer.giveCash(cashReward);
            board.moveRocket(descendingPlayer.getPawn(), -daysLost);
            moved.put("pawn", descendingPlayer.getPawn());
            moved.put("days", -daysLost);
            jsonArray.put(moved);
        }

        result.put("moved", jsonArray);

        return Result.ok(result);
    }

    @Override
    public boolean ready() {
        return someoneAccepted || allRegistered();
    }

    @Override
    public JSONObject getData() {
        JSONObject data = new JSONObject();
        data.put("abandoned_ship", "");
        return data;
    }

    public static List<Card> construct(Model model, FlightBoard board){
        String out = dump(Objects.requireNonNull(AbandonedShip.class.getResourceAsStream("abandoned_ship.json")));
        JSONArray jsonCards = new JSONArray(out);
        List<Card> cards = new ArrayList<>();

        jsonCards.forEach(item -> {
            JSONObject entry = (JSONObject) item;
            int id = entry.getInt("id");
            int days = entry.getInt("days");
            int guys = entry.getInt("guys");
            int cash = entry.getInt("cash");

            cards.add(new AbandonedShip(model, board, id, guys, cash, days));
        });

        return cards;
    }
}
