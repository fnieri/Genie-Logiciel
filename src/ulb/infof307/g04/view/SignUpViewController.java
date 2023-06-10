package ulb.infof307.g04.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ulb.infof307.g04.controllers.SignupController;
import ulb.infof307.g04.interfaces.controller.ISignUpController;
import ulb.infof307.g04.interfaces.view.IAppViewController;
import ulb.infof307.g04.interfaces.view.ISignUpViewController;

import java.util.HashMap;

/**
 * The signup view (declared in signup.fxml)
 * The Signup view is used to sign-up as a new user for the application.
 */
public class SignUpViewController extends AbstractViewController implements ISignUpViewController {
    private final ISignUpController controller;

    @FXML private PasswordField confirmPasswordSignup;
    @FXML private PasswordField createPasswordSignup;
    @FXML private TextField usernameSignup;

    public SignUpViewController(IAppViewController app, HashMap<String, Object> params)  {
        super(app);
        this.controller = new SignupController(this);
    }

    /**
     * Handle user onclick request to go to signup page

     */
    @FXML
    void goToLogin() {
        controller.goToLogin();
    }

    /**
     * Handle user request to signup
     */
    @FXML
    void signUp() {
        String username = usernameSignup.getText();
        String password = createPasswordSignup.getText();
        String confirmPassword = confirmPasswordSignup.getText();
        controller.signup(username, password, confirmPassword);
    }

    @Override
    public void clearForm() {
        createPasswordSignup.clear();
        confirmPasswordSignup.clear();
    }
}