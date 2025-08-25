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

public class ConsumerAdapter1 extends ArrayAdapter<MRUDetails> {
    Context context;
    int layoutResourceId;
    String fileName;


    ArrayList<MRUDetails> data = new ArrayList<MRUDetails>();

    public ConsumerAdapter1(Context context, int layoutResourceId,
                            ArrayList<MRUDetails> data) {
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
           // holder.tvStatus = (TextView) row.findViewById(R.id.tv_con_id1);
            holder.tvAcNo = (TextView) row.findViewById(R.id.tv_ac_no);
            holder.tvConName = (TextView) row.findViewById(R.id.tv_con_name);
            holder.tvAddress = (TextView) row.findViewById(R.id.tv_address);
            row.setTag(holder);
        } else {
            holder = (ImageHolder) row.getTag();
        }
        MRUDetails mru = data.get(position);
        holder.tvConsumerId.setText(mru.get_CON_ID());
        holder.tvAcNo.setText(mru.get_ACT_NO());
        holder.tvConName.setText(mru.get_CNAME());
        holder.tvAddress.setText(mru.get_BILL_ADDRESS());
  /*      if(mru.get_isPrinted()!=null){
            if( mru.get_isPrinted().equalsIgnoreCase("Y")){
//holder.tvStatus.setText("PRINTED");
                holder.mainlayout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            }  else{
                holder.mainlayout.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                //  holder.tvStatus.setText("UNPRINTED");
            }
        }else{
            holder.mainlayout.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        }*/

        holder = (ImageHolder) row.getTag();


        return row;
    }

    static class ImageHolder {

        TextView tvConsumerId, tvAcNo, tvConName, tvAddress,tvStatus;
        LinearLayout mainlayout;
    }


}


