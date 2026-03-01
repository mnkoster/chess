package model;

/** Public record for AuthData strings
 * added 2/28/26 for phase 3
 * @param authToken
 * @param username
 */
public record AuthData(
        String authToken,
        String username
) {}
