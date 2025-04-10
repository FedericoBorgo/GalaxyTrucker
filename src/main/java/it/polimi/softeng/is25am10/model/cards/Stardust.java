package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Stardust extends Card {
    public Stardust(FlightBoard board, int id) {
        super(null, false, board, id, Type.STARDUST);
    }

    //not really needed
    @Override
    public Result<Input> set(Player player, Input input) {
        if(isRegistered(player))
            return Result.err("player already registered");
        register(player);
        return Result.ok(input);
    }

    /**
     * this moves the rocket pawns in reverse order
     * after having counted the number of exposed connectors
     * @return result type tells if it's been successful
     */
    @Override
    public Result<Output> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end


        FlightBoard.Pawn pawn;
        for(int i= board.getOrder().size()-1; i >= 0; i--){
            pawn = board.getOrder().get(i);
            int days = -registered.get(pawn).getBoard().getTiles().countExposedConnectors();
            board.moveRocket(pawn, days);
        }

        return Result.ok(new Output());
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }

    @Override
    public JSONObject getData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("id", id);
        return jsonObject;
    }

    public static List<Card> construct(FlightBoard board){
        String out = dump(Stardust.class.getResourceAsStream("stardust.json"));
        JSONObject jsonObject = new JSONObject(out);
        JSONArray jsonArray = jsonObject.getJSONArray("ids");
        List<Card> cards = new ArrayList<>();

        jsonArray.forEach(item -> {
            cards.add(new Stardust(board, Integer.parseInt(item.toString())));
        });

        return cards;
    }
}
