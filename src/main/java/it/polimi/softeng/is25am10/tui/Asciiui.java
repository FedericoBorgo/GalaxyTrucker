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

    /**
     * Main method to start the text base program
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        ClientInterface client = Config.getControls();

        Game game = new Game(client);

    }
}
