package ulb.infof307.g04.factory;

import ulb.infof307.g04.interfaces.services.IUserService;

public class UserServiceFactory {
    private static IUserService service;

    private UserServiceFactory() {}
    public static void setInstance(IUserService service) {
        UserServiceFactory.service = service;
    }

    public static IUserService getInstance() {
        return UserServiceFactory.service;
    }
}
