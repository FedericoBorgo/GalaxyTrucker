package it.polimi.softeng.is25am10.tui;

import it.polimi.softeng.is25am10.Controller;
import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;
import it.polimi.softeng.is25am10.network.socket.SocketClient;
import it.polimi.softeng.is25am10.tui.asciiui.AutoBuilder;
import it.polimi.softeng.is25am10.tui.asciiui.Config;
import it.polimi.softeng.is25am10.tui.asciiui.Game;

import java.io.*;

public class Asciiui {


    public static void main(String[] args) throws IOException {
/*
        try{
            Controller.main(new String[]{"true"});
        }catch(Exception _){}

        String name = args[0];
        String conn = args[1];

        ClientInterface client = conn.equals("rmi")?
                new RMIClient(name, "localhost", 1234) :
                new SocketClient(name, "localhost", 1235, 1236);*/

        ClientInterface client = Config.getControls();
        Game game = new Game(client);

        //new SocketClient("npc", "localhost", 1235, 1236).join(new PlaceholderCallback());

        AutoBuilder.initGame(game);
    }
}
