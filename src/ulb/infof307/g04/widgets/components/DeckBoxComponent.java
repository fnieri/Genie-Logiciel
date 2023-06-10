package ulb.infof307.g04.widgets.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import ulb.infof307.g04.models.Deck;

import java.io.IOException;

public class DeckBoxComponent extends HBox {

    @FXML Text deckNameText;
    @FXML Text tagsText;

    public interface DeckBoxComponentHandler<T> {
        void handle(T event);
    }

    private Deck deck;
    private DeckBoxComponentHandler<Integer> onStudyClickHandler;
    private DeckBoxComponentHandler<Integer> onFreeClickHandler;
    private DeckBoxComponentHandler<Integer> onReviewClickHandler;
    private DeckBoxComponentHandler<Integer> onEditClickHandler;
    private DeckBoxComponentHandler<Deck> onDeleteClickHandler;
    private DeckBoxComponentHandler<Integer> onShareClickHandler;
    private DeckBoxComponentHandler<Integer> onExportPDFClickHandler;
    private DeckBoxComponentHandler<Integer> onExportJSONClickHandler;

    public DeckBoxComponent(Deck deck) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ulb/infof307/g04/ui/components/deckBox/layout.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(DeckBoxComponent.this);
        this.setSpacing(50);
        this.getStyleClass().add("section-line");
        this.setAlignment(javafx.geometry.Pos.CENTER);
        fxmlLoader.load();
        this.deck = deck;
        this.refresh();
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public void setStudyHandler(DeckBoxComponentHandler<Integer> handler) {
        this.onStudyClickHandler = handler;
    }

    public void setFreeHandler(DeckBoxComponentHandler<Integer> handler) {
        this.onFreeClickHandler = handler;
    }

    public void setReviewHandler(DeckBoxComponentHandler<Integer> handler) {
        this.onReviewClickHandler = handler;
    }

    public void setEditHandler(DeckBoxComponentHandler<Integer> handler) {
        this.onEditClickHandler = handler;
    }

    public void setDeleteHandler(DeckBoxComponentHandler<Deck> handler) {
        this.onDeleteClickHandler = handler;
    }

    public void setShareHandler(DeckBoxComponentHandler<Integer> handler) {
        this.onShareClickHandler = handler;
    }

    public void setExportPDFHandler(DeckBoxComponentHandler<Integer> handler) {
        this.onExportPDFClickHandler = handler;
    }

    public void setExportJSONHandler(DeckBoxComponentHandler<Integer> handler) {
        this.onExportJSONClickHandler = handler;
    }

    private void refresh() {
        if (deck != null) {
            deckNameText.setText(deck.getName());
            tagsText.setText(String.format("Tags: %s", deck.getTags()));
        }
    }

    /**
     * @note Although this method is not used in intellij, it is used in fxml
     */
    public void onDeckShare() {
        if (onStudyClickHandler != null) {
            onShareClickHandler.handle(deck.getId());
        }
    }

    /**
     * @note Although this method is not used in intellij, it is used in fxml
     */
    public void onDeckStudy() {
        if (onStudyClickHandler != null) {
            onStudyClickHandler.handle(deck.getId());
        }
    }

    /**
     * @note Although this method is not used in intellij, it is used in fxml
     */
    public void onDeckFree() {
        if (onFreeClickHandler != null) {
            onFreeClickHandler.handle(deck.getId());
        }
    }

    /**
     * @note Although this method is not used in intellij, it is used in fxml
     */
    public void onDeckReview() {
        if (onReviewClickHandler != null) {
            onReviewClickHandler.handle(deck.getId());
        }
    }

    /**
     * @note Although this method is not used in intellij, it is used in fxml
     */
    public void onDeckEdit() {
        if (onEditClickHandler != null) {
            onEditClickHandler.handle(deck.getId());
        }
    }

    /**
     * @note Although this method is not used in intellij, it is used in fxml
     */
    public void onDeckJsonExport() {
        if (onExportJSONClickHandler != null) {
            onExportJSONClickHandler.handle(deck.getId());
        }
    }

    /**
     * @note Although this method is not used in intellij, it is used in fxml
     */
    public void OnDeckPdfExport() {
        if (onExportPDFClickHandler != null) {
            onExportPDFClickHandler.handle(deck.getId());
        }
    }

    /**
     * @note Although this method is not used in intellij, it is used in fxml
     */
    public void handleDeckDeletion() {
        if (onDeleteClickHandler != null) {
            onDeleteClickHandler.handle(deck);
        }
    }
}
