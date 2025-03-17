package it.polimi.softeng.is25am10.model;

import org.json.JSONObject;

import java.io.Serializable;

public record Projectile(it.polimi.softeng.is25am10.model.Projectile.Type type, Tile.Side side, int where,
                         int ID) implements Serializable {

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type.toString());
        jsonObject.put("side", side.toString());
        jsonObject.put("where", where);
        jsonObject.put("ID", ID);
        return jsonObject.toString();
    }

    static Projectile fromString(String json) {
        JSONObject jsonObject = new JSONObject(json);
        Type type = Type.valueOf(jsonObject.getString("type"));
        Tile.Side side = Tile.Side.valueOf(jsonObject.getString("side"));
        int where = jsonObject.getInt("where");
        int ID = jsonObject.getInt("ID");
        return new Projectile(type, side, where, ID);
    }

    public enum Type {
        SMALL_ASTEROID,
        BIG_ASTEROID,
        SMALL_FIRE,
        BIG_FIRE;

        public Tile.Type stoppedBy() {
            return switch (this) {
                case SMALL_ASTEROID, SMALL_FIRE -> Tile.Type.SHIELD;
                case BIG_ASTEROID -> Tile.Type.DRILLS;
                case BIG_FIRE -> null;
            };
        }
    }
}


