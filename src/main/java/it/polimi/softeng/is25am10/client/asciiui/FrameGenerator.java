package it.polimi.softeng.is25am10.client.asciiui;

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
import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.boards.TilesBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.network.ClientInterface;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FrameGenerator {
    private final JSONObject textures;
    private final String[] boardBorder;
    private final Game game;
    private final Screen screen;
    private final Terminal terminal;
    private final TextGraphics graphics;
    private boolean pauseRender = false;
    private boolean isOkCommand = false;

    ArrayList<Character> command = new ArrayList<>();
    int cursor = 0;

    String response = "";

    public FrameGenerator(Game game) throws IOException {
        terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
        graphics = screen.newTextGraphics();
        graphics.enableModifiers(SGR.BOLD);
        screen.setCursorPosition(null);

        //get the textures
        boardBorder = Card.dump(Game.class.getResourceAsStream("board.txt")).split("\n");
        textures = new JSONObject(Card.dump(Game.class.getResourceAsStream("textures.json")));

        this.game = game;

        new Thread(() -> {
            try{
                while(true){
                    if(!pauseRender){
                        draw();
                        handleKeyBoard();
                    }
                    Thread.sleep(30);
                }
            }
            catch (Exception _){}}
        ).start();
    }

    /**
     * Draw the scheme of the board from the config file.
     * Draw also the indexes of the board.
     */
    private void drawBorderBoard(){
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(new TerminalPosition(1, 0), "x");
        graphics.putString(new TerminalPosition(0, 1), "y");

        for(int i = 0; i < 7; i++)
            graphics.putString(new TerminalPosition(i*16+2, 0), Integer.toString(i+4));

        for(int i = 0; i < 5; i++)
            graphics.putString(new TerminalPosition(0, i*6+2), Integer.toString(i+5));

        graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        for(int i = 0; i < boardBorder.length; i++)
            graphics.putString(new TerminalPosition(1, i+1), boardBorder[i]);

        if(game.state != Model.State.Type.BUILDING){
            graphics.fillRectangle(new TerminalPosition(119, 1), new TerminalSize(107, 15), ' ');
            graphics.fillRectangle(new TerminalPosition(161, 16), new TerminalSize(65, 28), ' ');
        }
    }

    /**
     * Convert the game coordinate to screen coordinate.
     * The game use a smaller coordinate from x0y0 to x7y5 to
     * identify the Tiles position.
     * The screen use the position of characters as coordinate.
     * Return the position of the top left caracter.
     * @param coord game coordinate
     * @return converted coordinate
     */
    private TerminalPosition coordToTerminalPosition(Coordinate coord){
        return new TerminalPosition(coord.x()*16+2, coord.y()*6+2);
    }

    /**
     * Draw the tiles board to the screen using the textures in the config file.
     * @param board the board to draw.
     */
    void drawTiles(TilesBoard board){
        Coordinate.forEach(c -> {
            Result<Tile> res = board.getTile(c);

            if(res.isErr())
                return;

            drawTile(coordToTerminalPosition(c), res.getData(), board.getRotation(c));
        });
    }

    /**
     * Draw the booked ties in their space
     * @param board
     */
    void drawBooked(TilesBoard board){
        if(game.state != Model.State.Type.BUILDING)
            return;

        if(!board.getBooked().isEmpty())
            drawTile(new TerminalPosition(120, 2),
                    board.getBooked().getFirst(), Tile.Rotation.NONE);

        if(board.getBooked().size() > 1)
            drawTile(new TerminalPosition(120, 8),
                    board.getBooked().getLast(), Tile.Rotation.NONE);
    }

    private void drawTile(TerminalPosition pos, Tile t, Tile.Rotation rotation){
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
    }

    /**
     * Draw the wrong placed tiles.
     * @param errors set of coordinates of wrong tiels
     */
    void drawErrors(Set<Coordinate> errors){
        graphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);

        if(errors.contains(new Coordinate(0, 0))) {
            graphics.putString(coordToTerminalPosition(new Coordinate(0, 0)), "Nave separata in più pezzi.");
            return;
        }

        errors.forEach(c -> {
            TerminalPosition pos = coordToTerminalPosition(c);
            graphics.putString(pos.plus(new TerminalPosition(2, 1)), "FIX");
        });
    }

    /**
     * Draw the batteries, astronauts, goods and aliens to the
     * screen.
     * @param board to draw
     */
    void drawElements(ShipBoard board){
        Map<Coordinate, Integer> offset = new HashMap<>();
        board.boards().forEach(b ->{
            graphics.setForegroundColor(b.getColor());

            b.getPositions().forEach((c, tot) -> {
                TerminalPosition pos = coordToTerminalPosition(c);
                int plus = offset.getOrDefault(c, 0);

                for(int i = plus; i < tot+plus; i++)
                    graphics.putString(pos.plus(new TerminalPosition(7, 1+i)), "X");

                offset.put(c, tot+plus);
            });
        });
    }

    /**
     * Draws the flightboard to the screen
     */
    void drawFlight(){
        List<FlightBoard.Pawn> order = game.flight.getOrder();
        int leader = game.flight.getLeaderPosition();
        List<Integer> offset = game.flight.getOffset();

        for(int i = 0; i < order.size(); i++){
            String pos = textures.getJSONObject("FLIGHT")
                    .getString(String.valueOf(leader + offset.get(i)));

            int x = Integer.parseInt(pos.substring(0, pos.indexOf(' ')));
            int y = Integer.parseInt(pos.substring(pos.indexOf(' ')+1));

            TerminalPosition screenPos = new TerminalPosition(120 + y, 17 + x);

            graphics.setForegroundColor(order.get(i).getColor());
            graphics.putString(screenPos, "X");
        }
    }

    /**
     * Draw the clock position
     */
    void drawClock(){
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.drawRectangle(new TerminalPosition(122+ game.flight.getTimer()*10, 25),
                new TerminalSize(8, 4), '#');

        if(game.state == Model.State.Type.BUILDING) {
            long time = Model.TIMER_DELAY/1000-((System.currentTimeMillis()-game.startTime)/1000);
            if(time < 0)
                time = 0;
            graphics.putString(new TerminalPosition(122+ game.flight.getTimer()*10, 30), String.valueOf(time));
        }
    }

    /**
     * Draw the players connected to the game
     */
    void drawPlayersName(){
        AtomicInteger pos = new AtomicInteger(34);
        game.players.forEach((name, pawn) -> {
            graphics.setForegroundColor(pawn.getColor());
            graphics.putString(new TerminalPosition(0, pos.get()), "#"+name);
            pos.getAndIncrement();
        });
    }

    void drawState(){
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(new TerminalPosition(93, 2), game.state.name());
    }

    void drawInput(){
        graphics.setForegroundColor(isOkCommand? TextColor.ANSI.WHITE_BRIGHT : TextColor.ANSI.RED_BRIGHT);

        for(int i = 0; i < command.size(); i++){
            graphics.putString(new TerminalPosition(29+i, 34), ""+command.get(i));
        }

        screen.setCursorPosition(new TerminalPosition(29+ cursor, 34));

        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(new TerminalPosition(29, 36), response);
    }

    void drawCurrentTile(){
        if(game.state != Model.State.Type.BUILDING)
            return;

        if(game.currentTile == null)
            return;

        drawTile(new TerminalPosition(140, 2),
                game.currentTile, Tile.Rotation.NONE);
    }

    /**
     * Draw everything to the screen.
     */
    public void draw(){
        screen.clear();
        drawBorderBoard();
        drawFlight();
        drawTiles(game.board.getTiles());
        drawBooked(game.board.getTiles());
        drawElements(game.board);
        drawErrors(game.board.getTiles().isOK());
        drawClock();
        drawPlayersName();
        drawState();
        drawInput();
        drawCurrentTile();
        drawOpenTiles();

        try {
            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        return box.getInt();
    }

    void drawOpenTiles(){
        if(game.state != Model.State.Type.BUILDING)
            return;

        TerminalPosition start = new TerminalPosition(162, 2);

        for (int i = 0; i < game.openTiles.size(); i++) {
            drawTile(start.plus(new TerminalPosition(
                    (i/7)*16,
                    (i%7)*6
            )), game.openTiles.get(i), Tile.Rotation.NONE);
        }
    }

    void dialog(String message){
        pauseRender = true;
        WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        Window window = new BasicWindow("Dialog");
        textGUI.addWindow(window);

        MessageDialog.showMessageDialog(textGUI, "Message", message, MessageDialogButton.OK);
        window.close();

        textGUI.waitForWindowToClose(window);
        screen.clear();
        pauseRender = false;
    }
}
