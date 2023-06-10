package ulb.infof307.g04.parsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.interfaces.models.ICard;


import static org.junit.jupiter.api.Assertions.*;

class CardParserTest {
    private String question = "is this a test ?";
    private String answer = "Yes it is";
    private CardType questionType = CardType.PLAIN_TEXT;
    private CardType answerType = CardType.PLAIN_TEXT;
    private CardMasteryLevels cardMasteryLevel = CardMasteryLevels.NOT_YET_LEARNED;
    private String validCardJson = "{\"id\":0,\"question\":\"is this a test ?\",\"answer\":\"Yes it is\",\"question_type\":\"text\",\"answer_type\":\"text\",\"deck\":0,\"mastery_level\":0}";

    private String invalidCardJson = "{\"id\"=0}";
    @Test
    void parseValidCard() throws JsonProcessingException {
        CardParser parser = new CardParser();
        ICard card = parser.parse(validCardJson);
        assertEquals(card.getQuestion(), question);
        assertEquals(card.getAnswer(), answer);
        assertEquals(card.getQuestionType(), questionType);
        assertEquals(card.getAnswerType(), answerType);
        assertEquals(card.getMasteryLevel(), cardMasteryLevel);
    }

    @Test
    void parseInvalidCard() {
        CardParser parser = new CardParser();
        assertThrows(JsonProcessingException.class, () -> parser.parse(invalidCardJson));
    }
}

