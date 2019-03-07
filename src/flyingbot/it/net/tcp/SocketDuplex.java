package flyingbot.it.net.tcp;

import flyingbot.it.util.Common;
import flyingbot.it.util.Result;
import flyingbot.it.util.Result.ResultState;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;

public abstract class SocketDuplex {

    Thread thread = null, keepThread = null;
    boolean firstConnected = true;
    private SocketWrapper tcp = null;
    private boolean isRecving = false;

	/**
     * Empty socket duplex. Need to call connect.
	 */
	public SocketDuplex() {
	}

	/**
     * Wrap an existing socket.
     * @param Sock existing socket
	 */
	public SocketDuplex(Socket Sock) {
		try {
            initConnection(Sock);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean IsConnected() {
        return isRecving && tcp.IsConnected();
	}
	
	/**
     * Connect to server.
     * @param IP IP in form of xxx.xxx.xxx.xxx
     * @param Port remote port
     * @return {@link Result}
	 */
	public Result Connect(String IP, int Port) {
		if (this.IsConnected()) {
            return new Result(Result.ResultState.Error, -1, "Duplicated connection");
		}
		try {
            initConnection(new Socket(IP, Port));
            keepThread = new Thread(new Runnable() {
				@Override
				public void run() {
                    keepaliveWorker();
				}});
			return new Result();
		} catch (IOException e) {
			return new Result(ResultState.Error, -1, e.getMessage());
		}
	}

	/**
     * Disconnect from server
     * @return {@link Result}
	 */
	public Result Disconnect() {
		if (!this.IsConnected()) {
            return new Result(Result.ResultState.Error, -1, "Not connected");
		}
		try {
            // check if deamon exits
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
			}
            if (keepThread != null && keepThread.isAlive()) {
                keepThread.interrupt();
			}

            // close underlining connection
            tcp.Close();
            isRecving = false;
			return new Result();
		} catch (IOException e) {
			return new Result(ResultState.Error, -1, e.getMessage());
		}
	}

	/**
     * Send binary data.
     * @param Bytes byte[] data
     * @return {@link Result}
	 */
	public Result SendStream(byte[] Bytes) {
        return tcp.Send(Bytes);
	}
	
	/**
     * Send string.
     * @param Text string to send
     * @return {@link Result}
	 */
	public Result SendStream(String Text) {
        return tcp.Send(Text, "UTF-8");
    }

	/**
     * Send text in specified charset.
     * @param Text string to send
     * @param CharsetName name of the charset
     * @return {@link Result}
	 */
	public Result SendStream(String Text, String CharsetName) {
        return tcp.Send(Text, CharsetName);
	}
	
	/**
     * Get IPv4 address.
     * @return {@link InetAddress}
	 */
	public InetAddress GetInetAddress() {
        return tcp.GetInetAddress();
	}
	
	/**
     * Get socket address.
     * @return {@link SocketAddress}
	 */
	public SocketAddress GetSocketAddress() {
        return tcp.GetSocketAddress();
	}
	
	/**
     * Callback.
	 */
	public abstract void OnConnect();
	
	/**
     * Callback on input data.
     * @param Data input binary data
	 */
	public abstract void OnStream(byte[] Data);
	
	/**
     * Callback.
	 */
	public abstract void OnDisconnect();
	
	/**
     * Callback.
     * @param Reason close reason
	 */
	public abstract void OnHearbeatError(Result Reason);

    private void initConnection(Socket Sock) throws IOException {
		if (Sock.isClosed() || Sock.isInputShutdown() || Sock.isOutputShutdown()) {
            throw new IOException("Socket error.");
        }
        tcp = new SocketWrapper(Sock);
        thread = new Thread(new Runnable() {
			@Override
			public void run() {
                streamWorker();
			}});
        thread.start();
	}

    private void streamWorker() {
        // mark
        isRecving = true;

        // ֻloop if it is connected
		while(this.IsConnected()) {
            if (firstConnected) {
                firstConnected = false;
				try {
					OnConnect();
				}catch (Exception e) {
                    e.printStackTrace();
				}
			}
            byte[] res = tcp.Recv();
			if (res != null) {
				if (res.length > 0) {
					Common.GetSingletonExecSvc().execute(new Runnable() {
						@Override
						public void run() {
							try {
								OnStream(res);
							}catch (Exception e) {
                                e.printStackTrace();
							}
						}	
					});
				}

                // keep-alive message. zero byte sent.
			}
			else {
				try {
                    // close underlining connection
                    isRecving = false;
                    tcp.Close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
			}
		}

        // callback.
		try {
			OnDisconnect();
		}catch (Exception e) {
            e.printStackTrace();
		}
	}

    private void keepaliveWorker() {
		while(this.IsConnected()) {
			try {
                tcp.Send("", "UTF-8");
				Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		}
	}
}
