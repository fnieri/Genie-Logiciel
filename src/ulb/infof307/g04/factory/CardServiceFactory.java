package ulb.infof307.g04.factory;

import ulb.infof307.g04.interfaces.services.ICardService;

public class CardServiceFactory {
    private static ICardService service;

    private CardServiceFactory() {}

    public static void setInstance(ICardService service) {
        CardServiceFactory.service = service;
    }

    public static ICardService getInstance() {
        return CardServiceFactory.service;
    }
}
