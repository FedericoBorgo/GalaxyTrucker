package it.polimi.softeng.is25am10;

import it.polimi.softeng.is25am10.gui.Launcher;
import it.polimi.softeng.is25am10.tui.Asciiui;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if(args.length != 1) {
            System.out.println("Unexpected");
            return;
        }

        switch (args[0]) {
            case "-s" -> Controller.main(new String[]{"false"});
            case "-t" -> Asciiui.main(new String[]{});
            case "-g" -> Launcher.main(new String[]{});
            default -> System.out.println("Unexpected");
        }
    }
}
