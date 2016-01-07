package com.android.getit.network;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.getit.GetITApp;
import com.android.getit.UserModule.UserHelper;
import com.android.getit.Utils.Utils;
import com.android.getit.Utils.YoukuAsyncTask;
import com.android.getit.android.GetITAPI;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * * HTTP请求类 使用方法示例： Initial initial = new Initial(); IHttpRequest httpRequest =
 * YoukuService.getService(IHttpRequest.class, true); HttpIntent httpIntent =
 * new HttpIntent(URLContainer.getInitURL());
 * <p/>
 * httpRequest.request(httpIntent, new IHttpRequestCallBack() {
 *
 * @author fengqve
 * @Override public void onSuccess(HttpRequestManager httpRequestManager) {
 * initial = httpRequestManager.parse(initial); }
 * @Override public void onFailed(String failReason) {
 * <p/>
 * } });
 */

public class HttpRequestManager implements IHttpRequest {

    public static final int SUCCESS = 0x1;
    public static final int FAIL = 0x2;

    /**
     * 执行状态
     */
    private int state = FAIL;

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    private String method = METHOD_GET;

    private boolean isSetCookie;

    /**
     * 失败原因
     */
    public String fail_reason;

    /**
     * 传递过来需要解析的数据对象
     */
    // private Object dataObject;

    /**
     * 从网络拿到的数据
     */
    private String dataString;

    /**
     * 请求的url地址
     */
    public String uri;

    public String formatUri;

    // public static final String LOGIN_FAILED="login first";

    /**
     * 请求参数 在post的方式使用
     */
    public String postdata;

    /**
     * 处理数据时是否cancle。
     */
    private boolean IScancle = false;

    public HttpRequestManager() {

    }

    /**
     * 设置请求方法
     *
     * @param method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 是否是可能带广告链接
     */
    //private boolean isAdIntent;
    public static final String TUDOU_AD_COOKIE = "ad_cookie";
    public static final String TUDOU_HOME_AD_COOKIC = "home_ad_cookic";
    private static final String AD_TAG = "adv/startpage";

    public String downloadUri(String uri, String method, boolean isSetCookie)
            throws NullPointerException {
        int connectTimeout = Utils.isWifi() ? Utils.HTTP_CONNECT_TIMEOUT_WIFI : Utils.HTTP_CONNECT_TIMEOUT_3G;
        int readTimeout = Utils.isWifi() ? Utils.HTTP_READ_TIMEOUT_WIFI : Utils.HTTP_READ_TIMEOUT_3G;
        return downloadUri(uri, method, isSetCookie, connectTimeout, readTimeout);
    }

    /**
     * 下载给出Uri的数据
     *
     * @param uri
     * @param method
     * @param isSetCookie
     * @return
     * @throws NullPointerException
     */
    public String downloadUri(String uri, String method, boolean isSetCookie, int connectTimeout, int readTimeOut)
            throws NullPointerException {
        if (Utils.hasInternet()) {
            InputStream is = null;
            int isModify = 0;
            try {
                URL url = new URL(uri);
                Log.d("GetIT", "url = " + url);
                Log.d("HttpRequestManager#downloadUri()", uri);
                formatUri = GetITAPI.formatURL(uri, isSetCookie);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setReadTimeout(readTimeOut);
                conn.setConnectTimeout(connectTimeout);
                conn.setRequestMethod(method);
                conn.setDoInput(true);
                Log.d("TAG_TUDOU", "formatUri=====" + formatUri);
                if (!TextUtils.isEmpty(GetITApp.getPreference(formatUri))) {
                    try {
                        dataString = GetITAPI.readUrlCacheFromLocal(formatUri);
                        new JSONObject(dataString);
                        if (dataString.contains("[]")) {
                            throw new Exception();
                        }
                        conn.setRequestProperty("if-None-Match",
                                GetITApp.getPreference(formatUri));

                        isModify = 1;
                        // Log.d("testcache12",
                        // ""+Youku.getPreference(formatUri));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        Log.d("Youku", "" + e.getMessage());
                        e.printStackTrace();
                    }
                    //Logger.d("TAG_TUDOU", "local sucess=====" + dataString);
                } else {
                    //Logger.d("TAG_TUDOU", "local failed=====");
                }

                if (isSetCookie && !TextUtils.isEmpty(GetITApp.COOKIE)) {
                    conn.setRequestProperty("Cookie", GetITApp.COOKIE);
                }
                conn.setRequestProperty("User-Agent", GetITApp.User_Agent);
                if (!TextUtils.isEmpty(postdata)
                        && METHOD_POST.equals(method)) {
                    conn.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream(
                            conn.getOutputStream());
                    out.write(postdata.getBytes("UTF-8"));
                    out.flush();
                    out.close(); // flush and close
                } else {
                    conn.connect();
                }

                // YoukuService.getService(ITracker.class).trackNetEvent(
                // BaseEvent.netRequest, uri, "");
                int response = conn.getResponseCode();
                Log.d("YoukuCacheTag", conn.getHeaderFields().toString());
                String eTag = conn.getHeaderField("Etag");
                Log.d("TAG_TUDOU", "response code=====" + response);
                //		Logger.d("TAG_TUDOU", "eTag=====" + eTag);
                if (response == HttpURLConnection.HTTP_OK) {
                    is = conn.getInputStream();
                    dataString = Utils.convertStreamToString(is);
                    String cookie = getCookie(conn);
//						conn.getHeaderField("set-cookie");
                    Log.d("Youku", "dataString = " + dataString);
                    Log.d("Youku", "cookie = " + cookie);
                    try {
                        new JSONObject(dataString);
                        state = SUCCESS;
                        if (!TextUtils.isEmpty(cookie)) {
                            if (TextUtils.isEmpty(GetITApp.COOKIE)) {
                                GetITApp.COOKIE = cookie;
                                GetITApp.savePreference(
                                        UserHelper.COOKIE,
                                        GetITApp.COOKIE);
                            } else {
                                if (!GetITApp.COOKIE.equals(cookie)) {
                                    GetITApp.COOKIE = cookie;
                                    GetITApp.savePreference(
                                            UserHelper.COOKIE,
                                            GetITApp.COOKIE);
                                } else {
                                }
                            }

                        }
                        if (eTag != null) {
                            // Log.d("YoukuCacheTag",
                            // conn.getHeaderFields().toString());
                            GetITApp.savePreference(formatUri, eTag);
                            // Log.d("testcache152",
                            // "formatUri:"+formatUri);
                            if (isModify == 1) {
                                Log.d("YoukuCacheTag",
                                        "is Modify:" + true);
                            }
                            GetITAPI.saveUrlCacheToLocal(formatUri, dataString);
                        }
                    } catch (Exception e) {
                        state = FAIL;
                        fail_reason = STATE_ERROR_REQUEST_DATA_FAIL;
                    }
                    // if(dataString.contains("<")){
                    // state= FAIL;
                    // fail_reason = "所在Wi-fi不可用";
                    // }else{
                    // state = SUCCESS;
                    // }

                    // YoukuService.getService(ITracker.class).trackNetEvent(
                    // BaseEvent.netResponse, uri, "1");
                } else if (response == HttpURLConnection.HTTP_BAD_REQUEST) {
                    is = conn.getErrorStream();
                   /* fail_reason = parseLogoutJson(Utils.convertStreamToString(is));*/
                    // fail_reason=LOGIN_FAILED; //cookie失效 特征字符串
                } else if (response == HttpURLConnection.HTTP_NOT_MODIFIED) {
                    // Log.d("testcache153", "formatUri:"+formatUri);
                    // Log.d("YoukuCacheTag",
                    // conn.getHeaderFields().toString());
                    if (isModify == 1) {
                        Log.d("YoukuCacheTag", "is Modify:" + false
                                + " ETAG = " + formatUri);
                    }
                    state = SUCCESS;
                } else if (response == HttpURLConnection.HTTP_GONE) {
                    is = conn.getErrorStream();
                    String returnStr = Utils.convertStreamToString(is);
                    if (TextUtils.isEmpty(returnStr)) {
                        fail_reason = "time empty";
                    } else {
//                        double d = Double.parseDouble(returnStr);
//                        TudouURLContainer.TIMESTAMP = (long) d
//                                - System.currentTimeMillis() / 1000;
//                        if (!TextUtils.isEmpty(postdata)
//                                && METHOD_POST.equals(method)) {
//                            postdata = TudouURLContainer.updateUrlForPost(uri,
//                                    method, postdata);
//                        } else {
//                            this.uri = TudouURLContainer.updateUrl(this.uri,
//                                    method);
//                        }
//
//                        dataString = downloadUri(this.uri, method, isSetCookie, connectTimeout, readTimeOut);
                    }

                } else {
                    // fail_reason = String.valueOf(response);
                    fail_reason = "获取数据失败，请稍后重试";
                    // YoukuService.getService(ITracker.class).trackNetEvent(
                    // BaseEvent.netResponse, uri, "0");
                }
                return dataString;
            } catch (MalformedURLException e) {
                e.printStackTrace();

                fail_reason = STATE_ERROR_MALFORMED_URL_EXCEPTION;
                return dataString;
            } catch (ProtocolException e) {
                e.printStackTrace();

                fail_reason = STATE_ERROR_PROTOCOL_EXCEPTION;
                return dataString;
            } catch (SocketTimeoutException e) {
                e.printStackTrace();

                fail_reason = STATE_ERROR_TIMEOUT;
                return dataString;
            } catch (IOException e) {
                e.printStackTrace();

                fail_reason = STATE_ERROR_REQUEST_DATA_FAIL;
                return dataString;
            } finally {
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
        } else {
            fail_reason = STATE_ERROR_NO_NETWORK;
            return fail_reason;
        }
    }

    private String getCookie(HttpURLConnection conn) {
        String cookieVal = null;
        String key = null;
        String sum = "";
        for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
            if (key.equalsIgnoreCase("Set-Cookie")) {
                cookieVal = conn.getHeaderField(i);
                sum += cookieVal + ", ";
            }
        }
        if (TextUtils.isEmpty(sum)) {
            return null;
        }
        sum = sum.substring(0, sum.length() - 2);
        return sum;
    }

    /**
     * 无网络连接错误提示
     */
    public static final String STATE_ERROR_NO_NETWORK = "当前无网络连接，请检查您的网络";

    /**
     * IO异常错误提示
     */
    public static final String STATE_ERROR_IO_EXCEPTION = "IO异常";

    /**
     * 网络超时错误提示
     */
    // public static final String STATE_ERROR_TIMEOUT = "亲，网络状态不给力啊";
    public static final String STATE_ERROR_TIMEOUT = "网络不稳定，请稍后再试 ";

    /**
     * 协议错误提示
     */
    public static final String STATE_ERROR_PROTOCOL_EXCEPTION = "协议不正确哦";

    /**
     * URL地址错误提示
     */
    public static final String STATE_ERROR_MALFORMED_URL_EXCEPTION = "地址不合法哦";
    /**
     * URL地址错误提示
     */
    public static final String STATE_ERROR_UNABLE_URL_EXCEPTION = "所在Wi-fi不可用";

    public static final String STATE_ERROR_REQUEST_DATA_FAIL = "请求失败，请稍后再试 ";
    private YoukuAsyncTask<Object, Integer, Object> task;

    /**
     * 解析json数据
     *
     * @return
     * @throws NullPointerException
     */
    @SuppressWarnings("unchecked")
    public <T> T parse(T dataObject) throws NullPointerException {
        dataObject = (T) JSON.parseObject(dataString, dataObject.getClass());
        return dataObject;
    }

    @SuppressWarnings("unchecked")
    public static <T> T parse(String dataStr, T dataObject)
            throws NullPointerException {
        dataObject = (T) JSON.parseObject(dataStr, dataObject.getClass());
        return dataObject;
    }

    @Override
    public void request(HttpIntent httpIntent,
                        final IHttpRequestCallBack callBack) {
        uri = httpIntent.getStringExtra(HttpIntent.URI);
        method = httpIntent.getStringExtra(HttpIntent.METHOD);
        isSetCookie = httpIntent.getBooleanExtra(HttpIntent.IS_SET_COOKIE,
                false);

        final int connect_timeout_millis = httpIntent.getIntExtra(
                HttpIntent.CONNECT_TIMEOUT, 0);
        final int read_timout_millis = httpIntent.getIntExtra(HttpIntent.READ_TIMEOUT, 0);
        postdata = httpIntent.getStringExtra(HttpIntent.POST_DATA);
        task = new YoukuAsyncTask<Object, Integer, Object>() {

            @Override
            protected Object doInBackground(Object... params) {
                return downloadUri(uri, method, isSetCookie, connect_timeout_millis, read_timout_millis);
            }

            /*
             * @see
             * com.youku.phone.YoukuAsyncTask#onPostExecute(java.lang.Object)
             */
            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                switch (state) {
                    case SUCCESS:

                        if (callBack != null)
                            callBack.onSuccess(HttpRequestManager.this);
                        break;

                    case FAIL:

                        if (callBack != null) {
                            Log.d(
                                    "HttpRequestManager.request(...).new YoukuAsyncTask() {...}#onPostExecute()",
                                    fail_reason);
                            callBack.onFailed(fail_reason);
                        }
                        break;
                }
            }

        };

        task.execute();

    }

    @Override
    public boolean isCancel() {
        return IScancle;
    }

    @Override
    public void cancel() {
        IScancle = true;
        if (null != task && !task.isCancelled()) {
            task.cancel(true);
        }
    }

    @Override
    public String getDataString() {
        return dataString;
    }

}
