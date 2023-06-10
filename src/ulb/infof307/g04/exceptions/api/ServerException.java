package ulb.infof307.g04.exceptions.api;

/**
 * Thrown when the request triggers an HTTP 500 : 
 * Internal Server Error status code
 */
public class ServerException extends ApiException {
    public ServerException(String message) {
        super("An internal server error occurred. " + message);
    }
}