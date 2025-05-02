package it.polimi.softeng.is25am10.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/welcome.fxml"));
        Parent root = loader.load();
        Welcome welcome = loader.getController();

        welcome.setStage(stage);
        Scene scene = new Scene(root);

        stage.setTitle("Galaxy Trucker");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }
}
