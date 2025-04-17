package it.polimi.softeng.is25am10.gui;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TileDrawController {

    private static final String TILES_FOLDER = "/tiles/";

    private ClientInterface firstPlayer;
    private ClientInterface secondPlayer;
    private final String firstPName = "tsv";
    private final String secondPName = "player2";

    @FXML
    private Button drawTileButton;

    @FXML
    private ImageView tileImageView;

    public TileDrawController() {
        try {
            // Inizializza il client RMI per il primo giocatore
            firstPlayer = new RMIClient(firstPName, "localhost", 1234);
            firstPlayer.join(new CallbackImpl());
            System.out.println("Giocatore 1 (" + firstPName + ") connesso");

            // Inizializza il client RMI per il secondo giocatore (necessario per iniziare la partita
            secondPlayer = new RMIClient(secondPName, "localhost", 1234);
            secondPlayer.join(new CallbackImpl());
            System.out.println("Giocatore 2 (" + secondPName + ") connesso");

        } catch (Exception e) {
            System.err.println("Errore connessione RMI: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            firstPlayer = null;
            secondPlayer = null;
        }
    }

    @FXML
    protected void loadRandomImage() {
        // Richiede una tile al server tramite il client del primo giocatore
        String tileFromServer = getTileFromServer();
        if (tileFromServer != null && !tileFromServer.isEmpty()) {
            String fullPath = TILES_FOLDER + tileFromServer;
            Image image = loadImage(fullPath);
            if (image != null) {
                tileImageView.setImage(image);
                tileImageView.setRotate(0);
            } else {
                System.err.println("Immagine non trovata: " + fullPath);
            }
        } else {
            System.err.println("Nessuna tile ricevuta dal server");
        }
    }

    // Ottiene il percorso di una tile dal server tramite il client del primo giocatore
    private String getTileFromServer() {
        if (firstPlayer == null) {
            System.err.println("Primo giocatore non connesso");
            return null;
        }
        try {
            Result<Tile> res = firstPlayer.drawTile();
            if (res.isOk()) {
                Tile t = res.getData();
                String folder = t.getType().toString();
                String file = getImageFileName(t);
                String path = folder + "/" + file + ".jpg";
                System.out.println("Tile ricevuta: " + path);
                return path;
            } else {
                System.err.println("Errore dal server: " + res.getReason());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Errore drawTile: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Converte i connettori della tile nella stringa numerica che identifica la texture della tile
    private String getImageFileName(Tile tile) {
        StringBuilder fileName = new StringBuilder();
        Tile.ConnectorType[] connectors = new Tile.ConnectorType[] {
                tile.getConnectors().get(Tile.Side.UP),
                tile.getConnectors().get(Tile.Side.RIGHT),
                tile.getConnectors().get(Tile.Side.DOWN),
                tile.getConnectors().get(Tile.Side.LEFT)
        };
        for (Tile.ConnectorType connector : connectors) {
            switch (connector) {
                case UNIVERSAL:
                    fileName.append("3");
                    break;
                case TWO_PIPE:
                    fileName.append("2");
                    break;
                case ONE_PIPE:
                    fileName.append("1");
                    break;
                case SMOOTH:
                    fileName.append("0");
                    break;
            }
        }
        return fileName.toString();
    }

    // Carica un'immagine dal percorso specificato
    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResource(path).toExternalForm());
        } catch (NullPointerException e) {
            return null;
        }
    }

    @FXML
    protected void rotateImage() {
        tileImageView.setRotate(tileImageView.getRotate() + 90);
    }
}