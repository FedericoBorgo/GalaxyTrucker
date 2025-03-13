package it.polimi.softeng.is25am10.model;

import it.polimi.softeng.is25am10.model.boards.*;

import java.util.List;

/**
 * Stores all the information of the single player, associating it with a pawn and the nickname.
 */

public class Player {
    private final FlightBoard.RocketPawn pawn;
    private int cash;
    private final ShipBoard board;
    private List<GoodsBoard.Type> goodsReward;

    /**
     * Constructs a Player with the specified RocketPawn and nickname.
     *
     * @param pawn the RocketPawn associated with the player
     */
    public Player(FlightBoard.RocketPawn pawn) {
        this.pawn = pawn;
        board = new ShipBoard();
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
    public FlightBoard.RocketPawn getPawn() {
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

    public List<GoodsBoard.Type> getGoodsReward() {
        return goodsReward;
    }

    public void setGoodsReward(List<GoodsBoard.Type> goodsReward) {
        this.goodsReward = goodsReward;
    }
}
