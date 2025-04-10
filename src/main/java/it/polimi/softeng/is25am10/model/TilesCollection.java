package it.polimi.softeng.is25am10.model;

import it.polimi.softeng.is25am10.model.cards.Card;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the entire set of tiles used for one game.
 * Stores the tiles in 2 lists: {@code tiles} for the face-down ones,
 * {@code seen} for the face-up ones.
 */
public class TilesCollection implements Serializable {
    private final List<Tile> tiles;
    private final List<Tile> seen;

    /**
     * Creates a deck of tiles for a match and shuffles it.
     */
    public TilesCollection() {
        tiles = new ArrayList<>();
        seen = new ArrayList<>();

        String out = Card.dump(Objects.requireNonNull(TilesCollection.class.getResourceAsStream("tiles.json")));
        JSONObject object = new JSONObject(out);

        for (Tile.Type type : Tile.Type.values()) {
            if(type == Tile.Type.EMPTY || type == Tile.Type.WALL || type == Tile.Type.C_HOUSE)
                continue;

            JSONArray array = object.getJSONArray(type.name());

            array.forEach(e -> tiles.add(new Tile(type, e.toString())));
        }

        Collections.shuffle(tiles);
    }

    /**
     * Retrieves a new tile from the face-down tiles by taking the first one
     * in the {@code tiles} list and removing it from the list.
     * Checks if the list is empty and returns an {@code EMPTY_TILE} in that case.
     * @return tile at the head of the list or {@code EMPTY_TILE}
     */
    public Tile getNew(){
        if(tiles.isEmpty())
            return Tile.EMPTY_TILE;

        Tile tile = tiles.getFirst();
        tiles.removeFirst();

        return tile;
    }

    /**
     * Retrieves the list containing all the face-up tiles.
     * @return The seen list.
     */
    public List<Tile> getSeen() {
        return seen;
    }

    /**
     * Retrieves a single tile from the face-up tiles, removing it from the face-up tiles.
     * Uses remove method from the interface Collections.
     *
     * @param tile the tile to be retrieved from the seen list.
     * @return parameter {@code tile} or {@code EMPTY_LIST}.
     */
    public Tile getFromSeen(Tile tile){
        if(!seen.remove(tile))
            return Tile.EMPTY_TILE;

        return tile;
    }

    /**
     * Adds a tile to the face-up tiles.
     * @param tile the tile to be added to the seen list.
     */
    public void give(Tile tile){
        seen.add(tile);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TilesCollection that = (TilesCollection) o;
        return Objects.equals(tiles, that.tiles) && Objects.equals(seen, that.seen);
    }
}
