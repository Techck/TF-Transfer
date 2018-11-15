package com.tf.transfer.activity;

import java.lang.ref.WeakReference;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tf.transfer.R;
import com.avos.avoscloud.AVAnalytics;
import com.tf.transfer.ui.voiceline.VoiceLineView;
import com.tf.transfer.util.VoiceUtils;
import com.tf.transfer.util.VoiceUtils.VoiceListener;

public class VoiceActivity extends BaseActivity implements Runnable{
	
	static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
	
    private VoiceLineView voiceLineView;
    private VoiceUtils voiceUtils;
    private boolean flag = true;
    private int count = 0;
	private MyHandler handler = new MyHandler(this);
    
    private static class MyHandler extends Handler {

    	private WeakReference<VoiceActivity> activity;

		public MyHandler(VoiceActivity activity) {
			this.activity = new WeakReference<>(activity);
		}

		@Override
        public void handleMessage(Message msg) {
			activity.get().voiceLineView.setVolume(new Random().nextInt(20)+30);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        setTitle("声波识别");
        setBackListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				finish();
				flag = false;
			}

		});
        voiceLineView = (VoiceLineView) findViewById(R.id.voice_line);
        
        //识别声波
        voiceUtils = new VoiceUtils(this);
        voiceUtils.receiveVoice(new VoiceListener() {
			
			@Override
			public void receiveVoice(String str) {
				System.out.println(str);
				Intent intent = new Intent();
				intent.putExtra("result", str);
				setResult(RESULT_OK, intent);
				flag = false;
				finish();
			}
		});

        Thread thread = new Thread(this);
        thread.start();
    }

	@Override
	public void run() {
		while(flag){
            handler.sendEmptyMessage(0);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            	
            }
		}
		//耗时操作
		voiceUtils.stopRecive();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		flag = false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		AVAnalytics.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		AVAnalytics.onPause(this);
	}

}
