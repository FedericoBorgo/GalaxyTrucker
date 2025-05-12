package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.State;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardOutput;
import it.polimi.softeng.is25am10.network.Callback;
import javafx.application.Platform;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public class GUIEventListener extends UnicastRemoteObject implements Remote, Callback {
    Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    protected GUIEventListener(Callback callback) throws RemoteException {
        this.callback = callback;
    }

    @Override
    public void pushPlayers(HashMap<String, FlightBoard.Pawn> players, HashSet<String> quid, HashSet<String> disconnected) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.pushPlayers(players, quid, disconnected);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public int askHowManyPlayers() throws RemoteException {
        AtomicInteger res =  new AtomicInteger(2);
        Platform.runLater(() -> {
            try {
                res.set(callback.askHowManyPlayers());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        return res.get();
    }

    @Override
    public void pushSecondsLeft(Integer seconds) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.pushSecondsLeft(seconds);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void pushState(State.Type state) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.pushState(state);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void pushCardData(CardData card) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.pushCardData(card);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void pushCardChanges(CardOutput output) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.pushCardChanges(output);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void waitFor(String name, FlightBoard.Pawn pawn) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.waitFor(name, pawn);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void gaveTile(Tile t) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.gaveTile(t);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void gotTile(Tile t) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.gotTile(t);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void pushBoard(ShipBoard board) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.pushBoard(board);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void pushFlight(FlightBoard board) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.pushFlight(board);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public int ping() throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.ping();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        return 0;
    }

    @Override
    public void placeTile(Coordinate c, Tile t, Tile.Rotation r) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.placeTile(c, t, r);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void bookedTile(Tile t) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.bookedTile(t);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void removed(Coordinate c) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.removed(c);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void pushDropped(Model.Removed dropped) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.pushDropped(dropped);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void pushCannons(HashMap<Tile.Rotation, Integer> cannons) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.pushCannons(cannons);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void pushModel(Model m) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.pushModel(m);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
