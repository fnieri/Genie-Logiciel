package ulb.infof307.g04.interfaces.services;

import java.util.List;

import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.models.StoreDeck;

public interface IStoreDeckService {
    List<List<StoreDeck>> getStoreDecks() throws ApiException;
    void shareDeck(int deckId) throws ApiException;
    void downloadDeck(int deckId) throws ApiException;
}