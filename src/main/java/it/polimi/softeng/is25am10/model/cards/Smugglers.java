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


    /**
     * this method is called when the player chooses to play the card, it checks if the inputs are legal
     * and the card can be properly played
     * @param player
     * @param input
     * @return
     */
    @Override
    public Result<CardInput> set(Player player, CardInput input) {
        if(isRegistered(player))
            return Result.err("il giocatore è già registrato");
        if(unexpected(player))
            return Result.err("la scelta del giocatore non è in ordine");

        if(!input.disconnected) {
            if (model.batteryForCannon(player.getName()) > model.getRemoved(player).battery)
                return Result.err("non ci sono abbastanza batterie per attivare i cannoni");

            double power = player.getBoard().getCannonsPower(model.getCannonsToUse(player));

            if (power > enemyPower){
                defeated = true;
                if(input.accept)
                    winner = Result.ok(player);

            } else if (power < enemyPower && (model.getRemoved(player).goods < goods && model.getRemoved(player).goods + player.getBoard().getTotalGoods() >= goods))
                    return Result.err("il giocatore non ha dato abbastanza beni ai contrabandieri");
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
        data.rewards = goodsGained;
        return data;
    }

    /**
     * plays the card, gives the winner his rewards and sets the other changes.
     * @return
     */
    @Override
    public Result<CardOutput> play() {
        // common part
        if(!ready())
            return Result.err("non tutti i giocatori hanno dichiarato la loro decisione");

        CardOutput output = new CardOutput();

        winner.ifPresent(p -> {
            p.setGoodsReward(goodsGained);
            flight.moveRocket(p.getPawn(), -days);
            output.rewards.put(p.getName(), goodsGained);
        });

        return Result.ok(output);
    }

    /**
     * Constructs the smugglers cards from the json file.
     * @param model
     * @param board
     * @return
     */
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