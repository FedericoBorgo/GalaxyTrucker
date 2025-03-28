package it.polimi.softeng.is25am10;

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


    private static void print(String prefix, String message, String color) {
        if(SILENCE)
            return;
        System.out.println(RESET + color + "[" + prefix + "]" + message);
    }

    public static void serverLog(String message) {
        print("SERVER", message, WHITE);
    }

    public static void modelLog(int id, String message) {
        print("MODEL", "[" + id + "]" + message, PURPLE);
    }

    public static void socketLog(String message) {
        print("SOCKET", message, YELLOW);
    }

    public static void clientLog(String message) {
        print("CLIENT", message, BLUE);
    }


    public static void playerLog(int id, String player, String message){
        String prefix = Controller.isRMI() ? "RMI" : "SOCKET";
        print("MODEL",
                "[" + id + "]" +
                        "[" + prefix + "]" +
                        "[" + player + "]" + message, CYAN);
    }
}
