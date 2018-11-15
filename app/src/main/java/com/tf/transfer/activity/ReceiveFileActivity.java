package com.tf.transfer.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tf.transfer.R;
import com.tf.transfer.service.ReceiveFileFromNetworkService;
import com.tf.transfer.service.ReceiveFileService;
import com.tf.transfer.ui.wave.WaveView;

public class ReceiveFileActivity extends BaseActivity implements OnClickListener{

	public static void start(Activity activity, long id, String wifi_name, String code, String ip) {
		Intent intent = new Intent(activity, ReceiveFileActivity.class);
		intent.putExtra("id", id);
		intent.putExtra("wifi_name", wifi_name);
		intent.putExtra("code", code);
		intent.putExtra("ip", ip);
		activity.startActivity(intent);
	}

	public static void start(Activity activity, String taskId) {
		Intent intent = new Intent(activity, ReceiveFileActivity.class);
		intent.putExtra("task_id", taskId);
		activity.startActivity(intent);
	}

	private TextView text_speed, text_process, text_progress;
	private WaveView waveView;
	private long id;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive);
		setTitle("接收文件");
		waveView = (WaveView) findViewById(R.id.receive_wave_view);
		findViewById(R.id.title_bar_back).setOnClickListener(this);
		text_speed = (TextView) findViewById(R.id.receive_speed);
		text_process = (TextView) findViewById(R.id.receive_process);
		text_progress = (TextView) findViewById(R.id.receive_progress);
		id = getIntent().getLongExtra("id", 0);
		if(id == 0){
			//网络
			String task_id = getIntent().getStringExtra("task_id");
			Intent intent = new Intent(this, ReceiveFileFromNetworkService.class);
			intent.putExtra("task_id", task_id);
			ReceiveFileFromNetworkService.activity = this;
			startService(intent);
		}else{
			//本地
			String ip = getIntent().getStringExtra("ip");
			String wifi_name = getIntent().getStringExtra("wifi_name");
			String code = getIntent().getStringExtra("code");
			Intent intent = new Intent(this, ReceiveFileService.class);
			intent.putExtra("id", id);
			intent.putExtra("wifi_name", wifi_name);
			intent.putExtra("code", code);
			intent.putExtra("ip", ip);
			ReceiveFileService.activity = this;
			startService(intent);
		}
		showDialog(true);
	}
	
	@Override
	public void onClick(View arg0) {
		finish();
	}
	
	public void setWaveProgress(int progress){
		waveView.setProgress(progress);
		text_progress.setText("已接收"+progress+"%");
	}

	public void setSpeed(String speed) {
		text_speed.setText(speed);
	}

	public void setProcess(String process) {
		text_process.setText(process);
	}
	
	public void showToast(String text){
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}
	
	public void showDialog(boolean flag){
		if(flag){
			progressDialog = ProgressDialog.show(this, "请稍后", "正在连接……", true);
			progressDialog.setCancelable(true);
		}else{
			if(progressDialog.isShowing()){
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ReceiveFileService.activity = null;
		ReceiveFileFromNetworkService.activity = null;
	}
	
}