package com.nic.app.biharelectricitybilling.SessionManager;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.nic.app.biharelectricitybilling.R;

import java.util.ArrayList;


public class StatisticsAdapter extends BaseAdapter {

    public ArrayList<BlueToothBean> list;
    public Context context;

    public void init(Context context) {
        this.context = context;
    }

    public void setList(ArrayList<BlueToothBean> list){
        this.list = list;
    }

    // 当前选中项下标
    private int currentIndex = -1;

    int userNum=0;

    BlueToothBean sb;

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return null == list ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder = null;

        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_layout, null);

            holder.num = (TextView) convertView.findViewById(R.id.btName);
            holder.date = (TextView) convertView.findViewById(R.id.btMac);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        userNum++;

        sb=list.get(position);

        holder.num.setText(sb.getBtName());
        holder.date.setText(sb.getBTMac());

        return convertView;
    }

    public static class ViewHolder {
        private TextView num;
        private TextView date;
    }

}
