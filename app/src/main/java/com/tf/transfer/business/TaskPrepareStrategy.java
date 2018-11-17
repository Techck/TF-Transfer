package com.tf.transfer.business;

import java.util.List;
import java.util.Map;

/**
 * @author huangyue
 * @date 2018/11/16 18:04
 * @Description 任务准备策略
 */
public abstract class TaskPrepareStrategy {

    Callback callback;

    TaskPrepareStrategy(Callback callback) {
        this.callback = callback;
    }

    public abstract void doWork(List<Map<String, String>> list);

    public interface Callback {
        void success(String id, String qrCodePath);
        void failure(String text);
        void progress(String text);
    }
}