package it.polimi.softeng.is25am10.gui;

import com.googlecode.lanterna.gui2.TextBox;
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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.transform.Rotate;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Building implements Callback{
    ClientInterface server;

    Tile drawnTile = null;
    int rotation = 0;
    ImageView drawnTileView = null;

    ArrayList<Tile> seenTiles = new ArrayList<>();
    ArrayList<ImageView> seenTileViews = new ArrayList<>();

    @FXML
    GridPane shipPane;

    @FXML
    Pane drawTilePane;

    @FXML
    ScrollPane seenScrollPane;

    @FXML
    ImageView clock1, clock2, clock3;

    @FXML
    Label secondsLabel;

    @FXML
    Label stateLabel;

    @FXML
    Label nameLabel;

    @FXML
    Label redLabel, blueLabel, yellowLabel, greenLabel;

    HBox seenImages = new HBox();

    @FXML
    private void initialize(){
        seenScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        seenScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        seenScrollPane.setContent(seenImages);

        clock2.setVisible(false);
        clock3.setVisible(false);

        drawnTileView = new ImageView();
        drawTilePane.getChildren().add(drawnTileView);

        drawnTileView.setOnDragDetected(event -> {
            if(drawnTileView.getImage() == null)
                return;

            Dragboard db = drawnTileView.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(drawnTile.toString());
            db.setContent(content);
            db.setDragView(getRotatedImage(drawnTileView.getImage(), rotation*90));
            event.consume();
        });

        shipPane.setOnDragOver(event -> {
            if (event.getGestureSource() != shipPane && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });


        shipPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if(db.hasString()){
                Tile t = new Tile(db.getString());
                boolean isFromSeen = false;

                if(seenTileViews.contains(event.getGestureSource())){
                    if(server.getTileFromSeen(t).isErr())
                        return;
                    isFromSeen = true;
                }

                int col = (int)(event.getX() / (shipPane.getWidth() / 7));
                int row = (int)(event.getY() / (shipPane.getHeight() / 5));

                System.out.println("x: " + col + " y: " + row);


                if(Coordinate.isInvalid(col, row))
                    return;

                if(server.setTile(new Coordinate(col, row), t, Tile.Rotation.fromInt(rotation)).isOk()){
                    if(event.getGestureSource() == drawnTileView) {
                        drawnTileView.setImage(null);
                        drawnTile = null;
                    }
                }
                else{
                    if(isFromSeen)
                        server.giveTile(t);
                }

                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    public Image getRotatedImage(Image original, double angleDegrees) {
        double size = drawTilePane.getHeight()/3;

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

    public void setServer(ClientInterface server){
        this.server = server;
        server.join(this).ifPresent(pawn -> {
            String path = "/tiles/C_HOUSE/3333_" + switch(pawn){
                case YELLOW -> "yellow";
                case GREEN -> "green";
                case BLUE -> "blue";
                case RED -> "red";
            } + ".jpg";

            Image image = new Image(getClass().getResource(path).toExternalForm());
            ImageView view = new ImageView(image);
            view.setFitHeight(shipPane.getHeight()/5);
            view.setFitWidth(shipPane.getWidth()/7);
            shipPane.add(view, 3, 2);
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
            if(drawnTile != null)
                server.giveTile(drawnTile);
            rotation = 0;
            drawnTile = t;
            drawnTileView.setImage(getImage(t));
            drawnTileView.setFitWidth(drawTilePane.getWidth());
            drawnTileView.setFitHeight(drawTilePane.getHeight());
        });
    }

    @FXML
    private void rotateTile(){
        if(drawnTile == null)
            return;

        rotation = (rotation + 1) % 4;
        drawnTileView.setRotate(rotation*90);

        seenTileViews.forEach(tileView -> tileView.setRotate(rotation*90));
    }

    @FXML
    private void moveClock(){
        server.moveTimer();
    }



    private Image getImage(Tile t){
        return new Image(getClass().getResource("/tiles/" + t.getType().name() + "/" + t.connectorsToInt() + ".jpg").toExternalForm());
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
            seenTiles.addLast(t);
            ImageView tileView = new ImageView(getImage(t));
            tileView.setFitWidth(seenScrollPane.getHeight());
            tileView.setFitHeight(seenScrollPane.getHeight());
            seenTileViews.addLast(tileView);
            seenImages.getChildren().add(tileView);

            tileView.setOnDragDetected(event -> {
                System.out.println("Drag da viste");
                Dragboard db = tileView.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(t.toString());
                db.setContent(content);
                db.setDragView(getRotatedImage(tileView.getImage(), rotation*90));
                event.consume();
            });
        });
    }

    @Override
    public void gotTile(Tile t) throws RemoteException {
        Platform.runLater(() -> {
            seenImages.getChildren().remove(seenTileViews.remove(seenTiles.indexOf(t)));
            seenTiles.remove(t);
        });
    }

    @Override
    public void pushBoard(ShipBoard board) throws RemoteException {

    }

    @Override
    public void pushFlight(FlightBoard board) throws RemoteException {
        Platform.runLater(() -> {
            switch(board.getTimer()){
                case 0 -> {
                    clock1.setVisible(true);
                    clock2.setVisible(false);
                    clock3.setVisible(false);
                }
                case 1 -> {
                    clock1.setVisible(false);
                    clock2.setVisible(true);
                    clock3.setVisible(false);
                }
                case 2 -> {
                    clock1.setVisible(false);
                    clock2.setVisible(false);
                    clock3.setVisible(true);
                }
                default -> {}
            }
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
