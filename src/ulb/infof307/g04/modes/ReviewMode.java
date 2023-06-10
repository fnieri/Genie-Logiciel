package ulb.infof307.g04.modes;

import java.util.*;

import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.exceptions.deck.DeckIsEmpty;
import ulb.infof307.g04.exceptions.deck.EndOfCards;
import ulb.infof307.g04.exceptions.*;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.patterns.LearningModeVisitor;
import ulb.infof307.g04.models.Deck;

/**
 * This class represents the review learning mode, extends {@code LearningMode}
 * It contains the attributes and methods specific to the review mode
 */

public class ReviewMode extends LearningMode {
    private final PriorityQueue<ICard> cardsToReview;
    private int progress = 0;
    private boolean addCurrentCardToQueue = false;

    public ReviewMode(Deck deck) throws DeckIsEmpty {
        super(deck);
        cardsToReview = new PriorityQueue<>(new CardsComparator());
        cardsToReview.addAll(deck.getCards());
    }

    @Override
    public void goToNextCard() throws EndOfCards, HasNotReviewedCardException {
        if (!hasReviewedCard) throw new HasNotReviewedCardException();
        boolean deckSizeSmallerOrEqualThanOne = cardsToReview.size() <= 1;
        if (deckSizeSmallerOrEqualThanOne) { throw new EndOfCards("No more cards left to study"); }
        if (addCurrentCardToQueue) cardsToReview.add(cardsToReview.peek());
        cardsToReview.poll();
        addCurrentCardToQueue = false;
    }

    /**
     * Can't go back in review mode
     * @throws EndOfCards If the user wants to go back
     */
    @Override
    public void goToPreviousCard() throws EndOfCards {
        throw new EndOfCards("");
    }

    @Override
    public ICard getCurrentCard() {
        return cardsToReview.peek();
    }

    @Override
    public int getProgress() {
        return progress;
    }

    /**
     * Update a card status and add it to the priority queue if it has not been learned
     * @note It has not been learned means that the mastery level is lower than good
     * @param newMasteryLevel  The new mastery level
     */
    public void handleMastery(CardMasteryLevels newMasteryLevel) {
        ICard oldCard = getCurrentCard();

        CardMasteryLevels oldMasteryLevel = oldCard.getMasteryLevel();
        boolean wasOldCardLearned = oldMasteryLevel.greaterThanOrEqualTo(CardMasteryLevels.GOOD);
        boolean isCardLearned = newMasteryLevel.greaterThanOrEqualTo(CardMasteryLevels.GOOD);
        oldCard.setMasteryLevel(newMasteryLevel);
        if (!isCardLearned || !wasOldCardLearned) addCurrentCardToQueue = true;
        else progress++;

    }

    public void accept(LearningModeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getRules() {
        return "Study your deck one card after another.\n" +
                "Test your knowledge over this deck by answering all the questions in order.";
    }

    public PriorityQueue<ICard> getCardsToReview() {
        return cardsToReview;
    }
}