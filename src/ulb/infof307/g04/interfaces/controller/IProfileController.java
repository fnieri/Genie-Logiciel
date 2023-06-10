package ulb.infof307.g04.interfaces.controller;

import java.io.File;
import org.json.JSONObject;

import javafx.collections.ObservableList;
import ulb.infof307.g04.models.Deck;

public interface IProfileController extends IController {
    /**
     * Create a new deck for the current user.
     *
     * @param name - the name of the Deck
     * @param theme - the tags of the deck
     */
    void createDeck(String name, String theme);

    /**
     * Remove the deck with the given id.
     *
     * @param deckId - the id of the deck to be removed.
     */
    void deleteDeck(int deckId);

    /**
     * Filters the deck list based on the selected tags.
     *
     * @param tags the tags that the user chose for the deck list
     */
    void filterDecks(ObservableList<String> tags);

    /**
     * Actions to undertake when a review request is sent from the view.
     *
     * @param deckID - the id of the deck to review
     */
    void onDeckReview(int deckID);

    /**
     * Actions to undertake when an edit request is sent from the view.
     *
     * @param deckID - the id of the deck to edit
     */
    void onDeckEdit(int deckID);

    /**
     * Actions to undertake when a free mode  request is sent from the view.
     *
     * @param deckID - the id of the deck to test in free mode
     */
    void onFreeMode(int deckID);

    /**
     * Actions to undertake when a study request is sent from the view.
     *
     * @param deckID - the id of the deck to study
     */
    void onDeckStudy(int deckID);

    /**
     * Actions to undertake when a share request is sent from the view.
     *
     * @param deckID - the id of the deck to share
     */
    void onDeckShare(int deckID);

    /**
     * Actions to undertake when a PDF download request is sent from the view.
     *
     * @param deckID - the id of the deck to download
     */
    void downloadDeck(int deckID);

    /**
     * Export a JSONObject of the deck
     *
     * @param deckID
     * @return JSONObject deck
     */
    JSONObject exportDeck(int deckID);

    /**
     * Import a deck from a json file
     *
     * @param jsonDeck - the JSONObject to import
     */
    void importDeck(JSONObject jsonDeck);

    /**
     * Write a to .json file
     *
     * @param jsonDeck - the deck to write
     * @param selected - the file to write to 
     */
    void writeJSONFile(JSONObject jsonDeck, File selected);

    /**
     * Reads a json file for import.
     *
     * @param selected - the file to import
     * @return
     */
    JSONObject readJSONFile(File selected);

    Deck getDeckByID(int deckId);

}