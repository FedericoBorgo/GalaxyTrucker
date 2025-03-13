package it.polimi.softeng.is25am10.model;

public enum Projectile {
    SMALL_ASTEROID,
    BIG_ASTEROID,
    SMALL_FIRE,
    BIG_FIRE;

    public Tile.Type stoppedBy(){
        return switch (this){
            case SMALL_ASTEROID, SMALL_FIRE -> Tile.Type.SHIELD;
            case BIG_ASTEROID -> Tile.Type.DRILLS;
            case BIG_FIRE -> null;
        };
    }
}
