package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.*;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Warzone extends Card {
    private enum MalusTypes {
        DAYS, GUYS, FIRE, GOODS
    }
    private enum LeastTypes {
        LEAST_GUYS, LEAST_ENGINE, LEAST_CANNON
    }

    private final List<Projectile> fire = new ArrayList<>();
    private final int days, goods, guys;
    private final Map<Player, List<Integer>> useBattery = new HashMap<>();
    private final Map<Player, Map<LeastTypes, Double>> declaredPower = new HashMap<>();
    private final Map<LeastTypes, MalusTypes> malusTypes;

    public static int rollDice() {
        return new Random().nextInt(6) + 1;
    }

    private Warzone(Model model, FlightBoard board, int id,
                   Map<LeastTypes, MalusTypes> malusTypes,
                   List<Pair<Projectile.Type, Tile.Side>> fire,
                   int days, int goods, int guys) {
        super(model, true, board, id, Type.WAR_ZONE);
        this.days = days;
        this.goods = goods;
        this.guys = guys;
        this.malusTypes = malusTypes;
        
        AtomicInteger counter = new AtomicInteger(0);

        fire.forEach(pair -> this.fire.add(new Projectile(
                pair.getKey(),
                pair.getValue(),
                rollDice() + rollDice(),
                counter.getAndIncrement()
        )));
    }

    @Override
    public Result<CardInput> set(Player player, CardInput input) {
        if(isRegistered(player))
            return Result.err("player already registered");
        if(unexpected(player))
            return Result.err("player choice is not in order");

        if(input.shieldFor.size() > player.getBoard().getBattery().getTotal())
            return Result.err("not enough battery");

        int requiredBattery = model.batteryForCannon(player.getName());
        int removedBattery = model.getRemoved(player).battery;

        if(requiredBattery > removedBattery)
            return Result.err("not enough battery");

        double cannonPower = player.getBoard().getCannonsPower(model.getCannonsToUse(player));
        double enginePower = player.getBoard().getEnginePower(removedBattery-requiredBattery);
        double astronaut = player.getBoard().getAstronaut().getTotal();

        Map<LeastTypes, Double> values = new HashMap<>();
        values.put(LeastTypes.LEAST_GUYS, astronaut);
        values.put(LeastTypes.LEAST_ENGINE, enginePower);
        values.put(LeastTypes.LEAST_CANNON, cannonPower);

        declaredPower.put(player, values);
        useBattery.put(player, input.shieldFor);
        register(player);
        return Result.ok(input);
    }

    private List<Player> findLeast(LeastTypes type){
        OptionalDouble min = declaredPower.values()
                .stream()
                .mapToDouble(map -> map.get(type))
                .min();

        List<Player> result = new ArrayList<>();

        min.ifPresent(value -> result.addAll(declaredPower.keySet()
                .stream()
                .filter(p -> declaredPower.get(p).get(type) == value)
                .toList()));

        return result;
    }

    @Override
    public Result<CardOutput> play() {
        if(!ready())
            return Result.err("not ready");

        CardOutput output = new CardOutput();

        malusTypes.forEach((malusType, type) -> {
            List<Player> players = findLeast(malusType);

            switch(type){
                case DAYS -> players.forEach(p -> flight.moveRocket(p.getPawn(), -days));
                case GUYS -> players.forEach(p -> model.getRemoved(p).guys -= guys);
                case FIRE -> {
                    players.forEach(p -> model.getRemoved(p).battery -= useBattery.get(p).size());

                    fire.forEach(proj -> players.forEach(p -> p.getBoard()
                            .hit(proj, useBattery.get(p).contains(proj.ID()))
                            .ifPresent(c -> output.removed.put(p.getName(), c))));
                }
                case GOODS -> players.forEach(p -> model.getRemoved(p).goods -= goods);
            }
        });

        return Result.ok(output);
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }

    @Override
    public CardData getData() {
        return new CardData(type, id);
    }

    public static List<Card> construct(Model model, FlightBoard board){
        String out = dump(Objects.requireNonNull(Warzone.class.getResourceAsStream("warzone.json")));
        JSONArray jsonCards = new JSONArray(out);
        List<Card> cards = new ArrayList<>();

        jsonCards.forEach(obj -> {
            Map<LeastTypes, MalusTypes> malusTypes = new HashMap<>();
            List<Pair<Projectile.Type, Tile.Side>> fire = new ArrayList<>();
            JSONObject item = (JSONObject) obj;
            JSONArray jsonProjectiles = item.getJSONArray("FIRE");
            int id = item.getInt("id");
            int guys = item.getInt("GUYS");
            int days = item.getInt("DAYS");
            int goods = item.getInt("GOODS");

            for (LeastTypes value : LeastTypes.values()) {
                malusTypes.put(value, MalusTypes.valueOf(item.getString(value.name())));
            }

            jsonProjectiles.forEach(objectProjectile -> {
                JSONObject projectile = (JSONObject) objectProjectile;
                Projectile.Type type = Projectile.Type.valueOf(projectile.getString("type"));
                Tile.Side side = Tile.Side.valueOf(projectile.getString("side"));
                fire.add(new Pair<>(type, side));
            });

            cards.add(new Warzone(model, board, id, malusTypes, fire, days, goods, guys));
        });

        return cards;
    }
}
