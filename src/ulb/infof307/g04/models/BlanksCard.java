package ulb.infof307.g04.models;

import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.patterns.CardVisitor;

import java.util.Collections;
import java.util.List;

public class BlanksCard extends AbstractCard {
    private final List<String> answers;

    public BlanksCard(int id, int deck, String question, CardType questionType, List<String> answers, CardMasteryLevels cardMasteryLevel) {
        super(id, deck, question, questionType, cardMasteryLevel);
        this.answers = answers;
    }

    public List<String> getAnswers() {
        return Collections.unmodifiableList(answers);
    }

    public void accept(CardVisitor visitor) {
        visitor.visit(this);
    }

    public String getAnswer() {
        String delimiter = ";";
        return String.join(delimiter, answers);
    }

    public CardType getAnswerType() {
        return CardType.BLNK;
    }
}
