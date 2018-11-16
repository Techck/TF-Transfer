package com.tf.transfer.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.tf.transfer.BuildConfig;
import com.tf.transfer.R;
import com.tf.transfer.adapter.HomeFileAdapter;
import com.tf.transfer.base.BaseActivity;
import com.tf.transfer.constant.PermissionConstant;
import com.tf.transfer.dialog.NormalDialog;
import com.tf.transfer.dialog.QRCodeDialog;
import com.tf.transfer.dialog.ReceiveChooseDialog;
import com.tf.transfer.ui.swipemenulistview.SwipeMenu;
import com.tf.transfer.ui.swipemenulistview.SwipeMenuCreator;
import com.tf.transfer.ui.swipemenulistview.SwipeMenuItem;
import com.tf.transfer.ui.swipemenulistview.SwipeMenuListView;
import com.tf.transfer.ui.zxing.MipcaActivityCapture;
import com.tf.transfer.util.AppUtil;
import com.tf.transfer.util.FileUtils;
import com.tf.transfer.util.UiUtils;
import com.tf.transfer.util.WifiAdmin;

public class MainActivity extends BaseActivity implements View.OnClickListener {

	static {
        System.loadLibrary("sinvoice");
    }

    private static final String TAG = "MainActivity";

	private Button[] fileTypeButtons = new Button[5];
	private AutoCompleteTextView acTextView;
	private DrawerLayout drawerLayout;
	private ViewGroup adContainer;
	private BannerView bannerView;
	private ArrayList<File> list_file = new ArrayList<>();
	private HomeFileAdapter adapter;
	private ReceiveChooseDialog dialog;
	private int type = 1;//照片1  文档2  视频3  音乐4  其他5

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initCategoryBar();
		initSwipeListView();
		initSlidingMenu();

		adContainer = findViewById(R.id.ad_container);

		acTextView = findViewById(R.id.main_search);
		acTextView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, FileUtils.getFilesNames()));
		acTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				FileUtils.openFile(MainActivity.this, new File(FileUtils.MAIN_FILE_PATH + "/" + acTextView.getText().toString()));
				acTextView.setText("");
			}

		});
		findViewById(R.id.main_transfer).setOnClickListener(this);
		findViewById(R.id.main_setting).setOnClickListener(this);
		findViewById(R.id.main_transfer_list).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkAndRequestPermission();
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		switch (id) {
			case R.id.main_transfer:
				drawerLayout.openDrawer(Gravity.END);
				break;
			case R.id.main_transfer_list:
				TransferListActivity.start(this);
				break;
			case R.id.main_setting:
				SettingActivity.start(this);
				break;
			case R.id.main_photo:
				type = 1;
				list_file = FileUtils.getFiles(FileUtils.PHOTO);
				adapter.setType(type);
				adapter.setFiles(list_file);
				setButtonColor();
				break;
			case R.id.main_doc:
				type = 2;
				list_file = FileUtils.getFiles(FileUtils.DOCUMENT);
				adapter.setType(type);
				adapter.setFiles(list_file);
				setButtonColor();
				break;
			case R.id.main_vedio:
				type = 3;
				list_file = FileUtils.getFiles(FileUtils.VEDIO);
				adapter.setType(type);
				adapter.setFiles(list_file);
				setButtonColor();
				break;
			case R.id.main_music:
				type = 4;
				list_file = FileUtils.getFiles(FileUtils.MUSIC);
				adapter.setType(type);
				adapter.setFiles(list_file);
				setButtonColor();
				break;
			case R.id.main_other:
				type = 5;
				list_file = FileUtils.getFiles(FileUtils.OTHER);
				adapter.setType(type);
				adapter.setFiles(list_file);
				setButtonColor();
				break;
		}
	}

	private void initBannerAd() {
		if (bannerView != null) return;
		bannerView = new BannerView(this, ADSize.BANNER, BuildConfig.GDTAdIdAppId, BuildConfig.GDTAdIdhomeBanner);
        bannerView.setRefresh(30);
		bannerView.setShowClose(true);
        bannerView.setADListener(new AbstractBannerADListener() {

			@Override
			public void onNoAD(AdError error) {
			}

			@Override
			public void onADReceiv() {
			}

			@Override
			public void onADClosed() {
				super.onADClosed();
				adContainer.removeAllViews();
				bannerView.destroy();
				bannerView = null;
			}

		});
		adContainer.addView(bannerView);
		bannerView.loadAD();
	}

	private void initSlidingMenu() {
		drawerLayout = findViewById(R.id.drawerLayout);
		findViewById(R.id.send_file).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "send");
				drawerLayout.closeDrawers();
				// 检查权限
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
				if (AppUtil.checkPermission(UiUtils.mContext.get(), permissions)) {
					jumpSDFileBrowse();
				} else {
					ActivityCompat.requestPermissions(MainActivity.this, permissions, PermissionConstant.PERMISSION_REQUEST_CODE_FOR_FILE);
				}
			}
		});
		findViewById(R.id.receive_file).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "receive");
				showReceiveChooseDialog();
			}
		});
	}

	private void initSwipeListView() {
		SwipeMenuListView mListView = findViewById(R.id.main_listview);
		mListView.setMenuCreator(new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				int width = UiUtils.dipToPx(50);

				SwipeMenuItem openItem = new SwipeMenuItem(MainActivity.this);
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
				openItem.setWidth(width);
				openItem.setTitle("改名");
				openItem.setTitleSize(18);
				openItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(openItem);

				SwipeMenuItem deleteItem = new SwipeMenuItem(MainActivity.this);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				deleteItem.setWidth(width);
				deleteItem.setTitle("删除");
				deleteItem.setTitleSize(18);
				deleteItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(deleteItem);
			}
		});
		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
					case 0:
						// 改名
						modifyName(position);
						break;
					case 1:
						// 删除
						if(FileUtils.deleteFile(list_file.get(position))){
							list_file.remove(position);
							adapter.notifyDataSetChanged();
						}else
							Toast.makeText(MainActivity.this, "文件删除失败", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		});
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				FileUtils.openFile(MainActivity.this, list_file.get(arg2));
			}
		});
		adapter = new HomeFileAdapter();
		mListView.setAdapter(adapter);
	}

	private void initCategoryBar() {
		fileTypeButtons[0] = findViewById(R.id.main_photo);
		fileTypeButtons[1] = findViewById(R.id.main_doc);
		fileTypeButtons[2] = findViewById(R.id.main_vedio);
		fileTypeButtons[3] = findViewById(R.id.main_music);
		fileTypeButtons[4] = findViewById(R.id.main_other);
		fileTypeButtons[0].setOnClickListener(this);
		fileTypeButtons[1].setOnClickListener(this);
		fileTypeButtons[2].setOnClickListener(this);
		fileTypeButtons[3].setOnClickListener(this);
		fileTypeButtons[4].setOnClickListener(this);
	}

	private void setButtonColor(){
		for (int i = 0;i<fileTypeButtons.length;i++) {
			if (type - 1 == i) {
				fileTypeButtons[i].setTextColor(getResources().getColor(R.color.theme_color));
			} else {
				fileTypeButtons[i].setTextColor(getResources().getColor(R.color.title_color));
			}
		}
	}

	//修改文件名称
	protected void modifyName(int position) {
		final File file = list_file.get(position);
		final EditText edit = new EditText(this);
		edit.setText(file.getName());
		edit.setFocusable(true);
		edit.setInputType(InputType.TYPE_CLASS_TEXT);
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setView(edit);
		ad.setTitle("请输入新的文件名:");
		ad.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (FileUtils.modifyFileName(file, edit.getText().toString())) {
					getFileList();
					adapter.setFiles(list_file);
				}
			}
		});
		ad.create();
		ad.show();
	}

	private void jumpSDFileBrowse() {
		startActivityForResult(new Intent(MainActivity.this, SDFileExplorerActivity.class), 3);
	}

	private void jumpScanActivity() {
		Intent intent = new Intent(MainActivity.this, MipcaActivityCapture.class);
		startActivityForResult(intent, 1);
	}

	private void jumpRecordAudioActivity() {
        Intent intent = new Intent(MainActivity.this, VoiceActivity.class);
        startActivityForResult(intent, 2);
    }

	/**
	 * 二维码展示框
	 */
	private void showQRCodeDialog(String objectId, String qrCode){
		QRCodeDialog dialog = new QRCodeDialog();
		Bundle bundle = new Bundle();
		bundle.putString("objectId", objectId);
		bundle.putString("qrCode", qrCode);
		dialog.setArguments(bundle);
		dialog.show(getSupportFragmentManager(), "");
	}

	/**
	 * 显示接收文件的方式对话框
	 */
	private void showReceiveChooseDialog() {
		drawerLayout.closeDrawers();
		//打开接收文件的提示框
		if (dialog == null) {
			dialog = new ReceiveChooseDialog();
			dialog.setListener(new ReceiveChooseDialog.OnReceiveChooseListener() {
				@Override
				public void click(int index) {
					if (index == 0) {
						// 扫描二维码
						// 检查权限
						if (AppUtil.checkPermission(MainActivity.this, Manifest.permission.CAMERA)) {
							jumpScanActivity();
						} else {
							ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PermissionConstant.PERMISSION_REQUEST_CODE_FOR_CAMERA);
						}
					} else {
						// 识别声波
						// 权限检查
						if (AppUtil.checkPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)) {
							jumpRecordAudioActivity();
						} else {
							ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, PermissionConstant.PERMISSION_REQUEST_CODE_FOR_RECORD_AUDIO);
						}

					}
				}
			});
		}
		dialog.show(getSupportFragmentManager(), "");
	}

	/**
	 * 根据当前type获取文件列表
	 */
	private void getFileList(){
		list_file = FileUtils.getFiles(type);
	}

	/**
	 * 展示申请权限失败对话框
	 */
	private void showPermissionDialog(final String permission) {
		pullRunnable(new Runnable() {
			@Override
			public void run() {
				NormalDialog.show(getSupportFragmentManager(), getString(R.string.permission_tip_title)
						, getString(R.string.permission_tip_content), permission, null);
			}
		});
	}

	/**
	 * 检查加载广点通广告的权限
	 */
	@TargetApi(Build.VERSION_CODES.M)
	private void checkAndRequestPermission() {
		List<String> lackedPermission = new ArrayList<>();
		if (!AppUtil.checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
			lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
		}

		if (!AppUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}

		if (!AppUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
			lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
		}

		// 权限都已经有了，那么直接调用SDK
		if (lackedPermission.size() == 0) {
			initBannerAd();
		} else {
			String[] requestPermissions = new String[lackedPermission.size()];
			lackedPermission.toArray(requestPermissions);
			requestPermissions(requestPermissions, PermissionConstant.PERMISSION_REQUEST_CODE_FOR_GDT_AD);
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
			case 1:	// 扫描二维码
				if (resultCode == RESULT_OK) {
                    if (data == null) return;
					Bundle bundle = data.getExtras();
					if (bundle == null) return;
					String json = bundle.getString("result");
					if (json == null) return;
					char c = json.charAt(0);
					if(c != '{'){
						//根据objectId 从服务器下载文件
						ReceiveFileActivity.start(MainActivity.this, json);
						return;
					}
					// 启动接收文件界面
					try {
						JSONObject object = new JSONObject(json);
						long id = object.getLong("id");
						String code = object.getString("code");
						String username = object.getString("username");
						String ip = object.getString("ip");
						Log.d("MainActivity", id+"  "+code+"  "+username+"  "+ip);
						ReceiveFileActivity.start(MainActivity.this, id, username, code, ip);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case 2:	// 识别声波
				if(resultCode == RESULT_OK){
					String str = data.getStringExtra("result");
					Log.d("MainActivity", "识别到的内容："+str);
					String[] strs = str.split("@");
					if(strs.length == 3){
						String code = strs[0];
						String username = strs[1];
						long id = Long.parseLong(strs[2]);
						String ip = "";
						Log.d("MainActivity", id+"  "+code+"  "+username+"  "+ip);
						ReceiveFileActivity.start(MainActivity.this, id, username, code, ip);
					}else{
						Toast.makeText(getApplicationContext(), "周围杂音太重识别异常，请重试！", Toast.LENGTH_SHORT).show();
					}
				}
				break;
        	case 3: // 勾选文件
                if(resultCode == RESULT_OK) {
                    if (data == null) return;
                    int flag = data.getIntExtra("flag", 0);
                    if (flag == 1) {
                        SendFileActivity.start(MainActivity.this, Long.parseLong(data.getStringExtra("id")), data.getStringExtra("qrCodePath"), false);
                    } else if (flag == 2) {
                        showQRCodeDialog(data.getStringExtra("id"), data.getStringExtra("qrCodePath"));
                    }
                }
				break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionConstant.PERMISSION_REQUEST_CODE_FOR_FILE == requestCode) {
            for (int result : grantResults) {
                if (PackageManager.PERMISSION_GRANTED != result) {
					showPermissionDialog(getString(R.string.read_write_phone_storage));
                    return;
                }
            }
			jumpSDFileBrowse();
        } else if (PermissionConstant.PERMISSION_REQUEST_CODE_FOR_CAMERA == requestCode) {
        	if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				jumpScanActivity();
			} else {
				showPermissionDialog(getString(R.string.camera));
			}
		} else if (PermissionConstant.PERMISSION_REQUEST_CODE_FOR_RECORD_AUDIO == requestCode) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                jumpRecordAudioActivity();
            } else {
				showPermissionDialog(getString(R.string.record_audio));
            }
        } else if (PermissionConstant.PERMISSION_REQUEST_CODE_FOR_GDT_AD == requestCode) {
			for (int result : grantResults) {
				if (PackageManager.PERMISSION_GRANTED != result) {
					// 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
					Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					intent.setData(Uri.parse("package:" + getPackageName()));
					startActivity(intent);
					return;
				}
			}
			initBannerAd();
		}
    }

    @Override
	protected void onDestroy() {
		super.onDestroy();
		WifiAdmin.getInstance().closeWifiAp();
//		Intent intent = new Intent(Intent.ACTION_MAIN);
//		intent.addCategory(Intent.CATEGORY_HOME);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent);
		System.exit(0);
	}

}