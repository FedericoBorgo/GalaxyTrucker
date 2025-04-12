package it.polimi.softeng.is25am10.model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Record used to store the data of a single projectile.
 *
 * @param type of projectile
 * @param side to hit
 * @param where coordinate to HIT
 * @param ID unique number
 */
public record Projectile(Type type, Tile.Side side, int where,
                         int ID) implements Serializable {

    /**
     * Convert this projectile to a String.
     *
     * @return converted String
     */
    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type.toString());
        jsonObject.put("side", side.toString());
        jsonObject.put("where", where);
        jsonObject.put("ID", ID);
        return jsonObject.toString();
    }

    /**
     * Type of asteroid.
     */
    public enum Type {
        SMALL_ASTEROID,
        BIG_ASTEROID,
        SMALL_FIRE,
        BIG_FIRE;

        public Tile.Type stoppedBy() {
            return switch (this) {
                case SMALL_ASTEROID, SMALL_FIRE -> Tile.Type.SHIELD;
                case BIG_ASTEROID -> Tile.Type.CANNON;
                case BIG_FIRE -> null;
            };
        }
    }
}


