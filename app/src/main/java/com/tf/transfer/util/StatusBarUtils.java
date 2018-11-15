package com.tf.transfer.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Pinger
 * @since 2017/4/14 0014 下午 6:00
 * 状态栏工具类
 */

public class StatusBarUtils {


    @IntDef({
            OTHER,
            MIUI,
            FLYME,
            ANDROID_M
    })
    // 注解
    @Retention(RetentionPolicy.SOURCE)
    public @interface SystemType {

    }

    public static final int OTHER = -1;
    public static final int MIUI = 1;
    public static final int FLYME = 2;
    public static final int ANDROID_M = 3;


    /**
     * 修改状态栏为全透明（沉浸）
     */
    @TargetApi(19)
    public static void setStatusBarTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     */
    public static void setStatusBarColor(Activity activity, int colorId) {
        // 只设置4.4以上的系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(colorId);
        }
    }

    /**
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public static int getStatusBarLightMode(Activity activity) {
        int result = OTHER;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 优先处理6.0以上的系统
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                result = ANDROID_M;
            } else if (MIUISetStatusBarLightMode(activity, true)) {
                result = MIUI;
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                result = FLYME;
            }
        }
        return result;
    }

    /**
     * 已知系统类型时，设置状态栏黑色字体图标。
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @param type     1:MIUUI 2:Flyme 3:android6.0
     */
    public static void setStatusBarLightMode(Activity activity, @SystemType int type, boolean isBlack) {
        if (type == MIUI) {
            MIUISetStatusBarLightMode(activity, isBlack);
        } else if (type == FLYME) {
            FlymeSetStatusBarLightMode(activity.getWindow(), isBlack);
        } else if (type == ANDROID_M) {
            AndroidMStatusBarLightMode(activity.getWindow(), isBlack);
        }
    }

    /**
     * Android 6.0以上修改状态栏字体颜色
     */
    private static void AndroidMStatusBarLightMode(Window window, boolean isBlack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = window.getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (isBlack) {
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
    }


    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param activity 用于获取window和decorView
     * @param dark     是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean MIUISetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    AndroidMStatusBarLightMode(activity.getWindow(), dark);
                }
            } catch (Exception e) {

            }
        }
        return result;
    }


    /**
     * Android系统沉浸式状态栏实现，支持4.4版本及以上的手机系统，API 19以上
     * 使用：
     * 在setContentView之后调用本代码，需要自己去掉ActionBar
     * 在布局文件中自己paddingTop状态栏的高度
     */
    public static void initStatusBar(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置状态栏字体颜色
     */
    public static void setStatusBarTextColor(Activity activity, boolean isBlack) {
        int lightMode = StatusBarUtils.getStatusBarLightMode(activity);
        switch (lightMode) {
            case MIUI:
                // MIUI
                setStatusBarLightMode(activity, MIUI, isBlack);
                break;
            case FLYME:
                // Fly
                setStatusBarLightMode(activity, FLYME, isBlack);
                break;
            case ANDROID_M:
                // android 6.0
                setStatusBarLightMode(activity, ANDROID_M, isBlack);
                break;
        }
    }


    /**
     * 设置全屏模式
     */
    public static void setFullScreenMode(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 清除全屏模式
     */
    public static void clearFullScreenMode(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static int getStatusBarHeight() {
        Context context = UiUtils.mContext.get();
        int statusBarHeight = 0;
        //获取status_bar_height资源的ID
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public static void setStatusBarHeight(View viewStateBar) {
        if (viewStateBar != null) {
            ViewGroup.LayoutParams layoutParams = viewStateBar.getLayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                layoutParams.height = getStatusBarHeight();
            } else {
                layoutParams.height = 0;
            }
            viewStateBar.setLayoutParams(layoutParams);
        }
    }
}