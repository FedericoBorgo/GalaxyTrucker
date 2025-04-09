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
        try{
            //ServerMain.main(new String[]{"true"});
            //ClientInterface clientInterface = new SocketClient("chia", "localhost", 1235, 1236);
            ClientInterface clientInterface = Config.getControls();

            Game renderer = new Game(clientInterface);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
