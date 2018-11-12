package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

public class Common {

	public Common() {
	}

	/**
	 * 打印异常信息到默认文件。文件位于程序运行目录下exception.log。
	 * 
	 * @param e
	 *            异常对象。
	 */
	public static void PrintException(Exception e) {
		File f = new File("exception.log");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			PrintStream ps = new PrintStream(f);
			ps.append("[" + GetTimestamp() + "]" + e.getMessage() + "\n");
			ps.close();
		} catch (Exception e1) {
		}
	}

	/**
	 * 打印消息到默认文件。
	 * 
	 * @param msg
	 *            打印的消息。
	 */
	public static void PrintException(String msg) {
		File f = new File("exception.log");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			PrintStream ps = new PrintStream(f);
			ps.append("[" + GetTimestamp() + "]" + msg + "\n");
			ps.close();
		} catch (Exception e1) {
		}
	}

	/**
	 * 接受输入流，从输入流读取JSON文本生成JSON对象。
	 * 
	 * @param is
	 *            JSON文本输入流
	 * @return JSON对象，参见 {@link JSONObject}
	 */
	public static JSONObject LoadJSONObject(InputStream is) {
		String line = null;
		try {
			if (is == null || is.available() < 1) {
				throw new Exception("输入流空引用");
			}
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			return new JSONObject(sb.toString());
		} catch (Exception e) {
			PrintException(new Exception("加载JSON文件错误，" + e.getMessage()));
			return null;
		}
	}

	/**
	 * 从文本文件里加载JSON对象。
	 * 
	 * @param Path
	 *            JSON文本文件路径。
	 * @return JSON对象，参见{@link JSONObject}。
	 */
	public static JSONObject LoadJSONObject(String Path) {
		if (Path == null || Path.length() < 1) {
			return null;
		}
		try {
			FileInputStream f = new FileInputStream(new File(Path));
			return LoadJSONObject(f);
		} catch (Exception e) {
			PrintException(new Exception("加载JSON文件错误，" + e.getMessage()));
			return null;
		}
	}
	
	/**
	 * 接受输入流，从输入流读取JSON文本生成JSON数组。
	 * 
	 * @param is
	 *            JSON文本输入流
	 * @return JSON对象，参见 {@link JSONObject}
	 */
	public static JSONArray LoadJSONArray(InputStream is) {
		String line = null;
		try {
			if (is == null || is.available() < 1) {
				throw new Exception("输入流空引用");
			}
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			return new JSONArray(sb.toString());
		} catch (Exception e) {
			PrintException(new Exception("加载JSON文件错误，" + e.getMessage()));
			return null;
		}
	}

	/**
	 * 从文本文件里加载JSON数组。
	 * 
	 * @param Path
	 *            JSON文本文件路径。
	 * @return JSON对象，参见{@link JSONObject}。
	 */
	public static JSONArray LoadJSONArray(String Path) {
		if (Path == null || Path.length() < 1) {
			return null;
		}
		try {
			FileInputStream f = new FileInputStream(new File(Path));
			return LoadJSONArray(f);
		} catch (Exception e) {
			PrintException(new Exception("加载JSON文件错误，" + e.getMessage()));
			return null;
		}
	}

	/**
	 * 返回当前时间戳，格式为yyyy-mm-dd hh:mm:ss sss
	 * 
	 * @return 时间戳字符串
	 */
	public static String GetTimestamp() {
		Calendar c = Calendar.getInstance();
		int y = c.get(Calendar.YEAR);
		int m = c.get(Calendar.MONTH) + 1;
		int d = c.get(Calendar.DAY_OF_MONTH);
		int h = c.get(Calendar.HOUR_OF_DAY);
		int mm = c.get(Calendar.MINUTE);
		int s = c.get(Calendar.SECOND);
		int ss = c.get(Calendar.MILLISECOND);
		String msg = y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d) + " " + (h < 10 ? "0" + h : h)
				+ ":" + (mm < 10 ? "0" + mm : mm) + (s < 10 ? "0" + s : s) + " " + ss;
		return msg;
	}
}
