package flyingbot.it.util;

/**
 * ��װ�ӿڷ���ֵ��
 * @author �º���
 *
 */
public class Result {

	/**
     * {@link ResultState}
	 */
	public ResultState State;
	/**
     * Result message.
	 */
	public String Message;
	/**
     * Result code.
	 */
	public int Code;
	/**
     * Other info.
	 */
	public double _0, _1, _2, _3, _4, _5, _6, _7, _8, _9;

    /**
     * Success by default.
	 */
	public Result() {
		State = ResultState.Success;
		Code = 0;
		Message = "";
	}

    public final static ResultState Success = ResultState.Success;

    public final static ResultState Error = ResultState.Error;

    /**
     * Result types.
     */
    public enum ResultState {
        Success, Error
    }

	public Result(ResultState state, int code, String message) {
		super();
		State = state;
		Message = message;
		Code = code;
		this._0 = 0;
		this._1 = 0;
		this._2 = 0;
		this._3 = 0;
		this._4 = 0;
		this._5 = 0;
		this._6 = 0;
		this._7 = 0;
		this._8 = 0;
		this._9 = 0;
	}

	@Override
	public boolean equals(Object R) {
		if (R instanceof Result) {
			Result r = (Result) R;
			return (State == r.State);
		}
		if (R instanceof ResultState) {
			return (State == R);
		}
		return false;
	}

}
