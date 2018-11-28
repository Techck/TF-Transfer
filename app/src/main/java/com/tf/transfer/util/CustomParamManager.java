package com.tf.transfer.util;

import com.avos.avoscloud.AVAnalytics;
import com.tf.transfer.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author huangyue
 * @date 2018/11/08 21:00
 * @Description
 */
public class CustomParamManager {

    private static Boolean isShowAd;

    /**
     * 文件是否可以上传至服务器
     */
    public static boolean canUpload() {
        try {
            return Boolean.parseBoolean(AVAnalytics.getConfigParams(UiUtils.mContext.get(), "can_upload"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 是否展示广告
     * 根据屏蔽规则判断
     */
    public static boolean isShowAd() {
        if (isShowAd != null) return isShowAd;
        // 根据屏蔽的渠道和版本号来判断
        String adShieldParamStr = AVAnalytics.getConfigParams(UiUtils.mContext.get(), "adShieldParam");
        if (adShieldParamStr == null || adShieldParamStr.isEmpty()) {
            return false;
        }
        try {
            JSONObject adShieldParam = new JSONObject(adShieldParamStr);
            // 获取需要屏蔽的渠道
            JSONArray channels = adShieldParam.getJSONArray("channel");
            String channel = AppUtil.getChannel(UiUtils.mContext.get());
            // 获取需要屏蔽的版本
            JSONArray versions = adShieldParam.getJSONArray("version");
            String version = AppUtil.getVersionName();
            boolean flagVersion = false;
            for (int i = 0;i<versions.length();i++) {
                if (version.equals(versions.optString(i))) {
                    // 如果版本需要屏蔽则遍历渠道是否需要屏蔽
                    for (int j = 0;j<channels.length();j++) {
                        if (channel.equals(channels.optString(j))) {
                            return isShowAd = false;
                        }
                    }
                    flagVersion = true;
                }
                if (flagVersion) break;
            }
            return isShowAd = true;
        } catch (Exception e) {
            e.printStackTrace();
            return isShowAd = false;
        }
    }

}
