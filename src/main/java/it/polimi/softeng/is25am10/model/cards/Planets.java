package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Planets extends Card{
    private Map<Planet, List<GoodsBoard.Type>> goods;
    private Map<Player, Pair<Boolean, Planet>> playerChoice;
    private List<Planet> occupiedPlanets;

    static private final List<InputType> inputPlanet;

    static {
        inputPlanet = new ArrayList<>();
        inputPlanet.add(InputType.BOOLEAN);
        inputPlanet.add(InputType.PLANET);
    }

    public enum Planet{
        PLANET1, PLANET2, PLANET3
    }

    public Planets(Map<Planet, List<GoodsBoard.Type>> goodsType) {
        super(true, inputPlanet);
        this.goods = goodsType;
        this.occupiedPlanets = new ArrayList<>();
    }


    @Override
    public Result<List<String>> set(Player player, List<String> input) {
        if(playerChoice.containsKey(player)){
            return Result.err("Player choice is already set");
        }

        FlightBoard.RocketPawn pawn = player.getPawn();
        Set<FlightBoard.RocketPawn> rockets = playerChoice.keySet().stream().map(Player::getPawn).collect(Collectors.toSet());

        if(!rockets.containsAll(board.getOrder().subList(0, board.getOrder().indexOf(pawn)))){
            return Result.err("Player choice is not in order");
        }

        Pair<Boolean, Planet> choice = new Pair<>(Boolean.parseBoolean(input.get(0)), Planet.valueOf(input.get(1)));

        if(choice.getKey()){
            playerChoice.put(player, choice);
            return Result.ok(input);
        }

        if(occupiedPlanets.contains(choice.getValue())){
            return Result.err("Player choice is already occupied");
        }

        playerChoice.put(player, choice);
        occupiedPlanets.add(choice.getValue());
        return Result.ok(input);
    }


    @Override
    public Result<String> play() {
        if(playerChoice.size() != board.getOrder().size()){
            return Result.err("Not all Player choice are set");
        }

        playerChoice.forEach((player, choice) -> {
            if(choice.getKey()){
                return;
            }

            player.setGoodsReward(goods.get(choice.getValue()));
        });

     return Result.ok("okay");
    }
}
