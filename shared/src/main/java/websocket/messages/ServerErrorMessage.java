package websocket.messages;

/**
 * 4/7/26: added for p6 websocket - messages
 */
public class ServerErrorMessage extends ServerMessage {
    
    private final String errorMessage;
    
    public ServerErrorMessage (String message) {
        super(ServerMessageType.ERROR);
        this.errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
