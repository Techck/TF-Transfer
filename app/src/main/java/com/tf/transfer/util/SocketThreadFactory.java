package com.tf.transfer.util;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class SocketThreadFactory {

	private static final String TAG = "SocketThreadFactory";

	// 储存发送任务，包含文件路径列表
	private static ArrayList<Map<String, Object>> list_task = new ArrayList<>();
	// 储存开启的服务器
	private static ArrayList<MyServerThread> list_client = new ArrayList<>();
	private static ServerSocketThread socketThread;

	/**
	 * 添加任务
	 */
	public static void addTask(Map<String, Object> map) throws IOException{
		if(socketThread == null){
			socketThread = new ServerSocketThread(9494);
			new Thread(socketThread).start();
			Log.d(TAG, "=============启动服务器=============");
		}
		list_task.add(map);
		Log.d(TAG, "++++++++++++添加任务++++++++++++");
	}

	/**
	 * 停止/删除任务
	 */
	public static void deleteTask(long id){
		for(int i = 0;i<list_task.size();i++){
			Map<String, Object> map = list_task.get(i);
			if((long) map.get("id") == id){
				list_task.remove(i);
			}
		}
		if(list_task.size() == 0){
			try {
				socketThread.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			socketThread = null;
		}
	}

	/**
	 * 返回该任务id的任务列表
	 */
	public static ArrayList<String> getTaskList(long id){
		Map<String, Object> map;
		for(int i = 0;i<list_task.size();i++){
			map = list_task.get(i);
			// 得到任务id
			long task_id = (long) map.get("id");
			// 匹配id
			if(task_id == id){
				return (ArrayList<String>) map.get("files");
			}
		}
		return new ArrayList<>();
	}

	/**
	 * 添加连接
	 */
	public static void addClient(MyServerThread client){
		list_client.add(client);
	}

	/**
	 * 删除连接
	 */
	public static void deleteClient(MyServerThread client){
		list_client.remove(client);
	}

	/**
	 * 返回当前正在进行的任务列表
	 */
	public static ArrayList<Map<String, Object>> getCurrentTaskList(){
		return list_task;
	}

	/**
	 * 判断是否存在这个id的任务
	 */
	public static boolean isExist(long id) {
		ArrayList<Map<String, Object>> list = getCurrentTaskList();
		for(int i = 0;i<list.size();i++){
			Map<String, Object> map = list.get(i);
			if((long)map.get("id") == id){
				return true;
			}
		}
		return false;
	}
	
}
