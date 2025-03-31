package it.polimi.softeng.is25am10.client.tui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
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
import it.polimi.softeng.is25am10.model.boards.TilesBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;
import javafx.util.Pair;
import org.json.JSONObject;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public class RendererTUI extends Thread implements Callback {
    private final JSONObject textures;
    private final String[] boardBorder;
    Screen screen;
    Terminal terminal;
    TextGraphics graphics;

    TilesBoard board = new TilesBoard();

    public RendererTUI(ClientInterface client) throws IOException {
        super();
        //client.join(this);
        terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
        graphics = screen.newTextGraphics();
        graphics.enableModifiers(SGR.BOLD);
        screen.setCursorPosition(null);

        //get the textures
        boardBorder = Card.dump(RendererTUI.class.getResourceAsStream("board.txt")).split("\n");
        textures = new JSONObject(Card.dump(RendererTUI.class.getResourceAsStream("textures.json")));

        board.setTile(new Coordinate(3, 1), new Tile(Tile.Type.CANNON, "stuo"), Tile.Rotation.NONE);
        board.setTile(new Coordinate(3, 3), new Tile(Tile.Type.PIPES, "tusu"), Tile.Rotation.NONE);

        board.setTile(new Coordinate(2, 2), new Tile(Tile.Type.B_BOX_2, "usos"), Tile.Rotation.NONE);
        board.setTile(new Coordinate(2, 3), new Tile(Tile.Type.ENGINE, "otst"), Tile.Rotation.NONE);

        board.setTile(new Coordinate(4, 1), new Tile(Tile.Type.PIPES, "uouo"), Tile.Rotation.NONE);
        board.setTile(new Coordinate(4, 2), new Tile(Tile.Type.B_ADDON, "tstt"), Tile.Rotation.NONE);
        board.setTile(new Coordinate(4, 3), new Tile(Tile.Type.CANNON, "sstu"), Tile.Rotation.CLOCK);
        board.setTile(new Coordinate(1, 2), new Tile(Tile.Type.D_CANNON, "sstu"), Tile.Rotation.CLOCK);
        board.setTile(new Coordinate(1, 3), new Tile(Tile.Type.D_ENGINE, "sstu"), Tile.Rotation.INV);
        board.setTile(new Coordinate(5, 1), new Tile(Tile.Type.R_BOX_1, "sstu"), Tile.Rotation.INV);
        board.setTile(new Coordinate(5, 2), new Tile(Tile.Type.R_BOX_2, "sstu"), Tile.Rotation.INV);
        board.setTile(new Coordinate(5, 3), new Tile(Tile.Type.B_BOX_2, "sstu"), Tile.Rotation.INV);
        board.setTile(new Coordinate(5, 4), new Tile(Tile.Type.B_BOX_3, "sstu"), Tile.Rotation.INV);
        board.setTile(new Coordinate(1, 4), new Tile(Tile.Type.HOUSE, "sstu"), Tile.Rotation.INV);

        board.setTile(new Coordinate(0, 2), new Tile(Tile.Type.SHIELD, "sstu"), Tile.Rotation.NONE);
        board.setTile(new Coordinate(0, 3), new Tile(Tile.Type.SHIELD, "sstu"), Tile.Rotation.CLOCK);
        board.setTile(new Coordinate(0, 4), new Tile(Tile.Type.SHIELD, "sstu"), Tile.Rotation.DOUBLE);
        board.setTile(new Coordinate(6, 2), new Tile(Tile.Type.BATTERY_2, "sstu"), Tile.Rotation.CLOCK);
        board.setTile(new Coordinate(6, 3), new Tile(Tile.Type.BATTERY_3, "sstu"), Tile.Rotation.DOUBLE);


        start();
    }

    @Override
    public void run(){
        try{
            while(true){
                draw();
                handleKeyBoard();


                sleep(30);
            }
        }
        catch (Exception _){}

    }

    /**
     * Draw everything to the screen.
     */
    private void draw(){
        drawBorderBoard();
        drawTiles(board);

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
            TerminalPosition pos = coordToTerminalPosition(c);
            Result<Tile> res = board.getTile(c);

            if(res.isErr())
                return;

            Tile.Type tile = res.getData().getType();
            Tile.Rotation rotation = board.getRotation(c);

            // get the texture of the corresponding tile
            JSONObject texture = textures.getJSONObject(tile.name());

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
                Tile.ConnectorType type = Tile.getSide(res.getData(), board.getRotation(c), side);
                texture = textures.getJSONObject("connectors");
                JSONObject offset = texture.getJSONObject(side.name());

                xOffset = offset.getInt("x");
                yOffset = offset.getInt("y");

                texture = texture.getJSONObject(type.name()).getJSONObject(side.name());

                for(int i = 0; texture.has(String.valueOf(i)); i++)
                    graphics.putString(pos.plus(new TerminalPosition(xOffset, yOffset + i)), texture.getString(String.valueOf(i)));
            }
        });
    }








    @Override
    public void joinedPlayer(String player) throws RemoteException {

    }

    @Override
    public int askHowManyPlayers() throws RemoteException {
        return 0;
    }

    @Override
    public void notifyState(Model.State.Type state) throws RemoteException {

    }

    @Override
    public void movedTimer() throws RemoteException {

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
