package ulb.infof307.g04.controllers;

import java.util.concurrent.CompletionException;

import ulb.infof307.g04.AppState;
import ulb.infof307.g04.enums.EPages;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.factory.UserServiceFactory;
import ulb.infof307.g04.interfaces.controller.ISignUpController;
import ulb.infof307.g04.interfaces.services.IUserService;
import ulb.infof307.g04.interfaces.view.ISignUpViewController;

/**
 * Controller for the Signup view (declared in signup.fmxl).
 * <p>
 * Acts as a listener for SignUpViewController
 *
 * @see ulb.infof307.g04.view.SignUpViewController
 * @see ulb.infof307.g04.interfaces.ISignUpController
 */
public class SignupController extends AbstractController implements ISignUpController {
    private final ISignUpViewController view;
    private final IUserService userService = UserServiceFactory.getInstance();
    private final AppState appState;

    /**
     * Constructor for the SignUpController
     *
     * @param view the view that the controller will control
     */
    public SignupController(ISignUpViewController view) {
        super(view);
        appState = AppState.getInstance();
        this.view = view;
    }

    @Override
    public void signup(String username, String password, String confirmPassword) {
        runAsync(
                () -> {
                    try {
                        return userService.signUp(username, password, confirmPassword);
                    } catch (ApiException e) {
                        throw new CompletionException(e);
                    }
                },
                user -> {
                    appState.setUserId(user.id());
                    view.switchView(EPages.PROFILE);
                    view.clearForm();
                },
                "Creating your account!"
        );
    }

    @Override
    public void goToLogin() {
        view.switchView(EPages.SIGN_IN);
    }
}