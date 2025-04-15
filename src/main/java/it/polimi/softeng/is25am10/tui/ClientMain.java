package it.polimi.softeng.is25am10.tui;

import it.polimi.softeng.is25am10.Controller;
import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;
import it.polimi.softeng.is25am10.network.socket.SocketClient;

import java.io.IOException;

public class ClientMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        new Thread(() -> {
            try {
                Controller.main(new String[]{"true"});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        Thread.sleep(1000);

        ClientInterface client = new RMIClient("fede", "localhost", 1234);
        ClientInterface client1 = new SocketClient("MARCO", "localhost", 1235, 1236);


        Thread.sleep(2000);

    }
}
