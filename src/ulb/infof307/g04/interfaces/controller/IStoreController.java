package ulb.infof307.g04.interfaces.controller;

public interface IStoreController extends IController {
    /**
     * Initialises the carousel's batch
     */
    void initCarousel();

    /**
     * Determines which decks are showed in the batch in the decks carousel,
     * after clicking on next.
     */
    void setNextPage();

    /**
     * Determines which decks are showed in the batch in the decks carousel,
     * after clicking on previous.
     */
    void setPreviousPage();

    /**
     * Downloads the deck to the user's collection
     *
     * @param deckId - the id of the deck to download
     */
    void downloadDeck(int userData);

    /**
     * Searches for decks in the catalog
     *
     * @param query - the string searched in the catalog
     */
    void catalogSearch(String text);
}