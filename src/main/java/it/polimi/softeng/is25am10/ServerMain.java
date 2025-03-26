package it.polimi.softeng.is25am10;


import it.polimi.softeng.is25am10.network.RMICLient;

import java.rmi.RemoteException;

public class ServerMain {
    public static void main(String[] args) throws RemoteException {
        Controller controller = new Controller();
        Logger.serverLog("started");
        controller.join("marco", new RMICLient());
    }
}