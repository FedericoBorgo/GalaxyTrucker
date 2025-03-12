package it.polimi.softeng.is25am10.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Represents the entire set of tiles used for one game.
 * Stores the tiles in 2 lists: {@code tiles} for the face-down ones,
 * {@code seen} for the face-up ones.
 */
public class TilesCollection {
    static final List<Tile> TILES;
    private final List<Tile> tiles;
    private final List<Tile> seen;

    /**
     * Creates a deck of tiles for a match and shuffles it.
     */
    TilesCollection() {
        tiles = new ArrayList<>();
        seen = new ArrayList<>();
        TILES.forEach(tile -> {
            tiles.add(new Tile(tile.getType(), tile.getConnectors()));
        });
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

    static {
        TILES = new ArrayList<>();

        Arrays.stream(Tile.Type.values()).forEach(tileType -> {
            if(tileType == Tile.Type.WALL || tileType == Tile.Type.EMPTY)
                return;

            try {
                Path resourcePath = Paths.get(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("tiles/"+tileType.name())).toURI());
                List<String> files = Files.list(resourcePath)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .map(s -> s.substring(0, 4))
                        .toList();

                for(String connectors : files)
                    TILES.add(new Tile(tileType, connectors));

            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
