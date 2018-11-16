package com.tf.transfer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.tf.transfer.R;
import com.tf.transfer.base.BaseActivity;
import com.tf.transfer.bean.TransferUser;
import com.tf.transfer.constant.RxBusTagConstant;
import com.tf.transfer.database.SqliteAdapter;
import com.tf.transfer.dialog.NormalDialog;
import com.tf.transfer.ui.RippleLayout;
import com.tf.transfer.util.AppUtil;
import com.tf.transfer.util.ImageManager;
import com.tf.transfer.util.SocketThreadFactory;
import com.tf.transfer.util.VoiceUtils;
import com.tf.transfer.util.WifiAdmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangyue
 * @date 2018/11/07 21:07
 * @Description
 */

public class SendFileActivity extends BaseActivity implements View.OnClickListener{

    /**
     *
     * @param flag 发送方式：声波/二维码
     */
    public static void start(Activity context, long id, String qrCode_path, boolean flag) {
        Intent intent = new Intent(context, SendFileActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("qrCode_path", qrCode_path);
        intent.putExtra("flag", flag);
        context.startActivity(intent);
    }

    private static final String TAG = "SendFileActivity";

    private Button button_qrcode, button_voice;
    private TextView textView_id, textView_title;
    private ViewGroup layout;
    private View voiceView;
    private ImageView qrCodeView;
    private long task_id;
    private String qrCode_path;
    private boolean flag = false; // false二维码  true声波
    private VoiceUtils voiceUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_send_file);
        layout = (ViewGroup) findViewById(R.id.send_file_qrcode);
        button_qrcode = (Button) findViewById(R.id.send_qrcode);
        button_voice = (Button) findViewById(R.id.send_voice);
        textView_id = (TextView) findViewById(R.id.send_file_id);
        textView_title = (TextView) findViewById(R.id.send_file_title);
        findViewById(R.id.send_file_cancel).setOnClickListener(this);
        findViewById(R.id.send_file_min).setOnClickListener(this);
        button_qrcode.setOnClickListener(this);
        button_voice.setOnClickListener(this);
        Intent intent = getIntent();
        task_id = intent.getLongExtra("id", 0);
        qrCode_path = intent.getStringExtra("qrCode_path");
        //显示ID
        textView_id.setText("ID:"+task_id);
        if (flag)   // 声波
            setVoice();
        else {      // 二维码
            setQrcode();
            start();
        }
        voiceUtils = new VoiceUtils(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.send_file_cancel:
                flag = false;
                voiceUtils.stopSend();
                SocketThreadFactory.deleteTask(task_id);
                sendTaskChangeBroadcast();
                finish();
                break;
            case R.id.send_file_min:
                flag = false;
                voiceUtils.stopSend();
                finish();
                break;
            case R.id.send_qrcode:
                voiceUtils.stopSend();
                setQrcode();
                break;
            case R.id.send_voice:
                setVoice();
                break;
        }
    }

    //设置二维码
    public void setQrcode(){
        flag = false;
        button_qrcode.setBackgroundResource(R.drawable.corners_bg_button_left);
        button_voice.setBackgroundResource(R.drawable.corners_bg_button_right_no);
        button_qrcode.setTextColor(0xffffffff);
        button_voice.setTextColor(getResources().getColor(R.color.theme_color));
        textView_title.setText(R.string.send_file_qrcode_tip);
        if (qrCodeView == null) {
            qrCodeView = new ImageView(this);
            ImageManager.loadBitmapFromFile(qrCode_path, qrCodeView);
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.removeAllViews();
        layout.addView(qrCodeView, params);
    }

    //设置声波
    public void setVoice(){
        flag = true;
        button_qrcode.setBackgroundResource(R.drawable.corners_bg_button_left_no);
        button_voice.setBackgroundResource(R.drawable.corners_bg_button_right);
        button_qrcode.setTextColor(getResources().getColor(R.color.theme_color));
        button_voice.setTextColor(0xffffffff);
        textView_title.setText(R.string.send_file_voice_tip);
        if (voiceView == null) {
            voiceView = LayoutInflater.from(this).inflate(R.layout.ripple_layout, null);
            RippleLayout rippleLayout = (RippleLayout) voiceView.findViewById(R.id.ripple_layout);
            rippleLayout.startRippleAnimation();
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.removeAllViews();
        layout.addView(voiceView, params);

        //开始发送声波
        StringBuilder sb = new StringBuilder();
        sb.append(AppUtil.getDeviceCode());
        sb.append("@");
        sb.append(TransferUser.getInstance().getUsername());
        sb.append("@");
        sb.append(task_id);
        final String str = sb.toString();
        new Thread(){
            @Override
            public void run() {
                while(flag){
                    voiceUtils.sendVoice(str);
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void sendTaskChangeBroadcast() {
        RxBus.get().post(RxBusTagConstant.CHANGE_TRANSFER_LIST, "");
    }

    /**
     * 启动任务逻辑
     */
    private void start() {
        //根据flag判断是否存在任务
        boolean flag = SocketThreadFactory.isExist(task_id);
        if(!flag){
            Log.d(TAG, "即将开始一个任务");
            sendFile(task_id);
            sendTaskChangeBroadcast();
        }
    }

    /**
     * 添加任务
     */
    public void sendFile(long id){
        //开启Wifi
        boolean is = WifiAdmin.getInstance().setHotspot(true);
        Log.d(TAG, "WIFI热点是否开启成功:" + is);
        if(is){
            try {
                //得到文件列表
                SqliteAdapter adapter = SqliteAdapter.getInstance();
                ArrayList<String> list = adapter.getTaskDetail(id);
                //将文件列表添加到map中
                Map<String, Object> map = new HashMap<>();
                map.put("id", task_id);
                map.put("files", list);
                SocketThreadFactory.addTask(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (voiceUtils != null)
            voiceUtils.stopSend();
        flag = false;
    }

    @Override
    public boolean isTitleBarTextColorBlack() {
        return true;
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = @Tag(RxBusTagConstant.WRITE_SETTING_FAIL))
    public void writeSettingPermissionFailure(String temp) {
        NormalDialog.show(getSupportFragmentManager(), getString(R.string.permission_tip_title)
                , getString(R.string.permission_tip_content), getString(R.string.system_setting)
                , new NormalDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogFragment dialogFragment) {
                        dialogFragment.dismiss();
                        finish();
                    }
                });
    }
}
