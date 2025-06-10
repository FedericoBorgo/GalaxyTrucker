package it.polimi.softeng.is25am10.model;

import it.polimi.softeng.is25am10.gui.Building;
import it.polimi.softeng.is25am10.gui.CardScene;
import it.polimi.softeng.is25am10.gui.Launcher;
import it.polimi.softeng.is25am10.model.boards.Coordinate;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * Keeps track of the current state of the game.
 */
public class State implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return prev == state.prev && curr == state.curr;
    }

    /**
     * Enumerates the possible states of the game.
     */
    public enum Type {
        JOINING,
        BUILDING,
        //checking the board and fixing
        CHECKING,
        //setting where to put the alien
        ALIEN_INPUT,
        DRAW_CARD,
        //waiting player input
        WAITING_INPUT,
        PLACE_REWARD,
        ENDED,
        PAUSED,
        PAY_DEBT;

        public String getName() {
            return switch (this) {
                case JOINING -> "Aspettare giocatori";
                case BUILDING -> "Assemblare";
                case CHECKING -> "Controllare connettori";
                case ALIEN_INPUT -> "Piazzare equipaggio";
                case DRAW_CARD -> "Pescare carta";
                case WAITING_INPUT -> "Dichiarare scelte";
                case PLACE_REWARD -> "Piazzare scatole";
                case ENDED -> "Terminata";
                case PAUSED -> "In pausa";
                case PAY_DEBT -> "Gettare elementi";
            };
        }

        /**
         * Sets the aliens and their data to where they are dragged and dropped.
         * @param view
         * @param pos
         * @param clip
         * @param texture
         */
        private void registerAlien(ImageView view, Result<Coordinate> pos, String clip, String texture) {
            view.setOnDragDetected(event -> {
                event.consume();
                if (pos.isOk()) {
                    view.setOnDragDetected(null);
                    return;
                }
                view.setCursor(Cursor.CLOSED_HAND);
                Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(clip);
                db.setContent(content);
                db.setDragView(Launcher.getRotatedImage(Launcher.getImage("/gui/textures/" + texture + ".png"), 0));
            });
        }

        /**
         * Manages the visible content of the gui when the game is in the ALIEN_INPUT state.
         * @param b
         */
        public void apply(Building b) {
            if (this == State.Type.ALIEN_INPUT) {
                b.pAlienView.setVisible(true);
                b.bAlienView.setVisible(true);

                registerAlien(b.pAlienView, b.purple, "p", "purple");
                registerAlien(b.bAlienView, b.brown, "b", "brown");
            } else if (this == State.Type.DRAW_CARD) {
                Pair<CardScene, Scene> handler = Launcher.loadScene("/gui/card.fxml");
                CardScene cardScene = handler.getKey();
                cardScene.config(b.server, b.listener, b.board, b.stateLabel.getText(), b.ship, b.players);
                cardScene.blueLabel.setText(b.blueLabel.getText());
                cardScene.redLabel.setText(b.redLabel.getText());
                cardScene.greenLabel.setText(b.greenLabel.getText());
                cardScene.yellowLabel.setText(b.yellowLabel.getText());
            }
        }

        /**
         * Manages some of the text of the gui when the game is in the BUILDING and ALIEN_INPUT states.
          * @param b
         */
        public void ready(Building b) {
            if (this == State.Type.BUILDING)
                b.server.setReady().ifPresent(_ -> b.buildingLabel.setVisible(true));
            else if (this == State.Type.ALIEN_INPUT) {
                b.ship.init(b.purple, b.brown);
                b.server.init(b.purple, b.brown).ifPresent(_ -> {
                    b.buildingLabel.setText("ALIENI ASSEGNATI");
                    b.buildingLabel.setVisible(true);
                });
            }
        }

        /**
         * Manages the visible content of the gui when the game is in the CHECKING state.
         * @param s
         */
        public void apply(CardScene s) {
            if (this == State.Type.CHECKING) {
                for (StackPane[] stackPane : s.stackPanes)
                    for (StackPane pane : stackPane)
                        if (pane != null)
                            pane.setVisible(false);

                s.images.forEach((c, view) -> {
                    view.setOnDragDetected(event -> {
                        if (view.getImage() == null)
                            return;

                        view.setCursor(Cursor.CLOSED_HAND);

                        Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
                        ClipboardContent content = new ClipboardContent();
                        content.putString(c.toString());
                        db.setContent(content);
                        db.setDragView(Launcher.getRotatedImage(view.getImage(), 0));
                        event.consume();
                    });

                    view.setOnDragDone(event -> {
                        event.consume();
                        if (!s.dragSuccess.get())
                            return;
                        s.shipPane.getChildren().remove(s.stackPanes[c.x()][c.y()]);
                        s.shipPane.getChildren().remove(view);
                    });

                    view.setOnMousePressed(event -> {
                        view.setCursor(Cursor.CLOSED_HAND);
                        event.consume();
                    });

                    view.setOnMouseEntered(_ -> {
                        view.setCursor(Cursor.OPEN_HAND);
                    });
                    view.setOnMouseExited(_ -> {
                        view.setCursor(Cursor.DEFAULT);
                    });
                });

                s.drawErrors();
            }
        }
    }

    public Type prev;
    public Type curr;
    private final Model m;
    public transient BiConsumer<Model, Type> notify;
    public transient ExecutorService executor;

    public State(Type curr, BiConsumer<Model, Type> notify, Model m) {
        prev = null;
        this.curr = curr;
        this.m = m;
        this.notify = notify;
        executor = Executors.newSingleThreadExecutor();
    }

    public void setNotify(BiConsumer<Model, Type> notify) {
        this.notify = notify;
    }

    public void next(Type next) {
        prev = curr;
        curr = next;
        executor.execute(() -> notify.accept(m, next));
    }

    public Type get() {
        return curr;
    }

    public Type getPrev() {
        return prev;
    }
}
