package com.tf.transfer.database;

import java.util.ArrayList;

import com.tf.transfer.bean.NoFinishTask;
import com.tf.transfer.util.UiUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqliteAdapter {
	
	private SqliteOpenHelper helper;
	private static SqliteAdapter mInstance;

	public static SqliteAdapter getInstance() {
		if (mInstance == null) {
			synchronized(SqliteAdapter.class) {
				if (mInstance == null) {
					mInstance = new SqliteAdapter(UiUtils.mContext.get());
				}
			}
		}
		return mInstance;
	}

	private SqliteAdapter(Context context) {
		super();
		this.helper = new SqliteOpenHelper(context);
	}
	
	public long addTask(ContentValues values){
		SQLiteDatabase database = helper.getWritableDatabase();
		return database.insert("task", null, values);
	}

	public void deleteTask(long id) {
		SQLiteDatabase database = helper.getWritableDatabase();
		database.delete("task", "_id = ?" , new String[]{String.valueOf(id)});
	}
	
	public void addTaskDetail(long trackId, String path){
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("file_path", path);
		values.put("task_id", Integer.parseInt(trackId+""));
		database.insert("task_detail", null, values);
	}
	
	public void upadteQRCode(String qrCode, long id){
		SQLiteDatabase database = helper.getWritableDatabase();
		String sql = "update task set qr_code = ? where _id = ?";
		database.execSQL(sql, new Object[]{qrCode,Integer.parseInt(id+"")});
	}
	
	public Cursor getTask(){
		SQLiteDatabase database = helper.getWritableDatabase();
		String sql = "select * from task";
		return database.rawQuery(sql, null);
	}
	
	public Cursor getTask(long id){
		SQLiteDatabase database = helper.getWritableDatabase();
		String sql = "select * from task where _id = ?";
		return database.rawQuery(sql, new String[]{id+""});
	}
	
	public ArrayList<String> getTaskDetail(long id){
		SQLiteDatabase database = helper.getWritableDatabase();
		String sql_detail = "select file_path from task_detail where task_id = ?";
		ArrayList<String> list = new ArrayList<>();
		Cursor cursor_detail = database.rawQuery(sql_detail, new String[]{id+""});
		while(cursor_detail.moveToNext()){
			String file_path = cursor_detail.getString(cursor_detail.getColumnIndex("file_path"));
			list.add(file_path);
		}
		return list;
	}
	
	public void addNoFinishTask(NoFinishTask noFinishTask){
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("task_id", noFinishTask.getId());
		values.put("file", noFinishTask.getStart_file());
		values.put("length", noFinishTask.getStart_length());
		values.put("code", noFinishTask.getCode());
		database.insert("no_finish_task", null, values);
	}
	
	public NoFinishTask getNoFinishTask(String code, long id){
		NoFinishTask noFinishTask = null;
		SQLiteDatabase database = helper.getWritableDatabase();
		String sql_detail = "select * from no_finish_task where task_id = ? and code = ?";
		System.out.println("2     "+code);
		Cursor cursor = database.rawQuery(sql_detail, new String[]{id+"", code});
		long _id = 0;
		while(cursor.moveToNext()){
			noFinishTask = new NoFinishTask();
			noFinishTask.setId(cursor.getLong(cursor.getColumnIndex("task_id")));
			noFinishTask.setStart_file(cursor.getInt(cursor.getColumnIndex("file")));
			noFinishTask.setStart_length(cursor.getInt(cursor.getColumnIndex("length")));
			noFinishTask.setCode(cursor.getString(cursor.getColumnIndex("code")));
			_id = cursor.getLong(cursor.getColumnIndex("_id"));
		}
		if(_id != 0)
			database.delete("no_finish_task", "_id = ?", new String[]{_id+""});
		return noFinishTask;
	}
	
}
