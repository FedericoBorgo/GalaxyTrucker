package it.polimi.softeng.is25am10.network;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import org.json.JSONObject;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ClientToServer extends Remote {
    void join(String name) throws RemoteException;

    Result<Integer> moveTimer(String name) throws RemoteException;

    Result<String> setReady(String name) throws RemoteException;

    Result<String> quit(String name) throws RemoteException;

    Result<Tile> setTile(String name, Coordinate c, Tile t, Tile.Rotation rotation) throws RemoteException;

    Result<Tile> getTile(String name, Coordinate c) throws RemoteException;

    Tile.Rotation getRotation(String name, Coordinate c) throws RemoteException;

    Result<Tile> bookTile(String name, Tile t) throws RemoteException;

    Result<Tile> useBookedTile(String name, Tile t, Tile.Rotation rotation, Coordinate c) throws RemoteException;

    List<Tile> getBooked(String name) throws RemoteException;

    Result<String> remove(String name, Coordinate c) throws RemoteException;

    Set<Coordinate> checkShip(String name) throws RemoteException;

    ShipBoard.CompressedShipBoard getShip(String name) throws RemoteException;

    Result<String> init(String name, Optional<Coordinate> purple, Optional<Coordinate> brown) throws RemoteException;

    List<GoodsBoard.Type> getReward(String name) throws RemoteException;

    Result<Integer> placeReward(String name, GoodsBoard.Type t, Coordinate c) throws RemoteException;

    int getCash(String name) throws RemoteException;

    Result<Integer> drop(String name, Coordinate c) throws RemoteException;

    Result<Integer> drop(String name, Coordinate c, GoodsBoard.Type t) throws RemoteException;

    Result<String> setCannonsToUse(String name, Map<Tile.Rotation, Integer> map) throws RemoteException;

    Result<Tile> drawTile(String name) throws RemoteException;

    Result<List<Tile>> getSeenTiles(String name) throws RemoteException;

    Result<String> giveTile(String name, Tile t) throws RemoteException;

    Result<Tile> getTileFromSeen(String name, Tile t) throws RemoteException;

    Result<Card.CompressedCard> drawCard(String name) throws RemoteException;

    Result<JSONObject> setInput(String name, JSONObject json) throws RemoteException;

    Result<JSONObject> getCardData(String name) throws RemoteException;

    Result<Card[][]> getVisible(String name) throws RemoteException;
}
