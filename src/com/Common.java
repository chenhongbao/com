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
	 * ��ӡ�쳣��Ϣ��Ĭ���ļ����ļ�λ�ڳ�������Ŀ¼��exception.log��
	 * 
	 * @param e
	 *            �쳣����
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
	 * ��ӡ��Ϣ��Ĭ���ļ���
	 * @param msg ��ӡ����Ϣ��
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
	 * ���ı��ļ������JSON����
	 * 
	 * @param Path
	 *            JSON�ı��ļ�·����
	 * @return JSON���󣬲μ�{@link JSONObject}��
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
			PrintException(new Exception("����JSON�ļ�����" + e.getMessage()));
			return null;
		}
	}
	
	/**
	 * ���ı��ļ������JSON���顣
	 * 
	 * @param Path
	 *            JSON�ı��ļ�·����
	 * @return JSON���󣬲μ�{@link JSONObject}��
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
			PrintException(new Exception("����JSON�ļ�����" + e.getMessage()));
			return null;
		}
	}
}
