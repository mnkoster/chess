package handler;

/**
 * 3/1/26: added to handle bad http requests
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
