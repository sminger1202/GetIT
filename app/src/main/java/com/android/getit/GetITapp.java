package com.android.getit;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.android.getit.EMChatModule.ChatHelper;
import com.android.getit.Utils.DeviceInfo;
import com.android.getit.Utils.ImageLoaderManager;
import com.android.getit.Utils.FrescoLoaderImage;
import com.android.getit.Utils.Profile;

import java.util.Iterator;
import java.util.List;

/**
 * Created by sminger on 2015/11/1.
 */
public class GetITApp extends MultiDexApplication  {
    public static Context context = null;
    public static String versionName;

    public static String appName;

    private static SharedPreferences mSharedPreferences;

    public static String COOKIE = null;

    public static boolean mIsMainProcess = false;
    /*
      Please do not adjust the sequence of the following init work,due to these init work maybe have some dependence.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        context = this.getApplicationContext();

        isMainProcess();

        FrescoLoaderImage.InitLoaderImage(this);

        ImageLoaderManager.initImageLoaderConfiguration(this);

        DeviceInfo.init();

        initVersionInfo();

        initPreferences();

        initEMChat();



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
}
