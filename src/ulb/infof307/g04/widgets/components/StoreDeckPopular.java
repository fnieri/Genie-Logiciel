package ulb.infof307.g04.widgets.components;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ulb.infof307.g04.models.StoreDeck;

public class StoreDeckPopular extends VBox implements Initializable {
    @FXML Text deckName;
    @FXML Text deckTags;
    @FXML Text deckAuthor;
    @FXML Text deckDownloads;

        /**
     * Functional interface to add a handler stored in another class, here from StoreViewController
     */
    @FunctionalInterface
    public interface StoreDeckPopularEventHandler<T> {
        void handle(T event);
    }

    private StoreDeck deck;
    private StoreDeckPopularEventHandler<Integer> onDownloadHandler;

    public StoreDeckPopular(StoreDeck deck) throws IOException {
        this.deck = deck;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ulb/infof307/g04/ui/components/storeDeckPopular/layout.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        fxmlLoader.load();

        this.getStyleClass().add("section-store-box");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.refresh();
    }

    private void refresh() {
        if (this.deck != null) {
            this.deckName.setText(this.deck.getName());
            this.deckTags.setText(String.format("Tags: %s", this.deck.getTags()));
            this.deckAuthor.setText(String.format("Author: %s", this.deck.getAuthor()));
            this.deckDownloads.setText(String.format("Downloads: %s", this.deck.getDownloads()));
        }
    }

    /**
     * Stores a specific handler as an attribute to use as callback for a specific
     * FXML component
     *
     * @param handler - the handler to store 
     */
    public void onDownloadAction(StoreDeckPopularEventHandler<Integer> handler) {
        this.onDownloadHandler = handler;
    }

    /**
     * Callback for the download store deck button
     *
     * @param event - the event that triggered the callback
     */
    @FXML
    public void handleDownloadButton(ActionEvent event){
        if (this.onDownloadHandler != null) {
            this.onDownloadHandler.handle(this.deck.getId());
        }
    }
}
