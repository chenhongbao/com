package flyingbot.it.net.ws;

import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 * WebSocket client based on Tomcat server.
 * Its dependencies: tomcat-api.jar, tomcat-juli.jar, tomcat-util.jar, tomcat-websocket.jar, websocket-api.jar
 */
public class JavaxWebSocketClient implements WebSocketClient {

    /**
     * Tyrus version.
     */
    final private static String Version = "Tyrus-standalone-client 1.15";
    /**
     * user handler
     */
    WebSocketHandler handler;
    /**
     * JavaAPI session
     */
    Session javaSession;
    /**
     * wrapped session
     */
    WebSocketSession wsSession;
    /**
     * User endpoint
     */
    JavaxWebSocketEndpoint point;
    /**
     * Client manager
     */
    ClientManager mgr;
    /**
     * Endpoint config
     */
    ClientEndpointConfig endpointConfig;
    /**
     * Sent bytes counter
     */
    AtomicLong sentCount;

    public JavaxWebSocketClient() {
        sentCount = new AtomicLong(0);
    }

    @Override
    public void connect(String url) throws Exception {
        endpointConfig = ClientEndpointConfig.Builder.create().build();
        mgr = ClientManager.createClient();
        point = new JavaxWebSocketEndpoint(handler);

        //
        // connectToServer uses single thread execution mode, and calls get()
        // on a Future<Session> before it returns to user.
        //
        // asyncConnectToServer uses a thread pool to process incoming data and returns a Future<Session>
        // to user.
        //
        // Here I wait connection to finish and then pass the Session
        Future<Session> fc = mgr.asyncConnectToServer(point, endpointConfig, new URI(url));
        javaSession = fc.get();

        wsSession = new JavaxWebSocketSession(javaSession);
    }

    public void disconnect() throws IOException {
        disconnect(
                CloseReason.CloseCodes.NORMAL_CLOSURE.getCode(),
                NettyWebSocketClient.CloseCodes.NORMAL_CLOSURE.name());
    }

    @Override
    public void disconnect(int code, String phrase) throws IOException {
        if (wsSession != null && wsSession.isOpen()) {
            javaSession.close(new CloseReason(CloseReason.CloseCodes.getCloseCode(code), phrase));
        }
    }

    @Override
    public void sendText(String text) throws IOException {
        if (javaSession != null && javaSession.isOpen()) {
            javaSession.getBasicRemote().sendText(text);

            // update counter
            sentCount.addAndGet(text.getBytes().length);
        } else {
            throw new IOException("Operation on invalid session");
        }
    }

    @Override
    public void sendBinary(ByteBuffer buf) throws IOException {
        if (javaSession != null && javaSession.isOpen()) {
            javaSession.getBasicRemote().sendBinary(buf);

            // update counter
            sentCount.addAndGet(buf.limit() - buf.position());
        } else {
            throw new IOException("Operation on invalid session");
        }
    }

    @Override
    public void addHandler(WebSocketHandler h) {
        this.handler = h;
    }

    @Override
    public long getReceivedByteCount() {
        return point.binaryMessageHandler().recvCount() + point.stringMessageHandler().recvCount();
    }

    @Override
    public long getSentByteCount() {
        return sentCount.get();
    }

    @Override
    public WebSocketSession session() {
        return wsSession;
    }

    @Override
    public String version() {
        return Version;
    }

    class JavaxWebSocketSession implements WebSocketSession {

        Session session;

        public JavaxWebSocketSession(Session s) {
            this.session = s;
        }

        @Override
        public boolean isOpen() {
            return session.isOpen();
        }
    }

    public class JavaxWebSocketEndpoint extends Endpoint {
        /**
         * default buffer size
         */
        final public int defaultBufferSize = 8 * 1024 * 1024;

        /**
         * user handler
         */
        WebSocketHandler handler;

        /**
         * message handlers
         */
        BinaryMessageHandler binaryMsgHandler;
        StringMessageHandler textMsgHandler;

        public JavaxWebSocketEndpoint(WebSocketHandler handler) {
            this.handler = handler;
        }

        public StringMessageHandler stringMessageHandler() {
            return textMsgHandler;
        }

        public BinaryMessageHandler binaryMessageHandler() {
            return binaryMsgHandler;
        }

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            try {
                textMsgHandler = new StringMessageHandler(handler);
                binaryMsgHandler = new BinaryMessageHandler(handler);

                // add handlers
                session.addMessageHandler(textMsgHandler);
                session.addMessageHandler(binaryMsgHandler);

                // set buffer size
                session.setMaxTextMessageBufferSize(defaultBufferSize);

                if (handler != null) {
                    try {
                        handler.onOpen(new JavaxWebSocketSession(session));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // exception during open
                onError(session, e);
            }
        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            if (handler != null) {
                try {
                    handler.onClose(closeReason.getCloseCode().getCode(), closeReason.getReasonPhrase());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        @Override
        public void onError(Session session, Throwable thr) {
            if (handler != null) {
                try {
                    handler.onError(thr);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public class StringMessageHandler implements MessageHandler.Partial<String> {
        /**
         * buffer
         */
        StringBuffer buf;

        /**
         * handler
         */
        WebSocketHandler handler;

        /**
         * received bytes counter
         */
        AtomicLong textRecvCount;


        public StringMessageHandler(WebSocketHandler handler) {
            this.buf = new StringBuffer();
            this.handler = handler;
            this.textRecvCount = new AtomicLong(0);

        }

        @Override
        public void onMessage(String msg, boolean last) {
            buf.append(msg);

            // update counter
            textRecvCount.addAndGet(msg.getBytes().length);

            if (last) {
                if (handler != null) {
                    try {
                        handler.onTextMessage(buf.toString());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                // reset buffer
                buf.setLength(0);
            }
        }

        public long recvCount() {
            return textRecvCount.get();
        }

        public void recvCount(long c) {
            textRecvCount.set(c);
        }
    }

    public class BinaryMessageHandler implements MessageHandler.Partial<ByteBuffer> {

        /**
         * default buffer size
         */
        final public int defaultBufferSize = 8 * 1024 * 1024;

        /**
         * buffer
         */
        ByteBuffer buf;

        /**
         * handler
         */
        WebSocketHandler handler;

        /**
         * received bytes counter
         */
        AtomicLong binaryRecvCount;

        public BinaryMessageHandler(WebSocketHandler handler) {
            this.buf = ByteBuffer.allocate(defaultBufferSize);
            this.handler = handler;
            this.binaryRecvCount = new AtomicLong(0);
        }

        @Override
        public void onMessage(ByteBuffer msg, boolean last) {
            try {
                buf.put(msg);
            } catch (BufferOverflowException e0) {
                int ns = (buf.capacity() + msg.capacity()) * 2;
                ByteBuffer b2 = ByteBuffer.allocate(ns);

                // transfer data
                b2.put(buf);
                b2.put(msg);

                // replace reference
                buf = null;
                buf = b2;
            }

            // update counter
            binaryRecvCount.addAndGet(buf.limit() - buf.position());

            if (last) {
                if (handler != null) {
                    try {
                        handler.onBinaryMessage(buf);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // reset buffer
                buf.clear();
            }
        }

        public long recvCount() {
            return binaryRecvCount.get();
        }

        public void recvCount(long c) {
            binaryRecvCount.set(c);
        }
    }
}

