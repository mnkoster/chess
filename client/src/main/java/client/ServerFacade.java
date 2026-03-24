package client;

import com.google.gson.Gson;
import model.*;
import requests.*;
import results.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 3/23/26: added for p5 client - serverFacade, calls ServerMain endpoints
 * 3/24/26: updated for p5 client -
 */
public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    // LogoutRepl: register, login, logout

    public AuthData register(String username, String password, String email) throws Exception {
        var request = new RegisterRequest(username, password, email);
        return makeRequest("POST", "/user", request, null, AuthData.class);
    }

    public AuthData login(String username, String password) throws Exception {
        var request = new LoginRequest(username, password);
        return makeRequest("POST", "/session", request, null, AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        makeRequest("DELETE", "/session", null, authToken, null);
    }

    // LoginRepl: listGames, joinGames, etc.

    public Map<String, List<GameData>> listGames(String authToken) throws Exception {
        return makeRequest("GET", "/game", null, authToken, null);
    }

    public void createGame(String authToken, String gameName) throws Exception {
        var request = new CreateGameRequest(gameName);
        makeRequest("POST", "/game", request, authToken, null);
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws Exception {
        var request = new JoinGameRequest(gameID, playerColor);
        makeRequest("PUT", "/game", request, authToken, null);
    }

    // Generic

    public void clear() throws Exception {
        makeRequest("DELETE", "/db", null, null, null);
    }

    private <T> T makeRequest(String method, String path, Object body, String authToken, Class<T> responseClass)
            throws Exception {

        URL url = (new URI(serverUrl + path)).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(method);
        connection.setDoOutput(true);

        // Headers
        connection.addRequestProperty("Content-Type", "application/json");
        if (authToken != null) {
            connection.addRequestProperty("Authorization", authToken);
        }

        // Request body
        if (body != null) {
            String json = gson.toJson(body);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(json.getBytes());
            }
        }

        connection.connect();

        int status = connection.getResponseCode();

        InputStream responseStream = (status >= 200 && status < 300)
                ? connection.getInputStream()
                : connection.getErrorStream();

        if (responseStream == null) {
            return null;
        }

        String response = new String(responseStream.readAllBytes());

        if (status >= 200 && status < 300) {
            if (responseClass != null) {
                return gson.fromJson(response, responseClass);
            }
            return null;
        } else {
            throw new RuntimeException("Error: " + response);
        }
    }
}