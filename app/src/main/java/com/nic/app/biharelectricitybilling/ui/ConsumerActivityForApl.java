package com.nic.app.biharelectricitybilling.ui;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.UserDetails;
import com.nic.app.biharelectricitybilling.entity.dconsumer_details;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.Utiilties;
import java.io.IOException;
import java.util.ArrayList;
public class ConsumerActivityForApl extends Activity {
    private ActionBar actionBar;
    Button btnContinue,btnUpload;
    DataBaseHelper localDBHelper;
    dconsumer_details mruList;
    ScrollView scrollView;
    TextView tvAcNo, tvConNo, tvConName, tvAddress, tvCategory;
    String Disremarks = "0";
    Spinner spoptions;
    Boolean isMeterNoCorrect;
    ArrayList<dconsumer_details> dconsumerlist=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dconsumer);
        localDBHelper = new DataBaseHelper(ConsumerActivityForApl.this);
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
        // -------------------------------------------------------------------------------------
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
        actionBar.setTitle(Html
                .fromHtml("<font color='#FFFFFF'>Consumer Details </font>"));
        String pos = getIntent().getStringExtra("POS");
        String value = getIntent().getStringExtra("VALUE");
        initialization();
        isMeterNoCorrect = true;
        mruList = localDBHelper.getdisconnectedconsumerdetails(pos, value);
        dconsumerlist=localDBHelper.getdisconnectedpendingconsumerdetails("", "");
        if(dconsumerlist.size()>0){
            ( (LinearLayout)findViewById(R.id.laypending)).setVisibility(View.VISIBLE);
        }else{
            ( (LinearLayout)findViewById(R.id.laypending)).setVisibility(View.GONE);
        }
        if (mruList == null) {
            scrollView.setVisibility(View.GONE);

        } else {
            valueInitialization();
        }
    }

    private void valueInitialization() {
        // TODO Auto-generated method stub
        tvAcNo.setText(mruList.getCon_id());
        tvConNo.setText(mruList.getCon_id());
        tvConName.setText(mruList.getCname());
        tvAddress.setText(mruList.getCfname());
        tvCategory.setText(mruList.getDisconnecteddate());
    }

    private void initialization() {
        // TODO Auto-generated method stub

        tvAcNo = (TextView) findViewById(R.id.tv_ac_no);
        tvConNo = (TextView) findViewById(R.id.tv_con_no);
        tvConName = (TextView) findViewById(R.id.tv_con_name);
        tvCategory = (TextView) findViewById(R.id.tv_ddate);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        spoptions = (Spinner) findViewById(R.id.sp_option);
        btnContinue = (Button) findViewById(R.id.btn_continue);
        btnUpload = (Button) findViewById(R.id.btn_pending);
        spoptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Disremarks = "0";
                } else {
                    Disremarks = getResources().getStringArray(R.array.reportoption)[position].trim();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Disremarks = "0";
            }
        });
        btnContinue.setOnClickListener(v -> {
            if (Disremarks.equalsIgnoreCase("0") || Disremarks.equalsIgnoreCase("- Select Options -")) {
                Toast.makeText(ConsumerActivityForApl.this, "Please Select Disconnection Remarks", Toast.LENGTH_SHORT).show();
            } else {
                if (Utiilties.isOnline(ConsumerActivityForApl.this)) {
                    new updateDisconnectionremarks().execute(tvConNo.getText().toString(), Disremarks, "");
                } else {
                    Long i = localDBHelper.updatedconsumerdetails(tvConNo.getText().toString(), Disremarks);
                    if (i >= 0) {
                        Toast.makeText(ConsumerActivityForApl.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ConsumerActivityForApl.this, "Error in local Database", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utiilties.isOnline(ConsumerActivityForApl.this)) {
                    Toast.makeText(ConsumerActivityForApl.this, "Turn On Internet", Toast.LENGTH_SHORT).show();
                } else {
                    btnUpload.setEnabled(false);
                    dconsumerlist=localDBHelper.getdisconnectedpendingconsumerdetails("", "");
                    if(dconsumerlist.size()>0){
                        ( (LinearLayout)findViewById(R.id.laypending)).setVisibility(View.VISIBLE);
                    }else{
                        ( (LinearLayout)findViewById(R.id.laypending)).setVisibility(View.GONE);
                    }
                    for (int i = 0; i < dconsumerlist.size(); i++) {
                        String[] param = new String[2];
                        param[0] = dconsumerlist.get(i).getCon_id();
                        param[1] = dconsumerlist.get(i).getCname();
                        new updateDisconnectionremarks().execute(param[0], param[1], "");
                    }
                }
            }
        });

    }

    private class updateDisconnectionremarks extends AsyncTask<String, Void, String> {
String conid="0";
        public updateDisconnectionremarks() {

        }

        private final ProgressDialog dialog = new ProgressDialog(
                ConsumerActivityForApl.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                ConsumerActivityForApl.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog
                    .setMessage("Please wait. \n Updating Disconnection Remarks...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... param) {
            UserDetails user = CommonPref
                    .getUserDetails(getApplicationContext());
            conid=param[0];
            String res1 = WebServiceHelper.Updatedisconnectionremarks(param[0], param[1], user);
            return res1;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (result.trim().equalsIgnoreCase("SUCCESS")) {
                    localDBHelper.deletedisremarks(conid);
                    dconsumerlist=localDBHelper.getdisconnectedpendingconsumerdetails("", "");
                    if(dconsumerlist.size()>0){
                        ( (LinearLayout)findViewById(R.id.laypending)).setVisibility(View.VISIBLE);
                        btnUpload.setText("Upload Pending"+dconsumerlist.size());
                    }else{
                        ( (LinearLayout)findViewById(R.id.laypending)).setVisibility(View.GONE);
                    }
                    Toast.makeText(getApplicationContext(),
                            "" + result,
                            Toast.LENGTH_LONG).show();
                    finish();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "" + result,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),
                            "" + result,
                            Toast.LENGTH_LONG).show();
                }

            }


        }

    }

    @Override
    protected void onResume() {
        dconsumerlist=localDBHelper.getdisconnectedpendingconsumerdetails("", "");
        if(dconsumerlist.size()>0){
            ( (LinearLayout)findViewById(R.id.laypending)).setVisibility(View.VISIBLE);
            btnUpload.setText("Upload Pending"+dconsumerlist.size());
        }else{
            ( (LinearLayout)findViewById(R.id.laypending)).setVisibility(View.GONE);
        }
        super.onResume();
    }
}
