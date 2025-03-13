package it.polimi.softeng.is25am10.model.boards;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Coordinate {
    private final int x;
    private final int y;

    private boolean check(int x, int y){
        return x < 0 || x >= TilesBoard.BOARD_WIDTH || y < 0 || y >= TilesBoard.BOARD_HEIGHT;
    }

    public Coordinate(int x, int y) {
        if(check(x, y))
            throw new IndexOutOfBoundsException();
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public Coordinate left() throws IOException {
        if(check(x-1, y))
            throw new IOException("left coordinate out of bounds");
        return new Coordinate(x-1, y);
    }
    public Coordinate right() throws IOException{
        if(check(x+1, y))
            throw new IOException("right coordinate out of bounds");
        return new Coordinate(x+1, y);
    }

    public Coordinate up() throws IOException{
        if(check(x, y-1))
            throw new IOException("up coordinate out of bounds");
        return new Coordinate(x, y-1);
    }
    public Coordinate down() throws IOException{
        if(check(x, y+1))
            throw new IOException("down coordinate out of bounds");
        return new Coordinate(x, y+1);
    }

    static public void forEach(Consumer<Coordinate> consumer) {
        for(int i = 0; i < TilesBoard.BOARD_WIDTH; i++)
            for(int j = 0; j < TilesBoard.BOARD_HEIGHT; j++)
                consumer.accept(new Coordinate(i, j));
    }

    static public void forEachUntil(Predicate<Coordinate> predicate) {
        boolean ok = true;
        for(int i = 0; i < TilesBoard.BOARD_WIDTH && ok; i++)
            for(int j = 0; j < TilesBoard.BOARD_HEIGHT && ok; j++)
                ok = predicate.test(new Coordinate(i, j));
    }

    static public void forEachUntil(int x, Predicate<Coordinate> predicate) {
        boolean ok = true;
        if(x < 0 || x >= TilesBoard.BOARD_WIDTH)
            return;
        for(int j = 0; j < TilesBoard.BOARD_HEIGHT && ok; j++)
            ok = predicate.test(new Coordinate(x, j));
    }

    static public void forEachUntil(Predicate<Coordinate> predicate, int y) {
        boolean ok = true;
        if(y < 0 || y >= TilesBoard.BOARD_HEIGHT)
            return;
        for(int i = 0; i < TilesBoard.BOARD_WIDTH && ok; i++)
            ok = predicate.test(new Coordinate(i, y));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
