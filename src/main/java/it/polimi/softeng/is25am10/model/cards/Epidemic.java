package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Epidemic extends Card {
   //constructor
    public Epidemic(FlightBoard board, int id) {
        super(null, false, board, id, Type.EPIDEMIC);
    }


    @Override
    public Result<JSONObject> set(Player player, JSONObject json) {
        if(isRegistered(player))
            return Result.err("player already registered");
        register(player);
        return Result.ok(genAccepted());
    }

    /**
     * removes the crew members that the epidemic killed
     * @return an error/success message
     */
    @Override
    public Result<JSONObject> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end

        JSONObject result = new JSONObject();
        JSONArray changes = new JSONArray();

        registered.forEach((_, player) -> {
            JSONObject entry = new JSONObject();
            JSONArray removed = new JSONArray();
            List<Coordinate> res = player.getBoard().epidemic();

            res.forEach(coordinate -> {
                removed.put(coordinate.toString());
            });

            entry.put("name", player.getName());
            entry.put("removed", removed);

            changes.put(entry);
        });

        result.put("removed", changes);

        return Result.ok(result);
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }

    @Override
    public JSONObject getData() {
        JSONObject data = new JSONObject();
        data.put("epidemic", "");
        return data;
    }

    public static List<Card> construct(FlightBoard board){
        String out = dump(Epidemic.class.getResourceAsStream("epidemic.json"));
        JSONObject jsonObject = new JSONObject(out);
        JSONArray jsonArray = jsonObject.getJSONArray("ids");
        List<Card> cards = new ArrayList<>();

        jsonArray.forEach(item -> {
            cards.add(new Epidemic(board, Integer.parseInt(item.toString())));
        });

        return cards;
    }
}
