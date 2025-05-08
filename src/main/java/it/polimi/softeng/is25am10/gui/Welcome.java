package it.polimi.softeng.is25am10.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Welcome {
    @FXML
    private void join(){
        Launcher.loadScene("/gui/login.fxml");
    }
}
