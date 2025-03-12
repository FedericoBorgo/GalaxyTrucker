package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
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

    public Planets(Map<Planet, List<GoodsBoard.Type>> goodsType, int id, int backmoves) {
        super(true, id);
        this.goods = goodsType;
        this.flightDays = backmoves;

    }

    @Override
    public Result<Object> set(Player player, JSONObject json) {
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
        return Result.ok(null);
    }


    @Override
    public Result<Object> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end

        registered.forEach((pawn, player) -> {
            player.setGoodsReward(goods.get(playerChoice.get(player)));
        });

        //move pawns in reverse flight order
        List<FlightBoard.RocketPawn> reversed = new ArrayList<>(board.getOrder());
        Collections.reverse(reversed);
        for(FlightBoard.RocketPawn pawn : reversed){
            Player player = registered.get(pawn);
            if(Planet.NOPLANET != playerChoice.get(player)){
                board.moveRocket(player.getPawn(), flightDays);
            }
        }

        return Result.ok(null);
    }

    @Override
    public boolean ready() {
        return ready;
    }
}
