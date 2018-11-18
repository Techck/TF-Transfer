package com.tf.transfer.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Locale;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.tf.transfer.business.FileTypeQueryThread;

public class FileUtils {

	public static String MAIN_FILE_PATH = Environment.getExternalStorageDirectory() + "/Transfer";
	public static String FILE_PATH = MAIN_FILE_PATH + "/files";
	// 浏览文件的根目录
	public static String BROWSE_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

	public static final int PHOTO = 1;
	public static final int DOCUMENT = 2;
	public static final int VIDEO = 3;
	public static final int MUSIC = 4;
	public static final int OTHER = 5;
	
	public static final String[] photo_type = {"png", "jpg", "jpeg"};
	public static final String[] doc_type = {"doc", "xls", "ppt", "docx", "xlsx", "pptx"};
	public static final String[] video_type = {"avi", "mp4", "mov", "wmv", "3gp", "flv"};
	public static final String[] music_type = {"mp3", "wma"};
	private static final String[][] MIME_MapTable={ 
            //{后缀名，MIME类型} 
            {".3gp",    "video/3gpp"}, 
            {".apk",    "application/vnd.android.package-archive"}, 
            {".asf",    "video/x-ms-asf"}, 
            {".avi",    "video/x-msvideo"}, 
            {".bin",    "application/octet-stream"}, 
            {".bmp",    "image/bmp"}, 
            {".c",  "text/plain"}, 
            {".class",  "application/octet-stream"}, 
            {".conf",   "text/plain"}, 
            {".cpp",    "text/plain"}, 
            {".doc",    "application/msword"}, 
            {".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"}, 
            {".xls",    "application/vnd.ms-excel"},  
            {".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}, 
            {".exe",    "application/octet-stream"}, 
            {".gif",    "image/gif"}, 
            {".gtar",   "application/x-gtar"}, 
            {".gz", "application/x-gzip"}, 
            {".h",  "text/plain"}, 
            {".htm",    "text/html"}, 
            {".html",   "text/html"}, 
            {".jar",    "application/java-archive"}, 
            {".java",   "text/plain"}, 
            {".jpeg",   "image/jpeg"}, 
            {".jpg",    "image/jpeg"}, 
            {".js", "application/x-javascript"}, 
            {".log",    "text/plain"}, 
            {".m3u",    "audio/x-mpegurl"}, 
            {".m4a",    "audio/mp4a-latm"}, 
            {".m4b",    "audio/mp4a-latm"}, 
            {".m4p",    "audio/mp4a-latm"}, 
            {".m4u",    "video/vnd.mpegurl"}, 
            {".m4v",    "video/x-m4v"},  
            {".mov",    "video/quicktime"}, 
            {".mp2",    "audio/x-mpeg"}, 
            {".mp3",    "audio/x-mpeg"}, 
            {".mp4",    "video/mp4"}, 
            {".mpc",    "application/vnd.mpohun.certificate"},        
            {".mpe",    "video/mpeg"},   
            {".mpeg",   "video/mpeg"},   
            {".mpg",    "video/mpeg"},   
            {".mpg4",   "video/mp4"},    
            {".mpga",   "audio/mpeg"}, 
            {".msg",    "application/vnd.ms-outlook"}, 
            {".ogg",    "audio/ogg"}, 
            {".pdf",    "application/pdf"}, 
            {".png",    "image/png"}, 
            {".pps",    "application/vnd.ms-powerpoint"}, 
            {".ppt",    "application/vnd.ms-powerpoint"}, 
            {".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"}, 
            {".prop",   "text/plain"}, 
            {".rc", "text/plain"}, 
            {".rmvb",   "audio/x-pn-realaudio"}, 
            {".rtf",    "application/rtf"}, 
            {".sh", "text/plain"}, 
            {".tar",    "application/x-tar"},    
            {".tgz",    "application/x-compressed"},  
            {".txt",    "text/plain"}, 
            {".wav",    "audio/x-wav"}, 
            {".wma",    "audio/x-ms-wma"}, 
            {".wmv",    "audio/x-ms-wmv"}, 
            {".wps",    "application/vnd.ms-works"}, 
            {".xml",    "text/plain"}, 
            {".z",  "application/x-compress"}, 
            {".zip",    "application/x-zip-compressed"}, 
            {"",        "*/*"}   
    };
	
	public static void getFiles(int type, FileTypeQueryThread.FileTypeQueryCallback callback){
		new FileTypeQueryThread(type, callback).start();
	}

	//匹配文件类型
	public static boolean matchFileType(String fileName, String[] fileTypes){
		String fileNames[] = fileName.split("\\.");
		if (fileNames.length < 2) {
			return false;
		}
		String fileType = fileNames[fileNames.length - 1];
		for(int i = 0;i<fileTypes.length;i++){
			if(fileTypes[i].equals(fileType.toLowerCase(Locale.getDefault()))){
				return true;
			}
		}
		return false;
	}
	
	public static String nameFile(String fileName){
		int count = 0;
		File file = new File(fileName);
		String oldName = file.getName();
		while(file.exists()){
			count++;
			String name = oldName;
			String[] names = name.split("\\.");
			name = names[0] + "("+count+")" + "." + names[1];
			file = new File(file.getParent() +"/"+name);
		}
		System.out.println(file.getAbsolutePath());
		return file.getAbsolutePath();
	}
	
	public static void openFile(Activity activity, File file){
		Intent intent = new Intent(); 
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	    //设置intent的Action属性 
	    intent.setAction(Intent.ACTION_VIEW); 
	    //获取文件file的MIME类型 
	    String type = getMIMEType(file); 
	    //设置intent的data和Type属性。 
	    intent.setDataAndType(Uri.fromFile(file), type); 
	    //跳转 
	    activity.startActivity(intent); 
	}
	
	private static String getMIMEType(File file) { 
	    String type = "*/*";
	    String fName = file.getName(); 
	    // 获取后缀名前的分隔符"."在fName中的位置。
	    int dotIndex = fName.lastIndexOf("."); 
	    if(dotIndex < 0){ 
	        return type; 
	    } 
	    // 获取文件的后缀名
	    String end = fName.substring(dotIndex,fName.length()).toLowerCase();
	    if(end.equals("")) return type;
	    // 在MIME和文件类型的匹配表中找到对应的MIME类型。
	    for(int i=0;i<MIME_MapTable.length;i++){
	        if(end.equals(MIME_MapTable[i][0])) 
	            type = MIME_MapTable[i][1]; 
	    }        
	    return type; 
	}

	//获得目录下的文件名数组
	public static String[] getFilesNames(){
		File dir = new File(MAIN_FILE_PATH + "/files");
		if(!dir.exists()){
			Log.d("FileUtils", "创建目录[" + dir.getAbsolutePath() + "]" + dir.mkdirs());
		}
		String[] result = dir.list();
		if (result == null) return new String[0];
		return result;
	}

	public static boolean deleteFile(File file) {
		// 路径为文件且不为空则进行删除
		return file.isFile() && file.exists() && file.delete();
	}

	public static boolean modifyFileName(File oldFile, String newsName) {
		File newFile = new File(oldFile.getParent()+"/" + newsName);
		if(!newFile.exists()){
			if(oldFile.renameTo(newFile)){
				return true;
			}else{
				Toast.makeText(UiUtils.mContext.get(), "修改文件名失败", Toast.LENGTH_SHORT).show();
				return false;
			}
		}else{
			Toast.makeText(UiUtils.mContext.get(), "文件名已存在", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	public static String saveQRCodeBitmapToFile(Bitmap bitmap, String name) {
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File dir = new File(MAIN_FILE_PATH + "/qrCode/");
			if(!dir.exists()){
				Log.d("FileUtils", "创建目录[" + dir.getAbsolutePath() + "]" + dir.mkdirs());
			}
			File file = new File(dir, name + ".jpg");
			try {
				if (!AppUtil.checkPermission(UiUtils.mContext.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
					return null;
				if(file.createNewFile()){
					FileOutputStream fos = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
					fos.close();
					Log.d("FileUtil", "二维码已保存[" + file.getAbsolutePath() + "]");
				}
				return file.getAbsolutePath();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public static final DecimalFormat twoDigitAfterDecimalPoint = new DecimalFormat("#.##");

    /**
     * 获取文件大小的格式化字符串
     * @param fileLength 单位字节
     */
    public static String getFileSizeStr(long fileLength) {
        if (fileLength < 1024) {
            return String.valueOf(fileLength) + "B";
        }

        double kb = fileLength / 1024d;
        if (kb < 1024) {
            return twoDigitAfterDecimalPoint.format(kb) + "KB";
        }

        double mb = kb / 1024;
        if (mb < 1024) {
            return twoDigitAfterDecimalPoint.format(kb) + "MB";
        }

        double gb = mb / 1024;
        if (gb < 1024) {
            return twoDigitAfterDecimalPoint.format(kb) + "GB";
        }

        double tb = gb / 1024;
        if (tb < 1024) {
            return twoDigitAfterDecimalPoint.format(kb) + "TB";
        }
        return "未知大小";
    }

}
