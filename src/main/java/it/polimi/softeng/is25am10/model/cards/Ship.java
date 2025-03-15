package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ship extends Card {
    private final int cash;
    private final int days;
    private final int astronaut;
    private boolean someoneAccepted;
    private Player descendingPlayer;

    public Ship(Model model, FlightBoard board, int id, int astronaut, int cash, int days) {
        super(model, true, board, id, Type.SHIP);
       this.cash = cash;
       this.days = days;
       this.astronaut = astronaut;
    }

    @Override
    public Result<String> set(Player player, JSONObject json) {
        //begin
        //this section is the same for almost every card.
        if(isRegistered(player))
            return Result.err("player already registered");

        if(!isCorrectOrder(player)){
            return Result.err("player choice is not in order");
        }
        //end

        if(!someoneAccepted && getChoice(json)){
            if(model.getRemovedItems(player).guys >= astronaut){
                someoneAccepted = true;
                descendingPlayer = player;
            }
        }

        register(player);
        return Result.ok("");
    }

    @Override
    public Result<String> play() {
        //begin common part
        if(!ready())
            return Result.err("not all players declared their decision");

        if(someoneAccepted){
            descendingPlayer.giveCash(cash);
            board.moveRocket(descendingPlayer.getPawn(), -days);
            return Result.ok("");
        }

        return Result.ok("Nobody wanted to descend on the abandoned ship");
        //end
    }

    @Override
    public boolean ready() {
        return someoneAccepted || allRegistered();
    }

    @Override
    public JSONObject getData() {
        JSONObject data = new JSONObject();
        data.put("ship", "");
        return data;
    }

    public static List<Card> construct(Model model, FlightBoard board){
        String out = dump(Ship.class.getResourceAsStream("ship.json"));
        JSONArray jsonCards = new JSONArray(out);
        List<Card> cards = new ArrayList<>();

        jsonCards.forEach(item -> {
            JSONObject entry = (JSONObject) item;
            int id = entry.getInt("id");
            int days = entry.getInt("days");
            int guys = entry.getInt("guys");
            int cash = entry.getInt("cash");

            cards.add(new Ship(model, board, id, guys, cash, days));
        });

        return cards;
    }
}
