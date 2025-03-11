package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Card {
    // does the player need to declare something?
    private final boolean needPlayerChoice;
    protected FlightBoard board;
    protected final Map<Player, List<String>> playerChoice;
    protected final List<InputType> inputOrder;

    public enum InputType {
        BOOLEAN,
        COORD_PAIR
    }

    public Card(boolean needPlayerChoice, List<InputType> inputOrder) {
        this.needPlayerChoice = needPlayerChoice;
        this.inputOrder = inputOrder;
        playerChoice = new HashMap<>();
    }

    public void setBoard(FlightBoard board) {
        this.board = board;
    }

    public boolean needPlayerChoice() {
        return needPlayerChoice;
    }

    public List<InputType> getInputOrder() {
        return inputOrder;
    }

    public abstract Result<List<String>> set(Player player, List<String> input);

    public abstract Result<String> play();
}
