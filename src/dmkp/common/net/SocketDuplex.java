package dmkp.common.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dmkp.common.util.Result;
import dmkp.common.util.Result.ResultState;

public abstract class SocketDuplex {
	
	/*网络连接*/
	private SocketWrapper _tcp;
	
	private boolean _isConnected;
	
	/*接收发来数据线程*/
	Thread _thd, _keepThd;
	ExecutorService _Exce;
	
	/*记录是否首次连接*/
	boolean _firstConnected;
	
	/**
	 * 无参构造函数。
	 */
	public SocketDuplex() {
		_isConnected = false;
		_firstConnected = true;
	}
	
	/**
	 * 把Socket连接封装起来。
	 * @param Sock 合法的Socket连接。
	 */
	public SocketDuplex(Socket Sock) {
		_firstConnected = true;
		try {
			_InitConnection(Sock);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 检查对象持有的连接是否合法。
	 * @return 连接仍然合法，返回true，否则返回false。
	 */
	public boolean IsConnected() {
		return _isConnected && _tcp.IsConnected();
	}
	
	/**
	 * 连接IP:Port网络地址。本对象此前需没有网络连接。
	 * @param IP IP地址，xxx.xxx.xxx.xxx。
	 * @param Port 网络端口。
	 * @return 返回连接的结果，参见 {@link Result}。
	 */
	public Result Connect(String IP, int Port) {
		if (_tcp != null && _tcp.IsConnected())
			return new Result(Result.ResultState.Error, -1, "已连接");
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
	 * 断开连接。需处于正在连接的状态
	 * @return 断开连接的结果，参见{@link Result}。
	 */
	public Result Disconnect() {
		if (_tcp == null || !_tcp.IsConnected())
			return new Result(Result.ResultState.Error, -1, "未连接");
		try {
			_tcp.Close();
			if (_thd != null && _thd.isAlive())
				_thd.interrupt();
			if (_keepThd != null && _keepThd.isAlive())
				_keepThd.interrupt();
			_isConnected = false;
			return new Result();
		} catch (IOException e) {
			return new Result(ResultState.Error, -1, e.getMessage());
		}
	}
	
	/**
	 * 发送字节数据，
	 * @param Bytes 待发送的字节数据。
	 * @return 发送的结果，参见{@link Result}。
	 */
	public Result SendStream(byte[] Bytes) {
			return _tcp.Send(Bytes);
	}
	
	/**
	 * 发送本文数据。文本以UTF-8编码发送。
	 * @param Text 文本。
	 * @return 发送的结果，参见{@link Result}。
	 */
	public Result SendStream(String Text) {
		return _tcp.Send(Text, "UTF-8");
	}
	
	/**
	 * 以指定编码发送文本。
	 * @param Text 文本。
	 * @param CharsetName 编码名称。
	 * @return 发送的结果，参见{@link Result}。
	 */
	public Result SendStream(String Text, String CharsetName) {
		return _tcp.Send(Text, CharsetName);
	}
	
	/**
	 * 获取对象关联的IPv4地址信息。
	 * @return 网络地址。参见 {@link InetAddress}。
	 */
	public InetAddress GetInetAddress() {
		return _tcp.GetInetAddress();
	}
	
	/**
	 * 获取对象关联的网络地址信息。
	 * @return 网络地址。参见 {@link SocketAddress}。
	 */
	public SocketAddress GetSocketAddress() {
		return _tcp.GetSocketAddress();
	}
	
	/**
	 * 首次连接回调函数
	 */
	public abstract void OnConnect();
	
	/**
	 * 接收对方发来数据的回调函数
	 * @param Data 对方发来的数据
	 */
	public abstract void OnStream(byte[] Data);
	
	/**
	 * 断开连接的回调函数
	 */
	public abstract void OnDisconnect();
	
	/**
	 * 心跳错误的回调函数
	 * @param Reason 错误原因
	 */
	public abstract void OnHearbeatError(Result Reason);
	
	private void _InitConnection(Socket Sock) throws IOException{
		_tcp = new SocketWrapper(Sock);
		_Exce = Executors.newCachedThreadPool();
		_thd = new Thread(new Runnable() {
			@Override
			public void run() {
				_StreamWorker();	
			}});
		_thd.start();
		_isConnected = true;
	}
	
	private void _StreamWorker() {
		while(_tcp.IsConnected()) {
			Result res;
			ArrayList<Byte> buffer = new ArrayList<Byte>();
			if (_firstConnected) {
				_firstConnected = false;
				try {
					OnConnect();
				}catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
			res = _tcp.Recv(buffer);
			if (res.equals(Result.Success) ) {
				if (buffer.size() > 0) {
					byte[] bytes = new byte[buffer.size()];
					for (int i=0; i<buffer.size(); ++i) {
						bytes[i] = buffer.get(i);
					}
					_Exce.execute(new Runnable() {
						@Override
						public void run() {
							try {
								OnStream(bytes);
							}catch (Exception e) {
								System.err.println(e.getMessage());
							}
						}	
					});
				}
				
				// keep-alive信息长度为0，不需要处理。
			}
			else {
				try {
					_isConnected = false;
					_tcp.Close();
				} catch (IOException e) {}
				try {
					OnDisconnect();
				}catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
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
