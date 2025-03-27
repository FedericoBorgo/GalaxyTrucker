package it.polimi.softeng.is25am10.network;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import org.json.JSONObject;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Callback extends Remote {
    void joinedPlayer(String player) throws RemoteException;

    int askHowManyPlayers() throws RemoteException;

    void notifyState(Model.State.Type state) throws RemoteException;

    void movedTimer() throws RemoteException;

    void pushPositions(List<FlightBoard.Pawn> order, List<Integer> offset) throws RemoteException;

    void pushCard(Card.CompressedCard card) throws RemoteException;

    void pushCardChanges(JSONObject data) throws RemoteException;

    void askForInput() throws RemoteException;
}
