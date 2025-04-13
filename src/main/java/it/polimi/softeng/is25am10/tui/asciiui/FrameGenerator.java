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
import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Player;
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.TilesBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
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

    public synchronized void clearTile(TerminalPosition pos){
        graphics.fillRectangle(pos, new TerminalSize(15, 5), ' ');
        refresh();
    }

    private void drawErrors(Set<Coordinate> errors){
        graphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);

        Coordinate.forEach(c -> {
            graphics.putString(coordToTerminalPosition(c).plus(new TerminalPosition(2, 1)), "   ");
        });

        if(errors.contains(new Coordinate(0, 0))) {
            graphics.putString(coordToTerminalPosition(new Coordinate(0, 0)), "SEP");
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
            String pos = textures.getJSONObject("FLIGHT")
                    .getString(String.valueOf(leader + offset.get(i)));

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
                        (quit.contains(name)? "fuori" : "") +
                        (dis.contains(name)? "(disconnesso)" : "             "));
                pos.getAndIncrement();
            });

            screen.refresh();
        }catch (Exception _){}
    }

    public synchronized void drawState(){
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(new TerminalPosition(93, 2), game.state.name() + "    ");
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
        graphics.fillRectangle(new TerminalPosition(117, 1), new TerminalSize(109, 16), ' ');
        graphics.fillRectangle(new TerminalPosition(161, 17), new TerminalSize(65, 43), ' ');
        refresh();
    }

    synchronized void drawInput(){
        graphics.putString(new TerminalPosition(29, 34), "                              ");
        graphics.setForegroundColor(isOkCommand? TextColor.ANSI.WHITE_BRIGHT : TextColor.ANSI.RED_BRIGHT);

        for(int i = 0; i < command.size(); i++){
            graphics.putString(new TerminalPosition(29+i, 34), ""+command.get(i));
        }

        screen.setCursorPosition(new TerminalPosition(29+ cursor, 34));

        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(new TerminalPosition(29, 36), response);

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

    /*
    public void drawCardData(){
        if(game.cardDataStr == null || game.state != Model.State.Type.WAITING_INPUT)
            return;

        for (int i = 0; i < game.cardDataStr.length; i++)
            graphics.putString(new TerminalPosition(160, 2+i), game.cardDataStr[i]);
    }

    public void drawDroppedItems(){
        if(game.state != Model.State.Type.WAITING_INPUT)
            return;

        graphics.putString(new TerminalPosition(120, 2), "Elementi rimossi");
        graphics.putString(new TerminalPosition(120, 3), "batterie: " + game.dropped.battery);
        graphics.putString(new TerminalPosition(120, 4), "membri: " + game.dropped.guys);
        graphics.putString(new TerminalPosition(120, 5), "scatole: " + game.dropped.goods);
    }

    public void drawCannonsToUse(){
        if(game.state != Model.State.Type.WAITING_INPUT)
            return;

        graphics.putString(new TerminalPosition(93, 4), "turno di " + game.waitFor);

        graphics.putString(new TerminalPosition(120, 7), "Cannoni");
        graphics.putString(new TerminalPosition(120, 8), "sorpa: " + game.cannonsToUse.get(Tile.Rotation.NONE));
        graphics.putString(new TerminalPosition(120, 9),"destra: " + game.cannonsToUse.get(Tile.Rotation.CLOCK));
        graphics.putString(new TerminalPosition(120, 10), "sotto: " + game.cannonsToUse.get(Tile.Rotation.DOUBLE));
        graphics.putString(new TerminalPosition(120, 11), "sinistra: " + game.cannonsToUse.get(Tile.Rotation.INV));
    }

    public void drawCash(){
        graphics.putString(new TerminalPosition(3, 3), "SOLDI");
        graphics.putString(new TerminalPosition(3, 4), ""+ game.cash);
    }*/

}
