package ulb.infof307.g04.view;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import ulb.infof307.g04.enums.EPages;
import ulb.infof307.g04.interfaces.view.IAppViewController;
import ulb.infof307.g04.interfaces.view.IViewController;
import ulb.infof307.g04.patterns.AlertFactory;
import ulb.infof307.g04.widgets.ProgressIndicatorView;

public abstract class AbstractViewController implements IViewController {
    protected IAppViewController app;
    protected ProgressIndicatorView progressIndicatorView;
    protected AlertFactory alertFactory;

    AbstractViewController(IAppViewController app) {
        this.app = app;
        alertFactory = new AlertFactory();
        this.progressIndicatorView = new ProgressIndicatorView();
    }

    public void showProgress(String message) {
        progressIndicatorView.show(message);
    }

    public void hideProgress() {
        progressIndicatorView.hide();
    }

    @Override
    public void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = alertFactory.makeAlert(type, title, header, content);
        alert.show();
    }


    @Override
    public void switchView(EPages page) {
        this.switchView(page, (HashMap<String, Object>) null);
    }

    @Override
    public void switchView(EPages page, HashMap<String, Object> params) {
        app.handleViewChange(page, params);
    }


    @Override
    public Optional<String> showTextInputDialog(String title, String headerText, String contentText, String defaultValue) {
        AlertFactory factory = new AlertFactory();
        Dialog<String> dialog = factory.makeTextInputDialog(title, headerText, contentText, defaultValue);
        return dialog.showAndWait();
    }

    @Override
    public File showSaveFileDialog(String title, String filename) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.setInitialFileName(filename);
        return chooser.showSaveDialog(this.app.getWindow());
    }

    @Override
    public File showOpenFileDialog(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        return chooser.showOpenDialog(this.app.getWindow());
    }
}
