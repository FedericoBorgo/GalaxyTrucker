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

    /**
     * The player that enters the abandoned ship.
     */
    private Optional<Player> descending = Optional.empty();

    public AbandonedShip(Model model, FlightBoard board, int id, int crew, int cash, int days) {
        super(model, true, board, id, Type.AB_SHIP);
        this.cash = cash;
        this.days = days;
        this.crew = crew;
    }

    @Override
    public Result<Input> set(Player player, Input input) {
        //begin
        //this section is the same for almost every card.
        if(isRegistered(player))
            return Result.err("player already registered");

        if(!isCorrectOrder(player)){
            return Result.err("player choice is not in order");
        }
        //end

        // the player want to descend?
        if(input.accept) {
            // the player remove enough crews?
            if (model.getRemovedItems(player).guys >= crew)
                descending = Optional.of(player);
            else
                return Result.err("not enough astronaut");
        }

        register(player);
        return Result.ok(input);
    }

    @Override
    public Result<Output> play() {
        // common part
        if(!ready())
            return Result.err("not all players declared their decision");

        Output output = new Output();

        // if someone accepted, give the cash and the removed days
        descending.ifPresent(player -> {
            player.giveCash(cash);
            board.moveRocket(player.getPawn(), -days);
            output.cash.put(player.getName(), cash);

        });

        return Result.ok(output);
    }

    @Override
    public boolean ready() {
        return descending.isPresent() || allRegistered();
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
