package com.tf.transfer.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author huangyue
 * @date 2018/11/08 17:00
 * @Description
 */
public final class SPUtil {

    private static final String SP_NAME = "info";

    static class SPConstant {
        static final String username = "username";
    }

    public static String getUserName() {
        SharedPreferences sharedPreferences = UiUtils.mContext.get().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SPConstant.username, "");
    }

    public static boolean setUserName(String newName) {
        SharedPreferences sharedPreferences = UiUtils.mContext.get().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SPConstant.username, newName);
        return editor.commit();
    }

}