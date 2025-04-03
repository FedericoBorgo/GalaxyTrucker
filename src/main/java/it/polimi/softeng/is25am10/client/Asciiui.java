package it.polimi.softeng.is25am10.client;

import it.polimi.softeng.is25am10.ServerMain;
import it.polimi.softeng.is25am10.client.asciiui.Config;
import it.polimi.softeng.is25am10.client.asciiui.Game;
import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;
import it.polimi.softeng.is25am10.network.socket.SocketClient;

import java.io.IOException;

public class Asciiui {

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerMain.main(new String[]{"true"});
        //ClientInterface clientInterface = Config.getControls();
        ClientInterface clientInterface = new SocketClient("fede", "localhost", 1235, 1236);
        ClientInterface player2 = new RMIClient("npc", "localhost", 1234);
        Game renderer = new Game(clientInterface);
        Thread.sleep(5000);
        player2.join(new PlaceholderCallback("chia"));
    }
}
