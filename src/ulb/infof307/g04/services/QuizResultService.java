package ulb.infof307.g04.services;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ulb.infof307.g04.AppState;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.interfaces.services.IQuizResultService;

public class QuizResultService extends AbstractAPIService implements IQuizResultService {
    private final AppState state = AppState.getInstance();

    public QuizResultService(String baseUri) {
        super(baseUri);
    }

    public QuizResultService(String baseUri, HttpClient client) {
        super(baseUri, client);
    }

    /**
     * Create a quiz result given a card id and a performance type
     * @param cardId id of the card
     * @param performanceType Performance type of the card
     * @return The id of the created quiz result
     * @throws ApiException
     */
    @Override
    public int createQuizResult(int cardId, int performanceType) throws ApiException {
        try {
            HashMap<String, Object> requestBody = new HashMap<>();
            requestBody.put("card", cardId);
            requestBody.put("performance_type", performanceType);

            HttpResponse<String> response = this.sendCreateObjectRequest(
                    "/api/quiz/quiz-results/",
                    this.state.getAuthToken(),
                    requestBody);

            handleExceptions(response);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.body());
            return node.get("id").asInt();
        } catch (IOException e) {
            throw new ApiException(e.getMessage());
        }
    }
}
