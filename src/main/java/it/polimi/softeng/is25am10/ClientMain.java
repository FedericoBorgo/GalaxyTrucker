package it.polimi.softeng.is25am10;

import it.polimi.softeng.is25am10.network.RMIClient;

import java.rmi.RemoteException;

public class ClientMain {
    public static void main(String[] args) throws RemoteException, InterruptedException {
        RMIClient clientA = new RMIClient("fede", "localhost", 1234);
        RMIClient clientB = new RMIClient("marco", "localhost", 1234);

        clientA.moveTimer();
        Thread.sleep(100);
        clientB.moveTimer();
        Thread.sleep(100);
        clientA.movedTimer();
    }
}
