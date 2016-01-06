package com.android.getit.network;

import android.content.Intent;

import com.android.getit.Utils.Utils;

/**
 * Created by sminger on 2016/1/5.
 */
public class HttpIntent extends Intent {
    public static final String URI = "uri";

    public static final String METHOD = "method";

    public static final String IS_SET_COOKIE = "is_set_cookie";

    public static final String READ_TIMEOUT = "read_timeout";

    public static final String CONNECT_TIMEOUT = "connect_timeout";

    public static final String POST_DATA = "data";

    /**
     * 是否缓存接口返回数据到本地
     */
    public static final String IS_CACHE_DATA = "is_cache_data";

    /**
     * 连接超时，读取超时
     */
    public int connectTimeout, readTimeout;

    private Object parseObject;

    public HttpIntent(String uri) {
        this(uri, HttpRequestManager.METHOD_GET, false);
    }

    public HttpIntent(String uri, String reqMethod) {
        this(uri, reqMethod, false);
    }

    public HttpIntent(String uri, boolean isSetCookie) {
        this(uri, HttpRequestManager.METHOD_GET, isSetCookie);
    }

    public HttpIntent(String uri, String reqMethod, boolean isSetCookie) {
        putExtra(URI, uri);
        putExtra(METHOD, reqMethod);
        putExtra(IS_SET_COOKIE, isSetCookie);
        putExtra(IS_CACHE_DATA, true);
        putExtra(CONNECT_TIMEOUT, Utils.isWifi() ? Utils.HTTP_CONNECT_TIMEOUT_WIFI : Utils.HTTP_CONNECT_TIMEOUT_3G);
        putExtra(READ_TIMEOUT, Utils.isWifi() ? Utils.HTTP_READ_TIMEOUT_WIFI : Utils.HTTP_READ_TIMEOUT_3G);
    }

    public HttpIntent(String uri, String reqMethod, boolean isSetCookie, String jsonString) {
        putExtra(URI, uri);
        putExtra(METHOD, reqMethod);
        putExtra(IS_SET_COOKIE, isSetCookie);
        putExtra(IS_CACHE_DATA, true);
        putExtra(CONNECT_TIMEOUT, Utils.isWifi() ? Utils.HTTP_CONNECT_TIMEOUT_WIFI : Utils.HTTP_CONNECT_TIMEOUT_3G);
        putExtra(READ_TIMEOUT, Utils.isWifi() ? Utils.HTTP_READ_TIMEOUT_WIFI : Utils.HTTP_READ_TIMEOUT_3G);
        putExtra(POST_DATA, jsonString);
    }


    /**
     * @param connectTimeout the connectTimeout to set
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        putExtra(CONNECT_TIMEOUT, connectTimeout);
    }

    /**
     * @param readTimeout the readTimeout to set
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        putExtra(READ_TIMEOUT, readTimeout);
    }

    public void putParseObject(Object o) {
        parseObject = o;
    }

    public Object getParseObject() {
        return parseObject;
    }

    /**
     * 设置是否需要缓存接口数据，缺省值是true
     *
     * @param isCache
     */
    public HttpIntent setCache(boolean isCacheData) {
        putExtra(IS_CACHE_DATA, isCacheData);
        return this;
    }
}
