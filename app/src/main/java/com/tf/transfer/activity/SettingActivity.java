package com.tf.transfer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tf.transfer.R;
import com.tf.transfer.bean.TransferUser;
import com.tf.transfer.util.SPUtil;

public class SettingActivity extends BaseActivity implements OnClickListener{

	public static void start(Activity activity) {
		Intent intent = new Intent(activity, SettingActivity.class);
		activity.startActivity(intent);
	}

	private EditText editText;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_setting);
		editText = (EditText) findViewById(R.id.setting_name);
		editText.setText(SPUtil.getUserName());
		findViewById(R.id.title_bar_back).setOnClickListener(this);
		setTitle("设置");
		setRightButton("完成", this);
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		switch (id) {
		case R.id.title_bar_back:
			finish();
			break;
		case R.id.title_bar_right_bt:
			//修改用户名
			boolean commit = SPUtil.setUserName(editText.getText().toString());
			if(commit){
				TransferUser.getInstance().setUsername(editText.getText().toString());
				Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
			}
			//返回
			finish();
			break;
		}
	}
	
}
