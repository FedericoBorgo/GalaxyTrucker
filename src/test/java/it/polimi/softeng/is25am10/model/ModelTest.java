package it.polimi.softeng.is25am10.model;

import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.cards.Card;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Optional;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelTest {
    static class JSONTileEntry{
        public final Tile t;
        public final Coordinate c;
        public final Tile.Rotation r;

        public JSONTileEntry(JSONObject j) {
            t = new Tile(
                    Tile.Type.valueOf(j.getString("type")),
                    j.getString("connectors")
            );
            c = Coordinate.fromString(j.getString("coords")).getData();
            r = Tile.Rotation.valueOf(j.getString("rotation"));
        }
    }

    Model model;

    void jsonForEach(JSONArray array, BiConsumer<String, JSONObject> consumer){
        array.forEach(e -> {
            JSONObject player = (JSONObject) e;
            String name = player.getString("name");

            consumer.accept(name, player);
        });
    }

    void loadPlayers(Model model, JSONArray players){
        jsonForEach(players, (name, playerObj) -> {
            for(int i = 0; playerObj.has(""+i); i++) {
                JSONTileEntry e = new JSONTileEntry(playerObj.getJSONObject(""+i));

                if (model.setTile(name, e.c, e.t, e.r).isErr())
                    throw new IndexOutOfBoundsException("cant place at x" + e.c.x() + "y" + e.c.y());
            }

            model.setReady(name);
        });
    }

    void checkTiles(Model model, JSONArray players){
        jsonForEach(players, (name, playerObj) -> {
            for(int i = 0; playerObj.has(""+i); i++) {
                JSONTileEntry e = new JSONTileEntry(playerObj.getJSONObject(""+i));
                Result<Tile> res = model.getTile(name, e.c);
                assertTrue(res.isOk());
                assertEquals(res.getData(), e.t);
                assertEquals(model.getRotation(name, e.c), e.r);
            }
        });
    }

    void removeWrong(Model model, JSONArray players){
        jsonForEach(players, (name, playerObj) -> {
            model.remove(name, Coordinate.fromString(playerObj.getString("wrong")).getData());
        });
    }

    void initShip(Model model, JSONArray players){
        jsonForEach(players, (name, playerObj) -> {
            Optional<Coordinate> brown = playerObj.has("brown")?
                        Optional.of(Coordinate.fromString(playerObj.getString("brown")).getData()) :
                        Optional.empty();
            Optional<Coordinate> purple = playerObj.has("purple")?
                    Optional.of(Coordinate.fromString(playerObj.getString("purple")).getData()) :
                    Optional.empty();

            model.init(name, purple, brown);

            brown.ifPresent(coord -> {
                assertEquals(1, model.getShip(name).brown.get(coord));
            });

            purple.ifPresent(coord -> {
                assertEquals(1, model.getShip(name).purple.get(coord));
            });
        });
    }

    void checkInit(Model model, JSONArray player){
        jsonForEach(player, (name, playerObj) -> {

            playerObj.getJSONArray("battery").forEach(coord ->{
                assertTrue(2 <= model.getShip(name).battery.get(Coordinate.fromString(coord.toString()).getData()));
            });

            playerObj.getJSONArray("astronaut").forEach(coord ->{
                assertEquals(2, model.getShip(name).astronaut.get(Coordinate.fromString(coord.toString()).getData()));
            });
        });
    }

    @Test
    void testModel(){
        JSONObject obj = new JSONObject(Card.dump(ModelTest.class.getResourceAsStream("modelTest.json")));
        model = new Model(obj.getInt("n_players"));
        JSONArray players = obj.getJSONArray("players");

        assertEquals(Model.State.Type.JOINING, model.getStatus());

        players.forEach(player -> {
            JSONObject playerObj = (JSONObject) player;
            model.addPlayer(playerObj.getString("name"));
        });

        model.startGame();
        assertEquals(Model.State.Type.BUILDING, model.getStatus());
        loadPlayers(model, players);
        checkTiles(model, players);
        assertEquals(Model.State.Type.CHECKING, model.getStatus());
        removeWrong(model, players);
        assertEquals(Model.State.Type.ALIEN, model.getStatus());
        initShip(model, players);
        assertEquals(Model.State.Type.DRAW, model.getStatus());
        checkInit(model, players);

        //TODO cards
    }

    @Test
    void testStore() throws IOException, ClassNotFoundException {
        testModel();

        model.store("out.bin");
        Model m = Model.load("out.bin");
        System.out.println();

        assertEquals(model, m);
    }
}