package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardOutput;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.transform.Rotate;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Building implements Callback {
    ClientInterface server;
    Scene scene;

    int rotation = 0;

    ImageView tileView = null;
    Map<ImageView, Tile> imgToTile = new HashMap<>();
    VBox seenImages = new VBox();

    AtomicBoolean dragSuccess = new AtomicBoolean(false);

    @FXML
    GridPane shipPane;

    @FXML
    Pane drawTilePane;

    @FXML
    ScrollPane seenScrollPane;

    @FXML
    ImageView clock1, clock2, clock3;
    ImageView[] clocks;

    @FXML
    Label secondsLabel;

    @FXML
    Label stateLabel;

    @FXML
    Label nameLabel;

    @FXML
    Label redLabel, blueLabel, yellowLabel, greenLabel;

    @FXML
    VBox bookedBox;

    @FXML
    private void initialize(){
        //configure scrollable panel
        seenScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        seenScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        seenScrollPane.setContent(seenImages);

        //configure clocks
        clock2.setVisible(false);
        clock3.setVisible(false);
        clocks = new ImageView[]{clock1, clock2, clock3};

        //configure drawn tiles
        tileView = new ImageView();
        drawTilePane.getChildren().add(tileView);

        //register dragOver for ship and bookedTiles
        List.of(shipPane, bookedBox).forEach(box ->{
            box.setOnDragOver(event -> {
                if (event.getGestureSource() != box && event.getDragboard().hasString())
                    event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            });
        });

        //register drag dropped on booked tiles
        bookedBox.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            event.setDropCompleted(true);
            event.consume();

            if (!db.hasString())
                return;

            server.bookTile(new Tile(db.getString()))
                    .ifPresent(_ -> {dragSuccess.set(true);});
        });

        //register the ship placing tile
        shipPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            event.setDropCompleted(true);
            event.consume();

            if(!db.hasString())
                return;

            Tile t = new Tile(db.getString());

            int col = (int)(event.getX() / (shipPane.getWidth() / 7));
            int row = (int)(event.getY() / (shipPane.getHeight() / 5));

            if(Coordinate.isInvalid(col, row))
                return;

            Tile.Rotation rot = Tile.Rotation.fromInt(rotation);
            Coordinate c = new Coordinate(col, row);

            if(bookedBox.getChildren().contains(event.getGestureSource()))
                server.useBookedTile(t, rot, c).ifPresent(_ -> dragSuccess.set(true));
            else
                server.setTile(c, t, rot).ifPresent(_ -> dragSuccess.set(true));
        });
    }

    public void setScene(Scene scene){
        this.scene = scene;
        scene.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.A)
                drawTile();
            else if(event.getCode() == KeyCode.S)
                rotateTile();
            else if(event.getCode() == KeyCode.D){
                if(!imgToTile.containsKey(tileView))
                    return;
                server.bookTile(imgToTile.get(tileView)).ifPresent(_ -> {
                    tileView.setImage(null);
                    imgToTile.remove(tileView);
                });
            }
            else if(event.getCode() == KeyCode.C)
                moveClock();
        });
    }

    public void setServer(ClientInterface server){
        this.server = server;
        try {
            server.join(new Listener(this)).ifPresent(pawn -> {
                ImageView view = new ImageView(getCHouse(pawn));
                view.setFitHeight(shipPane.getHeight()/5);
                view.setFitWidth(shipPane.getWidth()/7);
                shipPane.add(view, 3, 2);
            });
        } catch (RemoteException e) {
            throw new RuntimeException("Impossibile connettersi al server", e);
        }
    }

    private void register(ImageView view, Tile t, Runnable whenDone){
        view.setRotate(rotation*90);
        view.setFitWidth(seenScrollPane.getWidth());
        view.setFitHeight(seenScrollPane.getWidth());

        imgToTile.put(view, t);
        view.setOnDragDetected(event -> {
            if(view.getImage() == null)
                return;

            Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(imgToTile.get(view).toString());
            db.setContent(content);
            db.setDragView(getRotatedImage(view.getImage(), rotation*90));
            event.consume();
        });

        view.setOnDragDone(event -> {
            whenDone.run();
            dragSuccess.set(false);
            event.consume();
        });
    }

    public void setPlayerName(String name){
        nameLabel.setText(name);
    }

    @FXML
    private void playerReady(){

    }

    @FXML
    private void drawTile(){
        server.drawTile().ifPresent(t -> {
            if(imgToTile.containsKey(tileView))
                server.giveTile(imgToTile.get(tileView));

            register(tileView, t, () -> {
                if (dragSuccess.get()) {
                    tileView.setImage(null);
                    imgToTile.remove(tileView);
                }
            });

            tileView.setImage(getImage(t));
        });
    }

    @FXML
    private void rotateTile(){
        rotation = (rotation + 1) % 4;
        imgToTile.keySet().forEach(i -> i.setRotate(rotation*90));
    }

    @FXML
    private void moveClock(){
        server.moveTimer();
    }


    @Override
    public void pushPlayers(HashMap<String, FlightBoard.Pawn> players, HashSet<String> quid, HashSet<String> disconnected) throws RemoteException {
        Platform.runLater(() -> {
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
        return 0;
    }

    @Override
    public void pushSecondsLeft(Integer seconds) throws RemoteException {
        Platform.runLater(() -> {
            secondsLabel.setText("" +seconds);
        });
    }

    @Override
    public void pushState(Model.State.Type state) throws RemoteException {
        Platform.runLater(() -> {
            stateLabel.setText(state.name());
        });
    }

    @Override
    public void pushCardData(CardData card) throws RemoteException {

    }

    @Override
    public void pushCardChanges(CardOutput output) throws RemoteException {

    }

    @Override
    public void waitFor(String name, FlightBoard.Pawn pawn) throws RemoteException {

    }

    @Override
    public void gaveTile(Tile t) throws RemoteException {
        Platform.runLater(() -> {
            ImageView tileView = new ImageView(getImage(t));

            register(tileView, t, () -> {
                if(dragSuccess.get())
                    seenImages.getChildren().remove(tileView);
            });

            seenImages.getChildren().add(tileView);
        });
    }

    @Override
    public void gotTile(Tile t) throws RemoteException {
        Platform.runLater(() -> {
            Optional<ImageView> opt = imgToTile
                                        .keySet()
                                        .stream()
                                        .filter(e -> imgToTile.get(e).equals(t))
                                        .findFirst();
            opt.ifPresent(image -> {
                seenImages.getChildren().remove(image);
                imgToTile.remove(image);
            });
        });
    }

    @Override
    public void pushBoard(ShipBoard board) throws RemoteException {

    }

    @Override
    public void pushFlight(FlightBoard board) throws RemoteException {
        Platform.runLater(() -> {
            for (ImageView clock : clocks)
                clock.setVisible(false);
            clocks[board.getTimer()].setVisible(true);
        });
    }

    @Override
    public int ping() throws RemoteException {
        return 0;
    }

    @Override
    public void placeTile(Coordinate c, Tile t, Tile.Rotation r) throws RemoteException {
        Platform.runLater(() -> {
            ImageView view = new ImageView(getImage(t));
            view.setFitHeight(shipPane.getHeight() / 5);
            view.setFitWidth(shipPane.getWidth() / 7);
            view.setRotate(r.toInt()*90);

            shipPane.add(view, c.x(), c.y());
        });
    }

    @Override
    public void bookedTile(Tile t) throws RemoteException {
        Platform.runLater(() -> {
            ImageView view = new ImageView(getImage(t));

            register(view, t, () -> {
                if(dragSuccess.get())
                    bookedBox.getChildren().remove(view);
            });

            bookedBox.getChildren().add(view);
        });
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

    static public Image getRotatedImage(Image original, double angleDegrees) {
        double size = 70;

        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.save();
        Rotate r = new Rotate(angleDegrees, size/2, size/2);
        gc.transform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.drawImage(original, 0, 0, size, size);
        gc.restore();

        WritableImage rotatedImage = new WritableImage((int) size, (int) size);
        canvas.snapshot(null, rotatedImage);

        return rotatedImage;
    }

    private Image getImage(Tile t){
        return new Image(getClass().getResource("/tiles/" + t.getType().name() + "/" + t.connectorsToInt() + ".jpg").toExternalForm());
    }

    private Image getCHouse(FlightBoard.Pawn p){
        String path = "/tiles/C_HOUSE/3333_" + switch(p){
            case YELLOW -> "yellow";
            case GREEN -> "green";
            case BLUE -> "blue";
            case RED -> "red";
        } + ".jpg";

        return new Image(getClass().getResource(path).toExternalForm());
    }
}
