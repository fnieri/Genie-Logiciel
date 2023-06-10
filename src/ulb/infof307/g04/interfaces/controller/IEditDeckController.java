package ulb.infof307.g04.interfaces.controller;

import java.util.List;
import java.util.Set;

import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.interfaces.models.ICard;

public interface IEditDeckController extends IController {
    /**
     * Adds a card to the deck.
     *
     * @param question   The question for the new card.
     * @param answer     The answer for the new card.
     * @param answerType The type of the card
     */
    void addDeckCard(String question, String answer, CardType questionType, CardType answerType);

    /**
     * Edits an existing card in the deck.
     *
     * @param cardId   The ID of the card to be edited.
     * @param question The new question for the card.
     * @param answer   The new answer for the card.
     * @param questionType the type of the question
     * @param answerType the type of the answer
     */
    void editDeckCard(int cardId, String question, String answer, CardType questionType, CardType answerType);

    /**
     * Deletes a card from the deck.
     *
     * @param cardId The ID of the card to be deleted.
     */
    void deleteDeckCard(int cardId);

    /**
     * Updates the title of the deck.
     *
     * @param title The new title for the deck.
     */
    void editDeckTitle(String title);

    /**
     * Updates the tags of the deck.
     *
     * @param tags The new tags for the deck.
     */
    void editDeckTags(Set<String> category);

    String getDeckTitle();

    /**
     * gets the categories (tags) of the deck.
     *
     * @return the categories (tags) of the deck.
     */
    List<String> getDeckCategories();

    /**
     * Gets a card from the deck by its ID.
     *
     * @param cardId The ID of the card to get.
     * @return The card with the specified ID.
     * @throws ApiException if the card can't be fetched
     */
    ICard getCard(int cardId) throws ApiException;
    
    /**
     * Switches the view to the previous page (Profile).
     */
    void goBack();
}