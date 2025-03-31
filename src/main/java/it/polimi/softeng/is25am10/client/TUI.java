package it.polimi.softeng.is25am10.client;

import it.polimi.softeng.is25am10.ServerMain;
import it.polimi.softeng.is25am10.client.tui.RendererTUI;

import java.io.IOException;

public class TUI {

    public static void main(String[] args) throws IOException, InterruptedException {
        //ServerMain.main(new String[]{"true"});
        //ClientInterface clientInterface =  ConfigTUI.getControls();
        //System.out.println("ok");
        RendererTUI rendererTUI = new RendererTUI(null);
    }
}
