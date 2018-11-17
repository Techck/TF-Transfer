package com.tf.transfer.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.tf.transfer.R;
import com.tf.transfer.adapter.FileExplorerAdapter;
import com.tf.transfer.base.BaseActivity;
import com.tf.transfer.bean.QRCode;
import com.tf.transfer.bean.TransferUser;
import com.tf.transfer.business.NativeTaskStrategy;
import com.tf.transfer.business.NetworkTaskStrategy;
import com.tf.transfer.business.TaskPrepareStrategy;
import com.tf.transfer.dialog.NormalDialog;
import com.tf.transfer.util.AppUtil;
import com.tf.transfer.util.DateUtil;
import com.tf.transfer.util.FileUtils;
import com.tf.transfer.util.QRCodeUtil;
import com.tf.transfer.database.SqliteAdapter;

public class SDFileExplorerActivity extends BaseActivity {

	public static final String TAG = "SDFileExplorerActivity";

	ListView listView;
	File currentParent;	
	File[] currentFiles;
	private boolean flag = false;//是否上传至服务器
	private ProgressDialog progressDialog;
	private FileExplorerAdapter fileAdapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_explorer);
		listView = findViewById(R.id.explorer_list);
		fileAdapter = new FileExplorerAdapter(this);
		listView.setAdapter(fileAdapter);
		File root = new File(FileUtils.BROWSE_ROOT_PATH);
		if (root.exists()){
			currentParent = root;
			currentFiles = root.listFiles();
			inflateListView(currentFiles);
		}
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id){
				if (currentFiles[position].isFile())
					return;
				File[] tmp = currentFiles[position].listFiles();
				if (tmp == null || tmp.length == 0){
					Toast.makeText(SDFileExplorerActivity.this, "文件夹为空或无法进入",Toast.LENGTH_SHORT).show();
				}else{
					currentParent = currentFiles[position];
					currentFiles = tmp;
					inflateListView(currentFiles);
				}
			}
		});
		findViewById(R.id.explorer_select).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				List<Map<String, String>> selectItems = fileAdapter.getSelectItems(currentParent);
				if(selectItems.isEmpty()){
					Toast.makeText(SDFileExplorerActivity.this, "未选择文件",	Toast.LENGTH_SHORT).show();
				}else{
					TaskPrepareStrategy strategy;
					if(flag){
						//上传文件
						showProgressDialog();
						strategy = new NetworkTaskStrategy(new TaskPrepareStrategy.Callback() {
							@Override
							public void success(String id, String qrCodePath) {
								setResult(2, id, qrCodePath);
							}

							@Override
							public void failure(String text) {
								progressDialog.dismiss();
								NormalDialog.show(getSupportFragmentManager(), getString(R.string.tip), text, null);
							}

							@Override
							public void progress(String text) {
								if (progressDialog != null) {
									progressDialog.setMessage("正在上传……(" + text + ")");
								}
							}
						});

					}else{
						// 处理本地任务
						strategy = new NativeTaskStrategy(new TaskPrepareStrategy.Callback() {
							@Override
							public void success(String id, String qrCodePath) {
								setResult(1, id, qrCodePath);
							}

							@Override
							public void failure(String text) {
								NormalDialog.show(getSupportFragmentManager(), getString(R.string.tip), text, null);
							}

							@Override
							public void progress(String text) {

							}
						});
					}
					strategy.doWork(fileAdapter.getSelectItems(currentParent));
				}
			}
		});
		findViewById(R.id.explorer_cancel).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View source){
				finish();
			}
		});
		CheckBox checkBox = findViewById(R.id.explorer_network);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				flag = isChecked;
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ){  
			try{					
				if (!currentParent.getCanonicalPath().equals(FileUtils.BROWSE_ROOT_PATH)){
					currentParent = currentParent.getParentFile();			
					currentFiles = currentParent.listFiles();	
					inflateListView(currentFiles);	
				}				
			}catch (Exception e){		
				e.printStackTrace();			
			}		
        }  
		return false;
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

	/**
	 * 设置返回参数
	 */
    private void setResult(int flag, String id, String qrCodePath) {
		Intent intent = new Intent();
		intent.putExtra("flag", flag);
		intent.putExtra("id", id);
		intent.putExtra("qrCodePath", qrCodePath);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

    private void inflateListView(File[] files){
		List<Map<String, Object>> list = new ArrayList<>();
		for (File file : files){
			Map<String, Object> listItem = new HashMap<>();
			if (file.isDirectory())			{
				listItem.put("icon", R.mipmap.folder_64);
			}else{			
				listItem.put("icon", R.mipmap.file_64);
			}			
			listItem.put("fileName", file.getName());
			list.add(listItem);
		}
		fileAdapter.setList(list);
	}
    
    private void showProgressDialog(){
    	if (progressDialog == null) {
			progressDialog = ProgressDialog.show(this, "请稍后", "正在上传…", true);
			progressDialog.setCancelable(false);
		} else {
    		progressDialog.show();
		}
    }
    
}
