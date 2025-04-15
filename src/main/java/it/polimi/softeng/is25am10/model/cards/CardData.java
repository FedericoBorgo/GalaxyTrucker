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
    public Map<Warzone.LeastTypes, Warzone.MalusTypes> malusTypes = null;

    public int cash = 0, days = 0, power = 0, crew = 0, goods = 0;

    public CardData(Card.Type type, int id){
        this.type = type;
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String cardName = switch(type){
            case EPIDEMIC -> "Epidemia";
            case METEORS -> "Pioggia di meteoriti";
            case PLANETS -> "Pianeti";
            case AB_SHIP -> "Nave abbandonata";
            case OPEN_SPACE -> "Spazio aperto";
            case STARDUST -> "Polvere stellare";
            case STATION -> "Stazione abbandonata";
            case PIRATES -> "Pirati";
            case SMUGGLERS -> "Contrabbandieri";
            case SLAVERS -> "Schiavisti";
            case WAR_ZONE -> "Zona di guerra";
        };

        builder.append(cardName).append("\n");
        builder.append("ID: ").append(id).append("\n");

        if(projectiles != null){
            builder.append("Proiettili:\n");

            projectiles.forEach((p) -> {
                builder.append("  ").append(p).append("\n");
            });
        }

        if(planets != null){
            builder.append("Pianeti:\n");

            planets.forEach((p, goods) -> {
                if(chosenPlanets.contains(p))
                    return;
                builder.append("  ").append(p.getID()).append(": ");
                goods.forEach(e -> builder.append(e.getName()).append(", "));
                builder.append("\n");
            });

            builder.append("  ").append(Planets.Planet.NOPLANET.getID()).append(": Non atterrare\n");
        }

        if(declaredPower != null){
            builder.append("Potenza giocatori: \n");
            declaredPower.forEach((name, v) -> {
                builder.append("  ").append(name).append(": ").append(v).append("\n");
            });
        }

        if(rewards != null){
            builder.append("Ricompensa:\n ");
            rewards.forEach((p) -> {builder.append(p.getName()).append(", ");});
            builder.append("\n");
        }

        if(malusTypes != null){
            malusTypes.forEach((least, malus) -> {
                String out = switch (malus){
                    case DAYS -> days;
                    case GUYS -> crew;
                    case FIRE -> "";
                    case GOODS -> goods;
                } + " "  + malus.getName();

                builder.append(" -").append(least.getName()).append(": ").append(out).append("\n");
            });
        }
        else{
            if(cash != 0)
                builder.append("Soldi: ").append(cash).append("\n");

            if(days != 0)
                builder.append("Giorni di volo: ").append(days).append("\n");

            if(crew != 0)
                builder.append("Membri equipaggio: ").append(crew).append("\n");

            if(goods != 0)
                builder.append("Scatole: ").append(goods).append("\n");

            if(power != 0)
                builder.append("Potenza nemico: ").append(power).append("\n");
        }

        return builder.toString();
    }
}
