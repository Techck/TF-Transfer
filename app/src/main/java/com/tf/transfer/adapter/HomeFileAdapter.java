package com.tf.transfer.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tf.transfer.R;
import com.tf.transfer.util.FileUtils;
import com.tf.transfer.util.ImageManager;
import com.tf.transfer.util.UiUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huangyue
 * @date 2018/11/08 16:48
 * @Description
 */
public class HomeFileAdapter extends BaseAdapter {

    private List<File> list_file = new ArrayList<>();
    private int type = 0;

    @Override
    public int getCount() {
        return list_file.size();
    }

    @Override
    public Object getItem(int position) {
        return list_file.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(UiUtils.mContext.get(), R.layout.item_main_list, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        // 缩略图
        if(type == 1) ImageManager.loadBitmapFromFile(list_file.get(position).getAbsolutePath(), holder.iv_icon);
        else holder.iv_icon.setImageResource(getImage(list_file.get(position).getName()));
        // 名称
        holder.tv_name.setText(list_file.get(position).getName());
        // 空间大小
        holder.tv_size.setText(FileUtils.getFileSizeStr(list_file.get(position).length()));
        return convertView;
    }

    class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_size;

        public ViewHolder(View view) {
            iv_icon = (ImageView) view.findViewById(R.id.item_main_image);
            tv_name = (TextView) view.findViewById(R.id.item_main_name);
            tv_size = (TextView) view.findViewById(R.id.item_main_size);
            view.setTag(this);
        }
    }

    private int getImage(String fileName){
        switch (type) {
            case 2:
                String[] fileNames = fileName.split("\\.");
                if(fileNames[1].equals("doc"))
                    return R.mipmap.doc_90;
                else if(fileNames[1].equals("xls"))
                    return R.mipmap.xls_90;
                else if(fileNames[1].equals("ppt"))
                    return R.mipmap.ppt_90;
            case 3:
                return R.mipmap.vedio_90;
            case 4:
                return R.mipmap.music_90;
            case 5:
                fileNames = fileName.split("\\.");
                if (fileNames.length < 2) break;
                if(fileNames[1].equals("txt"))
                    return R.mipmap.txt_90;
                else if(fileNames[1].equals("zip") || fileNames[1].equals("rar"))
                    return R.mipmap.yasuobao_90;
        }
        return R.mipmap.weizhi_90;
    }

    public void setFiles(List<File> fileList) {
        this.list_file = fileList;
        notifyDataSetChanged();
    }

    public void setType(int type) {
        this.type = type;
    }
}
