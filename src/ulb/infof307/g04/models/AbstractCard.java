package ulb.infof307.g04.models;

import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.interfaces.models.ICard;

public abstract class AbstractCard implements ICard {
    private int id;
    private int deck;
    private String question;
    private CardType questionType;
    private CardMasteryLevels cardMasteryLevel;

    AbstractCard(int id, int deck, String question, CardType questionType, CardMasteryLevels cardMasteryLevel) {
        this.id = id;
        this.deck = deck;
        this.question = question;
        this.questionType = questionType;
        this.cardMasteryLevel = cardMasteryLevel;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getDeck() {
        return this.deck;
    }

    @Override
    public String getQuestion() {
        return this.question;
    }

    @Override
    public CardType getQuestionType() {
        return this.questionType;
    }

    @Override
    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public void setQuestionType(CardType questionType) {
        this.questionType = questionType;
    }

    @Override
    public CardMasteryLevels getMasteryLevel() {
        return this.cardMasteryLevel;
    }

    @Override
    public void setMasteryLevel(CardMasteryLevels cardMasteryLevel) {
        this.cardMasteryLevel = cardMasteryLevel;
    }
}
