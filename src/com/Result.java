package com;

/**
 * 封装接口返回值。
 * @author 陈宏葆
 *
 */
public class Result {

	/**
	 * 接口调用的返回状态。
	 * <li><b>Success</b>: 成功</li>
	 * <li><b>Error</b>: 失败</li>
	 * @author 陈宏葆
	 *
	 */
	public static enum ResultState {
		Success, Error
	}

	/**
	 * 本次调用的返回状态。{@link ResultState}
	 */
	public ResultState State;
	
	/**
	 * 本次调用的返回消息。
	 */
	public String Message;
	
	/**
	 * 本次调用的返回代码。
	 */
	public int Code;
	
	/**
	 * 本次调用的附加信息。
	 */
	public double _0, _1, _2, _3, _4, _5, _6, _7, _8, _9;

	/**
	 * 成功的静态状态，便于使用。
	 */
	public final static ResultState Success = ResultState.Success;
	

	/**
	 * 失败的静态状态，便于使用。
	 */
	public final static ResultState Error = ResultState.Error;

	/**
	 * 默认构造函数，状态为Success，返回码为0。
	 */
	public Result() {
		State = ResultState.Success;
		Code = 0;
		Message = "";
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
