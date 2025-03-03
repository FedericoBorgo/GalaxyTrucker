package it.polimi.softeng.is25am10.model;

public class Player {
    private final RocketPawn pawn;
    private String nickname;
    private int cash;
    private final ShipBoard board;
    private final AstronautBoard astronaut;
    private final PurpleAlienBoard purpleAlien;
    private final BrownAlienBoard brownAlien;
    private final BatteryBoard battery;
    private final GoodsBoard goods;

    public Player(RocketPawn pawn, String nickname) {
        this.nickname = nickname;
        this.pawn = pawn;
        board = new ShipBoard();
        astronaut = new AstronautBoard(board);
        purpleAlien = new PurpleAlienBoard(board);
        brownAlien = new BrownAlienBoard(board);
        battery = new BatteryBoard(board);
        goods = new GoodsBoard();
    }

    public void giveCash(int amount) {
        cash += amount;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public RocketPawn getPawn() {
        return pawn;
    }

    public String getNickname() {
        return nickname;
    }

    public int getCash() {
        return cash;
    }

    public ShipBoard getBoard() {
        return board;
    }

    public AstronautBoard getAstronaut() {
        return astronaut;
    }

    public PurpleAlienBoard getPurpleAlien() {
        return purpleAlien;
    }

    public BrownAlienBoard getBrownAlien() {
        return brownAlien;
    }

    public BatteryBoard getBattery() {
        return battery;
    }

    public GoodsBoard getGoods() {
        return goods;
    }
}
