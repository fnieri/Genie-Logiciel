package ulb.infof307.g04.modes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.exceptions.deck.DeckDoesNotExist;
import ulb.infof307.g04.exceptions.deck.DeckIsEmpty;
import ulb.infof307.g04.exceptions.deck.EndOfCards;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.models.Deck;
import ulb.infof307.g04.patterns.CardFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class StudyModeTest {

    Deck deck;
    StudyMode studyMode;

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
        deck = new Deck(deckId, "a", List.of("b"), List.of(), cards);

        studyMode = new StudyMode(deck);
    }

    /**
     * Test the behavior of goToNextCard method.
     * It should increment the current card index and update the progress.
     *
     * @throws EndOfCards if there are no more cards to review
     */
    @Test
    void testGoToNextCard() throws EndOfCards {
        // Arrange
        int initialProgress = studyMode.getProgress();

        // Act
        studyMode.goToNextCard();

        // Assert
        Assertions.assertEquals(initialProgress + 1, studyMode.getProgress());
    }

    /**
     * Test that goToNextCard method throws EndOfCards exception
     * when there are no more cards to review.
     *
     * @throws EndOfCards     if there are no more cards to review
     * @throws DeckIsEmpty    if the deck is empty
     * @throws DeckDoesNotExist if the deck does not exist
     */
    @Test
    void testGoToNextCardThrowsEndOfCardsException() throws EndOfCards, DeckIsEmpty {
        // Act
        studyMode.goToNextCard();

        // Assert
        Assertions.assertThrows(EndOfCards.class, () -> {
            // Trying to go to the next card should throw EndOfCards exception
            studyMode.goToNextCard();
        });
    }

    /**
     * Test the behavior of goToPreviousCard method.
     * It should decrement the current card index and update the progress.
     *
     * @throws EndOfCards if there are no more cards to review
     */
    @Test
    void testGoToPrevCard() throws EndOfCards {
        // Arrange
        studyMode.goToNextCard();
        int initialProgress = studyMode.getProgress();

        // Act
        studyMode.goToPreviousCard();

        // Assert
        Assertions.assertEquals(initialProgress - 1, studyMode.getProgress());
    }

    /**
     * Test that goToPreviousCard method throws EndOfCards exception
     * when there are no more cards to review.
     */
    @Test
    void testPrevCardEndofCards() {
        // Assert
        Assertions.assertThrows(EndOfCards.class, () -> {
            // Trying to go to the previous card should throw EndOfCards exception
            studyMode.goToPreviousCard();
        });
    }
}
