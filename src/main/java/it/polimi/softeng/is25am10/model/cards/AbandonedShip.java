package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import javafx.util.Pair;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static it.polimi.softeng.is25am10.model.boards.Coordinate.fromStringToCoordinate;

public class AbandonedShip extends Card {
    private final int cash;
    private final int days;
    private final int astronaut;
    private boolean someoneAccepted;
    private Player descendingPlayer;
    private List<Pair<Coordinate, Integer>> positionsCrew;
    private Optional<Coordinate> brownPosition;
    private Optional<Coordinate> purplePosition;

    public AbandonedShip(int id, int astronaut, int cash, int days) {
        super(true, id);
       this.cash = cash;
       this.days = days;
       this.astronaut = astronaut;
       brownPosition = Optional.empty();
       purplePosition = Optional.empty();
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

        if(json.getBoolean("accept")){
            int count = 0;
            JSONArray tempPositionsCrew = json.getJSONArray("positionsCrew");
            positionsCrew = new ArrayList<>();

            for(int i = 0; i < tempPositionsCrew.length(); i++){
                JSONObject o = tempPositionsCrew.getJSONObject(i);
                Result<Coordinate> res = fromStringToCoordinate(o.getString("coordinate"));
                if(res.isErr())
                    return Result.err("coordinate not valid");

                Coordinate cord = res.getData();
                int qty = o.getInt("removeCount");
                ShipBoard.CrewType type = ShipBoard.CrewType.valueOf(o.getString("crewType"));

                if(!player.getBoard().checkEnough(cord, type, qty))
                    return Result.err("not enough");

                count+=qty;

                switch(type){
                    case ASTRONAUT -> positionsCrew.add(new Pair<>(cord, qty));
                    case B_ALIEN -> brownPosition = Optional.of(cord);
                    case P_ALIEN -> purplePosition = Optional.of(cord);
                }
            }
            if(count < this.astronaut)
                return Result.err("not enough astronaut");

            someoneAccepted = true;
            descendingPlayer = player;
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
            brownPosition.ifPresent(coordinate -> {
                descendingPlayer.getBoard().getBrown().remove(coordinate, 1);
            });
            purplePosition.ifPresent(coordinate -> {
                descendingPlayer.getBoard().getPurple().remove(coordinate, 1);
            });
            // remove the astronauts
            descendingPlayer.getBoard().getAstronaut().remove(positionsCrew);
            // give the credits to the player
            descendingPlayer.giveCash(cash);
            board.moveRocket(descendingPlayer.getPawn(), -days);
            return Result.ok(descendingPlayer);
        }

        return Result.ok("Nobody wanted to descend on the abandoned ship");
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
