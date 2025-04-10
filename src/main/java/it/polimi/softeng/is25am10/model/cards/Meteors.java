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


public class Meteors extends Card {
    private final Map<Player, List<Integer>> useBattery = new HashMap<>();
    private final List<Projectile> projectiles = new ArrayList<>();

    public static int rollDice() {
        return new Random().nextInt(6) + 1;
    }

    private Meteors(FlightBoard board, List<Pair<Tile.Side, Projectile.Type>> meteors, int id) {
        super(null, true, board, id, Card.Type.METEORS);
        
        AtomicInteger counter = new AtomicInteger(0);

        meteors.forEach(pair -> projectiles.add(new Projectile(
                pair.getValue(),
                pair.getKey(),
                rollDice() + rollDice(),
                counter.getAndIncrement()
        )));
    }


    @Override
    public Result<CardInput> set(Player player, CardInput input) {
        if (isRegistered(player))
            return Result.err("player already registered");
        if(unexpected(player))
            return Result.err("player choice is not in order");

        // if the player is disconnected, check if he
        // dropped enough items.
        if (!input.disconnected
        && input.shieldFor.size() > model.getRemovedItems(player).battery)
                return Result.err("not enough battery");

        useBattery.put(player, input.shieldFor);
        register(player);

        return Result.ok(input);
    }

    @Override
    public Result<CardOutput> play() {
        if (!ready())
            return Result.err("not all player declared their decision");

        CardOutput output = new CardOutput();

        // for every projectile and for every player, hit them
        projectiles.forEach(proj -> registered.forEach((_, p) -> p.getBoard()
         .hit(proj, useBattery.get(p).contains(proj.ID()))
         .ifPresent(c -> output.removed.put(p.getName(), c))));

        return Result.ok(output);
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
        projectiles.forEach(projectile -> meteors.put(projectile.toString()));
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

