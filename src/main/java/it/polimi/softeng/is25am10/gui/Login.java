package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;
import it.polimi.softeng.is25am10.network.socket.SocketClient;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class Login {

    @FXML
    TextField usernameField;

    @FXML
    TextField addressField;

    @FXML
    SplitMenuButton connectionMenu;

    @FXML
    TextField port1;

    @FXML
    TextField port2;

    @FXML
    Label errLabel;

    @FXML
    private void join(){
        ClientInterface client;
        String name = usernameField.getText();
        String address = addressField.getText();

        if(name.isEmpty() || address.isEmpty()){
            errLabel.setText("Inserire nome utente");
            errLabel.setAlignment(Pos.CENTER);
        }

        errLabel.setText("porta non valida");
        errLabel.setAlignment(Pos.CENTER);
        int port1 = Integer.parseInt(this.port1.getText());
        int port2 = Integer.parseInt(this.port2.getText());
        errLabel.setText("");

        try{
            if(connectionMenu.getText().equals("RMI"))
                client = new RMIClient(name, address, port1);
            else
                client = new SocketClient(name, address, port1, port2);
        }catch (Exception _){
            errLabel.setText("Impossibile connettersi al server");
            errLabel.setAlignment(Pos.CENTER);
        }
    }

    @FXML
    private void socket(){
        connectionMenu.setText("SOCKET");
        port2.setVisible(true);
        port2.setEditable(true);
        port1.setText("1235");
        port2.setText("1236");
    }

    @FXML
    private void rmi(){
        connectionMenu.setText("RMI");
        port2.setVisible(false);
        port2.setEditable(false);
        port1.setText("1234");
    }
}
