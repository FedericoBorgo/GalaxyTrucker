package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONObject;

import java.util.List;

public class Epidemic extends Card {
   //constructor
    public Epidemic(FlightBoard board, int id) {
        super(null, false, board, id, Type.EPIDEMIC);
    }


    @Override
    public Result<String> set(Player player, JSONObject json) {
        if(isRegistered(player))
            return Result.err("player already registered");
        register(player);
        return Result.ok("");
    }

    /**
     * removes the crew members that the epidemic killed
     * @return an error/success message
     */
    @Override
    public Result<String> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end

        registered.forEach((_, player) -> {
            player.getBoard().epidemic();
        });

        return Result.ok("");
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
}
