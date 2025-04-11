package it.polimi.softeng.is25am10.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TileDraw extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/tile-draw-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 320);

        stage.setTitle("Prove con le tessere");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
