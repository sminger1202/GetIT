package com.android.getit;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.android.getit.Utils.DeviceInfo;
import com.android.getit.Utils.ImageLoaderManager;
import com.android.getit.Utils.FrescoLoaderImage;
import com.android.getit.Utils.Profile;

/**
 * Created by sminger on 2015/11/1.
 */
public class GetITApp extends MultiDexApplication  {
    public static Context context = null;
    public static String versionName;

    public static String appName;

    private static SharedPreferences mSharedPreferences;

    public static String COOKIE = null;// ���������ص� Cookie ��
    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();

        FrescoLoaderImage.InitLoaderImage(this);

        ImageLoaderManager.initImageLoaderConfiguration(this);

        DeviceInfo.init();

        initVersionInfo();

        initPreferences();

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

    private void initPreferences() {
//        mDownloadSharedPreferences = context.getSharedPreferences(context.getPackageName() + "_cache",
//                UIUtils.hasGingerbread() ? MODE_MULTI_PROCESS : MODE_PRIVATE);

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    }
}
