package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Projectile;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This class contains every data for every card.
 */
public class CardData implements Serializable {
    public final Card.Type type;
    public final int id;
    public List<Projectile> projectiles = null;
    public Map<Planets.Planet, List<GoodsBoard.Type>> planets = null;
    public List<Planets.Planet> chosenPlanets = null;
    public Map<String, Integer> declaredPower = null;
    public List<GoodsBoard.Type> rewards = null;

    public int cash, days, power, crew, goods;

    public CardData(Card.Type type, int id){
        this.type = type;
        this.id = id;
    }
}
