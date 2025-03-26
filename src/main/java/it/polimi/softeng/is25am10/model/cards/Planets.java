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
        PLANET1, PLANET2, PLANET3, PLANET4, NOPLANET
    }

    public Planets(FlightBoard board, Map<Planet, List<GoodsBoard.Type>> goodsType, int id, int backmoves) {
        super(null, true, board, id, Type.PLANETS);
        this.goods = goodsType;
        this.flightDays = backmoves;

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


        Planet planet = Planet.valueOf(json.getString("planet"));

        //some player already took the planet?
        if(planet != Planet.NOPLANET && playerChoice.containsValue(planet))
            return Result.err("planet already occupied");

        if(!goods.containsKey(planet))
            return Result.err("planet does not exist");

        playerChoice.put(player, planet);

        if(playerChoice.values().containsAll(goods.keySet()))
            ready = true;

        register(player);
        return Result.ok(genAccepted());
    }


    @Override
    public Result<JSONObject> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end

        registered.forEach((pawn, player) -> {
            player.setGoodsReward(goods.get(playerChoice.get(player)));
        });

        JSONObject result = new JSONObject();

        //move pawns in reverse flight order
        List<FlightBoard.Pawn> reversed = new ArrayList<>(board.getOrder());
        Collections.reverse(reversed);
        for(FlightBoard.Pawn pawn : reversed){
            Player player = registered.get(pawn);
            if(Planet.NOPLANET != playerChoice.get(player)){
                board.moveRocket(player.getPawn(), flightDays);
            }
        }

        result.put("flight", board.toJSON());

        return Result.ok(result);
    }

    @Override
    public boolean ready() {
        return ready || allRegistered();
    }

    @Override
    public JSONObject getData() {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        json.put("type", type);
        json.put("id", id);

        Arrays.asList(Planet.values()).forEach(planet -> {
            if(planet != Planet.NOPLANET && !playerChoice.containsValue(planet)){
                jsonArray.put(planet);
            }
        });

        json.put("planets", jsonArray);

        return json;
    }

    public static List<Card> construct(FlightBoard board){
        String out = dump(Planets.class.getResourceAsStream("planets.json"));
        JSONArray jsonCards = new JSONArray(out);
        List<Card> cards = new ArrayList<>();

        jsonCards.forEach(item -> {
            JSONObject entry = (JSONObject) item;
            int id = entry.getInt("id");
            int days = entry.getInt("days");
            Map<Planet, List<GoodsBoard.Type>> goods = new HashMap<>();

            for (Planet p: Planet.values()) {
                if(entry.has(p.name())){
                    List<GoodsBoard.Type> types = new ArrayList<>();

                    entry.getJSONArray(p.name()).forEach(box -> {
                        types.add(GoodsBoard.Type.valueOf(box.toString()));
                    });

                    goods.put(p, types);
                }
            }

            cards.add(new Planets(board, goods, id, days));
        });

        return cards;
    }
}
