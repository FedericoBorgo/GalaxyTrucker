package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
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
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class CardScene implements Callback {
    ClientInterface server;
    Model.State.Type state;
    HashMap<String, FlightBoard.Pawn> players;
    ShipBoard ship;

    @FXML
    Label stateLabel, nameLabel;

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
    void initialize() {
        posPanes = new Pane[]{posPane0, posPane1, posPane2, posPane3, posPane4, posPane5, posPane6,
        posPane7, posPane8, posPane9, posPane10, posPane11, posPane12, posPane13, posPane14, posPane15,
        posPane16, posPane17, posPane18, posPane19, posPane20, posPane21, posPane22, posPane23};
    }

    void config(ClientInterface server, Listener listener, FlightBoard flight, String state, ShipBoard ship, HashMap<String, FlightBoard.Pawn> players) {
        this.listener = listener;
        this.server = server;
        this.ship = ship;
        this.players = players;
        listener.setCallback(this);
        updatePos(flight);
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

    }

    @Override
    public void pushCardChanges(CardOutput output) throws RemoteException {

    }

    @Override
    public void waitFor(String name, FlightBoard.Pawn pawn) throws RemoteException {

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
