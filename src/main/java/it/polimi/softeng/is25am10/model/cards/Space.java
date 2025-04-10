package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Space extends Card {
    private final Map<FlightBoard.Pawn, Integer> enginePower = new HashMap<>();
    private final Map<String, Integer> enginePowerName = new HashMap<>();

    private Space(Model model, FlightBoard board, int id) {
        super(model, true, board, id, Type.OPEN_SPACE);
    }

    @Override
    public Result<CardInput> set(Player player, CardInput input) {
        //begin
        //this section is the same for almost every card.
        if(isRegistered(player))
            return Result.err("player already registered");
        if(unexpected(player))
            return Result.err("player choice is not in order");
        //end

        int power = player.getBoard().getEnginePower(model.getRemovedItems(player).battery);
        enginePower.put(player.getPawn(), power);
        enginePowerName.put(player.getName(), power);

        register(player);
        return Result.ok(input);
    }


    @Override
    public Result<CardOutput> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end
        for(int i = flight.getOrder().size() - 1; i >= 0; i--){
            FlightBoard.Pawn p = flight.getOrder().get(i);
            flight.moveRocket(p, enginePower.get(p));
        }

        return Result.ok(new CardOutput());
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }

    @Override
    public CardData getData() {
        CardData data = new CardData(type, id);
        data.declaredPower = enginePowerName;
        return data;
    }

    public static List<Card> construct(Model model, FlightBoard board){
        String out = dump(Objects.requireNonNull(Space.class.getResourceAsStream("open_space.json")));
        JSONObject jsonObject = new JSONObject(out);
        JSONArray jsonArray = jsonObject.getJSONArray("ids");
        List<Card> cards = new ArrayList<>();

        jsonArray.forEach(item -> cards.add(new Space(model, board, Integer.parseInt(item.toString()))));

        return cards;
    }
}
