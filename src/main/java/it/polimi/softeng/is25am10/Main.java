package it.polimi.softeng.is25am10;

import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.RocketPawn;

public class Main {
    public static void main(String[] args) {
        FlightBoard flightBoard = new FlightBoard();

        flightBoard.setRocketReady(RocketPawn.RED);
        flightBoard.setRocketReady(RocketPawn.BLUE);
        flightBoard.setRocketReady(RocketPawn.GREEN);
        flightBoard.setRocketReady(RocketPawn.YELLOW);

        flightBoard.moveRocket(RocketPawn.YELLOW, 4);
        flightBoard.moveRocket(RocketPawn.YELLOW, 4);
        flightBoard.moveRocket(RocketPawn.YELLOW, -4);
        flightBoard.moveRocket(RocketPawn.YELLOW, -4);

        System.out.println("Rocket ready");
    }
}