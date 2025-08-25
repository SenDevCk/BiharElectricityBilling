package com.nic.app.biharelectricitybilling.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.entity.Survey_Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Extends the SurveyAdapter class to RecyclerView.SurveyAdapter
// and implement the unimplemented methods
class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.ViewHolder>  {
    List<Survey_Data>  surveylist=new ArrayList<Survey_Data>();
    Context context;

    // Constructor for initialization
    public SurveyAdapter(Context context,  List<Survey_Data> survey_list) {
        this.context = context;
        this.surveylist = survey_list;
    }

    @NonNull
    @Override
    public SurveyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml
        // layout file into View object)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_row, parent, false);

        // Passing view to ViewHolder
        return new ViewHolder(view);
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull SurveyAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // TypeCast Object to int type

        holder.text_act_no.setText((String) surveylist.get(position).getACT_NO());
        holder.text_met_no.setText((String) surveylist.get(position).getMet_no());
        holder.text_name.setText((String) surveylist.get(position).getName());
        holder.text_guardian_name.setText((String) surveylist.get(position).getGuardian_name());
        holder.text_address.setText((String) surveylist.get(position).getAdrress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent surveydeatils=new Intent(context, SurveyDeatils.class);
                Bundle databundale=new Bundle();
                databundale.putString("actno",surveylist.get(position).getACT_NO());
                databundale.putString("name",surveylist.get(position).getName());
                databundale.putString("subdivid",surveylist.get(position).getSUB_DIV_ID());
                databundale.putString("meter_no",surveylist.get(position).getMet_no());
                surveydeatils.putExtra("bundle",databundale);
                context.startActivity(surveydeatils);
            }
        });
    }

    @Override
    public int getItemCount() {

        return surveylist.size();
    }
    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_act_no;
        TextView text_met_no;
        TextView text_name;
        TextView text_guardian_name;
        TextView text_address;
        public ViewHolder(View view) {
            super(view);
            text_act_no= (TextView) view.findViewById(R.id.act_no);
            text_met_no= (TextView) view.findViewById(R.id.met_no);
            text_name= (TextView) view.findViewById(R.id.cname);
            text_guardian_name= (TextView) view.findViewById(R.id.father_name);
            text_address= (TextView) view.findViewById(R.id.address);

        }
    }

}

