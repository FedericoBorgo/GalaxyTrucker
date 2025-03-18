package it.polimi.softeng.is25am10.model;

import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Stores all the information of the single player, associating it with a pawn and the nickname.
 */

public class Player implements Serializable {
    private final FlightBoard.Pawn pawn;
    private int cash;
    private final ShipBoard board;
    private List<GoodsBoard.Type> goodsReward;
    private final String name;

    /**
     * Constructs a Player with the specified RocketPawn and nickname.
     *
     * @param pawn the RocketPawn associated with the player
     */
    public Player(String name, FlightBoard.Pawn pawn) {
        this.name = name;
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
    public FlightBoard.Pawn getPawn() {
        return pawn;
    }

    /**
     * Retrieves the goodsReward associated with the player. Needed for some card effects.
     * @return List of goods
     */
    public List<GoodsBoard.Type> getGoodsReward() {
        return goodsReward;
    }

    /**
     * Retrieves the nickname of the player
     * @return string nickname
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the goodsReward field for the player.
     * @param goodsReward list
     */
    public void setGoodsReward(List<GoodsBoard.Type> goodsReward) {
        this.goodsReward = goodsReward;
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
     * Places a good from the goodsReward list of the player at a specific coordinate.
     * @param reward good to be placed.
     * @param c coordinate of the storage.
     * @return a successful {@code Result} or an error {@code Result} with a message explaining
     * why the operation failed.
     */
    public Result<Integer> placeReward(GoodsBoard.Type reward, Coordinate c) {
        if(!this.goodsReward.contains(reward))
            return Result.err("can't place goods that aren't in the goodsReward list");

        Result<Integer> res = board.getGoods(reward).put(c, 1);
        if(res.isErr())
            return res;
        goodsReward.remove(reward);
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return cash == player.cash && pawn == player.pawn && Objects.equals(board, player.board) && Objects.equals(goodsReward, player.goodsReward) && Objects.equals(name, player.name);
    }
}
