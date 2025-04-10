package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Smugglers extends Card {

    private final int days;
    private final int goods;
    private final int enemyPower;
    private boolean defeated = false;
    private Optional<Player> winner = Optional.empty();
    List<GoodsBoard.Type> goodsGained;

    private Map<Player, Map<Tile.Rotation, Integer>> playerChoice;

    public Smugglers(Model model, FlightBoard board, int id, int daysLost, int smugglersDrillPower, int goodsLost, List<GoodsBoard.Type> goodsGained) {
        super( model, true, board, id, Type.SMUGGLERS );

        this.enemyPower = smugglersDrillPower;
        this.days = daysLost;
        this.goods = goodsLost;
        this.goodsGained = goodsGained;
    }


    @Override
    public Result<Input> set(Player player, Input input) {
        if(isRegistered(player))
            return Result.err("player already registered");
        if(!isCorrectOrder(player)){
            return Result.err("player choice is not in order");
        }

        if(!input.disconnected) {
            if (model.batteryRequiredForCannon(player.getName()) > model.getRemovedItems(player).battery)
                return Result.err("not enough batteries used to activate the cannons");

            double power = player.getBoard().getCannonsPower(model.getCannonsToUse(player));

            if (power > enemyPower){
                defeated = true;
                if(input.accept)
                    winner = Optional.of(player);

            } else if (power < enemyPower && model.getRemovedItems(player).goods < goods)
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
    public JSONObject getData() {
        JSONObject data = new JSONObject();
        data.put("type", type);
        data.put("id", id);
        return data;
    }

    @Override
    public Result<Output> play() {
        // common part
        if(!ready())
            return Result.err("not all players declared their decision");

        Output output = new Output();

        winner.ifPresent(p -> {
            p.setGoodsReward(goodsGained);
            board.moveRocket(p.getPawn(), -days);
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