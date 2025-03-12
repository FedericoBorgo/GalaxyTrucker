package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenSpace extends Card {
    private final Map<FlightBoard.RocketPawn, Integer> enginePower = new HashMap<>();
    public OpenSpace(int id) {
        super(true, id);
    }

    @Override
    public Result<Object> set(Player player, JSONObject json) {
        //begin
        //this section is the same for almost every card.
        if(isRegistered(player))
            return Result.err("player already registered");

        if(!isCorrectOrder(player)){
            return Result.err("player choice is not in order");
        }
        //end

        // Prendo le coordinate dal JSON: Coordinates-be: [
        // { "coordinateBattery": "x2y4", "coordinateEngine": "x5y5" },
        // { "coordinateBattery": "x1y2", "coordinateEngine": "x4y4" }
        JSONArray coordinatesArray = json.getJSONArray("Coordinates-be");
        List<Coordinate> coordinatesBattery = new ArrayList<>();
        List<Coordinate> coordinatesEngines = new ArrayList<>();
        int numActiveEngines = 0;
        List<Coordinate> activeEngines = new ArrayList<>();

        for (int i = 0; i < coordinatesArray.length(); i++) {
            JSONObject cord = coordinatesArray.getJSONObject(i);

            // Coordinata batteria
            String batteryCoords = cord.getString("coordinateBattery");
            Result<Coordinate> tempCord = fromStringToCoordinate(batteryCoords);
            if(tempCord.isErr())
                return Result.err("invalid coordinate");
            coordinatesBattery.add(tempCord.getData());

            // Coordinata motore
            String engineCoords = cord.getString("coordinateEngine");
            tempCord = fromStringToCoordinate(engineCoords);
            if(tempCord.isErr())
                return Result.err("invalid coordinate");
            coordinatesEngines.add(tempCord.getData());
        }

        // Abbiamo coordinatesBattery={coordB1, coordB2} e coordinatesEngines={coordE1, coordE2}
        for (Coordinate coordinate : coordinatesBattery) {
            Result<Integer> integerResult = player.getBoard().getBattery().remove(coordinate, 1);
            if (integerResult.isErr())
                return Result.err("Le batterie in quelle coordinate non sono sufficienti");
            // Le batterie bastano
            activeEngines.add(coordinatesEngines.get(numActiveEngines));
            numActiveEngines++;
        }
        enginePower.put(player.getPawn(), player.getBoard().getRocketPower(activeEngines));
        register(player);
        return Result.ok(activeEngines);
    }


    @Override
    public Result<Object> play() {
        //begin common part
        if(!ready())
            return Result.err("not all player declared their decision");
        //end
        for(FlightBoard.RocketPawn p : board.getOrder())
        {
            board.moveRocket(p, enginePower.get(p));
        }
        return Result.ok(null);
    }

    @Override
    public boolean ready() {
        return allRegistered();
    }

    static private Result<Coordinate> fromStringToCoordinate(String s) {
        if(s.length() != 4 || s.charAt(0) != 'x' || s.charAt(2) != 'y' || !Character.isDigit(s.charAt(1)) || !Character.isDigit(s.charAt(3)))
            return Result.err("string is not a coordinate");
        else
            return Result.ok(new Coordinate(Character.getNumericValue(s.charAt(1)), Character.getNumericValue(s.charAt(3))));
    }
}
