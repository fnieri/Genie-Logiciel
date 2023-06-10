package ulb.infof307.g04.interfaces.models;

import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.patterns.CardVisitor;

public interface ICard {
    int getId();
    int getDeck();
    String getQuestion();
    CardType getQuestionType();
    CardType getAnswerType();
    void setQuestion(String question);
    void setQuestionType(CardType questionType);
    CardMasteryLevels getMasteryLevel();
    void setMasteryLevel(CardMasteryLevels cardMasteryLevel);
    void accept(CardVisitor visitor);
    String getAnswer();
}
