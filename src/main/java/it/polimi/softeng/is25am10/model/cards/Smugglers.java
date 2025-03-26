package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Smugglers extends Card {

    private final int daysLost;
    private final int goodsLost;
    private final int enemyPower;
    private boolean enemyIsDefeated;
    private Result<Player> winnerPlayer;
    List<GoodsBoard.Type> goodsGained;

    private Map<Player, Map<Tile.Rotation, Integer>> playerChoice;

    public Smugglers(Model model, FlightBoard board, int id, int daysLost, int smugglersDrillPower, int goodsLost, List<GoodsBoard.Type> goodsGained) {
        super( model, true, board, id, Type.SMUGGLERS );

        this.enemyPower = smugglersDrillPower;
        this.daysLost = daysLost;
        this.goodsLost = goodsLost;
        this.goodsGained = goodsGained;
        enemyIsDefeated = false;
        winnerPlayer = Result.err("No winner");
    }


    @Override
    public Result<JSONObject> set(Player player, JSONObject json) {
        if(isRegistered(player))
            return Result.err("player already registered");
        if(!isCorrectOrder(player)){
            return Result.err("player choice is not in order");
        }
        if(!enemyIsDefeated) {
            // Controllo se il giocatore ha buttato abbastanza batterie
            if (model.batteryRequiredForCannon(player.getName()) > model.getRemovedItems(player).battery)
                return Result.err("not enough batteries used to activate the cannons");
            double cannonsPower = player.getBoard().getCannonsPower(model.getCannonsToUse(player));
            if (cannonsPower > enemyPower){
                enemyIsDefeated = true;
                if(this.getChoice(json))
                    winnerPlayer = Result.err("player does not want the reward");
                else
                    winnerPlayer = Result.ok(player);
            } else if (cannonsPower < enemyPower)
            {
                if(model.getRemovedItems(player).goods < goodsLost)
                    return Result.err("player did not give enough goods to the smugglers");
            }
        }
        register(player);
        return Result.ok(genAccepted());
    }


    @Override
    public boolean ready() {
        return (allRegistered() || enemyIsDefeated);
    }


    @Override
    public JSONObject getData() {
        JSONObject data = new JSONObject();
        data.put("type", type);
        data.put("id", id);
        return data;
    }

    @Override
    public Result<JSONObject> play() {
        // common part
        if(!ready())
            return Result.err("not all players declared their decision");

        JSONObject result = new JSONObject();

        if(winnerPlayer.isOk()){
            Player p = winnerPlayer.getData();
            p.setGoodsReward(goodsGained);
            board.moveRocket(p.getPawn(), -daysLost);
        }
        result.put("flight", board.toJSON());
        return null;

    }

    public static List<Card> construct(Model model, FlightBoard board)
    {
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