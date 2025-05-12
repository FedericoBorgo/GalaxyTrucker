package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.State;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardOutput;
import it.polimi.softeng.is25am10.network.Callback;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;

public class CallbackImpl implements Callback, Serializable {

    /**
     * @param players
     * @param quid
     * @param disconnected
     * @throws RemoteException
     */
    @Override
    public void pushPlayers(HashMap<String, FlightBoard.Pawn> players, HashSet<String> quid, HashSet<String> disconnected) throws RemoteException {

    }

    @Override
    public int askHowManyPlayers() throws RemoteException {
        return 2;
    }

    @Override
    public void pushSecondsLeft(Integer seconds) throws RemoteException {}

    @Override
    public void pushState(State.Type state) throws RemoteException {
        System.out.println("Stato gioco: " + state);
    }

    @Override
    public void pushCardData(CardData card) throws RemoteException {}

    @Override
    public void pushCardChanges(CardOutput output) throws RemoteException {}

    /**
     * @param name
     * @param pawn
     * @throws RemoteException
     */
    @Override
    public void waitFor(String name, FlightBoard.Pawn pawn) throws RemoteException {

    }

    @Override
    public void gaveTile(Tile t) throws RemoteException {
        System.out.println("Tile assegnata: " + t.getType() + "/" + t.getConnectors() + ".jpg");
    }

    @Override
    public void gotTile(Tile t) throws RemoteException {}

    @Override
    public void pushBoard(ShipBoard board) throws RemoteException {}

    @Override
    public void pushFlight(FlightBoard board) throws RemoteException {}

    @Override
    public int ping() throws RemoteException {
        return 0;
    }

    @Override
    public void placeTile(Coordinate c, Tile t, Tile.Rotation r) throws RemoteException {}

    @Override
    public void bookedTile(Tile t) throws RemoteException {}

    @Override
    public void removed(Coordinate c) throws RemoteException {}

    /**
     * @param dropped
     * @throws RemoteException
     */
    @Override
    public void pushDropped(Model.Removed dropped) throws RemoteException {

    }

    /**
     * @param cannons
     * @throws RemoteException
     */
    @Override
    public void pushCannons(HashMap<Tile.Rotation, Integer> cannons) throws RemoteException {

    }

    /**
     * @param m
     * @throws RemoteException
     */
    @Override
    public void pushModel(Model m) throws RemoteException {

    }
}