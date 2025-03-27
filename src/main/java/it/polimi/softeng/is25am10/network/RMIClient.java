package it.polimi.softeng.is25am10.network;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import org.json.JSONObject;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RMIClient extends UnicastRemoteObject implements ServerToClient {
    private ClientToServer server;
    private String name;

    public RMIClient(String name, String host, int port) throws RemoteException {
        super();
        this.name = name;

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            server = (ClientToServer) registry.lookup("controller");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }

        server.join(name, this);
    }

    public Result<Integer> moveTimer() throws RemoteException {
        return server.moveTimer(name);
    }

    public Result<String> setReady() throws RemoteException {
        return server.setReady(name);
    }

    public Result<String> quit() throws RemoteException {
        return server.quit(name);
    }

    public Result<Tile> setTile(Coordinate c, Tile t, Tile.Rotation rotation) throws RemoteException {
        return server.setTile(name, c, t, rotation);
    }

    public Result<Tile> getTile(Coordinate c) throws RemoteException {
        return server.getTile(name, c);
    }

    public Tile.Rotation getRotation(Coordinate c) throws RemoteException {
        return server.getRotation(name, c);
    }

    public Result<Tile> bookTile(Tile t) throws RemoteException {
        return server.bookTile(name, t);
    }

    public Result<Tile> useBookedTile(Tile t, Tile.Rotation rotation, Coordinate c) throws RemoteException {
        return server.useBookedTile(name, t, rotation, c);
    }

    public List<Tile> getBooked() throws RemoteException {
        return server.getBooked(name);
    }

    public Result<String> remove(Coordinate c) throws RemoteException {
        return server.remove(name, c);
    }

    public Set<Coordinate> checkShip() throws RemoteException {
        return server.checkShip(name);
    }

    public ShipBoard.CompressedShipBoard getShip() throws RemoteException {
        return server.getShip(name);
    }

    public Result<String> init(Optional<Coordinate> purple, Optional<Coordinate> brown) throws RemoteException {
        return server.init(name, purple, brown);
    }

    public List<GoodsBoard.Type> getReward() throws RemoteException {
        return server.getReward(name);
    }

    public Result<Integer> placeReward(GoodsBoard.Type t, Coordinate c) throws RemoteException {
        return server.placeReward(name, t, c);
    }

    public int getCash() throws RemoteException {
        return server.getCash(name);
    }

    public Result<Integer> drop(Coordinate c) throws RemoteException {
        return server.drop(name, c);
    }

    public Result<Integer> drop(Coordinate c, GoodsBoard.Type t) throws RemoteException {
        return server.drop(name, c, t);
    }

    public Result<String> setCannonsToUse(Map<Tile.Rotation, Integer> map) throws RemoteException {
        return server.setCannonsToUse(name, map);
    }

    public Result<Tile> drawTile() throws RemoteException {
        return server.drawTile(name);
    }

    public Result<List<Tile>> getSeenTiles() throws RemoteException {
        return server.getSeenTiles(name);
    }

    public Result<String> giveTile(Tile t) throws RemoteException {
        return server.giveTile(name, t);
    }

    public Result<Tile> getTileFromSeen(Tile t) throws RemoteException {
        return server.getTileFromSeen(name, t);
    }

    public Result<Card.CompressedCard> drawCard() throws RemoteException {
        return server.drawCard(name);
    }

    public Result<JSONObject> setInput(JSONObject json) throws RemoteException {
        return server.setInput(name, json);
    }

    public Result<JSONObject> getCardData() throws RemoteException {
        return server.getCardData(name);
    }

    public Result<Card[][]> getVisible() throws RemoteException {
        return server.getVisible(name);
    }

    public void joinedPlayer(String player) throws RemoteException {
        System.out.println(name + ": joined " + player);
    }

    public int askHowManyPlayers() throws RemoteException {
        return 2;
    }

    public void notifyState(Model.State.Type state) throws RemoteException {

    }

    public void movedTimer() throws RemoteException {
        System.out.println(name + ": received removed timer");
    }

    public void pushPositions(List<FlightBoard.Pawn> order, List<Integer> offset) throws RemoteException {

    }

    public void pushCard(Card.CompressedCard card) throws RemoteException {

    }

    public void pushCardChanges(JSONObject data) throws RemoteException {

    }
}
