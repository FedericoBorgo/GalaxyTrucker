package it.polimi.softeng.is25am10.tui;

import it.polimi.softeng.is25am10.tui.asciiui.Config;
import it.polimi.softeng.is25am10.tui.asciiui.Game;
import it.polimi.softeng.is25am10.network.ClientInterface;

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
