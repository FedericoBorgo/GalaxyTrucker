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

public class AbandonedShip extends Card {
    private final int cash;
    private final int days;
    private final int crew;
    private Result<Player> winner = Result.err();

    private AbandonedShip(Model model, FlightBoard board, int id, int crew, int cash, int days) {
        super(model, true, board, id, Type.AB_SHIP);
        this.cash = cash;
        this.days = days;
        this.crew = crew;
    }

    /**
     * Manages the player interaction with the card according to the input.
     * @param player the player that executes the action
     * @param input this is dependent of every card
     * @return Result<CardInput> the result of the action
     */
    @Override
    public Result<CardInput> set(Player player, CardInput input) {
        if(isRegistered(player))
            return Result.err("il giocatore è già registrato");
        if(unexpected(player))
            return Result.err("la scelta del giocatore non è in ordine");

        //does the player want to land?
        if(input.accept) {
            // does the player remove enough crew?
            if (model.getRemoved(player).guys >= crew)
                winner = Result.ok(player);
            else
                return Result.err("non ci sono abbastanza astronauti");
        }

        register(player);
        return Result.ok(input);
    }

    /**
     * This method is called when the players have made their choices and the card is played.
     * @return Result<CardOutput> the result of the action
     */
    @Override
    public Result<CardOutput> play() {
        if(!ready())
            return Result.err("non tutti i giocatori hanno dichiarato la loro scelta");

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
        return winner.isOk() || allRegistered();
    }

    /**
     * This method is called to get the parameters for the card.
     * @return CardData the data of the card
     */
    @Override
    public CardData getData() {
        CardData data = new CardData(type, id);
        data.days = days;
        data.crew = crew;
        data.cash = cash;
        return data;
    }

    /**
     * This method is called to get parameters for the card from a json file.
     * @param model the model of the game
     * @param board the board of the game
     * @return List<Card> the list of cards
     */
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
