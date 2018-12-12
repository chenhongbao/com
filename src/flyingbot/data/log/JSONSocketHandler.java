package flyingbot.data.log;

import java.io.File;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import dmkp.common.util.Common;
import dmkp.common.util.Result;
import dmkp.common.net.SocketDuplex;

public class JSONSocketHandler extends Handler {

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
			obj.TimeStamp=Common.GetTimestamp();
			obj.Level=record.getLevel().getName();
			obj.LoggerName=record.getLoggerName();
			obj.Message=record.getMessage();
			obj.Millis=record.getMillis();
			obj.SourceClassName=record.getSourceClassName();
			obj.SourceMethodName=record.getSourceMethodName();
			/*
			 * ��Ϊ��ȡ�к���ҪJVM��ջ��Ϣ������LogRecord�����к�����Ҫ����Throwable���󣬿����ǳ���
			 * ��������Ĭ���ṩ�㡣
			 */
			obj.LineNumber=0;
			return obj.ToJSON().toString(-1);
		}
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
			} catch (Exception e1) {}
		}
		
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
			InetSocketAddress sa = (InetSocketAddress)GetSocketAddress();
			Common.PrintException("��־���������ӶϿ���" + sa.getAddress().getCanonicalHostName() + ":" + sa.getPort());
		}

		@Override
		public void OnHearbeatError(Result Reason) {
		}
	}
	
	private SocketDuplex _tcp;
	
	// ������Ϣ
	private String _host;
	private int _port;

	public JSONSocketHandler() {
	}

	public JSONSocketHandler(String host, int port) {
		_tcp = new SocketDuplexConn();
		
		// �ȵ�����־�����ӣ��ӳ�����
		_host = host;
		_port = port;
		
		// ����formatter
		setFormatter(new JSONFormatter());
		try {
			setEncoding("UTF-8");
		} catch (Exception e) {
			Common.PrintException(new Exception("������־���������" + e.getMessage()));
		}
		setErrorManager(new JSONErrorManager());
	}

	@Override
	public void publish(LogRecord record) {
		if (record == null) {
			return;
		}
		String msg = getFormatter().format(record);
		
		// ������־������
		if (!_tcp.IsConnected()) {
			Result r = _tcp.Connect(_host, _port);
			if (r.equals(Result.Error)) {
				Common.PrintException(new Exception("������־������" + r.Message + "��" + msg));
				return;
			}
		}
		
		// ������־
		Result r = _tcp.SendStream(msg.getBytes(Charset.forName(getEncoding())));
		if (r.equals(Result.Error)) {
			Common.PrintException(new Exception("������־������" + r.Message));
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws SecurityException {
		Result r = _tcp.Disconnect();
		if (r.equals(Result.Error)) {
			Common.PrintException(new Exception("�ر���־�������ӳ�����" + r.Message));
		}
	}
}