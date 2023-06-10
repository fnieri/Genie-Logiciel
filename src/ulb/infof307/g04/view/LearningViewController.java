package ulb.infof307.g04.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import ulb.infof307.g04.controllers.LearningController;
import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.interfaces.controller.ILearningController;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.interfaces.view.IAppViewController;
import ulb.infof307.g04.interfaces.view.ILearningViewController;
import ulb.infof307.g04.modes.LearningMode;
import ulb.infof307.g04.widgets.components.LearningModeCardComponent;

/**
 * @author @fnieri Pilote Refactor @lpalmisa Copilote Refactor @dpikoppo Pilote
 *         Class representing a view controller for all learning modes (Review,
 *         Study, Free)
 */

public class LearningViewController extends AbstractViewController implements Initializable, ILearningViewController {
    @FXML Text scoreText;
    @FXML Text deckName;
    @FXML LearningModeCardComponent cardComponent;

    private ILearningController controller;
    public LearningViewController(IAppViewController app, HashMap<String, Object> params) {
        super(app);
        LearningMode learningMode = (LearningMode) params.get("learning_mode");
        controller = new LearningController(this, learningMode);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controller.initialize();
        this.cardComponent.onGoToPrevCardEvent(this::handleGotoPrevQuestion);
        this.cardComponent.onGoToNextCardEvent(this::handleGotoNextQuestion);
        this.cardComponent.onFlipCardEvent(this::handleFlipCard);
        this.cardComponent.onAnswerEvent(this::handlerAnswerEvent);
        this.cardComponent.onCardSetMasteryEvent(this::handleMasteryLevel);
    }

    @FXML
    public void handleSpeakerBtn() {
        controller.speak();
    }

    @FXML
    public void handleRulesBtn() {
        controller.showRules();
    }

    /**
     * This method is called when the user clicks on the previous question button
     *
     * @param event
     */
    private void handleGotoPrevQuestion(ActionEvent event) {
        this.setFlipBtnDisable(false);
        controller.goToPrevQuestion();
    }

    /**
     * This method is called when the user clicks on the next question button
     *
     * @param event
     */
    private void handleGotoNextQuestion(ActionEvent event) {
        this.setFlipBtnDisable(false);
        controller.goToNextQuestion();
    }

    /**
     * This method is called when the user clicks the flip card button
     *
     * @param event
     */
    private void handleFlipCard(ActionEvent event) {
        this.cardComponent.setCorrectAnswerVisible(true);
        this.setFlipBtnDisable(true);
    }

    private Boolean handlerAnswerEvent(String answer) {
        this.cardComponent.setCorrectAnswerVisible(true);
        this.setFlipBtnDisable(true);
        return controller.checkAnswer(answer);
    }

    /**
     * This method is called when the user gives a mastery level to the card
     * 
     * @param masteryLevel
     */
    private void handleMasteryLevel(CardMasteryLevels masteryLevel) {
        controller.updateCardMasteryLevel(masteryLevel);
        controller.goToNextQuestion();
    }


    /**
     * This method is called when the user clicks the flip card button
     *
     * @param name
     */
    @Override
    public void setDeckName(String name) {
        deckName.setText(name);
    }

    @Override
    public void setScore(int score) {
        scoreText.setText(String.format("Score : %d", score));
    }

    @Override
    public void setProgress(int current, int max) {
        this.cardComponent.setPages(current, max);
    }

    @Override
    public void setNavButtonsVisibility(boolean isPrevBtnVisible, boolean isNextBtnVisible) {
        this.cardComponent.setNavButtonsVisible(isPrevBtnVisible, isNextBtnVisible);
    }

    @Override
    public void setNavButtonsDisable(boolean isPrevBtnDisabled, boolean isNextBtnDisabled) {
        this.cardComponent.setNavButtonsDisable(isPrevBtnDisabled, isNextBtnDisabled);
    }

    @Override
    public void setScoreVisibility(boolean isVisible) {
        this.scoreText.setVisible(isVisible);
    }

    @Override
    public void setCard(ICard card) {
        this.cardComponent.setCard(card);
        this.cardComponent.setCorrectAnswerVisible(false);
    }

    @Override
    public void setFlipBtnDisable(boolean disabled) {
        this.cardComponent.setFlipButtonDisable(disabled);
    }

    @Override
    public void setPerformanceTypeButtonVisible(boolean visible) {
        this.cardComponent.setPerformanceTypeButtonVisible(visible);
    }

}
