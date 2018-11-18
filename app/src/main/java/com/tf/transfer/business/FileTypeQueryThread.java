package com.tf.transfer.business;

import android.os.Environment;

import com.tf.transfer.util.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * @author huangyue
 * @date 2018/11/18 00:29
 * @Description 搜索应用专门用于储存接收文件的文件夹下对应的文件类型列表
 */
public class FileTypeQueryThread extends Thread {

    private int type;
    private FileTypeQueryCallback callback;

    public FileTypeQueryThread(int type, FileTypeQueryCallback callback) {
        this.type = type;
        this.callback = callback;
    }

    @Override
    public void run() {
        super.run();
        File[] files = null;
        ArrayList<File> list = new ArrayList<>();
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File dir = new File(FileUtils.FILE_PATH);
            if(!dir.exists()){
                System.out.println(dir.mkdirs());
            }
            files = dir.listFiles();
        }
        if (files == null) {
            return;
        }
        switch (type) {
            case FileUtils.PHOTO:
                for(int i = 0;i<files.length;i++){
                    String name = files[i].getName();
                    if(FileUtils.matchFileType(name, FileUtils.photo_type)){
                        list.add(files[i]);
                    }
                }
                break;
            case FileUtils.DOCUMENT:
                for(int i = 0;i<files.length;i++){
                    String name = files[i].getName();
                    if(FileUtils.matchFileType(name, FileUtils.doc_type)){
                        list.add(files[i]);
                    }
                }
                break;
            case FileUtils.VIDEO:
                for(int i = 0;i<files.length;i++){
                    String name = files[i].getName();
                    if(FileUtils.matchFileType(name, FileUtils.video_type)){
                        list.add(files[i]);
                    }
                }
                break;
            case FileUtils.MUSIC:
                for(int i = 0;i<files.length;i++){
                    String name = files[i].getName();
                    if(FileUtils.matchFileType(name, FileUtils.music_type)){
                        list.add(files[i]);
                    }
                }
                break;
            case FileUtils.OTHER:
                for(int i = 0;i<files.length;i++){
                    String name = files[i].getName();
                    if(!FileUtils.matchFileType(name, FileUtils.photo_type)
                            && !FileUtils.matchFileType(name, FileUtils.doc_type)
                            && !FileUtils.matchFileType(name, FileUtils.video_type)
                            && !FileUtils.matchFileType(name, FileUtils.music_type)){
                        list.add(files[i]);
                    }
                }
                break;
        }
        if (callback != null) callback.onResult(list);
    }

    public interface FileTypeQueryCallback {
        void onResult(ArrayList<File> list);
    }

}
