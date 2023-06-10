package ulb.infof307.g04.services;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.HashMap;

import ulb.infof307.g04.AppState;
import ulb.infof307.g04.exceptions.api.ApiException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ulb.infof307.g04.interfaces.services.IStoreDeckService;
import ulb.infof307.g04.models.StoreDeck;

/**
 * This class is used to communicate with the API for the StoreDeck model.
 * It is used to create, update, delete and fetch decks published on the store.
 * It also allows to export and import decks.
 * @see StoreDeck
 */
public class StoreDeckService extends AbstractAPIService implements IStoreDeckService {
    private final AppState appState;

    public StoreDeckService(String baseUri) {
        super(baseUri);
        appState = AppState.getInstance();
    }
    public StoreDeckService(String baseUri, HttpClient client) {
        super(baseUri, client);
        appState = AppState.getInstance();
    }

    /**
     * Fetches the store decks and returns them 
     * as a list with two sublists (most popular and catalog).
     *
     * @throws ApiException
     */
    @Override
    public List<List<StoreDeck>> getStoreDecks() throws ApiException {
            HttpResponse<String> response = sendFetchAllObjectRequest("/api/quiz/store/", appState.getAuthToken());
            handleExceptions(response);
            List<List<StoreDeck>> storeDecks = new ArrayList<>();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode deckJson;

            try {
                deckJson = objectMapper.readTree(response.body());
            } catch (JsonProcessingException e) {
                throw new ApiException(e.getMessage());
            }

            storeDecks.add(parseStoreDecks(deckJson, "populars"));
            storeDecks.add(parseStoreDecks(deckJson, "decks"));

            return storeDecks;
    }

    /**
     * Parses JSON store decks received from an API call.
     *
     * @param deckJson - the store deck list from the API response
     * @param type - a string indicating which type of decks must be parsed (most 
     *               popular or catalog)
     * @return all decks from the in a converted model format
     */
    private List<StoreDeck> parseStoreDecks(JsonNode deckJson, String type) {
        ArrayNode decksResponse = (ArrayNode) deckJson.get(type);
        List<StoreDeck> decks = new ArrayList<>();
        for (JsonNode node : decksResponse) {
             decks.add(parseStoreDeck(node));
        }

        return decks;

    }

    /**
     * Shares a user deck onto the store.
     *
     * @param deckId - the id of the user deck to add to the store
     *
     * @throws ApiException
     */
    @Override
    public void shareDeck(int deckId) throws ApiException {
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("deck", deckId);
        HttpResponse<String> response = this.sendCreateObjectRequest(
            "/api/quiz/store/", 
            appState.getAuthToken(), 
            requestBody);

        handleExceptions(response);
    }

    /**
     * Parses a JSON store deck received from an API call.
     *
     * @param node - the JSON node deck to convert
     * @return a new StoreDeck based on the JSON node
     */
    private StoreDeck parseStoreDeck(JsonNode node) {
        JsonNode data = node.get("data");
        String name = data.get("name").asText();
        ArrayNode tags = (ArrayNode) data.get("tags");
        List<String> stringTags = new ArrayList<>();
        for (JsonNode tag : tags) {
            stringTags.add(tag.asText());
        }
        // take only the first author 
        String author = ((ArrayNode) data.get("users")).elements().next().asText();
        int id = node.get("id").asInt();
        int downloads = node.get("downloads").asInt();
        
        return new StoreDeck(id, name, stringTags, author, downloads);
    }

    /**
     * Downloads a store deck into a user's collection
     *
     * @param deckId - the id of the store deck to download
     * @throws ApiException
     */
    @Override
    public void downloadDeck(int deckId) throws ApiException {
        String endPoint = String.format("/api/quiz/store/%d/download/", deckId);
        HttpResponse<String> response = this.sendCreateObjectRequest(endPoint, appState.getAuthToken(), new HashMap<>());
        handleExceptions(response);
        CacheService cacheService = CacheService.getInstance();
        cacheService.invalidate(DeckService.cacheKey);
    }
}