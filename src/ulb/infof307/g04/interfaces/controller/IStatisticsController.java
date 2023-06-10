package ulb.infof307.g04.interfaces.controller;


public interface IStatisticsController extends IController{
    void setNumberOfSessions(String chosenDeck);
    void setDeckMastery(String chosenDeck);
    void setDeckMasteryPerDay(String chosenDeck);
    void setNumberOfSessionsPerDay(String chosenDeck);
    void setNoDeckSelectedTextFalse();
}
