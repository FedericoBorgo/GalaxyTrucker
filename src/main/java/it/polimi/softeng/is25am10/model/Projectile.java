package it.polimi.softeng.is25am10.model;

import org.json.JSONObject;

public class Projectile{
    private final Type type;
    private final Tile.Side side;
    private final int where;
    private final int ID;

    public Projectile(Type type, Tile.Side side, int where, int ID) {
        this.type = type;
        this.side = side;
        this.where = where;
        this.ID = ID;
    }

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

        public Tile.Type stoppedBy(){
            return switch (this){
                case SMALL_ASTEROID, SMALL_FIRE -> Tile.Type.SHIELD;
                case BIG_ASTEROID -> Tile.Type.DRILLS;
                case BIG_FIRE -> null;
            };
        }
    }

    public Type getType() {
        return type;
    }


    public Tile.Side getSide() {
        return side;
    }


    public int getWhere() {
        return where;
    }

    public int getID() {
        return ID;
    }
}


