package ulb.infof307.g04.factory;

import ulb.infof307.g04.interfaces.services.IQuizResultService;

public class QuizResultServiceFactory {
    private static IQuizResultService service;

    private QuizResultServiceFactory() {}
    public static void setInstance(IQuizResultService service) {
        QuizResultServiceFactory.service = service;
    }

    public static IQuizResultService getInstance() {
        return QuizResultServiceFactory.service;
    }
}
