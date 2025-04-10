package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Stardust extends Card {
    private Stardust(FlightBoard board, int id) {
        super(null, false, board, id, Type.STARDUST);
    }

    @Override
    public Result<CardInput> set(Player player, CardInput input) {
        if(isRegistered(player))
            return Result.err("player already registered");
        register(player);
        return Result.ok(input);
    }

    @Override
    public Result<CardOutput> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end

        FlightBoard.Pawn pawn;
        for(int i = flight.getOrder().size()-1; i >= 0; i--){
            pawn = flight.getOrder().get(i);
            int days = -registered.get(pawn).getBoard().getTiles().countExposedConnectors();
            flight.moveRocket(pawn, days);
        }

        return Result.ok(new CardOutput());
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }

    @Override
    public JSONObject getData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("id", id);
        return jsonObject;
    }

    public static List<Card> construct(FlightBoard board){
        String out = dump(Objects.requireNonNull(Stardust.class.getResourceAsStream("stardust.json")));
        JSONObject jsonObject = new JSONObject(out);
        JSONArray jsonArray = jsonObject.getJSONArray("ids");
        List<Card> cards = new ArrayList<>();

        jsonArray.forEach(item -> cards.add(new Stardust(board, Integer.parseInt(item.toString()))));

        return cards;
    }
}
