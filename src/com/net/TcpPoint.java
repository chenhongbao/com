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
	/*代表本TCP端点使用的socket连接，可能为null。*/
	private Socket _Sock;
	
	/**
	 * TCP数据头，size_t(C++)或者long(Java)，表示该次发送数据的长度
	 */
	public final static int LEADING_BYTES = 8;

	/**
	 * 默认构造函数，不会实例化任何内部对象。
	 */
	public TcpPoint() {}
	
	/**
	 * 把合法的Socket封装起来。
	 * @param _Sock 合法的Socket对象。
	 */
	public TcpPoint(Socket _Sock) {
		this._Sock = _Sock;
	}
	
	/**
	 * 接收网络另一端发来的数据。
	 * @param Bytes 字节数组链表，网络对面发来的数据。
	 * @return 接收的结果，参见{@link Result}。
	 */
	public Result Recv(ArrayList<Byte> Bytes) {
		byte[] lead = new byte[LEADING_BYTES];
		try {
			InputStream is = _Sock.getInputStream();
			if (is.read(lead) < LEADING_BYTES)
				return new Result(Result.ResultState.Success, -1, "引导位损坏");
			ByteBuffer bb = ByteBuffer.wrap(lead);
			bb.order(ByteOrder.BIG_ENDIAN);
			long data_len = bb.getLong();
			if (data_len == 0)
				return new Result(Result.ResultState.Success, -1, "无接收数据");
			else if (data_len < 0)
				return new Result(Result.ResultState.Success, -1, "引导位错误");
			byte[] content = new byte[(int) data_len];
			long bytes_left = data_len;
			while (bytes_left > 0) {
				int bytes_read = is.read(content, 0, (int) bytes_left);
				if (bytes_read < 0)
					return new Result(ResultState.Error, -1, "接收数据错误");
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
	 * 发送二进制字节。
	 * @param Bytes 字节数据。
	 * @return 发送的结果，参见{@link Result}。
	 */
	public Result Send(byte[] Bytes) {
		try {
			/*
			 * 把数据头和数据拼凑一起，注意数据头的字节序是网络字节序
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
	 * 以指定编码发送文本。
	 * @param Text 文本。
	 * @param CharsetName 编码名称。
	 * @return 发送的结果，参见{@link Result}。
	 */
	public Result Send(String Text, String CharsetName) {
		byte[] bytes = Text.getBytes(Charset.forName(CharsetName));
		return Send(bytes);
	}
	
	/**
	 * 连接IP:Port网络地址。
	 * @param IP IP地址，xxx.xxx.xxx.xxx.
	 * @param Port 网络端口。
	 * @return 连接的结果，参见{@link Result}。
	 */
	public Result Connect(String IP, int Port) {
		if (_Sock != null && _Sock.isConnected())
			return new Result(ResultState.Error, -1, "已连接");
		try {
			_Sock = new Socket(IP, Port);
			return new Result();
		} catch (IOException e) {
			return new Result(ResultState.Error, -1, e.getMessage());
		}
	}
	
	/**
	 * 关闭网络连接。
	 * @return 关闭的结果，参见{@link Result}。
	 * @throws IOException 关闭网络IO流出错。
	 */
	public Result Close() throws IOException {
		if(_Sock == null)
			return new Result(ResultState.Error, -1, "无可用插口");
		if (_Sock.isClosed())
			return new Result(ResultState.Error, -1, "已关闭");
		_Sock.shutdownInput();
		_Sock.shutdownOutput();
		_Sock.close();
		return new Result();
	}
	
	/**
	 * 检查是否仍然连接。
	 * @return 仍然连接返回true，否则返回false。
	 */
	public boolean IsConnected(){
		return _Sock != null && !_Sock.isClosed() && _Sock.isConnected();
	}
	
	/**
	 * 获得本对象封装的网络地址。
	 * @return 网络地址。参见 {@link InetAddress}。
	 */
	public InetAddress GetInetAddress() {
		return _Sock.getInetAddress();
	}
	
	/**
	 * 对象销毁时会关闭网络连接。
	 */
	protected void finalize() throws Throwable {
		Close();
	}
}
