package com.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.Result;
import com.Result.ResultState;

public abstract class Duplex {
	
	/*��������*/
	private TcpPoint _tcp;
	
	/*���շ��������߳�*/
	Thread _thd, _keepThd;
	ExecutorService _Exce;
	
	/*��¼�Ƿ��״�����*/
	boolean _firstConnected;
	
	/**
	 * ��Socket���ӷ�װ������
	 * @param Sock �Ϸ���Socket���ӡ�
	 */
	public Duplex(Socket Sock) {
		_firstConnected = true;
		try {
			_InitConnection(Sock);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����IP:Port�����ַ���������ǰ��û���������ӡ�
	 * @param IP IP��ַ��xxx.xxx.xxx.xxx��
	 * @param Port ����˿ڡ�
	 * @return �������ӵĽ�����μ� {@link Result}��
	 */
	public Result Connect(String IP, int Port) {
		if (_tcp != null && _tcp.IsConnected())
			return new Result(Result.ResultState.Error, -1, "������");
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
		if (_tcp == null || !_tcp.IsConnected())
			return new Result(Result.ResultState.Error, -1, "δ����");
		try {
			_tcp.Close();
			if (_thd != null && _thd.isAlive())
				_thd.interrupt();
			if (_keepThd != null && _keepThd.isAlive())
				_keepThd.interrupt();
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
	 * ��ȡ��������������ַ��Ϣ��
	 * @return �����ַ���μ� {@link InetAddress}��
	 */
	public InetAddress GetInetAddress() {
		return _tcp.GetInetAddress();
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
		_tcp = new TcpPoint(Sock);
		_Exce = Executors.newCachedThreadPool();
		_thd = new Thread(new Runnable() {
			@Override
			public void run() {
				_StreamWorker();	
			}});
		_thd.start();
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
			}
			else {
				try {
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
		while(_tcp.IsConnected()) {
			try {
				_tcp.Send("", "UTF-8");
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {}
		}
	}
}
