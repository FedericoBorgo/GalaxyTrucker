package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epidemic extends Card {
    private Epidemic(FlightBoard board, int id) {
        super(null, false, board, id, Type.EPIDEMIC);
    }

    @Override
    public Result<CardInput> set(Player player, CardInput input) {
        if(isRegistered(player))
            return Result.err("player already registered");
        // the input of the player does not matter
        register(player);
        return Result.ok(input);
    }

    @Override
    public Result<CardOutput> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end

        CardOutput output = new CardOutput();
        registered.forEach((_, player) -> {
            List<Coordinate> res = player.getBoard().epidemic();
            output.killedCrew.put(player.getName(), res);
        });

        return Result.ok(output);
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }

    @Override
    public JSONObject getData() {
        JSONObject data = new JSONObject();
        data.put("type", type);
        data.put("id", id);
        return data;
    }

    public static List<Card> construct(FlightBoard board){
        String out = dump(Objects.requireNonNull(Objects.requireNonNull(Epidemic.class.getResourceAsStream("epidemic.json"))));
        JSONObject jsonObject = new JSONObject(out);
        JSONArray jsonArray = jsonObject.getJSONArray("ids");
        List<Card> cards = new ArrayList<>();

        jsonArray.forEach(item -> cards.add(new Epidemic(board, Integer.parseInt(item.toString()))));

        return cards;
    }
}
