package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.*;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Warzone extends Card {
    private enum MalusTypes {
        DAYS, GUYS, FIRE, GOODS;
    }
    private enum LeastTypes {
        LEAST_GUYS, LEAST_ENGINE, LEAST_CANNON;
    }

    private final List<Projectile> fire = new ArrayList<>();
    private final int days, goods, guys;
    private Map<Player, List<Integer>> useBattery = new HashMap<>();
    private Map<Player, Map<LeastTypes, Double>> declaredPower = new HashMap<>();
    private Map<LeastTypes, MalusTypes> malusTypes;

    public static int rollDice() {
        return new Random().nextInt(6) + 1;
    }

    public Warzone(Model model, FlightBoard board, int id,
                   Map<LeastTypes, MalusTypes> malusTypes,
                   List<Pair<Projectile.Type, Tile.Side>> fire,
                   int days, int goods, int guys) {
        super(model, true, board, id, Type.WARZONE);
        this.days = days;
        this.goods = goods;
        this.guys = guys;
        this.malusTypes = malusTypes;
        AtomicInteger counter = new AtomicInteger(0);

        fire.forEach(pair -> {
            int number = rollDice() + rollDice();
            Projectile p = new Projectile(pair.getKey(), pair.getValue(), number, counter.getAndIncrement());
            this.fire.add(p);
        });
    }

    @Override
    public Result<JSONObject> set(Player player, JSONObject json) {
        if(isRegistered(player))
            return Result.err("player already registered");

        if(!isCorrectOrder(player)){
            return Result.err("player choice is not in order");
        }

        JSONArray array = json.getJSONArray("use");
        List<Integer> use = new ArrayList<>();

        array.forEach(item -> {
            use.add(Integer.parseInt(item.toString()));
        });

        if(use.size() > player.getBoard().getBattery().getTotal())
            return Result.err("not enough battery");

        int requiredBattery = model.batteryRequiredForCannon(player.getName());
        int removedBattery = model.getRemovedItems(player).battery;

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
        useBattery.put(player, use);
        register(player);
        return Result.ok(genAccepted());
    }

    private List<Player> findLeast(LeastTypes type){
        OptionalDouble min = declaredPower.values()
                .stream()
                .mapToDouble(map -> map.get(type))
                .min();

        List<Player> result = new ArrayList<>();

        min.ifPresent(value -> {
            result.addAll(declaredPower.keySet()
                    .stream()
                    .filter(p -> declaredPower.get(p).get(type) == value)
                    .toList());
        });

        return result;
    }

    @Override
    public Result<JSONObject> play() {
        if(!ready())
            return Result.err("not ready");

        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();

        malusTypes.forEach((malusType, type) -> {
            List<Player> players = findLeast(malusType);

            switch(type){
                case DAYS -> {
                    players.forEach(p -> {
                        board.moveRocket(p.getPawn(), -days);
                    });
                }
                case GUYS -> {
                    players.forEach(p -> {
                        model.getRemovedItems(p).guys -= guys;
                    });
                }
                case FIRE -> {
                    players.forEach(p -> {
                        model.getRemovedItems(p).battery -= useBattery.get(p).size();
                    });

                    fire.forEach(p -> {
                        players.forEach(f -> {
                            Optional<Coordinate> destroyed = f
                                    .getBoard()
                                    .hit(p, useBattery.get(f).contains(p.ID()));

                            destroyed.ifPresent(c -> {
                                JSONObject obj = new JSONObject();
                                obj.put("name", f.getName());
                                obj.put("coord", c.toString());
                                array.put(obj);
                            });
                        });
                    });
                }
                case GOODS -> {
                    players.forEach(p -> {
                        model.getRemovedItems(p).goods -= goods;
                    });
                }
            }
        });

        object.put("destroyed", array);
        object.put("flight", board.toJSON());

        return Result.ok(object);
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
        fire.forEach(projectile -> {
            meteors.put(projectile.toString());
        });
        data.put("fire", meteors);

        JSONArray entry = new JSONArray();
        declaredPower.forEach((player, power) -> {
            JSONObject obj = new JSONObject();
            JSONObject playerJSON = new JSONObject();
            JSONArray arr = new JSONArray();

            power.forEach((type, value) -> {
                obj.put("type", type);
                obj.put("value", value);
                arr.put(obj);
            });

            playerJSON.put(player.getName(), arr);
            entry.put(playerJSON);
        });

        data.put("declaredPower", entry);

        return data;
    }

    public static List<Card> construct(Model model, FlightBoard board){
        String out = dump(Warzone.class.getResourceAsStream("warzone.json"));
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
