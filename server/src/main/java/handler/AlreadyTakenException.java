package handler;

/**
 * 3/1/26: added for p3 apis (register)
 * throws error if username already taken
 */
public class AlreadyTakenException extends RuntimeException {
    public AlreadyTakenException(String message) {
        super(message);
    }
}
