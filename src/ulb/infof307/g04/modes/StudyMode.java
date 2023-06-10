package ulb.infof307.g04.modes;

import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.exceptions.deck.DeckIsEmpty;
import ulb.infof307.g04.exceptions.deck.EndOfCards;
import ulb.infof307.g04.factory.QuizResultServiceFactory;
import ulb.infof307.g04.interfaces.services.IQuizResultService;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.patterns.LearningModeVisitor;
import ulb.infof307.g04.models.Deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author @fnieri pilote + refactor @lpalmisa Copilote + refactor @bmalyane Copilote
 * The study mode class, extends {@code LearningMode}
 */

public class StudyMode extends LearningMode {
    private final List<ICard> cardsToReview;
    private int currentCardIndex = 0;
    private final IQuizResultService quizResultService = QuizResultServiceFactory.getInstance();

    public StudyMode(Deck deck) throws DeckIsEmpty {
        super(deck);
        List<ICard> deckToShuffle = new ArrayList<>(deck.getCards());
        Collections.shuffle(deckToShuffle);
        cardsToReview = new ArrayList<>(deckToShuffle);
    }

    @Override
    public void goToNextCard() throws EndOfCards {
        currentCardIndex++;
        if (currentCardIndex >= cardsToReview.size()) throw new EndOfCards("");
    }

    @Override
    public void goToPreviousCard() throws EndOfCards {
        currentCardIndex--;
        if (currentCardIndex < 0) throw new EndOfCards("");
    }

    @Override
    public ICard getCurrentCard() {
        return cardsToReview.get(currentCardIndex);
    }


    public void accept(LearningModeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getRules() {
        return "The optimal study experience.\n" +
                "The questions you handle the worst come back more often, study until you get the answers right.";
    }

    @Override
    public int getProgress() {
        return currentCardIndex;
    }

    /**
     * Add the card mastery to the database
     * @param newMasteryLevel  The mastery level selected by the user
     */
    @Override
    public void handleMastery(CardMasteryLevels newMasteryLevel) throws ApiException {
        quizResultService.createQuizResult(getCurrentCard().getId(), newMasteryLevel.score());
    }
}