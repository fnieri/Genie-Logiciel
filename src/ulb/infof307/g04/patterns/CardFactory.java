package ulb.infof307.g04.patterns;

import ulb.infof307.g04.models.AbstractCard;
import ulb.infof307.g04.models.BlanksCard;
import ulb.infof307.g04.models.MCQCard;
import ulb.infof307.g04.models.OpenAnswerCard;
import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;

import java.util.List;

public class CardFactory {

    private BlanksCard makeBlanksCard(int id, int deck, String question, CardType questionType, List<String> answers, CardMasteryLevels cardMasteryLevel) {
        return new BlanksCard(id, deck, question, questionType, answers, cardMasteryLevel);
    }

    private MCQCard makeMCQCard(int id, int deck, String question, CardType questionType, List<String> answers, CardMasteryLevels cardMasteryLevel) {
        return new MCQCard(id, deck, question, questionType, answers, cardMasteryLevel);
    }

    private AbstractCard makeOpenAnswerCard(int id, int deck, String question, String answer, CardType questionType, CardType answerType, CardMasteryLevels cardMasteryLevel) {
        return new OpenAnswerCard(id, deck, question, answer, questionType, answerType, cardMasteryLevel);
    }

    public AbstractCard makeCard(int id, int deck, String question, String answer, CardType questionType, CardType answerType, CardMasteryLevels cardMasteryLevel) {
        return makeOpenAnswerCard(id, deck, question, answer, questionType, answerType, cardMasteryLevel);
    }

    public AbstractCard makeCard(int id, int deck, String question, List<String> answers, CardType questionType, CardType answerType, CardMasteryLevels cardMasteryLevel) {
        if (answerType.isType(CardType.MCQ))
            return makeMCQCard(id, deck, question, questionType, answers, cardMasteryLevel);
        else if (answerType.isType(CardType.BLNK))
            return makeBlanksCard(id, deck, question, questionType, answers, cardMasteryLevel);
        throw new IllegalArgumentException("Invalid card type");
    }
}
