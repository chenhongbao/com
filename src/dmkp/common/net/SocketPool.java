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
	 * ��װ�����ַ��Ϣ��
	 * @author �º���
	 *
	 */
	public class IpAddr {
		public IpAddr() {}
		
		public SocketDuplex TCP;
		public String IP;
		public int Port;
	}
	
	/**
	 * ��������������Ϣ��
	 */
	protected List<IpAddr> _Conns;
	
	/**
	 * �̳߳ء�
	 */
	protected ExecutorService _es;
	
	/**
	 * ���캯����
	 * @param addrs ��Ҫ���ӵ���·��ַ��
	 */
	public SocketPool(List<IpAddr> addrs) {
		_Conns = new LinkedList<IpAddr>();
		_Conns.addAll(addrs);
		_es.execute(this);
	}

	@Override
	public void run() {
		/*���ϼ���Ƿ������������Ͽ��Զ�����*/
		while (true) {
			for (IpAddr addr : _Conns) {
				if (!addr.TCP.IsConnected()) {
					addr.TCP.Connect(addr.IP,  addr.Port);
				}
			}
			try {
				/*15����һ��*/
				Thread.sleep(1000 * 5);
			} catch (InterruptedException e) {
			}
		}
	}
	
	/**
	 * �����ӳ����������ӷ������ݡ�
	 * @param data �����͵����ݡ�
	 * @return ÿһ�����ӷ��͵Ľ����
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
	 * ���ӳ��������ӶϿ����ص���
	 * @param addr �Ͽ����ӵĵ�ַ��Ϣ��
	 */
	public void OnDuplexDisconnect(SocketAddress addr) {}
}