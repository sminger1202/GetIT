package com.android.getit.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.android.getit.GetITApp;
import com.android.getit.Utils.Utils;

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
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by sminger on 2016/1/5.
 */
public class GetITAPI {
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

    /*
    * 存储请求结果到本地。其中文件名 采用 经过 formatURL 方法处理的返回结果
    */
    public static synchronized void saveUrlCacheToLocal(final String filename,
                                                        final String content) throws Exception {
        new Thread() {
            public void run() {
                File filePath = null;
                try {
                    filePath = new File(GetITApp.URLCacheDataPath);
                    Log.d("testsaveUrlCacheToLocal", "testcache3 :"
                            + filePath.getPath());
                    if (!filePath.exists()) {
                        Log.d("testCacheData make", filePath.mkdirs() + " ");
                        Log.d("testCacheData Directory", filePath.isDirectory() + " ");
                    }
                } catch (Exception e) {

                }
                File file = new File(filePath, filename);
                OutputStream out;
                try {
                    out = new FileOutputStream(file);
                    out.write(content.getBytes("utf-8"));
                    out.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                controlUrlCacheFilesSize(file);
            }
        }.start();

    }

    /*
     * 从本地读取文件，并返回字符串。
     */
    public static String readUrlCacheFromLocal(String fileName)
            throws Exception {
        StringBuffer sb = new StringBuffer();
        File file = new File(GetITApp.URLCacheDataPath + File.separator + fileName);
        InputStreamReader isr;

        isr = new InputStreamReader(new FileInputStream(file), "utf-8");
        BufferedReader read = new BufferedReader(isr);
        // FileInputStream fis = new FileInputStream(file);
        int c;
        while ((c = read.read()) != -1) {
            sb.append((char) c);
        }
        read.close();

        return sb.toString();
    }

    private static int CACHEDATA_SIZE = 0;

    /*
     * 如果本地缓存的URL数据文件的大小大于10M的时候，按照最后的修改时间 删除 最旧的40%文件。
     * 如果返回true，提示本地缓存数据的大小大于等于10M，清除旧文件 成功 如果返回false，提示本地缓存的数据大小小于10M，不清楚旧文件
     */
    public static synchronized void controlUrlCacheFilesSize(final File file) {
        new Thread() {
            public void run() {
                File cacheFilesDir = new File(GetITApp.URLCacheDataPath);
                File[] cacheFiles = cacheFilesDir.listFiles();
                if (CACHEDATA_SIZE == 0) {
                    if (cacheFiles == null) {
                        return;
                    }

                    int dirSize = 0;
                    for (int i = 0; i < cacheFiles.length; i++) {
                        dirSize += cacheFiles[i].length();
                    }
                    Log.d("Youku", "cacheData:" + dirSize);

                    CACHEDATA_SIZE = dirSize;
                } else {
                    if (file != null) {
                        CACHEDATA_SIZE = (int) (CACHEDATA_SIZE + file.length());
                        Log.d("Youku", "cacheData after add file:"
                                + CACHEDATA_SIZE);
                    }
                }
                if (CACHEDATA_SIZE >= 1024 * 1024 * 10) {
                    int removeFactor = (int) ((0.4 * cacheFiles.length) + 1);
                    System.setProperty("java.util.Arrays.useLegacyMergeSort",
                            "true");
                    try {
                        Arrays.sort(cacheFiles, new FileLastModifSort());
                    } catch (Exception e) {
                        return;
                    }
                    for (int i = 0; i < removeFactor; i++) {
                        cacheFiles[i].delete();
                        Log.d("Youku", "remove a cacheData file");
                    }
                    CACHEDATA_SIZE = 0;
                    cacheFiles = cacheFilesDir.listFiles();
                    if (cacheFiles == null) {
                        return;
                    }
                    int dirSize = 0;
                    for (int i = 0; i < cacheFiles.length; i++) {
                        dirSize += cacheFiles[i].length();
                    }
                    Log.d("Youku", "cacheData resize :" + dirSize);
                }
            }
        }.start();

    }

    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     *         otherwise.
     */
    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * Get the external app cache directory.
     *
     * @param context
     *            The context to use
     * @return The external cache dir
     */
    @SuppressLint("NewApi")
    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName()
                + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath()
                + cacheDir);
    }

    /**
     * Check if OS version has built-in external cache dir method.
     *
     * @return
     */
    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    static class FileLastModifSort implements Comparator<File> {
        public int compare(File arg0, File arg1) {
            if (arg0 == arg1) {
                return 0;
            } else if (arg0 == null) {
                return -1;
            } else if (arg1 == null) {
                return 1;
            } else if (arg0.lastModified() > arg1.lastModified()) {
                return 1;
            } else if (arg0.lastModified() == arg1.lastModified()) {
                return 0;
            }
            return -1;
        }
    }

    /*
     * 处理请求的uri信息，移除部分变量字段
     */
    public static String formatURL(String url) {
        return formatURL(url, false);
    }

    /*
     * 处理请求的uri信息，移除部分变量字段
     */
    public static String formatURL(String url, boolean isSetCookie) {
        StringBuffer urlStr = new StringBuffer(url);
        int i = urlStr.indexOf("_t_");
        int j;
        if (i != -1) {
            // System.out.println(i);
            if ((j = urlStr.indexOf("&", i)) != -1) {
                urlStr.delete(i, j + 1);
            } else {
                urlStr.delete(i, urlStr.length());
            }
            // System.out.println(urlStr.delete(i, j + 1));
        }

        i = urlStr.indexOf("_s_");
        // System.out.println(i);
        if (i != -1) {
            if ((j = urlStr.indexOf("&", i)) != -1) {
                urlStr.delete(i, j + 1);
            } else {
                urlStr.delete(i, urlStr.length());
            }
            // System.out.println(urlStr.delete(i, j + 1));
        }
        i = urlStr.indexOf("ver");
        // System.out.println(i);
        if (i != -1) {
            if ((j = urlStr.indexOf("&", i)) != -1) {
                urlStr.delete(i, j + 1);
            } else {
                urlStr.delete(i, urlStr.length());
            }
            // System.out.println(urlStr.delete(i, j + 1));
        }
        Log.d("testcache2", url);
        i = urlStr.indexOf("network");
        // System.out.println(i);
        if (i != -1) {
            if ((j = urlStr.indexOf("&", i)) != -1) {
                urlStr.delete(i, j + 1);
            } else {
                urlStr.delete(i, urlStr.length());
            }
        }
        i = urlStr.indexOf("operator");
        // System.out.println(i);
        if (i != -1) {
            if ((j = urlStr.indexOf("&", i)) != -1) {
                urlStr.delete(i, j + 1);
            } else {
                urlStr.delete(i, urlStr.length());
            }
        }
        if (isSetCookie) {
            urlStr.append(GetITApp.COOKIE);
        }
        // System.out.println(urlStr.delete(i, j + 1));
        return Utils.md5(urlStr.toString());
    }

}
