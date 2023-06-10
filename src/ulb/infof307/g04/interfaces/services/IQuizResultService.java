package ulb.infof307.g04.interfaces.services;


import ulb.infof307.g04.exceptions.api.ApiException;

public interface IQuizResultService {
    int createQuizResult(int cardId, int performanceType) throws ApiException;
}
