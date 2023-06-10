package ulb.infof307.g04.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import ulb.infof307.g04.AppState;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.interfaces.services.IStatisticService;
import ulb.infof307.g04.parsers.DeckParser;
import ulb.infof307.g04.models.Deck;


public class StatisticService extends AbstractAPIService implements IStatisticService {
    private final AppState state = AppState.getInstance();
    private static final String DECKS_API_URL = "/api/stats/all/";
    private static final String MOST_STUDIED_DECK_API_URL = "/api/quiz/decks/?ordering=-quiz_results_count-descending";

    public StatisticService(String baseUri) {
        super(baseUri);
    }

    @Override
    public JSONObject getUserStatistics() throws ApiException {
        return new JSONObject(fetchAllObjectRequest(DECKS_API_URL));
    }

    @Override
    public List<Deck> getMostStudiedDeck() throws ApiException, JsonProcessingException {
        String response = fetchAllObjectRequest(MOST_STUDIED_DECK_API_URL);
        return parseDeckList(response);
    }

    private String fetchAllObjectRequest(String apiUrl) throws ApiException {
        HttpResponse<String> response = this.sendFetchAllObjectRequest(apiUrl, state.getAuthToken());
        handleExceptions(response);
        return response.body();
    }

    private List<Deck> parseDeckList(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Deck> decks = new ArrayList<>();
        ArrayNode node = (ArrayNode) mapper.readTree(json);
        DeckParser parser = new DeckParser();
        for (JsonNode jsonDeck : node) {
            Deck deck = parser.parse(jsonDeck.toString());
            decks.add(deck);
        }

        return decks;
    }
}
