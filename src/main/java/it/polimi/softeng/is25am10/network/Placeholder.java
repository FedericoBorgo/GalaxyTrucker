package it.polimi.softeng.is25am10.network;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import org.json.JSONObject;

import java.util.List;

public class Placeholder implements ServerToClient{
    @Override
    public void joinedPlayer(String player) {

    }

    @Override
    public int askHowManyPlayers() {
        return 2;
    }

    @Override
    public void notifyState(Model.State.Type state) {

    }

    @Override
    public void movedTimer() {

    }

    @Override
    public void pushPositions(List<FlightBoard.Pawn> order, List<Integer> offset) {

    }

    @Override
    public void pushCard(Card.CompressedCard card) {

    }

    @Override
    public void pushCardChanges(JSONObject data) {

    }
}
