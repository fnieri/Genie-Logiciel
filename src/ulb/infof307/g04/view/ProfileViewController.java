package ulb.infof307.g04.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.controlsfx.control.CheckComboBox;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.json.JSONObject;

import ulb.infof307.g04.controllers.ProfileController;
import ulb.infof307.g04.interfaces.controller.IProfileController;
import ulb.infof307.g04.interfaces.view.IAppViewController;
import ulb.infof307.g04.interfaces.view.IProfileViewController;
import ulb.infof307.g04.models.Deck;
import ulb.infof307.g04.widgets.components.DeckBoxComponent;
import ulb.infof307.g04.widgets.dialogs.DeckCreationDialog;
import ulb.infof307.g04.widgets.dialogs.DeckDeletionDialog;

/**
 * View controller for the user profile view.
 */
public class ProfileViewController extends AbstractViewController implements Initializable, IProfileViewController {
    private final IProfileController controller;

    @FXML
    VBox deckList;
    @FXML
    HBox optionsBox;

    @FXML CheckComboBox<String> tagFilter;

    private final ListChangeListener<String> tagFilterListener;

    public ProfileViewController(IAppViewController app, HashMap<String, Object> params) {
        super(app);
        controller = new ProfileController(this);
        tagFilterListener = new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                controller.filterDecks(tagFilter.getCheckModel().getCheckedItems());
            }
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initTagFilter();
        controller.initialize();
    }

    /**
     * Initializes the dynamic tag filter
     */
    private void initTagFilter() {
        this.tagFilter = new CheckComboBox<>();
        this.optionsBox.getChildren().add(this.tagFilter);
        this.tagFilter.getCheckModel().checkAll();
    }

    /**
     * Adds a deck line into the decks widget onto the profile page.
     *
     * @param deck the deck to add to the list
     */
    private void addDeck(Deck deck) {
        DeckBoxComponent deckBox;
        try {
            deckBox = new DeckBoxComponent(deck);
        }
        catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Error while loading deck box, contact the developers", e.getMessage());
            return;
        }
        this.addDeckHandles(deckBox);
        deckList.getChildren().add(deckBox);
    }

    private void addDeckHandles(DeckBoxComponent deckBox) {
        deckBox.setStudyHandler(this::onDeckStudy);
        deckBox.setFreeHandler(this::onDeckFreeMode);
        deckBox.setReviewHandler(this::onDeckReview);
        deckBox.setEditHandler(this::onDeckEdit);
        deckBox.setDeleteHandler(this::handleDeckDeletion);
        deckBox.setShareHandler(this::onDeckShare);
        deckBox.setExportPDFHandler(this::onDeckPdfExport);
        deckBox.setExportJSONHandler(this::onDeckJsonExport);
    }

    private void onDeckPdfExport(int deckId) {
        controller.downloadDeck(deckId);
    }

    /**
     * Callback for when clicking on the review mode button
     */
    private void onDeckReview(int deckID) {
        controller.onDeckReview(deckID);
    }

    /**
     * CallBack when clicking on export button for a deck
     */
    public void onDeckJsonExport(int deckID) {
        JSONObject jsonDeck = controller.exportDeck(deckID);
        Deck deck = controller.getDeckByID(deckID);
        File selected = this.showSaveFileDialog("Browse Files", String.format("%s.json", deck.getName()));
        controller.writeJSONFile(jsonDeck, selected);
    }

    /**
     * Callback for when clicking the Edit button for a deck
     * 
     */
    public void onDeckEdit(int deckID) {
        controller.onDeckEdit(deckID);
    }

    /**
     * Callback for when clicking the Free mode button for a deck
     */
    private void onDeckFreeMode(int deckID) {
        controller.onFreeMode(deckID);
    }

    /**
     * Callback for when clicking the Study mode button for a deck
     */
    private void onDeckStudy(int deckID) {
        controller.onDeckStudy(deckID);
    }

    private void onDeckShare(int deckID) {
        controller.onDeckShare(deckID);
    }

    /**
     * Callback for when clicking the import deck button
     */
    @FXML
    public void handleDeckImport() {
        File selected = this.showOpenFileDialog("Browse Files");
        if (selected != null) {
            JSONObject jsonDeck = controller.readJSONFile(selected);
            controller.importDeck(jsonDeck);
        }
    }

    /**
     * Callback for when clicking the Create deck button

     */
    @FXML
    public void handleDeckCreation() {
        DeckCreationDialog deckCreationDialog = new DeckCreationDialog();
        Optional<ArrayList<String>> nameAndTheme = deckCreationDialog.showAndWait();
        try {
            nameAndTheme.ifPresent(list -> controller.createDeck(list.get(0), list.get(1)));
        }
        catch (IndexOutOfBoundsException ignored) {} //Ignore, user pressed cancel
    }


    /**
     * Callback for when clicking the Delete deck button
     */
    @FXML
    public void handleDeckDeletion(Deck deck) {
        DeckDeletionDialog deckDeletionDialog = new DeckDeletionDialog();
        Optional<ArrayList<String>> name = deckDeletionDialog.showAndWait();
        name.ifPresent(value -> {
            if (!value.isEmpty() && value.get(0).equals(deck.getName())) {
                controller.deleteDeck(deck.getId());
            } else {
                this.showAlert(AlertType.WARNING, "Incorrect Name", "Deck name does not match", null);
            }
        });
    }

    @Override
    public void setDecks(List<Deck> decks) {
        deckList.getChildren().clear();
        for (Deck deck : decks) {
            addDeck(deck);
        }
    }

    @Override
    public void setTags(List<String> tags) {
        this.tagFilter.getCheckModel().getCheckedItems().removeListener(tagFilterListener);
        this.tagFilter.getCheckModel().clearChecks();
        this.tagFilter.getItems().setAll(tags);
        this.tagFilter.getCheckModel().checkAll();
        this.tagFilter.getCheckModel().getCheckedItems().addListener(tagFilterListener);
    }
}