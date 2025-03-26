package it.polimi.softeng.is25am10;

import it.polimi.softeng.is25am10.network.ClientToServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {
    public static ClientToServer getRMIConnection(String host, int port) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            return (ClientToServer) registry.lookup("controller");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws RemoteException {
        ClientToServer server = getRMIConnection("localhost", 1234);
        server.join("fede");
    }
}
