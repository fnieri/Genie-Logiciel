package ulb.infof307.g04.exceptions.api;

/**
 * Thrown when a card couldn't be created.
 *
 * @see ulb.infof307.g04.services.CardService
 */
public class CardCreationException extends ApiException {
    public CardCreationException(String message) {
        super("Card creation error." + message);
    }
}