package model;

/**
 * UserData record
 * added 2/28/26 for phase 3
 * @param username
 * @param password
 * @param email
 */
public record UserData(
        String username,
        String password,
        String email
) {}
