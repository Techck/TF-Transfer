package com.tf.transfer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tf.transfer.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangyue
 * @date 2018/11/16 17:36
 * @Description
 */
public class FileExplorerAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, Object>> listItems = new ArrayList<>();
    private List<Map<String, String>> selectItems = new ArrayList<>();
    private boolean[] isCheck;

    public FileExplorerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount(){
        return listItems.size();
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int icon;
        final int index = position;
        String fileName;
        ViewHolder holder;
        if(convertView == null){
            holder=new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_explorer_list, parent, false);
            holder.image = convertView.findViewById(R.id.item_explorer_list_icon);
            holder.text = convertView.findViewById(R.id.item_explorer_list_file_name);
            holder.checkBox = convertView.findViewById(R.id.item_explorer_list_check_box);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        icon = Integer.parseInt(listItems.get(position).get("icon").toString());
        holder.image.setImageResource(icon);
        fileName=listItems.get(position).get("fileName").toString();
        holder.text.setText(fileName);
        holder.checkBox.setChecked(isCheck[index]);
        if(icon == R.mipmap.folder_64){
            holder.checkBox.setVisibility(View.GONE);
        }else
            holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isCheck[index]=!isCheck[index];
            }
        });
        return convertView;
    }

    public void setList(List<Map<String, Object>> list) {
        selectItems.clear();
        isCheck = new boolean[list.size()];
        this.listItems = list;
        notifyDataSetChanged();
    }

    public List<Map<String, String>> getSelectItems(File parentPath) {
        if (selectItems.isEmpty()) {
            for (int i = 0; i < isCheck.length; i++) {
                if (isCheck[i]) {
                    Map<String, String> selectItem = new HashMap<>();
                    selectItem.put("file_name", listItems.get(i).get("fileName").toString());
                    try {
                        selectItem.put("path", parentPath.getCanonicalPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    selectItems.add(selectItem);
                }
            }
        }
        return selectItems;
    }

    private static class ViewHolder	{
        ImageView image;
        TextView text;
        CheckBox checkBox;
    }

}
