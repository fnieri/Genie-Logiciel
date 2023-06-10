package ulb.infof307.g04.exceptions.api;

/**
 * Thrown when the user enters a wrong current password at 
 * password change.
 *
 * @see ulb.infof307.g04.services.UserService
 */
public class InvalidOldPasswordException extends ApiException{
    public InvalidOldPasswordException(String message) {
        super("Invalid old password." + message);
    }
}