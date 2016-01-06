package com.android.getit;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.getit.EMChatModule.ChatHelper;
import com.android.getit.Utils.DeviceInfo;
import com.android.getit.Utils.ImageLoaderManager;
import com.android.getit.Utils.FrescoLoaderImage;
import com.android.getit.Utils.Profile;
import com.android.getit.Utils.URLContainer;
import com.android.getit.Utils.Utils;
import com.android.getit.android.GetITAPI;
import com.android.getit.dataRequestResult.InitResult;
import com.android.getit.network.HttpIntent;
import com.android.getit.network.HttpRequestManager;
import com.android.getit.network.IHttpRequest;
import com.android.getit.service.GetItService;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sminger on 2015/11/1.
 */
public class GetITApp extends MultiDexApplication  {
    public static String TAG = GetITApp.class.getSimpleName();
    public static Context context = null;
    public static String versionName;

    public static String appName;

    private static SharedPreferences mSharedPreferences;

    public static String COOKIE = null;

    public static String User_Agent = null;

    public static boolean mIsMainProcess = false;

    public static String URLCacheDataPath = "";
    /*
      Please do not adjust the sequence of the following init work,due to these init work maybe have some dependence.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        context = this.getApplicationContext();

        isMainProcess();

        if(mIsMainProcess) {

            FrescoLoaderImage.InitLoaderImage(this);

            ImageLoaderManager.initImageLoaderConfiguration(this);

            DeviceInfo.init();

            initVersionInfo();

            initPreferences();

            initURLCacheDataPath();

            initEMChat();

            getSwitch();
        }
        
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }


    private void initVersionInfo() {
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_META_DATA).versionName;
            appName = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_META_DATA).applicationInfo.name;
            Profile.VER = versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "1.0";
            Profile.VER = versionName;
            Log.e("", e.toString());
        }
    }


    private String getCachePath() {
        return ((Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()) || !GetITAPI
                .isExternalStorageRemovable()) && (null != GetITAPI
                .getExternalCacheDir(context))) ? GetITAPI.getExternalCacheDir(
                context).getPath() : context.getCacheDir().getPath();
    }

    private void initURLCacheDataPath() {

        URLCacheDataPath = getCachePath() + File.separator + "cacheData";
    }

    private void initPreferences() {
//        mDownloadSharedPreferences = context.getSharedPreferences(context.getPackageName() + "_cache",
//                UIUtils.hasGingerbread() ? MODE_MULTI_PROCESS : MODE_PRIVATE);

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    }

    public static void savePreference(String key, String value) {
        if(Utils.hasGingerbread()) {
            mSharedPreferences.edit().putString(key, value).apply();
        } else {
            mSharedPreferences.edit().putString(key, value).commit();
        }
    }

    public static void savePreference(String key, int value) {
        if(Utils.hasGingerbread()) {
            mSharedPreferences.edit().putInt(key, value).apply();
        } else {
            mSharedPreferences.edit().putInt(key, value).commit();
        }
    }

    public static void savePreference(String key, Boolean value) {
        if(Utils.hasGingerbread()) {
            mSharedPreferences.edit().putBoolean(key, value).apply();
        } else {
            mSharedPreferences.edit().putBoolean(key, value).commit();
        }
    }

    public static void savePreference(String key, long value) {
        if(Utils.hasGingerbread()) {
            mSharedPreferences.edit().putLong(key, value).apply();
        } else {
            mSharedPreferences.edit().putLong(key, value).commit();
        }
    }

    public static boolean getPreferenceBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public static String getPreference(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public static String getPreference(String key, String def) {
        return mSharedPreferences.getString(key, def);
    }

    public static int getPreferenceInt(String key) {
        return mSharedPreferences.getInt(key, 0);
    }

    public static int getPreferenceInt(String key, int def) {
        return mSharedPreferences.getInt(key, def);
    }

    public static long getPreferenceLong(String key) {
        return mSharedPreferences.getLong(key, 0);
    }

    public static long getPreferenceLong(String key, long def) {
        return mSharedPreferences.getLong(key, def);
    }

    public static boolean getPreferenceBoolean(String key, boolean def) {
        return mSharedPreferences.getBoolean(key, def);
    }

    private void initEMChat() {
        if (mIsMainProcess) {
            ChatHelper.getInstance().init(getApplicationContext());
        }
    }

    private boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        String processName = getAppName(pid);
        if (processName != null && processName.equalsIgnoreCase(getPackageName())){
            mIsMainProcess = true;
        } else {
            mIsMainProcess = false;
        }
        return mIsMainProcess;
    }

    public String getAppName(int id ) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> l = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info :l) {
            try {
                if (info.pid == id) {
                    return info.processName;
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void getSwitch() {
        IHttpRequest mInitHttpRequest = GetItService.getService(IHttpRequest.class, true);
        String urlString = URLContainer.getInitURL();
        Log.d(TAG, "初始化接口请求接口======" + urlString);
        HttpIntent httpIntent = new HttpIntent(urlString);
        mInitHttpRequest.request(httpIntent, new IHttpRequest.IHttpRequestCallBack() {
            @Override
            public void onSuccess(HttpRequestManager httpRequestManager) {
                Log.d(TAG, httpRequestManager.getDataString());
                InitResult initResult = JSON.parseObject(httpRequestManager.getDataString(), InitResult.class);
            }

            @Override
            public void onFailed(String failReason) {

            }
        });

    }
}
