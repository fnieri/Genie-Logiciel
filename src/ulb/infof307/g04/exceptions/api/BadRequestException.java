package ulb.infof307.g04.exceptions.api;

/**
 * Thrown when the request triggers an HTTP 400 : 
 * Bad request status code
 */
public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super("Bad request error." + message);
    }
}