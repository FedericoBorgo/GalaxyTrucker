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
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.awt.*;
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
    int seenTilesIndex = 0;

    @FXML
    GridPane shipPane;

    @FXML
    Pane drawTilePane;

    @FXML
    Pane seenTilePane;

    public void setServer(ClientInterface server){
        this.server = server;
        server.join(this);
    }

    @FXML
    private void playerReady(){

    }

    @FXML
    private void drawTile(){
        server.drawTile().ifPresent(t -> {
            if(drawnTile != null)
                server.giveTile(drawnTile);

            drawnTile = t;


            Image image = new Image(getClass().getResource(getPath(t)).toExternalForm());
            drawnTileView = new ImageView(image);
            drawnTileView.setFitWidth(drawTilePane.getWidth());
            drawnTileView.setFitHeight(drawTilePane.getHeight());

            drawTilePane.getChildren().add(drawnTileView);
        });
    }

    @FXML
    private void rotateTile(){
        if(drawnTile == null)
            return;

        rotation = (rotation + 1) % 4;
        drawnTileView.setRotate(rotation*90);
    }

    @FXML
    private void bookTile(){

    }

    @FXML
    private void moveClock(){

    }

    @FXML
    private void leftSeen(){
        if(seenTilesIndex > 0)
            seenTilesIndex--;

        drawSeenTile();
    }

    @FXML
    private void rightSeen(){
        if(seenTilesIndex < seenTiles.size()-1)
            seenTilesIndex++;

        drawSeenTile();
    }

    private void drawSeenTile(){
        Image image = new Image(getClass().getResource(getPath(seenTiles.get(seenTilesIndex))).toExternalForm());
        ImageView view = new ImageView(image);
        view.setFitHeight(seenTilePane.getHeight());
        view.setFitWidth(seenTilePane.getWidth());
        seenTilePane.getChildren().add(view);
    }

    private String getPath(Tile t){
        return "/tiles/" + t.getType().name() + "/" + t.connectorsToInt() + ".jpg";
    }








    @Override
    public void pushPlayers(HashMap<String, FlightBoard.Pawn> players, HashSet<String> quid, HashSet<String> disconnected) throws RemoteException {

    }

    @Override
    public int askHowManyPlayers() throws RemoteException {
        return 0;
    }

    @Override
    public void pushSecondsLeft(Integer seconds) throws RemoteException {

    }

    @Override
    public void pushState(Model.State.Type state) throws RemoteException {

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
        seenTiles.add(t);
    }

    @Override
    public void gotTile(Tile t) throws RemoteException {
        seenTiles.remove(t);

        if(seenTilesIndex == seenTiles.size()-1)
            seenTilesIndex--;

        if(seenTilesIndex == -1){
            seenTilePane.getChildren().clear();
            seenTilesIndex = 0;
        }
        else
            drawSeenTile();
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
