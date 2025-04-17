package it.polimi.softeng.is25am10.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class HomeController {

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
        socketPort2Field.setText("1239");

        rmiRadio.setOnAction(e -> updateFields());
        socketRadio.setOnAction(e -> updateFields());

        updateFields();
    }

    private void updateFields() {
        boolean isRMI = rmiRadio.isSelected();
        rmiPortField.setDisable(!isRMI);
        socketPort1Field.setDisable(isRMI);
        socketPort2Field.setDisable(isRMI);
    }

    @FXML
    private void handleJoin() {
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
        } else {
            String port1 = socketPort1Field.getText().trim();
            String port2 = socketPort2Field.getText().trim();
            System.out.println("EEntrando con Socket | Username: " + username + " | IP: " + ip + " | Porta 1: " + port1 + " | Porta 2: " + port2);
            // Connect via Socket logic here
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Errore di input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}