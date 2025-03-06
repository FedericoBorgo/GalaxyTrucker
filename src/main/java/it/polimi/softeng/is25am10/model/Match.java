package it.polimi.softeng.is25am10.model;

import java.util.ArrayList;

/**
 * Stores all the information of a single game including the players and all their data,
 * the tiles for that game and the flightboard.
 */

public class Match {
    private ArrayList<Player> players;
    private TilesCollection tilesCollection;
    private FlightBoard flightBoard;

    //Constructs a new Match instance
    public Match() {
        players = new ArrayList<>();
        tilesCollection = new TilesCollection();
        flightBoard = new FlightBoard();
    }

    /**
     * Adds a new player to the match with a specified RocketPawn and nickname.
     *
     * @param pawn the RocketPawn associated with the player
     * @param nickname the player's nickname
     */
    public void addPlayer(RocketPawn pawn, String nickname) {
        // The server checked in the connection phase that the name is unique
        // The server checks for the number of players added to the game to be right
        Player player = new Player(pawn, nickname);
        players.add(player);
    }
}
