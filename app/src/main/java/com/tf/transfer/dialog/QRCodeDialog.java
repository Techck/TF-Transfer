package com.tf.transfer.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tf.transfer.R;
import com.tf.transfer.util.ImageManager;

/**
 * @author huangyue
 * @Package com.tf.transfer.dialog
 * @date 2018/11/07 20:40
 * @Description
 */

public class QRCodeDialog extends DialogFragment {

    public static void show(FragmentManager fragmentManager, String objectId, String qrCode) {
        QRCodeDialog dialog = new QRCodeDialog();
        Bundle bundle = new Bundle();
        bundle.putString("objectId", objectId);
        bundle.putString("qrCode", qrCode);
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, dialog.getTag());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_qrcode, container);
        TextView textView = (TextView) view.findViewById(R.id.qrcode_text);
        textView.setText(getArguments().getString("objectId"));
        ImageView imageView = (ImageView) view.findViewById(R.id.qrcode_image);
        ImageManager.loadBitmapFromFile(getArguments().getString("qrCode"), imageView);
        return view;
    }

}
