package it.polimi.softeng.is25am10.tui.asciiui;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.State;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.*;
import it.polimi.softeng.is25am10.model.cards.*;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
import javafx.util.Pair;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;


public class Game extends UnicastRemoteObject implements Callback {
    FrameGenerator frame;
    Map<String, Function<String, String>> executors = new HashMap<>();
    Map<String, Predicate<String>> checkers = new HashMap<>();

    ClientInterface server;

    public ShipBoard board = new ShipBoard();
    FlightBoard flight = new FlightBoard();
    ArrayList<Tile> openTiles = new ArrayList<>();

    List<GoodsBoard.Type> goodsReward = new ArrayList<>();
    CardData cardData = null;
    int cash = 0;
    public Tile currentTile = null;
    boolean notReady = true;
    public State.Type state = State.Type.JOINING;

    public FlightBoard.Pawn myPawn;


    public Game(ClientInterface server) throws IOException {
        super();

        frame = new FrameGenerator(this);

        executors.put("alieni", alien);
        executors.put("piazza", place);
        executors.put("rimuovi", remove);
        executors.put("prenota", book);
        executors.put("pesca", draw);
        executors.put("pronto", ready);
        executors.put("clessidra", clock);
        executors.put("esci", exit);
        executors.put("getta", drop);
        executors.put("cannoni", cannons);
        executors.put("invia", send);
        executors.put("abbandona", quit);
        executors.put("scatola", placeGoods);

        checkers.put("alieni", checkAlien);
        checkers.put("piazza", checkPlace);
        checkers.put("rimuovi", checkRemove);
        checkers.put("prenota", checkBook);
        checkers.put("pesca", checkDraw);
        checkers.put("pronto", checkReady);
        checkers.put("clessidra", checkClock);
        checkers.put("esci", checkExit);
        checkers.put("getta", checkDrop);
        checkers.put("cannoni", checkCannons);
        checkers.put("invia", checkSend);
        checkers.put("abbandona", checkQuit);
        checkers.put("scatola", checkPlaceGoods);

        this.server = server;

        Result<FlightBoard.Pawn> res = server.join(this);

        res.ifNotPresent(() -> {
            System.out.println("unable to join");
            System.exit(1);
        });

        myPawn = res.getData();
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

        Result<Coordinate> finalPurple = purple;
        Result<Coordinate> finalBrown = brown;
        res.ifPresent(_ -> {
            board.init(finalPurple.isOk()? Optional.of(finalPurple.getData()) : Optional.empty(),
                    finalBrown.isOk()? Optional.of(finalBrown.getData()) : Optional.empty());

            frame.drawElements();
        });

        return res.unwrap("nave riempita");
    };

    Predicate<String> checkAlien = (cmd) -> {
        if(state != State.Type.ALIEN_INPUT)
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

        Result<Tile> res;

        if(from == 2) {
            res = server.setTile(coord, currentTile, pos);
            res.ifPresent(_ -> currentTile = null);
            frame.drawCurrentTile();
        }
        else if (from < 2)
            res = server.useBookedTile(board.getBooked().get(from), pos, coord);
        else
            res = server.placeOpenTile(openTiles.get(from-3), pos, coord);

        return res.unwrap("piazzata");
    };

    Predicate<String> checkPlace = (cmd) -> {
        if(!notReady || state != State.Type.BUILDING)
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

    Function<String, String> remove = (cmd) -> server.remove(new Coordinate(cmd.charAt(0) - '0', cmd.charAt(1) - '0')).unwrap("rimosso");

    Predicate<String> checkRemove = (where) -> {
        if(state != State.Type.CHECKING)
            return false;

        int x = where.charAt(0) - '0';
        int y = where.charAt(1) - '0';

        return !Coordinate.isInvalid(x, y);
    };

    Function<String, String> book = (cmd) -> {
        if(currentTile != null)
            return server.bookTile(currentTile).unwrap("prenotato");
        return "nessun elemento pescato";
    };

    Predicate<String> checkBook = (cmd) -> state == State.Type.BUILDING;

    Function<String, String> draw = (cmd) -> {
        Result<?> res;
        if(state == State.Type.BUILDING){
            res = server.drawTile();
            if(res.isOk()){
                if(currentTile != null)
                    server.giveTile(currentTile);
                currentTile = (Tile)res.getData();
                frame.drawCurrentTile();
            }
        }
        else
            res = server.drawCard();
        return res.unwrap("pescata");
    };

    Predicate<String> checkDraw = (cmd) -> {
        if (state == State.Type.BUILDING && notReady)
            return true;
        return state == State.Type.DRAW_CARD && flight.getOrder().getFirst() == myPawn;
    };

    Function<String, String> ready = (cmd) -> {
        Result<String> res = server.setReady();
        res.ifPresent(_ -> notReady = false);
        return res.unwrap("ok");
    };

    Predicate<String> checkReady = (cmd) -> state == State.Type.BUILDING;

    Function<String, String> clock = (cmd) -> server.moveTimer().unwrap("spostato");

    Predicate<String> checkClock = (cmd) -> state == State.Type.BUILDING;

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

    Function<String, String> exit = (cmd) -> {
        System.exit(0);
        return null;
    };

    Predicate<String> checkExit = (cmd) -> true;

    Function<String, String> drop = (cmd) -> {
        Coordinate c = new Coordinate(cmd.charAt(0) - '0', cmd.charAt(1) - '0');
        Result<Integer> res;
        GoodsBoard.Type type;

        if(cmd.length() != 2){
            type = switch (cmd.charAt(3)) {
                case 'r' -> GoodsBoard.Type.RED;
                case 'b' -> GoodsBoard.Type.BLUE;
                case 'v' -> GoodsBoard.Type.GREEN;
                case 'g' -> GoodsBoard.Type.YELLOW;
                default -> throw new IllegalStateException("Unexpected value: " + cmd.charAt(3));
            };

            res = server.drop(c, type);
        }
        else {
            type = null;
            res = server.drop(c);
        }

        res.ifPresent(_ -> {
            board.removeSomeone(c);
            board.getBattery().remove(c, 1);

            if(type != null)
                board.getGoods(type).remove(c, 1);

            frame.drawElements();
        });

        return res.unwrap("rimosso");
    };

    Predicate<String> checkDrop = (cmd) -> {
        int x = cmd.charAt(0) - '0';
        int y = cmd.charAt(1) - '0';

        if (cmd.length() != 2) {
            GoodsBoard.Type type = switch (cmd.charAt(3)) {
                case 'r' -> GoodsBoard.Type.RED;
                case 'b' -> GoodsBoard.Type.BLUE;
                case 'v' -> GoodsBoard.Type.GREEN;
                case 'g' -> GoodsBoard.Type.YELLOW;
                default -> null;
            };

            if (type == null)
                return false;
        }

        return !Coordinate.isInvalid(x, y);
    };

    Function<String, String> cannons = (cmd) -> {
        String[] args = cmd.split(" ");
        int diff = args[0].equals("dim")? -1 : 1;

        Tile.Rotation rotation = switch(args[1].charAt(0) - '0'){
            case 0 -> Tile.Rotation.NONE;
            case 1 -> Tile.Rotation.CLOCK;
            case 2 -> Tile.Rotation.DOUBLE;
            case 3 -> Tile.Rotation.INV;
            default -> null;
        };

        return server.increaseCannon(rotation, diff).unwrap("cambiato");
    };

    Predicate<String> checkCannons = (cmd) -> {
        String[] args = cmd.split(" ");

        if(!(args[0].equals("dim") || args[0].equals("inc")))
            return false;

        int val = args[1].charAt(0) - '0';

        return val >= 0 && val <= 3;
    };

    Function<String, String> send = (cmd) -> {
        CardInput input = new CardInput();
        String[] args = cmd.split(" ");

        switch(cardData.type){
            case EPIDEMIC:
            case STARDUST:
            case OPEN_SPACE:
                break;
            case PLANETS:
                input.planet = Planets.Planet.values()[Integer.parseInt(args[0])-1];
                break;
            case METEORS:
            case WAR_ZONE:
                for(String s: args)
                    if(!s.isEmpty())
                        input.shieldFor.add(Integer.parseInt(s));
                break;
            case AB_SHIP:
            case STATION:
            case SLAVERS:
            case SMUGGLERS:
                input.accept = args[0].equals("si");
                break;
            case PIRATES:
                input.accept = args[0].equals("si");

                for (int i = 1; i < args.length ; i++)
                    input.shieldFor.add(Integer.parseInt(args[i]));
                break;

        }

        return server.setInput(input).unwrap("dichiarato");
    };

    Predicate<String> checkSend = (cmd) -> {
        String[] args = cmd.split(" ");

        if(state != State.Type.WAITING_INPUT)
            return false;

        switch(cardData.type){
            case EPIDEMIC:
            case STARDUST:
            case OPEN_SPACE:
                return true;
            case PLANETS:
                Planets.Planet chosen = Planets.Planet.values()[Integer.parseInt(args[0])-1];

                if(cardData.chosenPlanets.contains(chosen) && chosen != Planets.Planet.NOPLANET)
                    return false;

                return cardData.planets.containsKey(chosen) || chosen == Planets.Planet.NOPLANET;
            case METEORS:
            case WAR_ZONE:
                for (String s : args)
                    if(!s.isEmpty() && Integer.parseInt(s) > cardData.projectiles.getLast().ID())
                        return false;
                return true;
            case AB_SHIP:
            case STATION:
            case SLAVERS:
            case SMUGGLERS:
                return args[0].equals("si") || args[0].equals("no");
            case PIRATES:
                if(!(args[0].equals("si") || args[0].equals("no")))
                    return false;

                for (int i = 1; i < args.length ; i++)
                    if(Integer.parseInt(args[i]) > cardData.projectiles.getLast().ID())
                        return false;
                return true;
        }
        return false;
    };

    Function<String, String> quit = (cmd) -> server.quit().unwrap("abbandonato");

    Predicate<String> checkQuit = (cmd) -> state == State.Type.DRAW_CARD;

    Function<String, String> placeGoods = (cmd) -> {
        String[] args = cmd.split(" ");
        Result<?> res;

        if(args[0].equals("no")){
            res = server.dropReward();
            goodsReward = new ArrayList<>();
            frame.drawGoods(goodsReward);
        }
        else{
            GoodsBoard.Type type = switch(args[0].charAt(0)){
                case 'r' -> GoodsBoard.Type.RED;
                case 'b' -> GoodsBoard.Type.BLUE;
                case 'v' -> GoodsBoard.Type.GREEN;
                case 'g' -> GoodsBoard.Type.YELLOW;
                default -> throw new IllegalStateException("Unexpected value: " + args[0]);
            };

            Coordinate c = new Coordinate(args[1].charAt(0) - '0', args[1].charAt(1) - '0');

            res = server.placeReward(type, c);

            res.ifPresent(_ -> {
                goodsReward.remove(type);
                board.getGoods(type).put(c, 1);
                frame.drawElements();
                frame.drawGoods(goodsReward);
            });
        }

        return res.unwrap("fatto");
    };

    Predicate<String> checkPlaceGoods = (cmd) -> {
        if(state != State.Type.PLACE_REWARD)
            return false;

        String[] args = cmd.split(" ");

        if(args[0].equals("no"))
            return true;

        GoodsBoard.Type type = switch(args[0].charAt(0)){
            case 'r' -> GoodsBoard.Type.RED;
            case 'b' -> GoodsBoard.Type.BLUE;
            case 'v' -> GoodsBoard.Type.GREEN;
            case 'g' -> GoodsBoard.Type.YELLOW;
            default -> throw new IllegalStateException("Unexpected value: " + args[0]);
        };

        if(!goodsReward.contains(type))
            return false;

        int x = args[1].charAt(0) - '0';
        int y = args[1].charAt(1) - '0';

        return !Coordinate.isInvalid(x, y);
    };

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

    // callbacks

    @Override
    public synchronized void pushPlayers(HashMap<String, FlightBoard.Pawn> players, HashSet<String> quid, HashSet<String> disconnected) throws RemoteException {
        frame.drawPlayersName(players, quid, disconnected);
    }

    @Override
    public synchronized int askHowManyPlayers() throws RemoteException {
        return frame.askHowManyPlayer();
    }

    @Override
    public synchronized void pushSecondsLeft(Integer seconds) throws RemoteException {
        frame.drawSecondsLeft(seconds);
    }

    @Override
    public synchronized void pushState(State.Type state) throws RemoteException {
        this.state = state;

        if(state == State.Type.DRAW_CARD) {
            frame.clearUnusedSpace();
            frame.clearCardData();
        }

        if(state == State.Type.CHECKING)
            frame.drawErrors(board.getTiles().isOK());

        if(state == State.Type.WAITING_INPUT)
            frame.clearDestroyed();

        frame.drawState();
    }

    @Override
    public synchronized void pushCardData(CardData card) throws RemoteException {
        if(card == null)
            return;
        
        cardData = card;
        frame.drawCardData(card);
        frame.drawProjectile(card.projectiles);
    }

    @Override
    public synchronized void pushCardChanges(CardOutput output) throws RemoteException {
        List<Coordinate> remove = output.killedCrew.getOrDefault(server.getPlayerName(), null);

        if(remove != null) {
            remove.forEach(c -> {
                board.removeSomeone(c);
            });
            frame.drawElements();
        }

        remove = output.removed.getOrDefault(server.getPlayerName(), null);

        if(remove != null) {
            remove.forEach(c -> {
                board.getTiles().remove(c);
                frame.destroyed(c);
            });
            board.removeIllegals();
            frame.drawErrors(board.getTiles().isOK());
        }

        goodsReward = output.rewards.getOrDefault(server.getPlayerName(), new ArrayList<>());
        cash += output.cash.getOrDefault(server.getPlayerName(), 0);
        frame.drawCash(cash);
        frame.clearProjectile();
        frame.drawGoods(goodsReward);
    }

    @Override
    public synchronized void waitFor(String name, FlightBoard.Pawn pawn) throws RemoteException {
        frame.drawWaitFor(name, pawn);
    }

    @Override
    public synchronized void gaveTile(Tile t) throws RemoteException {
        openTiles.add(t);
        frame.drawOpenTiles();
    }

    @Override
    public synchronized void gotTile(Tile t) throws RemoteException {
        openTiles.remove(t);
        frame.drawOpenTiles();
    }

    @Override
    public synchronized void pushBoard(ShipBoard board) throws RemoteException {
        this.board = board;
        frame.drawTilesBoar(board.getTiles());
        frame.drawBooked();
        frame.drawElements();
    }

    @Override
    public synchronized void pushFlight(FlightBoard board) throws RemoteException {
        this.flight = board;
        frame.drawFlight();
    }

    @Override
    public synchronized int ping(){
        return 0;
    }

    @Override
    public synchronized void placeTile(Coordinate c, Tile t, Tile.Rotation r) throws RemoteException {
        board.getTiles().getBooked().removeIf((tile) -> tile.equals(t));
        board.getTiles().setTile(c, t, r);
        frame.drawTile(c, t, r);
        frame.drawBooked();
    }

    @Override
    public synchronized void bookedTile(Tile t) throws RemoteException {
        board.getTiles().bookTile(t);
        frame.drawBooked();
    }

    @Override
    public synchronized void removed(Coordinate c) throws RemoteException{
        board.getTiles().remove(c);
        board.removeIllegals();
        frame.destroyed(c);
    }

    @Override
    public synchronized void pushDropped(Model.Removed dropped) throws RemoteException {
        frame.drawDroppedItems(dropped);
    }

    @Override
    public synchronized void pushCannons(HashMap<Tile.Rotation, Integer> cannons) throws RemoteException {
        frame.drawCannonsToUse(cannons);
    }

    @Override
    public synchronized void pushModel(Model m) throws RemoteException {
        pushBoard(m.ship(server.getPlayerName()));
        pushFlight(m.getFlight());
        pushState(m.getState());
        
        CardData c = m.getCardData();
        
        if(c != null){
            frame.clearUnusedSpace();
            pushDropped(m.getRemoved(server.getPlayerName()));
            pushCannons(m.getCannonsToUse(server.getPlayerName()));
            pushCardData(m.getCardData());
            String next = m.getNextToPlay();
            if(next != null)
                waitFor(next, m.getPlayers().get(next));
        }

        m.getSeenTiles().forEach(t -> {
            try {
                gaveTile(t);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
