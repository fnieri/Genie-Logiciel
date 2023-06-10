package ulb.infof307.g04.exceptions.api;

/**
 * Thrown when the user can't be found.
 *
 * @see ulb.infof307.g04.services.UserService
 */
public class UserNotFoundException extends ApiException {
    public UserNotFoundException(String message) {
        super("User not found." + message);
    }
}