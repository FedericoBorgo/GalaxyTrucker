package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Projectile;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class MeteorSwarm extends Card {
    private Map<Player, List<Integer>> useBattery;
    List<Projectile> projectiles;

    public static int rollDice() {
        return new Random().nextInt(6) + 1;
    }

    public MeteorSwarm(FlightBoard board, List<Pair<Tile.Side, Projectile.ProjectileType>> meteors, int id) {
        super(null, true, board, id);
        projectiles = new ArrayList<>();
        useBattery = new HashMap<>();
        AtomicInteger counter = new AtomicInteger();
        counter.set(0);

        meteors.forEach(pair -> {
            int number = rollDice() + rollDice();
            Projectile p = new Projectile(pair.getValue(), pair.getKey(), number, counter.getAndIncrement());
            projectiles.add(p);
        });
    }


    @Override
    public Result<String> set(Player player, JSONObject json) {
        if (isRegistered(player))
            return Result.err("player already registered");

        if (!isCorrectOrder(player)) {
            return Result.err("player choice is not in order");

        }

        JSONArray array = json.getJSONArray("use");
        List<Integer> use = new ArrayList<>();

        array.forEach(item -> {
            use.add(Integer.parseInt(item.toString()));
        });

        if(use.size() > model.getRemovedItems(player).battery)
            return Result.err("not enough battery");

        useBattery.put(player, use);
        register(player);

        return Result.ok("");
    }

    @Override
    public Result<String> play() {
        if (!ready())
            return Result.err("not all player declared their decision");

        projectiles.forEach(projectile -> {
            registered.forEach((pawn, p) -> {
                p.getBoard().hit(projectile, useBattery.get(p).contains(projectile.getID()));
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
            if(projectile.getType() == Projectile.ProjectileType.SMALL_ASTEROID)
                meteors.put(projectile.toString());
        });
        json.put("meteorswarm", meteors);
        return json;

    }
}

