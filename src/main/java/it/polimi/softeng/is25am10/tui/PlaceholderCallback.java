package it.polimi.softeng.is25am10.tui;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.State;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardOutput;
import it.polimi.softeng.is25am10.network.Callback;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;

public class PlaceholderCallback implements Callback {

    @Override
    public void pushPlayers(HashMap<String, FlightBoard.Pawn> players, HashSet<String> quid, HashSet<String> disconnected) throws RemoteException {

    }

    @Override
    public int askHowManyPlayers() throws RemoteException {
        return 2;
    }

    @Override
    public void pushSecondsLeft(Integer seconds) throws RemoteException {

    }

    @Override
    public void pushState(State.Type state) throws RemoteException {

    }

    @Override
    public void pushCardData(CardData card) throws RemoteException {

    }

    @Override
    public void pushCardChanges(CardOutput output) throws RemoteException {

    }

    @Override
    public void waitFor(String name, FlightBoard.Pawn pawn) throws RemoteException {

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

    }

    @Override
    public int ping() throws RemoteException {
        return 0;
    }

    @Override
    public void placeTile(Coordinate c, Tile t, Tile.Rotation r) throws RemoteException {

    }

    @Override
    public void bookedTile(Tile t) throws RemoteException {

    }

    @Override
    public void removed(Coordinate c) throws RemoteException {

    }

    @Override
    public void pushDropped(Model.Removed dropped) throws RemoteException {

    }

    @Override
    public void pushCannons(HashMap<Tile.Rotation, Integer> cannons) throws RemoteException {

    }

    @Override
    public void pushModel(Model m) throws RemoteException {

    }

    @Override
    public void pushFinalCash(HashMap<String, Integer> cash) throws RemoteException {

    }
}
