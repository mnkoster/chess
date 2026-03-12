package results;

/**
 * 3/1/26: added for p3 apis - login
 * @param username
 * @param authToken
 */
public record LoginResult(
        String username,
        String authToken
) {}
