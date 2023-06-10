package ulb.infof307.g04.factory;

import ulb.infof307.g04.interfaces.services.IStatisticService;

public class StatisticServiceFactory {
    private static IStatisticService service;
    private StatisticServiceFactory() {}

    public static void setInstance(IStatisticService service) {
        StatisticServiceFactory.service = service;
    }

    public static IStatisticService getInstance() {
        return StatisticServiceFactory.service;
    }
}
