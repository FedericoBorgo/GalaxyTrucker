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
import javafx.util.Pair;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;


public class Game extends UnicastRemoteObject implements Callback {
    FrameGenerator frame;
    ClientInterface server;
    ShipBoard board = new ShipBoard();
    FlightBoard flight = new FlightBoard();
    ArrayList<Tile> openTiles = new ArrayList<>();

    Map<String, FlightBoard.Pawn> players = new HashMap<>();
    Tile currentTile = null;
    long startTime = 0;

    boolean notReady = true;

    Model.State.Type state = Model.State.Type.JOINING;
    Map<String, Function<String, String>> executors = new HashMap<>();
    Map<String, Predicate<String>> checkers = new HashMap<>();


    public Game(ClientInterface server) throws IOException {
        super();
        frame = new FrameGenerator(this);
        try{
            server.join(this).getData();
        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }

        executors.put("alieni", alien);
        executors.put("piazza", place);
        executors.put("rimuovi", remove);
        executors.put("prenota", book);
        executors.put("pesca", draw);
        executors.put("pronto", ready);
        executors.put("clessidra", clock);

        checkers.put("alieni", checkAlien);
        checkers.put("piazza", checkPlace);
        checkers.put("rimuovi", checkRemove);
        checkers.put("prenota", checkBook);
        checkers.put("pesca", checkDraw);
        checkers.put("pronto", checkReady);
        checkers.put("clessidra", checkClock);

        this.server = server;
    }

    Function<String, String> alien = (cmd) -> {
        String[] args = cmd.split(" ");
        Result<Coordinate> brown, purple;
        int x, y;

        brown = Result.err("");
        purple = Result.err("");

        if(!cmd.equals("no")){
            for (String arg : args) {
                x = arg.charAt(1) - '0';
                y = arg.charAt(2) - '0';

                switch (arg.charAt(0)) {
                    case 'v':
                        purple = Result.ok(new Coordinate(x, y));
                        break;
                    case 'm':
                        brown = Result.ok(new Coordinate(x, y));
                        break;
                }
            }
        }

        Result<String> res = server.init(purple, brown);

        if(res.isErr())
            return res.getReason();

        board.init(purple.isOk()? Optional.of(purple.getData()) : Optional.empty(),
                brown.isOk()? Optional.of(brown.getData()) : Optional.empty());
        return "nave riempita";
    };

    Predicate<String> checkAlien = (cmd) -> {
        if(state != Model.State.Type.ALIEN_INPUT)
            return false;

        String[] args = cmd.split(" ");

        if(args[0].equals("no"))
            return true;

        if(args.length > 2)
            return false;

        for (String arg : args) {
            if (arg.charAt(0) != 'v' && arg.charAt(0) != 'm')
                return false;

            if (!Character.isDigit(arg.charAt(1)) ||
                    !Character.isDigit(arg.charAt(2)))
                return false;

            int x = arg.charAt(1) - '0';
            int y = arg.charAt(2) - '0';

            if (Coordinate.isInvalid(x, y))
                return false;
        }

        return true;
    };

    Function<String, String> place = (cmd) -> {
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
    };

    Predicate<String> checkPlace = (cmd) -> {
        if(!notReady || state != Model.State.Type.BUILDING)
            return false;

        String[] args = cmd.split(" ");
        if(args.length < 3)
            return false;

        if(Integer.parseInt(args[0]) > 30)
            return false;

        if(!Character.isDigit(args[1].charAt(0)) ||
                !Character.isDigit(args[1].charAt(1)))
            return false;

        int x = args[1].charAt(0) - '0';
        int y = args[1].charAt(1) - '0';

        if(Coordinate.isInvalid(x, y))
            return false;

        int rot = args[2].charAt(0) - '0';

        return rot < 4;
    };

    Function<String, String> remove = (cmd) -> {
        Coordinate c = new Coordinate(cmd.charAt(0) - '0', cmd.charAt(1) - '0');

        Result<String> res = server.remove(c);

        if(res.isErr())
            return res.getReason();

        board.getTiles().remove(c);
        return "rimosso";
    };

    Predicate<String> checkRemove = (where) -> {
        if(state != Model.State.Type.CHECKING)
            return false;

        int x = where.charAt(0) - '0';
        int y = where.charAt(1) - '0';

        return !Coordinate.isInvalid(x, y);
    };

    Function<String, String> book = (cmd) -> {
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
    };

    Predicate<String> checkBook = (cmd) -> state == Model.State.Type.BUILDING;

    Function<String, String> draw = (cmd) -> {
        Result<Tile> res = server.drawTile();

        if(res.isErr())
            return res.getReason();
        else {
            if(currentTile != null)
                server.giveTile(currentTile);
            currentTile = res.getData();
            return "pescata";
        }
    };

    Predicate<String> checkDraw = (cmd) -> state == Model.State.Type.BUILDING && notReady;

    Function<String, String> ready = (cmd) -> {
        Result<String> res = server.setReady();

        if(res.isErr())
            return res.getReason();
        else {
            notReady = false;
            return "ok";
        }
    };

    Predicate<String> checkReady = (cmd) -> state == Model.State.Type.BUILDING;

    Function<String, String> clock = (cmd) -> {
        Result<Integer> res = server.moveTimer();

        if(res.isErr())
            return res.getReason();
        else {
            startTime = System.currentTimeMillis();
            return "spostata";
        }
    };

    Predicate<String> checkClock = (cmd) -> state == Model.State.Type.BUILDING;

    Pair<String, String> convert(String cmd){
        int divider = cmd.indexOf(' ');
        String request = cmd;
        String args = "";

        if (divider != -1){
            request = request.substring(0, divider);

            try{
                args = cmd.substring(divider + 1);
            }
            catch(Exception _) {};
        }

        return new Pair<>(request, args);
    }

    public String execute(String cmd) {
        Pair<String, String> request = convert(cmd);
        return executors.get(request.getKey()).apply(request.getValue());
    };

    public boolean checkCommand(String cmd) {
        try{
            Pair<String, String> request = convert(cmd);
            return checkers.get(request.getKey()).test(request.getValue());
        }
        catch (Exception _) {}
        return false;
    }

    @Override
    public void setPlayers(HashMap<String, FlightBoard.Pawn> players) throws RemoteException {
        this.players = players;
    }

    @Override
    public int askHowManyPlayers() throws RemoteException {
        return frame.askHowManyPlayer();
    }

    @Override
    public void pushState(Model.State.Type state) throws RemoteException {
        this.state = state;

        if(state == Model.State.Type.BUILDING)
            startTime = System.currentTimeMillis();
    }

    @Override
    public void pushCard(Card card) throws RemoteException {

    }

    @Override
    public void pushCardChanges(String data) throws RemoteException {

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

    @Override
    public void pushBoard(ShipBoard board) throws RemoteException {
        this.board = board;
    }

    @Override
    public void pushFlight(FlightBoard board) throws RemoteException {
        this.flight = board;
        startTime = System.currentTimeMillis();
        System.out.println();
    }

    @Override
    public int ping(){
        return 0;
    }
}
