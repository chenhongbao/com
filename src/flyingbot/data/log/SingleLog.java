package flyingbot.data.log;

import org.json.JSONObject;

import dmkp.common.util.Common;

public class SingleLog {
	
	public SingleLog() {	
	}
	
	public static SingleLog Parse(JSONObject obj) {
		SingleLog log = new SingleLog();
		try {
			log.TimeStamp = obj.getString("TimeStamp");
			log.Level = obj.getString("Level");
			log.LoggerName = obj.getString("LoggerName");
			log.Message = obj.getString("Message");
			log.Millis = obj.getLong("Millis");
			log.SourceClassName = obj.getString("SourceClassName");
			log.SourceMethodName = obj.getString("SourceMethodName");
			log.LineNumber = obj.getInt("LineNumber");
		} catch (Exception e) {
			Common.PrintException(e);
		}
		return log;
	}
	
	public JSONObject ToJSON() {
		JSONObject obj = new JSONObject();
		obj.put("TimeStamp", TimeStamp);
		obj.put("Level", Level);
		obj.put("LoggerName", LoggerName);
		obj.put("Message", Message);
		obj.put("Millis", Millis);
		obj.put("SourceClassName", SourceClassName);
		obj.put("SourceMethodName", SourceMethodName);
		obj.put("LineNumber", LineNumber);
		return obj;
	}
	
	public String TimeStamp = "";
	public String Level = "";
	public String LoggerName = "";
	public String Message = "";
	public long Millis = -1;
	public String SourceClassName = "";
	public String SourceMethodName = "";
	public int LineNumber = -1;
}
