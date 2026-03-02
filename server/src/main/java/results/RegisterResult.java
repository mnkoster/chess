package results;

/**
 * 3/1/26: added for p3 apis (register)
 * @param username
 * @param authToken
 */
public record RegisterResult(
        String username,
        String authToken
) {}
