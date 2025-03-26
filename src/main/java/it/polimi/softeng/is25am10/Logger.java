package it.polimi.softeng.is25am10;

import java.awt.*;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;

public class Logger {
    static final String RESET = "\u001B[0m";
    static final String BLACK = "\u001B[30m";
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String BLUE = "\u001B[34m";
    static final String PURPLE = "\u001B[35m";
    static final String CYAN = "\u001B[36m";
    static final String WHITE = "\u001B[37m";


    private static void print(String prefix, String message, String color) {
        System.out.println(RESET + color + "[" + prefix + "]" + message);
    }

    public static void serverLog(String message) {
        print("SERVER", message, WHITE);
    }

    public static void modelLog(int id, String message) {
        print("MODEL", "[" + id + "]" + message, CYAN);
    }

    public static void playerLog(int id, String player, String message){
        String prefix = Controller.isRMI() ? "RMI" : "SOCKET";
        print("MODEL",
                "[" + id + "]" +
                        "[" + prefix + "]" +
                        "[" + player + "]" + message, CYAN);
    }
}
