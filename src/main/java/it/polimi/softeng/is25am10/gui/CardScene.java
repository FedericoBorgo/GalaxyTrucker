package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.*;
import it.polimi.softeng.is25am10.model.cards.*;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CardScene implements Callback {
    ClientInterface server;
    Model.State.Type state;
    HashMap<String, FlightBoard.Pawn> players;
    CardInput cardInput = new CardInput();
    AtomicBoolean dragSuccess = new AtomicBoolean(false);
    CardData cardData = null;

    @FXML
    Label stateLabel, nameLabel;

    @FXML
    Pane cardPane;

    @FXML
    public Label redLabel, blueLabel, greenLabel, yellowLabel;
    Listener listener;

    @FXML
    Pane posPane0, posPane1, posPane2, posPane3, posPane4, posPane5, posPane6, posPane7, posPane8,
            posPane9, posPane10, posPane11, posPane12, posPane13, posPane14, posPane15, posPane16,
            posPane17, posPane18, posPane19, posPane20, posPane21, posPane22, posPane23;
    Pane[] posPanes;

    @FXML
    GridPane shipPane;

    @FXML
    Label errLabel;

    @FXML
    ImageView arrowView0, arrowView1, arrowView2, arrowView3;

    Text[][] counters = new Text[TilesBoard.BOARD_WIDTH][TilesBoard.BOARD_HEIGHT];
    StackPane[][] stackPanes = new StackPane[TilesBoard.BOARD_WIDTH][TilesBoard.BOARD_HEIGHT];
    Map<Coordinate, List<Pos>> freeContainers = new HashMap<>();
    @FXML
    Pane holePane;

    @FXML
    Text engineText, cannonText;

    @FXML
    Pane cardDataPane;

    @FXML
    Text cashText;

    @FXML
    void initialize() {
        posPanes = new Pane[]{posPane0, posPane1, posPane2, posPane3, posPane4, posPane5, posPane6,
        posPane7, posPane8, posPane9, posPane10, posPane11, posPane12, posPane13, posPane14, posPane15,
        posPane16, posPane17, posPane18, posPane19, posPane20, posPane21, posPane22, posPane23};
    }

    @FXML
    private void drawCard(){
        server.drawCard();
    }

    @FXML
    private void ready(){
        if(state == Model.State.Type.WAITING_INPUT){
            Result<CardInput> res = server.setInput(cardInput);

            if(res.isErr())
                errLabel.setText(res.getReason());
            else
                errLabel.setText("scelta dichiarata");
        }
        else if(state == Model.State.Type.PLACE_REWARD){
            server.dropReward().ifPresent(_ -> {
                cardDataPane.getChildren().clear();
            });
        }

    }

    void config(ClientInterface server, Listener listener, FlightBoard flight, String state, ShipBoard ship, HashMap<String, FlightBoard.Pawn> players) {
        this.listener = listener;
        this.server = server;
        this.players = players;
        listener.setCallback(this);
        updatePos(flight);
        updateNextTurn(flight.getOrder().getFirst());
        stateLabel.setText(state);
        nameLabel.setText(server.getPlayerName());

        Coordinate.forEach(c ->  {
            Result<Tile> res = ship.getTiles().getTile(c);

            if(res.isErr())
                return;
            Tile t = res.getData();
            Tile.Rotation r = ship.getTiles().getRotation(c);
            ImageView view;

            if(!Tile.real(t))
                return;

            if(t.getType() == Tile.Type.C_HOUSE)
                view = new ImageView(Building.getCHouse(players.get(server.getPlayerName())));
            else
                view = new ImageView(Building.getImage(t));

            view.setFitHeight(shipPane.getHeight() / 5);
            view.setFitWidth(shipPane.getWidth() / 7);
            view.setRotate(r.toInt()*90);

            stackPanes[c.x()][c.y()] = new StackPane();
            shipPane.add(view, c.x(), c.y());
            shipPane.add(stackPanes[c.x()][c.y()], c.x(), c.y());

            if(Tile.box(t))
                freeContainers.put(c, new ArrayList<>(List.of(Pos.TOP_LEFT, Pos.TOP_RIGHT, Pos.BOTTOM_RIGHT)));
        });

        List<Pair<Map<Coordinate, Integer>, String>> boards = new ArrayList<>();
        boards.add(new Pair<>(ship.getAstronaut().getPositions(), "astronaut"));
        boards.add(new Pair<>(ship.getBattery().getPositions(), "battery"));
        boards.add(new Pair<>(ship.getPurple().getPositions(), "purple"));
        boards.add(new Pair<>(ship.getBrown().getPositions(), "brown"));

        boards.forEach(pair -> {
            String type = pair.getValue();

            pair.getKey().forEach((c, qty) -> {
                StackPane cell = stackPanes[c.x()][c.y()];
                ImageView view = new ImageView(Building.getImage("/gui/" + type + ".png"));
                Text count = new Text(qty.toString() + "x");
                count.setFont(Font.font("Arial Black", FontWeight.BOLD, 30));
                count.setFill(Color.BLACK);
                count.setStroke(Color.WHITE);
                count.setStrokeWidth(2);

                view.setFitHeight(shipPane.getHeight()/7);
                view.setFitWidth(shipPane.getHeight()/7);

                view.setOnDragDetected(event -> {
                    if(view.getImage() == null)
                        return;

                    view.setCursor(Cursor.CLOSED_HAND);

                    Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(type + " " + c);
                    db.setContent(content);
                    db.setDragView(Building.getRotatedImage(view.getImage(), 0));
                    event.consume();
                });

                view.setOnDragDone(event -> {
                    event.consume();
                    if(!dragSuccess.get())
                        return;
                    dragSuccess.set(false);
                    removeOne(c);
                });

                view.setOnMousePressed(event -> {
                    view.setCursor(Cursor.CLOSED_HAND);
                    event.consume();
                });

                view.setOnMouseEntered(_ -> {view.setCursor(Cursor.OPEN_HAND);});
                view.setOnMouseExited(_ -> {view.setCursor(Cursor.DEFAULT);});

                cell.getChildren().add(view);
                cell.getChildren().add(count);
                StackPane.setAlignment(view, Pos.CENTER);
                StackPane.setAlignment(count, Pos.BOTTOM_RIGHT);
                counters[c.x()][c.y()] = count;
            });
        });

        shipPane.setOnDragOver(event -> {
            if (event.getGestureSource() != shipPane && event.getDragboard().hasString())
                event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        shipPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            event.setDropCompleted(true);
            event.consume();

            if (!db.hasString())
                return;

            // get dropped coordinate
            int col = (int) (event.getX() / (shipPane.getWidth() / 7));
            int row = (int) (event.getY() / (shipPane.getHeight() / 5));

            if (Coordinate.isInvalid(col, row))
                return;

            Coordinate c = new Coordinate(col, row);
            dragSuccess.set(false);

            if(cardData == null)
                return;

            String content = db.getString();

            if(this.state == Model.State.Type.WAITING_INPUT){
                if(content.contains("battery")){
                    Result<Tile> res = ship.getTiles().getTile(c);

                    if(res.isErr())
                        return;

                    Tile.Type t = res.getData().getType();

                    Rectangle rect = new Rectangle(shipPane.getWidth()/7, shipPane.getHeight()/5);
                    rect.setFill(Color.web("rgb(102, 255, 51)", 0.3));
                    rect.setStroke(Color.GREEN);
                    rect.setStrokeWidth(1);

                    if(cardData.type == Card.Type.OPEN_SPACE && t != Tile.Type.D_ENGINE)
                        return;

                    Coordinate from = Coordinate.fromString(content.substring(content.indexOf(' ') + 1)).getData();

                    server.drop(from).ifPresent(_ -> {
                        dragSuccess.set(true);
                        stackPanes[c.x()][c.y()].getChildren().add(rect);
                    });
                }

                engineText.setText(String.valueOf(server.getEnginePower(server.getPlayerName())));
                cannonText.setText(String.valueOf((int)server.getCannonPower(server.getPlayerName())));
            }
            else if(this.state == Model.State.Type.PLACE_REWARD){
                GoodsBoard.Type type = GoodsBoard.Type.valueOf(content);

                server.placeReward(type, c).ifPresent(_ -> {
                    dragSuccess.set(true);
                    ImageView view = new ImageView(Building.getImage("/gui/" + type.name().toLowerCase() + ".png"));
                    Pos pos = freeContainers.get(c).removeFirst();
                    stackPanes[c.x()][c.y()].getChildren().add(view);
                    StackPane.setAlignment(view, pos);
                });
            }
            });

    }

    void removeOne(Coordinate c){
        int val = counters[c.x()][c.y()].getText().charAt(0) - '0';
        val--;

        if(val <= 0)
            stackPanes[c.x()][c.y()].getChildren().clear();
        else
            counters[c.x()][c.y()].setText(val + "x");
    }

    void updatePos(FlightBoard board){
        int pos = board.getLeaderPosition()%24;
        List<FlightBoard.Pawn> order = board.getOrder();
        List<Integer> offset = board.getOffset();
        FlightBoard.Pawn pawn;

        for(Pane pane : posPanes)
            pane.getChildren().clear();

        for (int i = 0; i < order.size(); i++) {
            pawn = order.get(i);
            posPanes[pos].getChildren().add(new StackPane(new Circle(10, pawn.toColor())));

            if(i < offset.size()-1)
                pos = board.getLeaderPosition()%24 + offset.get(i+1);

            if(pos < 0)
                pos = 23 + pos;
        }
    }

    @Override
    public void pushPlayers(HashMap<String, FlightBoard.Pawn> players, HashSet<String> quid, HashSet<String> disconnected) throws RemoteException {
        Platform.runLater(() -> {
            this.players = players;
            players.forEach((name, pawn) -> {
                String text = name + (disconnected.contains(name)? " (disc)" : "")
                        + (quid.contains(name)? " (abb)" : "");

                switch (pawn){
                    case YELLOW -> yellowLabel.setText(text);
                    case GREEN -> greenLabel.setText(text);
                    case BLUE -> blueLabel.setText(text);
                    case RED -> redLabel.setText(text);
                };
            });
        });
    }

    @Override
    public int askHowManyPlayers() throws RemoteException {
        return 4;
    }

    @Override
    public void pushSecondsLeft(Integer seconds) throws RemoteException {

    }

    @Override
    public void pushState(Model.State.Type state) throws RemoteException {
        this.state = state;

        Platform.runLater(() -> {
            stateLabel.setText(state.getName());
        });
    }

    @Override
    public void pushCardData(CardData card) throws RemoteException {
        this.cardData = card;
        Platform.runLater(() -> {
            engineText.setText(String.valueOf(server.getEnginePower(server.getPlayerName())));
            cannonText.setText(String.valueOf((int)server.getCannonPower(server.getPlayerName())));

            Image img = Building.getImage("/cards/" + card.type.name()+ "/" + card.id + ".jpg");
            ImageView imgView = new ImageView(img);
            imgView.setFitHeight(cardPane.getHeight());
            imgView.setFitWidth(cardPane.getWidth());
            cardPane.getChildren().add(imgView);
            cardDataPane.getChildren().clear();
            VBox vBox = new VBox();

            switch(card.type){
                case OPEN_SPACE -> {
                    card.declaredPower.forEach((p, v) -> {
                        Text label = new Text(p + ": " + v);
                        label.setFill(Color.web("#14723e"));
                        label.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                        vBox.getChildren().add(label);
                    });
                }
                case PLANETS -> {
                    SplitMenuButton splitMenuButton = new SplitMenuButton();
                    splitMenuButton.setPrefWidth(cardDataPane.getWidth()/2);
                    splitMenuButton.setText(Planets.Planet.NOPLANET.name());

                    card.planets.forEach((p, _) -> {
                        if(card.chosenPlanets.contains(p))
                            return;

                        MenuItem item = new MenuItem("" + p);
                        splitMenuButton.getItems().add(item);
                        item.setOnAction(_ -> {
                            cardInput.planet = p;
                            splitMenuButton.setText(p.name());
                        });
                    });

                    MenuItem item = new MenuItem(Planets.Planet.NOPLANET.name());
                    item.setOnAction(_ -> {
                        cardInput.planet = Planets.Planet.NOPLANET;
                        splitMenuButton.setText(Planets.Planet.NOPLANET.name());
                    });
                    splitMenuButton.getItems().add(item);

                    vBox.getChildren().add(splitMenuButton);
                }
                default -> {}
            };

            cardDataPane.getChildren().add(vBox);
        });
    }

    @Override
    public void pushCardChanges(CardOutput output) throws RemoteException {
        cardInput = new CardInput();
        Platform.runLater(() -> {
            errLabel.setText("");
            cardPane.getChildren().clear();
            cardDataPane.getChildren().clear();

            if(output.cash.containsKey(server.getPlayerName())){
                int cash = output.cash.get(server.getPlayerName());
                cash += Integer.parseInt(cashText.getText());
                cashText.setText(String.valueOf(cash));
            }

            if(output.killedCrew.containsKey(server.getPlayerName()))
                output.killedCrew.get(server.getPlayerName()).forEach(this::removeOne);

            if(output.rewards.containsKey(server.getPlayerName())){
                VBox vBox = new VBox();
                output.rewards.get(server.getPlayerName()).forEach(r -> {
                    ImageView view = new ImageView(Building.getImage("/gui/" + r.name().toLowerCase() + ".png"));
                    vBox.getChildren().add(view);

                    view.setOnDragDetected(event -> {
                        if(view.getImage() == null)
                            return;

                        view.setCursor(Cursor.CLOSED_HAND);

                        Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
                        ClipboardContent content = new ClipboardContent();
                        content.putString(r.name());
                        db.setContent(content);
                        db.setDragView(Building.getRotatedImage(view.getImage(), 0));
                        event.consume();
                    });

                    view.setOnDragDone(event -> {
                        event.consume();
                        if(!dragSuccess.get())
                            return;
                        dragSuccess.set(false);
                        vBox.getChildren().remove(view);
                    });

                    view.setOnMousePressed(event -> {
                        view.setCursor(Cursor.CLOSED_HAND);
                        event.consume();
                    });

                    view.setOnMouseEntered(_ -> {view.setCursor(Cursor.OPEN_HAND);});
                    view.setOnMouseExited(_ -> {view.setCursor(Cursor.DEFAULT);});

                });
                cardDataPane.getChildren().add(vBox);
            }
        });
    }

    private void updateNextTurn(FlightBoard.Pawn pawn){
        arrowView0.setVisible(false);
        arrowView1.setVisible(false);
        arrowView2.setVisible(false);
        arrowView3.setVisible(false);

        switch(pawn){
            case YELLOW -> arrowView3.setVisible(true);
            case GREEN -> arrowView2.setVisible(true);
            case BLUE -> arrowView1.setVisible(true);
            case RED -> arrowView0.setVisible(true);
        }
    }

    @Override
    public void waitFor(String name, FlightBoard.Pawn pawn) throws RemoteException {
        Platform.runLater(() -> {
            updateNextTurn(pawn);
        });
    }

    @Override
    public void gaveTile(Tile t) throws RemoteException {

    }

    @Override
    public void gotTile(Tile t) throws RemoteException {

    }

    @Override
    public void pushBoard(ShipBoard board) throws RemoteException {

    }

    @Override
    public void pushFlight(FlightBoard board) throws RemoteException {
        Platform.runLater(() -> {
            updatePos(board);
            List<FlightBoard.Pawn> order = board.getOrder();

            if(!order.isEmpty())
                updateNextTurn(order.getFirst());
        });
    }

    @Override
    public int ping() throws RemoteException {
        return 0;
    }

    @Override
    public void placeTile(Coordinate c, Tile t, Tile.Rotation r) throws RemoteException {

    }

    @Override
    public void bookedTile(Tile t) throws RemoteException {

    }

    @Override
    public void removed(Coordinate c) throws RemoteException {

    }

    @Override
    public void pushDropped(Model.Removed dropped) throws RemoteException {

    }

    @Override
    public void pushCannons(HashMap<Tile.Rotation, Integer> cannons) throws RemoteException {

    }

    @Override
    public void pushModel(Model m) throws RemoteException {

    }
}
