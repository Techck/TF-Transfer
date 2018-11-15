package com.tf.transfer.util;

import com.avos.avoscloud.AVAnalytics;

/**
 * @author huangyue
 * @date 2018/11/08 21:00
 * @Description
 */
public class CustomParamManager {

    /**
     * 是否可以上传至服务器
     * @return
     */
    public static boolean canUpload() {
        try {
            return Boolean.parseBoolean(AVAnalytics.getConfigParams(UiUtils.mContext.get(), "can_upload"));
        } catch (Exception e) {
            return false;
        }
    }

}
