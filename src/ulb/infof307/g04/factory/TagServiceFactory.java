package ulb.infof307.g04.factory;

import ulb.infof307.g04.interfaces.services.ITagService;

public class TagServiceFactory {

    private TagServiceFactory() {}
    private static ITagService service;

    public static void setInstance(ITagService service) {
        TagServiceFactory.service = service;
    }

    public static ITagService getInstance() {
        return TagServiceFactory.service;
    }
}
