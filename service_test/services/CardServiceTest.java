package ulb.infof307.g04.services;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.factory.CardServiceFactory;
import ulb.infof307.g04.factory.DeckServiceFactory;
import ulb.infof307.g04.factory.QuizResultServiceFactory;
import ulb.infof307.g04.factory.TagServiceFactory;
import ulb.infof307.g04.factory.UserServiceFactory;
import ulb.infof307.g04.interfaces.services.ICardService;
import ulb.infof307.g04.interfaces.services.IDeckService;
import ulb.infof307.g04.services.models.Card;
import ulb.infof307.g04.services.models.Deck;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class CardServiceTest {
    private ICardService cardService;
    private IDeckService deckService;

    @BeforeEach
    void setUp() throws ApiException {
        String baseUri = "http://127.0.0.1:8000";
        UserServiceFactory.setInstance(new UserService(baseUri));
        TagServiceFactory.setInstance(new TagService(baseUri));
        CardServiceFactory.setInstance(new CardService(baseUri));
        DeckServiceFactory.setInstance(new DeckService(baseUri));
        QuizResultServiceFactory.setInstance(new QuizResultService(baseUri));

        cardService = CardServiceFactory.getInstance();
        deckService = DeckServiceFactory.getInstance();
        UserServiceFactory.getInstance().signIn("admin", "password");
    }

    @Test
    void testCreateCard() throws ApiException {
        // create a deck to add the card to
        int deckId = deckService.createDeck("Test Deck", new ArrayList<>());
        Deck deck = deckService.getDeck(deckId);

        int cardId = cardService.createCard(deckId, "Test Question", "Test Answer", CardType.PLAIN_TEXT);
        Card createdCard = cardService.getCard(cardId);

        // check that the card was created
        Assertions.assertNotNull(createdCard);
        Assertions.assertEquals("Test Question", createdCard.getQuestion());
        Assertions.assertEquals("Test Answer", createdCard.getAnswer());
        deckService.deleteDeck(deckId);
    }

    @Test
    void testUpdateCardQuestion() throws ApiException {
        int deckId = deckService.createDeck("Test Deck", new ArrayList<>());
        // retrieve the deck from the API
        Deck deck = deckService.getDeck(deckId);
        // create a card to add to the deck
        int cardId = cardService.createCard(deckId, "Test Question", "Test Answer", CardType.PLAIN_TEXT);
        // update the card's question
        String newQuestion = "New Question33";
        String newAnswer = "Answer33";
        cardService.updateCardQuestion(cardId, newQuestion);

        Card createdCard = cardService.getCard(cardId);
        Assertions.assertNotNull(createdCard);
        Assertions.assertEquals(newQuestion, createdCard.getQuestion());
        deckService.deleteDeck(deckId);
    }

    @Test
    void testUpdateCardAnswer() throws ApiException {
        int deckId = deckService.createDeck("Test Deck", new ArrayList<>());
        // retrieve the deck from the API
        Deck deck = deckService.getDeck(deckId);
        // create a card to add to the deck
        int cardId = cardService.createCard(deckId, "Test Question", "Test Answer", CardType.PLAIN_TEXT);
        // update the card's question
        String newQuestion = "Question33";
        String newAnswer = "New Answer33";
        cardService.updateCardAnswer(cardId, newAnswer);

        Card createdCard = cardService.getCard(cardId);
        Assertions.assertNotNull(createdCard);
        Assertions.assertEquals(newAnswer, createdCard.getAnswer());
        deckService.deleteDeck(deckId);
    }

    @Test
    void testDeleteCard() throws ApiException {
        int deckId = deckService.createDeck("Test Deck", new ArrayList<>());
        Deck deck = deckService.getDeck(deckId);
        int cardId = cardService.createCard(deckId, "Test Question", "Test Answer", CardType.PLAIN_TEXT);
        // delete the card
        cardService.deleteCard(cardId);
        Assert.assertThrows(ApiException.class, () -> cardService.getCard(cardId));
    }
}

