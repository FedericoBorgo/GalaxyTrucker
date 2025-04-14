package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.*;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Meteors extends Card {
    private final Map<Player, List<Integer>> useBattery = new HashMap<>();
    private final List<Projectile> projectiles;

    public static List<Projectile> genProjectiles(List<Pair<Tile.Side, Projectile.Type>> proj) {
        Map<Tile.Side, Set<Integer>> explored = new HashMap<>();
        List<Projectile> result = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < Tile.Side.values().length; i++)
            explored.put(Tile.Side.values()[i], new HashSet<>());

        proj.forEach(p -> {
            Tile.Side side = p.getKey();
            Projectile.Type type = p.getValue();
            int where;

            do {
                where = rollDice();
            }while (explored.get(side).contains(where));

           explored.get(side).add(where);

           result.add(new Projectile(type, side, where, counter.getAndIncrement()));
        });

        return result;
    }



    public static int rollDice() {
        return new Random().nextInt(6) + 1 + new Random().nextInt(6) + 1;
    }

    private Meteors(Model m, FlightBoard board, List<Pair<Tile.Side, Projectile.Type>> meteors, int id) {
        super(m, true, board, id, Card.Type.METEORS);

        projectiles = genProjectiles(meteors);
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
        && input.shieldFor.size() > model.getRemoved(player).battery)
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
         .ifPresent(c -> output.addDestroyed(p.getName(), c))));

        return Result.ok(output);
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }

    @Override
    public CardData getData() {
        CardData data = new CardData(type, id);
        data.projectiles = projectiles;
        return data;
    }

    public static List<Card> construct(Model m, FlightBoard board){
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

            cards.add(new Meteors(m, board, meteors, id));
        });

        return cards;
    }
}

