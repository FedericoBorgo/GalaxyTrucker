package it.polimi.softeng.is25am10.network;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardInput;

import java.util.List;
import java.util.Map;

public interface ClientInterface {
    String getPlayerName();

    Result<FlightBoard.Pawn> join(Callback callback);

    Result<Integer> moveTimer();

    Result<String> setReady();

    Result<String> quit();

    Result<Tile> setTile(Coordinate c, Tile t, Tile.Rotation rotation);

    Result<Tile> bookTile(Tile t);

    Result<Tile> useBookedTile(Tile t, Tile.Rotation rotation, Coordinate c);

    Result<String> remove(Coordinate c);

    ShipBoard getShip();

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

    Result<Card> drawCard();

    Result<CardInput> setInput(CardInput input);

    Result<CardData> getCardData();

    Result<Card[][]> getVisible();
}
