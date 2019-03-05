package flyingbot.it.net.ws;

import java.nio.ByteBuffer;

public interface WebSocketHandler {
    void onTextMessage(String text);

    void onBinaryMessage(ByteBuffer binary);

    void onOpen(WebSocketSession session);

    void onError(Throwable th);

    void onClose(int code, String phrase);
}
