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
    public OpenSpace(Model model, FlightBoard board, int id) {
        super(model, true, board, id);
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

        int rockets = getRocketToActivate(json);

        if(model.getRemovedBattery(player) < rockets)
            return Result.err("not enough battery");


        enginePower.put(player.getPawn(), player.getBoard().getRocketPower(rockets));
        register(player);
        return Result.ok(rockets);
    }


    @Override
    public Result<Object> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end

        for(int i = board.getOrder().size() - 1; i >= 0; i--){
            FlightBoard.RocketPawn p = board.getOrder().get(i);
            board.moveRocket(p, enginePower.get(p));
        }

        return Result.ok(null);
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }

    @Override
    public JSONObject getData() {
        return null;
    }
}
