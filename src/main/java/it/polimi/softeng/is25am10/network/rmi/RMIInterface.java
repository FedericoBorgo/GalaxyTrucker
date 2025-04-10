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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RMIInterface extends Remote {
    Result<FlightBoard.Pawn> join(String name) throws RemoteException;

    void setCallback(String name, Callback callback) throws RemoteException;

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

    ShipBoard getShip(String name) throws RemoteException;

    Result<String> init(String name, Result<Coordinate> purple, Result<Coordinate> brown) throws RemoteException;

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

    Result<Card> drawCard(String name) throws RemoteException;

    Result<CardInput> setInput(String name, CardInput json) throws RemoteException;

    Result<CardData> getCardData(String name) throws RemoteException;

    Result<Card[][]> getVisible(String name) throws RemoteException;
}
