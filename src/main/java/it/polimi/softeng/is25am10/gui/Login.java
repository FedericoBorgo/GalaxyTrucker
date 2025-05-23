package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;
import it.polimi.softeng.is25am10.network.socket.SocketClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Controller of the login screen, manages the entire process in the gui.
 * Automatically sets the port while selecting RMI/Socket.
 * Checks for connection problems such as same username error.
 */
public class Login {

    @FXML
    TextField usernameField, addressField, port1, port2, playersField;

    @FXML
    SplitMenuButton connectionMenu;

    @FXML
    Label errLabel;

    @FXML
    private void initialize(){
        try {
            FileInputStream in = new FileInputStream("config.json");
            JSONObject json = new JSONObject(Card.dump(in));
            in.close();
            usernameField.setText(json.getString("username"));
            addressField.setText(json.getString("address"));
            port1.setText(json.getString("port1"));
            port2.setText(json.getString("port2"));
            playersField.setText(json.getString("players"));
            connectionMenu.setText(json.getString("connection"));
            port2.setVisible(connectionMenu.getText().equals("SOCKET"));
        } catch (Exception _) {}
    }

    @FXML
    private void join(){
        ClientInterface server;
        String name = usernameField.getText();
        String address = addressField.getText();

        if(name.isEmpty() || address.isEmpty()){
            errLabel.setText("Inserire nome utente");
            errLabel.setAlignment(Pos.CENTER);
        }

        errLabel.setText("porta/giocatori non valida");
        errLabel.setAlignment(Pos.CENTER);
        int port1 = Integer.parseInt(this.port1.getText());
        int port2 = Integer.parseInt(this.port2.getText());
        int players = Integer.parseInt(this.playersField.getText());
        errLabel.setText("");

        if(players <= 0 || players > 4){
            errLabel.setText("Giocatori non validi");
            return;
        }

        try{
            if(connectionMenu.getText().equals("RMI"))
                server = new RMIClient(name, address, port1);
            else
                server = new SocketClient(name, address, port1, port2);

            Pair<Building, Scene> handler = Launcher.loadScene("/gui/building.fxml");
            Building building = handler.getKey();
            building.nPlayers = players;
            building.listener = new GUIEventListener(server, building);

            Result<FlightBoard.Pawn> res = server.join(building.listener);

            // Salva i valori inseriti dall'utente in caso di errore "utente già connesso"
            if(res.isErr()){
                Pair<Login, Scene> handler2 = Launcher.loadScene("/gui/login.fxml");
                Login login = handler2.getKey();

                login.addressField.setText(address);
                login.usernameField.setText(name);
                login.port1.setText(String.valueOf(port1));
                login.port2.setText(String.valueOf(port2));
                login.playersField.setText(String.valueOf(players));
                login.errLabel.setText("Giocatore già connesso");
            }
            else{
                FlightBoard.Pawn pawn = res.getData();
                building.config(pawn, name, server, handler.getValue());

                FileOutputStream on = new FileOutputStream("config.json");
                JSONObject json = new JSONObject();
                json.put("username", name);
                json.put("address", address);
                json.put("port1", String.valueOf(port1));
                json.put("port2", String.valueOf(port2));
                json.put("players", String.valueOf(players));
                json.put("connection", connectionMenu.getText());
                on.write(json.toString().getBytes());
                on.close();
            }
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
