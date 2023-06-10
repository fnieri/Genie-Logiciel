package ulb.infof307.g04.services.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static org.junit.Assert.*;

public class CardTest {

    /**
     * Tests the Card constructor.
     * Creates a new card and checks if its fields have the correct values.
     */
    @Test
    public void testCardConstructor() {
        Card card = new Card(1, 2, "What is the capital of France?", "Paris");
        Assert.assertEquals(1, card.getId());
        Assert.assertEquals(2, card.getDeck());
        Assert.assertEquals("What is the capital of France?", card.getQuestion());
        Assert.assertEquals("Paris", card.getAnswer());
    }

    /**
     * Tests the Card constructor that takes a JSON string as input.
     * Creates a new card from a JSON string and checks if its fields have the correct values.
     */
    @Test
    public void testCardJsonConstructor() throws JsonProcessingException {
        String json = "{\"id\":1,\"deck\":2,\"question\":\"What is the capital of France?\",\"answer\":\"Paris\", \"answer_type\":\"plain\", \"question_type\":\"plain\"}";
        Card card = new Card(json);
        Assert.assertEquals(1, card.getId());
        Assert.assertEquals(2, card.getDeck());
        Assert.assertEquals("What is the capital of France?", card.getQuestion());
        Assert.assertEquals("Paris", card.getAnswer());
    }

    /**
     * Tests the setters and getters of the Card class.
     * Creates a new card, sets its fields to new values, and checks if the new values were correctly set.
     */
    @Test
    public void testCardSettersAndGetters() {
        Card card = new Card(1, 2, "What is the capital of France?", "Paris");
        card.setDeck(3);
        card.setQuestion("What is the capital of Spain?");
        card.setAnswer("Madrid");
        Assert.assertEquals(3, card.getDeck());
        Assert.assertEquals("What is the capital of Spain?", card.getQuestion());
        Assert.assertEquals("Madrid", card.getAnswer());
    }

    /**
     * Tests the toString() method of the Card class.
     * Creates a new card and checks if its toString() method returns the expected string.
     */
    @Test
    public void testCardToString() {
        Card card = new Card(1, 2, "What is the capital of France?", "Paris");
        String expected = "Card{id=1', question='What is the capital of France?', answer='Paris', masteryLevel='NOT_YET_LEARNED'}";
        Assert.assertEquals(expected, card.toString());
    }
}
