package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.Controller;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class Launcher extends Application {

    static Stage stage = null;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the application and sets basic screen parameters.
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        Launcher.stage = stage;

        Font customFont = Font.loadFont(Launcher.class.getResourceAsStream("/gui/font.ttf"), 12);

        loadScene("/gui/welcome.fxml");

        stage.setTitle("Galaxy Trucker");
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(_ -> {
            System.out.println("Closing GalaxyTrucker");
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Loads a scene from the given path and sets it to the stage.
     * @param path The path to the FXML file.
     * @param <T> The type of the controller.
     * @return A pair containing the handler and the scene.
     */
    static public <T> Pair<T, Scene> loadScene(String path){
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource(path));
        Parent root;

        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        T handler = loader.getController();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Launcher.class.getResource("/gui/style.css").toExternalForm());
        Launcher.stage.setScene(scene);
        return new Pair<>(handler, scene);
    }

    // returns the image
    static public Image getSmallImage(Image original){
        return getRotatedImage(original, 0);
    }

    /**
     * Rotates an image by the given angle in degrees.
     * @param original
     * @param angleDegrees
     * @return
     */
    static public Image getRotatedImage(Image original, double angleDegrees) {
        double size = 30;

        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.save();
        Rotate r = new Rotate(angleDegrees, size/2, size/2);
        gc.transform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.drawImage(original, 0, 0, size, size);
        gc.restore();

        WritableImage rotatedImage = new WritableImage((int) size, (int) size);
        canvas.snapshot(null, rotatedImage);

        return rotatedImage;
    }

    // Get methods

    public static ImageView getView(String path){
        return new ImageView(getImage(path));
    }

    public static ImageView getView(Tile t){
        return new ImageView(getImage(t));
    }

    public static Image getImage(Tile t){
        return getImage("/tiles/" + t.getType().name() + "/" + t.connectorsToInt() + ".jpg");
    }

    public static Image getImage(String path){
        return new Image(Building.class.getResource(path).toExternalForm());
    }

    public static Image getCHouse(FlightBoard.Pawn p){
        String path = "/tiles/C_HOUSE/3333_" + switch(p){
            case YELLOW -> "yellow";
            case GREEN -> "green";
            case BLUE -> "blue";
            case RED -> "red";
        } + ".jpg";

        return new Image(Building.class.getResource(path).toExternalForm());
    }


    public static Optional<Pair<Coordinate, String>> getCoordinate(DragEvent event, GridPane ship, AtomicBoolean success){
        Dragboard db = event.getDragboard();
        event.setDropCompleted(true);
        event.consume();
        success.set(false);

        if(!db.hasString())
            return Optional.empty();

        int col = (int)(event.getX()/(ship.getWidth()/7));
        int row = (int)(event.getY()/(ship.getHeight()/5));

        if(Coordinate.isInvalid(col, row))
            return Optional.empty();

        return Optional.of(new Pair<>(new Coordinate(col, row), db.getString()));
    }
}
