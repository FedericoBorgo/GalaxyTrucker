package it.polimi.softeng.is25am10.client.asciiui;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.boards.TilesBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
import org.json.JSONObject;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class Game extends UnicastRemoteObject implements Callback {
    final FrameGenerator frame;
    final ClientInterface server;
    final ShipBoard board = new ShipBoard();
    final FlightBoard flight = new FlightBoard();
    final ArrayList<Tile> openTiles = new ArrayList<>();

    Map<String, FlightBoard.Pawn> players = new HashMap<>();
    Tile currentTile = null;
    long startTime = 0;

    boolean notReady = true;

    Model.State.Type state = Model.State.Type.JOINING;


    public Game(ClientInterface server) throws IOException {
        super();
        frame = new FrameGenerator(this);
        server.join(this).getData();
        this.server = server;
    }

    boolean checkCommand(String command) {
        try{
            if (command.equals("clessidra") && state == Model.State.Type.BUILDING) {
                return true;
            }
            else if(command.equals("pronto") && state == Model.State.Type.BUILDING) {
                return true;
            }
            else if(command.equals("pesca") && state == Model.State.Type.BUILDING && notReady) {
                return true;
            }
            else if(command.equals("prenota") && state == Model.State.Type.BUILDING && notReady) {
                return true;
            }
            else if(notReady && command.substring(0, command.indexOf(' ')).equals("piazza") && state == Model.State.Type.BUILDING) {
                String[] args = command.substring(command.indexOf(' ') + 1).split(" ");
                if(args.length < 3)
                    return false;

                if(Integer.parseInt(args[0]) > 30)
                    return false;

                if(!Character.isDigit(args[1].charAt(0)) ||
                    !Character.isDigit(args[1].charAt(1)))
                    return false;

                int x = args[1].charAt(0) - '0';
                int y = args[1].charAt(1) - '0';

                if(Coordinate.check(x, y))
                    return false;

                int rot = args[2].charAt(0) - '0';

                return rot < 4;
            }
        }
        catch (Exception _) {}
        return false;
    }


    String handleInsert(String cmd){;
        if(cmd.equals("clessidra")){
            Result<Integer> res = server.moveTimer();

            if(res.isErr())
                return res.getReason();
            else {
                flight.moveTimer();
                startTime = System.currentTimeMillis();
                return "spostata";
            }
        }
        else if(cmd.equals("pronto")){
            Result<String> res = server.setReady();

            if(res.isErr())
                return res.getReason();
            else {
                notReady = false;
                return "ok";
            }
        }
        else if(cmd.equals("pesca")){
            Result<Tile> res = server.drawTile();

            if(res.isErr())
                return res.getReason();
            else {
                if(currentTile != null)
                    server.giveTile(currentTile);
                currentTile = res.getData();
                return "pescata";
            }
        }
        else if(cmd.equals("prenota")){
            if(currentTile != null){
                Result<Tile> res = server.bookTile(currentTile);

                if(res.isErr())
                    return res.getReason();
                else {
                    board.getTiles().bookTile(currentTile);
                    currentTile = null;
                    return "fatto";
                }
            }
            else
                return "nessun elemento pescato";
        }
        else if(cmd.contains("piazza")){
            return handlePlace(cmd.substring(cmd.indexOf(' ') + 1));
        }

        return "comando non trovato";
    }


    String handlePlace(String cmd){
        TilesBoard board = this.board.getTiles();
        String[] args = cmd.split(" ");
        int from = Integer.parseInt(args[0]);

        switch(from){
            case 0:
                if(this.board.getTiles().getBooked().isEmpty())
                    return "impossibile prendere all'indice " + from;
                break;
            case 1:
                if(this.board.getTiles().getBooked().size() < 2)
                    return "impossibile prendere all'indice " + from;
                break;
            case 2:
                if(currentTile == null)
                    return "impossibile prendere all'indice " + from;
                break;
            default:
                if(from-3+1 > openTiles.size())
                    return "impossibile prendere all'indice " + from;
        }

        //0 x1y2 4
        Coordinate coord = new Coordinate(args[1].charAt(0) - '0', args[1].charAt(1) - '0');
        Tile.Rotation pos = switch(args[2].charAt(0) - '0'){
            case 0 -> Tile.Rotation.NONE;
            case 1 -> Tile.Rotation.CLOCK;
            case 2 -> Tile.Rotation.DOUBLE;
            case 3 -> Tile.Rotation.INV;
            default -> Tile.Rotation.NONE;
        };

        if(from == 2) {
            Result<Tile> res = server.setTile(coord, currentTile, pos);

            if(res.isOk()){
                board.setTile(coord, currentTile, pos);
                currentTile = null;
                return "piazzata";
            }
            return res.getReason();
        }
        else if (from < 2){
            Tile t = board.getBooked().get(from);

            Result<Tile> res = server.useBookedTile(t, pos, coord);

            if(res.isOk()){
                board.useBookedTile(t, pos, coord);
                return "piazzata";
            }
            return res.getReason();
        }
        else {
            from = from-3;
            Tile t = openTiles.get(from);
            Result<Tile> res = server.getTileFromSeen(t);

            if(res.isErr())
                return res.getReason();

            res = server.setTile(coord, t, pos);

            if(res.isOk()){
                board.setTile(coord, t, pos);
                return "piazzata";
            }
            else {
                server.giveTile(t);
                return res.getReason();
            }
        }
    }


    @Override
    public void setPlayers(Map<String, FlightBoard.Pawn> players) throws RemoteException {
        this.players = players;
    }

    @Override
    public int askHowManyPlayers() throws RemoteException {
        return frame.askHowManyPlayer();
    }

    @Override
    public void notifyState(Model.State.Type state) throws RemoteException {
        this.state = state;

        if(state == Model.State.Type.BUILDING)
            startTime = System.currentTimeMillis();
    }

    @Override
    public void movedTimer() throws RemoteException {
        flight.moveTimer();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void pushPositions(List<FlightBoard.Pawn> order, List<Integer> offset) throws RemoteException {
        flight.set(order, offset);
    }

    @Override
    public void pushCard(Card.CompressedCard card) throws RemoteException {

    }

    @Override
    public void pushCardChanges(JSONObject data) throws RemoteException {

    }

    @Override
    public void askForInput() throws RemoteException {

    }

    @Override
    public void gaveTile(Tile t) throws RemoteException {
        openTiles.add(t);
    }

    @Override
    public void gotTile(Tile t) throws RemoteException {
        openTiles.remove(t);
    }

}
