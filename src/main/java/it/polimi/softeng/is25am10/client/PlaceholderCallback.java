package it.polimi.softeng.is25am10.client;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.network.Callback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class PlaceholderCallback extends UnicastRemoteObject implements Callback {
    private final String name;
    public PlaceholderCallback(String name) throws RemoteException {
        super();
        this.name = name;
    }

    @Override
    public void setPlayers(HashMap<String, FlightBoard.Pawn> players) throws RemoteException {

    }

    @Override
    public int askHowManyPlayers() throws RemoteException {
        return 2;
    }

    @Override
    public void pushState(Model.State.Type state) throws RemoteException {

    }


    @Override
    public void pushCard(Card card) throws RemoteException {

    }

    @Override
    public void pushCardChanges(String data) throws RemoteException {

    }

    @Override
    public void askForInput() throws RemoteException {

    }

    @Override
    public void gaveTile(Tile t) throws RemoteException {

    }

    @Override
    public void gotTile(Tile t) throws RemoteException {

    }

    @Override
    public void pushBoard(ShipBoard board) throws RemoteException {

    }

    @Override
    public void pushFlight(FlightBoard board) throws RemoteException {
        System.out.println();
    }

    @Override
    public int ping() throws RemoteException {
        return 0;
    }
}