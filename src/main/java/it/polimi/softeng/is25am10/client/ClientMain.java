package it.polimi.softeng.is25am10.client;

import it.polimi.softeng.is25am10.Controller;
import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.socket.SocketClient;

import java.io.IOException;

public class ClientMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Controller.main(new String[]{"true"});
        ClientInterface client = new SocketClient("fede", "localhost", 1235, 1236);
        ClientInterface client1 = new SocketClient("MARCO", "localhost", 1235, 1236);

        client.join(new PlaceholderCallback("FEDE"));
        client1.join(new PlaceholderCallback("MARCO"));


        Thread.sleep(2000);
        client1.moveTimer();

    }
}
