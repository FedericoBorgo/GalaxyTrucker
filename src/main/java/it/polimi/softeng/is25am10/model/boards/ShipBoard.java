package it.polimi.softeng.is25am10.model.boards;


import it.polimi.softeng.is25am10.gui.CardScene;
import it.polimi.softeng.is25am10.model.Projectile;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * This class contains all the layouts of every type
 * of container in the board.
 * It contains batters, astronauts, aliens, boxes and tiles.
 */
public class ShipBoard implements Serializable {
    private final TilesBoard tiles;
    private final ElementsBoard astronaut;
    private final ElementsBoard purple;
    private final ElementsBoard brown;
    private final ElementsBoard battery;
    private final Map<GoodsBoard.Type, ElementsBoard> goods = new HashMap<>();
    private final List<ElementsBoard> boards;
    private boolean ready = false;

    public ShipBoard(){
        tiles = new TilesBoard();
        astronaut = new AstronautBoard(tiles);
        purple = new AlienBoard(tiles, AlienBoard.Type.PURPLE);
        brown = new AlienBoard(tiles, AlienBoard.Type.BROWN);
        battery = new BatteryBoard(tiles);

        Arrays.stream(GoodsBoard.Type.values())
                .forEach(type ->
                        goods.put(type, new GoodsBoard(tiles, type))
                );

        astronaut.setOthers(Arrays.asList(brown, purple));
        purple.setOthers(Arrays.asList(brown, astronaut));
        brown.setOthers(Arrays.asList(purple, astronaut));

        Arrays.stream(GoodsBoard.Type.values()).forEach(type -> {
            goods.get(type).setOthers(goods.values()
                                            .stream()
                                            .filter(b -> ((GoodsBoard)b).type != type)
                                            .toList()
                                        );
        });

        boards = new ArrayList<>(List.of(astronaut, purple, brown, battery));
        boards.addAll(goods.values());
    }

    public TilesBoard getTiles() {
        return tiles;
    }

    public ElementsBoard getAstronaut() {
        return astronaut;
    }

    public ElementsBoard getPurple() {
        return purple;
    }

    public ElementsBoard getBrown() {
        return brown;
    }

    public ElementsBoard getBattery() {
        return battery;
    }

    public ElementsBoard getGoods(GoodsBoard.Type type) {
        return goods.get(type);
    }

    /**
     * Get the total power of the cannons in base of how many
     * the players want to activate.
     *
     * @param count how many cannons the player want to activate
     * @return the power of the cannons
     */
    public double getCannonsPower(Map<Tile.Rotation, Integer> count){
        double power = tiles.countCannonsPower(count);

        if(power > 0)
            power += purple.getTotal()*2;

        return power;
    }

    /**
     * Get the total power of the engines in base of how many
     * the players want to activate.
     *
     * @param count how many engines the player want to activate
     * @return the power of the engines
     */
    public int getEnginePower(int count){
        int engines = tiles.countEnginePower(count);

        if(engines > 0){
            engines += brown.getTotal()*2;
        }

        return engines;
    }

    // there is someone at the specified coordinate?
    private boolean thereIsSomeone(Coordinate c){
        return purple.get(c) + astronaut.get(c) + brown.get(c) > 0;
    }

    // remove at least one member at the specified location
    // if there is one
    public void removeSomeone(Coordinate c){
        if(astronaut.get(c) > 0) {
            astronaut.remove(c, 1);
            return;
        }

        if(purple.get(c) > 0) {
            purple.remove(c, 1);
            return;
        }

        if(brown.get(c) > 0)
            brown.remove(c, 1);
    }

    private boolean toKill(Coordinate c){
        if(!thereIsSomeone(c))
            return false;

        boolean kill = false;

        try {
            if(thereIsSomeone(c.left()))
                return true;
        } catch (IOException _) {}

        try {
            if(thereIsSomeone(c.right()))
                return true;
        } catch (IOException _) {}

        try {
            if(thereIsSomeone(c.up()))
                return true;
        } catch (IOException _) {}

        try {
            if(thereIsSomeone(c.down()))
                return true;
        } catch (IOException _) {}

        return false;
    }

    /**
     * Cause an epidemic in the board. Used only for the epidemic card.
     *
     * @return the list of the positions of killed members
     */
    public List<Coordinate> epidemic(){
        List<Coordinate> removed = new ArrayList<>();

        Coordinate.forEach(c -> {
            if(toKill(c)) {
                removeSomeone(c);
                removed.add(c);
            }
        });

        return removed;
    }

    /**
     * Remove illegals elements inside the boards.
     */
    public void removeIllegals(){
        astronaut.removeIllegals();
        purple.removeIllegals();
        brown.removeIllegals();
        battery.removeIllegals();
        goods.forEach((_, board) -> board.removeIllegals());
    }

    /**
     * Hit the board with the Projectile.
     *
     * @param projectile that is going to hit the board.
     * @param useBattery the players want to use the battery?
     * @return the optional coordinate of the destroyed tile
     */
    public Optional<Coordinate> hit(Projectile projectile, boolean useBattery) {
        Optional<Coordinate> res = tiles.hit(projectile, useBattery);
        removeIllegals();
        return res;
    }

    /**
     * Initialize the board by filling the batteries and astronauts.
     * It also places the aliens.
     *
     * @param purple where to put the purple alien
     * @param brown where to put the brown alien
     */
    public void init(Optional<Coordinate> purple, Optional<Coordinate> brown){
        purple.ifPresent(c -> this.purple.put(c, 1));

        brown.ifPresent(c -> this.brown.put(c, 1));

        Coordinate.forEach(c -> {
            Result<Tile> res = tiles.getTile(c);

            if(res.isOk()){
                Tile tile = res.getData();

                if(Tile.house(tile))
                    astronaut.put(c, 2);
                else if(tile.getType() == Tile.Type.BATTERY_2)
                    battery.put(c, 2);
                else if(tile.getType() == Tile.Type.BATTERY_3)
                    battery.put(c, 3);
            }
        });

        ready = true;
    }

    public void init(Result<Coordinate> purple, Result<Coordinate> brown){
        Optional<Coordinate> p = purple.isOk()? Optional.of(purple.getData()): Optional.empty();
        Optional<Coordinate> b = brown.isOk()? Optional.of(brown.getData()): Optional.empty();

        init(p, b);
    }

    public boolean isReady(){
        return ready;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShipBoard shipBoard = (ShipBoard) o;
        return Objects.equals(tiles, shipBoard.tiles) && Objects.equals(astronaut, shipBoard.astronaut) && Objects.equals(purple, shipBoard.purple) && Objects.equals(brown, shipBoard.brown) && Objects.equals(battery, shipBoard.battery) && Objects.equals(goods, shipBoard.goods);
    }

    public List<ElementsBoard> boards(){
        return boards;
    }

    public int getTotalGoods(){
        return goods.values().stream().map(ElementsBoard::getTotal).reduce(0, Integer::sum);
    }
}
