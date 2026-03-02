package dataaccess;

/**
 * 3/1/26: added for p3 apis (unauthorized users)
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
