package com.tf.transfer.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.tf.transfer.R;
import com.tf.transfer.activity.SendFileActivity;
import com.tf.transfer.adapter.TransferListAdapter;
import com.tf.transfer.base.BaseFragment;
import com.tf.transfer.bean.TaskRecord;
import com.tf.transfer.constant.RxBusTagConstant;
import com.tf.transfer.dialog.QRCodeDialog;
import com.tf.transfer.util.SocketThreadFactory;
import com.tf.transfer.database.SqliteAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author huangyue
 * @date 2018/11/07 20:57
 * @Description
 */

public class SubTransferListFragment extends BaseFragment {

    public static SubTransferListFragment create(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        SubTransferListFragment fragment = new SubTransferListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private List<TaskRecord> list = new ArrayList<>();
    private TransferListAdapter adapter;
    private int type = 0;   // 0当前  1历史

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        type = getArguments().getInt("type", -1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_list, container, false);
        ListView listView = (ListView) view.findViewById(R.id.transfer_list_view);
        listView.setAdapter(adapter = new TransferListAdapter(getContext()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                TaskRecord record = list.get(arg2);
                if (record.getType() == 2) {
                    QRCodeDialog.show(getFragmentManager(), record.getRemark(), record.getQrCode_path());
                } else {
                    SendFileActivity.start(getActivity(), record.getId(), record.getQrCode_path(), false);
                }
            }

        });
        loadData();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void loadData() {
        if (type == 0) {
            list = getCurrentList();
        } else {
            list = getHistoryList();
        }
        adapter.setList(list);
    }

    //获取历史记录列表
    private ArrayList<TaskRecord> getHistoryList(){
        ArrayList<TaskRecord> list = new ArrayList<>();
        SqliteAdapter adapter = new SqliteAdapter(getActivity());
        Cursor cursor = adapter.getTask();
        while(cursor.moveToNext()){
            list.add(new TaskRecord(cursor.getLong(cursor.getColumnIndex("_id")),
                    cursor.getString(cursor.getColumnIndex("qr_code")),
                    cursor.getString(cursor.getColumnIndex("create_time")),
                    cursor.getInt(cursor.getColumnIndex("type")),
                    cursor.getString(cursor.getColumnIndex("remark"))));
        }
        return list;
    }

    //获取当前记录列表
    private ArrayList<TaskRecord> getCurrentList(){
        SqliteAdapter adapter = new SqliteAdapter(getActivity());
        ArrayList<Map<String, Object>> list_path = SocketThreadFactory.getCurrentTaskList();
        ArrayList<TaskRecord> list_task = new ArrayList<>();
        for(int i = 0;i<list_path.size();i++){
            Map<String, Object> map = list_path.get(i);
            long id = (long) map.get("id");
            Cursor cursor = adapter.getTask(id);
            while(cursor.moveToNext()){
                list_task.add(new TaskRecord(cursor.getLong(cursor.getColumnIndex("_id")),
                        cursor.getString(cursor.getColumnIndex("qr_code")),
                        cursor.getString(cursor.getColumnIndex("create_time")),
                        cursor.getInt(cursor.getColumnIndex("type")),""));
            }
        }
        return list_task;
    }


    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTagConstant.CHANGE_TRANSFER_LIST)})
    public void transferListChanged(String temp) {
        loadData();
    }

}
