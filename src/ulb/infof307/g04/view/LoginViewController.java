package ulb.infof307.g04.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import ulb.infof307.g04.controllers.LoginController;
import ulb.infof307.g04.interfaces.controller.ILoginController;
import ulb.infof307.g04.interfaces.view.IAppViewController;
import ulb.infof307.g04.interfaces.view.ILoginViewController;

import java.util.HashMap;

public class LoginViewController extends AbstractViewController implements ILoginViewController {
    @FXML private PasswordField passwordLogin;
    @FXML private TextField usernameLogin;

    private final ILoginController controller;

    public LoginViewController(IAppViewController app, HashMap<String, Object> params){
        super(app);
        this.controller = new LoginController(this);
    }

    /**
     * Event handler for switching to the sign-up view
     * 
     * @param event mouseClick
     */
    @FXML
    void goToSignUp(MouseEvent event) {
        controller.goToSignUp();
    }

    /**
     * Event handler for authenticating a user
     * 
     * @param event
     */
    @FXML
    void login(ActionEvent event) {
        String username = usernameLogin.getText();
        String password = passwordLogin.getText();
        controller.login(username, password);
    }

    @Override
    public void clearForm() {
        usernameLogin.clear();
        passwordLogin.clear();
    }
}
