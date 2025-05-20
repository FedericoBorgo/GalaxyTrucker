package it.polimi.softeng.is25am10;

import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;

public class Logger {
    public static boolean SILENCE = false;

    static final String RESET = "\u001B[0m";
    static final String BLACK = "\u001B[30m";
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String BLUE = "\u001B[34m";
    static final String PURPLE = "\u001B[35m";
    static final String CYAN = "\u001B[36m";
    static final String WHITE = "\u001B[37m";


    /**
     * Generic print method. Prints a message on the console.
     * @param prefix
     * @param message
     * @param color
     */
    private static void print(String prefix, String message, String color) {
        if(SILENCE)
            return;
        System.out.println(RESET + color + "[" + prefix + "]" + message);
    }

    /**
     * Prints a server message to the console
     * @param message
     */
    public static void serverLog(String message) {
        print("SERVER", message, WHITE);
    }

    /**
     * Prints a message from the model to the console
     * @param id
     * @param message
     */
    public static void modelLog(int id, String message) {
        print("MODEL", "[" + id + "]" + message, PURPLE);
    }

    /**
     * Prints a message from the socket connection to the console
     * @param message
     */
    public static void socketLog(String message) {
        print("SOCKET", message, YELLOW);
    }

    /**
     * Prints a message from the client to the console
     * @param message
     */
    public static void clientLog(String message) {
        print("CLIENT", message, BLUE);
    }


    /**
     * Log a message between the model and the player
     * @param id
     * @param player
     * @param message
     */
    public static void playerLog(int id, String player, String message){
        String prefix = isRMI() ? "RMI" : "SOCKET";
        print("MODEL",
                "[" + id + "]" +
                        "[" + prefix + "]" +
                        "[" + player + "]" + message, CYAN);
    }

    /**
     * Check if the client is running in RMI mode
     * @return
     */
    public static boolean isRMI(){
        try {
            RemoteServer.getClientHost();
            return true;
        } catch (ServerNotActiveException e) {
            return false;
        }
    }
}
