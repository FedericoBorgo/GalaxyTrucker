package it.polimi.softeng.is25am10.client;

import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.socket.SocketClient;

import java.io.IOException;

public class ClientMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        ClientInterface client = new SocketClient("FEDE", "localhost", 1235, 1236);
        //ClientInterface client1 = new SocketClient("MARCO", "localhost", 1235, 1236);
        //ClientInterface client2 = new RMIClient("MATTEO", "localhost", 1234);

        client.join(new PlaceholderCallback("FEDE"));
        //client1.join(new PlaceholderCallback("MARCO"));
        //client2.join(new PlaceholderCallback("MATTEO"));
/*
        Thread.sleep(100);
        client1.moveTimer();
        Thread.sleep(100);
        client2.moveTimer();
        Thread.sleep(100);
        client2.moveTimer();*/

    }
}
