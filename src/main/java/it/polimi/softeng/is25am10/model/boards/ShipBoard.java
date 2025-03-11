package it.polimi.softeng.is25am10.model.boards;

import java.util.*;

public class ShipBoard {
    private final TilesBoard tiles;
    private final ElementsBoard astronaut;
    private final ElementsBoard pAlien;
    private final ElementsBoard bAlien;
    private final ElementsBoard battery;
    private final Map<GoodsBoard.Type, ElementsBoard> goods;

    public ShipBoard(){
        tiles = new TilesBoard();
        astronaut = new AstronautBoard(tiles);
        pAlien = new AlienBoard(tiles, AlienBoard.Type.BROWN);
        bAlien = new AlienBoard(tiles, AlienBoard.Type.PURPLE);
        battery = new BatteryBoard(tiles);

        goods = new HashMap<>();
        Arrays.stream(GoodsBoard.Type.values()).forEach(type -> {
            goods.put(type, new GoodsBoard(tiles, type));
        });

        astronaut.setOthers(Arrays.asList(bAlien, pAlien));
        pAlien.setOthers(Arrays.asList(bAlien, astronaut));
        bAlien.setOthers(Arrays.asList(pAlien, astronaut));

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

    public ElementsBoard getPurpleAlien() {
        return pAlien;
    }

    public ElementsBoard getBrownAlien() {
        return bAlien;
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
            drills += pAlien.getTotal()*2;
        }

        return drills;
    }

    public int getRocketPower(List<Coordinate> activate){
        int rocket = tiles.countRocketPower(activate);

        if(rocket > 0){
            rocket += bAlien.getTotal()*2;
        }

        return rocket;
    }

    public void epidemic(){
        //pulls out the dead crew
    }
}
