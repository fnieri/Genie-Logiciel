package ulb.infof307.g04.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ulb.infof307.g04.controllers.StoreController;
import ulb.infof307.g04.interfaces.controller.IStoreController;
import ulb.infof307.g04.interfaces.view.IAppViewController;
import ulb.infof307.g04.interfaces.view.IStoreViewController;
import ulb.infof307.g04.models.StoreDeck;
import ulb.infof307.g04.widgets.components.StoreDeckCatalog;
import ulb.infof307.g04.widgets.components.StoreDeckPopular;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * FXML companion to organise the Store view.
 * <p>
 * The store view shows all user-shared decks
 *
 * @see ulb.infof307.g04.controllers.StoreController
 */
public class StoreViewController extends AbstractViewController implements Initializable, IStoreViewController {
    @FXML
    public HBox deckBatch;
    @FXML
    public Button prevBtn;
    @FXML
    public Button nextBtn;

    @FXML
    public VBox deckList;
    @FXML
    public TextField spotlight;

    private IStoreController controller;

    public StoreViewController(IAppViewController app, HashMap<String, Object> params) {
        super(app);
        controller = new StoreController(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controller.initialize();
        spotlight.setOnKeyPressed(this::onSearch);
    }

    /**
     * Draws the decks from the current batch in most popular decks container
     *
     * @param storeDecks - the decks in the current batch
     */
    public void setDeckBatch(List<StoreDeck> storeDecks){
        this.deckBatch.getChildren().clear();
        for (StoreDeck deck : storeDecks) {
            setDeckData(deck);
        }
    }
    
    /**
     * Draws a unique deck in the current carousel batch of popular decks
     *
     * @param storeDeck - the deck to draw 
     */
    public void setDeckData(StoreDeck storeDeck){
        StoreDeckPopular deck;
        try {
            deck = new StoreDeckPopular(storeDeck);
        }
        catch (IOException e){
            showAlert(Alert.AlertType.ERROR, "Error", "Error while loading deck", e.getMessage());
            return;
        }
        deck.onDownloadAction(this::onDownloadDeck);
        this.deckBatch.getChildren().add(deck);
    }

    /**
     * Draws all user-shared decks on the view
     *
     * @param storeDecks - the shared decks to draw
     */
    public void showDeckList(List<StoreDeck> storeDecks){
        deckList.getChildren().clear();

        for (StoreDeck storeDeck : storeDecks) {
            StoreDeckCatalog deck;
            try {
                deck = new StoreDeckCatalog(storeDeck);
            }
            catch (IOException e){
                showAlert(Alert.AlertType.ERROR, "Error", "Error while loading deck", e.getMessage());
                return;
            }
            deck.setDownloadHandler(this::onDownloadDeck);
            deck.refresh();
            this.deckList.getChildren().add(deck);
        }

    }

    /**
     * Callback for when downloading a deck
     *
     */
    public void onDownloadDeck(int deckId){
        this.controller.downloadDeck(deckId); 
    }

    /**
     * Callback for when going to the next batch in the carousel
     *
     * @param actionEvent - the event triggering the callback
     */
    public void handleGotoNext(ActionEvent actionEvent) {
        controller.setNextPage();
    }

    /**
     * Callback for when going to the previous batch in the carousel
     *
     * @param actionEvent - the event triggering the callback
     */
    public void handleGotoPrev(ActionEvent actionEvent) {
        controller.setPreviousPage();
    }

    /**
     * Callback for when the user searches for a deck in the catalog
     * @param keyEvent - the key event triggering the callback
     */
    public void onSearch(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            controller.catalogSearch(this.spotlight.getText());
        }
    }
}