package it.polimi.softeng.is25am10.network.socket;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SocketClient extends Thread implements ClientInterface {
    private final String name;
    private Callback callback;
    private final ObjectOutputStream methodOutput;
    private final ObjectInputStream methodInput;
    private final ObjectOutputStream eventOutput;
    private final ObjectInputStream eventInput;

    public SocketClient(String name, String host, int port1, int port2) {
        super();
        this.name = name;

        try {
            Socket method = new Socket(host, port1);
            Socket event = new Socket(host, port2);
            eventOutput = new ObjectOutputStream(event.getOutputStream());
            eventInput = new ObjectInputStream(event.getInputStream());
            methodOutput = new ObjectOutputStream(method.getOutputStream());
            methodInput = new ObjectInputStream(method.getInputStream());
            eventOutput.writeObject(name);
        } catch (IOException e) {
            throw new RuntimeException("unable to connect to the server", e);
        }

        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Request request = (Request) eventInput.readObject();
                Method method = Callback.class.getMethod(request.getMethod(), request.getType());
                eventOutput.writeObject(method.invoke(callback, request.getArgs()));
            } catch (IOException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     IllegalAccessException e) {
                throw new RuntimeException("unable to handle the event", e);
            }
        }
    }

    @Override
    public String getPlayerName(){
        return name;
    }
    
    private <T> T call(String name, Class<?>[] types, Object... args){
        try {
            methodOutput.writeObject(new Request(name, types, args));
            return (T) methodInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("unable to call: " + name, e);
        }
    }

    public Result<FlightBoard.Pawn> join(Callback callback){
        this.callback = callback;
        return call("join", new Class[]{String.class}, name);
    }

    public Result<Integer> moveTimer() {
        return call("moveTimer", new Class[]{String.class}, name);
    }

    public Result<String> setReady() {
        return call("setReady", new Class[]{String.class}, name);
    }

    public Result<String> quit() {
        return call("quit", new Class[]{String.class}, name);
    }

    public Result<Tile> setTile(Coordinate c, Tile t, Tile.Rotation rotation) {
        return call("setTile", new Class[]{
                String.class, Coordinate.class, Tile.class, Tile.Rotation.class}, 
                name, c, t, rotation);
    }

    public Result<Tile> getTile(Coordinate c) {
        return call("getTile", new Class[]{
                String.class, Coordinate.class}, 
                name, c);
    }

    public Tile.Rotation getRotation(Coordinate c) {
        return call("getRotation", new Class[]{
                String.class, Coordinate.class}, 
                name, c);
    }

    public Result<Tile> bookTile(Tile t) {
        return call("bookTile", new Class[]{
                String.class, Tile.class}, 
                name, t);
    }

    public Result<Tile> useBookedTile(Tile t, Tile.Rotation rotation, Coordinate c) {
        return call("useBookedTile", new Class[]{
                String.class, Tile.class, Tile.Rotation.class, Coordinate.class}, 
                name, t, rotation, c);
    }

    public List<Tile> getBooked() {
        return call("getBooked", new Class[]{String.class}, name);
    }

    public Result<String> remove(Coordinate c) {
        return call("remove", new Class[]{
                String.class, Coordinate.class}, 
                name, c);
    }

    public Set<Coordinate> checkShip() {
        return call("checkShip", new Class[]{String.class}, name);
    }

    public ShipBoard.CompressedShipBoard getShip() {
        return call("getShip", new Class[]{String.class}, name);
    }

    public Result<String> init(Result<Coordinate> purple, Result<Coordinate> brown) {
        return call("init", new Class[]{
                String.class, Result.class, Result.class},
                name, purple, brown);
    }

    public List<GoodsBoard.Type> getReward() {
        return call("getReward", new Class[]{String.class}, name);
    }

    public Result<Integer> placeReward(GoodsBoard.Type t, Coordinate c) {
        return call("placeReward", new Class[]{
                String.class, GoodsBoard.Type.class, Coordinate.class},
                name, t, c);
    }

    public int getCash() {
        return call("getCash", new Class[]{String.class}, name);
    }

    public Result<Integer> drop(Coordinate c) {
        return call("drop", new Class[]{
                String.class, Coordinate.class}, 
                name, c);
    }

    public Result<Integer> drop(Coordinate c, GoodsBoard.Type t) {
        return call("drop", new Class[]{
                String.class, Coordinate.class, GoodsBoard.Type.class}, 
                name, c, t);
    }

    public Result<String> setCannonsToUse(Map<Tile.Rotation, Integer> map) {
        return call("setCannonsToUse", new Class[]{
                String.class, Map.class}, 
                name, map);
    }

    public Result<Tile> drawTile() {
        return call("drawTile", new Class[]{String.class}, name);
    }

    public Result<List<Tile>> getSeenTiles() {
        return call("getSeenTiles", new Class[]{String.class}, name);
    }

    public Result<String> giveTile(Tile t) {
        return call("giveTile", new Class[]{
                String.class, Tile.class}, 
                name, t);
    }

    public Result<Tile> getTileFromSeen(Tile t) {
        return call("getTileFromSeen", new Class[]{
                String.class, Tile.class}, 
                name, t);
    }

    public Result<Card.CompressedCard> drawCard() {
        return call("drawCard", new Class[]{String.class}, name);
    }

    public Result<JSONObject> setInput(JSONObject json) {
        return call("setInput", new Class[]{
                String.class, JSONObject.class}, 
                name, json);
    }

    public Result<JSONObject> getCardData() {
        return call("getCardData", new Class[]{String.class}, name);
    }

    public Result<Card[][]> getVisible() {
        return call("getVisible", new Class[]{String.class}, name);
    }
}
