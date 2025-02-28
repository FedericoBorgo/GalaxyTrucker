package it.polimi.softeng.is25am10.model;

import java.util.ArrayList;

public class ShipContainer<T> {
    public static final int BOARD_WIDTH = 7;
    public static final int BOARD_HEIGHT = 5;

    private ArrayList<ArrayList<T>> board;

    ShipContainer(T defaultValue){
        ArrayList<T> colum;
        board = new ArrayList<>();

        for(int i = 0; i < BOARD_WIDTH; i++) {
            colum = new ArrayList<>();
            board.add(colum);
            for(int j = 0; j < BOARD_HEIGHT; j++) {
                colum.add(defaultValue);
            }
        }
    }

    private boolean check(int x, int y){
        return x >= 0 && x < BOARD_WIDTH && y >= 0 && y < BOARD_HEIGHT;
    }

    public Result<T> set(int x, int y, T t){
        if(!check(x, y))
            return new Result<T>(false, null, "out of bound");

        board.get(x).set(y, t);
        return new Result<T>(true, t, null);
    }

    public Result<T> get(int x, int y){
        if(!check(x, y))
            return new Result<>(false, null, "out of bound");

        return new Result<>(true, board.get(x).get(y), null);
    }
}
