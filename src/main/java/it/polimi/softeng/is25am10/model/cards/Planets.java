package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Planets extends Card{
    private final Map<Planet, List<GoodsBoard.Type>> planets;
    private final Map<Player, Planet> chosenPlanet = new HashMap<>();
    private final int days;
    
    private boolean ready = false;

    public enum Planet{
        PLANET1, PLANET2, PLANET3, PLANET4, NOPLANET;

        int getID(){
            return switch (this){
                case PLANET1 -> 1;
                case PLANET2 -> 2;
                case PLANET3 -> 3;
                case PLANET4 -> 4;
                case NOPLANET -> 5;
            };
        }
    }

    private Planets(FlightBoard board, Map<Planet, List<GoodsBoard.Type>> goodsType, int id, int days) {
        super(null, true, board, id, Type.PLANETS);
        this.planets = goodsType;
        this.days = days;
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

        Planet planet = input.planet;

        //some player already took the planet?
        if(planet != Planet.NOPLANET && chosenPlanet.containsValue(planet))
            return Result.err("planet already occupied");

        if(!planets.containsKey(planet) && planet != Planet.NOPLANET)
            return Result.err("planet does not exist");

        chosenPlanet.put(player, planet);

        if(chosenPlanet.values().containsAll(planets.keySet()))
            ready = true;

        register(player);
        return Result.ok(input);
    }


    /**
     * This method is called when the card is played. It takes and gives items to the player according to their choice.
     * @return
     */
    @Override
    public Result<CardOutput> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end

        CardOutput output = new CardOutput();

        chosenPlanet.forEach((player, planet) -> {
            if(planet == Planet.NOPLANET)
                return;

            List<GoodsBoard.Type> reward = planets.get(planet);
            flight.moveRocket(player.getPawn(), -days);
            player.setGoodsReward(reward);
            output.rewards.put(player.getName(), reward);
        });

        return Result.ok(output);
    }

    @Override
    public boolean ready() {
        return ready || allRegistered();
    }

    @Override
    public CardData getData() {
        CardData data = new CardData(type, id);
        data.planets = planets;
        data.days = days;
        data.chosenPlanets = new ArrayList<>(chosenPlanet.values());
        return data;
    }

    /**
     * This method is used to construct the card from a json file. It reads the json file and creates a list of cards.
     * @param board
     * @return
     */
    public static List<Card> construct(FlightBoard board){
        String out = dump(Objects.requireNonNull(Planets.class.getResourceAsStream("planets.json")));
        JSONArray jsonCards = new JSONArray(out);
        List<Card> cards = new ArrayList<>();

        jsonCards.forEach(item -> {
            JSONObject entry = (JSONObject) item;
            int id = entry.getInt("id");
            int days = entry.getInt("days");
            Map<Planet, List<GoodsBoard.Type>> goods = new HashMap<>();

            for (Planet p: Planet.values()) {
                if(entry.has(p.name())){
                    List<GoodsBoard.Type> types = new ArrayList<>();

                    entry.getJSONArray(p.name()).forEach(box -> types.add(GoodsBoard.Type.valueOf(box.toString())));

                    goods.put(p, types);
                }
            }

            cards.add(new Planets(board, goods, id, days));
        });

        return cards;
    }
}
