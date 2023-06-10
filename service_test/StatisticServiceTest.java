package ulb.infof307.g04.services;

import org.json.JSONObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.factory.StatisticServiceFactory;
import ulb.infof307.g04.interfaces.services.IStatisticService;
import ulb.infof307.g04.factory.UserServiceFactory;


public class StatisticServiceTest {
    private final String BASE_URI = "http://localhost:8000";
    private IStatisticService statisticService;

    @BeforeEach
    void init() throws ApiException {
        StatisticServiceFactory.setInstance(new StatisticService(BASE_URI));
        UserServiceFactory.setInstance(new UserService(BASE_URI));

        this.statisticService = StatisticServiceFactory.getInstance();
        UserServiceFactory.getInstance().signIn("ben", "ben1234ben");
    }

    @Test
    void testGetUserStatistics() throws ApiException {
        JSONObject userStatistics = this.statisticService.getUserStatistics();

        assertNotNull(userStatistics);
        assertTrue(userStatistics.length() > 0);
    }

    @Test
    void testGetMostStudiedDeck() throws ApiException {
        JSONObject mostStudiedDeck = this.statisticService.getMostStudiedDeck();

        assertNotNull(mostStudiedDeck);
        assertTrue(mostStudiedDeck.length() > 0);
    }
}