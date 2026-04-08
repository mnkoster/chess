package websocket.messages;

/**
 * 4/7/26: added for p6 websocket - messages
 */
public class ServerErrorMessage {
    
    private final String message;
    
    public ServerErrorMessage (String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return message;
    }
}
