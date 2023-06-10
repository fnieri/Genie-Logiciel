package ulb.infof307.g04.interfaces.controller;

import ulb.infof307.g04.enums.CardMasteryLevels;

public interface ILearningController extends IController {
    /**
     * Switches to the previous question of the deck 
     */
    void goToPrevQuestion();

    /**
     * Switches to the next question of the deck 
     */
    void goToNextQuestion();

    /**
     * Updates the card mastery level
     *
     * @param mastery The new mastery level
     */
    void updateCardMasteryLevel(CardMasteryLevels mastery);

    /**
     * Checks if the answer is correct only if the card is a MCQ
     *
     * @param answer The answer to be checked
     * @return True if the answer is correct, false otherwise
     */
    boolean checkAnswer(String answer);

    /**
     *  Triggers the question card to speech.
     */
    void speak();

    void showRules();
}