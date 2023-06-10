package ulb.infof307.g04.interfaces.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;

import java.util.List;

import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.models.Deck;

public interface IStatisticService {
    JSONObject getUserStatistics() throws ApiException;
    List<Deck> getMostStudiedDeck() throws ApiException, JsonProcessingException;
}
