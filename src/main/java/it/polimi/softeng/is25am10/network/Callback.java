package it.polimi.softeng.is25am10.network;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardOutput;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface Callback extends Remote {
    void setPlayers(HashMap<String, FlightBoard.Pawn> players) throws RemoteException;

    int askHowManyPlayers() throws RemoteException;

    void pushSecondsLeft(Integer seconds) throws RemoteException;

    void pushState(Model.State.Type state) throws RemoteException;

    void pushCardData(CardData card) throws RemoteException;

    void pushCardChanges(CardOutput output) throws RemoteException;

    void askForInput() throws RemoteException;

    void gaveTile(Tile t) throws RemoteException;

    void gotTile(Tile t) throws RemoteException;

    void pushBoard(ShipBoard board) throws RemoteException;

    void pushFlight(FlightBoard board) throws RemoteException;

    int ping() throws RemoteException;

    void placeTile(Coordinate c, Tile t, Tile.Rotation r) throws RemoteException;
}
