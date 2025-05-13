package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.State;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.boards.TilesBoard;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardOutput;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.util.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public class GUIEventListener extends UnicastRemoteObject implements Remote, Callback {
    Callback callback;
    ClientInterface server;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    protected GUIEventListener(ClientInterface server, Callback callback) throws RemoteException {
        this.server = server;
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

        return 4;
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
    public void pushFinalCash(HashMap<String, Integer> cash) throws RemoteException {
        Platform.runLater(() -> {
            try {
                callback.pushFinalCash(cash);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void pushModel(Model m) throws RemoteException {
        Platform.runLater(() -> {
            try {
                if(m.phase == 0)
                    rejoinedPhase0(m);
                else if(m.phase == 1)
                    rejoinedPhase1(m);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void rejoinedPhase0(Model m) throws RemoteException {
        Pair<Building, Scene> p = Launcher.loadScene("/gui/building.fxml");
        Building b = p.getKey();
        Scene s = p.getValue();
        String name = server.getPlayerName();

        callback = b;
        b.config(m.getPlayers().get(name), name, server, s);
        b.pushPlayers(m.getPlayers(), m.getQuit(), new HashSet<>());
        b.pushState(m.getState());

        for (Tile t : m.getSeenTiles())
            b.gaveTile(t);

        TilesBoard tb = m.ship(name).getTiles();

        b.pushFlight(m.getFlight());

        Coordinate.forEach(c -> {
            Result<Tile> res = tb.getTile(c);

            if(res.isErr())
                return;

            Tile t = res.getData();
            Tile.Rotation r = tb.getRotation(c);

            if(!Tile.real(t) || t.getType() == Tile.Type.C_HOUSE)
                return;

            try {
                b.placeTile(c, t, r);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        for (Tile tile : tb.getBooked())
            b.bookedTile(tile);
    }

    private void rejoinedPhase1(Model m) throws RemoteException {
        Pair<CardScene, Scene> p = Launcher.loadScene("/gui/card.fxml");
        CardScene c = p.getKey();
        String name = server.getPlayerName();

        callback = c;
        c.config(server, this, m.getFlight(), m.getState().getName(), m.ship(name), m.getPlayers());
        c.cashText.setText("" + m.getCash(name));

        String next;
        if((next = m.getNextToPlay()) != null)
            c.waitFor(next, m.getPlayers().get(next));

        CardData data;
        if((data = m.getCardData()) != null)
            c.pushCardData(data);
    }
}
