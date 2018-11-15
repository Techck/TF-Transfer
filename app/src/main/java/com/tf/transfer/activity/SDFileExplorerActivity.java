package com.tf.transfer.activity;

import java.io.File;
import java.io.IOException;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.tf.transfer.R;
import com.tf.transfer.base.BaseActivity;
import com.tf.transfer.bean.QRCode;
import com.tf.transfer.bean.TransferUser;
import com.tf.transfer.util.AppUtil;
import com.tf.transfer.util.DateUtil;
import com.tf.transfer.util.FileUtils;
import com.tf.transfer.util.QRCodeUtil;
import com.tf.transfer.database.SqliteAdapter;

public class SDFileExplorerActivity extends BaseActivity {

	public static final String TAG = "SDFileExplorerActivity";

	ListView listView;	
	boolean[] isCheck;	
	File currentParent;	
	File[] currentFiles;	
	List<Map<String, Object>> listItems = new ArrayList<>();
	ArrayList<Map<String, String>> selectItems = new ArrayList<>();
	private boolean flag = false;//是否上传至服务器
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_explorer);
		listView = (ListView) findViewById(R.id.explorer_list);
		listView.setAdapter(fileAdapter);
		File root = new File(FileUtils.BROWSE_ROOT_PATH);
		//sdcard/
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
				for(int i=0;i<isCheck.length;i++){
					if(isCheck[i]){
						Map<String, String> selectItem = new HashMap<>();
						selectItem.put("file_name", listItems.get(i).get("fileName").toString());
						try {
							selectItem.put("path", currentParent.getCanonicalPath());
						} catch (IOException e) {
							e.printStackTrace();
						}
						selectItems.add(selectItem);
					}
				}
				if(selectItems.isEmpty()){
					Toast.makeText(SDFileExplorerActivity.this, "未选择文件",	Toast.LENGTH_SHORT).show();
				}else{
					if(flag){
						//上传文件
						showProgressDialog();
						final int length = selectItems.size();
						final ArrayList<String> list_id = new ArrayList<>();
						try {
							for(int i = 0;i<length;i++){
								Map<String, String> map = selectItems.get(i);
								String path = map.get("path");
								String file_name = map.get("file_name");
								String filePath = path + "/" + file_name;

								final AVFile file = AVFile.withAbsoluteLocalPath(file_name, filePath);
								file.saveInBackground(new SaveCallback() {
									@Override
									public void done(AVException arg0) {
										list_id.add(file.getObjectId());
										if (list_id.size() == length) {
											upload(list_id);
										}
									}
								}, new ProgressCallback() {
									@Override
									public void done(Integer integer) {
										Log.d(TAG, integer.toString());
									}
								});
							}
						} catch (Exception e) {
							e.printStackTrace();
							progressDialog.dismiss();
							progressDialog = null;
						}
					}else{
						// 跳转至离线发送界面
						ArrayList<Object> list = insertLocalRecorde();
						if (list == null) {
							Toast.makeText(SDFileExplorerActivity.this, "创建任务失败", Toast.LENGTH_SHORT).show();
							return;
						}
						// 将文件添加到数据库
						insertFile(selectItems, (long) list.get(0));
						// 跳转到发送文件界面
						Intent intent = new Intent();
						intent.putExtra("flag", 1);
						intent.putExtra("id", (long) list.get(0));
						intent.putExtra("qrCode_path", (String) list.get(1));
						setResult(Activity.RESULT_OK, intent);
						finish();
					}
				}
			}
		});
		findViewById(R.id.explorer_cancel).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View source){
				finish();
			}
		});
		CheckBox checkBox = (CheckBox) findViewById(R.id.explorer_network);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				flag = isChecked;
			}
		});
	}
	
	private void upload(ArrayList list_id){
		//上传记录
		final AVObject task = new AVObject("Task");
		task.put("files", list_id.toArray());
		task.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(AVException arg0) {
				String path = insertNetworkRecorde(task.getObjectId());
				//跳转
				Intent intent = new Intent();
				intent.putExtra("qrCode_path", path);
				intent.putExtra("objectId", task.getObjectId());
				intent.putExtra("flag", 2);
				setResult(Activity.RESULT_OK, intent);
				finish();
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

    private void inflateListView(File[] files){
		listItems.clear();		
		selectItems.clear();		
		isCheck = new boolean[files.length];	
		for (int i = 0; i < files.length; i++){		
			isCheck[i]=false;			
			Map<String, Object> listItem = new HashMap<>();
			if (files[i].isDirectory())			{			
				listItem.put("icon", R.mipmap.folder_64);
			}else{			
				listItem.put("icon", R.mipmap.file_64);
			}			
			listItem.put("fileName", files[i].getName());		
			listItems.add(listItem);	
		}
		fileAdapter.notifyDataSetChanged();
	}

	BaseAdapter fileAdapter = new BaseAdapter(){
		@Override
		public int getCount(){
			return listItems.size();
		}
		@Override
		public Object getItem(int position) {
			return null;
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int icon;
			final int index = position;
			String fileName;
			ViewHolder holder;
			if(convertView == null){
				holder=new ViewHolder();
				convertView = LayoutInflater.from(SDFileExplorerActivity.this).inflate(R.layout.item_explorer_list, parent, false);
				holder.image = (ImageView)convertView.findViewById(R.id.item_explorer_list_icon);
				holder.text = (TextView)convertView.findViewById(R.id.item_explorer_list_file_name);
				holder.checkBox = (CheckBox)convertView.findViewById(R.id.item_explorer_list_check_box);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			icon = Integer.parseInt(listItems.get(position).get("icon").toString());
			holder.image.setImageResource(icon);
			fileName=listItems.get(position).get("fileName").toString();
			holder.text.setText(fileName);
			holder.checkBox.setChecked(isCheck[index]);
			if(icon == R.mipmap.folder_64){
				holder.checkBox.setVisibility(View.GONE);
			}else
				holder.checkBox.setVisibility(View.VISIBLE);
			holder.checkBox.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					isCheck[index]=!isCheck[index];
				}
			});
			return convertView;
		}
	};

	static class ViewHolder	{		
		public ImageView image;	
		public TextView text;	
		public CheckBox checkBox;	
	}
	
	//插入本地数据到数据库
	private ArrayList<Object> insertLocalRecorde(){
		String create_time = DateUtil.getNowTimeFormat();
		SqliteAdapter adapter = new SqliteAdapter(this);
		ContentValues values = new ContentValues();
		values.put("create_time", create_time);
		values.put("type", 1);
		long id = adapter.addTask(values);
		//生成qrCode
		String qrCode_path = getQRCodeCachePath(id, create_time);
		if (qrCode_path == null) {
			adapter.deleteTask(id);
			return null;
		}
		adapter.upadteQRCode(qrCode_path, id);
		ArrayList<Object> list = new ArrayList<>();
		list.add(id);
		list.add(qrCode_path);
		return list;
	}
	
	//插入网络数据到数据库
	private String insertNetworkRecorde(String objectId){
		String create_time = DateUtil.getNowTimeFormat();
		SqliteAdapter adapter = new SqliteAdapter(this);
		ContentValues values = new ContentValues();
		values.put("create_time", create_time);
		values.put("type", 2);
		values.put("remark", objectId);
		String qrcode_path = getQRCodeCachePath(objectId, create_time);
		values.put("qr_code", qrcode_path);
		adapter.addTask(values);
		return qrcode_path;
	}
	
	private String getQRCodeCachePath(String str, String time){
		//生成二维码
		Bitmap qrCode = QRCodeUtil.generateQRCode(str);
		//保存在本地
		return FileUtils.saveQRCodeBitmapToFile(qrCode, time);
	}

	//得到二维码的缓存地址
	private String getQRCodeCachePath(long id, String time){
		//获得传输者的热点名称
		String username = TransferUser.getInstance().getUsername();
		String code = AppUtil.getDeviceCode();
		Log.d("SDFileExplorerActivity", code);
        QRCode qrCode = new QRCode();
        qrCode.setId(id);
        qrCode.setUsername(username);
        qrCode.setCode(code);
		String json = JSONObject.toJSONString(qrCode);
		Log.d("SDFileExplorerActivity", "二维码内容：" + json);
		//生成二维码
		Bitmap qrCodeBitmap = QRCodeUtil.generateQRCode(json);
		//保存在本地
		return FileUtils.saveQRCodeBitmapToFile(qrCodeBitmap, time);
	}
	
    private void insertFile(ArrayList<Map<String, String>> list, long id){
    	SqliteAdapter adapter = new SqliteAdapter(this);
    	for(int i = 0;i<list.size();i++){
    		Map<String, String> map = list.get(i);
    		String path = map.get("path");
    		String file_name = map.get("file_name");
    		adapter.addTaskDetail(id, path+"/"+file_name);
    		System.out.println(path+"/"+file_name);
    	}
    }
    
    private void showProgressDialog(){
    	progressDialog = ProgressDialog.show(this, "请稍后", "正在上传……", true);
		progressDialog.setCancelable(false);
    }
    
}
