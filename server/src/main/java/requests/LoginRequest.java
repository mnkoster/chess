package requests;

public record LoginRequest(
        String Username,
        String password
) {}
