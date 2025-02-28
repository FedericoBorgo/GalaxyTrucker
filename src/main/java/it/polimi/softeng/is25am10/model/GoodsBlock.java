package it.polimi.softeng.is25am10.model;

public enum GoodsBlock {
    GREEN, YELLOW, BLUE, RED, EMPTY;
}

/*

non mi era chiaro cosa faceva bene questa funzione: ho supposto che ritorna un valore di int
unico per ogni tipo di enum


public int getValue(){
return switch (this) {
        case GREEN -> 1;
        case YELLOW -> 2;
        case BLUE -> 3;
        case RED -> 4;
        case EMPTY -> 0;
        }
}

 */