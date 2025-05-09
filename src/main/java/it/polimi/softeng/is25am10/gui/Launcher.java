package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;

public class Launcher extends Application {

    static Stage stage = null;

    public static void main(String[] args) throws IOException {
        try{
            Controller.main(new String[]{"false"});
        }catch(Exception _){}
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Launcher.stage = stage;
        loadScene("/gui/login.fxml");

        stage.setTitle("Galaxy Trucker");
        stage.setResizable(false);
        stage.show();

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(0);
        stage.setY(bounds.getMinY() + bounds.getHeight());
    }

    static public <T> Pair<T, Scene> loadScene(String path){
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource(path));
        Parent root = null;

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
}
