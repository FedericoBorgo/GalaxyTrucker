package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;

import java.util.*;
import java.util.stream.Collectors;

public class Planets extends Card{
    private List<String> planets;
    private List<String> availableGoods;
    private Map<Planet, List<GoodsBoard.Type>> goods;
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
        if(Boolean.parseBoolean(input.get(0))){
            playerChoice.put(player,input);
            return Result.ok(input);
        }

        if(occupiedPlanets.contains(Planet.valueOf(input.get(1)))){
            return Result.err("Player choice is already occupied");
        }
        playerChoice.put(player, input);
        occupiedPlanets.add(Planet.valueOf(input.get(1)));
        return Result.ok(input);
    }


    @Override
    public Result<String> play() {
        if(playerChoice.size() != board.getOrder().size()){
            return Result.err("Not all Player choice are set");
        }

        playerChoice.forEach((player, input) -> {
            if(input.get(0).equals("false")){
                return;
            }
            player.setGoodsReward(goods.get(Planet.valueOf(input.get(1))));
        });
     return Result.ok("okay");
    }
}
