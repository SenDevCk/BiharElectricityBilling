package com.nic.app.biharelectricitybilling.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;

import java.io.IOException;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

/**
 * Created by NIC123 on 01/04/16.
 */
public class ReportActivity extends Activity {

    private ActionBar actionBar;
    Button btnUnbilled,btnUnbilleddatewise,btnuntraced, btnOutSort,btnNetworkFailure;
    DataBaseHelper localDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Database Opening
        localDBHelper = new DataBaseHelper(ReportActivity.this);
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
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Report Activity</font>"));

        btnUnbilled = (Button) findViewById(R.id.btn_unbilled);
        btnUnbilleddatewise = (Button) findViewById(R.id.btn_unbilled_date);
        btnuntraced = (Button) findViewById(R.id.btn_untrace);
        btnOutSort = (Button) findViewById(R.id.btn_outsort);
        btnNetworkFailure = (Button) findViewById(R.id.btn_network_failure);

        btnUnbilled.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getBaseContext(), UnBilledActivity.class);
                startActivity(intent);
                overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);

            }
        });
        btnUnbilleddatewise.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent intent = new Intent(getBaseContext(), UnBilledActivityDate.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);

        });
        btnuntraced.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent intent = new Intent(getBaseContext(), UntracedActivity.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);

        });
        btnOutSort.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent intent = new Intent(getBaseContext(), OutSortActivity.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);


        });

        btnNetworkFailure.setOnClickListener(v -> {
            // TODO Auto-generated method stub

            Intent intent = new Intent(getBaseContext(), NetworkFailureActivity.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);

        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
            //Display alert message when back button has been pressed
            Intent i = new Intent(ReportActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);

            // finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
