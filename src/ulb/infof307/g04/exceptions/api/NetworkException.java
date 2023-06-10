package ulb.infof307.g04.exceptions.api;

/**
 * Thrown when an HTTP request can't be sent properly.
 *
 * @see ulb.infof307.g04.services.AbstractAPIService
 */
public class NetworkException extends ApiException {
    public NetworkException() {
        super("A network error occurred.");
    }
}