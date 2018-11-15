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
import android.widget.TextView;

import com.tf.transfer.R;
import com.tf.transfer.util.UiUtils;

/**
 * @author huangyue
 * @date 2018/11/14 14:12
 * @Description
 */
public class NormalDialog extends DialogFragment {

    public static void show(FragmentManager fragmentManager, String title, String content, OnClickListener onClickListener) {
        show(fragmentManager, title, content, null, onClickListener);
    }

    public static void show(FragmentManager fragmentManager, String title, String content, String secondContent, OnClickListener onClickListener) {
        NormalDialog dialog = new NormalDialog();
        dialog.setOnClickListener(onClickListener);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putString("secondContent", secondContent);
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, dialog.getTag());
    }

    private OnClickListener onClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_normal, container);
        TextView titleTv = (TextView) view.findViewById(R.id.normal_dialog_title);
        TextView contentTv = (TextView) view.findViewById(R.id.normal_dialog_content);
        TextView secondContentTv = (TextView) view.findViewById(R.id.normal_dialog_second_content);
        Bundle bundle = getArguments();
        if (bundle != null) {
            titleTv.setText(bundle.getString("title"));
            contentTv.setText(bundle.getString("content"));
            if ("".equals(bundle.getString("secondContent", ""))) {
                secondContentTv.setVisibility(View.GONE);
            } else {
                secondContentTv.setText(bundle.getString("secondContent"));
            }
        }
        view.findViewById(R.id.normal_dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(NormalDialog.this);
                } else {
                    dismiss();
                }
            }
        });
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

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(DialogFragment dialogFragment);
    }
}
