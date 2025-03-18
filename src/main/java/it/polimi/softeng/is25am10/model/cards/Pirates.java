package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.*;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Pirates extends Card {
    private final Map<Player, List<Integer>> useBattery = new HashMap<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private Player rewardedPlayer = null;
    private boolean defeated = false;
    private List<Player> defeatedPlayers = new ArrayList<>();
    private int cash;
    private int days;
    private final int piratePower;

    public static int rollDice() {
        return new Random().nextInt(6) + 1;
    }
    /**
     * Creates the card by giving the model from which to get player data, such as
     * the number of removed batteries, astronauts or goods.
     *
     * @param model     where to get the player data
     * @param board     flight board of the game
     * @param id        unique identification of the card
     * @param cash      player's reward if he wins
     * @param days      pawn's backward moves if the player accepts the reward
     */
    public Pirates(Model model, FlightBoard board, List<Projectile.Type> fire, int id, int cash, int days, int piratePower) {
        super(model, true, board, id, Type.PIRATES);
        this.piratePower = piratePower;
        this.days = days;
        this.cash = cash;
        AtomicInteger counter = new AtomicInteger();
        counter.set(0);

        fire.forEach(type -> {
            int number = rollDice() + rollDice();
            Projectile p = new Projectile(type, Tile.Side.UP, number, counter.getAndIncrement());
            projectiles.add(p);
        });
    }

    @Override
    public Result<JSONObject> set(Player player, JSONObject json) {
        if (isRegistered(player))
            return Result.err("player already registered");

        if (!isCorrectOrder(player))
            return Result.err("player choice is not in order");

        if(model.batteryRequiredForCannon(player.getName()) > model.getRemovedItems(player).battery)
            return Result.err("battery required");

        double power = player.getBoard().getCannonsPower(model.getCannonsToUse(player));
        if(power < piratePower) {
            if(!json.has("use"))
                return Result.err("missing use");

            JSONArray array = json.getJSONArray("use");
            if(array.length() > model.getRemovedItems(player).battery)
                return Result.err("not enough battery required");

            List<Integer> use = new ArrayList<>();

            array.forEach(item -> {
                use.add(Integer.parseInt(item.toString()));
            });

            useBattery.put(player, use);
            defeatedPlayers.add(player);
        }else if(power > piratePower) {
            defeated = true;

            if(getChoice(json))
                rewardedPlayer = player;
        }
        register(player);
        return Result.ok(genAccepted());
    }

    @Override
    public Result<JSONObject> play() {
        if (!ready())
            return Result.err("not all player declared their decision");

        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject moved = new JSONObject();

        projectiles.forEach(projectile -> {
            defeatedPlayers.forEach(( p) -> {
                Optional<Coordinate> destroyed = p.getBoard().hit(projectile, useBattery.get(p).contains(projectile.ID()));
                destroyed.ifPresent(c -> {
                    JSONObject obj = new JSONObject();
                    obj.put("name", p.getName());
                    obj.put("where", c.toString());
                    array.put(obj);
                });
            });
        });

        if(rewardedPlayer != null){
            rewardedPlayer.giveCash(cash);
            board.moveRocket(rewardedPlayer.getPawn(), -days);
            moved.put("pawn", rewardedPlayer.getPawn());
            moved.put("days", -days);
        }

        result.put("destroyed", array);
        result.put("moved", moved);
        result.put("cash", cash);
        return Result.ok(result);
    }

    @Override
    public boolean ready() {
        return allRegistered() || defeated;
    }

    @Override
    public JSONObject getData() {
        JSONObject json = new JSONObject();
        JSONArray fires = new JSONArray();
        projectiles.forEach(projectile -> {
            fires.put(projectile.toString());
        });
        json.put("pirates", fires);
        return json;
    }


    public static List<Card> construct(Model model,FlightBoard board) {
        String out = dump(Pirates.class.getResourceAsStream("pirates.json"));
        JSONArray jsonCards = new JSONArray(out);
        List<Card> cards = new ArrayList<>();

        jsonCards.forEach(item -> {
            JSONObject entry = (JSONObject) item;
            int id = entry.getInt("id");
            int cash = entry.getInt("cash");
            int days = entry.getInt("days");
            int power = entry.getInt("power");

            List<Projectile.Type> fire = new ArrayList<>();
            entry.getJSONArray("fire").forEach(str -> {
                fire.add(Projectile.Type.valueOf(str.toString()));
            });

            cards.add(new Pirates(model, board, fire, id, cash, days, power));
        });

        return cards;
    }
}
