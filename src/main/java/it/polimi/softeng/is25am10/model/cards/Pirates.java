package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.*;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Pirates extends Card {
    private final Map<Player, List<Integer>> useBattery = new HashMap<>();
    private final List<Projectile> projectiles;

    private Result<Player> rewarded = Result.err();
    private boolean defeated = false;

    private final List<Player> shotPlayers = new ArrayList<>();

    private final int cash;
    private final int days;
    private final int piratePower;

    public static int rollDice() {
        return new Random().nextInt(6) + 1;
    }


    private Pirates(Model model, FlightBoard board, List<Projectile.Type> fire, int id, int cash, int days, int piratePower) {
        super(model, true, board, id, Type.PIRATES);
        this.piratePower = piratePower;
        this.days = days;
        this.cash = cash;

        List<Pair<Tile.Side, Projectile.Type>> proj = new ArrayList<>();

        fire.forEach((p) -> {
            proj.add(new Pair<>(Tile.Side.UP, p));
        });

        projectiles = Meteors.genProjectiles(proj);
    }

    @Override
    public Result<CardInput> set(Player player, CardInput input) {
        if (isRegistered(player))
            return Result.err("player already registered");

        if (unexpected(player))
            return Result.err("player choice is not in order");

        // if the player is disconnected, he's automatically defeated
        if(input.disconnected)
            shotPlayers.add(player);
        else{
            //does the player dropped enough batteries?
            if(model.batteryForCannon(player.getName()) > model.getRemoved(player).battery)
                return Result.err("battery required");

            double power = player.getBoard().getCannonsPower(model.getCannonsToUse(player));

            if(power < piratePower) {
                if(input.shieldFor.size() > model.getRemoved(player).battery)
                    return Result.err("not enough battery required");

                useBattery.put(player, input.shieldFor);
                shotPlayers.add(player);
            }else if(power > piratePower) {
                defeated = true;

                if(input.accept)
                    rewarded = Result.ok(player);
            }
        }

        register(player);
        return Result.ok(input);
    }

    @Override
    public Result<CardOutput> play() {
        if (!ready())
            return Result.err("not all player declared their decision");

        CardOutput output = new CardOutput();

        // shoot the defeated players
        projectiles.forEach(proj -> shotPlayers.forEach((p) -> p.getBoard()
         .hit(proj, useBattery.get(p).contains(proj.ID()))
         .ifPresent(c -> output.addDestroyed(p.getName(), c))));

        // does the player want to be rewarded?
        rewarded.ifPresent(p -> {
            p.giveCash(cash);
            flight.moveRocket(p.getPawn(), -days);
            output.cash.put(p.getName(), cash);
        });

        return Result.ok(output);
    }

    @Override
    public boolean ready() {
        return allRegistered() || defeated;
    }

    @Override
    public CardData getData() {
        CardData data = new CardData(type, id);
        data.projectiles = projectiles;
        data.days = days;
        data.cash = cash;
        data.power = piratePower;
        return data;
    }


    public static List<Card> construct(Model model,FlightBoard board) {
        String out = dump(Objects.requireNonNull(Pirates.class.getResourceAsStream("pirates.json")));
        JSONArray jsonCards = new JSONArray(out);
        List<Card> cards = new ArrayList<>();

        jsonCards.forEach(item -> {
            JSONObject entry = (JSONObject) item;
            int id = entry.getInt("id");
            int cash = entry.getInt("cash");
            int days = entry.getInt("days");
            int power = entry.getInt("power");

            List<Projectile.Type> fire = new ArrayList<>();
            entry.getJSONArray("fire").forEach(str -> fire.add(Projectile.Type.valueOf(str.toString())));

            cards.add(new Pirates(model, board, fire, id, cash, days, power));
        });

        return cards;
    }
}
