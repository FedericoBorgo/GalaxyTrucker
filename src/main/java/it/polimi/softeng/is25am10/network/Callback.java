package it.polimi.softeng.is25am10.network;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.State;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardOutput;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;

public interface Callback extends Remote {
    void pushPlayers(HashMap<String, FlightBoard.Pawn> players, HashSet<String> quid, HashSet<String> disconnected) throws RemoteException;

    int askHowManyPlayers() throws RemoteException;

    void pushSecondsLeft(Integer seconds) throws RemoteException;

    void pushState(State.Type state) throws RemoteException;

    void pushCardData(CardData card) throws RemoteException;

    void pushCardChanges(CardOutput output) throws RemoteException;

    void waitFor(String name, FlightBoard.Pawn pawn) throws RemoteException;

    void gaveTile(Tile t) throws RemoteException;

    void gotTile(Tile t) throws RemoteException;

    void pushBoard(ShipBoard board) throws RemoteException;

    void pushFlight(FlightBoard board) throws RemoteException;

    int ping() throws RemoteException;

    void placeTile(Coordinate c, Tile t, Tile.Rotation r) throws RemoteException;

    void bookedTile(Tile t) throws RemoteException;

    void removed(Coordinate c) throws RemoteException;

    void pushDropped(Model.Removed dropped) throws RemoteException;

    void pushCannons(HashMap<Tile.Rotation, Integer> cannons) throws RemoteException;

    void pushModel(Model m) throws RemoteException;
}
