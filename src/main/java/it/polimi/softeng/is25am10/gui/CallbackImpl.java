package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.Model;
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

public class CallbackImpl implements Callback, Serializable {
    @Override
    public void setPlayers(HashMap<String, FlightBoard.Pawn> players) throws RemoteException {
        System.out.println("Giocatori: " + players);
    }

    @Override
    public int askHowManyPlayers() throws RemoteException {
        return 2;
    }

    @Override
    public void pushSecondsLeft(Integer seconds) throws RemoteException {}

    @Override
    public void pushState(Model.State.Type state) throws RemoteException {
        System.out.println("Stato gioco: " + state);
    }

    @Override
    public void pushCardData(CardData card) throws RemoteException {}

    @Override
    public void pushCardChanges(CardOutput output) throws RemoteException {}

    @Override
    public void askForInput() throws RemoteException {}

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
}