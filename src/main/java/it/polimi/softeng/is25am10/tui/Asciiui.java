package it.polimi.softeng.is25am10.tui;

import it.polimi.softeng.is25am10.Controller;
import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.TilesCollection;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.boards.TilesBoard;
import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;
import it.polimi.softeng.is25am10.network.rmi.RMIInterface;
import it.polimi.softeng.is25am10.network.socket.SocketClient;
import it.polimi.softeng.is25am10.network.socket.SocketListener;
import it.polimi.softeng.is25am10.tui.asciiui.Config;
import it.polimi.softeng.is25am10.tui.asciiui.Game;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.random.RandomGenerator;

public class Asciiui {
    static Random rmd = new Random();

    static TilesCollection collection = new TilesCollection();

    static Tile genRandomTile() {
        Tile t = collection.getNew();
        if(t == null){
            collection = new TilesCollection();
            t = collection.getNew();
        }
        return t;
    }

    static Tile.Rotation genRandomOri(){
        return switch(rmd.nextInt(0, 4)){
            case 1 -> Tile.Rotation.CLOCK;
            case 2 -> Tile.Rotation.DOUBLE;
            case 3 -> Tile.Rotation.INV;
            default -> Tile.Rotation.NONE;
        };
    }

    static int getOri(Tile.Rotation rot){
        return switch(rot){
            case CLOCK -> 1;
            case DOUBLE -> 2;
            case INV -> 3;
            default -> 0;
        };
    }

    static Coordinate genRandomCoord(){
        return new Coordinate(rmd.nextInt(0, TilesBoard.BOARD_WIDTH), rmd.nextInt(0, TilesBoard.BOARD_HEIGHT));
    }

    static void genRandomShip(Game game) {
        TilesBoard board = new TilesBoard();
        AtomicInteger piazzate = new AtomicInteger(0);

        while(piazzate.get() < 50){
            Coordinate c = genRandomCoord();
            Tile t = genRandomTile();
            Tile.Rotation r = genRandomOri();

            board.setTile(c, t, r).ifPresent(_ -> {
                if(!board.isOK().isEmpty())
                    board.remove(c);
                else {
                    piazzate.incrementAndGet();
                    String coord = "" + c.x() + c.y();
                    game.currentTile = t;
                    game.execute("piazza 2 " + coord +" " + getOri(r));
                }
            });
        }
    }

    static void placeRandom(Game game, int timeout){
        Future<?> future =  Executors.newSingleThreadExecutor().submit(() -> {
            genRandomShip(game);
        });

        try {
            future.get(timeout, TimeUnit.SECONDS);
        } catch (Exception _) {}
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        try{
            Controller.main(new String[]{"true"});
        }catch(Exception _){}

        String name = args[0];
        String conn = args[1];

        ClientInterface client = conn.equals("rmi")?
                new RMIClient(name, "localhost", 1234) :
                new SocketClient(name, "localhost", 1235, 1236);

        Game game = new Game(client);

        //new SocketClient("npc", "localhost", 1235, 1236).join(new PlaceholderCallback());

        while(game.state != Model.State.Type.BUILDING)
            Thread.sleep(100);

        placeRandom(game, 5);

        game.execute("clessidra");
    }
}
