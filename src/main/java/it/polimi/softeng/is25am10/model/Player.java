package it.polimi.softeng.is25am10.model;

public class Player {
    private final RocketPawn pawn;
    private String nickname;
    private int cash;
    private final ShipBoard board;
    private final ElementsBoard astronaut;
    private final ElementsBoard purpleAlien;
    private final ElementsBoard brownAlien;
    private final ElementsBoard battery;
    private final ElementsBoard[] goods;

    public Player(RocketPawn pawn, String nickname) {
        this.nickname = nickname;
        this.pawn = pawn;
        board = new ShipBoard();
        astronaut = new AstronautBoard(board);
        purpleAlien = new AlienBoard(board, 'p');
        brownAlien = new AlienBoard(board, 'b');
        battery = new BatteryBoard(board);
        goods = new GoodsBoard[4];
        goods[0] = new GoodsBoard(board, 'r');
        goods[1] = new GoodsBoard(board, 'b');
        goods[2] = new GoodsBoard(board, 'y');
        goods[3] = new GoodsBoard(board, 'g');
    }

    public int getCash() {
        return cash;
    }

    public ShipBoard getBoard() {
        return board;
    }

    public RocketPawn getPawn() {
        return pawn;
    }

    public void giveCash(int amount) {
        cash += amount;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
