package ulb.infof307.g04.view;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import ulb.infof307.g04.controllers.SettingsController;
import ulb.infof307.g04.interfaces.controller.ISettingsController;
import ulb.infof307.g04.interfaces.view.IAppViewController;
import ulb.infof307.g04.interfaces.view.ISettingsViewController;

public class SettingsViewController extends AbstractViewController implements Initializable, ISettingsViewController {
    @FXML PasswordField oldPasswordField;
    @FXML PasswordField newPasswordField;
    @FXML PasswordField confirmPasswordField;
    @FXML Text usernameText;

    private final ISettingsController controller;

    public SettingsViewController(IAppViewController app, HashMap<String, Object> params) {
        super(app);
        controller = new SettingsController(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller.initialize();
    }

    /**
     * Callback used when the user changes its password and hits the save button.
     */
    @FXML
    public void handlePasswordSaveBtn() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        controller.editPassword(oldPassword, newPassword, confirmPassword);
    }

    /**
     * Callback used when the user cancels his password change by hitting the cancel
     * button.
     */
    @FXML
    public void handlePasswordCancelBtn() {
        clearForm();
    }

    @Override
    public void setUsername(String username) {
        usernameText.setText(username);
    }

    @Override
    public void clearForm() {
        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
}
