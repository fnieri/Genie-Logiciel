package ulb.infof307.g04.models;

import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.patterns.CardVisitor;

public class OpenAnswerCard extends AbstractCard {
    String answer;
    CardType answerType;

    public OpenAnswerCard(int id, int deck, String question, String answer, CardType questionType, CardType answerType, CardMasteryLevels cardMasteryLevel) {
        super(id, deck, question, questionType, cardMasteryLevel);
        this.answer = answer;
        this.answerType = answerType;
    }

@Override
    public String getAnswer() {
        return answer;
    }

    public CardType getAnswerType() {
        return answerType;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void accept(CardVisitor visitor) {
        visitor.visit(this);
    }
}
