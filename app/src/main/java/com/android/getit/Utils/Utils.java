package com.android.getit.Utils;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.getit.GetITApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by sminger on 2015/11/1.
 */
public class Utils {
    public static final int KB = 1024;
    public static final int MB = 1048576;
    public static final int REGISTER = 100;

    /**
     * 获取通知栏handler消息的间隔时间 和 message.wath
     */
    private static final int GET_STATUS_BAR_HEIGHT = 200; // 超清：0

    public static int HTTP_CONNECT_TIMEOUT_3G = 3000;//HTTP连接超时时间-3G

    public static int HTTP_READ_TIMEOUT_3G = 8000;//HTTP读取超时时间-3G

    public static int HTTP_CONNECT_TIMEOUT_WIFI = 3000;//HTTP连接超时时间-WIFI

    public static int HTTP_READ_TIMEOUT_WIFI = 8000;//HTTP读取超时时间-WIFI

    /**
     * TODO �ж�����״̬�Ƿ����
     *
     * @return true: ������� ; false: ���粻����
     */
    public static boolean hasInternet() {
        ConnectivityManager m = (ConnectivityManager) GetITApp.context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (m == null) {
            Log.d("NetWorkState", "Unavailabel");
            return false;
        } else {
            try {
                NetworkInfo[] info = m.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            Log.d("NetWorkState", "Availabel");
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
        return false;
    }

    /**
     * Get the memory class of this device (approx. per-app memory limit)
     *
     * @param context
     * @return
     */
    public static int getMemoryClass(Context context) {
        return ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    }

    /** ���MD5�� */
    public static String md5(final String s) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    /**
     * ��url����utf-8ת��
     *
     * @param s
     *            �ַ���
     * @return
     */
    public static String URLEncoder(String s) {
        if (TextUtils.isEmpty(s))
            return "";
        try {
            s = URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (NullPointerException e) {
            return "";
        }
        return s;
    }

    /**
     * �������У����GUID
     *
     * @return
     */
    public static final String getGUID() {
        return md5(DeviceInfo.MAC + "&" + DeviceInfo.IMEI + "&" + "&");
    }

    public static String getNetworkType() {
        ConnectivityManager m = (ConnectivityManager) GetITApp.context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo n = m.getActiveNetworkInfo();
        if (n == null) {
            return "";
        }
        int type = n.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {// is WIFI
            return "WIFI";
        } else if (type == ConnectivityManager.TYPE_MOBILE) {// is MOBILE
            TelephonyManager tm = (TelephonyManager) GetITApp.context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return String.valueOf(tm.getNetworkType());
        } else {
            return "OTHER";
        }
    }

    public static void DebugShow(Context context, String str) {
        if(Profile.Debug) {
            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * @return 是否是wifi网络
     */
    public static boolean isWifi() {
        ConnectivityManager m = (ConnectivityManager) GetITApp.context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo n = m.getActiveNetworkInfo();
        if (n != null && n.getType() == ConnectivityManager.TYPE_WIFI)
            return true;
        return false;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * 获取设备屏幕尺寸 4.7 5.0 5.5
     * @param height 屏幕长
     * @param wight	屏幕宽
     * @param dpi 屏幕密度
     * @return
     */
    public static String getDeviceSize(int height, int wight, float dpi){
        DecimalFormat df = new DecimalFormat ("#.0");
        return df.format(Math.sqrt(Math.pow(height, 2) + Math.pow(wight, 2)) / dpi);
    }




    /**
     * 将字符串通过';'和'='分割成map，切割cookie
     * @param str	字符串样式的cookie
     * @return	格式化后的map
     */
    public static HashMap<String, String> strFormatMap(String str){
        HashMap<String, String> map = new HashMap<String, String>();
        String[] data = str.split(";");
        for(String item: data){
            String[] formatValue = item.split("=");
            String key, value;
            key = formatValue[0];
            if(formatValue.length == 1)
                value = "";
            else
                value = formatValue[1];
            map.put(key, value);
        }
        return map;
    }

    /**
     * 将map数据，拼接成字符串形式
     * @param map	map形式的cookie
     * @return	拼接后的字符串
     */
    public static String mapFormatStr(HashMap<String, String> map){
        StringBuilder sb = new StringBuilder();
        Set<String> set = map.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            sb.append(key).append("=").append(map.get(key)).append(";");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     *	根据新旧cookie，生成一个最新的cookie
     * @param newCookie
     * @param oldCookie
     * @return
     */
    public static String formatCookie(String newCookie, String oldCookie){

        if(TextUtils.isEmpty(newCookie))
            return oldCookie;

        if(TextUtils.isEmpty(oldCookie))
            return newCookie;

        HashMap<String, String> newCookieMap = strFormatMap(newCookie);
        HashMap<String, String> oldCookieMap = strFormatMap(oldCookie);

        Set<String> set = newCookieMap.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            oldCookieMap.put(key, newCookieMap.get(key));
        }
        String cookie = mapFormatStr(oldCookieMap);
        return cookie;
    }

    /** 获取系统换行符 */
    public final static String LINE_SEPARATOR = System
            .getProperty("line.separator");

    /**
     * 把输入流转化为字符串 1
     *
     * @param is
     *            输入流
     * @return
     */
    public static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                is));
        final StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append(LINE_SEPARATOR);
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {

            }
        }
        return sb.toString();
    }
}
