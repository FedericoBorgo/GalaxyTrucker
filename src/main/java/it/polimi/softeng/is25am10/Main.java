package it.polimi.softeng.is25am10;


import it.polimi.softeng.is25am10.model.TilesCollection;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.model.cards.Meteors;
import it.polimi.softeng.is25am10.model.cards.Space;

import java.util.List;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        List<Card> space = Meteors.construct(null);
        TilesCollection collection = new TilesCollection();
    }
}