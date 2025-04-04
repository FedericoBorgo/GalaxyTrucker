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
import java.util.*;

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
            start();
            eventOutput.writeObject(name);
        } catch (IOException e) {
            throw new RuntimeException("unable to connect to the server", e);
        }
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
    
    private <T> T call(String name, Object... args){
        try {
            Class<?>[] types = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
            methodOutput.writeObject(new Request(name, types, args));
            return (T) methodInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("unable to call: " + name, e);
        }
    }

    public Result<FlightBoard.Pawn> join(Callback callback){
        this.callback = callback;
        return call("join", name);
    }

    public Result<Integer> moveTimer() {
        return call("moveTimer", name);
    }

    public Result<String> setReady() {
        return call("setReady", name);
    }

    public Result<String> quit() {
        return call("quit", name);
    }

    public Result<Tile> setTile(Coordinate c, Tile t, Tile.Rotation rotation) {
        return call("setTile", name, c, t, rotation);
    }

    public Result<Tile> getTile(Coordinate c) {
        return call("getTile", name, c);
    }

    public Tile.Rotation getRotation(Coordinate c) {
        return call("getRotation", name, c);
    }

    public Result<Tile> bookTile(Tile t) {
        return call("bookTile", name, t);
    }

    public Result<Tile> useBookedTile(Tile t, Tile.Rotation rotation, Coordinate c) {
        return call("useBookedTile", name, t, rotation, c);
    }

    public List<Tile> getBooked() {
        return call("getBooked", name);
    }

    public Result<String> remove(Coordinate c) {
        return call("remove", name, c);
    }

    public Set<Coordinate> checkShip() {
        return call("checkShip", name);
    }

    public ShipBoard getShip() {
        return call("getShip", name);
    }

    public Result<String> init(Result<Coordinate> purple, Result<Coordinate> brown) {
        return call("init", name, purple, brown);
    }

    public List<GoodsBoard.Type> getReward() {
        return call("getReward", name);
    }

    public Result<Integer> placeReward(GoodsBoard.Type t, Coordinate c) {
        return call("placeReward", name, t, c);
    }

    public int getCash() {
        return call("getCash", name);
    }

    public Result<Integer> drop(Coordinate c) {
        return call("drop", name, c);
    }

    public Result<Integer> drop(Coordinate c, GoodsBoard.Type t) {
        return call("drop", name, c, t);
    }

    public Result<String> setCannonsToUse(Map<Tile.Rotation, Integer> map) {
        return call("setCannonsToUse", name, map);
    }

    public Result<Tile> drawTile() {
        return call("drawTile", name);
    }

    public Result<List<Tile>> getSeenTiles() {
        return call("getSeenTiles", name);
    }

    public Result<String> giveTile(Tile t) {
        return call("giveTile", name, t);
    }

    public Result<Tile> getTileFromSeen(Tile t) {
        return call("getTileFromSeen", name, t);
    }

    public Result<Card.CompressedCard> drawCard() {
        return call("drawCard", name);
    }

    public Result<String> setInput(String json) {
        return call("setInput", name, json);
    }

    public Result<String> getCardData() {
        return call("getCardData", name);
    }

    public Result<Card[][]> getVisible() {
        return call("getVisible", name);
    }
}
