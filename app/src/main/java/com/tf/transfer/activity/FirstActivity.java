package com.tf.transfer.activity;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.tf.transfer.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FirstActivity extends BaseActivity {

	private EditText editText;
	private int currentVersion;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		PackageInfo info;
		try {
			info = getPackageManager().getPackageInfo("com.tf.transfer", 0);
			currentVersion = info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		SharedPreferences sharedPreferences = getSharedPreferences("info", Context.MODE_PRIVATE);
		int lastVersion = sharedPreferences.getInt("version", 0);
		if (currentVersion <= lastVersion) {
			sharedPreferences.edit().putInt("version", currentVersion).apply();
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		editText = (EditText) findViewById(R.id.first_name);
		findViewById(R.id.first_next).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//设置用户名
				SharedPreferences sharedPreferences = getSharedPreferences("info", Context.MODE_PRIVATE);
				Editor edit = sharedPreferences.edit();
				edit.putString("username", editText.getText().toString());
				boolean commit = edit.commit();
				if(commit){
					edit.putInt("version", currentVersion);
					edit.commit();
					Intent intent = new Intent(FirstActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
				else{
					Toast.makeText(getApplicationContext(), "用户名不合法", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public boolean isTitleBarTextColorBlack() {
		return true;
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