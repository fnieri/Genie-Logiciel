package ulb.infof307.g04.exceptions.api;

/**
 * Thrown when there is an issue with an API
 * request to the server
 */
public class ApiException extends Exception {
    public ApiException(String message) {
        super(message);
    }
}