package flyingbot.data.hq;

import org.json.*;

public interface TransferredData<T> extends Comparable<T>{

	/*������ݵ�JSON��ʾ��ʽ*/
	public JSONObject ToJSON();

}
