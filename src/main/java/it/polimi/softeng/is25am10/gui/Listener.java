package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardOutput;
import it.polimi.softeng.is25am10.network.Callback;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;

public class Listener extends UnicastRemoteObject implements Remote, Callback {
    Callback callback;

    protected Listener(Callback callback) throws RemoteException {
        this.callback = callback;
    }

    @Override
    public void pushPlayers(HashMap<String, FlightBoard.Pawn> players, HashSet<String> quid, HashSet<String> disconnected) throws RemoteException {
        callback.pushPlayers(players, quid, disconnected);
    }

    @Override
    public int askHowManyPlayers() throws RemoteException {
        return callback.askHowManyPlayers();
    }

    @Override
    public void pushSecondsLeft(Integer seconds) throws RemoteException {
        callback.pushSecondsLeft(seconds);
    }

    @Override
    public void pushState(Model.State.Type state) throws RemoteException {
        callback.pushState(state);
    }

    @Override
    public void pushCardData(CardData card) throws RemoteException {
        callback.pushCardData(card);
    }

    @Override
    public void pushCardChanges(CardOutput output) throws RemoteException {
        callback.pushCardChanges(output);
    }

    @Override
    public void waitFor(String name, FlightBoard.Pawn pawn) throws RemoteException {
        callback.waitFor(name, pawn);
    }

    @Override
    public void gaveTile(Tile t) throws RemoteException {
        callback.gaveTile(t);
    }

    @Override
    public void gotTile(Tile t) throws RemoteException {
        callback.gotTile(t);
    }

    @Override
    public void pushBoard(ShipBoard board) throws RemoteException {
        callback.pushBoard(board);
    }

    @Override
    public void pushFlight(FlightBoard board) throws RemoteException {
        callback.pushFlight(board);
    }

    @Override
    public int ping() throws RemoteException {
        return callback.ping();
    }

    @Override
    public void placeTile(Coordinate c, Tile t, Tile.Rotation r) throws RemoteException {
        callback.placeTile(c, t, r);
    }

    @Override
    public void bookedTile(Tile t) throws RemoteException {
        callback.bookedTile(t);
    }

    @Override
    public void removed(Coordinate c) throws RemoteException {
        callback.removed(c);
    }

    @Override
    public void pushDropped(Model.Removed dropped) throws RemoteException {
        callback.pushDropped(dropped);
    }

    @Override
    public void pushCannons(HashMap<Tile.Rotation, Integer> cannons) throws RemoteException {
        callback.pushCannons(cannons);
    }

    @Override
    public void pushModel(Model m) throws RemoteException {
        callback.pushModel(m);
    }
}
