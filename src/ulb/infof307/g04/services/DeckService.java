/**
 * This class is used to communicate with the API for the Deck model.
 * It is used to create, update, delete and fetch decks.
 * It also allows to export and import decks.
 *
 * @see Deck
 */

package ulb.infof307.g04.services;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.json.JSONArray;
import org.json.JSONObject;
import ulb.infof307.g04.AppState;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.exceptions.api.BadRequestException;
import ulb.infof307.g04.factory.CardServiceFactory;
import ulb.infof307.g04.factory.TagServiceFactory;
import ulb.infof307.g04.interfaces.services.ICardService;
import ulb.infof307.g04.interfaces.services.IDeckService;
import ulb.infof307.g04.interfaces.services.ITagService;
import ulb.infof307.g04.parsers.DeckParser;
import ulb.infof307.g04.models.Deck;
import ulb.infof307.g04.utils.JSONWriter;


/**
 * This class is used to communicate with the API for the Deck model.
 * It is used to create, update, delete and fetch decks.
 * It also allows to export and import decks.
 * @see Deck
 */
public class DeckService extends AbstractAPIService implements IDeckService {
    private final CacheService cacheService = CacheService.getInstance();

    // must be public to allow other services to delete decks cache
    public static final String cacheKey = "api.deck";
    private static final String decksEndpoint = "/api/quiz/decks/";
    private final ITagService tagService = TagServiceFactory.getInstance();
    private final ICardService cardService = CardServiceFactory.getInstance();
    private final JSONWriter jsonWriter = new JSONWriter();

    /**
     * Constructor for DeckService class.
     *
     * @param baseUri The base URI of the API service.
     */
    public DeckService(String baseUri) {
        super(baseUri);
    }
    public DeckService(String baseUri, HttpClient client) {
        super(baseUri, client);
        this.state = AppState.getInstance();
    }

    /**
     * This inner class represents the request body for creating a deck.
     */
    private AppState state = AppState.getInstance();

    /**
     * This inner class represents the request body for creating a deck.
     */
    @JsonSerialize
    private record CreateDeckRequest(String name, List<String> users, List<String> tags) {
    }

    /**
     * Creates a deck with the specified name and tags.
     *
     * @param name The name of the deck.
     * @param tags The tags of the deck.
     * @return The ID of the created deck.
     * @throws ApiException if there is an error while creating the deck.
     */
    @Override
    public int createDeck(String name, List<String> tags) throws ApiException {
        try {
            // NOTE: Make sure tags exists
            List<String> existingTagsNames = tagService.getTagNames();

            for (String tagName : tags) {
                if (!existingTagsNames.contains(tagName)) {
                    try {
                        tagService.createTag(tagName);
                    } catch (BadRequestException e) {
                        // tag probably already exists, but the cache is not updated
                        // to make sure, invalidate the cache entry
                        cacheService.invalidate(TagService.cacheKey);
                        existingTagsNames = tagService.getTagNames();
                        if (!existingTagsNames.contains(tagName)) {
                            throw new ApiException(e.getMessage());
                        }
                    }
                }
            }

            HttpResponse<String> response = this.sendCreateObjectRequest(
                    decksEndpoint,
                    this.state.getAuthToken(),
                    new CreateDeckRequest(name, Collections.singletonList(this.state.getUser().username()), tags));

            handleExceptions(response);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.body());
            cacheService.invalidate(cacheKey);
            return node.get(JSONWriter.ID).asInt();
        } catch (IOException e) {
            throw new ApiException(e.getMessage());
        }
    }

    /**
     * Creates a deck with the specified deckId.
     *
     * @param deckId The id of the deck.
     * @return The ID of the created deck.
     * @throws ApiException if there is an error while creating the deck.
     */
    @Override
    public Deck getDeck(int deckId) throws ApiException {
        try {

            List<Deck> decks = getDecks();


            for (Deck deck : decks) {
                if (deck.getId() == deckId) {
                    return deck;
                }
            }

            // if the deck is not in the cache, fetch it from the API

            HttpResponse<String> response = this.sendFetchOneObjectRequest(
                    decksEndpoint,
                    this.state.getAuthToken(),
                    deckId);

            handleExceptions(response);

            // if there was no exception, the deck exists and our cache is outdated
            cacheService.invalidate(cacheKey);
            DeckParser parser = new DeckParser();
            return parser.parse(response.body());
        } catch (IOException e) {
            throw new ApiException(e.getMessage());
        }
    }

    @Override
    public List<Deck> getDecks() throws ApiException {
        try {
            String responseBody = cacheService.get(cacheKey);
            if (responseBody == null) {

                HttpResponse<String> response = this.sendFetchAllObjectRequest(
                        decksEndpoint,
                        this.state.getAuthToken());

                handleExceptions(response);
                responseBody = response.body();
                cacheService.put(cacheKey, responseBody);

            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(responseBody);
            List<Deck> decks = new ArrayList<>();
            for (JsonNode deckNode : node) {
                DeckParser parser = new DeckParser();
                Deck deck = parser.parse(deckNode.toString());
                decks.add(deck);
            }
            return decks;
        } catch (IOException e) {
            throw new ApiException(e.getMessage());
        }
    }

    /**
     * updates the deck with the specified deckId and name.
     * @param deckId
     * @param name
     * @throws ApiException
     */
    @Override
    public void updateDeckName(int deckId, String name) throws ApiException {
        HashMap<String, Object> request = new HashMap<>();
        request.put(JSONWriter.name, name);
        request.put(JSONWriter.users, Collections.singletonList(state.getUser().username()));
        HttpResponse<String> response = this.sendPatchObjectRequest(
                decksEndpoint,
                this.state.getAuthToken(),
                deckId,
                request);
        handleExceptions(response);
        cacheService.invalidate(cacheKey);
    }

    /**
     * updates the deck with the specified deckId and tags.
     * @param deckId
     * @param tags
     * @throws ApiException
     */
    @Override
    public void updateDeckTags(int deckId, List<String> tags) throws ApiException {

        // NOTE: Make sure tags exists
        for (String tagName : tags) {
            tagService.createTag(tagName);
        }
        HashMap<String, Object> request = new HashMap<>();
        request.put(JSONWriter.tags, tags);
        request.put(JSONWriter.users, Collections.singletonList(state.getUser().username()));
        HttpResponse<String> response = this.sendPatchObjectRequest(
                decksEndpoint,
                this.state.getAuthToken(),
                deckId,
                request);

        handleExceptions(response);
        cacheService.invalidate(cacheKey);
    }

    /**
     * deletes the deck with the specified deckId.
     * @param deckId
     * @throws ApiException
     */
    @Override
    public void deleteDeck(int deckId) throws ApiException {
        HttpResponse<String> response = this.sendDeleteObjectRequest(
                decksEndpoint,
                this.state.getAuthToken(),
                deckId);

        handleExceptions(response);
        cacheService.invalidate(cacheKey);
    }

    /**
     * exports the deck with the specified deckId.
     * @param deckId
     * @return
     * @throws ApiException
     */
    @Override
    public JSONObject exportDeck(int deckId) throws ApiException {
        Deck deck = getDeck(deckId);
        return jsonWriter.getDeckAsJSONObject(deck);
    }

    /**
     * imports the deck with the jsonDeck and userId.
     * @param jsonDeck
     * @param userId
     * @throws ApiException
     */
    @Override
    public void importDeck(JSONObject jsonDeck, int userId) throws ApiException {
        String title = jsonDeck.getString(JSONWriter.name);

        List<String> tags = new ArrayList<>();
        JSONArray jsonArray = jsonDeck.getJSONArray(JSONWriter.tags);
        for (int i = 0; i < jsonArray.length(); i++) {
            tags.add(jsonArray.getString(i));
        }

        JSONArray cards = jsonDeck.getJSONArray(JSONWriter.cards);
        int deckId = this.createDeck(title, tags);

        String question;
        String questionType;
        String answer;
        String answerType;
        for (int i = 0; i < cards.length(); i++) {
            JSONObject card = cards.getJSONObject(i);
            question = card.getString(JSONWriter.question);
            answer = card.getString(JSONWriter.answer);
            questionType = card.getString(JSONWriter.questionType);
            answerType = card.getString(JSONWriter.answerType);
            cardService.createCard(deckId, question, CardType.fromString(questionType), answer, CardType.fromString(answerType));
        }
    }

    @Override
    public JSONObject getUserDeckStatistics(int deckId) throws ApiException {
        HttpResponse<String> response = this.sendFetchOneObjectRequest(
                decksEndpoint,
                this.state.getAuthToken(),
                deckId);


        handleExceptions(response);

        return new JSONObject(response.body());
    }
}