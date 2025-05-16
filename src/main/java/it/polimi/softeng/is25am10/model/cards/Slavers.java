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

public class Slavers extends Card {
    private final int cash;
    private final int days;
    private final int crew;
    private final int enemyPower;

    private boolean defeated = false;
    private Result<Player> winner = Result.err();

    private Slavers(Model model, FlightBoard board, int id, int cash, int days, int astronauts, int enemyPower) {
        super(model, true, board, id, Type.SLAVERS);
        this.cash = cash;
        this.days = days;
        this.crew = astronauts;
        this.enemyPower = enemyPower;
    }

    /**
     * checks if the player inputs are legal and sends error messages otherwise
     * @param player the player that execute the action
     * @param input this is dependent of every card
     * @return
     */
    @Override
    public Result<CardInput> set(Player player, CardInput input) {
        //begin
        //this section is the same for almost every card.
        if(isRegistered(player))
            return Result.err("player already registered");
        if(unexpected(player))
            return Result.err("player choice is not in order");
        //end

        if(!input.disconnected) {
            if (model.batteryForCannon(player.getName()) > model.getRemoved(player).battery)
                return Result.err("not enough batteries used to activate the cannons");

            double power = player.getBoard().getCannonsPower(model.getCannonsToUse(player));

            if (power > enemyPower) {
                defeated = true;

                if (input.accept)
                    winner = Result.ok(player);
            }
            else if (power < enemyPower)
                if (model.getRemoved(player).guys < crew && player.getBoard().getAstronaut().getTotal() > 0)
                    return Result.err("player did not give enough astronauts to the slavers");
        }

        register(player);
        return Result.ok(input);
    }

    /**
     * Gives rewards and makes changes according to player choice.
     * @return
     */
    @Override
    public Result<CardOutput> play() {
        // common part
        if(!ready())
            return Result.err("not all players declared their decision");

        CardOutput output = new CardOutput();

        winner.ifPresent(p -> {
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
        data.cash = cash;
        data.days = days;
        data.power = enemyPower;
        data.crew = crew;
        return data;
    }

    /**
     * This method is used to create the Slavers cards from json files.
     * @param model the model
     * @param board the board
     * @return a list of Slavers cards
     */
    public static List<Card> construct(Model model, FlightBoard board)
    {
        String out = dump(Objects.requireNonNull(Slavers.class.getResourceAsStream("slavers.json")));
        JSONArray jsonCards = new JSONArray(out);
        List<Card> cards = new ArrayList<>();

        jsonCards.forEach(item -> {
            JSONObject entry = (JSONObject) item;
            int id = entry.getInt("id");
            int cash = entry.getInt("cash");
            int days = entry.getInt("days");
            int guys = entry.getInt("guys");
            int power = entry.getInt("power");

            cards.add(new Slavers(model, board, id, cash, days, guys, power));
        });

        return cards;
    }

}
