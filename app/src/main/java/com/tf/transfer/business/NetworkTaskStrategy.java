package com.tf.transfer.business;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.tf.transfer.database.SqliteAdapter;
import com.tf.transfer.util.DateUtil;
import com.tf.transfer.util.FileUtils;
import com.tf.transfer.util.QRCodeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author huangyue
 * @date 2018/11/16 18:29
 * @Description 准备网络任务
 */
public class NetworkTaskStrategy extends TaskPrepareStrategy {

    public NetworkTaskStrategy(Callback callback) {
        super(callback);
    }

    private int index;
    private List<Map<String, String>> fileList;
    private AVFile file;
    private ArrayList<String> idList = new ArrayList<>();

    @Override
    public void doWork(List<Map<String, String>> list) {
        fileList = list;
        index = 0;
        if (fileList.size() > 0) {
            uploadFile();
        }
    }

    private void uploadFile() {
        try {
            Map<String, String> map = fileList.get(index);
            String path = map.get("path");
            String file_name = map.get("file_name");
            String filePath = path + "/" + file_name;
            // 单个文件上传
            file = AVFile.withAbsoluteLocalPath(file_name, filePath);
            file.saveInBackground(saveCallback, progressCallback);
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) callback.failure();
        }
    }

    private SaveCallback saveCallback = new SaveCallback() {
        @Override
        public void done(AVException e) {
            if (e != null) {
                if (callback != null) callback.failure();
                return;
            }
            idList.add(file.getObjectId());
            if (++index < fileList.size()) {
                uploadFile();
            } else {
                uploadTask();
            }
        }
    };

    private ProgressCallback progressCallback = new ProgressCallback() {
        @Override
        public void done(Integer integer) {
            if (callback != null) callback.progress("第" + (index + 1) + "个文件:" + integer + "%");
        }
    };

    private void uploadTask() {
        // 上传任务记录，包含多个文件的id
        final AVObject task = new AVObject("Task");
        task.put("files", idList.toArray());
        task.saveInBackground(new SaveCallback() {

            @Override
            public void done(AVException e) {
                if (e != null) {
                    if (callback != null) callback.failure();
                    return;
                }
                String taskId = task.getObjectId();
                // 将任务保存在数据库中
                String create_time = DateUtil.getNowTimeFormat();
                SqliteAdapter adapter = SqliteAdapter.getInstance();
                ContentValues values = new ContentValues();
                values.put("create_time", create_time);
                values.put("type", 2);
                values.put("remark", taskId);
                // 生成二维码
                Bitmap qrCode = QRCodeUtil.generateQRCode(taskId);
                // 保存在本地
                String qrCodePath = FileUtils.saveQRCodeBitmapToFile(qrCode, create_time);
                values.put("qr_code", qrCodePath);
                // 添加任务
                adapter.addTask(values);
                if (callback != null) callback.success(taskId, qrCodePath);
            }

        });
    }

}
