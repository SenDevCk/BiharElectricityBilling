package com.nic.app.biharelectricitybilling.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.entity.Report;
import com.nic.app.biharelectricitybilling.util.CommonPref;

import java.io.IOException;
import java.util.ArrayList;


public class UntracedActivity extends Activity {

    private ActionBar actionBar;
    DataBaseHelper localDBHelper;
    TextView mruName,noOfRecords, totalCon,billed,Untraced,Traced,accountNo,conName,address,fathername;
    TableLayout table,table2;
    HorizontalScrollView horizontalScrollView1,horizontalScrollView2;

    ArrayList<Report> ReportList = new ArrayList<Report>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_untraced);

        // Database Opening
        localDBHelper = new DataBaseHelper(UntracedActivity.this);
        localDBHelper = new DataBaseHelper(this);
        try {
            localDBHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            localDBHelper.openDataBase();

        } catch (SQLException sqle) {

            throw sqle;

        }

        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));

        // For displaying title and subtitle and change text color
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Untraced Consumer Report</font>"));

        table = (TableLayout) findViewById(R.id.tableLayoutrep);
        table2 = (TableLayout) findViewById(R.id.tableLayoutrep2);

        noOfRecords = (TextView) findViewById(R.id.no_of_record);
        mruName = (TextView) findViewById(R.id.mruName);

        horizontalScrollView1 = (HorizontalScrollView) findViewById(R.id.horscroll_view1);
        horizontalScrollView2 = (HorizontalScrollView) findViewById(R.id.horscroll_view2);

        mruName.setText("\t\t\t\t \t\t\t\t MRU Name : " + CommonPref.getUserDetails(getApplicationContext()).get_MRUNo());

        horizontalScrollView1.setVisibility(View.VISIBLE);
        horizontalScrollView2.setVisibility(View.GONE);

        ReportList = localDBHelper.getUntracedReport();
        if(ReportList != null){
            noOfRecords.setText("No. Of Record Found : " + ReportList.size());
            addData();

        }else{
            Toast.makeText(getApplicationContext(), "Error in loading",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void addData() {
        // tr.removeView(tr);
        for (Report rept : ReportList) {
            // ** Create a TableRow dynamically **//*
            TableRow row = new TableRow(getApplicationContext());
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            row.setClickable(true);  //allows you to select a specific row

            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    v.setBackgroundColor(Color.GRAY);
                    System.out.println("Row clicked: " + v.getId());
                    horizontalScrollView1.setVisibility(View.GONE);
                    horizontalScrollView2.setVisibility(View.VISIBLE);
                    ReportList = localDBHelper.getUntracedList();
                    if(ReportList != null){
                        noOfRecords.setText("No. Of Record(s) Found : " + ReportList.size());
                        addDataForList();

                    }else{
                        Toast.makeText(getApplicationContext(), "Error in loading",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });


            // Adding textView to tablerow.

            mruName = new TextView(getApplicationContext());
            mruName.setText(CommonPref.getUserDetails(getApplicationContext()).get_MRUNo());
            // tvCandidateName2.setText(distrpt.getRcpt_amount_Paddy());
            mruName.setTextColor(Color.WHITE);
            mruName.setBackgroundResource(R.drawable.cell_shape2);
            // tvCandidateName2.setMinLines(3);
            mruName.setGravity(Gravity.CENTER);
            mruName.setTextSize(14);
            mruName.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
           /* pending.setLayoutParams(new TableRow.LayoutParams(
                    rowHeader.getChildAt(3).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            mruName.setPadding(10, 5, 5, 5);

            row.addView(mruName);

            // Adding textView to tablerow.

            billed = new TextView(getApplicationContext());
            billed.setText(rept.get_Billed());
            billed.setTextColor(Color.WHITE);
            billed.setBackgroundResource(R.drawable.cell_shape2);
            billed.setGravity(Gravity.CENTER);
            billed.setTextSize(14);
            billed.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            /*billed.setLayoutParams(new TableRow.LayoutParams(
                        rowHeader.getChildAt(2).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            billed.setPadding(10, 5, 5, 5);
            row.addView(billed);

            // Adding textView to tablerow.

            Untraced = new TextView(getApplicationContext());
            Untraced.setText(rept.get_UnBilled());
            // tvCandidateName2.setText(distrpt.getRcpt_amount_Paddy());
            Untraced.setTextColor(Color.WHITE);
            Untraced.setBackgroundResource(R.drawable.cell_shape2);
            // tvCandidateName2.setMinLines(3);
            Untraced.setGravity(Gravity.CENTER);
            Untraced.setTextSize(14);
            Untraced.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
           /* billedAmt.setLayoutParams(new TableRow.LayoutParams(
                    rowHeader.getChildAt(3).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            Untraced.setPadding(10, 5, 5, 5);

            row.addView(Untraced);

            Traced = new TextView(getApplicationContext());
            Traced.setText(rept.get_traced());
            // tvCandidateName2.setText(distrpt.getRcpt_amount_Paddy());
            Traced.setTextColor(Color.WHITE);
            Traced.setBackgroundResource(R.drawable.cell_shape2);
            // tvCandidateName2.setMinLines(3);
            Traced.setGravity(Gravity.CENTER);
            Traced.setTextSize(14);
            Traced.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
           /* billedAmt.setLayoutParams(new TableRow.LayoutParams(
                    rowHeader.getChildAt(3).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            Traced.setPadding(10, 5, 5, 5);

            row.addView(Traced);


            // Adding textView to tablerow.

            totalCon = new TextView(getApplicationContext());
            totalCon.setText(rept.get_TotalCon());
            // tvCandidateName1.setText(distrpt.getApproved_Cash_Credit());
            totalCon.setTextColor(Color.WHITE);
            totalCon.setBackgroundResource(R.drawable.cell_shape2);
            totalCon.setGravity(Gravity.CENTER);
            // tvCandidateName1.setMinLines(3);
            totalCon.setTextSize(14);
            totalCon.setTypeface(Typeface.SERIF, Typeface.NORMAL);
           /* totalCon.setLayoutParams(new TableRow.LayoutParams(rowHeader.getChildAt(1).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            totalCon.setPadding(10, 5, 5, 5);
            row.addView(totalCon);


            // Add the TableRow to the TableLayout
            table.addView(row, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        }
    }


    public void addDataForList() {
        // tr.removeView(tr);
        for (final Report rept : ReportList) {
            // ** Create a TableRow dynamically **//*
           TableRow row = new TableRow(getApplicationContext());
            row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
    //   Log.e("Unmeter",rept.get_AccountNo()+","+rept.getUnmeter());
            // Adding textView to tablerow.
            row.setClickable(true);
            //allows you to select a specific row
            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    v.setBackgroundColor(Color.GRAY);
                    //        System.out.println("Row clicked: " + v.getId());
                    horizontalScrollView1.setVisibility(View.GONE);
                    horizontalScrollView2.setVisibility(View.VISIBLE);
                    String conid=rept.get_AccountNo();
                    Intent intent=null;
                    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    //    intent = new Intent(getBaseContext(), ConsumerActivity.class);
                  //      intent = new Intent(getBaseContext(), ConsumerActivity4A11specific.class);                      //  intent = new Intent(getBaseContext(), ConsumerActivity4A11new.class);
                       intent = new Intent(getBaseContext(), ConsumerActivity4A11new.class);

                    }else{
                        intent = new Intent(getBaseContext(), ConsumerActivity4A11new.class);

                    }
                    intent.putExtra("POS", "ACT_NO");
                    intent.putExtra("VALUE", conid);
                    startActivity(intent);

                }
            });
            accountNo = new TextView(getApplicationContext());
            accountNo.setText(rept.get_AccountNo());
            // tvCandidateName2.setText(distrpt.getRcpt_amount_Paddy());
            accountNo.setTextColor(Color.WHITE);
            accountNo.setBackgroundResource(R.drawable.cell_shape2);
            // tvCandidateName2.setMinLines(3);
            accountNo.setGravity(Gravity.CENTER);
            accountNo.setTextSize(14);
            accountNo.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
           /* pending.setLayoutParams(new TableRow.LayoutParams(
                    rowHeader.getChildAt(3).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            accountNo.setPadding(10, 15, 5, 15);
            row.addView(accountNo);
            // Adding textView to tablerow.
            conName = new TextView(getApplicationContext());
            conName.setText(rept.get_ConName());
            // tvVote1.setText(distrpt.getPaid_to_farmer());
            conName.setTextColor(Color.WHITE);
            conName.setBackgroundResource(R.drawable.cell_shape2);
            // tvVote1.setMinLines(3);
            conName.setGravity(Gravity.CENTER);
            conName.setTextSize(14);
            conName.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            /*billed.setLayoutParams(new TableRow.LayoutParams(
                        rowHeader.getChildAt(2).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            conName.setPadding(10, 15, 5, 15);
            row.addView(conName);
//adding consumer father's name

            fathername = new TextView(getApplicationContext());
            fathername.setText(rept.get_Confathername());
            // tvVote1.setText(distrpt.getPaid_to_farmer());
            fathername.setTextColor(Color.WHITE);
            fathername.setBackgroundResource(R.drawable.cell_shape2);
            // tvVote1.setMinLines(3);
            fathername.setGravity(Gravity.CENTER);
            fathername.setTextSize(14);
            fathername.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            /*billed.setLayoutParams(new TableRow.LayoutParams(
                        rowHeader.getChildAt(2).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            fathername.setPadding(10, 15, 5, 15);
            row.addView(fathername);
            // Adding textView to tablerow.
            address = new TextView(getApplicationContext());
            address.setText(rept.get_Address());
            // tvCandidateName2.setText(distrpt.getRcpt_amount_Paddy());
            address.setTextColor(Color.WHITE);
            address.setBackgroundResource(R.drawable.cell_shape2);
            // tvCandidateName2.setMinLines(3);
            address.setGravity(Gravity.CENTER);
            address.setTextSize(14);
            address.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
           /* billedAmt.setLayoutParams(new TableRow.LayoutParams(
                    rowHeader.getChildAt(3).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            address.setPadding(10, 15, 5, 15);

            row.addView(address);


            // Add the TableRow to the TableLayout
            table2.addView(row, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        }
    }


}
