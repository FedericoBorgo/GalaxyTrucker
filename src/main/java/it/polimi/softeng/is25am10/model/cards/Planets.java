package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Planets extends Card{
    private Map<Planet, List<GoodsBoard.Type>> goods;
    private Map<Player, Planet> playerChoice;
    private boolean ready = false;
    private int flightDays;

    public enum Planet{
        PLANET1, PLANET2, PLANET3, NOPLANET
    }

    public Planets(FlightBoard board, Map<Planet, List<GoodsBoard.Type>> goodsType, int id, int backmoves) {
        super(null, true, board, id);
        this.goods = goodsType;
        this.flightDays = backmoves;

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


        Planet planet = Planet.valueOf(json.getString("planet"));

        //some player already took the planet?
        if(planet != Planet.NOPLANET && playerChoice.containsValue(planet))
            return Result.err("planet already occupied");

        playerChoice.put(player, planet);

        if(playerChoice.values().containsAll(List.of(Planet.values())))
            ready = true;

        register(player);
        return Result.ok("");
    }


    @Override
    public Result<String> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end

        registered.forEach((pawn, player) -> {
            player.setGoodsReward(goods.get(playerChoice.get(player)));
        });

        //move pawns in reverse flight order
        List<FlightBoard.Pawn> reversed = new ArrayList<>(board.getOrder());
        Collections.reverse(reversed);
        for(FlightBoard.Pawn pawn : reversed){
            Player player = registered.get(pawn);
            if(Planet.NOPLANET != playerChoice.get(player)){
                board.moveRocket(player.getPawn(), flightDays);
            }
        }

        return Result.ok("");
    }

    @Override
    public boolean ready() {
        return ready || allRegistered();
    }

    @Override
    public JSONObject getData() {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        Arrays.asList(Planet.values()).forEach(planet -> {
            if(planet != Planet.NOPLANET && !playerChoice.containsValue(planet)){
                jsonArray.put(planet);
            }
        });

        json.put("planets", jsonArray);

        return json;
    }
}
