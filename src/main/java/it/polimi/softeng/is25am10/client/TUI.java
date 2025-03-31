package it.polimi.softeng.is25am10.client;

import it.polimi.softeng.is25am10.ServerMain;
import it.polimi.softeng.is25am10.client.tui.ConfigTUI;
import it.polimi.softeng.is25am10.client.tui.RendererTUI;
import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;
import it.polimi.softeng.is25am10.network.socket.SocketClient;

import java.io.IOException;

public class TUI {

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerMain.main(new String[]{"true"});
        ClientInterface clientInterface = ConfigTUI.getControls();
        System.out.println("ok");
        RendererTUI rendererTUI = new RendererTUI(clientInterface);
    }
}
