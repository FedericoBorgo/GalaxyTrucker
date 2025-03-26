package it.polimi.softeng.is25am10.network;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import org.json.JSONObject;

import java.util.List;

public interface ServerToClient {
    void joinedPlayer(String player);

    int askHowManyPlayers();

    void notifyState(Model.State.Type state);

    void movedTimer();

    void pushPositions(List<FlightBoard.Pawn> order, List<Integer> offset);

    void pushCard(Card.CompressedCard card);

    void pushCardChanges(JSONObject data);
}
