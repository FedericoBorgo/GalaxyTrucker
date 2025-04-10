package it.polimi.softeng.is25am10.network.socket;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardInput;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
                Object obj = eventInput.readObject();
                Request request = (Request) obj;
                Method method = Callback.class.getMethod(request.getMethod(), request.getType());
                eventOutput.writeObject(method.invoke(callback, request.getArgs()));
                eventOutput.flush();
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

    private synchronized <T> T call(Object... args){
        try {
            Class<?>[] types = Arrays.stream(args)
                    .map(Object::getClass)
                    .toArray(Class<?>[]::new);

            String methodName = Thread.currentThread()
                    .getStackTrace()[2]
                    .getMethodName();

            methodOutput.reset();
            methodOutput.writeObject(new Request(methodName, types, args));
            methodOutput.flush();

            return (T) methodInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("unable to call", e);
        }
    }

    public Result<FlightBoard.Pawn> join(Callback callback){
        this.callback = callback;
        return call(name);
    }

    public Result<Integer> moveTimer() {
        return call(name);
    }

    public Result<String> setReady() {
        return call(name);
    }

    public Result<String> quit() {
        return call(name);
    }

    public Result<Tile> setTile(Coordinate c, Tile t, Tile.Rotation rotation) {
        return call(name, c, t, rotation);
    }

    public Result<Tile> bookTile(Tile t) {
        return call(name, t);
    }

    public Result<Tile> useBookedTile(Tile t, Tile.Rotation rotation, Coordinate c) {
        return call(name, t, rotation, c);
    }

    public Result<String> remove(Coordinate c) {
        return call(name, c);
    }

    public ShipBoard getShip() {
        return call(name);
    }

    public Result<String> init(Result<Coordinate> purple, Result<Coordinate> brown) {
        return call(name, purple, brown);
    }

    public List<GoodsBoard.Type> getReward() {
        return call(name);
    }

    public Result<Integer> placeReward(GoodsBoard.Type t, Coordinate c) {
        return call(name, t, c);
    }

    public int getCash() {
        return call(name);
    }

    public Result<Integer> drop(Coordinate c) {
        return call(name, c);
    }

    public Result<Integer> drop(Coordinate c, GoodsBoard.Type t) {
        return call(name, c, t);
    }

    public Result<String> setCannonsToUse(Map<Tile.Rotation, Integer> map) {
        return call(name, map);
    }

    public Result<Tile> drawTile() {
        return call(name);
    }

    public Result<List<Tile>> getSeenTiles() {
        return call(name);
    }

    public Result<String> giveTile(Tile t) {
        return call(name, t);
    }

    public Result<Tile> getTileFromSeen(Tile t) {
        return call(name, t);
    }

    public Result<Card> drawCard() {
        return call(name);
    }

    public Result<CardInput> setInput(CardInput input) {
        return call(name, input);
    }

    public Result<CardData> getCardData() {
        return call(name);
    }

    public Result<Card[][]> getVisible() {
        return call(name);
    }
}
