package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class AbandonedStation extends Card {
    private List<GoodsBoard.Type> goods;
    private Map<Player, Boolean> playerChoice;
    private int requiredCrew;
    private int flightDays;
    private boolean ready = false;

    public AbandonedStation(FlightBoard board, int Crew, List<GoodsBoard.Type> goodsType, int id, int backmoves) {
        super(null, true, board, id);
        this.goods = goodsType;
        this.requiredCrew = Crew;
        this.flightDays = backmoves;

    }

    @Override
    public Result<Object> set(Player player, JSONObject json) {
        if(isRegistered(player))
            return Result.err("player already registered");

        if(!isCorrectOrder(player)){
            return Result.err("player choice is not in order");
        }

        boolean choice = getChoice(json);

        //enough crew?
        if(choice) {
            int crew = player.getBoard().getAstronaut().getTotal();
            if (crew < requiredCrew) {
                return Result.err("not enough crew");
            }else{
                ready = true;
            }
        }

        playerChoice.put(player, choice);
        register(player);

        return Result.ok(null);
    }

    @Override
    public Result<Object> play() {
        if(!ready())
            return Result.err("nobody chose yes");

        registered.forEach((pawn, player) -> {
            if(playerChoice.get(player)) {
                player.setGoodsReward(goods);
                board.moveRocket(player.getPawn(), flightDays);
            }
        });

        return Result.ok(null);
    }

    @Override
    public boolean ready() {
        return ready;
    }

    @Override
    public JSONObject getData() {
        return null;
    }
}
