package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.gui.CardScene;
import it.polimi.softeng.is25am10.gui.Launcher;
import it.polimi.softeng.is25am10.model.Projectile;
import it.polimi.softeng.is25am10.model.State;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

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

    public VBox handle(CardScene s){
        VBox vBox = new VBox();

        switch(type){
            case OPEN_SPACE:
                declaredPower.forEach((p, v) -> {
                    Text label = new Text(p + ": " + v);
                    label.setFill(Color.web("#14723e"));
                    label.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                    vBox.getChildren().add(label);
                });
                break;
            case PLANETS:
                SplitMenuButton splitMenuButton = new SplitMenuButton();
                splitMenuButton.setPrefWidth(s.cardDataPane.getWidth()/2);
                splitMenuButton.setText(Planets.Planet.NOPLANET.name());

                planets.forEach((p, _) -> {
                    if(chosenPlanets.contains(p))
                        return;

                    MenuItem item = new MenuItem("" + p);
                    splitMenuButton.getItems().add(item);
                    item.setOnAction(_ -> {
                        s.cardInput.planet = p;
                        splitMenuButton.setText(p.name());
                    });
                });

                MenuItem item = new MenuItem(Planets.Planet.NOPLANET.name());
                item.setOnAction(_ -> {
                    s.cardInput.planet = Planets.Planet.NOPLANET;
                    splitMenuButton.setText(Planets.Planet.NOPLANET.name());
                });
                splitMenuButton.getItems().add(item);

                vBox.getChildren().add(splitMenuButton);
                break;
            case STATION:
            case AB_SHIP:
                CheckBox checkBox = new CheckBox();
                Text text = new Text("Accettare ricompensa?");

                text.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                text.setFill(Color.WHITE);

                checkBox.setSelected(false);
                checkBox.setMinSize(s.cardDataPane.getHeight()/7, s.cardDataPane.getHeight()/7);
                checkBox.setOnAction(event -> {
                    s.cardInput.accept = checkBox.isSelected();
                    event.consume();
                });
                vBox.getChildren().add(text);
                vBox.getChildren().add(checkBox);
                break;
            case WAR_ZONE:

            case PIRATES:
            case METEORS:
                projectiles.forEach(p -> {
                    ImageView view = new ImageView(Launcher.getImage("/gui/textures/asteroid/" + p.type().name().toLowerCase() + ".png"));
                    view.setRotate(switch(p.side()){
                        case UP -> 0;
                        case RIGHT -> 90;
                        case DOWN -> 180;
                        case LEFT -> 270;
                    });

                    if(p.type() == Projectile.Type.SMALL_ASTEROID || p.type() == Projectile.Type.SMALL_FIRE){
                        view.setOnDragOver(event -> {
                            if (event.getGestureSource() != view && event.getDragboard().hasString())
                                event.acceptTransferModes(TransferMode.MOVE);
                            event.consume();
                        });

                        view.setOnDragDropped(event -> {
                            Dragboard db = event.getDragboard();
                            event.setDropCompleted(true);
                            event.consume();

                            if (!db.hasString())
                                return;
                            String data = db.getString();
                            s.dragSuccess.set(false);

                            if (data.contains("battery")) {
                                Coordinate from = Coordinate.fromString(data.substring(data.indexOf(' ') + 1)).getData();

                                s.server.drop(from).ifPresent(_ -> {
                                    s.cardInput.shieldFor.add(p.ID());
                                    s.dragSuccess.set(true);
                                });
                            }
                        });
                    }

                    switch(p.side()){
                        case UP -> {
                            if(p.where() < 4 || p.where() > 10)
                                break;
                            view.setFitHeight(s.upGrid.getHeight());
                            view.setFitWidth(s.upGrid.getWidth()/7);
                            s.upGrid.add(view, p.where()-4, 0);
                        }
                        case RIGHT -> {
                            if(p.where() < 5 || p.where() > 9)
                                break;
                            view.setFitHeight(s.leftGrid.getHeight()/5);
                            view.setFitWidth(s.leftGrid.getWidth());
                            s.rightGrid.add(view, 0, p.where()-5);
                        }
                        case DOWN -> {
                            if(p.where() < 4 || p.where() > 10)
                                break;
                            view.setFitHeight(s.upGrid.getHeight());
                            view.setFitWidth(s.upGrid.getWidth()/7);
                            s.downGrid.add(view, p.where()-4, 0);
                        }
                        case LEFT -> {
                            if(p.where() < 5 || p.where() > 9)
                                break;
                            view.setFitHeight(s.leftGrid.getHeight()/5);
                            view.setFitWidth(s.leftGrid.getWidth());
                            s.leftGrid.add(view, 0, p.where()-5);
                        }
                    }
                });

                break;
        };

        return vBox;
    }
}
