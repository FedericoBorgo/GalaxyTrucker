package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Smugglers extends Card {

    private final int days;
    private final int goods;
    private final int enemyPower;
    private boolean defeated = false;
    private Result<Player> winner = Result.err();
    private final List<GoodsBoard.Type> goodsGained;

    private Smugglers(Model model, FlightBoard board, int id, int daysLost, int smugglersDrillPower, int goodsLost, List<GoodsBoard.Type> goodsGained) {
        super( model, true, board, id, Type.SMUGGLERS );

        this.enemyPower = smugglersDrillPower;
        this.days = daysLost;
        this.goods = goodsLost;
        this.goodsGained = goodsGained;
    }


    @Override
    public Result<CardInput> set(Player player, CardInput input) {
        if(isRegistered(player))
            return Result.err("player already registered");
        if(unexpected(player))
            return Result.err("player choice is not in order");

        if(!input.disconnected) {
            if (model.batteryForCannon(player.getName()) > model.getRemoved(player).battery)
                return Result.err("not enough batteries used to activate the cannons");

            double power = player.getBoard().getCannonsPower(model.getCannonsToUse(player));

            if (power > enemyPower){
                defeated = true;
                if(input.accept)
                    winner = Result.ok(player);

            } else if (power < enemyPower && model.getRemoved(player).goods < goods)
                    return Result.err("player did not give enough goods to the smugglers");
        }
        register(player);
        return Result.ok(input);
    }


    @Override
    public boolean ready() {
        return (allRegistered() || defeated);
    }


    @Override
    public CardData getData() {
        CardData data = new CardData(type, id);
        data.goods = goods;
        data.days = days;
        data.power = enemyPower;
        return data;
    }

    @Override
    public Result<CardOutput> play() {
        // common part
        if(!ready())
            return Result.err("not all players declared their decision");

        CardOutput output = new CardOutput();

        winner.ifPresent(p -> {
            p.setGoodsReward(goodsGained);
            flight.moveRocket(p.getPawn(), -days);
            output.rewards.put(p.getName(), goodsGained);
        });

        return Result.ok(output);
    }

    public static List<Card> construct(Model model, FlightBoard board){
        String out = dump(Objects.requireNonNull(Smugglers.class.getResourceAsStream("smugglers.json")));
        JSONArray jsonCards = new JSONArray(out);
        List<Card> cards = new ArrayList<>();

        jsonCards.forEach(item -> {
            JSONObject entry = (JSONObject) item;
            int id = entry.getInt("id");
            int daysLost = entry.getInt("daysLost");
            int smugglersDrillPower = entry.getInt("smugglersDrillPower");
            int goodsLost = entry.getInt("goodsLost");
            JSONArray jsonArray = entry.getJSONArray("goodsGained");
            List<GoodsBoard.Type> goodsGained = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                goodsGained.add(GoodsBoard.Type.valueOf(jsonArray.getString(i)));
            }
            cards.add(new Smugglers(model, board, id, daysLost, smugglersDrillPower, goodsLost, goodsGained ));
        });
        return cards;

    }

}