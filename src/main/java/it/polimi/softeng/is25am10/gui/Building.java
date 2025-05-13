package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.*;
import it.polimi.softeng.is25am10.model.boards.*;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardOutput;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.rmi.RemoteException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Building implements Callback {
    public ClientInterface server;

    int rotation = 0;
    public int nPlayers = 0;

    public FlightBoard board;
    public HashMap<String, FlightBoard.Pawn> players;
    public State.Type state = State.Type.JOINING;
    public ShipBoard ship = new ShipBoard();

    ImageView tileView = null;
    Map<ImageView, Tile> imgToTile = new HashMap<>();
    VBox seenImages = new VBox();

    public AtomicBoolean dragSuccess = new AtomicBoolean(false);

    public Result<Coordinate> purple = Result.err();
    public Result<Coordinate> brown = Result.err();

    public GUIEventListener listener;

    @FXML
    public GridPane shipPane;

    @FXML
    Pane drawTilePane;

    @FXML
    ScrollPane seenScrollPane;

    @FXML
    public ImageView clock1, clock2, clock3;

    @FXML
    Label secondsLabel;

    @FXML
    public Label stateLabel;

    @FXML
    Label nameLabel;

    @FXML
    public Label redLabel, blueLabel, yellowLabel, greenLabel;

    @FXML
    VBox bookedBox;

    @FXML
    public Label buildingLabel;

    @FXML
    public ImageView pAlienView, bAlienView;

    @FXML
    public Text shipFixText;

    public Map<Rectangle, StackPane> rectangles = new HashMap<>();
    public StackPane[][] stackPanes = new StackPane[TilesBoard.BOARD_WIDTH][TilesBoard.BOARD_HEIGHT];

    @FXML
    private void initialize(){
        //configure scrollable panel
        seenScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        seenScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        seenScrollPane.setContent(seenImages);
        shipFixText.setVisible(false);

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

        shipPane.setOnMouseClicked(event -> {
            if(state != State.Type.CHECKING || ship.getTiles().isOK().isEmpty())
                return;

            int col = (int)(event.getX()/(shipPane.getWidth()/7));
            int row = (int)(event.getY()/(shipPane.getHeight()/5));

            if(Coordinate.isInvalid(col,row))
                return;

            Coordinate c = new Coordinate(col,row);

            server.remove(c).ifPresent(_ -> {
                ship.getTiles().remove(c);

                shipPane.getChildren().removeIf(node -> {
                    int nodeCol = GridPane.getColumnIndex(node) == null ? 0 : GridPane.getColumnIndex(node);
                    int nodeRow = GridPane.getRowIndex(node) == null ? 0 : GridPane.getRowIndex(node);
                    return nodeCol == c.x() && nodeRow == c.y();
                });

                ship.getTiles().drawErrors(this);
            });
        });

        //register the ship placing tile
        shipPane.setOnDragDropped(event -> {
            Optional<Pair<Coordinate, String>> opt = Launcher.getCoordinate(event, shipPane, dragSuccess);
            if(opt.isEmpty())
                return;

            Coordinate c = opt.get().getKey();

            if(state == State.Type.BUILDING){
                Tile t = new Tile(opt.get().getValue());
                Tile.Rotation rot = Tile.Rotation.fromInt(rotation);

                if(bookedBox.getChildren().contains(event.getGestureSource()))
                    server.useBookedTile(t, rot, c).ifPresent(_ -> dragSuccess.set(true));
                else if(seenImages.getChildren().contains(event.getGestureSource())){
                    Result<Tile> res = server.getTileFromSeen(t);

                    if(res.isErr())
                        return;

                    res = server.setTile(c, t, rot);

                    if(res.isOk())
                        dragSuccess.set(true);
                    else
                        server.giveTile(t);
                }
                else
                    server.setTile(c, t, rot).ifPresent(_ -> dragSuccess.set(true));
            }
            else if(state == State.Type.ALIEN_INPUT){
                ImageView view = new ImageView();
                view.setFitHeight(shipPane.getHeight()/7);
                view.setFitWidth(shipPane.getHeight()/7);

                if(opt.get().getValue().equals("p"))
                    ((AlienBoard)ship.getPurple()).placeAlien(this, c, view);
                else if(opt.get().getValue().equals("b"))
                    ((AlienBoard)ship.getBrown()).placeAlien(this, c, view);

                if(dragSuccess.get())
                    shipPane.add(new StackPane(view), c.x(), c.y());
            }
        });

    }

    public void config(FlightBoard.Pawn pawn, String name, ClientInterface server, Scene scene){
        this.server = server;
        nameLabel.setText(name);

        ImageView view = new ImageView(Launcher.getCHouse(pawn));
        resizeForShip(view);
        shipPane.add(view, 3, 2);

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

        new AutoBuilder(this);
    }

    private void register(ImageView view, Tile t, Runnable whenDone){
        view.setRotate(rotation*90);
        view.setFitWidth(seenScrollPane.getWidth());
        view.setFitHeight(seenScrollPane.getWidth());

        imgToTile.put(view, t);
        view.setOnDragDetected(event -> {
            if(view.getImage() == null)
                return;

            view.setCursor(Cursor.CLOSED_HAND);

            Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(imgToTile.get(view).toString());
            db.setContent(content);
            db.setDragView(Launcher.getRotatedImage(view.getImage(), rotation*90));
            event.consume();
        });

        view.setOnDragDone(event -> {
            whenDone.run();
            dragSuccess.set(false);
            event.consume();
        });

        view.setOnMousePressed(event -> {
            view.setCursor(Cursor.CLOSED_HAND);
            event.consume();
        });

        view.setOnMouseEntered(_ -> {view.setCursor(Cursor.OPEN_HAND);});
        view.setOnMouseExited(_ -> {view.setCursor(Cursor.DEFAULT);});
    }

    @FXML
    private void playerReady(){
        state.ready(this);
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

            tileView.setImage(Launcher.getImage(t));
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
        this.players = players;
        Player.drawPlayers(players, quid, disconnected, yellowLabel, redLabel, blueLabel, greenLabel);
    }

    @Override
    public int askHowManyPlayers() throws RemoteException {
        return nPlayers;
    }

    @Override
    public void pushSecondsLeft(Integer seconds) throws RemoteException {
        secondsLabel.setText("" +seconds);
    }

    @Override
    public void pushState(State.Type state) throws RemoteException {
        this.state = state;
        stateLabel.setText(state.getName());
        state.apply(this);
    }

    @Override
    public void gaveTile(Tile t) throws RemoteException {
        register(t, seenImages);
    }

    @Override
    public void gotTile(Tile t) throws RemoteException {
        imgToTile.keySet()
                .stream()
                .filter(e -> imgToTile.get(e).equals(t))
                .findFirst()
                .ifPresent(image -> {
                    seenImages.getChildren().remove(image);
                    imgToTile.remove(image);
                });
    }

    @Override
    public void pushFlight(FlightBoard board) throws RemoteException {
        this.board = board;
        board.updateClock(this);
    }

    @Override
    public int ping() throws RemoteException {
        return 0;
    }

    @Override
    public void placeTile(Coordinate c, Tile t, Tile.Rotation r) throws RemoteException {
        ImageView view = Launcher.getView(t);
        resizeForShip(view);
        view.setRotate(r.toInt()*90);
        shipPane.add(view, c.x(), c.y());
        stackPanes[c.x()][c.y()] = new StackPane();
        shipPane.add(stackPanes[c.x()][c.y()], c.x(), c.y());
        ship.getTiles().setTile(c, t, r);
        ship.getTiles().drawErrors(this);
    }

    @Override
    public void bookedTile(Tile t) throws RemoteException {
        register(t, bookedBox);
    }

    private void register(Tile t, Pane p){
        ImageView view = Launcher.getView(t);

        register(view, t, () -> {
            if(dragSuccess.get())
                p.getChildren().remove(view);
        });

        p.getChildren().add(view);
    }


    @Override
    public void removed(Coordinate c) throws RemoteException {}
    @Override
    public void pushDropped(Model.Removed dropped) throws RemoteException {}
    @Override
    public void pushCannons(HashMap<Tile.Rotation, Integer> cannons) throws RemoteException {}
    @Override
    public void pushModel(Model m) throws RemoteException {}
    @Override
    public void pushBoard(ShipBoard board) throws RemoteException {}
    @Override
    public void pushCardData(CardData card) throws RemoteException {}
    @Override
    public void pushCardChanges(CardOutput output) throws RemoteException {}
    @Override
    public void waitFor(String name, FlightBoard.Pawn pawn) throws RemoteException {}

    private void resizeForShip(ImageView view){
        view.setFitHeight(shipPane.getHeight()/5);
        view.setFitWidth(shipPane.getWidth()/7);
    }
}
