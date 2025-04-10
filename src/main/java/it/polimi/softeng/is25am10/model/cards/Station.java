package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Station extends Card {
    private final List<GoodsBoard.Type> goods;
    private Optional<Player> winner = Optional.empty();
    private final int crew;
    private final int days;

    private Station(FlightBoard board, int crew, List<GoodsBoard.Type> goodsType, int id, int days) {
        super(null, true, board, id, Type.STATION);
        this.goods = goodsType;
        this.crew = crew;
        this.days = days;

    }

    @Override
    public Result<CardInput> set(Player player, CardInput input) {
        if(isRegistered(player))
            return Result.err("player already registered");
        if(unexpected(player))
            return Result.err("player choice is not in order");

        //enough crew?
        if(input.accept) {
            int crew = player.getBoard().getAstronaut().getTotal();

            if (crew < this.crew)
                return Result.err("not enough crew");

            winner = Optional.of(player);
        }

        register(player);

        return Result.ok(input);
    }

    @Override
    public Result<CardOutput> play() {
        if(!ready())
            return Result.err("nobody chose yes");

        CardOutput output = new CardOutput();

        winner.ifPresent(player -> {
            player.setGoodsReward(goods);
            flight.moveRocket(player.getPawn(), -days);
            output.rewards.put(player.getName(), goods);
        });

        return Result.ok(output);
    }

    @Override
    public boolean ready() {
        return winner.isPresent() || allRegistered();
    }

    @Override
    public CardData getData() {
        CardData data = new CardData(type, id);
        data.days = days;
        data.crew = crew;
        data.rewards = goods;
        return data;
    }

    public static List<Card> construct(FlightBoard board){
        String out = dump(Objects.requireNonNull(Station.class.getResourceAsStream("station.json")));
        JSONArray jsonCards = new JSONArray(out);
        List<Card> cards = new ArrayList<>();

        jsonCards.forEach(item -> {
            JSONObject entry = (JSONObject) item;
            int id = entry.getInt("id");
            int days = entry.getInt("days");
            int guys = entry.getInt("guys");
            List<GoodsBoard.Type> types = new ArrayList<>();

            entry.getJSONArray("goods").forEach(type -> types.add(GoodsBoard.Type.valueOf(type.toString())));

            cards.add(new Station(board, guys, types, id, days));
        });

        return cards;
    }
}
