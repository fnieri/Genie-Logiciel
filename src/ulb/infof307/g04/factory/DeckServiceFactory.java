package ulb.infof307.g04.factory;

import ulb.infof307.g04.interfaces.services.IDeckService;

public class DeckServiceFactory {
    private static IDeckService service;

    private DeckServiceFactory() {}
    public static void setInstance(IDeckService service) {
        DeckServiceFactory.service = service;
    }

    public static IDeckService getInstance() {
        return DeckServiceFactory.service;
    }
}
