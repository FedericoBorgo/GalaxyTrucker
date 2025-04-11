package it.polimi.softeng.is25am10.tui;

import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;
import it.polimi.softeng.is25am10.network.socket.SocketClient;
import it.polimi.softeng.is25am10.tui.asciiui.Config;
import it.polimi.softeng.is25am10.tui.asciiui.Game;
import it.polimi.softeng.is25am10.network.ClientInterface;

import java.io.IOException;

public class Asciiui {

    public static void main(String[] args) throws IOException, InterruptedException {
        try{
            //ServerMain.main(new String[]{"true"});
            //ClientInterface clientInterface = new SocketClient("fede", "localhost", 1235, 1236);
            //ClientInterface f2 = new RMIClient("chia", "localhost", 1234);
            //f2.join(new PlaceholderCallback("chia"));
            ClientInterface clientInterface = Config.getControls();

            Game renderer = new Game(clientInterface);

            //renderer.currentTile = new Tile(Tile.Type.PIPES, "uuuu");
            //renderer.execute("piazza 2 43 0");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
