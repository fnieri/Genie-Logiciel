package ulb.infof307.g04.controllers;

import java.util.concurrent.CompletionException;

import ulb.infof307.g04.AppState;
import ulb.infof307.g04.enums.EPages;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.factory.UserServiceFactory;
import ulb.infof307.g04.interfaces.controller.ILoginController;
import ulb.infof307.g04.interfaces.services.IUserService;
import ulb.infof307.g04.interfaces.view.ILoginViewController;
import ulb.infof307.g04.view.LoginViewController;

/**
 * Controller for the Login view (declared in login.fmxl).
 * <p>
 * The Login view is used to log in the application as an existing user.
 * It is the first view displayed when launching the application.
 *
 * @see ulb.infof307.g04.view.LoginViewController
 * @see ulb.infof307.g04.interfaces.ILoginController
 */
public class LoginController extends AbstractController implements ILoginController {
    private final AppState appState;
    private final IUserService userService = UserServiceFactory.getInstance();
    private final ILoginViewController view;

    /**
     * Constructor for the LoginController.
     *
     * @param view The view associated with the controller.
     */
    public LoginController(LoginViewController view) {
        super(view);
        appState = AppState.getInstance();
        this.view = view;
    }

    @Override
    public void login(String username, String password) {
        runAsync(
                () -> {
                    try {
                        return userService.signIn(username, password);
                    } catch (ApiException e) {
                        throw new CompletionException(e);
                    }
                },
                user -> {
                    appState.setUserId(user.id());
                    view.switchView(EPages.PROFILE);
                    view.clearForm();
                },
                "Logging you in",
                "Failed to log you in"
        );
    }

    @Override
    public void goToSignUp() {
        view.switchView(EPages.SIGN_UP);
    }

}