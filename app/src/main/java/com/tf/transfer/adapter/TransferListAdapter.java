package com.tf.transfer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tf.transfer.R;
import com.tf.transfer.bean.TaskRecord;
import com.tf.transfer.util.DateUtil;
import com.tf.transfer.util.ImageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangyue
 * @date 2018/11/13 20:17
 * @Description
 */
public class TransferListAdapter extends BaseAdapter {

    private List<TaskRecord> list = new ArrayList<>();
    private Context context;

    public TransferListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_transfer_list, null);
            holder.image = (ImageView) view.findViewById(R.id.item_transfer_qrCode);
            holder.time = (TextView) view.findViewById(R.id.item_transfer_time);
            holder.id = (TextView) view.findViewById(R.id.item_transfer_id);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        ImageManager.loadBitmapFromFile(list.get(i).getQrCode_path(), holder.image);
        holder.time.setText("时间: " + DateUtil.getTimeString(list.get(i).getTime()));
        holder.id.setText("ID:" + list.get(i).getId());
        return view;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setList(List<TaskRecord> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    private static class ViewHolder	{
        public ImageView image;
        public TextView time;
        public TextView id;
    }
}
