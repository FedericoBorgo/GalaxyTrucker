package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Stardust extends Card {
    private Stardust(FlightBoard board, int id) {
        super(null, false, board, id, Type.STARDUST);
    }

    /**
     * checks that the card can be played
     * @param player the player that executes the action
     * @param input this is dependent of every card
     * @return
     */
    @Override
    public Result<CardInput> set(Player player, CardInput input) {
        if(isRegistered(player))
            return Result.err("il giocatore è già registrato");
        register(player);
        return Result.ok(input);
    }

    /**
     * Makes the changes consequent to the card being played.
     * @return
     */
    @Override
    public Result<CardOutput> play() {
        //begin common part
        if(!ready())
            return Result.err("non tutti i giocatori hanno dichiarato la loro decisione");
        //end

        FlightBoard.Pawn pawn;
        for(int i = flight.getOrder().size()-1; i >= 0; i--){
            pawn = flight.getOrder().get(i);
            int days = -registered.get(pawn).getBoard().getTiles().countExposedConnectors();
            flight.moveRocket(pawn, days);
        }

        return Result.ok(new CardOutput());
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }

    @Override
    public CardData getData() {
        return new CardData(type, id);
    }

    /**
     * Creates the card from the json file.
     * @param board
     * @return
     */
    public static List<Card> construct(FlightBoard board){
        String out = dump(Objects.requireNonNull(Stardust.class.getResourceAsStream("stardust.json")));
        JSONObject jsonObject = new JSONObject(out);
        JSONArray jsonArray = jsonObject.getJSONArray("ids");
        List<Card> cards = new ArrayList<>();

        jsonArray.forEach(item -> cards.add(new Stardust(board, Integer.parseInt(item.toString()))));

        return cards;
    }
}
