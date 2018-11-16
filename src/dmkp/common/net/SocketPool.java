package dmkp.common.net;

import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import dmkp.common.util.Result;

class SocketPool implements Runnable {
	
	class SocketDuplexConn extends SocketDuplex {

		@Override
		public void OnConnect() {}

		@Override
		public void OnStream(byte[] Data) {}

		@Override
		public void OnDisconnect() {
			OnDuplexDisconnect(GetSocketAddress());
		}

		@Override
		public void OnHearbeatError(Result Reason) {}
		
	}
	
	/**
	 * 封装网络地址信息。
	 * @author 陈宏葆
	 *
	 */
	public class IpAddr {
		public IpAddr() {}
		
		public SocketDuplex TCP;
		public String IP;
		public int Port;
	}
	
	/**
	 * 保存网络连接信息。
	 */
	protected List<IpAddr> _Conns;
	
	/**
	 * 线程池。
	 */
	protected ExecutorService _es;
	
	/**
	 * 构造函数。
	 * @param addrs 需要连接的网路地址。
	 */
	public SocketPool(List<IpAddr> addrs) {
		_Conns = new LinkedList<IpAddr>();
		_Conns.addAll(addrs);
		_es.execute(this);
	}

	@Override
	public void run() {
		/*不断检查是否连接正常，断开自动重连*/
		while (true) {
			for (IpAddr addr : _Conns) {
				if (!addr.TCP.IsConnected()) {
					addr.TCP.Connect(addr.IP,  addr.Port);
				}
			}
			try {
				/*15秒检查一次*/
				Thread.sleep(1000 * 5);
			} catch (InterruptedException e) {
			}
		}
	}
	
	/**
	 * 向连接池中所有连接发送数据。
	 * @param data 待发送的数据。
	 * @return 每一个连接发送的结果。
	 */
	public List<Result> BroadcastTokens(byte[] data) {
		List<Result> res =  new LinkedList<Result>();
		for (IpAddr addr : _Conns) {
			if (!addr.TCP.IsConnected()) {
				Result r = addr.TCP.Connect(addr.IP,  addr.Port);
				if (r.equals(Result.Error)) {
					res.add(r);
					continue;
				}
			}
			res.add(addr.TCP.SendStream(data));
		}
		return res;
	}
	
	/**
	 * 连接池中有连接断开，回调。
	 * @param addr 断开连接的地址信息。
	 */
	public void OnDuplexDisconnect(SocketAddress addr) {}
}