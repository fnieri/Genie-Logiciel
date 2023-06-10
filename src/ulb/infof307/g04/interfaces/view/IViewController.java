package ulb.infof307.g04.interfaces.view;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;

import javafx.scene.control.Alert.AlertType;
import ulb.infof307.g04.enums.EPages;

public interface IViewController {
    void showAlert(AlertType type, String title, String header, String content);
    void switchView(EPages page);
    void switchView(EPages page, HashMap<String, Object> params);
    Optional<String> showTextInputDialog(String title, String headerText, String contentText, String defaultValue);
    File showSaveFileDialog(String title, String filename);
    File showOpenFileDialog(String title);
    void showProgress(String message);
    void hideProgress();
}
