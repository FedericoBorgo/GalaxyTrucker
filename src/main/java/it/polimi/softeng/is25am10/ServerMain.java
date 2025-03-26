package it.polimi.softeng.is25am10;


import it.polimi.softeng.is25am10.model.Model;

import java.rmi.RemoteException;
import java.util.function.BiConsumer;

public class ServerMain {
    public static void main(String[] args) throws RemoteException {
        Controller controller = new Controller();
        Logger.serverLog("started");
        controller.join("marco");
    }
}