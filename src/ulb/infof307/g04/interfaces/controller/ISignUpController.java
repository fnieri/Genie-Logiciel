package ulb.infof307.g04.interfaces.controller;

public interface ISignUpController extends IController { 
    /**
     * Signs up the user for the application.
     *
     * @param username - the user name to register
     * @param passowrd - the password of the new account
     * @param confirmPassword - the password confirmation
     */
    void signup(String username, String password, String confirmPassword);

    /**
     * Switches to the login view.
     */
    void goToLogin();
}