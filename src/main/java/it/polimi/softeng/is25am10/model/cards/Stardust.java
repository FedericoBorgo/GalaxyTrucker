package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;

import java.util.List;


public class Stardust extends Card {
    /**
     * constructor method
     * @param flightBoard takes in the current flightboard
     */
    public Stardust(FlightBoard flightBoard) {
        super( false, null);
        board = flightBoard;
    }

    //not really needed
    @Override
    public Result<List<String>> set(Player player, List<String> input) {
        playerChoice.put(player, null);
        return Result.ok(null);
    }

    /**
     * this moves the rocket pawns in reverse order
     * after having counted the number of exposed connectors
     * @return result type tells if it's been successful
     */
    @Override

    public Result<String> play() {
        if(board.getOrder().size()== playerChoice.size()){
            for(int i= board.getOrder().size()-1; i >= 0; i++){
                for(Player p: playerChoice.keySet())
                    if(p.getPawn() == board.getOrder().get(i))
                        board.moveRocket(board.getOrder().get(i), -p.getBoard().getBoard().countExposedConnectors());
            }
            return Result.ok(null);
        }
        return Result.err("not all players are declared");
    }
}
