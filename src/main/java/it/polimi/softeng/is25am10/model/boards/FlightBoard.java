package it.polimi.softeng.is25am10.model.boards;

import java.util.ArrayList;
import java.util.List;

/**
 * The board used by all the players, manages everything done on the flightboard in the physical game.
 * Includes methods for moving pawns
 */
public class FlightBoard {
    private int timer;

    private final List<RocketPawn> order;
    private final List<Integer> offset;
    private int leaderPosition;

    // Constructor method
    public FlightBoard() {
        this.timer = 0;
        this.order = new ArrayList<>();
        this.offset = new ArrayList<>();
        this.leaderPosition = 6;
    }

    // The Rocket pawn is positioned on the flight board

    /**
     * Method used to add the rocket pawn on the flight board by finishing order
     * @param pawn The pawn to be added
     */
    public void setRocketReady(RocketPawn pawn) {
        //TODO the player can chose which position
        int[] OFFSET = {0, -3, -5, -6};
        order.addLast(pawn);
        offset.addLast(OFFSET[offset.size()]);
    }

    /**
     * Flips the hourglass. This can be done by players only after the time from the previous
     * flip has run out.
     */
    public void moveTimer() {
        if(timer < 2) timer++;
    }

    /**
     *  Method for moving the rocket pawn in a new position, moves the {@code pawn} to the position
     *  {@code pos} while accounting for the order of the pawns and for possible overtakings of other pawns.
     * @param pawn the pawn to be moved.
     * @param pos the number of steps the pawn needs to take
     */
    public void moveRocket(RocketPawn pawn, int pos) {
        int index = order.indexOf(pawn);
        boolean positive = pos > 0;
        int steps = positive? -1 : 1;

        // if the move is positive, we go forward from the pawn to the leader.
        //if the move is negative, we go backwards from the pawn to the back.
        while((positive? index > 0 : index < order.size()-1) &&
                // the next (or previous) has a position lower (higher)
                // than the current pawn position + the position.
               (pos > 0? offset.get(index-1) <= offset.get(index) + pos:
                         offset.get(index+1) >= offset.get(index) + pos)){

                // swap the content of the 2 pawns
            swap(index + steps, index, offset);
            swap(index + steps, index, order);
            pos -= steps;
            index += steps;
        }

        //update the new pawn index
        offset.set(index, offset.get(index) + pos);

        //shift all the pawns
        int shift = offset.getFirst();
        offset.replaceAll(val -> val - shift);
        leaderPosition += shift;
        leaderPosition %= 24;
    }

    private static <T> void swap(int x, int y, List <T> list) {
        T s = list.get(x);
        list.set(x, list.get(y));
        list.set(y, s);
    }

    /**
     * Get method for the position of the hourglass.
     * @return the number of flips done on the hourglass.
     */
    public int getTimer() {
        return timer;
    }


    /**
     * Get method for the order of the pawns on the flightboard. The pawns are stored in an ordered list,
     * their position on the list is their order on the flightboard.
     *
     * @return list of the order of the pawns.
     */
    public List<RocketPawn> getOrder() {
        return order;
    }

    /**
     * Get method for the relative distances between pawns on the flightboard. It is used to account for
     * overtakings and for pawns lapping their opponents.
     * @return list of distances between each pawn and the leader.
     */
    public List<Integer> getOffset() {
        return offset;
    }

    /**
     * Get method for the absolute position of the leader on the flightboard.
     * @return leader position.
     */
    public int getLeaderPosition() {
        return leaderPosition;
    }

    /**
     * Enumerates the different rocket pawns which can be used by the player.
     * Includes an {@code EMPTY} value.
     */

    public enum RocketPawn {
        YELLOW, GREEN, BLUE, RED, EMPTY;
    }
}
