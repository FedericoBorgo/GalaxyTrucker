package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * In turn, each player declares their engine strength, beginning with the leader,
 * and continuing in the order shown by the rockets on the flight board.
 * You must decide whether to spend battery tokens on any double engines
 * when it is your turn to declare engine strength.
 * Then you immediately move your rocket marker that many empty spaces
 * forward. This may allow you to pass players ahead of you
 * (occupied spaces are skipped) and perhaps even take the lead.
 */
public class OpenSpace extends Card {
    private Map<Player, List<Coordinate>> playerChoice;

    static private final List<InputType> inputOpenSpace;
    static {
        inputOpenSpace = new ArrayList<>();
        inputOpenSpace.add(InputType.COORD_PAIR); // Coordinate batteria
        inputOpenSpace.add(InputType.COORD_PAIR); // Coordinata motore doppio
    }

    public OpenSpace(FlightBoard fBoard) {
        super(true, inputOpenSpace);
        this.setBoard(fBoard);
    }

    /**
     * Takes the input from the players to prepare the card
     * @param player the player who sent these inputs
     * @param input in the form: x3y4x5y4 (pair of coordinates, uses qty=1 battery)
     * @return the engines activated with the battery (in String)
     */
    @Override
    public Result<List<String>> set(Player player, List<String> input) {
        // Same code as planets
        if(playerChoice.containsKey(player)){
            return Result.err("Player choice is already set");
        }
        FlightBoard.RocketPawn pawn = player.getPawn();
        // Put in a set all pawns of the players that have already made their choice
        Set<FlightBoard.RocketPawn> rockets = playerChoice.keySet().stream().map(Player::getPawn).collect(Collectors.toSet());
        if(!rockets.containsAll(board.getOrder().subList(0, board.getOrder().indexOf(pawn)))){
            return Result.err("Player choice is not in order");
        }

        List<Coordinate> activeDoubleEngines = new ArrayList<>();
        // Controllo che le coordinate fornite siano del tipo corretto e aggiorno la lista
        for (String s : input) {
            String sBattery = s.substring(0, 4);
            String sEngine = s.substring(4, 8);
            Result<Coordinate> rBattery = Coordinate.fromString(sBattery); // Controlla che la forma della stringa sia corretta
            Result<Coordinate> rEngine = Coordinate.fromString(sEngine);
            if(rBattery.isOk() && rEngine.isOk())
            {
                if(!player.getBoard().getBoard().getTile(rBattery.getData()).getData().getType().equals(Tile.Type.BATTERY_2)
                    || !player.getBoard().getBoard().getTile(rBattery.getData()).getData().getType().equals(Tile.Type.BATTERY_3)
                        || !player.getBoard().getBoard().getTile(rBattery.getData()).getData().getType().equals(Tile.Type.D_ROCKET)
                )
                {
                    return Result.err("Le coordinate fornite non sono valide");
                }
                else
                // le coordinate sono giuste
                {
                    // Se il cannone non è già stato attivato lo provo ad aggiungere ai cannoni da attivare
                    if(!activeDoubleEngines.contains(rEngine.getData())){
                        Result<Integer> integerResult = player.getBoard().getBattery().remove(rBattery.getData(), 1);
                        if(integerResult.isErr())
                            return Result.err("Le batterie in quelle coordinate non sono sufficienti");
                        // Le batterie bastano
                        activeDoubleEngines.add(rEngine.getData());
                    }
                }
            }
        }
        playerChoice.put(player, activeDoubleEngines);
        ArrayList<String> res = activeDoubleEngines.stream().map(Coordinate::CoordtoString).collect(Collectors.toCollection(ArrayList::new));
        return Result.ok(res);
    }

    @Override
    public Result<String> play() {
        if(playerChoice.size() != board.getOrder().size()){
            return Result.err("Not all Player choice are set");
        }

        playerChoice.forEach((player, input) -> {
            // è un int in realtà, da cambiare
            int power = (int) player.getBoard().getDrillsPower(input);
            board.moveRocket(player.getPawn(), power);
        });
        return Result.ok("okay");
    }
}
