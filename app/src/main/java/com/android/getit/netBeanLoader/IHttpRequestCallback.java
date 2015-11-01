package com.android.getit.netBeanLoader;

/**
 * 网络加载的CALLBACK
 * @author gengchenghai
 *
 */
public interface IHttpRequestCallback {
	/**
	 * 加载完成的毁掉
	 * @param bSuccess   是否加载成功
	 * @param errorCode  加载失败的错误码
	 * @param json		  加载结果
	 * {@link IHttpRequest#ERROR_CODE_PARAM_ERROR}
	 * {@link IHttpRequest#ERROR_CODE_TIME_OUT}
	 * {@link IHttpRequest#ERROR_CODE_NET_ERROR}
	 */
	public void onRequsetComplete(boolean bSuccess, int errorCode, String json, int tag) ;
}
