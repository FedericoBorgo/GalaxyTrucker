package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.*;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class MeteorSwarm extends Card {
    private Map<Player, Map<Integer, Optional<Coordinate>>> playerChoice;
    List<Projectile> projectiles;

    public static int rollDice() {
        return new Random().nextInt(6) + 1;
    }

    public MeteorSwarm(FlightBoard board, List<Pair<Tile.Side, Projectile.ProjectileType>> meteors, int id) {
        super(null, true, board, id);
        projectiles = new ArrayList<>();
        playerChoice = new HashMap<>();
        AtomicInteger counter = new AtomicInteger();
        counter.set(0);

        meteors.forEach(pair -> {
            int number = rollDice() + rollDice();
            Projectile p = new Projectile(pair.getValue(), pair.getKey(), number, counter.getAndIncrement());
            projectiles.add(p);
        });

    }


    @Override
    public Result<Object> set(Player player, JSONObject json) {
        if (isRegistered(player))
            return Result.err("player already registered");

        if (!isCorrectOrder(player)) {
            return Result.err("player choice is not in order");

        }


        AtomicBoolean error = new AtomicBoolean(false);
        Map<Integer, Optional<Coordinate>> map = new HashMap<>();

        projectiles.forEach(projectile -> {
            Result<Coordinate> c = Coordinate.fromStringToCoordinate(json.getString("" + projectile.getID()));

            if(c.isOk()){
                if(player.getBoard().getBattery().get(c.getData()) > 0)
                    map.put(projectile.getID(), Optional.of(c.getData()));
                else
                    error.set(true);
            }
            else
                map.put(projectile.getID(), Optional.empty());
        });

        if (error.get()) {
            return Result.err("not enough battery");
        }

        playerChoice.put(player, map);
        register(player);

        return Result.ok(null);
    }

    @Override
    public Result<Object> play() {
        if (!ready())
            return Result.err("not all player declared their decision");

        playerChoice.forEach((player, choice) -> {
            projectiles.forEach(projectile -> {
                Optional<Coordinate> choiceOptional = choice.get(player);

                player.getBoard().hit(projectile, choiceOptional.isPresent());

                choiceOptional.ifPresent(coordinate -> {
                    player.getBoard().getBattery().remove(coordinate, 1);
                });

            });
        });

        return Result.ok(null);
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }

    @Override
    public JSONObject getData() {

        JSONObject json = new JSONObject();
        JSONArray meteors = new JSONArray();
        projectiles.forEach(projectile -> {
            meteors.put(projectile.toString());
        });
        json.put("meteors", meteors);
        return json;

    }
}

