package flyingbot.data.hq;

import org.json.*;

public interface TransferredData<T> extends Comparable<T>{

	/*获得数据的JSON表示形式*/
	public JSONObject ToJSON();

}
