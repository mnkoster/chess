package websocket.messages;

/**
 * 4/7/26: added for p6 websocket - messages
 */
public class NotificationMessage extends ServerMessage {
    
    private final String message;
    
    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
