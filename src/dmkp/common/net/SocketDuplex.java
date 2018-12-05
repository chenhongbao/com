package dmkp.common.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;

import dmkp.common.util.Common;
import dmkp.common.util.Result;
import dmkp.common.util.Result.ResultState;

public abstract class SocketDuplex {
	
	/*��������*/
	private SocketWrapper _tcp = null;
	
	private boolean _isRecving = false;
	
	/*���շ��������߳�*/
	Thread _thd = null, _keepThd = null;
	
	/*��¼�Ƿ��״�����*/
	boolean _firstConnected = true;
	
	/**
	 * �޲ι��캯����
	 */
	public SocketDuplex() {
	}
	
	/**
	 * ��Socket���ӷ�װ������
	 * @param Sock �Ϸ���Socket���ӡ�
	 */
	public SocketDuplex(Socket Sock) {
		try {
			_InitConnection(Sock);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��������е������Ƿ�Ϸ���
	 * @return ������Ȼ�Ϸ�������true�����򷵻�false��
	 */
	public boolean IsConnected() {
		return _isRecving && _tcp.IsConnected();
	}
	
	/**
	 * ����IP:Port�����ַ���������ǰ��û���������ӡ�
	 * @param IP IP��ַ��xxx.xxx.xxx.xxx��
	 * @param Port ����˿ڡ�
	 * @return �������ӵĽ�����μ� {@link Result}��
	 */
	public Result Connect(String IP, int Port) {
		if (this.IsConnected()) {
			return new Result(Result.ResultState.Error, -1, "�ظ�����");
		}
		try {
			_InitConnection(new Socket(IP, Port));
			_keepThd = new Thread(new Runnable() {
				@Override
				public void run() {
					_KeepaliveWorker();	
				}});
			return new Result();
		} catch (IOException e) {
			return new Result(ResultState.Error, -1, e.getMessage());
		}
	}

	/**
	 * �Ͽ����ӡ��账���������ӵ�״̬
	 * @return �Ͽ����ӵĽ�����μ�{@link Result}��
	 */
	public Result Disconnect() {
		if (!this.IsConnected()) {
			return new Result(Result.ResultState.Error, -1, "δ����");
		}
		try {
			// ���˳��߳�
			if (_thd != null && _thd.isAlive()) {
				_thd.interrupt();
			}
			if (_keepThd != null && _keepThd.isAlive()) {
				_keepThd.interrupt();
			}
			
			// �ر�����
			_tcp.Close();
			_isRecving = false;
			return new Result();
		} catch (IOException e) {
			return new Result(ResultState.Error, -1, e.getMessage());
		}
	}
	
	/**
	 * �����ֽ����ݣ�
	 * @param Bytes �����͵��ֽ����ݡ�
	 * @return ���͵Ľ�����μ�{@link Result}��
	 */
	public Result SendStream(byte[] Bytes) {
			return _tcp.Send(Bytes);
	}
	
	/**
	 * ���ͱ������ݡ��ı���UTF-8���뷢�͡�
	 * @param Text �ı���
	 * @return ���͵Ľ�����μ�{@link Result}��
	 */
	public Result SendStream(String Text) {
		return _tcp.Send(Text, "UTF-8");
	}
	
	/**
	 * ��ָ�����뷢���ı���
	 * @param Text �ı���
	 * @param CharsetName �������ơ�
	 * @return ���͵Ľ�����μ�{@link Result}��
	 */
	public Result SendStream(String Text, String CharsetName) {
		return _tcp.Send(Text, CharsetName);
	}
	
	/**
	 * ��ȡ���������IPv4��ַ��Ϣ��
	 * @return �����ַ���μ� {@link InetAddress}��
	 */
	public InetAddress GetInetAddress() {
		return _tcp.GetInetAddress();
	}
	
	/**
	 * ��ȡ��������������ַ��Ϣ��
	 * @return �����ַ���μ� {@link SocketAddress}��
	 */
	public SocketAddress GetSocketAddress() {
		return _tcp.GetSocketAddress();
	}
	
	/**
	 * �״����ӻص�����
	 */
	public abstract void OnConnect();
	
	/**
	 * ���նԷ��������ݵĻص�����
	 * @param Data �Է�����������
	 */
	public abstract void OnStream(byte[] Data);
	
	/**
	 * �Ͽ����ӵĻص�����
	 */
	public abstract void OnDisconnect();
	
	/**
	 * ��������Ļص�����
	 * @param Reason ����ԭ��
	 */
	public abstract void OnHearbeatError(Result Reason);
	
	private void _InitConnection(Socket Sock) throws IOException{
		if (Sock.isClosed() || Sock.isInputShutdown() || Sock.isOutputShutdown()) {
			throw new IOException("�������Ӳ��Ϸ��������Ѿ��ر�");
		}
		_tcp = new SocketWrapper(Sock);
		_thd = new Thread(new Runnable() {
			@Override
			public void run() {
				_StreamWorker();	
			}});
		_thd.start();
	}
	
	private void _StreamWorker() {
		// ���ñ�־
		_isRecving = true;
		// ֻҪ����ʧ��һ�ξ��˳�
		while(this.IsConnected()) {
			if (_firstConnected) {
				_firstConnected = false;
				try {
					OnConnect();
				}catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
			byte[] res = _tcp.Recv();
			if (res != null) {
				if (res.length > 0) {
					Common.GetSingletonExecSvc().execute(new Runnable() {
						@Override
						public void run() {
							try {
								OnStream(res);
							}catch (Exception e) {
								System.err.println(e.getMessage());
							}
						}	
					});
				}
				
				// keep-alive��Ϣ����Ϊ0������Ҫ����
			}
			else {
				try {
					// whileѭ���˳�
					_isRecving = false;
					_tcp.Close();
				} catch (IOException e) {}
			}
		}
		
		// �ص�
		try {
			OnDisconnect();
		}catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	private void _KeepaliveWorker() {
		while(this.IsConnected()) {
			try {
				_tcp.Send("", "UTF-8");
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {}
		}
	}
}
