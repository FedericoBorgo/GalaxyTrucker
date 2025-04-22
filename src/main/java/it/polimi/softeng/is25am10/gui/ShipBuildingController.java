package it.polimi.softeng.is25am10.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShipBuildingController {
    @FXML private GridPane shipBoard;
    @FXML private GridPane tilePile;
    @FXML private Pane bookedTile1, bookedTile2, drawnTile;
    @FXML private ImageView bookedImage1, bookedImage2, drawnImage;
    @FXML private Pane pileTile0, pileTile1, pileTile2, pileTile3; // Extend to pileTile19
    @FXML private ImageView pileImage0, pileImage1, pileImage2, pileImage3; // Extend to pileImage19
    // Add ImageViews for shipboard tiles, e.g., image_0_2, image_1_1, etc.

    @FXML private ListView<String> playerList;

    private ObservableList<String> players; // List of player names

    private List<Image> tileImages; // List of available tile PNGs
    private ImageView selectedTile; // Currently selected tile
    private int selectedRotation; // Rotation state (0, 90, 180, 270 degrees)
    private List<Pane> pilePanes; // List of pile tile panes

    @FXML
    private void initialize() {
        // Initialize tile images (replace with actual PNG paths)
        // In the initialization we will place just the tile with the main cockpit
        tileImages = new ArrayList<>();
        tileImages.add(new Image("file:tile1.png"));
        tileImages.add(new Image("file:tile2.png"));
        // Add more tile images if needed

        // Initialize pile panes
        pilePanes = new ArrayList<>();
        pilePanes.add(pileTile0);
        pilePanes.add(pileTile1);
        pilePanes.add(pileTile2);
        pilePanes.add(pileTile3);
        // Add remaining pile tiles up to pileTile19

        // Set up drag-and-drop for shipboard tiles
        setupShipBoardDragAndDrop();
        // Set up drag-and-drop and booking for tile selection
        setupTileSelectionDragAndDrop();

        // Initialize player list
        players = FXCollections.observableArrayList();
        playerList.setItems(players);

        // Add sample players (replace with actual player data)
        players.addAll("Player 1", "Player 2", "Player 3");
    }
    /*
    Not needed, will have to do it with the game's logic
    // Optional: Method to add a player dynamically
    public void addPlayer(String playerName) {
        if (!players.contains(playerName)) {
            players.add(playerName);
        }
    }
    // Optional: Method to remove a player
    public void removePlayer(String playerName) {
        players.remove(playerName);
    }
    */

    private void setupShipBoardDragAndDrop() {
        // For each shipboard tile pane (e.g., tile_0_2, tile_1_1, etc.)
        for (var node : shipBoard.getChildren()) {
            Pane pane = (Pane) node;
            ImageView imageView = (ImageView) pane.getChildren().get(0);

            // Accept dropped tiles
            pane.setOnDragOver(event -> {
                if (event.getGestureSource() != pane && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            pane.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString() && imageView.getImage() == null) {
                    // Place tile from source
                    String sourceId = db.getString();
                    Image tileImage = getTileImageById(sourceId);
                    if (tileImage != null) {
                        imageView.setImage(tileImage);
                        imageView.setRotate(selectedRotation);
                        clearSourceTile(sourceId);
                        success = true;
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });
        }
    }

    private void setupTileSelectionDragAndDrop() {
        // Setup for booked tiles
        setupTilePane(bookedTile1, bookedImage1);
        setupTilePane(bookedTile2, bookedImage2);
        setupTilePane(drawnTile, drawnImage);
        for (Pane pilePane : pilePanes) {
            ImageView imageView = (ImageView) pilePane.getChildren().get(0);
            setupTilePane(pilePane, imageView);
        }
    }

    private void setupTilePane(Pane pane, ImageView imageView) {
        // Allow dragging from tiles with images
        pane.setOnDragDetected(event -> {
            if (imageView.getImage() != null) {
                Dragboard db = pane.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(pane.getId());
                db.setContent(content);
                selectedTile = imageView;
                selectedRotation = (int) imageView.getRotate();
            }
            event.consume();
        });

        // Allow booking by clicking
        //it's probably better to drag the image to book to the booked section
        //this section will be rewritten
        pane.setOnMouseClicked(event -> {
            if (imageView.getImage() != null && (pane == drawnTile || pilePanes.contains(pane))) {
                if (bookedImage1.getImage() == null) {
                    bookedImage1.setImage(imageView.getImage());
                    bookedImage1.setRotate(imageView.getRotate());
                    imageView.setImage(null);
                } else if (bookedImage2.getImage() == null) {
                    bookedImage2.setImage(imageView.getImage());
                    bookedImage2.setRotate(imageView.getRotate());
                    imageView.setImage(null);
                }
            }
            selectedTile = imageView;
            selectedRotation = (int) imageView.getRotate();
        });
    }

    @FXML
    private void drawTile() {
        if (drawnImage.getImage() == null) {
            Random rand = new Random();
            Image tileImage = tileImages.get(rand.nextInt(tileImages.size()));
            drawnImage.setImage(tileImage);
            drawnImage.setRotate(0);
        } else {
            // Move drawn tile to pile
            for (Pane pilePane : pilePanes) {
                ImageView pileImageView = (ImageView) pilePane.getChildren().get(0);
                if (pileImageView.getImage() == null) {
                    pileImageView.setImage(drawnImage.getImage());
                    pileImageView.setRotate(drawnImage.getRotate());
                    drawnImage.setImage(null);
                    // Draw new tile
                    Random rand = new Random();
                    Image tileImage = tileImages.get(rand.nextInt(tileImages.size()));
                    drawnImage.setImage(tileImage);
                    drawnImage.setRotate(0);
                    break;
                }
            }
        }
    }

    @FXML
    private void rotateTile() {
        if (selectedTile != null && selectedTile.getImage() != null) {
            selectedRotation = (selectedRotation + 90) % 360;
            selectedTile.setRotate(selectedRotation);
        }
    }

    @FXML
    private void finishBuilding() {
        // Validate shipboard and proceed
        System.out.println("Astronave costruita!");
        // Add logic to transition to next game phase
    }

    private Image getTileImageById(String id) {
        if (id.equals("drawnTile")) return drawnImage.getImage();
        if (id.equals("bookedTile1")) return bookedImage1.getImage();
        if (id.equals("bookedTile2")) return bookedImage2.getImage();
        for (int i = 0; i < pilePanes.size(); i++) {
            if (id.equals("pileTile" + i)) {
                return ((ImageView) pilePanes.get(i).getChildren().get(0)).getImage();
            }
        }
        return null;
    }

    private void clearSourceTile(String id) {
        if (id.equals("drawnTile")) drawnImage.setImage(null);
        else if (id.equals("bookedTile1")) bookedImage1.setImage(null);
        else if (id.equals("bookedTile2")) bookedImage2.setImage(null);
        else {
            for (int i = 0; i < pilePanes.size(); i++) {
                if (id.equals("pileTile" + i)) {
                    ((ImageView) pilePanes.get(i).getChildren().get(0)).setImage(null);
                    break;
                }
            }
        }
    }
}