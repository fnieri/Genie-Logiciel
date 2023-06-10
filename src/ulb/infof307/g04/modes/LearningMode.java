package ulb.infof307.g04.modes;

import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.exceptions.HasNotReviewedCardException;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.exceptions.deck.DeckIsEmpty;
import ulb.infof307.g04.exceptions.deck.EndOfCards;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.patterns.LearningModeVisitor;
import ulb.infof307.g04.models.Deck;

/**
 * @author @fnieri Pilote @lpalmisa Copilote
 * @date 16/04/2022
 * The parent class to all learning modes (ReviewMode, StudyMode, FreeMode)
 */


public abstract class LearningMode {
    protected Deck deck;
    protected boolean hasReviewedCard = false;

    LearningMode(Deck deck) throws DeckIsEmpty {
        if (deck.isEmpty()) throw new DeckIsEmpty();
        this.deck = deck;
    }

    /**
     * Go to the previous card in the mode deck
     * @throws EndOfCards   If the mode doesn't allow to go to the previous card or if there aren't any cards to go back to
     */
    public abstract void goToPreviousCard() throws EndOfCards;

    /**
     * Go to the next card in the mode deck
     * @throws EndOfCards   If there aren't any more cards left to study
     */
    public abstract void goToNextCard() throws EndOfCards, HasNotReviewedCardException;

    /**
     *
     * @return The current card being studied
     */
    public abstract ICard getCurrentCard();

    /**
     * Handle the user's desired mastery level
     * @param cardMasteryLevel  The mastery level selected by the user
     */
    public abstract void handleMastery(CardMasteryLevels cardMasteryLevel) throws ApiException;

    /**
     * @return The progress of the deck, that is, the current card the user is on
     */
    public abstract int getProgress();


    public int getScore() {
        return 0;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setHasReviewedCard(boolean hasReviewedCard) {
        this.hasReviewedCard = hasReviewedCard;
    }

    public abstract void accept(LearningModeVisitor visitor);

    public abstract String getRules();
}
