package it.polimi.softeng.is25am10.model.cards;

import java.util.ArrayList;
import java.util.List;

public class Input {
    public boolean disconnected = false;
    public boolean accept = false;
    List<Integer> shieldFor = new ArrayList<>();
    Planets.Planet planet = Planets.Planet.NOPLANET;

    public static Input disconnected(){
        Input input = new Input();
        input.disconnected = true;
        return input;
    }
}
