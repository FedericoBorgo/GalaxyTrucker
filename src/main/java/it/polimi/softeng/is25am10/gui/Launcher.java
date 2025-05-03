package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.Controller;
import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.socket.SocketClient;
import it.polimi.softeng.is25am10.tui.PlaceholderCallback;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class Launcher extends Application {

    public static void main(String[] args) throws IOException {
        Controller.main(new String[]{"false"});

        Font customFont = Font.loadFont(Launcher.class.getResourceAsStream("/gui/font.ttf"), 12);

        ClientInterface bot = new SocketClient("bot", "localhost", 1235, 1236);

        bot.join(new PlaceholderCallback());

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

        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());



    }
}
