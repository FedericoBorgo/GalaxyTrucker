package it.polimi.softeng.is25am10;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import org.json.JSONObject;

import java.util.*;

public interface PlayerControls {
    Result<Integer> moveTimer(String name);

    Result<String> setReady(String name);

    Result<String> quit(String name);

    Result<Tile> setTile(String name, Coordinate c, Tile t, Tile.Rotation rotation);

    Result<Tile> getTile(String name, Coordinate c);

    Tile.Rotation getRotation(String name, Coordinate c);

    Result<Tile> bookTile(String name, Tile t);

    Result<Tile> useBookedTile(String name, Tile t, Tile.Rotation rotation, Coordinate c);

    List<Tile> getBooked(String name);

    Result<String> remove(String name, Coordinate c);

    Set<Coordinate> checkShip(String name);

    ShipBoard.CompressedShipBoard getShip(String name);

    Result<String> init(String name, Optional<Coordinate> purple, Optional<Coordinate> brown);

    List<GoodsBoard.Type> getReward(String name);

    Result<Integer> placeReward(String name, GoodsBoard.Type t, Coordinate c);

    int getCash(String name);

    Result<Integer> drop(String name, Coordinate c);

    Result<Integer> drop(String name, Coordinate c, GoodsBoard.Type t);

    Result<String> setCannonsToUse(String name, Map<Tile.Rotation, Integer> map);

    Result<Tile> drawTile(String name);

    Result<List<Tile>> getSeenTiles(String name);

    Result<String> giveTile(String name, Tile t);

    Result<Tile> getTileFromSeen(String name, Tile t);

    Result<Card.CompressedCard> drawCard(String name);

    Result<JSONObject> setInput(String name, JSONObject json);

    Result<JSONObject> getCardData(String name);

    Result<Card[][]> getVisible(String name);
}
