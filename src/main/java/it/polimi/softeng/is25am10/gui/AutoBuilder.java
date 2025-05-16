package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.*;
import it.polimi.softeng.is25am10.model.boards.AlienBoard;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.TilesBoard;

import java.util.concurrent.atomic.AtomicReference;

import static it.polimi.softeng.is25am10.tui.asciiui.AutoBuilder.*;

/**
 * This class is used to generate a ship automatically by placing random tiles on the board in the gui.
 * It also places the aliens on the board. Its purpose is to avoid the preparatory phase of the game
 * when the game has to be tested or displayed in a demo.
 */
public class AutoBuilder extends Thread{
    Building building;


    public AutoBuilder(Building building) {
        super();
        this.building = building;
        start();
    }

    /**
     * This method is used to automatically generate a random ship with aliens.
     */
    @Override
    public void run() {
        //works only in the building state
        while(building.state != it.polimi.softeng.is25am10.model.State.Type.BUILDING) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        TilesBoard board = building.ship.getTiles();
        long start = System.currentTimeMillis();

        //places the tiles on the shipboard automatically and randomly
        while(System.currentTimeMillis()-start < 5000){
            Coordinate c = genRandomCoord();
            Tile t = genRandomTile();
            Tile.Rotation r = Tile.engine(t) ? Tile.Rotation.NONE : genRandomOri();

            board.setTile(c, t, r).ifPresent(_ -> {
                if(!board.isOK().isEmpty())
                    board.remove(c);
                else
                    building.server.setTile(c, t, r);
            });
        }

        building.server.setReady();

        while(building.state != it.polimi.softeng.is25am10.model.State.Type.ALIEN_INPUT) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //here the aliens are placed
        AtomicReference<Result<Coordinate>> purple = new AtomicReference<>(Result.err());
        AtomicReference<Result<Coordinate>> brown = new AtomicReference<>(Result.err());

        Coordinate.forEach(c -> {
            Result<Tile> res = board.getTile(c);

            if(res.isErr())
                return;

            Tile t = res.getData();

            if(t.getType() == Tile.Type.HOUSE){
                if(AlienBoard.thereIsAddon(c, board, Tile.Type.P_ADDON) && purple.get().isErr())
                    purple.set(Result.ok(c));

                if(AlienBoard.thereIsAddon(c, board, Tile.Type.B_ADDON) && brown.get().isErr())
                    brown.set(Result.ok(c));
            }
        });

        building.ship.init(purple.get(), brown.get());
        building.server.init(purple.get(), brown.get());
    }
}
