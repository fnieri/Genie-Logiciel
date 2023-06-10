/**
 * This class provides methods for creating, retrieving, updating, and deleting quiz cards via an API service.
 */

package ulb.infof307.g04.services;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.fasterxml.jackson.databind.node.ArrayNode;
import ulb.infof307.g04.AppState;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.exceptions.api.CardCreationException;
import ulb.infof307.g04.interfaces.services.ICardService;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.parsers.CardParser;
import ulb.infof307.g04.utils.JSONWriter;

public class CardService extends AbstractAPIService implements ICardService {

    private static final String cardsEndpoint = "/api/quiz/cards/";
    public static final String cacheKey = "api.card";
    private final CacheService cacheService = CacheService.getInstance();

    /**
     * This inner class represents the request body for creating a quiz card.
     */
    @JsonSerialize
    private record CreateCardRequest(String question, String question_type, String answer, String answer_type,
                                     int deck) {
    }

    /**
     * Constructor for CardService class.
     *
     * @param baseUri The base URI of the API service.
     */
    private final AppState state = AppState.getInstance();

    /**
     * Creates a quiz card with the specified deck ID, question, answer, and card type.
     *
     * @param baseUri The base URI of the API service.
     * @return The ID of the created card.
     * @throws ApiException if there is an error while creating the card.
     */
    public CardService(String baseUri) {
        super(baseUri);
    }

    public CardService(String baseUri, HttpClient client) {
        super(baseUri, client);
    }

    /**
     * Retrieves the quiz card with the specified ID.
     *
     * @param deckId       The ID of the deck to which the card belongs.
     * @param question     The question of the card.
     * @param questionType The type of the question.
     * @param answer       The answer of the card.
     * @param answerType   The type of the answer.
     * @return The retrieved card.
     * @throws ApiException if there is an error while retrieving the card.
     */
    @Override
    public int createCard(int deckId, String question, CardType questionType, String answer, CardType answerType) throws ApiException {
        try {
            HttpResponse<String> response = this.sendCreateObjectRequest(
                    cardsEndpoint,
                    this.state.getAuthToken(),
                    new CreateCardRequest(question, questionType.getType(), answer, answerType.getType(), deckId));

            handleExceptions(response);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.body());
            cacheService.invalidate(cacheKey);
            cacheService.invalidate(DeckService.cacheKey);
            return node.get(JSONWriter.ID).asInt();
        } catch (JsonProcessingException e) {
            throw new CardCreationException(e.getMessage());
        }
    }

    /**
     * Retrieves the quiz card with the specified ID.
     *
     * @param cardId The ID of the card to retrieve.
     * @return The retrieved card.
     * @throws ApiException if there is an error while retrieving the card.
     */
    @Override
    public ICard getCard(int cardId) throws ApiException {
        try {
            // getCards is cached, so it's more efficient to retrieve all cards and then filter the one we want
            List<ICard> cards = getCards();

            for (ICard card : cards) {
                if (card.getId() == cardId) {
                    return card;
                }
            }
            // if not found, try to retrieve it from the API
            HttpResponse<String> response = this.sendFetchOneObjectRequest(cardsEndpoint,
                    this.state.getAuthToken(),
                    cardId);
            handleExceptions(response);

            // if found, the cache is no longer up to date
            cacheService.invalidate(cacheKey);
            CardParser parser = new CardParser();
            return parser.parse(response.body());

        } catch (JsonProcessingException e) {
            throw new ApiException(e.getMessage());
        }
    }

    @Override
    public List<ICard> getCards() throws ApiException {
        String response = cacheService.get(cacheKey);
        if (response == null) {
            HttpResponse<String> httpResponse = this.sendFetchAllObjectRequest(cardsEndpoint, this.state.getAuthToken());
            handleExceptions(httpResponse);
            cacheService.put(cacheKey, httpResponse.body());
            response = httpResponse.body();
        }
        try {
            // parse all cards
            ObjectMapper mapper = new ObjectMapper();
            List<ICard> cardList = new ArrayList<>();
            ArrayNode node = (ArrayNode) mapper.readTree(response);
            CardParser parser = new CardParser();
            for (JsonNode jsonCard : node) {
                ICard card = parser.parse(jsonCard.toString());
                cardList.add(card);
            }
            return cardList;
        } catch (JsonProcessingException e) {
            cacheService.invalidate(cacheKey);
            throw new ApiException(e.getMessage());
        }
    }

    /**
     * Updates the question of the quiz card with the specified ID.
     *
     * @param cardId   The ID of the card to update.
     * @param question The new question of the card.
     * @throws ApiException if there is an error while updating the card.
     */
    @Override
    public void updateCardQuestion(int cardId, String question) throws ApiException {
        ICard card = getCard(cardId);
        int deckId = card.getDeck();
        HashMap<String, Object> request = new HashMap<>();
        request.put(JSONWriter.deck, deckId);
        request.put(JSONWriter.question, question);

        HttpResponse<String> response = this.sendPatchObjectRequest(
                cardsEndpoint,
                this.state.getAuthToken(),
                cardId,
                request);

        handleExceptions(response);
        cacheService.invalidate(cacheKey);
        cacheService.invalidate(DeckService.cacheKey);
    }

    /**
     * Updates the answer of the quiz card with the specified ID.
     *
     * @param cardId The ID of the card to update.
     * @param answer The new answer of the card.
     * @throws ApiException if there is an error while updating the card.
     */
    @Override
    public void updateCardAnswer(int cardId, String answer) throws ApiException {
        ICard card = getCard(cardId);
        int deckId = card.getDeck();
        HashMap<String, Object> request = new HashMap<>();
        request.put(JSONWriter.deck, deckId);
        request.put(JSONWriter.answer, answer);
        HttpResponse<String> response = this.sendPatchObjectRequest(
                cardsEndpoint,
                this.state.getAuthToken(),
                cardId,
                request);

        handleExceptions(response);
        cacheService.invalidate(cacheKey);
        cacheService.invalidate(DeckService.cacheKey);
    }


    @Override
    public void updateCardType(int cardId, CardType questionType, CardType answerType) throws ApiException {
        ICard card = getCard(cardId);
        int deckId = card.getDeck();
        HashMap<String, Object> request = new HashMap<>();
        request.put(JSONWriter.deck, deckId);
        request.put(JSONWriter.questionType, questionType);
        request.put(JSONWriter.answerType, answerType);
        HttpResponse<String> response = this.sendPatchObjectRequest(
                cardsEndpoint,
                this.state.getAuthToken(),
                cardId,
                request);

        handleExceptions(response);

                cacheService.invalidate(cacheKey);
        cacheService.invalidate(DeckService.cacheKey);
    }

    /**
     * Updates the question and answer of the quiz card with the specified ID.
     *
     * @param cardId The ID of the card to update.
     * @throws ApiException if there is an error while updating the card.
     */
    @Override
    public void deleteCard(int cardId) throws ApiException {
        HttpResponse<String> response = this.sendDeleteObjectRequest(
                cardsEndpoint,
                this.state.getAuthToken(),
                cardId);

        handleExceptions(response);
        cacheService.invalidate(cacheKey);
        cacheService.invalidate(DeckService.cacheKey);
    }
}
