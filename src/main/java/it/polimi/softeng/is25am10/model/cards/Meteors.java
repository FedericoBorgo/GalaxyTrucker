package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Projectile;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Meteors extends Card {
    private Map<Player, List<Integer>> useBattery;
    List<Projectile> projectiles;

    public static int rollDice() {
        return new Random().nextInt(6) + 1;
    }

    public Meteors(FlightBoard board, List<Pair<Tile.Side, Projectile.Type>> meteors, int id) {
        super(null, true, board, id, Card.Type.METEORS);
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
    public Result<JSONObject> set(Player player, JSONObject json) {
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

        return Result.ok(genAccepted());
    }

    @Override
    public Result<JSONObject> play() {
        if (!ready())
            return Result.err("not all player declared their decision");

        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();

        projectiles.forEach(projectile -> {
            registered.forEach((pawn, p) -> {
                Optional<Coordinate> destroyed = p.getBoard().hit(projectile, useBattery.get(p).contains(projectile.ID()));

                destroyed.ifPresent(c -> {
                    JSONObject obj = new JSONObject();
                    obj.put("name", p.getName());
                    obj.put("coord", c.toString());
                    array.put(obj);
                });
            });
        });

        result.put("destroyed", array);

        return Result.ok(result);
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }

    @Override
    public JSONObject getData() {
        JSONObject data = new JSONObject();
        data.put("type", type);
        data.put("id", id);

        JSONArray meteors = new JSONArray();
        projectiles.forEach(projectile -> {
            meteors.put(projectile.toString());
        });
        data.put("meteors", meteors);
        return data;
    }

    public static List<Card> construct(FlightBoard board){
        String out = dump(Objects.requireNonNull(Meteors.class.getResourceAsStream("meteors.json")));
        JSONArray jsonCards = new JSONArray(out);
        List<Card> cards = new ArrayList<>();

        jsonCards.forEach(item -> {
            JSONObject entry = (JSONObject) item;
            int id = entry.getInt("id");
            List<Pair<Tile.Side, Projectile.Type>> meteors = new ArrayList<>();

            entry.getJSONArray("asteroids").forEach(obj -> {
                JSONObject asteroid = (JSONObject) obj;
                Tile.Side side = Tile.Side.valueOf(asteroid.getString("side"));
                Projectile.Type type = Projectile.Type.valueOf(asteroid.getString("type"));
                meteors.add(new Pair<>(side, type));
            });

            cards.add(new Meteors(board, meteors, id));
        });

        return cards;
    }
}

