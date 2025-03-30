package it.polimi.softeng.is25am10.network.rmi;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
import org.json.JSONObject;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RMIClient implements ClientInterface {
    private RMIInterface server;
    private String name;

    public RMIClient(String name, String host, int port) {
        this.name = name;

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            server = (RMIInterface) registry.lookup("controller");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPlayerName() {
        return name;
    }

    public Result<FlightBoard.Pawn> join(Callback callback) {
        try {
            server.setCallback(name, callback);
            return server.join(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<Integer> moveTimer() {
        try {
            return server.moveTimer(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<String> setReady() {
        try {
            return server.setReady(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<String> quit() {
        try {
            return server.quit(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<Tile> setTile(Coordinate c, Tile t, Tile.Rotation rotation) {
        try {
            return server.setTile(name, c, t, rotation);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<Tile> getTile(Coordinate c) {
        try {
            return server.getTile(name, c);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Tile.Rotation getRotation(Coordinate c) {
        try {
            return server.getRotation(name, c);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<Tile> bookTile(Tile t) {
        try {
            return server.bookTile(name, t);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<Tile> useBookedTile(Tile t, Tile.Rotation rotation, Coordinate c) {
        try {
            return server.useBookedTile(name, t, rotation, c);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Tile> getBooked() {
        try {
            return server.getBooked(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<String> remove(Coordinate c) {
        try {
            return server.remove(name, c);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Coordinate> checkShip() {
        try {
            return server.checkShip(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public ShipBoard.CompressedShipBoard getShip() {
        try {
            return server.getShip(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<String> init(Optional<Coordinate> purple, Optional<Coordinate> brown) {
        try {
            return server.init(name, purple, brown);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<GoodsBoard.Type> getReward() {
        try {
            return server.getReward(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<Integer> placeReward(GoodsBoard.Type t, Coordinate c) {
        try {
            return server.placeReward(name, t, c);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public int getCash() {
        try {
            return server.getCash(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<Integer> drop(Coordinate c) {
        try {
            return server.drop(name, c);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<Integer> drop(Coordinate c, GoodsBoard.Type t) {
        try {
            return server.drop(name, c, t);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<String> setCannonsToUse(Map<Tile.Rotation, Integer> map) {
        try {
            return server.setCannonsToUse(name, map);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<Tile> drawTile() {
        try {
            return server.drawTile(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<List<Tile>> getSeenTiles() {
        try {
            return server.getSeenTiles(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<String> giveTile(Tile t) {
        try {
            return server.giveTile(name, t);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<Tile> getTileFromSeen(Tile t) {
        try {
            return server.getTileFromSeen(name, t);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<Card.CompressedCard> drawCard() {
        try {
            return server.drawCard(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<JSONObject> setInput(JSONObject json) {
        try {
            return server.setInput(name, json);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<JSONObject> getCardData() {
        try {
            return server.getCardData(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Result<Card[][]> getVisible() {
        try {
            return server.getVisible(name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
