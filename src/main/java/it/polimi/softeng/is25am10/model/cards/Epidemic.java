package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.boards.TilesBoard;

import java.util.EventListenerProxy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static it.polimi.softeng.is25am10.model.boards.TilesBoard.BOARD_HEIGHT;
import static it.polimi.softeng.is25am10.model.boards.TilesBoard.BOARD_WIDTH;

public class Epidemic extends Card {
    private final Set<Player> players;

   //constructor
    public Epidemic() {
        super( false, null);
        players = new HashSet<>();
    }


    @Override
    public Result<List<String>> set(Player player, List<String> input) {
        players.add(player);
        return Result.ok(null);
    }

    /**
     * removes the crew members that the epidemic killed
     * @return an error/success message
     */
    @Override
    public Result<String> play() {
        if(board.getOrder().size()== players.size()) {
            players.forEach(p -> {
                p.getBoard().epidemic();
            });
            return Result.ok(null);
        }
        return Result.err("not all players are declared");
    };
}
