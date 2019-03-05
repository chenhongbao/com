package flyingbot.it.data.log;

import flyingbot.it.net.tcp.SocketDuplex;
import flyingbot.it.util.Common;
import flyingbot.it.util.Result;

import java.io.File;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class JSONSocketHandler extends Handler {

	public JSONSocketHandler(String host, int port) {
		_tcp = new SocketDuplexConn();
		lock = new ReentrantLock();

        // save info
		_host = host;
		_port = port;

        // set formatter
		setFormatter(new JSONFormatter());
		try {
			setEncoding("UTF-8");
		} catch (Exception e) {
            Common.PrintException(new Exception("Setting encoding failed, " + e.getMessage()));
		}
		setErrorManager(new JSONErrorManager());
	}

    class JSONErrorManager extends ErrorManager {

        public JSONErrorManager() {
        }

        @Override
        public synchronized void error(String msg, Exception ex, int code) {
            File f = new File("exception.log");
            try {
                if (!f.exists()) {
                    f.createNewFile();
                }
                ex.printStackTrace(new PrintStream(f));
            } catch (Exception e1) {
            }
        }

    }
	
	@Override
	public void publish(LogRecord record) {
		if (record == null) {
			return;
		}
		String msg = getFormatter().format(record);

		// Check if Socket is connected
		if (!_tcp.IsConnected()) {
			// Exclusively lock
			lock.lock();

			// Reconfirm the state
			if (!_tcp.IsConnected())
			{
				Result r = _tcp.Connect(_host, _port);
				if (r.equals(Result.Error)) {
                    Common.PrintException(new Exception("Connect error, " + r.Message + ", " + msg));
					return;
				}
			}

			// Unlock
			lock.unlock();
		}

        // Send log
		Result r = _tcp.SendStream(msg.getBytes(Charset.forName(getEncoding())));
		if (r.equals(Result.Error)) {
            Common.PrintException(new Exception("Send log error, " + r.Message));
		}
	}

    private SocketDuplex _tcp;

    private String _host;
    private int _port;

    // Connection mutex
    private ReentrantLock lock;

    public JSONSocketHandler() {
        lock = new ReentrantLock();
	}

	@Override
	public void close() throws SecurityException {
		Result r = _tcp.Disconnect();
		if (r.equals(Result.Error)) {
            Common.PrintException(new Exception("Close logger failed, " + r.Message));
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
