package it.polimi.softeng.is25am10.tui.asciiui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import it.polimi.softeng.is25am10.model.*;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;
import it.polimi.softeng.is25am10.model.boards.TilesBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.model.cards.CardData;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FrameGenerator {
    private final JSONObject textures;
    private final Game game;
    private final Screen screen;
    private final TextGraphics graphics;
    private boolean pauseRender = false;
    private boolean isOkCommand = false;

    ArrayList<Character> command = new ArrayList<>();
    int cursor = 0;

    String response = "";
    private boolean cleared = false;

    public FrameGenerator(Game game) throws IOException {
        screen = new TerminalScreen(new DefaultTerminalFactory().createTerminal());
        screen.startScreen();
        graphics = screen.newTextGraphics();
        graphics.enableModifiers(SGR.BOLD);

        textures = new JSONObject(Card.dump(Objects.requireNonNull(Game.class.getResourceAsStream("textures.json"))));

        this.game = game;

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(!pauseRender)
                    handleKeyBoard();
            }
        }, 0, 100/6);

        initBoard();
    }

    private void refresh(){
        try {
            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void initBoard(){
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(new TerminalPosition(1, 0), "x");
        graphics.putString(new TerminalPosition(0, 1), "y");

        String[] boardBorder = Card.dump(Objects.requireNonNull(Game.class.getResourceAsStream("board.txt"))).split("\n");

        for(int i = 0; i < 7; i++)
            graphics.putString(new TerminalPosition(i*16+2, 0), Integer.toString(i+4));

        for(int i = 0; i < 5; i++)
            graphics.putString(new TerminalPosition(0, i*6+2), Integer.toString(i+5));

        graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        for(int i = 0; i < boardBorder.length; i++)
            graphics.putString(new TerminalPosition(1, i+1), boardBorder[i]);

        drawTile(new Coordinate(3, 2), new Tile(Tile.Type.C_HOUSE, "uuuu"), Tile.Rotation.NONE);
        drawFlight();
        drawCash(0);
    }

    private TerminalPosition coordToTerminalPosition(Coordinate coord){
        return new TerminalPosition(coord.x()*16+2, coord.y()*6+2);
    }

    public synchronized void drawBooked(){
        clearTile(new TerminalPosition(120, 2));
        clearTile(new TerminalPosition(120, 8));
        clearTile(new TerminalPosition(140, 2));
        List<Tile> booked = game.board.getTiles().getBooked();

        if(!booked.isEmpty())
            drawTile(new TerminalPosition(120, 2),
                    booked.getFirst(), Tile.Rotation.NONE);

        if(booked.size() > 1)
            drawTile(new TerminalPosition(120, 8),
                    booked.getLast(), Tile.Rotation.NONE);
        refresh();
    }

    public synchronized void drawTile(Coordinate c, Tile t, Tile.Rotation r){
        drawTile(coordToTerminalPosition(c), t, r);
        drawErrors(game.board.getTiles().isOK());
    }

    public synchronized void drawTile(TerminalPosition pos, Tile t, Tile.Rotation rotation){
        // get the texture of the corresponding tile
        JSONObject texture = textures.getJSONObject(t.getType().name());

        // if a texture does not have rotation, it means that
        // the tile can not be printed to the screen (es: WALL, EMPTY)
        if(!texture.has("rotation"))
            return;

        graphics.setForegroundColor(TextColor.ANSI.valueOf(texture.getString("color")));

        if(texture.getBoolean("rotation"))
            texture = texture.getJSONObject(rotation.name());

        int xOffset = texture.getInt("x");
        int yOffset = texture.getInt("y");

        // draw the texture
        for(int i = 0; texture.has(String.valueOf(i)); i++)
            graphics.putString(pos.plus(new TerminalPosition(xOffset, yOffset + i)), texture.getString(String.valueOf(i)));


        //draw the connectors of the tile
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        for (Tile.Side side : Tile.Side.values()) {
            Tile.ConnectorType type = Tile.getSide(t, rotation, side);
            texture = textures.getJSONObject("connectors");
            JSONObject offset = texture.getJSONObject(side.name());

            xOffset = offset.getInt("x");
            yOffset = offset.getInt("y");

            texture = texture.getJSONObject(type.name()).getJSONObject(side.name());

            for(int i = 0; texture.has(String.valueOf(i)); i++)
                graphics.putString(pos.plus(new TerminalPosition(xOffset, yOffset + i)), texture.getString(String.valueOf(i)));
        }

        refresh();
    }

    public synchronized void clearTile(Coordinate coord){
        clearTile(coordToTerminalPosition(coord));
        drawErrors(game.board.getTiles().isOK());
    }

    public synchronized void destroyed(Coordinate coord){
        clearTile(coord);

        graphics.setForegroundColor(TextColor.ANSI.RED);
        graphics.putString(coordToTerminalPosition(coord).plus(new TerminalPosition(6, 2)), "D");
        refresh();
    }

    public synchronized void clearTile(TerminalPosition pos){
        graphics.fillRectangle(pos, new TerminalSize(15, 5), ' ');
        refresh();
    }

    public void drawErrors(Set<Coordinate> errors){
        graphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);

        graphics.putString(coordToTerminalPosition(new Coordinate(0, 0)), "              ");

        Coordinate.forEach(c -> {
            graphics.putString(coordToTerminalPosition(c).plus(new TerminalPosition(2, 1)), "   ");
        });

        if(errors.contains(new Coordinate(0, 0))) {
            graphics.putString(coordToTerminalPosition(new Coordinate(0, 0)), "NAVE SEPARATA");
            return;
        }

        errors.forEach(c -> {
            TerminalPosition pos = coordToTerminalPosition(c);
            graphics.putString(pos.plus(new TerminalPosition(2, 1)), "FIX");
        });

        refresh();
    }

    public void drawElements(){
        Map<Coordinate, Integer> offset = new HashMap<>();

        Coordinate.forEach(c -> {
            Result<Tile> res = game.board.getTiles().getTile(c);

            if(res.isErr())
                return;

            Tile t = res.getData();

            if(!(Tile.box(t) || Tile.house(t) || Tile.battery(t)))
                return;

            TerminalPosition pos = coordToTerminalPosition(c);
            graphics.putString(pos.plus(new TerminalPosition(7, 1)), " ");
            graphics.putString(pos.plus(new TerminalPosition(7, 2)), " ");
            graphics.putString(pos.plus(new TerminalPosition(7, 3)), " ");
        });

        game.board.boards().forEach(b ->{
            graphics.setForegroundColor(b.getColor());

            b.getPositions().forEach((c, tot) -> {
                TerminalPosition pos = coordToTerminalPosition(c);
                int plus = offset.getOrDefault(c, 0);

                for(int i = plus; i < tot+plus; i++)
                    graphics.putString(pos.plus(new TerminalPosition(7, 1+i)), "X");

                offset.put(c, tot+plus);
            });
        });

        refresh();
    }
    public void drawFlight(){
        List<FlightBoard.Pawn> order = game.flight.getOrder();
        int leader = game.flight.getLeaderPosition();
        List<Integer> offset = game.flight.getOffset();

        for(int i = 0; i < 24; i ++){
            String pos = textures.getJSONObject("FLIGHT")
                    .getString(String.valueOf(i));

            int x = Integer.parseInt(pos.substring(0, pos.indexOf(' ')));
            int y = Integer.parseInt(pos.substring(pos.indexOf(' ')+1));

            TerminalPosition screenPos = new TerminalPosition(120 + y, 17 + x);

            graphics.putString(screenPos," ");
        }

        for(int i = 0; i < order.size(); i++){
            int a = leader + offset.get(i);
            if(a < 0)
                a = 24 + a;
            String pos = textures.getJSONObject("FLIGHT")
                    .getString(String.valueOf(a));

            int x = Integer.parseInt(pos.substring(0, pos.indexOf(' ')));
            int y = Integer.parseInt(pos.substring(pos.indexOf(' ')+1));

            TerminalPosition screenPos = new TerminalPosition(120 + y, 17 + x);

            graphics.setForegroundColor(order.get(i).getColor());
            graphics.putString(screenPos, "X");
        }

        graphics.drawRectangle(new TerminalPosition(122, 25),
                new TerminalSize(8, 4), ' ');
        graphics.drawRectangle(new TerminalPosition(132, 25),
                new TerminalSize(8, 4), ' ');

        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.drawRectangle(new TerminalPosition(122+ game.flight.getTimer()*10, 25),
                new TerminalSize(8, 4), '#');

        refresh();
    }

    public synchronized void drawSecondsLeft(int secondsLeft){
        graphics.putString(new TerminalPosition(122, 30), "                     ");
        graphics.putString(new TerminalPosition(122+ game.flight.getTimer()*10, 30), String.valueOf(secondsLeft));
        refresh();
    }

    public synchronized void drawPlayersName(Map<String, FlightBoard.Pawn> players, Set<String> quit, Set<String> dis) {
        AtomicInteger pos = new AtomicInteger(34);
        try{
            players.forEach((name, pawn) -> {
                graphics.setForegroundColor(pawn.getColor());
                graphics.putString(new TerminalPosition(0, pos.get()), "#" + name +
                        (name.equals(game.server.getPlayerName())? "*" : "") +
                        (quit.contains(name)? " fuori " : "") +
                        (dis.contains(name)? " (disc) " : "             "));
                pos.getAndIncrement();
            });

            screen.refresh();
        }catch (Exception _){}
    }

    public synchronized void drawState(){
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(new TerminalPosition(93, 2), "                       ");
        graphics.putString(new TerminalPosition(93, 2), game.state.getName());
        refresh();
    }

    public synchronized void drawOpenTiles(){
        TerminalPosition start = new TerminalPosition(162, 2);

        for(int i = 0; i < 30; i++)
            clearTile(start.plus(new TerminalPosition(
                    (i/7)*16,
                    (i%7)*6)));

        for (int i = 0; i < game.openTiles.size(); i++) {
            drawTile(start.plus(new TerminalPosition(
                    (i/7)*16,
                    (i%7)*6
            )), game.openTiles.get(i), Tile.Rotation.NONE);
        }

        refresh();
    }

    public void drawTilesBoar(TilesBoard board){
        Coordinate.forEach(c -> {
            Result<Tile> res = board.getTile(c);

            if(res.isErr())
                return;

            Tile t = res.getData();

            drawTile(c, t, board.getRotation(c));
        });

        refresh();
    }

    public synchronized void clearUnusedSpace(){
        if(cleared)
            return;
        cleared = true;
        graphics.fillRectangle(new TerminalPosition(117, 1), new TerminalSize(109, 16), ' ');
        graphics.fillRectangle(new TerminalPosition(161, 17), new TerminalSize(65, 43), ' ');
        refresh();
    }

    synchronized void drawInput(){
        graphics.putString(new TerminalPosition(29, 34), "                                ");
        graphics.setForegroundColor(isOkCommand? TextColor.ANSI.WHITE_BRIGHT : TextColor.ANSI.RED_BRIGHT);

        for(int i = 0; i < command.size(); i++){
            graphics.putString(new TerminalPosition(29+i, 34), ""+command.get(i));
        }

        screen.setCursorPosition(new TerminalPosition(29+ cursor, 34));

        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(new TerminalPosition(29, 36), response+ "                                 ");

        refresh();
    }
    synchronized void drawCurrentTile(){
        clearTile(new TerminalPosition(140, 2));

        if(game.currentTile == null)
            return;

        drawTile(new TerminalPosition(140, 2),
                game.currentTile, Tile.Rotation.NONE);

        refresh();
    }


    private String toStringCommand(){
        StringBuilder builder = new StringBuilder();
        for (Character character : command) builder.append(character);
        return builder.toString();
    }

    /**
     * Handles the user keyboard input.
     */
    private void handleKeyBoard(){
        try {
            KeyStroke key = screen.pollInput();

            if(key != null){
                if(key.getKeyType() == KeyType.Delete && cursor < command.size())
                    command.remove(cursor);
                else if(key.getKeyType() == KeyType.Backspace && cursor > 0){
                    command.remove(cursor-1);
                    cursor--;
                }
                else if(key.getKeyType() == KeyType.ArrowLeft && cursor > 0)
                    cursor--;
                else if(key.getKeyType() == KeyType.ArrowRight && cursor < command.size())
                    cursor++;
                else if(key.getKeyType() == KeyType.Character) {
                    command.add(cursor++, key.getCharacter());
                    isOkCommand = game.checkCommand(toStringCommand());
                }
                else if(key.getKeyType() == KeyType.Enter) {
                    String cmd = toStringCommand();

                    if(isOkCommand) {
                        response = game.execute(cmd);
                        command.clear();
                        cursor = 0;
                    }
                }

                drawInput();
            }
        } catch (IOException _) {}
    }

    int askHowManyPlayer(){
        pauseRender = true;
        WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        Window window = new BasicWindow("Numero Giocatori");
        Panel panel = new Panel(new LinearLayout());
        IntegerBox box = new IntegerBox(2);
        Button ok = new Button("Ok", () -> {
            if(box.getInt() > 4 || box.getInt() < 0)
                MessageDialog.showMessageDialog(textGUI, "Error", "Numero giocatori non valido (massimo 4)", MessageDialogButton.OK);
            else
                window.close();
        });
        textGUI.setTheme(new SimpleTheme(TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK));

        panel.addComponent(box);
        panel.addComponent(ok);

        window.setComponent(panel);
        window.setHints(Collections.singletonList(Window.Hint.CENTERED));

        textGUI.addWindow(window);
        textGUI.waitForWindowToClose(window);
        screen.clear();
        pauseRender = false;

        screen.clear();
        initBoard();
        try {
            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return box.getInt();
    }

    public void clearCardData(){
        for (int i = 0; i < 10; i++)
            graphics.putString(new TerminalPosition(140, 3+i), "                                             ");
        refresh();
    }


    public void drawCardData(CardData data){
        graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        graphics.putString(new TerminalPosition(140, 2), "DATI CARTA");
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);

        clearCardData();

        String[] str = data.toString().split("\n");

        for (int i = 0; i < str.length; i++)
            graphics.putString(new TerminalPosition(140, 3+i), str[i]);

        refresh();
    }

    public void drawDroppedItems(Model.Removed rm){
        graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        graphics.putString(new TerminalPosition(120, 2), "RIMOSSI");
        graphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
        graphics.putString(new TerminalPosition(120, 3), "batterie: " + rm.battery + "  ");
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(new TerminalPosition(120, 4), "membri: " + rm.guys + "  ");
        graphics.setForegroundColor(TextColor.ANSI.BLUE_BRIGHT);
        graphics.putString(new TerminalPosition(120, 5), "scatole: " + rm.goods + "  ");

        refresh();
    }

    public void drawCannonsToUse(Map<Tile.Rotation, Integer> cannons){
        graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        graphics.putString(new TerminalPosition(120, 7), "CANNONI DA USARE");
        graphics.setForegroundColor(TextColor.ANSI.MAGENTA_BRIGHT);
        graphics.putString(new TerminalPosition(120, 8), "sorpa: " + cannons.get(Tile.Rotation.NONE) + "  ");
        graphics.putString(new TerminalPosition(120, 9),"destra: " + cannons.get(Tile.Rotation.CLOCK) + "  ");
        graphics.putString(new TerminalPosition(120, 10), "sotto: " + cannons.get(Tile.Rotation.DOUBLE) + "  ");
        graphics.putString(new TerminalPosition(120, 11), "sinistra: " + cannons.get(Tile.Rotation.INV) + "  ");

        refresh();
    }


    public synchronized void drawCash(int cash){
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(new TerminalPosition(93, 4), ""+ cash);
        refresh();
    }

    public synchronized void drawWaitFor(String name, FlightBoard.Pawn pawn){
        graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        graphics.putString(new TerminalPosition(120, 13), "TURNO DI");
        graphics.setForegroundColor(pawn.getColor());
        graphics.putString(new TerminalPosition(120, 14), name + "    ");
        refresh();
    }

    public synchronized void drawProjectile(List<Projectile> proj){
        if(proj == null)
            return;

        proj.forEach(p -> {
            graphics.setForegroundColor(p.getColor());

            String type = switch(p.type()){
                case SMALL_ASTEROID, SMALL_FIRE -> "p ";
                case BIG_ASTEROID, BIG_FIRE -> "g ";
            };

            type += p.ID();

            if(p.side() == Tile.Side.UP || p.side() == Tile.Side.DOWN) {
                if (p.where() < 4 || p.where() > 10)
                    return;

                Coordinate c = new Coordinate(p.where()-4, p.side() == Tile.Side.UP? 0 : 4);
                TerminalPosition pos = coordToTerminalPosition(c);

                pos = pos.plus(new TerminalPosition(2, p.side() == Tile.Side.UP ? -2: 6));

                graphics.putString(pos, type);
            }
            else {
                if (p.where() < 5 || p.where() > 9)
                    return;


                Coordinate c = new Coordinate(p.side() == Tile.Side.LEFT? 0 : 6, p.where()-5);
                TerminalPosition pos = coordToTerminalPosition(c);

                pos = pos.plus(new TerminalPosition(p.side() == Tile.Side.LEFT ? -2: 16, 1));

                for (int i = 0; i < type.length(); i++)
                    graphics.putString(pos.plus(new TerminalPosition(0, i)), "" + type.charAt(i));
            }
        });

        refresh();
    }

    public synchronized void clearProjectile(){
        for (int i = 0; i < TilesBoard.BOARD_WIDTH; i++) {
            TerminalPosition pos = coordToTerminalPosition(new Coordinate(i, 0)).plus(new TerminalPosition(2, -2));
            graphics.putString(pos, "    ");
            pos = coordToTerminalPosition(new Coordinate(i, TilesBoard.BOARD_HEIGHT-1)).plus(new TerminalPosition(2, 6));
            graphics.putString(pos, "    ");
        }

        for(int i = 0; i < TilesBoard.BOARD_HEIGHT; i++){
            TerminalPosition pos = coordToTerminalPosition(new Coordinate(0, i)).plus(new TerminalPosition(-2, 1));
            TerminalPosition pos2 = coordToTerminalPosition(new Coordinate(TilesBoard.BOARD_WIDTH-1, i)).plus(new TerminalPosition(16, 1));


            for (int j = 0; j < 3; j++) {
                graphics.putString(pos.plus(new TerminalPosition(0, j)), " ");
                graphics.putString(pos2.plus(new TerminalPosition(0, j)), " ");
            }
        }

        refresh();
    }

    public synchronized void drawGoods(List<GoodsBoard.Type> reward){
        graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        graphics.putString(new TerminalPosition(161, 17), "SCATOLE");

        for (int i = 0; i < 6; i++) {
            graphics.putString(new TerminalPosition(161, 18+i), "        ");
        }


        for (int i = 0; i < reward.size(); i++) {
            graphics.setForegroundColor(reward.get(i).getColor());
            graphics.putString(new TerminalPosition(161, 18+i), reward.get(i).getName());
        }
        refresh();
    }

    public synchronized void clearDestroyed(){
        Coordinate.forEach(c -> {
            Result<Tile> res = game.board.getTiles().getTile(c);

            if(res.isErr())
                return;

            Tile t = res.getData();

            if(!Tile.real(t))
                graphics.putString(coordToTerminalPosition(c).plus(new TerminalPosition(6, 2)), " ");
        });

        refresh();
    }
}
