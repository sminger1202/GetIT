package com.android.getit.service;

import android.util.Log;

import com.android.getit.network.HttpRequestManager;
import com.android.getit.network.IHttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sminger on 2016/1/5.
 */
public class GetItService {
    public final static String TAG = "GetItService";
    private static Map<String, Object> services = new HashMap<String, Object>();
    static {
//        services.put(ILogin.class.getName(), LoginManager.getInstance());
//        services.put(IDevice.class.getName(), new DeviceManagerImpl(
//                Youku.context));
        services.put(IHttpRequest.class.getName(), new HttpRequestManager());
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> clazz) {
        Object s = services.get(clazz.getName());
        return (T) s;
    }

    /**
     * YoukuService 是否需要提供新对象
     *
     * @param clazz
     * @param isNeedNewObject
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> clazz, boolean isNeedNewObject) {
        if (!isNeedNewObject) {
            return getService(clazz);
        } else {
            try {
                Object s = getService(clazz).getClass().newInstance();
                return (T) s;
            } catch (InstantiationException e) {
                Log.e(TAG, "Service#getService()", e);
                return null;
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Service#getService()", e);
                return null;
            }

        }
    }
}
