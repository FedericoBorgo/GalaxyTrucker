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
    private final int cashReward;
    private final int daysLost;
    private final int astronautCost;
    private final int enemyPower;
    private boolean enemyIsDefeated;
    private Result<Player> winnerPlayer;
    public Slavers(Model model, FlightBoard board, int id, int cash, int days, int astronauts, int enemyPower) {
        super(model, true, board, id, Type.SLAVERS);
        this.cashReward = cash;
        this.daysLost = days;
        this.astronautCost = astronauts;
        this.enemyPower = enemyPower;
        winnerPlayer = Result.err("nobody defeated the slavers");
        this.enemyIsDefeated = false;
    }

    @Override
    public Result<JSONObject> set(Player player, JSONObject json) {
        //begin
        //this section is the same for almost every card.
        if(isRegistered(player))
            return Result.err("player already registered");

        if(!isCorrectOrder(player)){
            return Result.err("player choice is not in order");
        }
        //end
        // Controllo se il nemico deve ancora essere sconfitto
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
            }
            else if (cannonsPower < enemyPower)
            {
                if(model.getRemovedItems(player).guys < astronautCost)
                    return Result.err("player did not give enough astronauts to the slavers");
            }
            // l'ultima alternativa è che il giocatore abbia pareggiato e non bisogna fare nulla
        }
        register(player);
        return Result.ok(genAccepted());
    }

    @Override
    public Result<JSONObject> play() {
        // common part
        if(!ready())
            return Result.err("not all players declared their decision");

        JSONObject result = new JSONObject();

        // Giocatore che ha vinto e accetta il premio sacrificando giorni di volo
        if(winnerPlayer.isOk())
        {
            Player p = winnerPlayer.getData();
            p.giveCash(cashReward);
            board.moveRocket(p.getPawn(), -daysLost);

            JSONObject rewarded = new JSONObject();
            rewarded.put(p.getName(), cashReward);
            result.put("cash", rewarded);
            result.put("flight", board.toJSON());
        }
        return Result.ok(result);
    }

    @Override
    public boolean ready() {
        return allRegistered() || enemyIsDefeated;
    }

    @Override
    public JSONObject getData() {
        JSONObject data = new JSONObject();
        data.put("type", type);
        data.put("id", id);
        return data;
    }

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

            cards.add(new AbandonedShip(model, board, id, guys, cash, days));
        });

        return cards;
    }

}
