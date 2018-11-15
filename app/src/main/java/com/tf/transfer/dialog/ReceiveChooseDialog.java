package com.tf.transfer.dialog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tf.transfer.R;
import com.tf.transfer.activity.MainActivity;
import com.tf.transfer.activity.VoiceActivity;
import com.tf.transfer.ui.zxing.MipcaActivityCapture;
import com.tf.transfer.util.AppUtil;

/**
 * @author huangyue
 * @date 2018/11/07 17:09
 * @Description
 */

public class ReceiveChooseDialog extends DialogFragment implements View.OnClickListener {

    public interface OnReceiveChooseListener {
        void click(int index);
    }

    private OnReceiveChooseListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_recive, container);
        Button button_scan = (Button) view.findViewById(R.id.revice_scan);
        button_scan.setOnClickListener(this);
        Button button_voice = (Button) view.findViewById(R.id.revice_voice);
        button_voice.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (listener == null) return;
        if (id == R.id.revice_scan) {
            listener.click(0);
        } else if (id == R.id.revice_voice) {
            listener.click(1);
        }
        dismiss();
    }

    public void setListener(OnReceiveChooseListener listener) {
        this.listener = listener;
    }

}
