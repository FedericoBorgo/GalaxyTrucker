package it.polimi.softeng.is25am10.network;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import org.json.JSONObject;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * RMI calls from the client
 */

public class RMIClient extends UnicastRemoteObject implements ServerToClient {
    private ClientToServer server;
    private String name;

    public RMIClient(String name, String host, int port) throws RemoteException {
        super();
        this.name = name;

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            server = (ClientToServer) registry.lookup("controller");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }

        server.join(name, this);
    }

    /**
     * moves the timer
     * @return
     * @throws RemoteException
     */
    public Result<Integer> moveTimer() throws RemoteException {
        return server.moveTimer(name);
    }

    /**
     * sets the player to ready
     * @return
     * @throws RemoteException
     */
    public Result<String> setReady() throws RemoteException {
        return server.setReady(name);
    }

    /**
     * makes the player quit
     * @return
     * @throws RemoteException
     */
    public Result<String> quit() throws RemoteException {
        return server.quit(name);
    }

    /**
     * used to set a specific tile
     * @param c
     * @param t
     * @param rotation
     * @return
     * @throws RemoteException
     */
    public Result<Tile> setTile(Coordinate c, Tile t, Tile.Rotation rotation) throws RemoteException {
        return server.setTile(name, c, t, rotation);
    }

    /**
     * used to get a tile from the board
     * @param c
     * @return
     * @throws RemoteException
     */
    public Result<Tile> getTile(Coordinate c) throws RemoteException {
        return server.getTile(name, c);
    }

    /**
     * used to get a tile's rotation given the coordinates
     * @param c coordinates
     * @return rotation
     * @throws RemoteException
     */
    public Tile.Rotation getRotation(Coordinate c) throws RemoteException {
        return server.getRotation(name, c);
    }

    /**
     * books a tile while setting up the ship
     * @param t
     * @return
     * @throws RemoteException
     */
    public Result<Tile> bookTile(Tile t) throws RemoteException {
        return server.bookTile(name, t);
    }

    /**
     * makes the bord use the previously booked tile
     * @param t
     * @param rotation
     * @param c
     * @return
     * @throws RemoteException
     */
    public Result<Tile> useBookedTile(Tile t, Tile.Rotation rotation, Coordinate c) throws RemoteException {
        return server.useBookedTile(name, t, rotation, c);
    }

    /**
     * Returns a list with the board's booked tiles
     * @return a list of the booked tiles
     * @throws RemoteException
     */
    public List<Tile> getBooked() throws RemoteException {
        return server.getBooked(name);
    }

    /**
     * removes a specified tile
     * @param c
     * @return  a string that specifies how the operation went
     * @throws RemoteException
     */
    public Result<String> remove(Coordinate c) throws RemoteException {
        return server.remove(name, c);
    }

    /**
     * checks if the ship is compliant with the game's rules
     * @return
     * @throws RemoteException
     */
    public Set<Coordinate> checkShip() throws RemoteException {
        return server.checkShip(name);
    }

    /**
     * gets the ship in acompressed package from the server
     * @return
     * @throws RemoteException
     */
    public ShipBoard.CompressedShipBoard getShip() throws RemoteException {
        return server.getShip(name);
    }

    /**
     * carries out the aliens' initialization and returns a status code
     * @param purple
     * @param brown
     * @return
     * @throws RemoteException
     */
    public Result<String> init(Optional<Coordinate> purple, Optional<Coordinate> brown) throws RemoteException {
        return server.init(name, purple, brown);
    }

    /**
     * returns the list of rewards
     * @return
     * @throws RemoteException
     */
    public List<GoodsBoard.Type> getReward() throws RemoteException {
        return server.getReward(name);
    }

    /**
     * places the goods won as reward on the board
     * @param t
     * @param c
     * @return
     * @throws RemoteException
     */
    public Result<Integer> placeReward(GoodsBoard.Type t, Coordinate c) throws RemoteException {
        return server.placeReward(name, t, c);
    }

    /**
     * returns the amount of cash the player holds
     * @return
     * @throws RemoteException
     */
    public int getCash() throws RemoteException {
        return server.getCash(name);
    }

    /**
     * Removes one element on the coordinate {@code c}.
     * @param c
     * @return
     * @throws RemoteException
     */
    public Result<Integer> drop(Coordinate c) throws RemoteException {
        return server.drop(name, c);
    }

    /**
     * same as above but implemented for goods
     * @param c
     * @param t
     * @return
     * @throws RemoteException
     */
    public Result<Integer> drop(Coordinate c, GoodsBoard.Type t) throws RemoteException {
        return server.drop(name, c, t);
    }

    /**
     * makes the server know which cannons the player wants to use
     * @param map
     * @return
     * @throws RemoteException
     */
    public Result<String> setCannonsToUse(Map<Tile.Rotation, Integer> map) throws RemoteException {
        return server.setCannonsToUse(name, map);
    }

    /**
     * draws a tile from the deck while buiilding the ship
     * @return
     * @throws RemoteException
     */
    public Result<Tile> drawTile() throws RemoteException {
        return server.drawTile(name);
    }

    /**
     * returns tile already seen by the the player
     * @return
     * @throws RemoteException
     */
    public Result<List<Tile>> getSeenTiles() throws RemoteException {
        return server.getSeenTiles(name);
    }

    /**
     * assigns a tile to a player
     * @param t
     * @return
     * @throws RemoteException
     */
    public Result<String> giveTile(Tile t) throws RemoteException {
        return server.giveTile(name, t);
    }

    /**
     * returns a specific tile from the ones the player has already seen
     * @param t
     * @return
     * @throws RemoteException
     */
    public Result<Tile> getTileFromSeen(Tile t) throws RemoteException {
        return server.getTileFromSeen(name, t);
    }

    /**
     * draws a card from the deck
     * @return
     * @throws RemoteException
     */
    public Result<Card.CompressedCard> drawCard() throws RemoteException {
        return server.drawCard(name);
    }

    /**
     * sets the player's input for the drawn card
     * @param json
     * @return
     * @throws RemoteException
     */
    public Result<JSONObject> setInput(JSONObject json) throws RemoteException {
        return server.setInput(name, json);
    }

    /**
     * gets data about the drawn card
     * @return
     * @throws RemoteException
     */
    public Result<JSONObject> getCardData() throws RemoteException {
        return server.getCardData(name);
    }

    /**
     * returns the visible cards while the player is building the ship
     * @return
     * @throws RemoteException
     */
    public Result<Card[][]> getVisible() throws RemoteException {
        return server.getVisible(name);
    }

    /**
     * informs that a player has joined
     * @param player
     * @throws RemoteException
     */
    public void joinedPlayer(String player) throws RemoteException {
        System.out.println(name + ": joined " + player);
    }

    public int askHowManyPlayers() throws RemoteException {
        return 2;
    }

    /**
     * sends the state
     * @param state
     * @throws RemoteException
     */
    public void notifyState(Model.State.Type state) throws RemoteException {

    }

    /**
     * informs that the timer has been moved
     * @throws RemoteException
     */
    public void movedTimer() throws RemoteException {
        System.out.println(name + ": received removed timer");
    }

    /**
     * pushes the pawn's positions that have to change
     * @param order
     * @param offset
     * @throws RemoteException
     */
    public void pushPositions(List<FlightBoard.Pawn> order, List<Integer> offset) throws RemoteException {

    }

    /**
     * sends the card in compressed form
     * @param card
     * @throws RemoteException
     */
    public void pushCard(Card.CompressedCard card) throws RemoteException {

    }

    /**
     * pushes the changes associated with the card
     * @param data
     * @throws RemoteException
     */
    public void pushCardChanges(JSONObject data) throws RemoteException {

    }
}
