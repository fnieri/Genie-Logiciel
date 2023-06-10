package ulb.infof307.g04.exceptions.api;

/**
 * Thrown when the request triggers an HTTP 401 : 
 * Unauthorized status code
 */
public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) {
        super("Unauthorized access." + message);
    }
}