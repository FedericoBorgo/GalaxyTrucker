package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Space extends Card {
    private final Map<FlightBoard.Pawn, Integer> enginePower = new HashMap<>();
    private final Map<String, Integer> enginePowerName = new HashMap<>();

    public Space(Model model, FlightBoard board, int id) {
        super(model, true, board, id, Type.OPEN_SPACE);
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

        enginePower.put(player.getPawn(), player.getBoard().getEnginePower(model.getRemovedItems(player).battery));
        register(player);
        return Result.ok(genAccepted());
    }


    @Override
    public Result<JSONObject> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end

        JSONObject result = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for(int i = board.getOrder().size() - 1; i >= 0; i--){
            JSONObject player = new JSONObject();
            FlightBoard.Pawn p = board.getOrder().get(i);
            board.moveRocket(p, enginePower.get(p));

            player.put("days", enginePower.get(p));
            player.put("pawn", p);
            jsonArray.put(player);
        }
        result.put("moved", jsonArray);

        return Result.ok(result);
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }

    @Override
    public JSONObject getData() {
        JSONArray jsonArray = new JSONArray();
        JSONObject json = new JSONObject();

        enginePowerName.forEach((name, power) -> {
            JSONObject entry = new JSONObject();
            entry.put("name", name);
            entry.put("val", power);
            jsonArray.put(entry);
        });

        json.put("space", jsonArray);

        return json;
    }

    public static List<Card> construct(Model model, FlightBoard board){
        String out = dump(Objects.requireNonNull(Space.class.getResourceAsStream("open_space.json")));
        JSONObject jsonObject = new JSONObject(out);
        JSONArray jsonArray = jsonObject.getJSONArray("ids");
        List<Card> cards = new ArrayList<>();

        jsonArray.forEach(item -> {
            cards.add(new Space(model, board, Integer.parseInt(item.toString())));
        });

        return cards;
    }
}
