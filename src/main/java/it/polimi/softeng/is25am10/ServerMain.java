package it.polimi.softeng.is25am10;
import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        if(args.length > 0)
            Logger.SILENCE = Boolean.parseBoolean(args[0]);

        Controller controller = new Controller(1234, 1235, 1236);
    }
}