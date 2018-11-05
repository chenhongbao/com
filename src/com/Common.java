package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

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
			e.printStackTrace(new PrintStream(f));
		} catch (Exception e1) {
		}
	}
	
	/**
	 * 打印消息到默认文件。
	 * @param msg 打印的消息。
	 */
	public static void PrintException(String msg) {
		File f = new File("exception.log");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			PrintStream ps = new PrintStream(f);
			ps.write(msg.getBytes("UTF-8"));
			ps.close();
		} catch (Exception e1) {
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
		StringBuilder sb = new StringBuilder();
		String line = null;
		File f = new File(Path);
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
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
		StringBuilder sb = new StringBuilder();
		String line = null;
		File f = new File(Path);
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
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
}
