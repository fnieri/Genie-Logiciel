package ulb.infof307.g04.view;

import java.net.URL;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import ulb.infof307.g04.controllers.StatisticsController;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.interfaces.controller.IStatisticsController;
import ulb.infof307.g04.interfaces.view.IAppViewController;
import ulb.infof307.g04.interfaces.view.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StatisticsViewController extends AbstractViewController implements IStatisticsViewController, Initializable {
    @FXML
    public Text studyTimeAllTimeText;
    @FXML
    public Text deckNumber;
    @FXML
    public Text numberDeckStudied;
    @FXML
    public Text favoriteDecks;
    @FXML
    LineChart<String, Integer> lineChartTime;
    @FXML
    public CategoryAxis xAxisTime;
    @FXML
    public NumberAxis yAxisTime;
    @FXML
    public ComboBox deckNameComboBox;
    @FXML
    public Text NumOfSess;
    @FXML
    public Text DeckMastery;
    @FXML
    public Text noDeckSelectedMastery;
    @FXML
    public Text noDeckSelectedSession;
    @FXML
    public LineChart<String, Integer> lineChartMastery;
    @FXML
    public CategoryAxis xAxisMastery;
    @FXML
    public NumberAxis yAxisMastery;
    @FXML
    public LineChart<String, Integer> lineChartSession;
    @FXML
    public CategoryAxis xAxisSession;
    @FXML
    public NumberAxis yAxisSession;
    private final IStatisticsController controller;

    public StatisticsViewController(IAppViewController app, HashMap<String, Object> params) {
        super(app);
        controller = new StatisticsController(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller.initialize();
    }

    @Override
    public void setStudyTime(String studyTime) {
        studyTimeAllTimeText.setText(studyTime);
    }

    @Override
    public void setDeckNumber(String numberOfDecks) {
        deckNumber.setText(numberOfDecks);
    }

    @Override
    public void setNumberDeckStudied(String numDeckStudied) {
        numberDeckStudied.setText(numDeckStudied);
    }


    @Override
    public void setFavoriteDecks(String favDecks) {
        favoriteDecks.setText(favDecks);
    }

    @Override
    public void setTimeSpentPerDay(Map<String, Integer> timeSpentPerDay) {

        ObservableList<String> xTickLabels = FXCollections.observableArrayList();
        setFormatChart(xTickLabels, "Time spent per day", yAxisTime, xAxisTime);
        setDataInChart(timeSpentPerDay, xTickLabels, lineChartTime);
    }

    @Override
    public String getDeckNameComboBox() {
        return (String) deckNameComboBox.getValue();
    }

    @Override
    public void setDeckNameComboBox(ObservableList<String> deckList) {
        deckNameComboBox.setItems(deckList);
    }

    @Override
    public void handleDeckNameComboBox(ActionEvent event) {
        String chosenDeck = (String) deckNameComboBox.getValue();
        controller.setNoDeckSelectedTextFalse();
        controller.setNumberOfSessions(chosenDeck);
        controller.setDeckMastery(chosenDeck);
        controller.setDeckMasteryPerDay(chosenDeck);
        controller.setNumberOfSessionsPerDay(chosenDeck);
    }

    @Override
    public void setNumberOfSessions(String numberOfSessions) {
        NumOfSess.setText(numberOfSessions);
    }

    @Override
    public void setDeckMastery(String masteryLevel) {
        DeckMastery.setText(masteryLevel);
    }

    @Override
    public void setDeckMasteryPerDay(Map<String, Integer> deckMasteryPerDayMap) {
        yAxisMastery.setUpperBound(100);
        yAxisMastery.setLowerBound(0);

        ObservableList<String> xTickLabels = FXCollections.observableArrayList();
        setFormatChart(xTickLabels, "Mastery per day in (%)", yAxisMastery, xAxisMastery);
        setDataInChart(deckMasteryPerDayMap, xTickLabels, lineChartMastery);
    }

    @Override
    public void setNumberOfSessionsPerDay(Map<String, Integer> sessionsPerDay) {

        ObservableList<String> xTickLabels = FXCollections.observableArrayList();
        setFormatChart(xTickLabels, "Session(s) per day", yAxisSession, xAxisSession);
        setDataInChart(sessionsPerDay, xTickLabels, lineChartSession);
    }

    public void setFormatChart(ObservableList<String> xTickLabels, String chartTitle, NumberAxis yAxis, CategoryAxis xAxis) {
        xAxis.setTickLabelGap(0.1);
        yAxis.setTickLabelFont(new javafx.scene.text.Font(12));
        xAxis.setTickLabelFont(new javafx.scene.text.Font(12));
        xAxis.setCategories(xTickLabels);
        yAxis.setLabel(chartTitle);
        yAxis.setTickLabelRotation(90);
        yAxis.setTickLabelRotation(90);
        yAxis.setLowerBound(0);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = currentDate.minusDays(i);
            String formattedDate = date.format(formatter);
            xTickLabels.add(formattedDate);
        }
    }

    private void setDataInChart(Map<String, Integer> timeSpentPerDay, ObservableList<String> xTickLabels, LineChart<String, Integer> lineChart) {
        lineChart.getData().clear();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        for (int i = 0; i < 7; i++) {
            if (timeSpentPerDay.get(xTickLabels.get(i)) == null) {
                series.getData().add(new XYChart.Data<>(xTickLabels.get(i), 0));
            } else {
                series.getData().add(new XYChart.Data<>(xTickLabels.get(i), timeSpentPerDay.get(xTickLabels.get(i))));
            }
        }
        lineChart.setVisible(true);
        lineChart.getData().add(series);
    }

    @Override
    public void setNoDeckSelectedTextFalse() {
        noDeckSelectedMastery.setVisible(false);
        noDeckSelectedSession.setVisible(false);
    }
}
