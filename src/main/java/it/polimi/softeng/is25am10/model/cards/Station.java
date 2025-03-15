package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import org.json.JSONObject;

import java.util.List;

public class Station extends Card {
    private List<GoodsBoard.Type> goods;
    private Player p;
    private int requiredCrew;
    private int flightDays;
    private boolean ready = false;

    public Station(FlightBoard board, int crew, List<GoodsBoard.Type> goodsType, int id, int backmoves) {
        super(null, true, board, id, Type.STATION);
        this.goods = goodsType;
        this.requiredCrew = crew;
        this.flightDays = backmoves;

    }

    @Override
    public Result<String> set(Player player, JSONObject json) {
        if(isRegistered(player))
            return Result.err("player already registered");

        if(!isCorrectOrder(player)){
            return Result.err("player choice is not in order");
        }

        //enough crew?
        if(!ready && getChoice(json)) {
            int crew = player.getBoard().getAstronaut().getTotal();
            if (crew < requiredCrew) {
                return Result.err("not enough crew");
            }else{
                ready = true;
            }
            p = player;
        }

        register(player);

        return Result.ok("");
    }

    @Override
    public Result<String> play() {
        if(!ready())
            return Result.err("nobody chose yes");

        p.setGoodsReward(goods);
        board.moveRocket(p.getPawn(), -flightDays);

        return Result.ok("");
    }

    @Override
    public boolean ready() {
        return ready || allRegistered();
    }

    @Override
    public JSONObject getData() {
        JSONObject json = new JSONObject();
        json.put("station", "");
        return json;
    }
}
