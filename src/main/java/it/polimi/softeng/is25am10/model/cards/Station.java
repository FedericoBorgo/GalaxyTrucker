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
    public Result<JSONObject> set(Player player, JSONObject json) {
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

        return Result.ok(genAccepted());
    }

    @Override
    public Result<JSONObject> play() {
        if(!ready())
            return Result.err("nobody chose yes");

        JSONObject result = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject moved = new JSONObject();

        p.setGoodsReward(goods);
        board.moveRocket(p.getPawn(), -flightDays);

        moved.put("pawn", p.getPawn());
        moved.put("days", -flightDays);
        jsonArray.put(moved);
        result.put("moved", jsonArray);

        return Result.ok(result);
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

    public static List<Card> construct(FlightBoard board){
        String out = dump(Station.class.getResourceAsStream("station.json"));
        JSONArray jsonCards = new JSONArray(out);
        List<Card> cards = new ArrayList<>();

        jsonCards.forEach(item -> {
            JSONObject entry = (JSONObject) item;
            int id = entry.getInt("id");
            int days = entry.getInt("days");
            int guys = entry.getInt("guys");
            List<GoodsBoard.Type> types = new ArrayList<>();

            entry.getJSONArray("goods").forEach(type -> {
                types.add(GoodsBoard.Type.valueOf(type.toString()));
            });

            cards.add(new Station(board, guys, types, id, days));
        });

        return cards;
    }
}
