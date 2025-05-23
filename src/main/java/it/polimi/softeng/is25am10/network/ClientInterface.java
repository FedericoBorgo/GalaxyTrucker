package it.polimi.softeng.is25am10.network;

import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.model.cards.CardData;
import it.polimi.softeng.is25am10.model.cards.CardInput;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class ClientInterface extends Thread{
    String name;

    protected ClientInterface(String name) {
        this.name = name;
    }

    protected abstract <T> T call(Object... args);

    public abstract Result<FlightBoard.Pawn> join(Callback callback);

    public String getPlayerName(){
        return name;
    }

    public Result<Integer> moveTimer() {
        return call(name);
    }

    public Result<String> setReady() {
        return call(name);
    }

    public Result<String> quit() {
        return call(name);
    }

    public Result<Tile> setTile(Coordinate c, Tile t, Tile.Rotation rotation) {
        return call(name, c, t, rotation);
    }

    public Result<Tile> bookTile(Tile t) {
        return call(name, t);
    }

    public Result<Tile> useBookedTile(Tile t, Tile.Rotation rotation, Coordinate c) {
        return call(name, t, rotation, c);
    }

    public Result<Tile> placeOpenTile(Tile t, Tile.Rotation rotation, Coordinate c) {
        return call(name, t, rotation, c);
    }

    public Result<String> remove(Coordinate c) {
        return call(name, c);
    }

    public ShipBoard getShip() {
        return call(name);
    }

    public ShipBoard getShip(String playerName) {
        return call(playerName);
    }

    public Result<String> init(Result<Coordinate> purple, Result<Coordinate> brown) {
        return call(name, purple, brown);
    }

    public List<GoodsBoard.Type> getReward() {
        return call(name);
    }

    public Result<Integer> placeReward(GoodsBoard.Type t, Coordinate c) {
        return call(name, t, c);
    }

    public Result<String> dropReward(){
        return call(name);
    }

    public int getCash() {
        return call(name);
    }

    public Result<Integer> drop(Coordinate c) {
        return call(name, c);
    }

    public Result<Integer> drop(Coordinate c, GoodsBoard.Type t) {
        return call(name, c, t);
    }

    public Result<String> increaseCannon(Tile.Rotation r, int count) {
        return call(name, r, count);
    }

    public Result<Tile> drawTile() {
        return call(name);
    }

    public Result<List<Tile>> getSeenTiles() {
        return call(name);
    }

    public Result<String> giveTile(Tile t) {
        return call(name, t);
    }

    public Result<Tile> getTileFromSeen(Tile t) {
        return call(name, t);
    }

    public Result<Card> drawCard() {
        return call(name);
    }

    public int getEnginePower(String name) {
        return call(name);
    }

    public double getCannonPower(String name) {
        return call(name);
    }

    public Result<CardInput> setInput(CardInput input) {
        return call(name, input);
    }

    public Result<CardData> getCardData() {
        return call(name);
    }

    public Result<Card[][]> getVisible() {
        return call(name);
    }

    public Model.Removed getRemoved(){
        return call(name);
    }

    static public Class<?>[] getClasses(Object... args) {
        return Arrays.stream(args)
                .map(Object::getClass)
                .toArray(Class<?>[]::new);
    }

    static public String getCallerName(){
        return Thread.currentThread()
                .getStackTrace()[3]
                .getMethodName();
    }
}
