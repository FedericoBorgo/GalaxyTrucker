package it.polimi.softeng.is25am10.network.rmi;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardInput;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;

import java.lang.reflect.InvocationTargetException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<FlightBoard.Pawn> join(Callback callback) {
        try {
            server.setCallback(getPlayerName(), callback);
            return server.join(getPlayerName());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
