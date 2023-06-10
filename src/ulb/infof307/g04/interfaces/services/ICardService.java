package ulb.infof307.g04.interfaces.services;

import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.interfaces.models.ICard;

import java.util.List;

public interface ICardService {
    int createCard(int deckId, String question, CardType questionType, String answer, CardType answerType) throws ApiException;
    ICard getCard(int cardId) throws ApiException;

    List<ICard> getCards() throws ApiException;

    void updateCardQuestion(int cardId, String question) throws ApiException;
    void updateCardAnswer(int cardId, String answer) throws ApiException;
    void updateCardType(int cardId, CardType questionType, CardType answerType) throws ApiException;
    void deleteCard(int cardId) throws ApiException;
}
