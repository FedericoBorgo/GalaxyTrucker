package it.polimi.softeng.is25am10.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;

public class End {
    @FXML
    VBox resBox;

    @FXML
    public Text disconnected;

    public void setRes(HashMap<String,Integer> res) {
        res.forEach((p, v) -> {
            Text text = new Text(p + ": " + v);
            text.setFont(Font.font("Arial", FontWeight.BOLD, 30));
            text.setFill(Color.MAGENTA);
            resBox.getChildren().add(text);
        });
    }

    @FXML
    void newGame(){
        Launcher.loadScene("/gui/welcome.fxml");
    }
}
