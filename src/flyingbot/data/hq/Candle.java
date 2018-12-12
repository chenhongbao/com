package flyingbot.data.hq;

import org.json.JSONException;
import org.json.JSONObject;

public class Candle implements TransferredData<Candle>{
	
	/*
	 * 数据类型值
	 * 该值与UTP类型名称相同。
	 * */
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
		if (this.ActionDay.compareTo(Cnd.ActionDay) < 0)
			return -1;
		else 
			if (this.ActionDay.compareTo(Cnd.ActionDay) == 0  && this.UpdateTime.compareTo(Cnd.UpdateTime) < 0)
				return -1;
			else
				if (this.ActionDay.compareTo(Cnd.ActionDay) == 0 && this.UpdateTime.compareTo(Cnd.UpdateTime) == 0)
					return 0;			
		return 1;
	}

	/// K线最高价
	public double  High;
	/// K线最低价
	public double  Low;
	/// K线收盘价
	public double  Close;
	/// K线开盘价
	public double  Open;
	/// 业务日期
	public String  ActionDay;
	/// 交易日期
	public String  TradingDay;
	/// 更新时间
	public String  UpdateTime;
	/// K线成交量
	public int Volume;
	/// K线最后持仓量
	public int OpenInterest;
	/// 合约代码
	public String InstrumentID;
	/// 该数据的序列号，用于唯一标识该数据，递增
	public String SerialNo;
	/// 蜡烛线周期（分）
	public int Period;
	/// 元数据，表示该数据的类型
	public String _METADATA_;
}
