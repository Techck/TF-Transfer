package com.tf.transfer.util;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

/**
 * @author huangyue
 * @Package com.example.transfer.util
 * @date 2018/11/07 15:40
 * @Description
 */

public class ImageManager {

    public static void loadBitmap(String url, ImageView imageView) {
        Glide.with(UiUtils.mContext.get()).load(url).into(imageView);
    }

    public static void loadBitmapFromFile(String filePath, ImageView imageView) {
        if (filePath == null) return;
        loadBitmapFromFile(new File(filePath), imageView);
    }

    public static void loadBitmapFromFile(File file, ImageView imageView) {
        Glide.with(UiUtils.mContext.get()).load(file).into(imageView);
    }

}
