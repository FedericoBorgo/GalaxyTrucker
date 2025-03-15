package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONObject;

public class AbandonedShip extends Card {
    private final int cash;
    private final int days;
    private final int astronaut;
    private boolean someoneAccepted;
    private Player descendingPlayer;

    public AbandonedShip(Model model, FlightBoard board, int id, int astronaut, int cash, int days) {
        super(model, true, board, id);
       this.cash = cash;
       this.days = days;
       this.astronaut = astronaut;
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

        if(!someoneAccepted && getChoice(json)){
            if(model.getRemovedItems(player).guys >= astronaut){
                someoneAccepted = true;
                descendingPlayer = player;
            }
        }

        register(player);
        return Result.ok("");
    }

    @Override
    public Result<String> play() {
        //begin common part
        if(!ready())
            return Result.err("not all players declared their decision");

        if(someoneAccepted){
            descendingPlayer.giveCash(cash);
            board.moveRocket(descendingPlayer.getPawn(), -days);
            return Result.ok("");
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
        JSONObject data = new JSONObject();
        data.put("abandonedship", "");
        return data;
    }
}
