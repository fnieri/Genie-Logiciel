package ulb.infof307.g04.services.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class DeckTest {

    private Deck deck;

    @BeforeEach
    public void setUp() {
        List<Card> cards = Arrays.asList(
                new Card(1, 1, "What is the capital of France?", "Paris"),
                new Card(2, 1, "What is the tallest mountain in the world?", "Mount Everest"),
                new Card(3, 1, "What is the formula for water?", "H2O")
        );
        List<String> tags = Arrays.asList("geography", "science");
        List<String> users = Arrays.asList("alice", "bob");
        deck = new Deck(1, "Test Deck", tags, users, cards);
    }

    @Test
    void testGetDatabaseId() {
        Assertions.assertEquals(1, deck.getId());
    }

    @Test
    void testGetName() {
        Assertions.assertEquals("Test Deck", deck.getName());
    }

    @Test
    void testGetTags() {
        List<String> expectedTags = Arrays.asList("geography", "science");
        Assertions.assertEquals(expectedTags, deck.getTags());
    }

    @Test
    void testGetCards() {
        List<Card> expectedCards = Arrays.asList(
                new Card(1, 1, "What is the capital of France?", "Paris"),
                new Card(2, 1, "What is the tallest mountain in the world?", "Mount Everest"),
                new Card(3, 1, "What is the formula for water?", "H2O")
        );
        // toString() is used to compare the lists properly, not theirs instances
        Assertions.assertEquals(expectedCards.toString(), deck.getCards().toString());    }

    @Test
    void testGetUsers() {
        List<String> expectedUsers = Arrays.asList("alice", "bob");
        Assertions.assertEquals(expectedUsers, deck.getUsers());
    }

    @Test
    void testSize() {
        Assertions.assertEquals(3, deck.size());
    }


    // TODO: test the JSON Parsing methods
    @Test
    void testDeckJsonParsing() {
    }
}
