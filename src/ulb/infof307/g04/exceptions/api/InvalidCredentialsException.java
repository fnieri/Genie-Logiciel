package ulb.infof307.g04.exceptions.api;

/**
 * Thrown when the user enters wrong credentials at sign-in.
 *
 * @see ulb.infof307.g04.services.UserService
 */
public class InvalidCredentialsException extends ApiException {
    public InvalidCredentialsException(String message) {
        super("Invalid credentials." + message);
    }
}