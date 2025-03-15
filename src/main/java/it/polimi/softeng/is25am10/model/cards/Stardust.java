package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONObject;


public class Stardust extends Card {
    public Stardust(FlightBoard board, int id) {
        super(null, false, board, id);
    }

    //not really needed
    @Override
    public Result<String> set(Player player, JSONObject json) {
        if(isRegistered(player))
            return Result.err("player already registered");
        register(player);
        return Result.ok("");
    }

    /**
     * this moves the rocket pawns in reverse order
     * after having counted the number of exposed connectors
     * @return result type tells if it's been successful
     */
    @Override
    public Result<String> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end

        FlightBoard.RocketPawn pawn;
        for(int i= board.getOrder().size()-1; i >= 0; i--){
            pawn = board.getOrder().get(i);
            board.moveRocket(pawn, -registered.get(pawn).getBoard().getTiles().countExposedConnectors());
        }

        return Result.ok("");
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }

    @Override
    public JSONObject getData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("stardust", "");
        return jsonObject;
    }
}
