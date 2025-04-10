package it.polimi.softeng.is25am10.network.rmi;

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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RMIClient implements ClientInterface {
    private final RMIInterface server;
    private final String name;

    public RMIClient(String name, String host, int port) {
        this.name = name;

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            server = (RMIInterface) registry.lookup("controller");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPlayerName() {
        return name;
    }

    private <T> T call(Object... args){
        Class<?>[] types = Arrays.stream(args)
                .map(Object::getClass)
                .toArray(Class<?>[]::new);

        String methodName = Thread.currentThread()
                .getStackTrace()[2]
                .getMethodName();

        try {
            return (T) Callback.class.getMethod(methodName, types)
                    .invoke(server, args);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<FlightBoard.Pawn> join(Callback callback) {
        try {
            server.setCallback(name, callback);
            return server.join(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
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
