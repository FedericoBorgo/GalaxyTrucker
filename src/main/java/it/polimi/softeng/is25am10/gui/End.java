package it.polimi.softeng.is25am10.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;

/**
 * Manages the end screen with the points obtained by each player.
 */
public class End {
    @FXML
    VBox resBox;

    @FXML
    public Text disconnected;

    public void setRes(HashMap<String,Integer> res) {
        Text titleText = new Text("Cosmic credits:");
        titleText.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
        titleText.setFill(Color.WHITE);
        resBox.getChildren().add(titleText);
        res.forEach((p, v) -> {
            Text text = new Text(p + ": " + v);
            text.setFont(Font.font("Arial", FontWeight.BOLD, 30));
            text.setFill(Color.WHITE);
            resBox.getChildren().add(text);
        });
    }

    @FXML
    void newGame(){
        Launcher.loadScene("/gui/welcome.fxml");
    }
}
