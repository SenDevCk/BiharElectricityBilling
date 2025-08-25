package com.nic.app.biharelectricitybilling.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.util.AppUtils;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.Utiilties;

import java.io.File;

public class MainActivity extends Activity {
    LinearLayout logout, billing, Albilling, sync, missingConsumer, report, summary, setup,survey, print, disconnectedcon;
    private ActionBar actionBar;
    TextView userName;
    View v;
    public static final String SDCARD = String.valueOf(Environment.getExternalStorageDirectory());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            deleteLog();
            createAppDirectories();
        } catch (Exception ex) {
            Utiilties.writeIntoLog(Log.getStackTraceString(ex));
        }
        setContentView(R.layout.activity_main);
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
        // For displaying title and subtitle and change text color
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>User Home</font>"));
        logout =  findViewById(R.id.linlayout_logout);
        billing =  findViewById(R.id.billing);
        Albilling =  findViewById(R.id.billingApl);
        sync =  findViewById(R.id.linearlayout_sync);
        print =  findViewById(R.id.linearlayout_print);
        missingConsumer =  findViewById(R.id.linlayout_missing_consumer);
        disconnectedcon =  findViewById(R.id.linlayout_disconnected_consumer);
        report =  findViewById(R.id.linearlayout_report);
        summary =  findViewById(R.id.linlayout_summary);
        setup =  findViewById(R.id.linearlayout_setup);
        survey =  findViewById(R.id.linearlayout_survey);
        userName = (TextView) findViewById(R.id.userName);
        v=(View)findViewById(R.id.surveyview);
        userName.setText(CommonPref.getUserDetails(getApplicationContext()).get_UserName());
        String subdivid=CommonPref.getUserDetails(MainActivity.this).get_SubdivId();
        if(subdivid.startsWith("2")){
             survey.setVisibility(View.GONE);
             v.setVisibility(View.GONE);
        }
        clicklistner();
    }
    private void clicklistner() {
        logout.setOnClickListener(v -> showdialoggogreen(2));

        billing.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent intent = new Intent(getBaseContext(), BillingHomeActivity.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);

        });
        Albilling.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent intent = new Intent(getBaseContext(), AplConsumerListActivity.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);

            //	finish();
        });
        sync.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent intent = new Intent(getBaseContext(), SyncHomeActivity.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
        });

        print.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent intent = new Intent(getBaseContext(), PrintHomeActivity.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);


        });

        missingConsumer.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent intent = new Intent(getBaseContext(), MissingConSearchActivity.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);

        });
        disconnectedcon.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent intent = new Intent(getBaseContext(), AplConsumerListActivity.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);

        });
        setup.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent intent = new Intent(getBaseContext(), SetUpActivity.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
        });

        survey.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent intent = new Intent(getBaseContext(), SurveyActivity.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
        });

        report.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent intent = new Intent(getBaseContext(), ReportActivity.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
        });

        summary.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent intent = new Intent(getBaseContext(), SummaryActivity.class);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
        });
    }

    private void deleteLog() {

        File log = new File(Utiilties.LOG_FILE_PATH);
        if (log.exists()) {
            log.delete();
        }
    }

    // Creating App Directory For Log File
    private void createAppDirectories() {
        System.out.println("log file");
        File dir1 = new File(Utiilties.APP_DIR);
        if (!dir1.exists()) {
            dir1.mkdir();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // do something on back.
            //Display alert message when back button has been pressed
            showdialoggogreen(1);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void showdialoggogreen(final int i) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Do You Want To Close The Application !!");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    dialog.cancel();
                    if (i == 2) {
                        finishAffinity();
                        Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);

                        //  finish();
                    } else {
                        finishAffinity();
                    }
                });

        builder1.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppUtils.deleteCache(getApplicationContext());
    }
}
