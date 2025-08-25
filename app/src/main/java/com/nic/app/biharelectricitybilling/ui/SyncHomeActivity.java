package com.nic.app.biharelectricitybilling.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lowagie.text.Utilities;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.BillDetails;
import com.nic.app.biharelectricitybilling.entity.MRUDetails;
import com.nic.app.biharelectricitybilling.entity.Report;
import com.nic.app.biharelectricitybilling.entity.UserDetails;
import com.nic.app.biharelectricitybilling.entity.consumer_address;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.GlobalVariables;
import com.nic.app.biharelectricitybilling.util.Utiilties;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SyncHomeActivity extends Activity {
    private ActionBar actionBar;
    Button btnSyncBills, btnSyncMissingConsumer, btnSyncOutsortconsumer, btnmobiledtupdate, btnaddressupadte;
    String msg = "success";
    ArrayList<Report> mruList;
    ArrayList<Report> pdList;
    ArrayList<String> Consumerlist;
    DataBaseHelper localDBHelper;
    Report report;
    consumer_address address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_home);
        // Database Opening
        localDBHelper = new DataBaseHelper(SyncHomeActivity.this);
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

        // For displaying title and subtitle and change text color
        actionBar.setTitle(Html
                .fromHtml("<font color='#FFFFFF'>Synchronize Data</font>"));
        btnSyncBills = (Button) findViewById(R.id.btn_sync_bill);
        btnSyncMissingConsumer = (Button) findViewById(R.id.btn_sync_missing_con);
        btnSyncOutsortconsumer = (Button) findViewById(R.id.btn_sync_image);
        btnmobiledtupdate = (Button) findViewById(R.id.btn_sync_phonedtno);
        btnaddressupadte = (Button) findViewById(R.id.btn_sync_address);


        btnaddressupadte.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                if (Utiilties.isOnline(SyncHomeActivity.this)) {
                    DataBaseHelper placeData = new DataBaseHelper(SyncHomeActivity.this);
                    SQLiteDatabase db = placeData.getReadableDatabase();
                    String tablName = "consumer_address";
                    Cursor cursor = db
                            .rawQuery(
                                    "SELECT  Conid ,distcode ,blockcode,panchayatcode,vill_code,toll_code FROM " + tablName,
                                    null);
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            address = new consumer_address();
                            address.setConid(cursor.getString(cursor.getColumnIndex("Conid")));
                            address.setDistCode(cursor.getString(cursor.getColumnIndex("distcode")));
                            address.setBlockCode(cursor.getString(cursor.getColumnIndex("blockcode")));
                            address.setPanchayatcode(cursor.getString(cursor.getColumnIndex("panchayatcode")));
                            address.setVillcode(cursor.getString(cursor.getColumnIndex("vill_code")));
                            address.setTollcode(cursor.getString(cursor.getColumnIndex("toll_code")));
                            new updateaddress().execute();
                        }
                    } else {
                        Toast.makeText(SyncHomeActivity.this,"आपके पास कोई रलंबित रिकॉर्ड  नहीं है !", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                    db.close();
                    //  new updateDtNo().execute(param[1], param[2],param[0]);
                } else {
                    AlertDialog.Builder ab = new AlertDialog.Builder(SyncHomeActivity.this);
                    ab.setMessage(Html
                            .fromHtml("<font color=#000000>Internet Connection is not avaliable..Please Turn ON Network Connection OR Continue With Off-line Mode..\nTo Turn ON Network Connection Press Yes Button else To Continue With Off-Line Mode Press No Button..</font>"));
                    ab.setPositiveButton("Turn On Network Connection",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    GlobalVariables.isOffline = false;
                                    Intent I = new Intent(
                                            android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                    startActivity(I);
                                }
                            });
                    ab.setNegativeButton("Continue Offline",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {

                                    GlobalVariables.isOffline = true;
                                }
                            });

                    ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
                    ab.show();
                }

            }
        });


        btnmobiledtupdate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                if (Utiilties.isOnline(SyncHomeActivity.this)) {
                    DataBaseHelper placeData = new DataBaseHelper(SyncHomeActivity.this);
                    SQLiteDatabase db = placeData.getReadableDatabase();
                    String tablName = "mobile_dt_table";
                    Cursor cursor = db
                            .rawQuery(
                                    "SELECT  Act_no ,Mobile_no ,Dt_no FROM " + tablName,
                                    null);
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            // ((Button) view).setEnabled(false);
                            // showToast("Uploading...");
                            String[] param = new String[3];
                            param[0] = cursor.getString(cursor.getColumnIndex("Act_no"));
                            param[1] = cursor.getString(cursor.getColumnIndex("Mobile_no"));
                            param[2] = cursor.getString(cursor.getColumnIndex("Dt_no"));

                            new updateDtNo().execute(param[1], param[2], param[0]);
                        }
                    } else {
                        Toast.makeText(SyncHomeActivity.this,
                                "आपके पास कोई रलंबित रिकॉर्ड  नहीं है !", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                    db.close();
                    //  new updateDtNo().execute(param[1], param[2],param[0]);
                } else {
                    AlertDialog.Builder ab = new AlertDialog.Builder(SyncHomeActivity.this);
                    ab.setMessage(Html
                            .fromHtml("<font color=#000000>Internet Connection is not avaliable..Please Turn ON Network Connection OR Continue With Off-line Mode..\nTo Turn ON Network Connection Press Yes Button else To Continue With Off-Line Mode Press No Button..</font>"));
                    ab.setPositiveButton("Turn On Network Connection",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    GlobalVariables.isOffline = false;
                                    Intent I = new Intent(
                                            android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                    startActivity(I);
                                }
                            });
                    ab.setNegativeButton("Continue Offline",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    GlobalVariables.isOffline = true;
                                }
                            });
                    ab.create().getWindow().getAttributes().windowAnimations = R.style.alert_animation;
                    ab.show();
                }

            }
        });
        btnSyncBills.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent i = new Intent(SyncHomeActivity.this, SynPendingData.class);
            startActivity(i);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
            /*	msg="success";
            mruList = localDBHelper.getAllMRU();
            for (MRUDetails mru : mruList) {
                File file = new File(Environment.getExternalStorageDirectory()
                        .getPath() + "/BEB/CropImage/"+ CommonPref.getUserDetails(getApplicationContext()).get_MRUNo() + "/" + mru.get_CON_ID() + ".jpg");
                if(file.exists()) {
                    byte[] photoByte = convertFileToByteArray(file);
                    mru.set_Meter_Photo_Byte(Base64.encodeToString(photoByte, Base64.NO_WRAP));
                }
                if(msg.equalsIgnoreCase("success")){
                    if(Utiilties.isOnline(SyncHomeActivity.this)){
                        new requestForBill().execute(mru);
                    }else{
                        Toast.makeText(SyncHomeActivity.this, "Please Turn On Your Data", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    break;
                }

            }*/

        });
       findViewById(R.id.btn_sync_reason).setOnClickListener(v -> {
           // TODO Auto-generated method stub
           Intent i = new Intent(SyncHomeActivity.this, SynPendingReasondata.class);
           startActivity(i);
           overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
       });

        btnSyncMissingConsumer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

				/*Intent intent = new Intent(getBaseContext(), ConsumerActivity.class);
                startActivity(intent);*/
            }
        });

        btnSyncOutsortconsumer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Consumerlist = new ArrayList<String>();
                report = new Report();
                mruList = localDBHelper.getOutSortList();
                pdList = localDBHelper.getpdList();
                if (mruList.size() > 0) {
                    for (Report mru : mruList) {
                        Consumerlist.add(mru.get_ConID());
                    }
                  /*  report.setUploadlineArrayList(Consumerlist);
                    if (Utiilties.isOnline(SyncHomeActivity.this)) {
                        new requestForOutsort().execute();
                    }*/
                }if(pdList.size()>0){
                    for (Report mru : pdList) {
                        Consumerlist.add(mru.get_ConID());
                    }
                }
                report.setUploadlineArrayList(Consumerlist);
                if (Utiilties.isOnline(SyncHomeActivity.this) && Consumerlist.size()>0) {
                    new requestForOutsort().execute();
                }else{
                    Toast.makeText(SyncHomeActivity.this, "Either you device is offline or no data found", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public static byte[] convertFileToByteArray(File f) {
        byte[] byteArray = null;
        try {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 8];
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    private class requestForBill extends AsyncTask<MRUDetails, Void, BillDetails> {

        public requestForBill() {

        }

        private final ProgressDialog dialog = new ProgressDialog(
                SyncHomeActivity.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SyncHomeActivity.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog
                    .setMessage("Please wait. \n  Request for Generate bills...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected BillDetails doInBackground(MRUDetails... param) {
            UserDetails user = CommonPref.getUserDetails(getApplicationContext());
            BillDetails res1 = WebServiceHelper.BillRequest(param[0], user);
            return res1;
        }
        @Override
        protected void onPostExecute(BillDetails result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                if (result == null) {
                    alertDialog.setTitle("Failed!!");
                    alertDialog.setMessage("Some Error in Network ");
                    alertDialog.show();
                } else {
                    if ((result.get_RESPONSE_MESSAGE().toString().trim().equals("Success")) || (result.get_RESPONSE_MESSAGE().toString().trim().equals("Duplicate Bill"))) {
                        msg = "success";
                        try {
                            SQLiteDatabase db = localDBHelper.getReadableDatabase();
                            long c = localDBHelper.insertBillDetails(result);
                            if (c > 0) {
                                localDBHelper.saveBillStatus("G", result.get_ACT_NO());

                            } else {
                                Toast.makeText(SyncHomeActivity.this, "Sqlite Error during inserting Data",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(SyncHomeActivity.this, "Sqlite Error" + ex.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        msg = result.get_RESPONSE_MESSAGE().toString().trim();
                        alertDialog.setTitle("Failed!!");
                        alertDialog.setMessage(msg);
                        alertDialog.show();
                    }
                }
            }
        }
    }

    private class requestForOutsort extends AsyncTask<MRUDetails, Void, ArrayList<BillDetails>> {

        public requestForOutsort() {

        }

        private final ProgressDialog dialog = new ProgressDialog(
                SyncHomeActivity.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SyncHomeActivity.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog
                    .setMessage("Please wait. \n  Request for Outsort bills...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected ArrayList<BillDetails> doInBackground(MRUDetails... param) {
            UserDetails user = CommonPref.getUserDetails(getApplicationContext());
            ArrayList<BillDetails> res1 = WebServiceHelper.BillRequestOutsort(user, report);
            return res1;
        }

        @Override
        protected void onPostExecute(ArrayList<BillDetails> result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                if (result == null) {
                    alertDialog.setTitle("Failed!!");
                    alertDialog.setMessage("Some Error in Network ");
                    alertDialog.show();
                } else if (result.size() == 1) {
                    if ((result.get(0).get_RESPONSE_MESSAGE().toString().trim().equals("Success")) || (result.get(0).get_RESPONSE_MESSAGE().toString().trim().equals("Duplicate Bill"))) {
                        msg = "success";
                        try {
                            SQLiteDatabase db = localDBHelper.getReadableDatabase();
                            long c = localDBHelper.insertBillDetails(result.get(0));
                            if (c > 0) {
                                localDBHelper.saveBillStatus("G", result.get(0).get_ACT_NO());

                            } else {
                                Toast.makeText(SyncHomeActivity.this, "Sqlite Error during inserting Data",
                                        Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception ex) {
                            Toast.makeText(SyncHomeActivity.this, "Sqlite Error" + ex.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        msg = result.get(0).get_RESPONSE_MESSAGE().toString().trim();
                        alertDialog.setTitle("Failed!!");
                        alertDialog.setMessage(msg);
                        alertDialog.show();

                    }
                } else if (result.size() > 1) {
                    for (BillDetails bill : result) {
                        if ((bill.get_RESPONSE_MESSAGE().toString().trim().equals("Success")) || (bill.get_RESPONSE_MESSAGE().toString().trim().equals("Duplicate Bill"))) {
                            try {
                                SQLiteDatabase db = localDBHelper.getReadableDatabase();
                                long c = localDBHelper.insertBillDetails(bill);
                                if (c > 0) {
                                    localDBHelper.saveBillStatus("G", bill.get_ACT_NO());
                                } else {
                                    Toast.makeText(SyncHomeActivity.this, "Sqlite Error during inserting Data", Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception ex) {
                                Toast.makeText(SyncHomeActivity.this, "Sqlite Error" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SyncHomeActivity.this, "Bill Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }

    private class updateDtNo extends AsyncTask<String, Void, String> {
        String Acc_no = "";
        public updateDtNo() {

        }
        private final ProgressDialog dialog = new ProgressDialog(
                SyncHomeActivity.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SyncHomeActivity.this).create();
        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog
                    .setMessage("Please wait. \n Updating DT and Mobile Number...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... param) {
            UserDetails user = CommonPref.getUserDetails(getApplicationContext());
            Acc_no = param[2];
            String res1 = WebServiceHelper.UpdateMRU(param[0], param[1], param[2], user);
            return res1;
        }

        @Override
        protected void onPostExecute(String result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                if (result != null) {
                    if (result.trim().equalsIgnoreCase("Update Successfull")) {
                        long c = localDBHelper.deletemoiledtno(Acc_no);
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error in Network",
                            Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    private class updateaddress extends AsyncTask<consumer_address, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(
                SyncHomeActivity.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SyncHomeActivity.this).create();
        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Address Updating...");
            this.dialog.show();
        }
        @Override
        protected String doInBackground(consumer_address... address1) {
            return WebServiceHelper.updateaddress(address, CommonPref.getUserDetails(SyncHomeActivity.this));
        }
        @Override
        protected void onPostExecute(String result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                if (result != null) {
                    if (result.contains("SUCCESS")) {
                        long c = localDBHelper.deleteaddress(address.getConid());
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    } else if(result.contains("UPDATED")){
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                        long c = localDBHelper.deleteaddress(address.getConid());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error in Network",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
