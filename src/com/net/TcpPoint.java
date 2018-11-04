package com.net;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.Result;
import com.Result.ResultState;

public class TcpPoint {
	/*����TCP�˵�ʹ�õ�socket���ӣ�����Ϊnull��*/
	private Socket _Sock;
	
	/**
	 * TCP����ͷ��size_t(C++)����long(Java)����ʾ�ôη������ݵĳ���
	 */
	public final static int LEADING_BYTES = 8;

	/**
	 * Ĭ�Ϲ��캯��������ʵ�����κ��ڲ�����
	 */
	public TcpPoint() {}
	
	/**
	 * �ѺϷ���Socket��װ������
	 * @param _Sock �Ϸ���Socket����
	 */
	public TcpPoint(Socket _Sock) {
		this._Sock = _Sock;
	}
	
	/**
	 * ����������һ�˷��������ݡ�
	 * @param Bytes �ֽ���������������淢�������ݡ�
	 * @return ���յĽ�����μ�{@link Result}��
	 */
	public Result Recv(ArrayList<Byte> Bytes) {
		byte[] lead = new byte[LEADING_BYTES];
		try {
			InputStream is = _Sock.getInputStream();
			if (is.read(lead) < LEADING_BYTES)
				return new Result(Result.ResultState.Success, -1, "����λ��");
			ByteBuffer bb = ByteBuffer.wrap(lead);
			bb.order(ByteOrder.BIG_ENDIAN);
			long data_len = bb.getLong();
			if (data_len == 0)
				return new Result(Result.ResultState.Success, -1, "�޽�������");
			else if (data_len < 0)
				return new Result(Result.ResultState.Success, -1, "����λ����");
			byte[] content = new byte[(int) data_len];
			long bytes_left = data_len;
			while (bytes_left > 0) {
				int bytes_read = is.read(content, 0, (int) bytes_left);
				if (bytes_read < 0)
					return new Result(ResultState.Error, -1, "�������ݴ���");
				if (bytes_read > 0) {
					for (int i = 0; i < bytes_read; ++i)
						Bytes.add(content[i]);
					bytes_left -= bytes_read;
				}
			}
			return new Result();
		} catch (IOException e) {
			return new Result(ResultState.Error, -1, e.getMessage());
		}
	}
	
	/**
	 * ���Ͷ������ֽڡ�
	 * @param Bytes �ֽ����ݡ�
	 * @return ���͵Ľ�����μ�{@link Result}��
	 */
	public Result Send(byte[] Bytes) {
		try {
			/*
			 * ������ͷ������ƴ��һ��ע������ͷ���ֽ����������ֽ���
			 */
			ByteBuffer bb = ByteBuffer.allocate(LEADING_BYTES + Bytes.length);
			bb.order(ByteOrder.BIG_ENDIAN);
			bb.putLong((long)Bytes.length);
			bb.put(Bytes);
			OutputStream os = _Sock.getOutputStream();
			os.write(bb.array());
			os.flush();
			return new Result();
		} catch (IOException e) {
			return new Result(ResultState.Error, -1, e.getMessage());
		}
	}
	
	/**
	 * ��ָ�����뷢���ı���
	 * @param Text �ı���
	 * @param CharsetName �������ơ�
	 * @return ���͵Ľ�����μ�{@link Result}��
	 */
	public Result Send(String Text, String CharsetName) {
		byte[] bytes = Text.getBytes(Charset.forName(CharsetName));
		return Send(bytes);
	}
	
	/**
	 * ����IP:Port�����ַ��
	 * @param IP IP��ַ��xxx.xxx.xxx.xxx.
	 * @param Port ����˿ڡ�
	 * @return ���ӵĽ�����μ�{@link Result}��
	 */
	public Result Connect(String IP, int Port) {
		if (_Sock != null && _Sock.isConnected())
			return new Result(ResultState.Error, -1, "������");
		try {
			_Sock = new Socket(IP, Port);
			return new Result();
		} catch (IOException e) {
			return new Result(ResultState.Error, -1, e.getMessage());
		}
	}
	
	/**
	 * �ر��������ӡ�
	 * @return �رյĽ�����μ�{@link Result}��
	 * @throws IOException �ر�����IO������
	 */
	public Result Close() throws IOException {
		if(_Sock == null)
			return new Result(ResultState.Error, -1, "�޿��ò��");
		if (_Sock.isClosed())
			return new Result(ResultState.Error, -1, "�ѹر�");
		_Sock.shutdownInput();
		_Sock.shutdownOutput();
		_Sock.close();
		return new Result();
	}
	
	/**
	 * ����Ƿ���Ȼ���ӡ�
	 * @return ��Ȼ���ӷ���true�����򷵻�false��
	 */
	public boolean IsConnected(){
		return _Sock != null && !_Sock.isClosed() && _Sock.isConnected();
	}
	
	/**
	 * ��ñ������װ�������ַ��
	 * @return �����ַ���μ� {@link InetAddress}��
	 */
	public InetAddress GetInetAddress() {
		return _Sock.getInetAddress();
	}
	
	/**
	 * ��������ʱ��ر��������ӡ�
	 */
	protected void finalize() throws Throwable {
		Close();
	}
}
