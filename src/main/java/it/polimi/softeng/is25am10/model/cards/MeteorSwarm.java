package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Projectile;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class MeteorSwarm extends Card {
    Projectile meteorsType;
    Tile.Side meteorsDirection;
    int where;
    private boolean ready = false;
    JSONObject json2 = new JSONObject();
    private Map<Player, Boolean> playerChoice;

    public static int rollDice() {
        return new Random().nextInt(6) + 1;
    }

    public MeteorSwarm(Projectile type,Tile.Side direction,boolean choice, int where, int id) {
        super(true, id);
        this.meteorsType = type;
        this.meteorsDirection = direction;
        this.where = where;
        int number1 = rollDice();
        int number2 = rollDice();
        int target = number1 + number2;
        json2.put("target", target);
    }



    @Override
    public Result<Object> set(Player player, JSONObject json) {
        if (isRegistered(player))
            return Result.err("player already registered");

        if (!isCorrectOrder(player)) {
            return Result.err("player choice is not in order");

        }

        // if the player wants to use battery (if they aren't required or he doesn't have any, the choice is no)
        boolean choice = json.getBoolean("choice");
        if(choice && player.getBoard().getBattery().getTotal()<1) {
                return Result.err("not enough battery");
        }

        playerChoice.put(player,choice);
        ready = true;

        return Result.ok(null);
    }

    @Override
    public Result<Object> play() {
        if(!ready())
            return Result.err("not all player declared their decision");

        Set<Player> players = playerChoice.keySet();
        for(Player player : players) {
            player.getBoard().getTiles().hit(meteorsType, meteorsDirection, where, playerChoice.get(player));
        }


        return Result.ok(null);
    }

    @Override
    public boolean ready() {
        return ready;
    }

    @Override
    public JSONObject getData() {
        json2.put("type", meteorsType);
        json2.put("direction", meteorsDirection);
        if(meteorsDirection == Tile.Side.LEFT || meteorsDirection == Tile.Side.RIGHT) {
            int y = json2.getInt("target");

        } else{
           int x = json2.getInt("target");
        }
        return json2;
    }
}

