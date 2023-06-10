package ulb.infof307.g04.exceptions.api;

/**
 * Thrown when the user changing his password fails to 
 * confirm the new password.
 *
 * @see ulb.infof307.g04.services.UserService
 */
public class PasswordMismatchException extends ApiException {
    public PasswordMismatchException(String message) {
        super("The password is incorrect." + message);
    }
}