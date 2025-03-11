package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Stardust extends Card {
    private final Set<Player> players;
    /**
     * constructor method
     * @param flightBoard takes in the current flightboard
     */
    public Stardust(FlightBoard flightBoard) {
        super( false, null);
        board = flightBoard;
        players = new HashSet<>();
    }

    //not really needed
    @Override
    public Result<List<String>> set(Player player, List<String> input) {
        players.add(player);
        return Result.ok(null);
    }

    /**
     * this moves the rocket pawns in reverse order
     * after having counted the number of exposed connectors
     * @return result type tells if it's been successful
     */
    @Override

    public Result<String> play() {
        if(board.getOrder().size()== players.size()){
            for(int i= board.getOrder().size()-1; i >= 0; i--){
                for(Player p: players)
                    if(p.getPawn() == board.getOrder().get(i))
                        board.moveRocket(board.getOrder().get(i), -p.getBoard().getBoard().countExposedConnectors());
            }
            return Result.ok(null);
        }
        return Result.err("not all players are declared");
    }
}
