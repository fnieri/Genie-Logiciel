package ulb.infof307.g04.interfaces.controller;

public interface ILoginController extends IController {

    /**
     * Signs the user in the app.
     * 
     * @param username - the name of the user
     * @param password - the password of the user
     */
    void login(String username, String password);

    /**
     * Switches to the sign-up view.
     */
    void goToSignUp();
}