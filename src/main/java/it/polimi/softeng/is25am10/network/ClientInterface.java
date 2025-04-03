package it.polimi.softeng.is25am10.network;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ClientInterface {
    String getPlayerName();

    Result<FlightBoard.Pawn> join(Callback callback);

    Result<Integer> moveTimer();

    Result<String> setReady();

    Result<String> quit();

    Result<Tile> setTile(Coordinate c, Tile t, Tile.Rotation rotation);

    Result<Tile> getTile(Coordinate c);

    Tile.Rotation getRotation(Coordinate c);

    Result<Tile> bookTile(Tile t);

    Result<Tile> useBookedTile(Tile t, Tile.Rotation rotation, Coordinate c);

    List<Tile> getBooked();

    Result<String> remove(Coordinate c);

    Set<Coordinate> checkShip();

    ShipBoard.CompressedShipBoard getShip();

    Result<String> init(Result<Coordinate> purple, Result<Coordinate> brown);

    List<GoodsBoard.Type> getReward();

    Result<Integer> placeReward(GoodsBoard.Type t, Coordinate c);

    int getCash();

    Result<Integer> drop(Coordinate c);

    Result<Integer> drop(Coordinate c, GoodsBoard.Type t);

    Result<String> setCannonsToUse(Map<Tile.Rotation, Integer> map);

    Result<Tile> drawTile();

    Result<List<Tile>> getSeenTiles();

    Result<String> giveTile(Tile t);

    Result<Tile> getTileFromSeen(Tile t);

    Result<Card.CompressedCard> drawCard();

    Result<JSONObject> setInput(JSONObject json);

    Result<JSONObject> getCardData();

    Result<Card[][]> getVisible();
}
