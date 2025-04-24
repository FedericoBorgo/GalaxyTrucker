package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;
import it.polimi.softeng.is25am10.network.socket.SocketClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;


public class HomeController {
    private ClientInterface player;
    //passo il riferimento al primaryStage da appLauncher
    private Stage primaryStage;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField ipAddressField;

    @FXML
    private RadioButton rmiRadio;

    @FXML
    private RadioButton socketRadio;

    @FXML
    private TextField rmiPortField;

    @FXML
    private TextField socketPort1Field;

    @FXML
    private TextField socketPort2Field;

    @FXML
    private Button joinButton;

    @FXML
    private final ToggleGroup connectionToggleGroup = new ToggleGroup();


    @FXML
    public void initialize() {
        rmiRadio.setToggleGroup(connectionToggleGroup);
        socketRadio.setToggleGroup(connectionToggleGroup);

        rmiRadio.setSelected(true);
        ipAddressField.setText("localhost");
        rmiPortField.setText("1234");
        socketPort1Field.setText("1235");
        socketPort2Field.setText("1236");

        rmiRadio.setOnAction(e -> updateFields());
        socketRadio.setOnAction(e -> updateFields());

        updateFields();
        joinButton.setOnAction(e -> handleJoin());
    }

    private void updateFields() {
        boolean isRMI = rmiRadio.isSelected();
        rmiPortField.setDisable(!isRMI);
        socketPort1Field.setDisable(isRMI);
        socketPort2Field.setDisable(isRMI);
    }

    @FXML
    private void handleJoin() {
        // do we have to set a maximum number of characters for the name?
        String username = usernameField.getText().trim();
        String ip = ipAddressField.getText().trim();

        if (username.isEmpty()) {
            showAlert("Inserisci il nome.");
            return;
        }

        if (ip.isEmpty()) {
            showAlert("Inserisci l'indirizzo IP.");
            return;
        }

        if (rmiRadio.isSelected()) {
            String port = rmiPortField.getText().trim();
            System.out.println("Entrando con RMI | Username: " + username + " | IP: " + ip + " | Porta: " + port);
            // Connect via RMI logic here
            try {
                player = new RMIClient(username, ip, Integer.parseInt(port));
                player.join(new CallbackImpl());
                //qui ci vorrebbe un if, in base alla logica di gioco parte da 0 oppure dalla partita salvata
                switchToNext("/gui/ShipBuilding.fxml");
            } catch (Exception e) {
                showAlert("Errore di connessione RMI: " + e.getMessage());
            }

        } else {
            String port1 = socketPort1Field.getText().trim();
            String port2 = socketPort2Field.getText().trim();
            System.out.println("Entrando con Socket | Username: " + username + " | IP: " + ip + " | Porta 1: " + port1 + " | Porta 2: " + port2);

            // Connect via Socket logic here
            try {
                player = new SocketClient(username, ip, Integer.parseInt(port1), Integer.parseInt(port2));
                player.join(new CallbackImpl());
                //qui ci vorrebbe un if
                switchToNext("/gui/ShipBuilding.fxml");

            } catch (Exception e) {
                showAlert("Errore di connessione Socket: " + e.getMessage());
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Errore di input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //metodo setPrimaryStage in HomeController: Questo metodo permette di salvare il riferimento al primaryStage
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Se la partita era già in corso, il giocatore riprende, altrimenti passa alla schermata di ShipBuilding
    private void switchToNext(String fxml) {
        try {
            System.out.println("Loading FXML from: " + getClass().getResource("/gui/ShipBuilding.fxml"));
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ShipBuilding.fxml"));
            Parent root = loader.load();

            //passa primarystage
            ShipBuildingController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);

            // Set the scene
            Scene scene = new Scene(root);

            // Configure the stage
            primaryStage.setTitle("Galaxy Trucker");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception
            System.err.println("Fallito a cambiare scena, errore: " + e.getMessage());
        }
    }
}