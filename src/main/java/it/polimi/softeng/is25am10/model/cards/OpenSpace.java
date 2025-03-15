package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.softeng.is25am10.model.boards.Coordinate.fromStringToCoordinate;

public class OpenSpace extends Card {
    private final Map<FlightBoard.RocketPawn, Integer> enginePower = new HashMap<>();
    private final Map<String, Integer> enginePowerName = new HashMap<>();

    public OpenSpace(Model model, FlightBoard board, int id) {
        super(model, true, board, id);
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

        enginePower.put(player.getPawn(), player.getBoard().getRocketPower(model.getRemovedItems(player).battery));
        model.getRemovedItems(player).battery = 0;
        register(player);
        return Result.ok("" + enginePower.get(player.getPawn()));
    }


    @Override
    public Result<String> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end

        for(int i = board.getOrder().size() - 1; i >= 0; i--){
            FlightBoard.RocketPawn p = board.getOrder().get(i);
            board.moveRocket(p, enginePower.get(p));
        }

        return Result.ok("");
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

        json.put("openspace", jsonArray);

        return json;
    }
}
