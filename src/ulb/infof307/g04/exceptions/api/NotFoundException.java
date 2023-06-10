package ulb.infof307.g04.exceptions.api;

/**
 * Thrown when the request triggers an HTTP 500 : 
 * Internal Server Error status code
 */
public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super("Resource not found." + message);
    }
}