package it.polimi.softeng.is25am10.model.cards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the input gave by the player to the cards.
 * It contains all the possible data about the player choice.
 * It also accepts disconnected players.
 */
public class CardInput implements Serializable {
    /**
     * The player has been disconnected, this is
     * generated only by the controller.
     */
    public boolean disconnected = false;

    /**
     * The player accept to use the card.
     */
    public boolean accept = false;

    /**
     * Witch asteroid should the ship activate the shield?
     */
    public List<Integer> shieldFor = new ArrayList<>();

    /**
     * Planet chosen by the player to descend.
     */
    public Planets.Planet planet = Planets.Planet.NOPLANET;

    /**
     * Generate a disconnected input state.
     *
     * @return disconnected CardInput
     */
    public static CardInput disconnected(){
        CardInput input = new CardInput();
        input.disconnected = true;
        return input;
    }
}
