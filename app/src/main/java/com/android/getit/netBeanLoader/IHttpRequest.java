package com.android.getit.netBeanLoader;

import org.apache.http.message.BasicNameValuePair;

import java.util.List;

/**
 * 网络请求的接口
 * @author gengchenghai
 *
 */
public interface IHttpRequest {
	public static final String OFFICIAL_USER_HOST = "http://user.api.3g.tudou.com"  ;


	/**
	 * 穿入参数错误
	 */
	public static final int ERROR_CODE_PARAM_ERROR  = 1;
	/**
	 * 网络异常
	 */
	public static final int ERROR_CODE_NET_ERROR = 2;
	/**
	 * 网络超时
	 */
	public static final int ERROR_CODE_TIME_OUT = 3 ;
	/**
	 * 没有网络错误
	 */
	public static final int ERROR_CODE_NO_ERROR = 4 ;
	/**
	 *	返回数据错误 
	 */
	public static final int ERROR_CODE_JSON_ERROR = 5 ;
	
	
	/**
	 * 开始加载云端数据
	 * @param context
	 * @param requestUrl 网络加载的url
	 * @param params	网络加载的参数
	 * @param callback	callback 不可以为null 否则会抛出 null 异常 ，调用者注意
	 */
	public void startRequest(String requestUrl, List<BasicNameValuePair> params, IHttpRequestCallback callback, boolean bPost, int tag, boolean isSaveCookie, boolean isSetCookie) ;
}
