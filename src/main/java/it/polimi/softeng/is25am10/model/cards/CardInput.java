package it.polimi.softeng.is25am10.model.cards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CardInput implements Serializable {
    public boolean disconnected = false;
    public boolean accept = false;
    public List<Integer> shieldFor = new ArrayList<>();
    public Planets.Planet planet = Planets.Planet.NOPLANET;

    public static CardInput disconnected(){
        CardInput input = new CardInput();
        input.disconnected = true;
        return input;
    }
}
