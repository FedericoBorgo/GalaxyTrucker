package it.polimi.softeng.is25am10.model.boards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The board used by all the players, manages everything done on the flightboard in the physical game.
 * Includes methods for moving pawns
 */
public class FlightBoard implements Serializable {
    public static class CompressedFlightBoard{
        public final List<Pawn> order;
        public final List<Integer> offset;
        public final int leaderPosition;


        CompressedFlightBoard(List<Pawn> order, List<Integer> offset, int leaderPosition){
            this.order = Collections.unmodifiableList(order);
            this.offset = Collections.unmodifiableList(offset);
            this.leaderPosition = leaderPosition;
        }
    }

    private int timer;

    private final List<Pawn> order;
    private final List<Integer> offset;
    private int leaderPosition;
    private final List<Pawn> quitters;

    // Constructor method
    public FlightBoard() {
        this.timer = 0;
        this.order = new ArrayList<>();
        this.offset = new ArrayList<>();
        this.quitters = new ArrayList<>();
        this.leaderPosition = 6;
    }

    // The Rocket pawn is positioned on the flight board

    /**
     * Method used to add the rocket pawn on the flight board by finishing order
     * @param pawn The pawn to be added
     */
    public void setRocketReady(Pawn pawn) {
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
     * @param pos the target position of the pawn. If overtaking happens {@code pos} is not the new position
     *            of the pawn {@code pawn}.
     */
    public void moveRocket(Pawn pawn, int pos) {
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
        shift();
        checkDuped();
    }

    ///  normalize the offsets of the players
    private void shift(){
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
    public List<Pawn> getOrder() {
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

    public enum Pawn {
        YELLOW, GREEN, BLUE, RED, EMPTY;
    }

    public CompressedFlightBoard compress() {
        return new CompressedFlightBoard(order, offset, leaderPosition);
    }

    /**
     * The player quits the game. It can be intentional by
     * the player or an automatic decision from the model.
     * @param pawn the player to quit
     */
    public void quit(Pawn pawn) {
        offset.remove(order.indexOf(pawn));
        order.remove(pawn);
        quitters.add(pawn);
        shift();
    }

    ///  get the list of player quits
    public List<Pawn> getQuitters() {
        return quitters;
    }

    ///  check if the leader made a total lap over some player
    public void checkDuped(){
        for(int i = order.size()-1; i >= 0; i--)
            if(offset.get(i) <= -24)
                quit(order.get(i));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FlightBoard that = (FlightBoard) o;
        return timer == that.timer && leaderPosition == that.leaderPosition && Objects.equals(order, that.order) && Objects.equals(offset, that.offset) && Objects.equals(quitters, that.quitters);
    }
}
