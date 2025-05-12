package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.State;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.*;
import it.polimi.softeng.is25am10.model.cards.*;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
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
    State.Type state;
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
    GUIEventListener listener;

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
    Text shipFixText;

    @FXML
    ImageView arrowView0, arrowView1, arrowView2, arrowView3;

    @FXML
    GridPane downGrid, upGrid, leftGrid, rightGrid;

    Map<Coordinate, ImageView> images = new HashMap<>();
    Map<Rectangle, StackPane> rectangles = new HashMap<>();
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
        shipFixText.setVisible(false);
    }

    @FXML
    private void drawCard(){
        server.drawCard();
    }

    @FXML
    private void ready(){
        if(state == State.Type.WAITING_INPUT){
            Result<CardInput> res = server.setInput(cardInput);

            if(res.isErr())
                errLabel.setText(res.getReason());
            else
                errLabel.setText("scelta dichiarata");
        }
        else if(state == State.Type.PLACE_REWARD){
            server.dropReward().ifPresent(_ -> {
                cardDataPane.getChildren().clear();
            });
        }

    }

    public void config(ClientInterface server, GUIEventListener listener, FlightBoard flight, String state, ShipBoard ship, HashMap<String, FlightBoard.Pawn> players) {
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
                view = new ImageView(Launcher.getCHouse(players.get(server.getPlayerName())));
            else
                view = new ImageView(Launcher.getImage(t));

            view.setFitHeight(shipPane.getHeight() / 5);
            view.setFitWidth(shipPane.getWidth() / 7);
            view.setRotate(r.toInt()*90);

            stackPanes[c.x()][c.y()] = new StackPane();
            shipPane.add(view, c.x(), c.y());
            shipPane.add(stackPanes[c.x()][c.y()], c.x(), c.y());
            images.put(c, view);

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
                ImageView view = new ImageView(Launcher.getImage("/gui/textures/" + type + ".png"));
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
                    db.setDragView(Launcher.getRotatedImage(view.getImage(), 0));
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

        holePane.setOnDragOver(event -> {
            if (event.getGestureSource() != holePane && event.getDragboard().hasString())
                event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        holePane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            event.setDropCompleted(true);
            event.consume();

            if (!db.hasString())
                return;
            String data = db.getString();
            dragSuccess.set(false);

            if(this.state == State.Type.CHECKING){
                Coordinate del = Coordinate.fromString(data).getData();
                server.remove(del).ifPresent(_ -> {
                    dragSuccess.set(true);
                    drawErrors();
                });
            }

            if(data.contains("astronaut")){
                Coordinate from = Coordinate.fromString(data.substring(data.indexOf(' ')+1)).getData();
                server.drop(from).ifPresent(_ -> dragSuccess.set(true));
            }
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

            if(this.state == State.Type.WAITING_INPUT){
                if(content.contains("battery")){
                    Result<Tile> res = ship.getTiles().getTile(c);

                    if(res.isErr())
                        return;

                    Tile.Type t = res.getData().getType();

                    Rectangle rect = new Rectangle(shipPane.getWidth()/7, shipPane.getHeight()/5);
                    rect.setFill(Color.web("rgb(102, 255, 51)", 0.3));
                    rect.setStroke(Color.GREEN);
                    rect.setStrokeWidth(1);

                    if(cardData.type != Card.Type.OPEN_SPACE || t != Tile.Type.D_ENGINE)
                        return;

                    Coordinate from = Coordinate.fromString(content.substring(content.indexOf(' ') + 1)).getData();

                    server.drop(from).ifPresent(_ -> {
                        dragSuccess.set(true);
                        stackPanes[c.x()][c.y()].getChildren().add(rect);
                        rectangles.put(rect, stackPanes[c.x()][c.y()]);
                    });
                }

                engineText.setText(String.valueOf(server.getEnginePower(server.getPlayerName())));
                cannonText.setText(String.valueOf((int)server.getCannonPower(server.getPlayerName())));
            }
            else if(this.state == State.Type.PLACE_REWARD){
                GoodsBoard.Type type = GoodsBoard.Type.valueOf(content);

                server.placeReward(type, c).ifPresent(_ -> {
                    dragSuccess.set(true);
                    ImageView view = new ImageView(Launcher.getImage("/gui/textures/" + type.name().toLowerCase() + ".png"));
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
    public void pushState(State.Type state) throws RemoteException {
        this.state = state;

        Platform.runLater(() -> {
            stateLabel.setText(state.getName());

            if(state == State.Type.CHECKING){
                for (StackPane[] stackPane : stackPanes)
                    for (StackPane pane : stackPane)
                        if(pane != null)
                            pane.setVisible(false);

                images.forEach((c, view) -> {
                    view.setOnDragDetected(event -> {
                        if(view.getImage() == null)
                            return;

                        view.setCursor(Cursor.CLOSED_HAND);

                        Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
                        ClipboardContent content = new ClipboardContent();
                        content.putString(c.toString());
                        db.setContent(content);
                        db.setDragView(Launcher.getRotatedImage(view.getImage(), 0));
                        event.consume();
                    });

                    view.setOnDragDone(event -> {
                        event.consume();
                        if(!dragSuccess.get())
                            return;
                        shipPane.getChildren().remove(stackPanes[c.x()][c.y()]);
                        shipPane.getChildren().remove(view);
                    });

                    view.setOnMousePressed(event -> {
                        view.setCursor(Cursor.CLOSED_HAND);
                        event.consume();
                    });

                    view.setOnMouseEntered(_ -> {view.setCursor(Cursor.OPEN_HAND);});
                    view.setOnMouseExited(_ -> {view.setCursor(Cursor.DEFAULT);});
                });

                drawErrors();
            }
        });
    }

    void drawErrors(){
        Set<Coordinate> res = server.getShip().getTiles().isOK();

        rectangles.forEach((r, p) -> p.getChildren().remove(r));

        if(res.isEmpty()){
            for (StackPane[] stackPane : stackPanes)
                for (StackPane pane : stackPane)
                    if(pane != null)
                        pane.setVisible(true);
            shipFixText.setVisible(false);
            return;
        }

        if(res.contains(new Coordinate(0, 0))){
            shipFixText.setVisible(true);
            return;
        }

        res.forEach(c -> {
            Rectangle rect = new Rectangle(shipPane.getWidth()/7, shipPane.getHeight()/5);
            rect.setFill(Color.web("rgb(255, 0, 0)", 0.3));
            rect.setStroke(Color.RED);
            rect.setStrokeWidth(1);
            stackPanes[c.x()][c.y()].getChildren().add(rect);
            rectangles.put(rect, stackPanes[c.x()][c.y()]);
        });
    }

    @Override
    public void pushCardData(CardData card) throws RemoteException {
        this.cardData = card;
        Platform.runLater(() -> {
            engineText.setText(String.valueOf(server.getEnginePower(server.getPlayerName())));
            cannonText.setText(String.valueOf((int)server.getCannonPower(server.getPlayerName())));

            Image img = Launcher.getImage("/cards/" + card.type.name()+ "/" + card.id + ".jpg");
            ImageView imgView = new ImageView(img);
            imgView.setFitHeight(cardPane.getHeight());
            imgView.setFitWidth(cardPane.getWidth());
            cardPane.getChildren().add(imgView);
            cardDataPane.getChildren().clear();
            VBox vBox = new VBox();

            switch(card.type){
                case OPEN_SPACE:
                    card.declaredPower.forEach((p, v) -> {
                        Text label = new Text(p + ": " + v);
                        label.setFill(Color.web("#14723e"));
                        label.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                        vBox.getChildren().add(label);
                    });
                    break;
                case PLANETS:
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
                    break;
                case STATION:
                case AB_SHIP:
                    CheckBox checkBox = new CheckBox();
                    Text text = new Text("Accettare ricompensa?");

                    text.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                    text.setFill(Color.WHITE);

                    checkBox.setSelected(false);
                    checkBox.setMinSize(cardDataPane.getHeight()/7, cardDataPane.getHeight()/7);
                    checkBox.setOnAction(event -> {
                        cardInput.accept = checkBox.isSelected();
                        event.consume();
                    });
                    vBox.getChildren().add(text);
                    vBox.getChildren().add(checkBox);
                    break;
                case METEORS:
                    cardData.projectiles.forEach(p -> {
                        ImageView view = new ImageView(Launcher.getImage("/gui/textures/asteroid/" + p.type().name().toLowerCase() + ".png"));
                        view.setRotate(switch(p.side()){
                            case UP -> 0;
                            case RIGHT -> 90;
                            case DOWN -> 180;
                            case LEFT -> 270;
                        });


                        switch(p.side()){
                            case UP -> {
                                if(p.where() < 4 || p.where() > 10)
                                    break;
                                view.setFitHeight(upGrid.getHeight());
                                view.setFitWidth(upGrid.getWidth()/7);
                                upGrid.add(view, p.where()-4, 0);
                            }
                            case RIGHT -> {
                                if(p.where() < 5 || p.where() > 9)
                                    break;
                                view.setFitHeight(leftGrid.getHeight()/5);
                                view.setFitWidth(leftGrid.getWidth());
                                rightGrid.add(view, 0, p.where()-5);
                            }
                            case DOWN -> {
                                if(p.where() < 4 || p.where() > 10)
                                    break;
                                view.setFitHeight(upGrid.getHeight());
                                view.setFitWidth(upGrid.getWidth()/7);
                                downGrid.add(view, p.where()-4, 0);
                            }
                            case LEFT -> {
                                if(p.where() < 5 || p.where() > 9)
                                    break;
                                view.setFitHeight(leftGrid.getHeight()/5);
                                view.setFitWidth(leftGrid.getWidth());
                                leftGrid.add(view, 0, p.where()-5);
                            }
                        }
                    });

                    break;
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

            upGrid.getChildren().clear();
            leftGrid.getChildren().clear();
            rightGrid.getChildren().clear();
            downGrid.getChildren().clear();

            rectangles.forEach((r, p) -> {
                p.getChildren().remove(r);
            });

            rectangles.clear();

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
                    ImageView view = new ImageView(Launcher.getImage("/gui/textures/" + r.name().toLowerCase() + ".png"));
                    vBox.getChildren().add(view);

                    view.setOnDragDetected(event -> {
                        if(view.getImage() == null)
                            return;

                        view.setCursor(Cursor.CLOSED_HAND);

                        Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
                        ClipboardContent content = new ClipboardContent();
                        content.putString(r.name());
                        db.setContent(content);
                        db.setDragView(Launcher.getRotatedImage(view.getImage(), 0));
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

            if(output.removed.containsKey(server.getPlayerName())){
                output.removed.get(server.getPlayerName()).forEach(c -> {
                    shipPane.getChildren().removeIf(node -> {
                        int nodeCol = GridPane.getColumnIndex(node) == null ? 0 : GridPane.getColumnIndex(node);
                        int nodeRow = GridPane.getRowIndex(node) == null ? 0 : GridPane.getRowIndex(node);
                        return nodeCol == c.x() && nodeRow == c.y();
                    });
                });
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
