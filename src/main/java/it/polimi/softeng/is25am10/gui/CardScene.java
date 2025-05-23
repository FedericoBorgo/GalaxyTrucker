package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.*;
import it.polimi.softeng.is25am10.model.boards.*;
import it.polimi.softeng.is25am10.model.cards.*;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CardScene implements Callback {
    public ClientInterface server;
    State.Type state;
    HashMap<String, FlightBoard.Pawn> players;
    public CardInput cardInput = new CardInput();
    public AtomicBoolean dragSuccess = new AtomicBoolean(false);
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
    public Pane[] posPanes;

    @FXML
    public GridPane shipPane;

    @FXML
    Label errLabel;

    @FXML
    public Text shipFixText;

    @FXML
    ImageView arrowView0, arrowView1, arrowView2, arrowView3;

    @FXML
    public GridPane downGrid;
    @FXML
    public GridPane upGrid;
    @FXML
    public GridPane leftGrid;
    @FXML
    public GridPane rightGrid;

    public Map<Coordinate, ImageView> images = new HashMap<>();
    public Map<Rectangle, StackPane> rectangles = new HashMap<>();
    Text[][] counters = new Text[TilesBoard.BOARD_WIDTH][TilesBoard.BOARD_HEIGHT];
    public StackPane[][] stackPanes = new StackPane[TilesBoard.BOARD_WIDTH][TilesBoard.BOARD_HEIGHT];
    Map<Coordinate, List<Pos>> freeContainers = new HashMap<>();
    @FXML
    Pane holePane;

    @FXML
    Text engineText, cannonText;

    @FXML
    public Pane cardDataPane;

    @FXML
    public Text cashText;

    @FXML
    private Button readyButton;
    @FXML
    private Button quitButton;
    @FXML
    private Button drawButton;

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
        flight.drawPos(this);
        updateNextTurn(flight.getOrder().getFirst());
        stateLabel.setText(state);
        nameLabel.setText(server.getPlayerName());

        // saves shipboard from Building to CardScene
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

            if(data.contains("box")){
                String[] split = data.split(" ");
                GoodsBoard.Type t = GoodsBoard.Type.valueOf(split[1]);
                Coordinate c = Coordinate.fromString(split[2]).getData();

                server.drop(c, t).ifPresent(_ -> dragSuccess.set(true));
            }

            if(data.contains("astronaut")){
                Coordinate from = Coordinate.fromString(data.substring(data.indexOf(' ')+1)).getData();
                server.drop(from).ifPresent(_ -> dragSuccess.set(true));
            }

            if(this.state == State.Type.PAY_DEBT){
                cardDataPane.getChildren().clear();
                Model.Removed rm = server.getRemoved();
                VBox box = new VBox();

                if(rm.guys < 0){
                    Text text = new Text("Astronauti: " + rm.guys);
                    text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                    text.setFill(Color.WHITE);
                    box.getChildren().add(text);
                }

                if(rm.goods < 0){
                    Text text = new Text("Astronauti: " + rm.guys);
                    text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                    text.setFill(Color.WHITE);
                    box.getChildren().add(text);
                }

                cardDataPane.getChildren().add(box);
            }
        });

        shipPane.setOnDragDropped(event -> {
            Optional<Pair<Coordinate, String>> opt = Launcher.getCoordinate(event, shipPane, dragSuccess);

            if(opt.isEmpty() || cardData == null)
                return;

            Coordinate c = opt.get().getKey();
            String content = opt.get().getValue();

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

                    if(cardData.type == Card.Type.OPEN_SPACE && t != Tile.Type.D_ENGINE)
                        return;

                    if((cardData.type == Card.Type.PIRATES || cardData.type == Card.Type.SMUGGLERS) && t != Tile.Type.D_CANNON)
                        return;

                    if(cardData.type == Card.Type.WAR_ZONE && (t != Tile.Type.D_ENGINE && t != Tile.Type.D_CANNON))
                        return;

                    Coordinate from = Coordinate.fromString(content.substring(content.indexOf(' ') + 1)).getData();

                    server.drop(from).ifPresent(_ -> {
                        dragSuccess.set(true);

                        if(cardData.type == Card.Type.PIRATES || cardData.type == Card.Type.SMUGGLERS)
                            server.increaseCannon(ship.getTiles().getRotation(c), 1);

                        if(cardData.type == Card.Type.WAR_ZONE && t == Tile.Type.D_CANNON)
                            server.increaseCannon(ship.getTiles().getRotation(c), 1);

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

                    view.setOnDragDetected(e -> {
                        view.setCursor(Cursor.CLOSED_HAND);
                        Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
                        ClipboardContent clipboardContent = new ClipboardContent();
                        clipboardContent.putString("box " + type + " " + c);
                        db.setContent(clipboardContent);
                        db.setDragView(Launcher.getRotatedImage(view.getImage(), 0));
                        e.consume();
                    });

                    view.setOnDragDone(e -> {
                        e.consume();
                        if(!dragSuccess.get())
                            return;
                        dragSuccess.set(false);
                        stackPanes[c.x()][c.y()].getChildren().remove(view);
                        freeContainers.get(c).add(pos);
                    });

                    view.setOnMousePressed(e -> {
                        view.setCursor(Cursor.CLOSED_HAND);
                        e.consume();
                    });

                    view.setOnMouseEntered(_ -> {view.setCursor(Cursor.OPEN_HAND);});
                    view.setOnMouseExited(_ -> {view.setCursor(Cursor.DEFAULT);});
                });
            }
        });

        // EventHandler per visualizzare la nave di altri giocatori
        redLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            String playerName = redLabel.getText();
            if (!playerName.isEmpty()) {
                ShipBoard otherShip = server.getShip(playerName);
                showOtherShip(playerName, otherShip);
            }
        });
        yellowLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            String playerName = yellowLabel.getText();
            if (!playerName.isEmpty()) {
                ShipBoard otherShip = server.getShip(playerName);
                showOtherShip(playerName, otherShip);
            }
        });
        greenLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            String playerName = greenLabel.getText();
            if (!playerName.isEmpty()) {
                ShipBoard otherShip = server.getShip(playerName);
                showOtherShip(playerName, otherShip);
            }
        });
        blueLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            String playerName = blueLabel.getText();
            if (!playerName.isEmpty()) {
                ShipBoard otherShip = server.getShip(playerName);
                showOtherShip(playerName, otherShip);
            }
        });
    }

    public void showOtherShip(String shipOwner, ShipBoard otherShip) {
        if (shipOwner.equals(server.getPlayerName())) {
            // La funzione sta venendo chiamata per visualizzare la propria nave, non necessario
            return;
        }
        try {
            // Carica il file FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/viewShip.fxml"));
            Parent root = loader.load();

            // Trova i nodi usando gli fx:id
            GridPane shipPaneView = (GridPane) root.lookup("#shipPaneView");
            Label specialState = (Label) root.lookup("#specialState");

            if (shipPaneView == null || specialState == null || nameLabel == null) {
                throw new IllegalStateException("Uno o più elementi non trovati nell'FXML: shipPane, specialState, nameLabel");
            }

            // Scena e finestra
            Scene scene = new Scene(root, 1080, 601);
            Stage stage = new Stage();
            stage.setTitle("Galaxy Trucker - Visualizzatore navi");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            // Scritte nel gioco
            specialState.setText("Visualizzazione nave di " + shipOwner);

            // Usa le dimensioni specificate nell'FXML
            double shipPaneWidth = shipPaneView.getWidth(); // 504.0;
            double shipPaneHeight = shipPaneView.getPrefHeight(); // 365.0;

            // Ricopia nave
            Map<Coordinate, ImageView> images = new HashMap<>();
            Text[][] counters = new Text[TilesBoard.BOARD_WIDTH][TilesBoard.BOARD_HEIGHT];
            StackPane[][] stackPanes = new StackPane[TilesBoard.BOARD_WIDTH][TilesBoard.BOARD_HEIGHT];
            Map<Coordinate, List<Pos>> freeContainers = new HashMap<>();

            Coordinate.forEach(c -> {
                Result<Tile> res = otherShip.getTiles().getTile(c);
                if (res.isErr()) {
                    return;
                }
                Tile t = res.getData();
                Tile.Rotation r = otherShip.getTiles().getRotation(c);
                ImageView view;
                if (!Tile.real(t)) {
                    return;
                }
                if (t.getType() == Tile.Type.C_HOUSE) {
                    view = new ImageView(Launcher.getCHouse(players.get(shipOwner)));
                } else {
                    view = new ImageView(Launcher.getImage(t));
                }
                view.setFitHeight(shipPaneHeight / 5);
                view.setFitWidth(shipPaneWidth / 7);
                view.setPreserveRatio(true);
                view.setRotate(r.toInt() * 90);

                stackPanes[c.x()][c.y()] = new StackPane();
                shipPaneView.add(view, c.x(), c.y());
                shipPaneView.add(stackPanes[c.x()][c.y()], c.x(), c.y());
                images.put(c, view);

                if (Tile.box(t)) {
                    freeContainers.put(c, new ArrayList<>(List.of(Pos.TOP_LEFT, Pos.TOP_RIGHT, Pos.BOTTOM_RIGHT)));
                }
            });

            // Gestisce astronauti, batterie e alieni
            List<Pair<Map<Coordinate, Integer>, String>> boards = new ArrayList<>();
            boards.add(new Pair<>(otherShip.getAstronaut().getPositions(), "astronaut"));
            boards.add(new Pair<>(otherShip.getBattery().getPositions(), "battery"));
            boards.add(new Pair<>(otherShip.getPurple().getPositions(), "purple"));
            boards.add(new Pair<>(otherShip.getBrown().getPositions(), "brown"));

            boards.forEach(pair -> {
                String type = pair.getValue();
                pair.getKey().forEach((c, qty) -> {
                    StackPane cell = stackPanes[c.x()][c.y()];
                    if (cell == null) {
                        return;
                    }
                    ImageView view = new ImageView(Launcher.getImage("/gui/textures/" + type + ".png"));
                    Text count = new Text(qty.toString() + "x");
                    count.setFont(Font.font("Arial Black", FontWeight.BOLD, 30));
                    count.setFill(Color.BLACK);
                    count.setStroke(Color.WHITE);
                    count.setStrokeWidth(2);

                    view.setFitHeight(shipPaneHeight / 7);
                    view.setFitWidth(shipPaneWidth / 7);
                    view.setPreserveRatio(true);

                    cell.getChildren().add(view);
                    cell.getChildren().add(count);
                    StackPane.setAlignment(view, Pos.CENTER);
                    StackPane.setAlignment(count, Pos.BOTTOM_RIGHT);
                    counters[c.x()][c.y()] = count;
                });
            });

        } catch (Exception e) {
            if (errLabel != null) {
                errLabel.setText("Errore: impossibile visualizzare la nave di " + shipOwner + ". Dettaglio: " + e.getMessage());
            }
        }
    }


    public void removeOne(Coordinate c){
        int val = counters[c.x()][c.y()].getText().charAt(0) - '0';
        val--;

        if(val <= 0)
            stackPanes[c.x()][c.y()].getChildren().clear();
        else
            counters[c.x()][c.y()].setText(val + "x");
    }


    @Override
    public void pushPlayers(HashMap<String, FlightBoard.Pawn> players, HashSet<String> quid, HashSet<String> disconnected) throws RemoteException {
        this.players = players;
        Player.drawPlayers(players, quid, disconnected, yellowLabel, redLabel, blueLabel, greenLabel);
    }


    @Override
    public void pushState(State.Type state) throws RemoteException {
        this.state = state;
        stateLabel.setText(state.getName());
        state.apply(this);
    }

    public void drawErrors(){
        server.getShip().getTiles().drawErrors(this);
    }

    @FXML
    public void quit(){
        server.quit();
    }

    @Override
    public void pushCardData(CardData card) throws RemoteException {
        this.cardData = card;
        engineText.setText(String.valueOf(server.getEnginePower(server.getPlayerName())));
        cannonText.setText(String.valueOf((int)server.getCannonPower(server.getPlayerName())));

        Image img = Launcher.getImage("/cards/" + card.type.name()+ "/" + card.id + ".jpg");
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(cardPane.getHeight());
        imgView.setFitWidth(cardPane.getWidth());
        cardPane.getChildren().add(imgView);
        cardDataPane.getChildren().clear();

        cardDataPane.getChildren().add(card.handle(this));
    }

    @Override
    public void pushCardChanges(CardOutput output) throws RemoteException {
        cardInput = new CardInput();
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
        output.handleChanges(this, server.getPlayerName());

        Model.Removed rm = server.getRemoved();

        if(rm.isDebt(server.getShip())){
            VBox box = new VBox();

            if(rm.guys < 0){
                Text text = new Text("Astronauti: " + rm.guys);
                text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                text.setFill(Color.WHITE);
                box.getChildren().add(text);
            }

            if(rm.goods < 0){
                Text text = new Text("Astronauti: " + rm.guys);
                text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                text.setFill(Color.WHITE);
                box.getChildren().add(text);
            }

            cardDataPane.getChildren().add(box);
        }
    }

    @Override
    public void waitFor(String name, FlightBoard.Pawn pawn) throws RemoteException {
        updateNextTurn(pawn);
    }

    @Override
    public void pushFlight(FlightBoard board) throws RemoteException {
        board.drawPos(this);
        if(!board.getOrder().isEmpty())
            updateNextTurn(board.getOrder().getFirst());
    }

    private void updateNextTurn(FlightBoard.Pawn pawn){
        if(pawn == null)
            return;

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
    public int ping() throws RemoteException {
        return 0;
    }

    @Override
    public void pushFinalCash(HashMap<String, Integer> cash) throws RemoteException {
        Pair<End, Scene> p = Launcher.loadScene("/gui/end.fxml");
        p.getKey().setRes(cash);
    }

    @Override
    public void placeTile(Coordinate c, Tile t, Tile.Rotation r) throws RemoteException {}
    @Override
    public void bookedTile(Tile t) throws RemoteException {}
    @Override
    public void removed(Coordinate c) throws RemoteException {}
    @Override
    public void pushDropped(Model.Removed dropped) throws RemoteException {}
    @Override
    public void pushCannons(HashMap<Tile.Rotation, Integer> cannons) throws RemoteException {}
    @Override
    public void pushModel(Model m) throws RemoteException {}
    @Override
    public void gaveTile(Tile t) throws RemoteException {}
    @Override
    public void gotTile(Tile t) throws RemoteException {}
    @Override
    public void pushBoard(ShipBoard board) throws RemoteException {}
    @Override
    public int askHowManyPlayers() throws RemoteException {return 4;}
    @Override
    public void pushSecondsLeft(Integer seconds) throws RemoteException {}
}
