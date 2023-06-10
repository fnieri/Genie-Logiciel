package ulb.infof307.g04.parsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.patterns.CardFactory;
import ulb.infof307.g04.models.AbstractCard;
import java.util.List;

public class CardParser {

    public AbstractCard parse(String json) throws JsonProcessingException {
        CardFactory cardFactory = new CardFactory();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode cardJson = objectMapper.readTree(json);

        int id = cardJson.get("id").asInt();
        int deckId = cardJson.get("deck").asInt();

        String question = cardJson.get("question").asText();
        String answer = cardJson.get("answer").asText();

        CardType questionType = CardType.fromString(cardJson.get("question_type").asText());
        CardType answerType = CardType.fromString(cardJson.get("answer_type").asText());
        // Extract the 'mastery_level' field from the JsonNode object and assign it to the corresponding field in the Card object.
        // The 'mastery_level' field is optional, so we need to check if it is present before trying to extract it.
        JsonNode masteryLevelJson = cardJson.get("mastery_level");

        CardMasteryLevels cardMasteryLevel = CardMasteryLevels.NOT_YET_LEARNED;
        if (masteryLevelJson != null) {
            cardMasteryLevel = CardMasteryLevels.fromInt(masteryLevelJson.asInt());
        }

        boolean hasMultipleAnswers = answerType.isType(CardType.BLNK) || answerType.isType(CardType.MCQ);
        if (hasMultipleAnswers) {
            CardContentParser cardContentParser = new CardContentParser();
            List<String> answers = cardContentParser.parseAnswers(answer);
            return cardFactory.makeCard(id, deckId, question, answers, questionType, answerType, cardMasteryLevel);
        }
        return cardFactory.makeCard(id, deckId, question, answer, questionType, answerType, cardMasteryLevel);
    }


}

