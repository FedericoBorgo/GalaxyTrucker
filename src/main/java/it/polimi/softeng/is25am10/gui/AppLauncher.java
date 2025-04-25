package it.polimi.softeng.is25am10.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Loading FXML from: " + getClass().getResource("/gui/home.fxml"));
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/home.fxml"));
            Parent root = loader.load();

            // Ottieni il controller e passa il primaryStage
            HomeController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);

            // Set the scene
            Scene scene = new Scene(root);

            // Configure the stage
            primaryStage.setTitle("Galaxy Trucker");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
            System.err.println("Fallito ad avviare l'applicazione, errore: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}