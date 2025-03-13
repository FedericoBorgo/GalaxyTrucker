package it.polimi.softeng.is25am10.model.boards;

import org.json.JSONArray;

import java.io.IOException;
import java.util.*;

public class ShipBoard {
    private final TilesBoard tiles;
    private final ElementsBoard astronaut;
    private final ElementsBoard purple;
    private final ElementsBoard brown;
    private final ElementsBoard battery;
    private final Map<GoodsBoard.Type, ElementsBoard> goods;

    public ShipBoard(){
        tiles = new TilesBoard();
        astronaut = new AstronautBoard(tiles);
        purple = new AlienBoard(tiles, AlienBoard.Type.PURPLE);
        brown = new AlienBoard(tiles, AlienBoard.Type.BROWN);
        battery = new BatteryBoard(tiles);

        goods = new HashMap<>();
        Arrays.stream(GoodsBoard.Type.values()).forEach(type -> {
            goods.put(type, new GoodsBoard(tiles, type));
        });

        astronaut.setOthers(Arrays.asList(brown, purple));
        purple.setOthers(Arrays.asList(brown, astronaut));
        brown.setOthers(Arrays.asList(purple, astronaut));

        Arrays.stream(GoodsBoard.Type.values()).forEach(type -> {
            List<ElementsBoard> other = new ArrayList<>(goods.values());
            other.remove(goods.get(type));
            goods.get(type).setOthers(other);
        });
    }

    public TilesBoard getTiles() {
        return tiles;
    }

    public ElementsBoard getAstronaut() {
        return astronaut;
    }

    public ElementsBoard getPurple() {
        return purple;
    }

    public ElementsBoard getBrown() {
        return brown;
    }

    public ElementsBoard getBattery() {
        return battery;
    }

    public ElementsBoard getGoods(GoodsBoard.Type type) {
        return goods.get(type);
    }

    public double getDrillsPower(List<Coordinate> activate){
        double drills = tiles.countDrillsPower(activate);

        if(drills > 0){
            drills += purple.getTotal()*2;
        }

        return drills;
    }

    public int getRocketPower(List<Coordinate> activate){
        int rocket = tiles.countRocketPower(activate);

        if(rocket > 0){
            rocket += brown.getTotal()*2;
        }

        return rocket;
    }

    private boolean thereIsSomeone(Coordinate c){
        return purple.get(c) + astronaut.get(c) + brown.get(c) > 0;
    }

    private void removeSomeone(Coordinate c){
        if(astronaut.get(c) > 0) {
            astronaut.remove(c, 1);
            return;
        }

        if(purple.get(c) > 0) {
            purple.remove(c, 1);
            return;
        }

        if(brown.get(c) > 0) {
            brown.remove(c, 1);
            return;
        }
    }

    private void toRemove(boolean[][] marked, Coordinate c){
        if(marked[c.x()][c.y()] || !thereIsSomeone(c))
            return;

        marked[c.x()][c.y()] = true;

        try {
            toRemove(marked, c.left());
        } catch (IOException _) {
        }
        try {
            toRemove(marked, c.right());
        } catch (IOException _) {
        }
        try {
            toRemove(marked, c.up());
        } catch (IOException _) {
        }
        try {
            toRemove(marked, c.down());
        } catch (IOException _) {
        }
    }

    public void epidemic(){
        boolean[][] marked = new boolean[TilesBoard.BOARD_WIDTH][TilesBoard.BOARD_HEIGHT];

        for(boolean[] b: marked)
            Arrays.fill(b, false);

        Coordinate.forEach(c ->  {
            toRemove(marked, c);
        });

        Coordinate.forEach(c -> {
            if(marked[c.x()][c.y()])
                removeSomeone(c);
        });
    }

    public void abandonCrew(JSONArray positionsCrew){
    }
}
