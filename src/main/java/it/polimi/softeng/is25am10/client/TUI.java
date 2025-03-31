package it.polimi.softeng.is25am10.client;

import it.polimi.softeng.is25am10.ServerMain;
import it.polimi.softeng.is25am10.client.tui.ConfigTUI;
import it.polimi.softeng.is25am10.client.tui.RendererTUI;
import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;

import java.io.IOException;

public class TUI {

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerMain.main(new String[]{"true"});
        ClientInterface clientInterface =  new RMIClient("fede", "localhost", 1234);
        System.out.println("ok");
        RendererTUI rendererTUI = new RendererTUI(clientInterface);
    }
}
