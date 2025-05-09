package it.polimi.softeng.is25am10.tui.asciiui;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.TilesCollection;
import it.polimi.softeng.is25am10.model.boards.AlienBoard;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.TilesBoard;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class AutoBuilder {
    static Random rmd = new Random();

    static TilesCollection collection = new TilesCollection();

    public static Tile genRandomTile() {
        Tile t = collection.getNew();
        if(t == null){
            collection = new TilesCollection();
            t = collection.getNew();
        }
        return t;
    }

    public static Tile.Rotation genRandomOri(){
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

    public static Coordinate genRandomCoord(){
        return new Coordinate(rmd.nextInt(0, TilesBoard.BOARD_WIDTH), rmd.nextInt(0, TilesBoard.BOARD_HEIGHT));
    }

    static void genRandomShip(Game game) {
        TilesBoard board = new TilesBoard();

        while(true){
            Coordinate c = genRandomCoord();
            Tile t = genRandomTile();

            Tile.Rotation r = Tile.engine(t) ? Tile.Rotation.NONE : genRandomOri();

            board.setTile(c, t, r).ifPresent(_ -> {
                if(!board.isOK().isEmpty())
                    board.remove(c);
                else {
                    String coord = "" + c.x() + c.y();
                    game.currentTile = t;
                    game.execute("piazza 2 " + coord +" " + getOri(r));
                }
            });
        }
    }

    static void placeAlien(Game game){
        AtomicReference<Optional<Coordinate>> purple = new AtomicReference<>(Optional.empty());
        AtomicReference<Optional<Coordinate>> brown = new AtomicReference<>(Optional.empty());

        TilesBoard board = game.board.getTiles();

        Coordinate.forEach(c -> {
            Result<Tile> res = board.getTile(c);

            if(res.isErr())
                return;

            Tile t = res.getData();

            if(t.getType() == Tile.Type.HOUSE){
                if(AlienBoard.thereIsAddon(c, board, Tile.Type.P_ADDON) && purple.get().isEmpty())
                    purple.set(Optional.of(c));

                if(AlienBoard.thereIsAddon(c, board, Tile.Type.B_ADDON) && brown.get().isEmpty())
                    brown.set(Optional.of(c));
            }
        });
        StringBuilder cmd = new StringBuilder();
        cmd.append("alieni");

        brown.get().ifPresent(c -> cmd.append(" m").append(c.x()).append(c.y()));

        purple.get().ifPresent(c -> cmd.append(" v").append(c.x()).append(c.y()));

        if(purple.get().isEmpty() && brown.get().isEmpty())
            cmd.append(" no");

        game.execute(cmd.toString());
    }

    static void placeRandom(Game game, int timeout){
        Future<?> future =  Executors.newSingleThreadExecutor().submit(() -> genRandomShip(game));

        try {
            future.get(timeout, TimeUnit.SECONDS);
        } catch (Exception _) {}
    }

    public static void initGame(Game game){
        while(game.state != Model.State.Type.BUILDING) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        placeRandom(game, 5);

        //game.execute("clessidra");
        //game.execute("clessidra");

        while(game.state != Model.State.Type.ALIEN_INPUT) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        placeAlien(game);

        while(game.state == Model.State.Type.ALIEN_INPUT) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        while(game.state == Model.State.Type.WAITING_INPUT) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
