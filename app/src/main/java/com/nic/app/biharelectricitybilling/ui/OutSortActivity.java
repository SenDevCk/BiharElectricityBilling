package com.nic.app.biharelectricitybilling.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.entity.Report;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.Utiilties;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by NIC123 on 04/04/16.
 */
public class OutSortActivity extends Activity {
    private ActionBar actionBar;
    DataBaseHelper localDBHelper;
    TextView mruName,noOfRecords,accountNo,conName,address;
    TableLayout table;
    ArrayList<Report> ReportList = new ArrayList<Report>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outsort);
        // Database Opening
        localDBHelper = new DataBaseHelper(OutSortActivity.this);
        localDBHelper = new DataBaseHelper(this);
        try {
            localDBHelper.createDataBase();
        } catch (IOException ioe) {
            Utiilties.writeIntoLog(Log.getStackTraceString(ioe));
            throw new Error("Unable to create database");
        }
        try {
            localDBHelper.openDataBase();
        } catch (SQLException sqle) {
            Utiilties.writeIntoLog(Log.getStackTraceString(sqle));
            throw sqle;
        }
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
        // For displaying title and subtitle and change text color
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>OutSort Report</font>"));
        table = (TableLayout) findViewById(R.id.tableLayoutrep2);
        noOfRecords = (TextView) findViewById(R.id.no_of_record);
        mruName = (TextView) findViewById(R.id.mruName);
        mruName.setText("\t\t\t\t \t\t\t\t MRU Name : " + CommonPref.getUserDetails(getApplicationContext()).get_MRUNo());
        ReportList = localDBHelper.getOutSortList();
        if(ReportList != null){
            noOfRecords.setText("No. Of record Found : " + ReportList.size());
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

            // Adding textView to tablerow.12610062242

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
            table.addView(row, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        }

    }


}
