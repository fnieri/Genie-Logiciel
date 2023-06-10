package ulb.infof307.g04.modes;

import java.util.*;

import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.exceptions.deck.DeckIsEmpty;
import ulb.infof307.g04.exceptions.deck.EndOfCards;
import ulb.infof307.g04.exceptions.HasNotReviewedCardException;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.patterns.LearningModeVisitor;
import ulb.infof307.g04.models.Deck;

/**
 * The free mode where the user can work to get the better score
 * Extends {@code LearningMode}
 */

public class FreeMode extends LearningMode {
    private int score = 0;
    private final Queue<ICard> cardsToReview;
    private int progress = 0;
    private CardMasteryLevels currentMasteryLevel;

    public FreeMode(Deck deck) throws DeckIsEmpty {
        super(deck);
        List<ICard> deckToShuffle = new ArrayList<>(deck.getCards());
        Collections.shuffle(deckToShuffle);
        cardsToReview = new LinkedList<>(deckToShuffle);
    }

    /**
     * Add the {@code status.score()} to the score
     * @param masteryLevel The status input by the user
     */
    @Override
    public void handleMastery(CardMasteryLevels masteryLevel) {
        currentMasteryLevel = masteryLevel;
        setHasReviewedCard(true);
    }

    @Override
    public void goToPreviousCard() throws EndOfCards {
        throw new EndOfCards("");
    }

    @Override
    public void goToNextCard() throws EndOfCards, HasNotReviewedCardException {
        if (!hasReviewedCard) throw new HasNotReviewedCardException();
        boolean isQueueEmpty = cardsToReview.size() <= 1;
        if (isQueueEmpty) throw new EndOfCards("No more cards left to study");
        progress++;
        cardsToReview.poll();
        score += currentMasteryLevel.score();
        setHasReviewedCard(false);
    }

    @Override
    public ICard getCurrentCard() {
        return cardsToReview.peek();
    }


    /**
     * @return The session score
     */
    @Override
    public int getScore() {
        return score;
    }

    @Override
    public int getProgress() {
        return progress;
    }


    public void accept(LearningModeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getRules() {
        return "Gather points depending of your knowledge! \n" +
                "After you verify your answer, click on one of the five button under the card and gain points based on the " +
                "accuracy of your answer.";
    }

}