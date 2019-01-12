package com.tf.transfer;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVOnlineConfigureListener;
import com.hwangjr.rxbus.RxBus;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;
import com.tf.transfer.bean.TransferUser;
import com.tf.transfer.constant.RxBusTagConstant;
import com.tf.transfer.util.CustomParamManager;
import com.tf.transfer.util.SPUtil;
import com.tf.transfer.util.UiUtils;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * @author huangyue
 * @date 2018/11/07 15:43
 * @Description
 */

public class MyApplication extends Application {

    private boolean firstOnlineDataReceived = true;

    @Override
    public void onCreate() {
        super.onCreate();
        UiUtils.mContext = new WeakReference<Context>(this);
        AVOSCloud.initialize(this, BuildConfig.leanCloudApplicationId, BuildConfig.leanCloudClientKey);
        // 基础统计API
        StatService.registerActivityLifecycleCallbacks(this);
        // 设置用户名
        TransferUser.getInstance().setUsername(SPUtil.getUserName());
        // 设置自定义参数更新回调
        AVAnalytics.setOnlineConfigureListener(new AVOnlineConfigureListener() {
            @Override
            public void onDataReceived(JSONObject jsonObject) {
                if (firstOnlineDataReceived) {
                    firstOnlineDataReceived = false;
                    if (CustomParamManager.isShowAd())
                        RxBus.get().post(RxBusTagConstant.FIRST_ONLINE_CONFIG_RECEIVE, "");
                }
            }
        });
    }
}
