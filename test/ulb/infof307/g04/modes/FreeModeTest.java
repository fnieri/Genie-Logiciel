package ulb.infof307.g04.modes;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.exceptions.deck.DeckDoesNotExist;
import ulb.infof307.g04.exceptions.deck.DeckIsEmpty;
import ulb.infof307.g04.exceptions.deck.EndOfCards;
import ulb.infof307.g04.exceptions.HasNotReviewedCardException;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.models.Deck;
import ulb.infof307.g04.patterns.CardFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class FreeModeTest {
    Deck deck;
    FreeMode freeMode;

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
        deck = new Deck(deckId,  "a", List.of("b"), List.of(), cards);

        freeMode = new FreeMode(deck);
    }



    /**
     * Test that the score updation is correct
     * Check that the score is reset correctly and that the final score represents what it is supposed to be
     */
    @Test
    void getScoreTest() {
        int score = 0;

        for (;;) {
            try {
                //Generate a random mastery level to be added
                CardMasteryLevels cardMasteryLevel = CardMasteryLevels.values()[new Random().nextInt(CardMasteryLevels.values().length)];
                score += cardMasteryLevel.score();

                //Test that the score is correctly added
                int freeModeScoreBefore = freeMode.getScore();
                freeMode.handleMastery(cardMasteryLevel);
                try {
                    freeMode.goToNextCard();
                }
                catch (EndOfCards e) {
                    break;
                }
                int freeModeScoreAfter = freeMode.getScore();
                assertEquals(cardMasteryLevel.score(), freeModeScoreAfter - freeModeScoreBefore);
            }
            catch (HasNotReviewedCardException ignored) {
            }
        }
    }

}