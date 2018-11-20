package com.tf.transfer.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.qq.e.ads.nativ.NativeExpressADView;
import com.tf.transfer.R;
import com.tf.transfer.business.LoadAdManager;
import com.tf.transfer.util.UiUtils;

/**
 * @author huangyue
 * @date 2018/11/20 22:01
 * @Description App退出弹框
 */
public class ExitAppDialog extends DialogFragment {

    public static void show(FragmentManager fragmentManager, View.OnClickListener onClickListener) {
        ExitAppDialog dialog = new ExitAppDialog();
        dialog.setOnClickListener(onClickListener);
        dialog.show(fragmentManager, dialog.getTag());
    }

    private View.OnClickListener onClickListener;
    private ViewGroup adContainer;
    private NativeExpressADView adView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_exit_app, container);
        view.findViewById(R.id.exit_app_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        view.findViewById(R.id.exit_app_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) onClickListener.onClick(view);
            }
        });
        // 加载广告
        adContainer = view.findViewById(R.id.exit_app_ad_container);
        if (adContainer.getChildCount() > 0) {
            adContainer.removeAllViews();
        }
        adView = LoadAdManager.getAdView();
        if (adView != null) {
            adContainer.addView(adView);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        if (win == null) return;
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable( new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = win.getAttributes();
        params.width = UiUtils.dipToPx(300);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adContainer.removeAllViews();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
