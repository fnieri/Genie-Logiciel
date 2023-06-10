package ulb.infof307.g04.interfaces.view;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import ulb.infof307.g04.exceptions.api.ApiException;

import java.util.Map;

public interface IStatisticsViewController extends IViewController{
    void setDeckNumber(String deckNumber);
    void setNumberDeckStudied(String numberDeckStudied);
    void setFavoriteDecks(String favoriteDecks);
    void setStudyTime(String studyTime);
    void handleDeckNameComboBox(ActionEvent event) throws ApiException;
    void setDeckNameComboBox(ObservableList<String> decks);
    void setNumberOfSessions(String chosenDeck);
    String getDeckNameComboBox();
    void setDeckMastery(String masteryLevel);
    void setTimeSpentPerDay(Map <String, Integer> timeSpentPerDay);
    void setDeckMasteryPerDay(Map<String, Integer> sessionsPerDay);
    void setNumberOfSessionsPerDay(Map<String, Integer> sessionsPerDay);
    void setNoDeckSelectedTextFalse();
}
