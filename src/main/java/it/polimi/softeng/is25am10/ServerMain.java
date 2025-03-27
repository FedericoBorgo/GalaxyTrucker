package it.polimi.softeng.is25am10;


import java.rmi.RemoteException;

public class ServerMain {
    public static void main(String[] args) throws RemoteException {
        Controller controller = new Controller(1234);
    }
}