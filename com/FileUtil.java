package com.yys.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import com.yys.utils.excel.ExcelManager;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>文件描述：文件工具类<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2020/5/28<p>
 * <p>更新时间：2020/5/28<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class FileUtil {

    private static final String TAG = FileUtil.class.getName();

    /**
     * 获取外置卡（可拆卸的）的目录。
     * Environment.getExternalStorageDirectory()获取的目录，有可能是内置卡的。
     * 在高版本上，能访问的外置卡目录只能是/Android/data/{package}。
     */
    public static String getAppRootOfSdCardRemovable(Context context) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        /**
         * 这一句取的还是内置卡的目录。
         * /storage/emulated/0/Android/data/net.quantum6.q6telcom/cache
         * 神奇的是，加上这一句，这个可移动卡就能访问了。
         * 猜测是相当于执行了某种初始化动作。
         */
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                if ((Boolean) isRemovable.invoke(storageVolumeElement)) {
                    return path;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 外设的总大小
     */
    public static long totalExternalStorageRemovable(Context context) {
        String externalSdCardRemovablePath = getAppRootOfSdCardRemovable(context);
        if (!TextUtils.isEmpty(externalSdCardRemovablePath)) {
            return new File(externalSdCardRemovablePath).getTotalSpace();
        }
        return 0;
    }

    /**
     * 外设SDCARD的可使用大小
     * */
    public static long usableExternalStorageRemovable(Context context){
        String externalSdCardRemovablePath = getAppRootOfSdCardRemovable(context);
        if (!TextUtils.isEmpty(externalSdCardRemovablePath)) {
            return new File(externalSdCardRemovablePath).getUsableSpace();
        }
        return 0;
    }

    /**
     * 获取到内外置的所有缓存文件夹路径
     * **/
    public static File[] getExternalCaches(Context context){
        File[] files = context.getExternalCacheDirs();//如果有2个文件，一般第一个是内置，第二个外置
        return files;
    }



}
