package flyingbot.it.net.ws;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface WebSocketClient {
    void connect(String url) throws Exception;

    void disconnect(int code, String phrase) throws IOException;

    void sendText(String text) throws IOException;

    void sendBinary(ByteBuffer buf) throws IOException;

    void addHandler(WebSocketHandler h);

    long getReceivedByteCount();

    long getSentByteCount();

    WebSocketSession session();

    String version();
}
