package ulb.infof307.g04.interfaces.services;

import java.util.List;

import org.json.JSONObject;

import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.models.Deck;

public interface IDeckService {
    int createDeck(String name, List<String> tags) throws ApiException;
    Deck getDeck(int deckId) throws ApiException;
    List<Deck> getDecks() throws ApiException;
    void updateDeckName(int deckId, String name) throws ApiException;
    void updateDeckTags(int deckId, List<String> tags) throws ApiException;
    void deleteDeck(int deckId) throws ApiException;
    JSONObject exportDeck(int deckID) throws ApiException;
    void importDeck(JSONObject jsonDeck, int userId) throws ApiException;
    JSONObject getUserDeckStatistics(int deckId) throws ApiException;
}
