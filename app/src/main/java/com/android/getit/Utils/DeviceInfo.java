package com.android.getit.Utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.android.getit.GetITApp;

import java.util.UUID;

public class DeviceInfo {
    /** �豸 ������ */
    public static String MANUFACTURER = "";
    /** �豸Ʒ�� */
    public static String BRAND = "";
    /** �豸�ͺ� */
    public static String MODEL = "";
    /** ����ϵͳ�汾 */
    public static String OS = "";
    /** ��Ļ�?���أ� */
    public static int WIDTH = 0;
    /** ��Ļ�� �����أ� */
    public static int HEIGHT = 0;
    /** ��Ļ�ܶ� */
    public static float DENSITY;
    /** ��Ļ�ܶ�DPI */
    public static float DENSITYDPI;
    /** �豸���� */
    public static String IMEI = "";
    /** SIM���� */
    public static String IMSI = "";
    /** ��Ӫ�� */
    public static String OPERATOR = "";
    /** mac��ַ */
    public static String MAC = "";
    /** UUID */
    public static String UUID = "";
    /** GUID */
    public static String GUID = "";
    /** �ֻ���� */
    public static String MOBILE = "";
    /** CPU��Ϣ */
    public static String CPU = "";
    /** ��ǰ������Ϣ */
    public static String NETWORKINFO = "";
    /** ��ǰ�������� for example "WIFI" or "MOBILE". */
    public static String NETWORKTYPE = "";
    /** ���ڴ� */
    public static String MEM_TOTAL = "";
    /** �����ڴ� */
    public static String MEM_FREE = "";
    /** ROM���� (KB) */
    public static String ROM_TOTAL = "";
    /** ROM����(KB) */
    public static String ROM_FREE = "";
    /** SD����(KB) */
    public static String SDCARD_TOTAL = "";
    /** SD����(KB) */
    public static String SDCARD_FREE = "";
    /** DEVICEID */
    public static String DEVICEID;

    // public static String OS_VER;
    /** ����ʱ�� */
    public static String ACTIVE_TIME = "";

    public static String HOST_IP="";

    private DeviceInfo() {// ����
    }

    public static void init(){
        Context context = GetITApp.context ;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        DeviceInfo.DEVICEID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//        DeviceInfo.ACTIVE_TIME = Youku.getPreference("active_time");
        DeviceInfo.MOBILE = Utils.URLEncoder(tm.getLine1Number());
        DeviceInfo.IMEI = Utils.URLEncoder(tm.getDeviceId());
//		DeviceInfo.IMSI = Util.URLEncoder(tm.getSimSerialNumber());
        DeviceInfo.IMSI = "";
        if (!TextUtils.isEmpty(tm.getSimOperatorName()) || !TextUtils.isEmpty(tm.getSimOperator())) {
            DeviceInfo.OPERATOR = Utils.URLEncoder(tm.getSimOperatorName() + "_" + tm.getSimOperator());
        } else {
            DeviceInfo.OPERATOR = "";
        }

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi.getConnectionInfo().getMacAddress() != null) {
            DeviceInfo.MAC = wifi.getConnectionInfo().getMacAddress();
        }

        if (!DeviceInfo.IMEI.trim().equals("") || !DeviceInfo.MAC.trim().equals("")) {
            // �����ȡ���������м�����һֵ������Ҫ��UUID

        } else {
            DeviceInfo.UUID = getUUID();
        }

        DisplayMetrics dm ;
        dm = context.getResources().getDisplayMetrics();
        DeviceInfo.WIDTH = dm.widthPixels;
        DeviceInfo.HEIGHT = dm.heightPixels;
        DeviceInfo.DENSITY = dm.density;
        DeviceInfo.DENSITYDPI = dm.densityDpi;

    }

    private static String getUUID() {
        try {
            final String tmDevice, tmSerial, androidId;
            TelephonyManager tm = (TelephonyManager) GetITApp.context.getSystemService(Context.TELEPHONY_SERVICE);
            tmDevice = tm.getDeviceId();
            tmSerial = tm.getSimSerialNumber();
            androidId = android.provider.Settings.Secure.getString(GetITApp.context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            java.util.UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            return deviceUuid.toString();
        } catch (Exception e) {
            return java.util.UUID.randomUUID().toString();
        }
    }
}