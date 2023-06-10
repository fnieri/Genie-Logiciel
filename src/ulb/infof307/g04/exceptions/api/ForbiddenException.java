package ulb.infof307.g04.exceptions.api;

/**
 * Thrown when the request triggers an HTTP 403 : 
 * Forbidden status code
 */
public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super("Access is forbidden." + message);
    }
}