package ulb.infof307.g04.factory;

import ulb.infof307.g04.interfaces.services.IStoreDeckService;

public class StoreDeckServiceFactory {
    private static IStoreDeckService service;

    private StoreDeckServiceFactory() {}
    public static void setInstance(IStoreDeckService service) {
        StoreDeckServiceFactory.service = service;
    }

    public static IStoreDeckService getInstance() {
        return StoreDeckServiceFactory.service;
    }
}