package ulb.infof307.g04.exceptions.api;

/**
 * Thrown when the request triggers an HTTP 415 : 
 * Unsupported media type status code
 */
public class UnsupportedMediaTypeException extends ApiException {
    public UnsupportedMediaTypeException(String message) {
        super("Unsupported media type." + message);
    }
}