package com.tf.transfer.activity;

import com.avos.avoscloud.AVAnalytics;
import com.tf.transfer.R;
import com.tf.transfer.base.BaseActivity;
import com.tf.transfer.bean.TransferUser;
import com.tf.transfer.util.SPUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class FirstActivity extends BaseActivity {

	private EditText editText;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		if (!"".equals(TransferUser.getInstance().getUsername())) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		editText = findViewById(R.id.first_name);
		findViewById(R.id.first_next).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				//设置用户名;
				if(SPUtil.setUserName(editText.getText().toString())){
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