package com.tf.transfer.util;

import com.avos.avoscloud.AVAnalytics;

/**
 * @author huangyue
 * @date 2018/11/08 20:48
 * @Description
 */
public class ActionEventManager {

    /** 事件 **/
    public static final String CREATE_NATIVE_TASK = "CREATE_NATIVE_TASK";
    public static final String CREATE_NETWORK_TASK = "CREATE_NETWORK_TASK";
    public static final String RESTART_TASK = "RESTART_TASK";
    public static final String RECEIVE_FILE_BY_SCAN = "RECEIVE_FILE_BY_SCAN";
    public static final String RECEIVE_FILE_BY_VOICE = "RECEIVE_FILE_BY_VOICE";

    public static void send(String eventName) {
        AVAnalytics.onEvent(UiUtils.mContext.get(), eventName);
    }

}
