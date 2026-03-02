package handler;

/**
 * 3/1/26: added for p3 apis
 */
public class ErrorResponse extends RuntimeException {
    public ErrorResponse(String message) {
        super(message);
    }
}
