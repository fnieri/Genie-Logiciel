package ulb.infof307.g04.exceptions.api;

/**
 * Thrown when the sign up process doesn't complete properly.
 *
 * @see ulb.infof307.g04.services.UserService
 */
public class RegistrationErrorException extends ApiException{
    public RegistrationErrorException(String message) {
        super("Registration error." + message);
    }
}