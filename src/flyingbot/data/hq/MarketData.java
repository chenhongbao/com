package flyingbot.data.hq;

import org.json.JSONException;
import org.json.JSONObject;

public class MarketData implements TransferredData<MarketData>{
	
	/*
	 * 数据类型值
	 * 该值与CTP类型的名称相同。
	 * */
	public final static String DataType = "CThostFtdcDepthMarketDataField";

	public MarketData() {
		this.ActionDay = "";
		this.TradingDay = "";
		this.ExchangeID = "";
		this.ExchangeInstID = "";
		this.UpdateTime = "";
		this.InstrumentID = "";
		this._METADATA_ = "";
	}

	public static MarketData Parse(JSONObject Json) throws JSONException {
		if (Json == null)
			return new MarketData();
		MarketData md = new MarketData();
		md.TradingDay = Json.getString("TradingDay");
		md.InstrumentID = Json.getString("InstrumentID");
		md.ExchangeID = Json.getString("ExchangeID");
		md.ExchangeInstID = Json.getString("ExchangeInstID");
		md.LastPrice = Json.getDouble("LastPrice");
		md.PreSettlementPrice = Json.getDouble("PreSettlementPrice");
		md.PreClosePrice = Json.getDouble("PreClosePrice");
		md.PreOpenInterest = Json.getDouble("PreOpenInterest");
		md.OpenPrice = Json.getDouble("OpenPrice");
		md.HighestPrice = Json.getDouble("HighestPrice");
		md.LowestPrice = Json.getDouble("LowestPrice");
		md.Volume = Json.getInt("Volume");
		md.Turnover = Json.getDouble("Turnover");
		md.OpenInterest = Json.getDouble("OpenInterest");
		md.ClosePrice = Json.getDouble("ClosePrice");
		md.SettlementPrice = Json.getDouble("SettlementPrice");
		md.UpperLimitPrice = Json.getDouble("UpperLimitPrice");
		md.LowerLimitPrice = Json.getDouble("LowerLimitPrice");
		md.PreDelta = Json.getDouble("PreDelta");
		md.CurrDelta = Json.getDouble("CurrDelta");
		md.UpdateTime = Json.getString("UpdateTime");
		md.UpdateMillisec = Json.getInt("UpdateMillisec");
		md.BidPrice1 = Json.getDouble("BidPrice1");
		md.BidVolume1 = Json.getInt("BidVolume1");
		md.AskPrice1 = Json.getDouble("AskPrice1");
		md.AskVolume1 = Json.getInt("AskVolume1");
		md.BidPrice2 = Json.getDouble("BidPrice2");
		md.BidVolume2 = Json.getInt("BidVolume2");
		md.AskPrice2 = Json.getDouble("AskPrice2");
		md.AskVolume2 = Json.getInt("AskVolume2");
		md.BidPrice3 = Json.getDouble("BidPrice3");
		md.BidVolume3 = Json.getInt("BidVolume3");
		md.AskPrice3 = Json.getDouble("AskPrice3");
		md.AskVolume3 = Json.getInt("AskVolume3");
		md.BidPrice4 = Json.getDouble("BidPrice4");
		md.BidVolume4 = Json.getInt("BidVolume4");
		md.AskPrice4 = Json.getDouble("AskPrice4");
		md.AskVolume4 = Json.getInt("AskVolume4");
		md.BidPrice5 = Json.getDouble("BidPrice5");
		md.BidVolume5 = Json.getInt("BidVolume5");
		md.AskPrice5 = Json.getDouble("AskPrice5");
		md.AskVolume5 = Json.getInt("AskVolume5");
		md.AveragePrice = Json.getDouble("AveragePrice");
		md.ActionDay = Json.getString("ActionDay");
		md.SerialNo = Json.getString("SerialNo");
		md._METADATA_ = Json.getString("_METADATA_");
		return md;
	}

	@Override
	public JSONObject ToJSON() {
		JSONObject obj = new JSONObject();
		obj.put("TradingDay" ,TradingDay);
		obj.put("InstrumentID" ,InstrumentID);
		obj.put("ExchangeID" ,ExchangeID);
		obj.put("ExchangeInstID" ,ExchangeInstID);
		obj.put("LastPrice" ,LastPrice);
		obj.put("PreSettlementPrice" ,PreSettlementPrice);
		obj.put("PreClosePrice" ,PreClosePrice);
		obj.put("PreOpenInterest" ,PreOpenInterest);
		obj.put("OpenPrice" ,OpenPrice);
		obj.put("HighestPrice" ,HighestPrice);
		obj.put("LowestPrice" ,LowestPrice);
		obj.put("Volume" ,Volume);
		obj.put("Turnover" ,Turnover);
		obj.put("OpenInterest" ,OpenInterest);
		obj.put("ClosePrice" ,ClosePrice);
		obj.put("SettlementPrice" ,SettlementPrice);
		obj.put("UpperLimitPrice" ,UpperLimitPrice);
		obj.put("LowerLimitPrice" ,LowerLimitPrice);
		obj.put("PreDelta" ,PreDelta);
		obj.put("CurrDelta" ,CurrDelta);
		obj.put("UpdateTime" ,UpdateTime);
		obj.put("UpdateMillisec" ,UpdateMillisec);
		obj.put("BidPrice1" ,BidPrice1);
		obj.put("BidVolume1" ,BidVolume1);
		obj.put("AskPrice1" ,AskPrice1);
		obj.put("AskVolume1" ,AskVolume1);
		obj.put("BidPrice2" ,BidPrice2);
		obj.put("BidVolume2" ,BidVolume2);
		obj.put("AskPrice2" ,AskPrice2);
		obj.put("AskVolume2" ,AskVolume2);
		obj.put("BidPrice3" ,BidPrice3);
		obj.put("BidVolume3" ,BidVolume3);
		obj.put("AskPrice3" ,AskPrice3);
		obj.put("AskVolume3" ,AskVolume3);
		obj.put("BidPrice4" ,BidPrice4);
		obj.put("BidVolume4" ,BidVolume4);
		obj.put("AskPrice4" ,AskPrice4);
		obj.put("AskVolume4" ,AskVolume4);
		obj.put("BidPrice5" ,BidPrice5);
		obj.put("BidVolume5" ,BidVolume5);
		obj.put("AskPrice5" ,AskPrice5);
		obj.put("AskVolume5" ,AskVolume5);
		obj.put("AveragePrice" ,AveragePrice);
		obj.put("ActionDay" ,ActionDay);
		obj.put("Period", Period);
		obj.put("SerialNo", SerialNo);
		obj.put("_METADATA_" ,_METADATA_);
		return obj;
	}
	
	@Override
	public int compareTo(MarketData Md) {
		if (this.ActionDay.compareTo(Md.ActionDay) < 0)
			return -1;
		else 
			if (this.ActionDay.compareTo(Md.ActionDay) == 0 && this.UpdateTime.compareTo(Md.UpdateTime) < 0)
				return -1;
			else
				if (this.ActionDay.compareTo(Md.ActionDay) == 0 && this.UpdateTime.compareTo(Md.UpdateTime) == 0 
				&& this.UpdateMillisec < Md.UpdateMillisec)
					return -1;
				else
					if (this.ActionDay.compareTo(Md.ActionDay) == 0 && this.UpdateTime.compareTo(Md.UpdateTime) == 0 
					&& this.UpdateMillisec == Md.UpdateMillisec)
						return 0;
		return 1;
	}

	/// 交易日
	public String TradingDay;
	/// 合约代码
	public String InstrumentID;
	/// 交易所代码
	public String ExchangeID;
	/// 合约在交易所的代码
	public String ExchangeInstID;
	/// 最新价
	public double LastPrice;
	/// 上次结算价
	public double PreSettlementPrice;
	/// 昨收盘
	public double PreClosePrice;
	/// 昨持仓量
	public double PreOpenInterest;
	/// 今开盘
	public double OpenPrice;
	/// 最高价
	public double HighestPrice;
	/// 最低价
	public double LowestPrice;
	/// 数量
	public int Volume;
	/// 成交金额
	public double Turnover;
	/// 持仓量
	public double OpenInterest;
	/// 今收盘
	public double ClosePrice;
	/// 本次结算价
	public double SettlementPrice;
	/// 涨停板价
	public double UpperLimitPrice;
	/// 跌停板价
	public double LowerLimitPrice;
	/// 昨虚实度
	public double PreDelta;
	/// 今虚实度
	public double CurrDelta;
	/// 最后修改时间
	public String UpdateTime;
	/// 最后修改毫秒
	public int UpdateMillisec;
	/// 申买价一
	public double BidPrice1;
	/// 申买量一
	public int BidVolume1;
	/// 申卖价一
	public double AskPrice1;
	/// 申卖量一
	public int AskVolume1;
	/// 申买价二
	public double BidPrice2;
	/// 申买量二
	public int BidVolume2;
	/// 申卖价二
	public double AskPrice2;
	/// 申卖量二
	public int AskVolume2;
	/// 申买价三
	public double BidPrice3;
	/// 申买量三
	public int BidVolume3;
	/// 申卖价三
	public double AskPrice3;
	/// 申卖量三
	public int AskVolume3;
	/// 申买价四
	public double BidPrice4;
	/// 申买量四
	public int BidVolume4;
	/// 申卖价四
	public double AskPrice4;
	/// 申卖量四
	public int AskVolume4;
	/// 申买价五
	public double BidPrice5;
	/// 申买量五
	public int BidVolume5;
	/// 申卖价五
	public double AskPrice5;
	/// 申卖量五
	public int AskVolume5;
	/// 当日均价
	public double AveragePrice;
	/// 业务日期
	public String ActionDay;
	/// 行情数据周期（默认0，不可变）
	public final int Period = 0;
	/// 该数据的序列号，用于唯一标识该数据，递增
	public String SerialNo;
	/// 元数据，表示该数据类型
	public String _METADATA_;
}
