package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.boards.TilesBoard;

import java.util.EventListenerProxy;
import java.util.List;

import static it.polimi.softeng.is25am10.model.boards.TilesBoard.BOARD_HEIGHT;
import static it.polimi.softeng.is25am10.model.boards.TilesBoard.BOARD_WIDTH;

public class Epidemic extends Card {

    public Epidemic(boolean needPlayerChoice, List<InputType> inputOrder) {
        super( needPlayerChoice, inputOrder);
    }

    @Override
    public Result<List<String>> set(Player player, List<String> input) {
        playerChoice.put(player, null);
        return Result.ok(null);
    }

    @Override
    public Result<String> play() {
        if(board.getOrder().size()== playerChoice.size()) {
            playerChoice.keySet().forEach(p -> {
                p.getBoard().epidemic();
            });
            return Result.ok(null);
        }
            return Result.err("not all players are declared");
    };

}
