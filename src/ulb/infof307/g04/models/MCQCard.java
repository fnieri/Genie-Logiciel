package ulb.infof307.g04.models;

import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.patterns.CardVisitor;

import java.util.Collections;
import java.util.List;

public class MCQCard extends AbstractCard{
    private List<String> answers;

    public MCQCard(int id, int deck, String question, CardType questionType, List<String> answers, CardMasteryLevels cardMasteryLevel) {
        super(id, deck, question, questionType, cardMasteryLevel);
        this.answers = answers;
    }

    public List<String> getAnswers() {
        return Collections.unmodifiableList(answers);
    }

    public void accept(CardVisitor visitor) {
        visitor.visit(this);
    }

    public String getCorrectAnswer() {
        return answers.get(0);
    }

    public String getAnswer() {
        return String.join("; ", answers);
    }

    public CardType getAnswerType() {
        return CardType.MCQ;
    }
}
