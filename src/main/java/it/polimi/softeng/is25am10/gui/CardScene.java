package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.boards.TilesBoard;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardInput;
import it.polimi.softeng.is25am10.model.cards.CardOutput;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class CardScene implements Callback {
    ClientInterface server;
    Model.State.Type state;
    HashMap<String, FlightBoard.Pawn> players;
    CardInput cardInput = new CardInput();

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
        Result<CardInput> res = server.setInput(cardInput);

        if(res.isErr())
            errLabel.setText(res.getReason());
        else
            errLabel.setText("scelta dichiarata");
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

            shipPane.add(view, c.x(), c.y());
        });

        ship.getAstronaut().getPositions().forEach((c, qty) -> {
            StackPane cell = new StackPane();
            ImageView view = new ImageView(Building.getImage("/gui/astronaut.png"));
            Text count = new Text(qty.toString() + "x");

            count.setFont(Font.font("Arial Black", FontWeight.BOLD, 30));
            count.setFill(Color.BLACK);
            count.setStroke(Color.WHITE);
            count.setStrokeWidth(2);

            view.setFitHeight(shipPane.getHeight()/7);
            view.setFitWidth(shipPane.getHeight()/7);

            cell.getChildren().add(view);
            cell.getChildren().add(count);
            StackPane.setAlignment(view, Pos.CENTER);
            StackPane.setAlignment(count, Pos.BOTTOM_RIGHT);

            shipPane.add(cell, c.x(), c.y());
            counters[c.x()][c.y()] = count;
            stackPanes[c.x()][c.y()] = cell;
        });

        ship.getBattery().getPositions().forEach((c, qty) -> {
            StackPane cell = new StackPane();
            ImageView view = new ImageView(Building.getImage("/gui/battery.png"));

            Text count = new Text(qty.toString() + "x");
            count.setFont(Font.font("Arial Black", FontWeight.BOLD, 30));
            count.setFill(Color.BLACK);
            count.setStroke(Color.WHITE);
            count.setStrokeWidth(2);

            view.setFitHeight(shipPane.getHeight()/7);
            view.setFitWidth(shipPane.getHeight()/7);

            cell.getChildren().add(view);
            cell.getChildren().add(count);
            StackPane.setAlignment(view, Pos.CENTER);
            StackPane.setAlignment(count, Pos.BOTTOM_RIGHT);

            shipPane.add(cell, c.x(), c.y());
            counters[c.x()][c.y()] = count;
            stackPanes[c.x()][c.y()] = cell;
        });

        ship.getPurple().getPositions().forEach((c, _) -> {
            StackPane cell = new StackPane();
            ImageView view = new ImageView(Building.getImage("/gui/purple.png"));
            view.setFitHeight(shipPane.getHeight()/7);
            view.setFitWidth(shipPane.getHeight()/7);
            cell.getChildren().add(view);
            StackPane.setAlignment(view, Pos.CENTER);
            shipPane.add(cell, c.x(), c.y());
            counters[c.x()][c.y()] = new Text("1x");
            stackPanes[c.x()][c.y()] = cell;
        });

        ship.getBrown().getPositions().forEach((c, _) -> {
            StackPane cell = new StackPane();
            ImageView view = new ImageView(Building.getImage("/gui/brown.png"));
            view.setFitHeight(shipPane.getHeight()/7);
            view.setFitWidth(shipPane.getHeight()/7);
            cell.getChildren().add(view);
            StackPane.setAlignment(view, Pos.CENTER);
            shipPane.add(cell, c.x(), c.y());
            counters[c.x()][c.y()] = new Text("1x");
            stackPanes[c.x()][c.y()] = cell;
        });

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
        Platform.runLater(() -> {
            Image img = Building.getImage("/cards/" + card.type.name()+ "/" + card.id + ".jpg");
            ImageView imgView = new ImageView(img);
            imgView.setFitHeight(cardPane.getHeight());
            imgView.setFitWidth(cardPane.getWidth());
            cardPane.getChildren().add(imgView);
        });
    }

    @Override
    public void pushCardChanges(CardOutput output) throws RemoteException {
        cardInput = new CardInput();
        Platform.runLater(() -> {
            errLabel.setText("");
            cardPane.getChildren().clear();

            output.killedCrew.get(server.getPlayerName()).forEach(c ->{
                int val = counters[c.x()][c.y()].getText().charAt(0) - '0';
                val--;

                if(val <= 0)
                    stackPanes[c.x()][c.y()].getChildren().clear();
                else
                    counters[c.x()][c.y()].setText(val + "x");
            });

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
