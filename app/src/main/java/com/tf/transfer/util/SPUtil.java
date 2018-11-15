package com.tf.transfer.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author huangyue
 * @date 2018/11/08 17:00
 * @Description
 */
public class SPUtil {

    private static final String SP_NAME = "info";

    public static String getUserName() {
        SharedPreferences sharedPreferences = UiUtils.mContext.get().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("username", "");
    }

    public static boolean setUserName(String newName) {
        SharedPreferences sharedPreferences = UiUtils.mContext.get().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", newName);
        return editor.commit();
    }

}