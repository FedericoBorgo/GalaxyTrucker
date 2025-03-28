package it.polimi.softeng.is25am10;


import it.polimi.softeng.is25am10.network.socket.SocketListener;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        if(args.length > 0)
            Logger.SILENCE = Boolean.parseBoolean(args[0]);

        Controller controller = new Controller(1234);
        SocketListener listener = new SocketListener(controller, 1235, 1236);
    }
}