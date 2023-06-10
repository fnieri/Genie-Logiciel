package ulb.infof307.g04.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONObject;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.factory.DeckServiceFactory;
import ulb.infof307.g04.factory.StatisticServiceFactory;
import ulb.infof307.g04.interfaces.controller.IStatisticsController;
import ulb.infof307.g04.interfaces.services.IDeckService;
import ulb.infof307.g04.interfaces.services.IStatisticService;
import ulb.infof307.g04.models.Deck;
import ulb.infof307.g04.interfaces.view.*;

import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Statistics Controller class for managing statistics related operations.
 */
public class StatisticsController extends AbstractController implements IStatisticsController {
    private final IStatisticsViewController view;
    private final IDeckService deckService = DeckServiceFactory.getInstance();
    private final IStatisticService statisticService = StatisticServiceFactory.getInstance();
    private List<Deck> decks;
    private JSONObject globalStats;
    private List<Deck> mostStudiedDeck;

    public StatisticsController(IStatisticsViewController view) {
        super(view);
        this.view = view;
    }

    @Override
    public void initialize() {
        super.initialize();
        fetchAndSetStatistics();
    }

    /**
     * Fetch and set statistics data
     */
    private void fetchAndSetStatistics() {
        runAsync(() -> {
            try {
                fetchStatisticsData();
            } catch (ApiException | JsonProcessingException e) {
                throw new CompletionException(e);
            }
        }, this::setStatistics, "Fetching statistics data", "Failed to fetch statistics data");
    }

    private void fetchStatisticsData() throws ApiException, JsonProcessingException {
        globalStats = statisticService.getUserStatistics();
        mostStudiedDeck = statisticService.getMostStudiedDeck();
        decks = deckService.getDecks();
    }

    /**
     * Set statistics for view
     */
    private void setStatistics() {
        setGlobalStatistics();
        setDeckStatistics();
    }

    /**
     * Set global statistics for view
     */
    private void setGlobalStatistics() {
        String studyTimeInMinutes = convertToMinutes(globalStats.getFloat("total_study_duration"));
        view.setStudyTime(studyTimeInMinutes + " minute(s)");

        view.setNumberDeckStudied(globalStats.get("number_of_decks_studied").toString());
        JSONObject dailyStudyDuration = (JSONObject) globalStats.get("daily_study_duration");
        Map<String, Integer> studyTimePerDay = convertToMapObjectJSON(dailyStudyDuration, "timeSpentPerDay");
        view.setTimeSpentPerDay(studyTimePerDay);

        String favoriteDeckNames = getFavoriteDeckNames();
        view.setFavoriteDecks(favoriteDeckNames);

        int numberOfDecks = decks.size();
        view.setDeckNumber(String.valueOf(numberOfDecks));

        ObservableList<String> deckNamesList = getDeckNames();
        view.setDeckNameComboBox(deckNamesList);
    }

    /**
     * Set deck statistics for view
     */
    private void setDeckStatistics() {
        processDeck(view.getDeckNameComboBox(), this::setDeckStatisticsFor);
    }

    /**
     * Set deck statistics for specific deck
     */
    private void setDeckStatisticsFor(Deck deck, int deckId) {
        runAsync(() -> {
            try {
                return fetchDeckStudySessions(deckId);
            } catch (ApiException e) {
                throw new CompletionException(e);
            }
        }, view::setNumberOfSessions, "Fetching deck study sessions", "Failed to fetch statistics data");
    }

    private String getFavoriteDeckNames() {
        return mostStudiedDeck.stream().limit(5).map(Deck::getName).collect(Collectors.joining(", "));
    }

    private ObservableList<String> getDeckNames() {
        return FXCollections.observableArrayList(decks.stream().map(Deck::getName).collect(Collectors.toList()));
    }

    private static String convertToMinutes(float studyTime) {
        return Integer.toString((int) studyTime / 60);
    }

    /**
     * Set number of sessions for specific deck
     */
    public void setNumberOfSessions(String chosenDeck) {
        processDeck(chosenDeck, this::setNumberOfSessionsFor);
    }

    /**
     * Set number of sessions for specific deck
     */
    private void setNumberOfSessionsFor(Deck deck, int deckId) {
        runAsync(() -> {
            try {
                return fetchDeckStudySessions(deckId);
            } catch (ApiException e) {
                throw new CompletionException(e);
            }
        }, view::setNumberOfSessions, "Fetching statistics data", "Failed to fetch statistics data");
    }

    /**
     * Set deck mastery for specific deck
     */
    public void setDeckMastery(String chosenDeck) {
        processDeck(chosenDeck, this::setDeckMasteryFor);
    }

    /**
     * Set deck mastery for specific deck
     */
    private void setDeckMasteryFor(Deck deck, int deckId) {
        runAsync(() -> {
            try {
                return fetchDeckMastery(deckId);
            } catch (ApiException e) {
                throw new CompletionException(e);
            }
        }, view::setDeckMastery, "Fetching deck mastery", "Failed to fetch statistics data");
    }

    /**
     * Set deck mastery per day for specific deck
     */
    public void setDeckMasteryPerDay(String chosenDeck) {
        processDeck(chosenDeck, this::setDeckMasteryPerDayFor);
    }

    /**
     * Set deck mastery per day for specific deck
     */
    private void setDeckMasteryPerDayFor(Deck deck, int deckId) {
        JSONObject deckStats = safeFetchDeckStatistics(deckId, "Failed to fetch statistics data");
        if (deckStats == null) return;
        Map<String, Integer> masteryPerDayMap = convertToMapObjectJSON((JSONObject) deckStats.get("mastery_level_by_day"), "masteryLevelByDay");
        view.setDeckMasteryPerDay(masteryPerDayMap);
    }

    /**
     * Set number of sessions per day for specific deck
     */
    public void setNumberOfSessionsPerDay(String chosenDeck) {
        processDeck(chosenDeck, this::setNumberOfSessionsPerDayFor);
    }

    /**
     * Set number of sessions per day for specific deck
     */
    private void setNumberOfSessionsPerDayFor(Deck deck, int deckId) {
        JSONObject deckStats = safeFetchDeckStatistics(deckId, "Failed to fetch statistics data");
        if (deckStats == null) return;
        Map<String, Integer> sessionPerDayMap = convertToMapObjectJSON((JSONObject) deckStats.get("study_sessions_by_day"), "sessionPerDay");
        view.setNumberOfSessionsPerDay(sessionPerDayMap);
    }

    @Override
    public void setNoDeckSelectedTextFalse() {
        view.setNoDeckSelectedTextFalse();
    }

    private void processDeck(String chosenDeck, BiConsumer<Deck, Integer> deckProcessor) {
        decks.stream()
                .filter(deck -> deck.getName().equals(chosenDeck))
                .findFirst()
                .ifPresent(deck -> deckProcessor.accept(deck, deck.getId()));
    }

    private JSONObject safeFetchDeckStatistics(int deckId, String errorMessage) {
        try {
            return deckService.getUserDeckStatistics(deckId);
        } catch (ApiException e) {
            showAlertForException(e, errorMessage);
            return null;
        }
    }

    private String fetchDeckStudySessions(int deckId) throws ApiException {
        JSONObject deckStats = deckService.getUserDeckStatistics(deckId);
        return deckStats.get("study_sessions").toString();
    }

    private Map<String, Integer> convertToMapObjectJSON(JSONObject dataPerDayJSON, String chartType) {
        Map<String, Function<Double, Integer>> chartCalculations = new HashMap<>();
        chartCalculations.put("masteryLevelByDay", d -> (int) (d * 100));
        chartCalculations.put("timeSpentPerDay", d -> (int) (d / 60));
        chartCalculations.put("sessionPerDay", Double::intValue);

        if (!chartCalculations.containsKey(chartType)) {
            throw new IllegalArgumentException("Invalid chart type");
        }

        Function<Double, Integer> calculation = chartCalculations.get(chartType);

        Map<String, Integer> result = new HashMap<>();
        for (String key : dataPerDayJSON.keySet()) {
            String value = dataPerDayJSON.get(key).toString();
            double doubleValue = Double.parseDouble(value);
            int finalValue = calculation.apply(doubleValue);
            result.put(key, finalValue);
        }
        return result;
    }

    /**
     * Fetch deck mastery for specific deck
     *
     * @param deckId deck id
     * @return String mastery level in percentage format
     * @throws ApiException if any exception occurred while processing
     */
    private String fetchDeckMastery(int deckId) throws ApiException {
        JSONObject deckStats = deckService.getUserDeckStatistics(deckId);
        float masteryLevel = Float.parseFloat(deckStats.get("mastery_level").toString());
        float percentage = masteryLevel * 100;
        return String.format("%.2f%%", percentage);
    }
}

