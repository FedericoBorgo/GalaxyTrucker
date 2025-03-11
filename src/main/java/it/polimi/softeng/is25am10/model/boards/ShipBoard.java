package it.polimi.softeng.is25am10.model.boards;

import javafx.util.Pair;

import java.util.*;

public class ShipBoard {
    private final TilesBoard board;
    private final ElementsBoard astronaut;
    private final ElementsBoard pAlien;
    private final ElementsBoard bAlien;
    private final ElementsBoard battery;
    private final Map<GoodsBoard.Type, ElementsBoard> goods;

    public ShipBoard(){
        board = new TilesBoard();
        astronaut = new AstronautBoard(board);
        pAlien = new AlienBoard(board, AlienBoard.Type.BROWN);
        bAlien = new AlienBoard(board, AlienBoard.Type.PURPLE);
        battery = new BatteryBoard(board);

        goods = new HashMap<>();
        Arrays.stream(GoodsBoard.Type.values()).forEach(type -> {
            goods.put(type, new GoodsBoard(board, type));
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

    public TilesBoard getBoard() {
        return board;
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
        double drills = board.countDrillsPower(activate);

        if(drills > 0){
            drills += pAlien.getTotal()*2;
        }

        return drills;
    }

    public double getRocketPower(List<Coordinate> activate){
        double rocket = board.countRocketPower(activate);

        if(rocket > 0){
            rocket += bAlien.getTotal()*2;
        }

        return rocket;
    }
}
