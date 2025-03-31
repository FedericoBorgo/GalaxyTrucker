package it.polimi.softeng.is25am10.client.tui;

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
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
import org.json.JSONObject;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class RendererTUI extends UnicastRemoteObject implements Callback {
    private final JSONObject textures;
    private final String[] boardBorder;
    Screen screen;
    Terminal terminal;
    TextGraphics graphics;
    Map<String, FlightBoard.Pawn> players = new HashMap<>();

    ShipBoard board = new ShipBoard();
    FlightBoard flight = new FlightBoard();

    Model.State.Type state = Model.State.Type.JOINING;

    boolean pauseRender = false;

    public RendererTUI(ClientInterface client) throws IOException {
        super();
        terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
        graphics = screen.newTextGraphics();
        graphics.enableModifiers(SGR.BOLD);
        screen.setCursorPosition(null);

        //get the textures
        boardBorder = Card.dump(RendererTUI.class.getResourceAsStream("board.txt")).split("\n");
        textures = new JSONObject(Card.dump(RendererTUI.class.getResourceAsStream("textures.json")));

        new Thread(update).start();

        client.join(this).getData();
    }

    final Runnable update = () -> {
        try{
            while(true){
                if(!pauseRender){
                    draw();
                    handleKeyBoard();
                }
                Thread.sleep(30);
            }
        }
        catch (Exception _){}
    };


    /**
     * Draw everything to the screen.
     */
    private void draw(){
        drawBorderBoard();
        drawFlight();
        drawTiles(board.getTiles());
        drawBooked(board.getTiles());
        drawElements(board);
        drawErrors(board.getTiles().isOK());
        drawClock();
        drawPlayersName();
        drawState();

        try {
            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the user keyboard input.
     */
    private void handleKeyBoard(){
        try {
            KeyStroke key = screen.pollInput();

            if(key != null){
                if(key.getCharacter() == 'c' && key.isCtrlDown()){
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        List<FlightBoard.Pawn> order = flight.getOrder();
        int leader = flight.getLeaderPosition();
        List<Integer> offset = flight.getOffset();

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
        graphics.drawRectangle(new TerminalPosition(122+ (flight.getTimer()+1)*10, 25),
                new TerminalSize(8, 4), '#');
    }

    /**
     * Draw the players connected to the game
     */
    void drawPlayersName(){
        AtomicInteger pos = new AtomicInteger(34);
        players.forEach((name, pawn) -> {
            graphics.setForegroundColor(pawn.getColor());
            graphics.putString(new TerminalPosition(0, pos.get()), "#"+name);
            pos.getAndIncrement();
        });
    }

    void drawState(){
        graphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        graphics.putString(new TerminalPosition(93, 2), state.name());
    }




    @Override
    public void setPlayers(Map<String, FlightBoard.Pawn> players) throws RemoteException {
        this.players = players;
    }

    @Override
    public int askHowManyPlayers() throws RemoteException {
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

    @Override
    public void notifyState(Model.State.Type state) throws RemoteException {
        this.state = state;
    }

    @Override
    public void movedTimer() throws RemoteException {
        flight.moveTimer();
    }

    @Override
    public void pushPositions(List<FlightBoard.Pawn> order, List<Integer> offset) throws RemoteException {

    }

    @Override
    public void pushCard(Card.CompressedCard card) throws RemoteException {

    }

    @Override
    public void pushCardChanges(JSONObject data) throws RemoteException {

    }

    @Override
    public void askForInput() throws RemoteException {

    }
}
