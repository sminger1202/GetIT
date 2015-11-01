package com.android.getit.netBeanLoader;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.getit.GetITApp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.security.KeyStore;
import java.util.List;
import java.util.zip.GZIPInputStream;


/**
 * 网络请求 
 * @author gengchenghai
 * {@link com.tudou.pgc.netBeanLoader.baseNetBean.INetBean}
 * {@link IBeanLoader}
 */
public class HttpRequestImpl implements IHttpRequest {

	private static final String TAG = HttpRequestImpl.class.getSimpleName();
	/**
	 * 网络加载失败的时候重复加载的次数
	 */
	private static final int 		REPEAT_NUM = 1;

	private IHttpRequestCallback 	mCallback ;
	private int 					mTag ;
	private static HttpClient customerHttpClient;
	private boolean isAdIntent;
	private static final String AD_TAG = "adv/startpage";
	public String formatUri;
	private String dataString;


	public HttpRequestImpl(Context context){
	}

	@Override
	public void startRequest(String requestUrl, List<BasicNameValuePair> params, IHttpRequestCallback callback ,boolean bPost , int tag,boolean isSaveCookie,boolean isSetCookie ) {
		if(callback != null){
			if(callback == null || TextUtils.isEmpty(requestUrl) || params == null ){
				callback.onRequsetComplete(false, ERROR_CODE_PARAM_ERROR, null , tag) ;
			}else{
				mTag = tag ;
				mCallback = callback ;
				HttpRequestThread requestThread = new  HttpRequestThread(requestUrl , params , bPost ,isSaveCookie,isSetCookie) ;
				requestThread.start() ;
			}
		}
	}
	
	class HttpRequestThread extends Thread {
		/**
		 * 标示请求方法默认为post方法
		 */
		private boolean 					bPost = true;
		
		/**
		 * 请求的地址
		 */
		private String mRequestUrl;
		private List<BasicNameValuePair> mParams = null;
		private HttpClient mHttpClient = null;
		private boolean 					mIsSaveCookie=false;
		private boolean 					mIsSetCookie=false;
		HttpRequestThread(String requestUrl, List<BasicNameValuePair> params , boolean post ,boolean isSaveCookie,boolean isSetCookie){
			mRequestUrl = requestUrl ;
			mParams = params ;
			bPost = post ;
			mHttpClient = getHttpClient();
			mIsSaveCookie = isSaveCookie;
			mIsSetCookie = isSetCookie;
		}

		@Override
		public void run() {
			int errorCode = -1 ;
			for(int i = 0 ; i <= REPEAT_NUM ; i ++){
				errorCode = doRequest() ;
				if(errorCode == ERROR_CODE_NO_ERROR){
					break ;
				}
			}

			if(errorCode != ERROR_CODE_NO_ERROR && mCallback != null){
				mCallback.onRequsetComplete(false, errorCode, null , mTag) ;
			}
		}
		
		private int doRequest(){
			int resp_code = 0 ;
			String curUrl = mRequestUrl;
			HttpResponse httpResponse = null;
			try {
				if (bPost) {
					HttpPost httpRequest = getHttpPostFromUrlStr(curUrl);
					httpRequest.setEntity(new UrlEncodedFormEntity(mParams, HTTP.UTF_8));

					if(mIsSaveCookie){
						httpResponse = mHttpClient.execute(httpRequest);
						toGetCookie(mHttpClient);
					}else {
						httpResponse = mHttpClient.execute(httpRequest);
					}
				} else {
					HttpGet httpRequest = getHttpGetFromUrlStr(curUrl);
					if(mIsSetCookie){
						if (mIsSetCookie && !TextUtils.isEmpty(GetITApp.COOKIE)) {
							httpRequest.addHeader((new BasicHeader("Cookie",GetITApp.COOKIE)));
							httpResponse = mHttpClient.execute(httpRequest);

							Log.e(TAG, "mIsSetCookie--->" + GetITApp.COOKIE);
						}
					}else {
						httpResponse = mHttpClient.execute(httpRequest);
					}
				}

				resp_code = httpResponse.getStatusLine().getStatusCode();
			} catch (Exception e) {
				Log.e("", "========= url" + curUrl) ;
				e.printStackTrace();
				return ERROR_CODE_NET_ERROR ;
			}
			
			if (resp_code != HttpStatus.SC_OK) {
				return resp_code ;
			}
			
			String resultJson = null;
			InputStream stream = null;
			try {
				HttpEntity entity = httpResponse.getEntity();
				if (entity.getContentEncoding() != null && entity.getContentEncoding().getValue().contains("gzip")) {
					stream = new GZIPInputStream(entity.getContent());
				} else {
					stream = entity.getContent(); // 110608
				}
				int i = (int) entity.getContentLength();
				if (i < 0) {
					i = 4096;
				}

				Reader reader = new InputStreamReader(stream, HTTP.UTF_8);
				CharArrayBuffer buffer = new CharArrayBuffer(i);
				try {
					char[] tmp = new char[1024];
					int l;
					while ((l = reader.read(tmp)) != -1) {
						buffer.append(tmp, 0, l);
					}
				} finally {
					reader.close();
				}
				resultJson = buffer.toString();
				Log.d(TAG, "resultJson:" + resultJson);
				if(!TextUtils.isEmpty(resultJson) ){
					if(mCallback != null){
						mCallback.onRequsetComplete(true, ERROR_CODE_NO_ERROR, resultJson , mTag) ;
					}
				}else{
					if(mCallback != null){
						mCallback.onRequsetComplete(false, ERROR_CODE_JSON_ERROR, null , mTag) ;
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				return ERROR_CODE_NET_ERROR ;
			}
			
			if(httpResponse != null){
				try {
					httpResponse.getEntity().consumeContent();
				} catch (IOException e) {
				}
			}
			return ERROR_CODE_NO_ERROR ;
		}
		

	}

	/** post方式通过urlStr 获取HttpPost */
	public HttpPost getHttpPostFromUrlStr(String urlStr) throws MalformedURLException {
		HttpPost httpRequest = null;
		httpRequest = new HttpPost(urlStr);
		httpRequest.setHeader("Accept-Encoding", "gzip");
		return httpRequest;
	}

	public HttpGet getHttpGetFromUrlStr(String urlStr) throws MalformedURLException {
		HttpGet httpRequest = null;
		httpRequest = new HttpGet(urlStr);
		httpRequest.setHeader("Accept-Encoding", "gzip");
		return httpRequest;
	}

	public static synchronized HttpClient getHttpClient() {
		if (null == customerHttpClient) {
			HttpParams params = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams.setUserAgent(params, "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
					+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
			// 超时设置
			/* 从连接池中取连接的超时时间 */
			ConnManagerParams.setTimeout(params, 5000);
			/* 连接超时 */
			HttpConnectionParams.setConnectionTimeout(params, 5000);
			/* 请求超时 */
			HttpConnectionParams.setSoTimeout(params, 10000);

			// 设置我们的HttpClient支持HTTP和HTTPS两种模式
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			KeyStore trustStore = null;
			SSLSocketFactory sf = null;
			try {
				trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustStore.load(null, null);
				sf = new SSLSocketFactoryEx(trustStore);
				sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			} catch (Exception e) {
				e.printStackTrace();
				sf = SSLSocketFactory.getSocketFactory();
			}
			schReg.register(new Scheme("https", sf, 443));

			// 使用线程安全的连接管理来创建HttpClient
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

			customerHttpClient = new DefaultHttpClient(conMgr ,params );
		}
		return customerHttpClient;
	}

	private void toGetCookie(HttpClient httpClient) {
		List<Cookie> cookies = ((AbstractHttpClient) httpClient).getCookieStore().getCookies();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cookies.size(); i++) {
			Cookie cookie = cookies.get(i);
			String cookieName = cookie.getName();
			String cookieValue = cookie.getValue();
			if (!TextUtils.isEmpty(cookieName)
					&& !TextUtils.isEmpty(cookieValue)) {
				sb.append(cookieName + "=");
				sb.append(cookieValue+";");
			}
		}

		if (!TextUtils.isEmpty(sb.toString())) {
			if (TextUtils.isEmpty(GetITApp.COOKIE)) {
				GetITApp.COOKIE = sb.toString();
//				TicketApplication.savePreference(
//						LoginActivity.COOKIE,
//						TudouPgcApplication.COOKIE);
			} else {
				if (!GetITApp.COOKIE.equals(sb.toString())) {
					GetITApp.COOKIE = sb.toString();
//					TicketApplication.savePreference(
//							LoginActivity.COOKIE,
//							TudouPgcApplication.COOKIE);
				} else {
				}
			}

		}
		Log.e(TAG, "cookie.toString()------>" + sb.toString());
	}
//	public String downloadUri(String uri, String method, boolean isSetCookie)
//			throws NullPointerException {
//		int connectTimeout = Util.isWifi() ? TudouPgcApplication.HTTP_CONNECT_TIMEOUT_WIFI : TudouPgcApplication.HTTP_CONNECT_TIMEOUT_3G;
//		int readTimeout = Util.isWifi() ? TudouPgcApplication.HTTP_READ_TIMEOUT_WIFI : TudouPgcApplication.HTTP_READ_TIMEOUT_3G;
//		return downloadUri(uri, method, isSetCookie, connectTimeout, readTimeout);
//	}
//
//	/**
//	 * 下载给出Uri的数据
//	 *
//	 * @param uri
//	 * @param method
//	 * @param isSetCookie
//	 * @return
//	 * @throws NullPointerException
//	 */
//	public String downloadUri(String uri, String method, boolean isSetCookie, int connectTimeout, int readTimeOut)
//			throws NullPointerException {
//		if (Util.hasInternet()) {
//			isAdIntent = uri.contains(AD_TAG);
//			if (isAdIntent) {
//				return null;
//			}
//			dataString = downloadUri(this.uri, method, isSetCookie, connectTimeout, readTimeOut);
//			return dataString;
//		}
//		}


}
