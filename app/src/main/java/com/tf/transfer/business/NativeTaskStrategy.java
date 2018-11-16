package com.tf.transfer.business;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.tf.transfer.bean.QRCode;
import com.tf.transfer.bean.TransferUser;
import com.tf.transfer.database.SqliteAdapter;
import com.tf.transfer.util.AppUtil;
import com.tf.transfer.util.DateUtil;
import com.tf.transfer.util.FileUtils;
import com.tf.transfer.util.QRCodeUtil;

import java.util.List;
import java.util.Map;

/**
 * @author huangyue
 * @date 2018/11/16 18:09
 * @Description 准备本地任务
 */
public class NativeTaskStrategy extends TaskPrepareStrategy {

    public NativeTaskStrategy(TaskPrepareStrategy.Callback callback) {
        super(callback);
    }

    @Override
    public void doWork(List<Map<String, String>> list) {
        String create_time = DateUtil.getNowTimeFormat();
        SqliteAdapter adapter = SqliteAdapter.getInstance();
        ContentValues values = new ContentValues();
        values.put("create_time", create_time);
        values.put("type", 1);
        long id = adapter.addTask(values);
        // 生成qrCode
        String qrCode_path = getQRCodeCachePath(id, create_time);
        if (qrCode_path == null) {
            adapter.deleteTask(id);
            if (callback != null) callback.failure();
            return;
        }
        // 更新二维码到数据库中
        adapter.upadteQRCode(qrCode_path, id);
        // 将文件添加到数据库
        insertFile(list, id);
        if (callback != null) callback.success(String.valueOf(id), qrCode_path);
    }

    /**
     * 得到二维码的缓存地址
     */
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

    /**
     * 将文件路径保存到数据库中
     */
    private void insertFile(List<Map<String, String>> list, long id){
        SqliteAdapter adapter = SqliteAdapter.getInstance();
        for(int i = 0;i<list.size();i++){
            Map<String, String> map = list.get(i);
            String path = map.get("path");
            String file_name = map.get("file_name");
            adapter.addTaskDetail(id, path + "/" + file_name);
        }
    }

}
