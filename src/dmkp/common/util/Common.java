package dmkp.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	public static void PrintException(Throwable e) {
		File f = new File("exception.log");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f, true);
			fw.write("[" + GetTimestamp() + "]\n");
			e.printStackTrace(new PrintWriter(fw));
			fw.close();
		} catch (Exception e1) {
			e1.printStackTrace(System.err);
		}
	}

	/**
	 * ��ӡ��Ϣ��Ĭ���ļ���
	 * 
	 * @param msg
	 *            ��ӡ����Ϣ��
	 */
	public static void PrintException(String msg) {
		File f = new File("exception.log");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f, true);
			fw.write("[" + GetTimestamp() + "]" + msg + "\n");
			fw.close();
		} catch (Exception e1) {
			e1.printStackTrace(System.err);
		}
	}

	/**
	 * ����������������������ȡJSON�ı�����JSON����
	 * 
	 * @param is
	 *            JSON�ı�������
	 * @return JSON���󣬲μ� {@link JSONObject}
	 */
	public static JSONObject LoadJSONObject(InputStream is) {
		String line = null;
		try {
			if (is == null || is.available() < 1) {
				throw new Exception("�����������û�������");
			}
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
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
		try {
			FileInputStream f = new FileInputStream(new File(Path));
			return LoadJSONObject(f);
		} catch (Exception e) {
			PrintException(new Exception("����JSON�ļ�����" + e.getMessage()));
			return null;
		}
	}

	/**
	 * ����������������������ȡJSON�ı�����JSON���顣
	 * 
	 * @param is
	 *            JSON�ı�������
	 * @return JSON���󣬲μ� {@link JSONObject}
	 */
	public static JSONArray LoadJSONArray(InputStream is) {
		String line = null;
		try {
			if (is == null || is.available() < 1) {
				throw new Exception("�����������û�������");
			}
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
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
		try {
			FileInputStream f = new FileInputStream(new File(Path));
			return LoadJSONArray(f);
		} catch (Exception e) {
			PrintException(new Exception("����JSON�ļ�����" + e.getMessage()));
			return null;
		}
	}

	/**
	 * ���ص�ǰʱ�������ʽΪyyyy-mm-dd hh:mm:ss sss
	 * 
	 * @return ʱ����ַ���
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
				+ ":" + (mm < 10 ? "0" + mm : mm) + ":" + (s < 10 ? "0" + s : s) + " " + ss;
		return msg;
	}

	// �̳߳ص���
	static ExecutorService _execSvc = null;
	static {
		_execSvc = Executors.newCachedThreadPool();
	}

	/**
	 * ���ȫ��Ψһ�̳߳ء�
	 * 
	 * @return �̳߳ء�
	 */
	public static ExecutorService GetSingletonExecSvc() {
		if (_execSvc == null) {
			Common.PrintException("Executor service is null.");
			return Executors.newCachedThreadPool();
		} else {
			return _execSvc;
		}
	}

	/**
	 * ����IP���ã����������IP�Ƿ���������
	 * 
	 * @param inIP
	 *            ������IP
	 * @param conf
	 *            �����ļ�·��
	 * @return IP�Ϸ�����true�����򷵻�false
	 */
	public static boolean VerifyIP(String inIP, String conf) {
		File f = new File(conf);
		if (!f.exists()) {
			return false;
		}
		try {
			return VerifyIP(inIP, new FileInputStream(f));
		} catch (FileNotFoundException e) {
			Common.PrintException(e);
			return false;
		}
	}

	/**
	 * ����IP���ã����������IP�Ƿ���������
	 * 
	 * @param inIP
	 *            ������IP
	 * @param is
	 *            �����ļ�������
	 * @return IP�Ϸ�����true�����򷵻�false
	 */
	public static boolean VerifyIP(String inIP, InputStream is) {
		boolean matched = true;

		// �ָ�IP��ַ
		String[] segs = inIP.split("\\.");
		if (segs.length != 4) {
			return false;
		}
		
		// ��ȡ����
		JSONArray arr = LoadJSONArray(is);
		if (arr == null || arr.length() < 1) {
			// �Ҳ������ã�Ĭ������IP����������
			return true;
		}

		// �Ƚ�IP
		for (int index = 0; index < arr.length(); ++index) {
			String[] s = arr.getString(index).split("\\.");
			if (s.length != 4) {
				continue;
			}
			for (int i = 0; i < 4; ++i) {
				if (s[i] != "*" && segs[i].compareTo(s[i]) != 0) {
					matched = false;
					break;
				}
			}
			if (matched) {
				return true;
			} else {
				matched = true;
			}
		}
		return false;
	}
}
