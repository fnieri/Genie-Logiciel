package ulb.infof307.g04.parsers;

import com.fasterxml.jackson.core.JsonProcessingException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.models.Deck;
import ulb.infof307.g04.patterns.CardFactory;

import java.util.ArrayList;
import java.util.List;

class DeckParserTest {
    private int deckId = 0;
    private String name = "DeckTest";
    private List<String> tags = new ArrayList<>();
    private List<String> users = new ArrayList<>();
    private List<ICard> cards = new ArrayList<>();
    private String validJsonDeck = "{\"id\":0,\"name\":\"DeckTest\",\"tags\":[\"Test\",\"TagTest\"],\"users\":[\"Elliot\",\"Lucas\"],\"cards\":[{\"id\":0,\"question\":\"is this a test ?\",\"answer\":\"Yes it is\",\"question_type\":\"text\",\"answer_type\":\"text\",\"deck\":0,\"mastery_level\":0}]}";
    private String invalidDeckJson = "{\"id\"=0}";
    private String question = "is this a test ?";
    private String answer = "Yes it is";
    private CardType questionType = CardType.PLAIN_TEXT;
    private CardType answerType = CardType.PLAIN_TEXT;
    private CardMasteryLevels cardMasteryLevel = CardMasteryLevels.NOT_YET_LEARNED;

    public Deck createFakeDeck() {
        initializeFakeParams();
        return new Deck(deckId, name, tags, users, cards);
    }

    public void initializeFakeParams() {
        tags.add("Test");
        tags.add("TagTest");
        users.add("Elliot");
        users.add("Lucas");
        CardFactory cardFactory = new CardFactory();
        int id = 0;
        cards.add(cardFactory.makeCard(id, deckId, question, answer, questionType, answerType, cardMasteryLevel));
    }

    @Test
    void validDeckParseTest() throws JsonProcessingException {
        // NOTE: asserting for cards is in CardParserTest
        DeckParser deckParser = new DeckParser();
        Deck exeptedDeck = createFakeDeck();
        Deck testDeck = deckParser.parse(validJsonDeck);
        assertEquals(exeptedDeck.getName(), testDeck.getName());
        assertEquals(exeptedDeck.getTags(), testDeck.getTags());
        assertEquals(exeptedDeck.getId(), testDeck.getId());
        assertEquals(exeptedDeck.getUsers(), testDeck.getUsers());
    }

    @Test
    void invalidDeckParseTest(){
        DeckParser deckParser = new DeckParser();
        assertThrows(JsonProcessingException.class, () -> deckParser.parse(invalidDeckJson));
    }

}