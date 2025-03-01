package it.polimi.softeng.is25am10.model;

public enum GoodsBlock {
    GREEN, YELLOW, BLUE, RED, EMPTY;

    public int getValue(){
        return switch (this) {
            case GREEN -> 2;
            case YELLOW -> 3;
            case BLUE -> 1;
            case RED -> 4;
            case EMPTY -> 0;
            default -> 0;
        };
    }
}