package ulb.infof307.g04.modes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.exceptions.HasNotReviewedCardException;
import ulb.infof307.g04.exceptions.deck.DeckIsEmpty;
import ulb.infof307.g04.exceptions.deck.EndOfCards;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.models.Deck;
import ulb.infof307.g04.patterns.CardFactory;
import java.util.*;

class ReviewModeTest {
    Deck deck;
    ReviewMode reviewMode;

    @BeforeEach
    void setUpDeck() throws DeckIsEmpty {

        int cardId = 0;
        int deckId = 0;
        List<ICard> cards = new ArrayList<>();
        for (int i = 0; i < 2; i++) {

            CardFactory cardFactory = new CardFactory();

            // Generate a random cardType
            CardType questionType = CardType.values()[new Random().nextInt(CardType.values().length)];
            CardType answerType = CardType.values()[new Random().nextInt(CardType.values().length)];

            CardMasteryLevels masteryLevel = CardMasteryLevels.values()[new Random().nextInt(CardMasteryLevels.values().length)];
            ICard card = cardFactory.makeCard(cardId, deckId, "What's updog?", "Nothing much what's up with you", questionType, answerType, masteryLevel);
            cardId++;
            card.setMasteryLevel(CardMasteryLevels.NOT_YET_LEARNED);
            cards.add(card);
        }
        deck = new Deck(deckId,  "a", Arrays.asList("b"),  Arrays.asList(), cards);

        reviewMode = new ReviewMode(deck);
    }

    /**
     * Test that all cards in a priority queue are ordered by descending order
     */
    @Test
    void startModeTest() throws HasNotReviewedCardException {
        while (true) {
            try {
                ICard firstCard = reviewMode.getCurrentCard();
                reviewMode.setHasReviewedCard(true);
                reviewMode.goToNextCard();
                ICard secondCard = reviewMode.getCurrentCard();
                reviewMode.setHasReviewedCard(true);
                Assertions.assertTrue(firstCard.getMasteryLevel().lessThanOrEqualTo(secondCard.getMasteryLevel()));
            } catch (EndOfCards e) {
                break;
            }
        }
    }

    /**
     * Test that the card being removed and being reinserted after putting very bad is the same
     */
    @Test
    void testUpdateCardStatus() {
        // Test that the card is being readded on the top of the queue (As it is the only card with very_bad prio)
        ICard firstCard = reviewMode.getCurrentCard();

        CardMasteryLevels newMasteryLevel = CardMasteryLevels.VERY_BAD;
        reviewMode.handleMastery(newMasteryLevel);

        ICard updatedFirstCard = reviewMode.getCurrentCard();
        Assertions.assertEquals(firstCard, updatedFirstCard);

        // Card is being readded at the back of priority queue because it wasn't learnt before
        newMasteryLevel = CardMasteryLevels.VERY_GOOD;
        reviewMode.handleMastery(newMasteryLevel);

        PriorityQueue<ICard> cardQueue = reviewMode.getCardsToReview();
        Assertions.assertTrue(cardQueue.contains(updatedFirstCard));
    }
}
