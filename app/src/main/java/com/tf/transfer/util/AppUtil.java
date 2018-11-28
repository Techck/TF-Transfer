package com.tf.transfer.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.tf.transfer.BuildConfig;

/**
 * @author huangyue
 * @date 2018/11/12 20:32
 * @Description
 */
public class AppUtil {

    private static final String TAG = "AppUtil";

    public static String getDeviceCode() {
        try {
            Context context = UiUtils.mContext.get();
            return Settings.Secure.getString(context.getContentResolver(), "android_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检查权限
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (context == null) {
            Log.e(TAG, "checkPermission方法中的 Context 字段为空");
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 检查权限
     * @param context
     * @param permissions
     * @return
     */
    public static boolean checkPermission(Context context, String[] permissions) {
        if (context == null) {
            Log.e(TAG, "checkPermission方法中的 Context 字段为空");
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                    return false;
                }
            }
        } else {
            PackageManager pm = context.getPackageManager();
            for (String permission : permissions) {
                if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_DENIED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取渠道名
     */
    public static String getChannel(Context context) {
        if (context == null) return "";
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String channel = appInfo.metaData.getString("leancloud");
            if (channel == null) return "";
            return channel;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取版本号
     */
    public static String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

}
