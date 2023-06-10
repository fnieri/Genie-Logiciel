/**
 * Controller for the EditDeck view (declared in editDeck.fmxl).
 *
 * @see ulb.infof307.g04.view.EditDeckViewController
 */
package ulb.infof307.g04.controllers;

import javafx.scene.control.Alert;
import ulb.infof307.g04.exceptions.*;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.enums.EPages;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.factory.CardServiceFactory;
import ulb.infof307.g04.factory.DeckServiceFactory;
import ulb.infof307.g04.interfaces.controller.IEditDeckController;
import ulb.infof307.g04.interfaces.services.ICardService;
import ulb.infof307.g04.interfaces.services.IDeckService;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.interfaces.view.IEditDeckViewController;
import ulb.infof307.g04.models.Deck;

import java.util.*;
import java.util.concurrent.CompletionException;

/**
 * Controller for the EditDeck view (declared in editDeck.fmxl).
 *
 * @see ulb.infof307.g04.view.EditDeckViewController
 */
public class EditDeckController extends AbstractController implements IEditDeckController {
    private final IDeckService deckService = DeckServiceFactory.getInstance();
    private final ICardService cardService = CardServiceFactory.getInstance();
    private final int deckID;

    private Deck deck;
    private final IEditDeckViewController view;

    /**
     * Constructor for the EditDeckController.
     *
     * @param view   The view associated with the controller.
     * @param deckID The ID of the deck to be edited.
     */
    public EditDeckController(IEditDeckViewController view, int deckID) {
        super(view);
        this.view = view;
        this.deckID = deckID;
    }

    /**
     * Initializes the controller by refreshing the deck.
     */
    @Override
    public void initialize() {
        super.initialize();
        this.refreshDeck();
        this.view.setupCardTypes(Arrays.asList(
                CardType.OAR.toViewString(),
                CardType.BLNK.toViewString(),
                CardType.MCQ.toViewString()
        ));
    }

    /**
     * Refreshes the deck information displayed in the view.
     */
    private void refreshDeck() {
        try {
            deck = deckService.getDeck(deckID);
            view.setDeckTitle(getDeckTitle());
            view.setDeckCategories(getDeckCategories());
            view.setDeckCards(deck.getCards());
        } catch (ApiException e) {
            showAlertForException(e, "Failed to fetch deck");
        }

        runAsync(() -> {
                    try {
                        return deckService.getDeck(deckID);
                    } catch (ApiException e) {
                        throw new CompletionException(e);
                    }
                },
                deck -> {
                    view.setDeckTitle(getDeckTitle());
                    view.setDeckCategories(getDeckCategories());
                    view.setDeckCards(deck.getCards());
                }
        );
    }

    @Override
    public void addDeckCard(String question, String answer, CardType questionType, CardType answerType) {
        if (question.isEmpty() || answer.isEmpty()) {
            view.showAlert(Alert.AlertType.ERROR, "Card Creation Error", "Question field or Answer field is empty", "Add a question and an answer");
            return;
        }
        if (answer.length() > 250) {
            view.showAlert(Alert.AlertType.ERROR, "Card Creation Error", "Answer field is too long", "Answer field must be less than 250 characters");
            return;
        }
        runAsync(() ->
                {
                    try {
                        return cardService.createCard(deck.getId(), question, questionType, answer, answerType);
                    } catch (ApiException e) {
                        throw new CompletionException(e);
                    }
                },
                card -> {
                    this.refreshDeck();
                }
        );
    }

    @Override
    public void editDeckCard(int cardId, String question, String answer, CardType questionType, CardType answerType) {
        try {
            this.checkCardLengthValidity(question, answer);

        } catch (CardLengthException e) {
            view.showAlert(Alert.AlertType.INFORMATION, "Too much caracters", e.getMessage() + " has too much caracters", "");
        }

        runAsync(() -> {
                    try {
                        cardService.updateCardQuestion(cardId, question);
                        cardService.updateCardAnswer(cardId, answer);
                        cardService.updateCardType(cardId, questionType, answerType);
                    } catch (ApiException e) {
                        throw new CompletionException(e);
                    }
                }, this::refreshDeck,
                "Updating question",
                "Failed to update question"
        );

        this.refreshDeck();
    }

    @Override
    public void deleteDeckCard(int cardId) {
        runAsync(() -> {
                    try {
                        cardService.deleteCard(cardId);
                    } catch (ApiException e) {
                        throw new CompletionException(e);
                    }
                }, this::refreshDeck,
                "Deleting card", "Failed to delete card");
    }

    @Override
    public void editDeckTitle(String title) {
        runAsync(() -> {
                    try {
                        deckService.updateDeckName(deck.getId(), title);
                    } catch (ApiException e) {
                        throw new CompletionException(e);
                    }
                }, this::refreshDeck,
                "Updating deck",
                "Failed to update title"
        );
    }

    @Override
    public void editDeckTags(Set<String> tags) {
        runAsync(() -> {
                    try {
                        deckService.updateDeckTags(deck.getId(), tags.stream().toList());
                    } catch (ApiException e) {
                        throw new CompletionException(e);
                    }
                }, this::refreshDeck,
                "Updating deck", "Failed to update tags"
        );
    }

    @Override
    public String getDeckTitle() {
        return deck.getName();
    }

    @Override
    public List<String> getDeckCategories() {
        return deck.getTags();
    }

    @Override
    public ICard getCard(int cardId) throws ApiException {
        return cardService.getCard(cardId);
    }

    @Override
    public void goBack() {
        view.switchView(EPages.PROFILE);
    }

    /**
     * Checks if the length of the question and answer of a card are valid.
     *
     * @param question The question to be checked.
     * @param answer   The answer to be checked.
     * @throws CardLengthException if the length of either the question or the answer is invalid.
     */
    private void checkCardLengthValidity(String question, String answer) throws CardLengthException {
        if (question.length() > 250) {
            throw new CardLengthException("Question");
        }
        if (answer.length() > 250) {
            throw new CardLengthException("Answer");
        }
    }
}