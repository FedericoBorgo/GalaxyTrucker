package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import org.json.JSONObject;
import org.json.JSONArray;

public class AbandonedShip extends Card {
    private int creditsReward, astronautsCost;
    private boolean someoneAccepted;
    private Player descendingPlayer;
    private JSONArray positionsCrew;

    public AbandonedShip(int id, JSONObject json) {
        super(true, id);
        creditsReward = json.getInt("Credits");
        astronautsCost = json.getInt("AstronautsCost");
        someoneAccepted = false;
    }

    // playerAccepts does not check if the player has enough astronauts or if the positions are correct
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
        boolean playerAccepts = json.getBoolean("playerAccepts");
        if(playerAccepts){
            someoneAccepted = true;
            descendingPlayer = player;
            positionsCrew = json.getJSONArray("positionsCrew");
        }
        register(player);
        return Result.ok(someoneAccepted);
    }

    @Override
    public Result<Object> play() {
        //begin common part
        if(!ready())
            return Result.err("not all players declared their decision");
        if(someoneAccepted)
        {
            descendingPlayer.giveCash(creditsReward);
            descendingPlayer.getBoard().abandonCrew(positionsCrew);
            return Result.ok(descendingPlayer);
        }
        else
        {
            return Result.ok("Nobody wanted to descend on the abandoned ship");
        }
        //end
    }

    @Override
    public boolean ready() {
        return someoneAccepted || allRegistered();
    }

    @Override
    public JSONObject getData() {
        return null;
    }
}
