package it.polimi.softeng.is25am10.model;

import java.util.Arrays;

public class Player {
    private final RocketPawn pawn;
    private String nickname;
    private int cash;
    private final ShipBoard board;
    private final ElementsBoard astronaut;
    private final ElementsBoard pAlien;
    private final ElementsBoard bAlien;
    private final ElementsBoard battery;
    private final ElementsBoard[] goods;

    /**
     * Constructs a Player with the specified RocketPawn and nickname.
     *
     * @param pawn the RocketPawn associated with the player
     * @param nickname the player's nickname
     */
    public Player(RocketPawn pawn, String nickname) {
        this.nickname = nickname;
        this.pawn = pawn;

        board = new ShipBoard();
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

    /**
     * Retrieves the current amount of cash the player has.
     *
     * @return the current cash amount as an integer
     */
    public int getCash() {
        return cash;
    }

    /**
     * Retrieves the ShipBoard associated with the player.
     *
     * @return the ShipBoard instance linked to the player
     */
    public ShipBoard getBoard() {
        return board;
    }

    /**
     * Retrieves the player's RocketPawn.
     *
     * @return the RocketPawn instance linked to the player
     */
    public RocketPawn getPawn() {
        return pawn;
    }

    /**
     * Adds the specified amount of cash to the player's balance.
     *
     * @param amount the amount of cash to add.
     */
    public void giveCash(int amount) {
        cash += amount;
    }

    /**
     * Sets the nickname of the player.
     *
     * @param nickname the nickname to be assigned
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
