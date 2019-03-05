package flyingbot.it.data.hq;

import org.json.JSONObject;

public interface TransferredData<T> extends Comparable<T>{

    JSONObject ToJSON();

}
