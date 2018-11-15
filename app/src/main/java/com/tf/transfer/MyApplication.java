package com.tf.transfer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.tf.transfer.bean.TransferUser;
import com.tf.transfer.util.FileUtils;
import com.tf.transfer.util.SPUtil;
import com.tf.transfer.util.UiUtils;

import java.lang.ref.WeakReference;

/**
 * @author huangyue
 * @Package com.example.transfer
 * @date 2018/11/07 15:43
 * @Description
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UiUtils.mContext = new WeakReference<Context>(this);
        AVOSCloud.initialize(this, BuildConfig.leanCloudApplicationId, BuildConfig.leanCloudClientKey);
        //开启统计SDK
        AVAnalytics.enableCrashReport(this, true);
        //设置用户名
        TransferUser.getInstance().setUsername(SPUtil.getUserName());
    }
}
