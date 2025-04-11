package it.polimi.softeng.is25am10.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TileDrawController {

    private static final String TILES_FOLDER = "/tiles/"; // Cartella principale delle tile

    @FXML
    private Button drawTileButton;

    @FXML
    private ImageView tileImageView;

    // Metodo chiamato quando si preme il bottone
    @FXML
    protected void loadRandomImage() {
        String tileFromServer = getTileFromServer();
        // Controllo
        if (tileFromServer != null && !tileFromServer.isEmpty()) {
            String fullPath = TILES_FOLDER + tileFromServer;
            Image image = loadImage(fullPath);
            if (image != null) {
                tileImageView.setImage(image);
                tileImageView.setRotate(0); // Resetta la rotazione
            } else {
                System.err.println("Immagine non trovata: " + fullPath);
            }
        } else {
            System.err.println("Nessuna tile ricevuta dal server");
        }
    }

    // Simula la richiesta al server, da sostituire con la logica reale
    private String getTileFromServer() {
        return "BATTERY_2/0303.jpg";
    }

    // Carica l'immagine dal percorso specificato
    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResource(path).toExternalForm());
        } catch (NullPointerException e) {
            return null; // Immagine non trovata
        }
    }

    // Ruota l'immagine di 90 gradi
    @FXML
    protected void rotateImage() {
        tileImageView.setRotate(tileImageView.getRotate() + 90);
    }
}