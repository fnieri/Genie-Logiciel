package ulb.infof307.g04.controllers;

import javafx.scene.control.Alert;
import ulb.infof307.g04.AppState;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.factory.UserServiceFactory;
import ulb.infof307.g04.interfaces.controller.ISettingsController;
import ulb.infof307.g04.interfaces.services.IUserService;
import ulb.infof307.g04.interfaces.view.ISettingsViewController;

import java.util.concurrent.CompletionException;

/**
 * Controller for sub-view Settings of the Home view (declared in
 * settings.fmxl).
 * <p>
 * The Settings subview contains a user's settings utilities for the application
 * (e.g. change its password).
 *
 * @see ulb.infof307.g04.view.SettingsViewController
 * @see ulb.infof307.g04.interfaces.ISettingsController
 */
public class SettingsController extends AbstractController implements ISettingsController {
    private final AppState appState;
    private final ISettingsViewController view;
    private final IUserService userService = UserServiceFactory.getInstance();

    /**
     * Constructor for the SettingsController
     *
     * @param view the view that the controller will control
     */
    public SettingsController(ISettingsViewController view) {
        super(view);
        appState = AppState.getInstance();
        this.view = view;
    }

    @Override
    public void initialize() {
        super.initialize();

        runAsync(
                () -> {
                    try {
                        return userService.getUser(appState.getUserId());
                    } catch (ApiException e) {
                        throw new CompletionException(e);
                    }
                },
                user -> {
                    view.setUsername(user.username());
                },
                "Loading your data",
                "Failed to load your data"
        );
    }

    @Override
    public void editPassword(String oldPassword, String newPassword, String confirmPassword) {

        runAsync(
                () -> {
                    try {
                        userService.editPassword(oldPassword, newPassword, confirmPassword);
                    } catch (ApiException e) {
                        throw new CompletionException(e);
                    }
                },
                () -> {
                    view.showAlert(Alert.AlertType.INFORMATION, "Password Changed", "Your password has been changed.", "");
                    view.clearForm();
                },
                "Changing your password",
                "Failed to change your password"
        );

    }
}