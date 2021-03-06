package flyingbot.it.data.hq;

import org.json.JSONException;
import org.json.JSONObject;

public class Candle implements TransferredData<Candle>{

    public final static String DataType = "CThostFtdcCandleField";
	
	public Candle() {
		this.ActionDay = "";
		this.TradingDay = "";
		this.UpdateTime = "";
		this.InstrumentID = "";
		this._METADATA_ = "";
	}

	public static Candle Parse(JSONObject Json) throws JSONException {
		if (Json == null)
			return new Candle();
		Candle cd = new Candle();
		cd.High = Json.getDouble("High");
		cd.Low = Json.getDouble("Low");
		cd.Close = Json.getDouble("Close");
		cd.Open = Json.getDouble("Open");
		cd.ActionDay = Json.getString("ActionDay");
		cd.TradingDay = Json.getString("TradingDay");
		cd.UpdateTime = Json.getString("UpdateTime");
		cd.Volume = Json.getInt("Volume");
		cd.OpenInterest = Json.getInt("OpenInterest");
		cd.InstrumentID = Json.getString("InstrumentID");
		cd.SerialNo = Json.getString("SerialNo");
		cd.Period = Json.getInt("Period");
		cd._METADATA_ = Json.getString("_METADATA_");
		return cd;
	}

	@Override
	public JSONObject ToJSON() {
		JSONObject obj =  new JSONObject();
		obj.put("High", High);
		obj.put("Low", Low);
		obj.put("Close", Close);
		obj.put("Open", Open);
		obj.put("ActionDay", ActionDay);
		obj.put("TradingDay", TradingDay);
		obj.put("UpdateTime", UpdateTime);
		obj.put("Volume", Volume);
		obj.put("OpenInterest", OpenInterest);
		obj.put("InstrumentID", InstrumentID);
		obj.put("SerialNo", SerialNo);
		obj.put("Period", Period);
		obj.put("_METADATA_", _METADATA_);
		return obj;
	}
	
	@Override
	public int compareTo(Candle Cnd) {
        return this.SerialNo.compareTo(Cnd.SerialNo);
	}

	public double  High;
	public double  Low;
	public double  Close;
	public double  Open;
	public String  ActionDay;
	public String  TradingDay;
	public String  UpdateTime;
	public int Volume;
	public int OpenInterest;
	public String InstrumentID;
	public String SerialNo;
	public int Period;
	public String _METADATA_;
}
