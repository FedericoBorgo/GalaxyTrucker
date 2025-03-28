package it.polimi.softeng.is25am10.client;

import it.polimi.softeng.is25am10.ServerMain;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;
import it.polimi.softeng.is25am10.network.socket.SocketClient;

import java.io.IOException;
import java.util.Scanner;

public class TUI {
    public static void main(String[] args) throws IOException {
        ServerMain.main(null);

        String name = "fede";
        String host = "localhost";
        int rmiPort = 1234;
        int socketPort1 = 1235;
        int socketPort2 = 1236;
        int rmiOrSocket = 0;

        ClientInterface client = rmiOrSocket == 0?
                new RMIClient(name, host, rmiPort) :
                new SocketClient(name, host, socketPort1, socketPort2);

        Result<FlightBoard.Pawn> pawn = client.join(new PlaceholderCallback(name));

        System.out.println();
    }
}
