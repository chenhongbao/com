package flyingbot.it.data.hq;

import org.json.JSONException;
import org.json.JSONObject;

public class MarketData implements TransferredData<MarketData>{

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

	/// ������
	public String TradingDay;
	/// ��Լ����
	public String InstrumentID;
	/// ����������
	public String ExchangeID;
	/// ��Լ�ڽ������Ĵ���
	public String ExchangeInstID;
	/// ���¼�
	public double LastPrice;
	/// �ϴν����
	public double PreSettlementPrice;
	/// ������
	public double PreClosePrice;
	/// ��ֲ���
	public double PreOpenInterest;
	/// ����
	public double OpenPrice;
	/// ��߼�
	public double HighestPrice;
	/// ��ͼ�
	public double LowestPrice;
	/// ����
	public int Volume;
	/// �ɽ����
	public double Turnover;
	/// �ֲ���
	public double OpenInterest;
	/// ������
	public double ClosePrice;
	/// ���ν����
	public double SettlementPrice;
	/// ��ͣ���
	public double UpperLimitPrice;
	/// ��ͣ���
	public double LowerLimitPrice;
	/// ����ʵ��
	public double PreDelta;
	/// ����ʵ��
	public double CurrDelta;
	/// ����޸�ʱ��
	public String UpdateTime;
	/// ����޸ĺ���
	public int UpdateMillisec;
	/// �����һ
	public double BidPrice1;
	/// ������һ
	public int BidVolume1;
	/// ������һ
	public double AskPrice1;
	/// ������һ
	public int AskVolume1;
	/// ����۶�
	public double BidPrice2;
	/// ��������
	public int BidVolume2;
	/// �����۶�
	public double AskPrice2;
	/// ��������
	public int AskVolume2;
	/// �������
	public double BidPrice3;
	/// ��������
	public int BidVolume3;
	/// ��������
	public double AskPrice3;
	/// ��������
	public int AskVolume3;
	/// �������
	public double BidPrice4;
	/// ��������
	public int BidVolume4;
	/// ��������
	public double AskPrice4;
	/// ��������
	public int AskVolume4;
	/// �������
	public double BidPrice5;
	/// ��������
	public int BidVolume5;
	/// ��������
	public double AskPrice5;
	/// ��������
	public int AskVolume5;
	/// ���վ���
	public double AveragePrice;
	/// ҵ������
	public String ActionDay;
	/// �����������ڣ�Ĭ��0�����ɱ䣩
	public final int Period = 0;
	/// �����ݵ����кţ�����Ψһ��ʶ�����ݣ�����
	public String SerialNo;
	/// Ԫ���ݣ���ʾ����������
	public String _METADATA_;
}
