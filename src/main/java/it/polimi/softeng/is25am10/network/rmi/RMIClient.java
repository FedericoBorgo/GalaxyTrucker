package it.polimi.softeng.is25am10.network.rmi;

import it.polimi.softeng.is25am10.gui.End;
import it.polimi.softeng.is25am10.gui.Launcher;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
import javafx.application.Platform;

import java.lang.reflect.InvocationTargetException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient extends ClientInterface {
    private final RMIInterface server;

    public RMIClient(String name, String host, int port) {
        super(name);

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            server = (RMIInterface) registry.lookup("controller");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected  <T> T call(Object... args){
        try {
            return (T) RMIInterface.class.getMethod(getCallerName(), getClasses(args))
                    .invoke(server, args);
        } catch (Exception e) {
            Platform.runLater(() -> {
                ((End) Launcher.loadScene("/gui/end.fxml").getKey()).disconnected.setVisible(true);
            });
            throw new RuntimeException(e);
        }
    }

    public Result<FlightBoard.Pawn> join(Callback callback) {
        try {
            server.setCallback(getPlayerName(), callback);
            return server.join(getPlayerName());
        } catch (RemoteException e) {
            Platform.runLater(() -> {
                ((End) Launcher.loadScene("/gui/end.fxml").getKey()).disconnected.setVisible(true);
            });
            throw new RuntimeException(e);
        }
    }
}
