package com.nic.app.biharelectricitybilling.ui;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
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

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class SummaryActivity extends Activity {

    private ActionBar actionBar;
    DataBaseHelper localDBHelper;
    TextView mruName,noOfRecords, category, totalCon,billed,units,billedAmt,pending;
    TableLayout table;
    TableRow rowHeader;

    ArrayList<Report> ReportList = new ArrayList<Report>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // Database Opening
        localDBHelper = new DataBaseHelper(SummaryActivity.this);
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
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Summary</font>"));

        table = (TableLayout) findViewById(R.id.tableLayoutrep);

        noOfRecords = (TextView) findViewById(R.id.no_of_record);
        mruName = (TextView) findViewById(R.id.mruName);
        mruName.setText("\t\t\t\t \t\t\t\t MRU Name : " +CommonPref.getUserDetails(getApplicationContext()).get_MRUNo());

        ReportList = localDBHelper.getSummaryReport();
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


                // ** Creating a TextView to add to the row **//*

            category = new TextView(getApplicationContext());
            category.setText(rept.get_CategoryName());
            // tvACName.setMinLines(3);
            category.setTextColor(Color.WHITE);
            category.setBackgroundResource(R.drawable.cell_shape2);
            category.setGravity(Gravity.CENTER);
            category.setTextSize(12);
            category.setTypeface(Typeface.SERIF, Typeface.NORMAL);
           /* category.setLayoutParams(new TableRow.LayoutParams(
                    rowHeader.getChildAt(0).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            category.setPadding(10, 5, 5, 5);
                row.addView(category);

                // Adding textView to tablerow.

            totalCon = new TextView(getApplicationContext());
            totalCon.setText(rept.get_TotalCon());
                // tvCandidateName1.setText(distrpt.getApproved_Cash_Credit());
            totalCon.setTextColor(Color.WHITE);
            totalCon.setBackgroundResource(R.drawable.cell_shape2);
            totalCon.setGravity(Gravity.CENTER);
                // tvCandidateName1.setMinLines(3);
            totalCon.setTextSize(12);
            totalCon.setTypeface(Typeface.SERIF, Typeface.NORMAL);
           /* totalCon.setLayoutParams(new TableRow.LayoutParams(rowHeader.getChildAt(1).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            totalCon.setPadding(10, 5, 5, 5);
                row.addView(totalCon); // Adding textView to tablerow.

            billed = new TextView(getApplicationContext());
            billed.setText(rept.get_Billed());
                // tvVote1.setText(distrpt.getPaid_to_farmer());
            billed.setTextColor(Color.WHITE);
            billed.setBackgroundResource(R.drawable.cell_shape2);
            // tvVote1.setMinLines(3);
            billed.setGravity(Gravity.CENTER);
            billed.setTextSize(12);
            billed.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            /*billed.setLayoutParams(new TableRow.LayoutParams(
                        rowHeader.getChildAt(2).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            billed.setPadding(10, 5, 5, 5);
                row.addView(billed);

                // Adding textView to tablerow.

            units = new TextView(getApplicationContext());
            units.setText(rept.get_Units());
                // tvCandidateName2.setText(distrpt.getRcpt_amount_Paddy());
            units.setTextColor(Color.WHITE);
            units.setBackgroundResource(R.drawable.cell_shape2);
                // tvCandidateName2.setMinLines(3);
            units.setGravity(Gravity.CENTER);
            units.setTextSize(12);
            units.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            /*units.setLayoutParams(new TableRow.LayoutParams(
                    rowHeader.getChildAt(3).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            units.setPadding(10, 5, 5, 5);

            row.addView(units);

            // Adding textView to tablerow.

            billedAmt = new TextView(getApplicationContext());
            billedAmt.setText(rept.get_BilledAmt());
            // tvCandidateName2.setText(distrpt.getRcpt_amount_Paddy());
            billedAmt.setTextColor(Color.WHITE);
            billedAmt.setBackgroundResource(R.drawable.cell_shape2);
            // tvCandidateName2.setMinLines(3);
            billedAmt.setGravity(Gravity.CENTER);
            billedAmt.setTextSize(12);
            billedAmt.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
           /* billedAmt.setLayoutParams(new TableRow.LayoutParams(
                    rowHeader.getChildAt(3).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            billedAmt.setPadding(10, 5, 5, 5);

            row.addView(billedAmt);

            // Adding textView to tablerow.

            pending = new TextView(getApplicationContext());
            pending.setText(Integer.toString(Integer.valueOf(rept.get_TotalCon()) - Integer.valueOf(rept.get_Billed())));
            // tvCandidateName2.setText(distrpt.getRcpt_amount_Paddy());
            pending.setTextColor(Color.WHITE);
            pending.setBackgroundResource(R.drawable.cell_shape2);
            // tvCandidateName2.setMinLines(3);
            pending.setGravity(Gravity.CENTER);
            pending.setTextSize(12);
            pending.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
           /* pending.setLayoutParams(new TableRow.LayoutParams(
                    rowHeader.getChildAt(3).getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT));*/
            pending.setPadding(10, 5, 5, 5);

            row.addView(pending);
            // Add the TableRow to the TableLayout
            table.addView(row, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
            //Display alert message when back button has been pressed
            Intent i = new Intent(SummaryActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
            //   finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
