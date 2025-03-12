package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import org.json.JSONObject;

public class Epidemic extends Card {
   //constructor
    public Epidemic(int id) {
        super( false, id);
    }


    @Override
    public Result<Object> set(Player player, JSONObject json) {
        if(isRegistered(player))
            return Result.err("player already registered");
        register(player);
        return Result.ok(null);
    }

    /**
     * removes the crew members that the epidemic killed
     * @return an error/success message
     */
    @Override
    public Result<Object> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end

        registered.forEach((_, player) -> {
            player.getBoard().epidemic();
        });

        return Result.ok(null);
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }
}
