package requests;

/**
 * 3/1/26: added for p3 apis (login)
 * @param username
 * @param password
 */
public record LoginRequest(
        String username,
        String password
) {}
