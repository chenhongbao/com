package flyingbot.it.net.tcp;

import flyingbot.it.util.Result;
import flyingbot.it.util.Result.ResultState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class SocketWrapper {

    /**
     * TCP packet header, telling how many bytes following.
     */
    public final static int LEADING_BYTES = 8;
    private Socket sock;

    /**
     * Empty socket.
     */
    public SocketWrapper() {
    }

    /**
     * Wrap an existing socket.
     *
     * @param s existing socket
     */
    public SocketWrapper(Socket s) {
        this.sock = s;
    }

    /**
     * Receive binary data.
     *
     * @return byte[]
     */
    public byte[] Recv() {
        int bytes_left = 0, total_read = 0;
        long data_len = 0;
        byte[] lead = new byte[LEADING_BYTES];
        try {
            InputStream is = sock.getInputStream();

            // read leading bytes
            // identify how many bytes are sent (following leading bytes)
            bytes_left = LEADING_BYTES;
            while (bytes_left > 0) {
                int bytes_read = is.read(lead, total_read, bytes_left);

                // read failed
                if (bytes_read == -1) {
                    break;
                } else {
                    bytes_left -= bytes_read;
                    total_read += bytes_read;
                }
            }

            // Socket has been closed nicely before we read from it.
            if (total_read == 0) {
                return null;
            }

            // Bytes not enough for correct leading bytes
            if (0 < total_read && total_read < LEADING_BYTES) {
                return null;
            }

            // Decode the leading bytes
            ByteBuffer bb = ByteBuffer.wrap(lead);
            bb.order(ByteOrder.BIG_ENDIAN);
            data_len = bb.getLong();
            if (data_len == 0) {
                return new byte[0];
            }
            if (data_len < 0) {
                return null;
            }

            // Wrap content into byte[]
            byte[] content = new byte[(int) data_len];

            // Read bytes
            total_read = 0;
            bytes_left = (int) data_len;
            while (bytes_left > 0) {
                int bytes_read = is.read(content, total_read, bytes_left);
                if (bytes_read < 0) {
                    break;
                }
                if (bytes_read > 0) {
                    // counting
                    bytes_left -= bytes_read;
                    total_read += bytes_read;
                }
            }

            // sent bytes less than expected
            if (bytes_left > 0) {
                return null;
            } else {
                return content;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Send bytes.
     *
     * @param Bytes bytes to send
     * @return {@link Result}
     */
    public Result Send(byte[] Bytes) {
        try {
            // Add leading bytes and then content
            ByteBuffer bb = ByteBuffer.allocate(LEADING_BYTES + Bytes.length);
            bb.order(ByteOrder.BIG_ENDIAN);
            bb.putLong((long) Bytes.length);

            // content
            bb.put(Bytes);

            // flush to output stream
            OutputStream os = sock.getOutputStream();
            os.write(bb.array());
            os.flush();
            return new Result();
        } catch (IOException e) {
            return new Result(ResultState.Error, -1, e.getMessage());
        }
    }

    /**
     * Send string in specified charset.
     *
     * @param Text        string to send
     * @param CharsetName charset name
     * @return {@link Result}
     */
    public Result Send(String Text, String CharsetName) {
        byte[] bytes = Text.getBytes(Charset.forName(CharsetName));
        return Send(bytes);
    }

    /**
     * Connect to remote server.
     */
    public Result Connect(String IP, int Port) {
        if (sock != null && sock.isConnected())
            return new Result(ResultState.Error, -1, "Socket has been connected");
        try {
            sock = new Socket(IP, Port);
            return new Result();
        } catch (IOException e) {
            return new Result(ResultState.Error, -1, e.getMessage());
        }
    }

    /**
     * Close connection.
     */
    public Result Close() throws IOException {
        if (sock == null)
            return new Result(ResultState.Error, -1, "Socket null");
        if (sock.isClosed())
            return new Result(ResultState.Error, -1, "Socket has been closed");
        sock.shutdownInput();
        sock.shutdownOutput();
        sock.close();
        return new Result();
    }

    /**
     * Return true if socket is not null, is not close and is connected.
     */
    public boolean IsConnected() {
        return sock != null && !sock.isClosed() && sock.isConnected();
    }

    /**
     * Get IPv4 address.
     */
    public InetAddress GetInetAddress() {
        return sock.getInetAddress();
    }

    /**
     * Get socket address.
     *
     * @return {@link SocketAddress}
     */
    public SocketAddress GetSocketAddress() {
        return sock.getRemoteSocketAddress();
    }

    /**
     * Finally close connection.
     */
    protected void finalize() throws Throwable {
        Close();
    }
}
