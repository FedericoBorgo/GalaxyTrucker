package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AbandonedShip extends Card {
    private final int cash;
    private final int days;
    private final int crew;
    private Optional<Player> winner = Optional.empty();

    private AbandonedShip(Model model, FlightBoard board, int id, int crew, int cash, int days) {
        super(model, true, board, id, Type.AB_SHIP);
        this.cash = cash;
        this.days = days;
        this.crew = crew;
    }

    @Override
    public Result<CardInput> set(Player player, CardInput input) {
        if(isRegistered(player))
            return Result.err("player already registered");
        if(unexpected(player))
            return Result.err("player choice is not in order");

        // the player want to descend?
        if(input.accept) {
            // the player remove enough crews?
            if (model.getRemovedItems(player).guys >= crew)
                winner = Optional.of(player);
            else
                return Result.err("not enough astronaut");
        }

        register(player);
        return Result.ok(input);
    }

    @Override
    public Result<CardOutput> play() {
        if(!ready())
            return Result.err("not all players declared their decision");

        CardOutput output = new CardOutput();

        // if someone accepted, give the cash and the removed days
        winner.ifPresent(p -> {
            p.giveCash(cash);
            flight.moveRocket(p.getPawn(), -days);
            output.cash.put(p.getName(), cash);
        });

        return Result.ok(output);
    }

    @Override
    public boolean ready() {
        return winner.isPresent() || allRegistered();
    }

    @Override
    public JSONObject getData() {
        JSONObject data = new JSONObject();
        data.put("type", type);
        data.put("id", id);
        return data;
    }

    public static List<Card> construct(Model model, FlightBoard board){
        String out = dump(Objects.requireNonNull(AbandonedShip.class.getResourceAsStream("abandoned_ship.json")));
        JSONArray jsonCards = new JSONArray(out);
        List<Card> cards = new ArrayList<>();

        jsonCards.forEach(item -> {
            JSONObject entry = (JSONObject) item;
            int id = entry.getInt("id");
            int days = entry.getInt("days");
            int guys = entry.getInt("guys");
            int cash = entry.getInt("cash");

            cards.add(new AbandonedShip(model, board, id, guys, cash, days));
        });

        return cards;
    }
}
