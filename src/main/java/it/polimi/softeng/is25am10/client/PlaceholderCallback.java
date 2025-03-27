package it.polimi.softeng.is25am10.client;

import it.polimi.softeng.is25am10.Logger;
import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.network.Callback;
import org.json.JSONObject;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class PlaceholderCallback extends UnicastRemoteObject implements Callback {
    private final String name;
    public PlaceholderCallback(String name) throws RemoteException {
        super();
        this.name = name;
    }

    @Override
    public void joinedPlayer(String player) throws RemoteException {
        Logger.clientLog("per : " + name + " evento: giocatore unito " + player);
    }

    @Override
    public int askHowManyPlayers() throws RemoteException {
        return 3;
    }

    @Override
    public void notifyState(Model.State.Type state) throws RemoteException {
        Logger.clientLog("per : " + name + " evento: cambio stato " + state);
    }

    @Override
    public void movedTimer() throws RemoteException {
        Logger.clientLog("per : " + name + " evento: timer spostato");
    }

    @Override
    public void pushPositions(List<FlightBoard.Pawn> order, List<Integer> offset) throws RemoteException {

    }

    @Override
    public void pushCard(Card.CompressedCard card) throws RemoteException {

    }

    @Override
    public void pushCardChanges(JSONObject data) throws RemoteException {

    }

    @Override
    public void askForInput() throws RemoteException {

    }
}