package it.polimi.softeng.is25am10.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TO BE WRITTEN
 */
public class FlightBoard {
    private int timer;
    private final Card[][] visible;
    private final List<Card> deck;

    private final List<RocketPawn> order;
    private final List<Integer> offset;
    private int leaderPosition;

    // Constructor method
    public FlightBoard() {
        this.timer = 0;
        this.visible = new Card[3][3];
        this.deck = new ArrayList<>();
        this.order = new ArrayList<>();
        this.offset = new ArrayList<>();
        this.leaderPosition = 6;
    }

    // The Rocket pawn is positioned on the flight board
    public void setRocketReady(RocketPawn pawn) {
        int[] OFFSET = {0, -3, -5, -6};
        order.addLast(pawn);
        offset.addLast(OFFSET[offset.size()]);
    }

    // Increases the position of the hourglass
    public void moveTimer() {
        if(timer < 2) timer++;
    }

    // Move the rocket pawn in a new position
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
    }

    private static <T> void swap(int x, int y, List <T> list) {
        T s = list.get(x);
        list.set(x, list.get(y));
        list.set(y, s);
    }


    public int getTimer() {
        return timer;
    }

    public Card[][] getVisible() {
        return visible;
    }

    // Draw a card from the deck
    public Card drawCard() {
        if(!deck.isEmpty()) {
            return deck.removeFirst();
        }
        return null; // no available cards
    }

    public List<RocketPawn> getOrder() {
        return order;
    }

    public List<Integer> getOffset() {
        return offset;
    }

    public int getLeaderPosition() {
        return leaderPosition;
    }
}
