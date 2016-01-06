package com.android.getit.network;

public interface IHttpRequest {

	public void request(HttpIntent httpIntent, IHttpRequestCallBack callBack);
	
	public <T> T parse(T dataObject);
	
	/**
	 * 获得接口未解析时的数据String
	 * @return
	 */
	public String getDataString();
	
	public void cancel();
	
	public boolean isCancel();
	
	public interface IHttpRequestCallBack{
		
		public void onSuccess(HttpRequestManager httpRequestManager);
		
		public void onFailed(String failReason);
		
//		public void onCancel();
	}
    public String downloadUri(String uri, String method, boolean isSetCookie) throws NullPointerException;

    public String downloadUri(String uri, String method, boolean isSetCookie, int connectTimeout, int readTimeOut) throws NullPointerException;

}