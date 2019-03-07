package flyingbot.it.data.log;

import flyingbot.it.net.tcp.SocketDuplex;
import flyingbot.it.util.Common;
import flyingbot.it.util.Result;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class JSONSocketHandler extends Handler {

    private SocketDuplex tcp;
    private String host;
    private int port;

	public JSONSocketHandler(String host, int port) {
        tcp = new SocketDuplexConn();
		lock = new ReentrantLock();

        // save info
        this.host = host;
        this.port = port;

        // set formatter
		setFormatter(new JSONFormatter());
		try {
			setEncoding("UTF-8");
		} catch (Exception e) {
            Common.PrintException(new Exception("Setting encoding failed, " + e.getMessage()));
		}
		setErrorManager(new JSONErrorManager());
	}

	@Override
	public void publish(LogRecord record) {
		if (record == null) {
			return;
		}
		String msg = getFormatter().format(record);

		// Check if Socket is connected
        if (!tcp.IsConnected()) {
			// Exclusively lock
			lock.lock();

			// Reconfirm the state
            if (!tcp.IsConnected())
			{
                Result r = tcp.Connect(host, port);
				if (r.equals(Result.Error)) {
                    Common.PrintException(new Exception("Connect error, " + r.Message + ", " + msg));

                    // unlock
                    lock.unlock();
					return;
				}
			}

			// Unlock
			lock.unlock();
		}

        // Send log
        Result r = tcp.SendStream(msg.getBytes(Charset.forName(getEncoding())));
		if (r.equals(Result.Error)) {
            Common.PrintException(new Exception("Send log error, " + r.Message));
		}
	}

    @Override
    public void close() throws SecurityException {
        Result r = tcp.Disconnect();
        if (r.equals(Result.Error)) {
            Common.PrintException(new Exception("Close logger failed, " + r.Message));
        }
    }

    // Connection mutex
    private ReentrantLock lock;

    public JSONSocketHandler() {
        lock = new ReentrantLock();
	}

    class JSONErrorManager extends ErrorManager {

        public JSONErrorManager() {
        }

        @Override
        public synchronized void error(String msg, Exception ex, int code) {
            Common.PrintException(msg + "[CODE:" + code + "]");
            Common.PrintException(ex);
        }

    }

    class JSONFormatter extends Formatter {

        String _JsonText = null;

        public JSONFormatter() {
        }

        @Override
        public String format(LogRecord record) {
            return GetJsonText(record);
        }

        protected String GetJsonText(LogRecord record) {
            SingleLog obj = new SingleLog();
            obj.TimeStamp = Common.GetTimestamp();
            obj.Level = record.getLevel().getName();
            obj.LoggerName = record.getLoggerName();
            obj.Message = record.getMessage();
            obj.Millis = record.getMillis();
            obj.SourceClassName = record.getSourceClassName();
            obj.SourceMethodName = record.getSourceMethodName();

            // It costs a lot in JAVA to get line number. Miss it.
            obj.LineNumber = 0;
            return obj.ToJSON().toString(-1);
        }
    }

    @Override
    public void flush() {
    }

    class SocketDuplexConn extends SocketDuplex {

        @Override
        public void OnConnect() {
        }

        @Override
        public void OnStream(byte[] Data) {
        }

        @Override
        public void OnDisconnect() {
            InetSocketAddress sa = (InetSocketAddress) GetSocketAddress();
            Common.PrintException("Unexpected disconnected: " + sa.getAddress().getCanonicalHostName() + ":" + sa.getPort());
        }

        @Override
        public void OnHearbeatError(Result Reason) {
		}
	}
}
