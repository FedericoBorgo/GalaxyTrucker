package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Projectile;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;

import java.io.Serializable;
import java.util.ArrayList;
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
    public List<Planets.Planet> chosenPlanets = new ArrayList<>();
    public Map<String, Integer> declaredPower = null;
    public List<GoodsBoard.Type> rewards = null;

    public int cash = 0, days = 0, power = 0, crew = 0, goods = 0;

    public CardData(Card.Type type, int id){
        this.type = type;
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Card data\n");
        builder.append("type: ").append(type).append("\n");
        builder.append("id: ").append(id).append("\n");

        if(projectiles != null){
            builder.append("projectiles:\n");

            projectiles.forEach((p) -> {
                builder.append("  ").append(p).append("\n");
            });
        }

        if(planets != null){
            builder.append("planets:\n");

            planets.forEach((p, goods) -> {
                if(chosenPlanets.contains(p))
                    return;
                builder.append("  ").append(p).append(": ");
                goods.forEach(e -> builder.append(e).append(", "));
                builder.append("\n");
            });
        }

        if(declaredPower != null){
            builder.append("declared power: \n");
            declaredPower.forEach((name, v) -> {
                builder.append("  ").append(name).append(": ").append(v).append("\n");
            });
        }

        if(rewards != null){
            builder.append("rewards:\n ");
            rewards.forEach((p) -> {builder.append(p).append(", ");});
        }

        if(cash != 0)
            builder.append("cash: ").append(cash).append("\n");

        if(days != 0)
            builder.append("days: ").append(days).append("\n");

        if(crew != 0)
            builder.append("crew: ").append(crew).append("\n");

        if(goods != 0)
            builder.append("goods: ").append(goods).append("\n");

        if(power != 0)
            builder.append("power: ").append(power).append("\n");

        return builder.toString();
    }
}
