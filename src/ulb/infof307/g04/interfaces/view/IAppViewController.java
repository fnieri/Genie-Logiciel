package ulb.infof307.g04.interfaces.view;

import javafx.stage.Window;
import ulb.infof307.g04.enums.EPages;

import java.util.HashMap;

public interface IAppViewController {
    Window getWindow();
    void handleViewChange(EPages page, HashMap<String, Object> params);
}
