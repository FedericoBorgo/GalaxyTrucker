package it.polimi.softeng.is25am10.model.boards;


import it.polimi.softeng.is25am10.model.Projectile;
import it.polimi.softeng.is25am10.model.Tile;

import java.io.IOException;
import java.util.*;

public class ShipBoard {
    public static class CompressedShipBoard{
        public final Tile[][] board;
        public final Tile.Rotation[][] rotation;

        public final Map<Coordinate, Integer> astronaut;
        public final Map<Coordinate, Integer> purple;
        public final Map<Coordinate, Integer> brown;
        public final Map<Coordinate, Integer> battery;
        public final Map<GoodsBoard.Type, Map<Coordinate, Integer>> goods;

        CompressedShipBoard(Tile[][] board, Tile.Rotation[][] rotation,
                            Map<Coordinate, Integer> astronaut,
                            Map<Coordinate, Integer> purple,
                            Map<Coordinate, Integer> brown,
                            Map<Coordinate, Integer> battery,
                            Map<GoodsBoard.Type, Map<Coordinate, Integer>> goods){
            this.board = board;
            this.rotation = rotation;
            this.astronaut = astronaut;
            this.purple = purple;
            this.brown = brown;
            this.battery = battery;
            this.goods = goods;
        }
    }

    public enum CrewType{
        ASTRONAUT, B_ALIEN, P_ALIEN
    }

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

    public CompressedShipBoard compress(){
        Map<GoodsBoard.Type, Map<Coordinate, Integer>> goodMap = new HashMap<>();

        goods.forEach((type, board) -> {
            goodMap.put(type, board.positions);
        });

        return new CompressedShipBoard(tiles.getBoard(), tiles.getRotation(),
                astronaut.getPositions(),
                purple.getPositions(),
                brown.getPositions(),
                battery.getPositions(), goodMap);
    }

    public void removeIllegals(){
        astronaut.removeIllegals();
        purple.removeIllegals();
        brown.removeIllegals();
        battery.removeIllegals();
        goods.forEach((type, board) -> {
            board.removeIllegals();
        });
    }

    public void hit(Projectile projectile, boolean useBattery) {

    }

    public boolean checkEnough(Coordinate c, CrewType crewType, int qty){
        return qty <= switch(crewType){
            case ASTRONAUT -> astronaut.get(c);
            case B_ALIEN -> brown.get(c);
            case P_ALIEN -> purple.get(c);
        };
    }
}
