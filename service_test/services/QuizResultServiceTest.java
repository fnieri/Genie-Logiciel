package ulb.infof307.g04.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.factory.CardServiceFactory;
import ulb.infof307.g04.factory.DeckServiceFactory;
import ulb.infof307.g04.factory.QuizResultServiceFactory;
import ulb.infof307.g04.factory.TagServiceFactory;
import ulb.infof307.g04.factory.UserServiceFactory;
import ulb.infof307.g04.interfaces.services.ICardService;
import ulb.infof307.g04.interfaces.services.IDeckService;
import ulb.infof307.g04.interfaces.services.IQuizResultService;
import ulb.infof307.g04.services.models.QuizResult;

public class QuizResultServiceTest {
    private IQuizResultService quizResultService;
    private IDeckService deckService;
    private ICardService cardService;

    @BeforeEach
    void setUp() throws ApiException {
        String baseUri = "http://127.0.0.1:8000";
        UserServiceFactory.setInstance(new UserService(baseUri));
        TagServiceFactory.setInstance(new TagService(baseUri));
        CardServiceFactory.setInstance(new CardService(baseUri));
        DeckServiceFactory.setInstance(new DeckService(baseUri));
        QuizResultServiceFactory.setInstance(new QuizResultService(baseUri));

        quizResultService = QuizResultServiceFactory.getInstance();
        deckService = DeckServiceFactory.getInstance();
        cardService = CardServiceFactory.getInstance();
        UserServiceFactory.getInstance().signIn("admin", "password");
    }


    @Test
    void testCreateAndGetQuizResult() throws ApiException {
        // Create a deck
        int deckId = deckService.createDeck("Test Deck", new ArrayList<>());

        // Create a card
        String question = "What is the capital of Belgium?";
        String answer = "Brussels";
        int cardId = cardService.createCard(deckId, question, answer, CardType.PLAIN_TEXT);

        // Create a result for the card
        int performanceType = CardMasteryLevels.GOOD.score();
        int quizResultId = quizResultService.createQuizResult(cardId, performanceType);

        // Check if the result was created
        QuizResult quizResult = quizResultService.getQuizResult(quizResultId);
        Assertions.assertEquals(CardMasteryLevels.GOOD.score(), quizResult.getPerformanceType().score());
    }
}
