package requests;

/**
 * 3/1/26: added for p3 apis - register
 * @param username
 * @param password
 * @param email
 */
public record RegisterRequest(
        String username,
        String password,
        String email
) {}
