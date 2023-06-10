package ulb.infof307.g04.controllers;

import java.util.List;

import javafx.scene.control.Alert;
import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.enums.EPages;
import ulb.infof307.g04.exceptions.HasNotReviewedCardException;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.exceptions.deck.EndOfCards;
import ulb.infof307.g04.interfaces.controller.ILearningController;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.interfaces.view.ILearningViewController;
import ulb.infof307.g04.models.Deck;
import ulb.infof307.g04.modes.FreeMode;
import ulb.infof307.g04.modes.LearningMode;
import ulb.infof307.g04.modes.ReviewMode;
import ulb.infof307.g04.modes.StudyMode;
import ulb.infof307.g04.parsers.CardContentParser;
import ulb.infof307.g04.patterns.LearningModeVisitor;
import ulb.infof307.g04.utils.TextToSpeech;


/**
 * Controller for the learning views.
 * <p>
 * This views allows the user to go through each card of a given deck
 * and study them in the chosen learning mode
 *
 * @see ulb.infof307.g04.view.LearningViewController
 * @see ulb.infof307.g04.interfaces.controller.ILearningController
 */
public class LearningController extends AbstractController implements ILearningController, LearningModeVisitor {

    LearningMode learningMode;
    Deck deck;

    private final ILearningViewController view;

    /**
     * Constructor for the LearningController
     *
     * @param view        The view associated with the controller
     * @param learningMode The learning mode to be used
     */
    public LearningController(ILearningViewController view, LearningMode learningMode) {
        super(view);
        this.learningMode = learningMode;
        deck = learningMode.getDeck();
        this.view = view;

    }

    /**
     * Initializes the controller.
     * Sets the deck name and displays the first card.
     */
    @Override
    public void initialize() {
        view.setDeckName(deck.getName());
        updateCard();
    }

    /**
     * Updates the view with the current card information.
     */
    private void updateCard() {
        view.setProgress(learningMode.getProgress() + 1, learningMode.getDeck().size());
        view.setCard(learningMode.getCurrentCard());
        this.view.setFlipBtnDisable(false);

        removeLearningModesElements();
    }

    /**
     * Removes the appropriate learning modes elements through
     * the visitor pattern.
     */
    public void removeLearningModesElements() {
        learningMode.accept(this);
    }

    @Override
    public void visit(StudyMode learningMode) {
        view.setScoreVisibility(false);
        view.setPerformanceTypeButtonVisible(true);

        ICard card = learningMode.getCurrentCard();
        view.setNavButtonsDisable(learningMode.getProgress() == 0, card.getMasteryLevel() != CardMasteryLevels.NOT_YET_LEARNED);
    }

    @Override
    public void visit(FreeMode freeMode) {
        view.setNavButtonsVisibility(false, true);
        view.setScore(learningMode.getScore());
        view.setPerformanceTypeButtonVisible(true);
    }

    @Override
    public void visit(ReviewMode reviewMode) {
        view.setNavButtonsVisibility(false, true);
        view.setScoreVisibility(false);
        view.setPerformanceTypeButtonVisible(true);
    }

    @Override
    public void speak() {
        ICard card = learningMode.getCurrentCard();

        if (card.getQuestionType() == CardType.PLAIN_TEXT){
            try {
                TextToSpeech textToSpeech = new TextToSpeech();
                textToSpeech.speak(card.getQuestion());
            }
            catch (Exception e) {
                showAlertForException(e, "Error while initializing text to speech");
            }
        }
    }

    @Override
    public void goToPrevQuestion() {
        try {
            learningMode.goToPreviousCard();
            learningMode.setHasReviewedCard(false);
            updateCard();
            if (learningMode instanceof StudyMode) {
                view.setNavButtonsDisable(learningMode.getProgress() == 0, false);
            }
        } catch (EndOfCards e) {
            view.switchView(EPages.PROFILE);
        }
    }

    @Override
    public void goToNextQuestion() {
        try {
            learningMode.goToNextCard();
            learningMode.setHasReviewedCard(false);
            updateCard();
        } catch (EndOfCards e) {
            view.switchView(EPages.PROFILE);
        }
        catch (HasNotReviewedCardException e) {
            view.showAlert(Alert.AlertType.ERROR, "Error changing card", "You must review the card before going to the next one", "");
        }
    }

    @Override
    public void updateCardMasteryLevel(CardMasteryLevels mastery) {
        try {
            learningMode.setHasReviewedCard(true);
            learningMode.handleMastery(mastery);
        } catch (ApiException e) {
            showAlertForException(e, "Error when updating the card result");
        }
    }

    @Override
    public boolean checkAnswer(String answer) {
        CardContentParser parser = new CardContentParser();
        ICard card = learningMode.getCurrentCard();
        if (card.getAnswerType() == CardType.MCQ) {
            List<String> answersList = parser.parseAnswers(card.getAnswer());
            return answer.equals(answersList.get(0));
        }
        return answer.equals(card.getAnswer());
    }

    @Override
    public void showRules() {
        view.showAlert(Alert.AlertType.INFORMATION, "Rules", learningMode.getRules(), "");
    }
}