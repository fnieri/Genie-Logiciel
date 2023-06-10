package ulb.infof307.g04.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ulb.infof307.g04.models.Tag;
import ulb.infof307.g04.controllers.EditDeckController;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.interfaces.controller.IEditDeckController;
import ulb.infof307.g04.interfaces.view.IAppViewController;
import ulb.infof307.g04.interfaces.view.IEditDeckViewController;
import ulb.infof307.g04.widgets.components.EditDeckCardComponent;
import ulb.infof307.g04.widgets.dialogs.EditCardDialog;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import ulb.infof307.g04.enums.CardType;


/**
 * View controller for the Edit deck page (declared in editDeck.fxml)
 */
public class EditDeckViewController extends AbstractViewController implements Initializable, IEditDeckViewController {
    public ChoiceBox<java.lang.constant.Constable> choiceBoxCardType;
    private final IEditDeckController controller;

    @FXML VBox cardList;
    @FXML private Text categoryText;
    @FXML Text deckTitle;

    public EditDeckViewController(IAppViewController app, HashMap<String, Object> params) {
        super(app);
        Object deckID = params.get("deckID");
        this.controller = new EditDeckController(this, (int) deckID);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller.initialize();
    }

    @Override
    public void setupCardTypes(List<String> types) {
        choiceBoxCardType.getItems().addAll(types);
    }

    /**
     * Opens the popup that asks for a new title for the deck.
     *
     */
    @FXML
    private void showEditTitleDialog() {
        Optional<String> newTitle = this.showTextInputDialog(null, "Edit Title", null, controller.getDeckTitle());
        newTitle.ifPresent(controller::editDeckTitle);
    }

    /**
     * Opens the popup that asks for confirmation to delete a card.
     *
     */
    private void showDeleteCardDialog(int cardId) {
        controller.deleteDeckCard(cardId);
    }

    /**
     * Opens the popup that asks for new details about a selected card.
     *
     */
    private void showEditCardDialog(int cardId) {
        try {
            ICard cardToEdit = controller.getCard(cardId);
            EditCardDialog dialog = new EditCardDialog(cardToEdit);
            Optional<ICard> updatedCard = dialog.showAndWait();
            dialog.checkCardLengthValidity();

            updatedCard.ifPresent(card -> controller.editDeckCard(cardId, card.getQuestion(), card.getAnswer(), card.getQuestionType(), card.getAnswerType()));
        } catch (ApiException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error while editing card", e.getMessage());
        }
    }

    /**
     * Opens the popup that asks for a new category for the deck.
     *
     */
    public void showEditCategoryDialog() {
        Optional<String> result = this.showTextInputDialog(
            "Edit Tags", 
            "Enter the new tag names (comma-separated)", 
            "Tags:", 
            String.join(", ", controller.getDeckCategories()));

        result.ifPresent(this::processCategories);
    }

    private void processCategories(String categories) {
        controller.editDeckTags(Tag.getTagsFromCommaSeparatedString(categories));
    }


    /**
     * Handles to button to go back to the profile page
     *
     */
    public void handleBack() {
        controller.goBack();
    }

    @Override
    public void setDeckCategories(List<String> categories) {
        categoryText.setText(String.join(", ", categories));
    }

    @Override
    public void setDeckTitle(String title) {
        deckTitle.setText(title);
    }

    @Override
    public void setDeckCards(List<ICard> cards) {
        cardList.getChildren().clear();
        for (ICard card : cards) {
            // NOTE: AbstractCard.fromOldCardModel is temporary. When services use
            // the AbstractCard class, this can be remove
            EditDeckCardComponent cardComponent;
            try {
                cardComponent = new EditDeckCardComponent(card);
            }
            catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error while loading card, contact the developers", e.getMessage());
                return;
            }
            cardComponent.onCardEdit(this::showEditCardDialog);
            cardComponent.onCardDelete(this::showDeleteCardDialog);
            cardList.getChildren().addAll(cardComponent);
        }
    }

    /**
     * handle the card type choice box
     */
    @FXML
    public void handleCardType() {
        if (choiceBoxCardType.getValue() != "Add card") {
            CardType cardType = CardType.fromViewString((String) choiceBoxCardType.getValue());
            EditCardDialog dialog = new EditCardDialog(cardType);
    
            Optional<ICard> newCard = dialog.showAndWait();
            newCard.ifPresent(card -> controller.addDeckCard(card.getQuestion(), card.getAnswer(), card.getQuestionType(), card.getAnswerType()));
            choiceBoxCardType.setValue("Add card");
        }
    }
}
