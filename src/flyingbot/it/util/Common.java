package flyingbot.it.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Common {

	public Common() {
	}

	/**
     * Thread pool singleton.
     */
    static ExecutorService _execSvc = null;

    /**
     * Print exception message to file.
     * @param e exception instance
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
     * Print message to file.
     * @param msg exception message
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
     * Load JSON from input stream.
     * @param is input stream to JSON text.
     * @return JSON object
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
     * Load JSON from text file.
     * @param Path text file path
     * @return JSON object
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
     * Load JSON array from input stream.
     * @param is input stream to JSON text
     * @return JSON array
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
     * Get timestamp string in form of yyyy-mm-dd hh:mm:ss
     * @return timestamp string
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
	static {
		_execSvc = Executors.newCachedThreadPool();
	}

	/**
     * Get thread pool singleton.
     * @return thread pool
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
     * Check if IP matches the pattern in the file by path.
     *
     * Example: 127.0.0.1 matches pattern of 127.*.*.*
     *
     * @param inIP input IP
     * @param conf path to the configuration.
     * @return true if patterns match
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
     * Check if IP matches the pattern by input stream.
     * @param inIP input IP
     * @param is input stream
     * @return true if patterns match
	 */
	public static boolean VerifyIP(String inIP, InputStream is) {
		boolean matched = true;

		String[] segs = inIP.split("\\.");
		if (segs.length != 4) {
			return false;
		}

		JSONArray arr = LoadJSONArray(is);
		if (arr == null || arr.length() < 1) {
            // configuration is empty
			return true;
		}

        // iterate over all patterns
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
