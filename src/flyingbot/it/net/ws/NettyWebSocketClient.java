package flyingbot.it.net.ws;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

public class NettyWebSocketClient extends SimpleChannelInboundHandler<Object> implements WebSocketClient {
    /**
     * Netty version
     */
    final private static String Version = "Netty 4.1.30";
    /**
     * Maximum content length
     */
    public static int maxContentLength = 64 * 1024 * 1024;
    /**
     * WebSocket shake hand
     */
    WebSocketClientHandshaker handshaker;
    ChannelPromise handshakeFuture;
    /**
     * User code, handling in-coming data
     */
    WebSocketHandler handler;
    /**
     * Whether onClose has been called
     */
    boolean onCloseCalled;
    /**
     * user session
     */
    WebSocketSession wsSession;
    /**
     * The output channel associated with the connection
     */
    Channel peerChannel;
    /**
     * Receiving and sending byte counters
     */
    AtomicLong recvCount, sentCount;

    public NettyWebSocketClient() {
        recvCount = new AtomicLong(0);
        sentCount = new AtomicLong(0);
        onCloseCalled = false;
    }

    @Override
    public void addHandler(WebSocketHandler h) {
        handler = h;
    }

    @Override
    public long getReceivedByteCount() {
        return recvCount.get();
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

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (handler != null && !onCloseCalled) {
            try {
                handler.onClose(CloseCodes.CLOSED_ABNORMALLY.getCode(), CloseCodes.CLOSED_ABNORMALLY.name());
                onCloseCalled = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);

                // create use session
                wsSession = new NettyWebSocketSession(ch);

                if (handler != null) {
                    try {
                        handler.onOpen(wsSession);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {

                if (handler != null) {
                    try {
                        handler.onError(e);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                handshakeFuture.setFailure(e);
            }
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;

            if (handler != null) {
                try {
                    handler.onError(new IllegalStateException(
                            "Unexpected FullHttpResponse (getStatus=" + response.status() +
                                    ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')'));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;

            if (handler != null) {
                try {
                    String in = ((TextWebSocketFrame) frame).text();
                    handler.onTextMessage(in);

                    // update counter
                    recvCount.addAndGet(in.getBytes().length);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } else if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;
            ByteBuf buf = binaryFrame.content();

            if (handler != null) {
                try {
                    handler.onBinaryMessage(buf.nioBuffer());

                    // update counter
                    recvCount.addAndGet(buf.writerIndex() - buf.readerIndex());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else if (frame instanceof PongWebSocketFrame) {
            // Omit the pong frame
        } else if (frame instanceof CloseWebSocketFrame) {
            CloseWebSocketFrame cf = (CloseWebSocketFrame) frame;
            if (handler != null && !onCloseCalled) {
                try {
                    handler.onClose(cf.statusCode(), cf.reasonText());
                    onCloseCalled = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            ch.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (handler != null) {
            try {
                handler.onError(cause);

                cause.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }

    @Override
    public void connect(String URL) throws Exception {
        URI uri = new URI(URL);

        if (uri.getHost() == null) {
            throw new UnknownHostException("Host not found, " + URL);
        }

        String scheme = uri.getScheme() == null ? "ws" : uri.getScheme();
        final String host = uri.getHost();
        final int port;
        if (uri.getPort() == -1) {
            if ("ws".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("wss".equalsIgnoreCase(scheme)) {
                port = 443;
            } else {
                port = -1;
            }
        } else {
            port = uri.getPort();
        }

        if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {

            if (handler != null) {
                try {
                    handler.onError(new Exception("Unsupported protocol, only WS(S) supported."));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return;
        }

        final boolean ssl = "wss".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
            // If you change it to V00, ping is not supported and remember to change
            // HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
            handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                    uri, WebSocketVersion.V13, null, true,
                    new DefaultHttpHeaders(), maxContentLength);

            Bootstrap b = new Bootstrap();
            NettyWebSocketClient client = this;
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                            }
                            p.addLast(
                                    new HttpClientCodec(),
                                    new HttpObjectAggregator(maxContentLength),
                                    WebSocketClientCompressionHandler.INSTANCE,
                                    client);
                        }
                    });

            peerChannel = b.connect(uri.getHost(), port).sync().channel();
            handshakeFuture.sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Channel peerChannel() {
        return peerChannel;
    }

    @Override
    public void disconnect(int code, String phrase) throws IOException {
        if (peerChannel != null && peerChannel.isWritable()) {
            try {
                peerChannel.writeAndFlush(new CloseWebSocketFrame(code, phrase));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void sendText(String text) throws IOException {
        peerChannel.writeAndFlush(new TextWebSocketFrame(text));

        // update counter
        sentCount.addAndGet(text.getBytes().length);
    }

    @Override
    public void sendBinary(ByteBuffer buf) throws IOException {
        byte[] data = buf.array();

        // allocate ByteBuf and copy data
        ByteBuf bbuf = ByteBufAllocator.DEFAULT.directBuffer(data.length);
        bbuf.writeBytes(data);

        peerChannel.writeAndFlush(new BinaryWebSocketFrame(bbuf));

        // update counter
        sentCount.addAndGet(buf.limit() - buf.position());
    }

    public enum CloseCodes implements CloseCode {
        NORMAL_CLOSURE(1000),
        GOING_AWAY(1001),
        PROTOCOL_ERROR(1002),
        CANNOT_ACCEPT(1003),
        RESERVED(1004),
        NO_STATUS_CODE(1005),
        CLOSED_ABNORMALLY(1006),
        NOT_CONSISTENT(1007),
        VIOLATED_POLICY(1008),
        TOO_BIG(1009),
        NO_EXTENSION(1010),
        UNEXPECTED_CONDITION(1011),
        SERVICE_RESTART(1012),
        TRY_AGAIN_LATER(1013),
        TLS_HANDSHAKE_FAILURE(1015);

        private int code;

        CloseCodes(int code) {
            this.code = code;
        }

        public static CloseCode getCloseCode(final int code) {
            if (code > 2999 && code < 5000) {
                return new CloseCode() {
                    public int getCode() {
                        return code;
                    }
                };
            } else {
                switch (code) {
                    case 1000:
                        return NORMAL_CLOSURE;
                    case 1001:
                        return GOING_AWAY;
                    case 1002:
                        return PROTOCOL_ERROR;
                    case 1003:
                        return CANNOT_ACCEPT;
                    case 1004:
                        return RESERVED;
                    case 1005:
                        return NO_STATUS_CODE;
                    case 1006:
                        return CLOSED_ABNORMALLY;
                    case 1007:
                        return NOT_CONSISTENT;
                    case 1008:
                        return VIOLATED_POLICY;
                    case 1009:
                        return TOO_BIG;
                    case 1010:
                        return NO_EXTENSION;
                    case 1011:
                        return UNEXPECTED_CONDITION;
                    case 1012:
                        return SERVICE_RESTART;
                    case 1013:
                        return TRY_AGAIN_LATER;
                    case 1014:
                    default:
                        throw new IllegalArgumentException("Invalid close code: [" + code + "]");
                    case 1015:
                        return TLS_HANDSHAKE_FAILURE;
                }
            }
        }

        public int getCode() {
            return this.code;
        }
    }

    public interface CloseCode {
        int getCode();
    }

    public static class NettyWebSocketSession implements WebSocketSession {
        Channel ch;

        public NettyWebSocketSession(Channel c) {
            ch = c;
        }

        @Override
        public boolean isOpen() {
            return ch.isOpen();
        }
    }
}
