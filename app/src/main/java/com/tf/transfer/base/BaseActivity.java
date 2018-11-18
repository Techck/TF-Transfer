package com.tf.transfer.base;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.hwangjr.rxbus.RxBus;
import com.tf.transfer.R;
import com.tf.transfer.util.StatusBarUtils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author huangyue
 * @date 2018/11/08 17:33
 * @Description
 */
public abstract class BaseActivity extends BaseBannerAdActivity {

    private Queue<Runnable> resumedDo = new LinkedList<>();
    public Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSystemBarTint();
        RxBus.get().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initBlackView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
        Runnable runnable;
        while ((runnable = resumedDo.poll()) != null) {
            mHandler.post(runnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    private void initBlackView() {
        try {
            StatusBarUtils.setStatusBarHeight(findViewById(R.id.title_bar_blank_view));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置状态栏颜色
     */
    private void initSystemBarTint() {
        int lightMode = StatusBarUtils.getStatusBarLightMode(this);
        switch (lightMode) {
            case -1 :
                // 设置沉浸，字体颜色可能是白色的，需要修改状态栏颜色为透明
                StatusBarUtils.setStatusBarTranslucent(this);
                StatusBarUtils.setStatusBarColor(this, Color.parseColor("#60202020"));
                break;
            case 1 : // MIUI
                setTranslucent(StatusBarUtils.MIUI);
                break;
            case 2 : // Fly
                setTranslucent(StatusBarUtils.FLYME);
                break;
            case 3 : // android 6.0
                setTranslucent(StatusBarUtils.ANDROID_M);
                break;
        }
    }

    /**
     * 设置沉浸式
     */
    private void setTranslucent(@StatusBarUtils.SystemType int type) {
        StatusBarUtils.setStatusBarTranslucent(this);
        StatusBarUtils.setStatusBarLightMode(this, type, isTitleBarTextColorBlack());
    }

    /**
     * 状态栏文字是否黑色
     */
    public boolean isTitleBarTextColorBlack() {
        return false;
    }

    /**
     * 设置标题栏标题
     */
    public void setTitle(String title) {
        TextView textView = findViewById(R.id.title_bar_title);
        if (textView != null) {
            textView.setText(title);
        }
    }

    /**
     * 设置标题栏右侧按钮文本和点击监听器
     */
    public void setRightButton(String text, View.OnClickListener listener) {
        TextView rightBt = findViewById(R.id.title_bar_right_bt);
        rightBt.setText(text);
        if (listener != null) rightBt.setOnClickListener(listener);
    }

    /**
     * 设置标题栏返回键监听
     */
    public void setBackListener(View.OnClickListener listener) {
        ImageView back = findViewById(R.id.title_bar_back);
        if (back != null) {
            back.setOnClickListener(listener);
        }
    }

    /**
     * 添加一个resume任务
     */
    public void pullRunnable(Runnable runnable) {
        resumedDo.offer(runnable);
    }

}
