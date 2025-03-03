package it.polimi.softeng.is25am10.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightBoard {
    private RocketPawn[] positions;
    private int hourglassesPosition;
    private Card[][] visibleCards;
    private List<Card> cardsDeck;
    private Map<RocketPawn, Integer> laps;

    // Constructor method
    public FlightBoard() {
        this.positions = new RocketPawn[24]; //24 possible positions on teh board
        this.hourglassesPosition = 0;
        this.visibleCards = new Card[3][3];
        this.cardsDeck = new ArrayList<>();
        this.laps = new HashMap<>();
    }

    // The Rocket pawn is positioned on the flight board
    public Result<String> setRocketReady(RocketPawn pawn) {

        // Check that the pawn isn't already set
        for (int i = 0; i < positions.length; i++) {
            if (positions[i] == pawn) {
                return new Result<>(false, null, "Rocket is already on the board");
            }
        }

        if(positions[6] == null){
                positions[6] = pawn;
                laps.put(pawn, 0);
                return new Result<>(true, "Rocket " + pawn + " is ready", null);
        }else if(positions[3] == null){
                positions[3] = pawn;
                laps.put(pawn, 0);
                return new Result<>(true, "Rocket " + pawn + " is ready", null);
        }else if(positions[1] == null){
                 positions[1] = pawn;
                 laps.put(pawn, 0);
                return new Result<>(true, "Rocket " + pawn + " is ready", null);
        }else if(positions[0] == null){
                 positions[0] = pawn;
                 laps.put(pawn, 0);
                return new Result<>(true, "Rocket " + pawn + " is ready", null);
        }else {
                return new Result<>(false, null, "No available positions");
        }

    }

    // Increases the position of the hourglass
    public void moveHourglasses() {
        if(hourglassesPosition < 2) hourglassesPosition++;
    }

    // Move the rocket pawn in a new position
    public void moveRocket(RocketPawn pawn, int pos) {
        int currentIndex = -1;

        //Find the pawn's current index
        for(int i = 0; i < positions.length; i++) {
            if(positions[i] == pawn) {
                currentIndex = i;
                break;
            }
        }

        //if the pawn isn't on the board
        if(currentIndex == -1) return;

        int direction = (pos > 0) ? 1 : -1; //1 if I have to move forward, -1 if I have to move backward
        int stepsNumber = Math.abs(pos);
        int newIndex = currentIndex;// The index of the new position I have to determine
        int stepsLeft = positions.length - currentIndex; // steps left before reaching the last cell
        if (stepsNumber > stepsLeft) {
            laps.put(pawn, laps.getOrDefault(pawn,0) +1);
        }

        // do all the mandatory steps in the right direction
        while(stepsNumber > 0) {
           newIndex = (newIndex + direction + positions.length) % positions.length; // circular array

            // the next cell is free
            if(positions[newIndex] == null || positions[newIndex].equals(pawn)) {
                stepsNumber--;
            }

        }

        // if the last step cell is occupied, search for the first empty one in th same direction
        while (positions[newIndex] != null) {
           newIndex = (newIndex + direction + positions.length) % positions.length;
        }


        positions[currentIndex] = null; // free old cell
        positions[newIndex] = pawn; // assign new cell

    }

    public Result<Integer> getLaps(RocketPawn pawn) {
        int currentIndex = -1;

        //Find the pawn's current index
        for (int i = 0; i < positions.length; i++) {
            if (positions[i] == pawn) {
                currentIndex = i;
                break;
            }
        }

        // if the pawn isn't on the board
        if (currentIndex == -1) return new Result<>(false, null, "Rocket is not on the board");

        int completedLaps = laps.getOrDefault(pawn, 0);

        return new Result<>(true, completedLaps, null);
    }

    public RocketPawn[] getPositions() {
        return positions;
    }

    public int getHourglassesPosition() {
        return hourglassesPosition;
    }

    public Card[][] getVisibleCards() {
        return visibleCards;
    }

    // Draw a card from the deck
    public Card drawCard() {
        if(!cardsDeck.isEmpty()) {
            return cardsDeck.removeFirst();
        }
        return null; // no available cards
    }

}
