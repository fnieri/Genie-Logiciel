package ulb.infof307.g04.widgets.components;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import ulb.infof307.g04.models.StoreDeck;

/**
 * Custom component for a store deck shown in the catalog.
 * The related FXML implementation is under the folder /components in resources
 */
public class StoreDeckCatalog extends HBox implements Initializable {
    @FXML
    Text deckName;
    @FXML
    Text deckTags;
    @FXML
    Text deckAuthor;
    @FXML
    Text deckDownloads;


    /**
     * Functional interface to add a handler stored in another class, here from StoreViewController
     */
    @FunctionalInterface
    public interface StoreDeckCatalogEventHandler<T> {
        void handle(T event);
    }

	private StoreDeck deck;
    private StoreDeckCatalogEventHandler<Integer> downloadHandler;


    public StoreDeckCatalog(StoreDeck deck) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ulb/infof307/g04/ui/components/storeDeckCatalog/layout.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        this.deck = deck;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.refresh();
    }

    public void refresh() {
        if (deck != null) {
            this.deckName.setText(this.deck.getName());
            this.deckTags.setText("Tags: " + this.deck.getTags()); 
            this.deckAuthor.setText("Author: " + this.deck.getAuthor()); 
            this.deckDownloads.setText("Downloads: " + this.deck.getDownloads()); 
        }
    }

    /**
     * Stores a specific handler as an attribute to use as callback for a specific
     * FXML component
     *
     * @param handler - the handler to store 
     */
    public void setDownloadHandler(StoreDeckCatalogEventHandler<Integer> handler) {
        this.downloadHandler = handler;
    }

    /**
     * Callback for the download store deck button
     *
     * @param event - the event that triggered the callback
     */
    @FXML
    public void onDownload(ActionEvent event){
        if (downloadHandler != null) {
            downloadHandler.handle(deck.getId());
        }
    }
}