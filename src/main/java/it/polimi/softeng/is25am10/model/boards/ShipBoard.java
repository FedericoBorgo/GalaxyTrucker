package it.polimi.softeng.is25am10.model.boards;

import java.util.Arrays;

public class ShipBoard {
    private final TilesBoard board;
    private final ElementsBoard astronaut;
    private final ElementsBoard pAlien;
    private final ElementsBoard bAlien;
    private final ElementsBoard battery;
    private final ElementsBoard[] goods;

    public ShipBoard(){
        board = new TilesBoard();
        astronaut = new AstronautBoard(board);
        pAlien = new AlienBoard(board, 'p');
        bAlien = new AlienBoard(board, 'b');
        battery = new BatteryBoard(board);
        goods = new GoodsBoard[4];

        // a GoodsBoard for every type of good
        goods[0] = new GoodsBoard(board, 'r');
        goods[1] = new GoodsBoard(board, 'b');
        goods[2] = new GoodsBoard(board, 'y');
        goods[3] = new GoodsBoard(board, 'g');

        astronaut.setOthers(Arrays.asList(bAlien, pAlien));
        pAlien.setOthers(Arrays.asList(bAlien, astronaut));
        bAlien.setOthers(Arrays.asList(pAlien, astronaut));
        goods[0].setOthers(Arrays.asList(goods[1], goods[2], goods[3]));
        goods[1].setOthers(Arrays.asList(goods[0], goods[2], goods[3]));
        goods[2].setOthers(Arrays.asList(goods[0], goods[1], goods[3]));
        goods[3].setOthers(Arrays.asList(goods[0], goods[1], goods[2]));
    }
}
