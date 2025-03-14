package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import javafx.util.Pair;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.softeng.is25am10.model.boards.Coordinate.fromStringToCoordinate;

public class AbandonedShip extends Card {
    private final int creditsReward;
    private boolean someoneAccepted;
    private Player descendingPlayer;
    private List<Pair<Coordinate, Integer>> positionsCrew;
    private Result<Coordinate> brownPosition;
    private Result<Coordinate> purplePosition;

    public enum CrewType{
        ASTRONAUT, B_ALIEN, P_ALIEN
    }

    public AbandonedShip(int id, JSONObject json) {
        super(true, id);
        creditsReward = json.getInt("Credits");
        someoneAccepted = false;
        brownPosition = Result.err("no brown alien");
        purplePosition = Result.err("no purple alien");
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
        boolean playerAccepts = json.getBoolean("playerAccepts");
        // JSON: positionsCrew: [
        //    { "coordinate": "3,4", "removeCount": 2, "type": 'ASTRONAUT' },
        //    { "coordinate": "5,6", "removeCount": 1, "type": 'P_ALIEN' },
        //    { "coordinate": "1,2", "removeCount": 2, "type": 'ASTRONAUT' }
        //  ]
        if(playerAccepts){
            someoneAccepted = true;
            descendingPlayer = player;
            JSONArray tempPositionsCrew = json.getJSONArray("positionsCrew");
            positionsCrew = new ArrayList<>();
            for(int i = 0; i < tempPositionsCrew.length(); i++){
                JSONObject o = tempPositionsCrew.getJSONObject(i);
                Result<Coordinate> cord = fromStringToCoordinate(o.getString("coordinate"));
                int qty = o.getInt("removeCount");
                CrewType type = CrewType.valueOf(o.getString("crewType"));
                if(cord.isErr())
                    return Result.err("coordinate non valide");
                if(type == CrewType.ASTRONAUT)
                    positionsCrew.add(new Pair<>(cord.getData(), qty));
                if(type == CrewType.B_ALIEN)
                    brownPosition = Result.ok(cord.getData());
                if(type == CrewType.P_ALIEN)
                    purplePosition = Result.ok(cord.getData());
            }
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
            // remove the purple/brown alien
            if(brownPosition.isOk())
                descendingPlayer.getBoard().getBrown().remove(positionsCrew);
            if(purplePosition.isOk())
                descendingPlayer.getBoard().getPurple().remove(positionsCrew);
            // remove the astronauts
            descendingPlayer.getBoard().getAstronaut().remove(positionsCrew);
            // give the credits to the player
            descendingPlayer.giveCash(creditsReward);
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
