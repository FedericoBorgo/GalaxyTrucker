package it.polimi.softeng.is25am10.model;

import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.cards.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

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
                //Result<Tile> res = model.getTile(name, e.c);
                //assertTrue(res.isOk());
                //assertEquals(res.getData(), e.t);
                //assertEquals(model.getRotation(name, e.c), e.r);
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
                assertEquals(1, model.ship(name).getBrown().get(coord));
            });

            purple.ifPresent(coord -> {
                assertEquals(1, model.ship(name).getPurple().get(coord));
            });
        });
    }

    void checkInit(Model model, JSONArray player){
        jsonForEach(player, (name, playerObj) -> {

            playerObj.getJSONArray("battery").forEach(coord ->{
                assertTrue(2 <= model.ship(name).getBattery().get(Coordinate.fromString(coord.toString()).getData()));
            });

            playerObj.getJSONArray("astronaut").forEach(coord ->{
                assertEquals(2, model.ship(name).getAstronaut().get(Coordinate.fromString(coord.toString()).getData()));
            });
        });
    }

    void loadCards(Model model){
        List<Card> cards = new ArrayList<>();

        cards.add(Epidemic.construct(model.debug_getFlightBoard()).getFirst());
        cards.add(Meteors.construct(model, model.debug_getFlightBoard()).getFirst());
        cards.add(Planets.construct(model.debug_getFlightBoard()).getFirst());
        cards.add(AbandonedShip.construct(model, model.debug_getFlightBoard()).getFirst());
        cards.add(Space.construct(model, model.debug_getFlightBoard()).getFirst());
        cards.add(Stardust.construct(model.debug_getFlightBoard()).getFirst());
        cards.add(Station.construct(model.debug_getFlightBoard()).getFirst());
        cards.add(Pirates.construct(model,model.debug_getFlightBoard()).getFirst());
        cards.add(Slavers.construct(model,model.debug_getFlightBoard()).getFirst());
        cards.add(Smugglers.construct(model,model.debug_getFlightBoard()).getFirst());
        cards.add(Warzone.construct(model,model.debug_getFlightBoard()).getFirst());

        model.debug_setCards(cards);
    }

    @Test
    void testModel(){
        JSONObject obj = new JSONObject(Card.dump(ModelTest.class.getResourceAsStream("modelTest.json")));
        model = new Model(obj.getInt("n_players"), new BiConsumer<Model, State.Type>() {
            @Override
            public void accept(Model model, State.Type type) {

            }
        });
        JSONArray players = obj.getJSONArray("players");

        assertEquals(State.Type.JOINING, model.getState());

        players.forEach(player -> {
            JSONObject playerObj = (JSONObject) player;
            model.addPlayer(playerObj.getString("name"));
        });

        assertEquals(State.Type.BUILDING, model.getState());
        assertEquals("clessidra non ancora esaurita", model.moveTimer().getReason());
        model.getSeenTiles();
        model.drawTile("player1");
        model.giveTile(new Tile(Tile.Type.PIPES, "ssss"));
        model.getTileFromSeen(new Tile(Tile.Type.PIPES, "ssss"));
        loadPlayers(model, players);
        checkTiles(model, players);
        assertEquals(State.Type.CHECKING, model.getState());
        removeWrong(model, players);
        assertEquals(State.Type.ALIEN_INPUT, model.getState());
        initShip(model, players);
        assertEquals(State.Type.DRAW_CARD, model.getState());
        checkInit(model, players);

        loadCards(model);

        Result<Card> res = model.drawCard("player2");
        assertTrue(res.isErr());
        assertNull(model.getNextToPlay());
        res = model.drawCard("player1");
        assertTrue(res.isOk());
        assertEquals("player1", model.getNextToPlay());
        model.getSecondsLeft();
        model.getFlight();
        model.pause();
        model.resume();
        model.getVisible();
        model.getPlayers();
        model.getCardData();
        model.getChanges();
        model.computeCash();
        model.quit("player1");
        model.getQuit();
        model.ignoreCheck("player2");
        model.removeIgnore("player2");
        model.getReward("player2");
        model.dropReward("player2");
        model.getCash("player2");
        model.getEnginePower("player2");
        model.getCannonPower("player2");
        model.getRemoved("player2");
        model.increaseCannon("player2", Tile.Rotation.CLOCK, 1);
        model.getCannonsToUse(model.get("player2"));
        model.getCannonsToUse("player2");
        model.batteryForCannon("player2");
    }

    /*void testStore() throws IOException, ClassNotFoundException {
        testModel();

        model.store("out.bin");
        Model m = Model.load("out.bin", new BiConsumer<Model, Model.State.Type>() {
            @Override
            public void accept(Model model, Model.State.Type type) {

            }
        });
        File file = new File("out.bin");
        file.delete();
    }*/
}