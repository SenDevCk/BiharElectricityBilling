package com.nic.app.biharelectricitybilling.entity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nic.app.biharelectricitybilling.R;

import java.util.ArrayList;

public class ConsumerAdapterApl extends ArrayAdapter<dconsumer_details> {
    Context context;
    int layoutResourceId;
    String fileName;
    ArrayList<dconsumer_details> data = new ArrayList<dconsumer_details>();
    public ConsumerAdapterApl(Context context, int layoutResourceId,
                              ArrayList<dconsumer_details> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ImageHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ImageHolder();
            holder.mainlayout=(LinearLayout)row.findViewById(R.id.mainlay);
            holder.tvConsumerId = (TextView) row.findViewById(R.id.tv_con_id);
            holder.tvAcNo = (TextView) row.findViewById(R.id.tv_ac_no);
            holder.tvConName = (TextView) row.findViewById(R.id.tv_con_name);
            holder.tvAddress = (TextView) row.findViewById(R.id.tv_address);
            row.setTag(holder);
        } else {
            holder = (ImageHolder) row.getTag();
        }
        dconsumer_details mru = data.get(position);
        holder.tvConsumerId.setText(mru.getCon_id());
        holder.tvAcNo.setText(mru.getCname());
        holder.tvConName.setText(mru.getCfname());
        holder.tvAddress.setText(mru.getDisconnecteddate());
                holder.mainlayout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        holder = (ImageHolder) row.getTag();
        return row;
    }

    static class ImageHolder {

        TextView tvConsumerId, tvAcNo, tvConName, tvAddress,tvStatus;
        LinearLayout mainlayout;
    }


}


