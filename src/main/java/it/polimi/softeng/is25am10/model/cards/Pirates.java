package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.*;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Pirates extends Card {
    private final Map<Player, List<Integer>> useBattery = new HashMap<>();
    private final List<Projectile> projectiles = new ArrayList<>();

    private Player rewarded = null;
    private boolean defeated = false;

    private final List<Player> shotPlayers = new ArrayList<>();

    private final int cash;
    private final int days;
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
    public Result<Input> set(Player player, Input input) {
        if (isRegistered(player))
            return Result.err("player already registered");

        if (!isCorrectOrder(player))
            return Result.err("player choice is not in order");

        // if the player is disconnected, he's automatically defeated
        if(input.disconnected)
            shotPlayers.add(player);
        else{
            //does the player dropped enough batteries?
            if(model.batteryRequiredForCannon(player.getName()) > model.getRemovedItems(player).battery)
                return Result.err("battery required");

            double power = player.getBoard().getCannonsPower(model.getCannonsToUse(player));

            if(power < piratePower) {
                if(input.shieldFor.size() > model.getRemovedItems(player).battery)
                    return Result.err("not enough battery required");

                useBattery.put(player, input.shieldFor);
                shotPlayers.add(player);
            }else if(power > piratePower) {
                defeated = true;

                if(input.accept)
                    rewarded = player;
            }
        }

        register(player);
        return Result.ok(input);
    }

    @Override
    public Result<Output> play() {
        if (!ready())
            return Result.err("not all player declared their decision");

        Output output = new Output();

        // shoot the defeated players
        projectiles.forEach(proj -> {
            shotPlayers.forEach((p) -> {
                p.getBoard()
                 .hit(proj, useBattery.get(p).contains(proj.ID()))
                 .ifPresent(c -> {output.removed.put(p.getName(), c);});
            });
        });

        // does the player want to be rewarded?
        if(rewarded != null){
            rewarded.giveCash(cash);
            board.moveRocket(rewarded.getPawn(), -days);
            output.cash.put(rewarded.getName(), cash);
        }

        return Result.ok(output);
    }

    @Override
    public boolean ready() {
        return allRegistered() || defeated;
    }

    @Override
    public JSONObject getData() {
        JSONObject json = new JSONObject();
        JSONArray fires = new JSONArray();
        json.put("type", type);
        json.put("id", id);
        projectiles.forEach(projectile -> {
            fires.put(projectile.toString());
        });
        json.put("fires", fires);
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
