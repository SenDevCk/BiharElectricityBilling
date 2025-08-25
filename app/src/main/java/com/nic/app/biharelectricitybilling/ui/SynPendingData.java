package com.nic.app.biharelectricitybilling.ui;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.BillDetails;
import com.nic.app.biharelectricitybilling.entity.ConsumerAdapter1;
import com.nic.app.biharelectricitybilling.entity.MRUDetails;
import com.nic.app.biharelectricitybilling.entity.UserDetails;
import com.nic.app.biharelectricitybilling.entity.lk_md_details;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.Utiilties;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
public class SynPendingData extends Activity {
    ArrayList<MRUDetails> mruList, mruList1;
    DataBaseHelper localDBHelper;
    ConsumerAdapter1 adapter;
    ListView dataList;
    String msg = "success";
    Button senddata;
    Button Search;
    private ActionBar actionBar;
    AutoCompleteTextView accountno;
    ArrayList<String> accList;
    lk_md_details lkdetails;
    Context mcontext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syn_pending_data);
        msg = "success";
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
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'> Syncronization </font>"));
        dataList = findViewById(R.id.listConsumer);
        senddata = findViewById(R.id.total);
        accountno = findViewById(R.id.et_accountno);
        Search = findViewById(R.id.bt_search);
        mcontext = this;
        mruList = localDBHelper.getUnbilledMRU();
        if (mruList.size() > 0) {
            accList = new ArrayList<>();
            for (MRUDetails mru : mruList) {
                accList.add(mru.get_ACT_NO());
            }
        } else {
            Toast.makeText(SynPendingData.this, "No Data Found",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, accList);
        accountno.setThreshold(0);
        accountno.setAdapter(adapter);
        if (mruList.size() != 0) {
            loadMruList();
        } else {
            Toast.makeText(SynPendingData.this, "No Data Found",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        Search.setOnClickListener(view -> {
            String acc_no = accountno.getText().toString();
            if (acc_no.matches("")) {
                mruList = localDBHelper.getUnbilledMRU();
                loadMruList();
            } else {
                mruList = localDBHelper.getMRUByAccNO1("ACT_NO", acc_no);
                if (mruList.size() > 0) {
                    loadMruList();
                }
            }
        });
        senddata.setOnClickListener(v -> {
            msg = "success";
            mruList1 = localDBHelper.getAllMRU();
            Bitmap thumbnail = null;
            for (MRUDetails mru : mruList1) {
                Uri file = Uri.parse(mru.get_Meter_Photo());
               // mru.set_OCR_Agency(CommonPref.getUserDetails(SynPendingData.this).get_ocr_agency());
                if (!Uri.EMPTY.equals(file)) {
                    byte[] inputData = null;
                    try {
                        InputStream iStream = getContentResolver().openInputStream(file);
                        inputData = getBytes1(iStream);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                                thumbnail = mcontext.getContentResolver().loadThumbnail(file, new Size(200, 200), null);
                            } else {
                                thumbnail = MediaStore.Images.Media.getBitmap(mcontext.getContentResolver(), file);
                            }
                            inputData = getbytearray(thumbnail);
                            Log.e("Image", thumbnail.toString());
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            Log.e("exception :", thumbnail.toString());
                        }
                    }

                    //    byte[] photoByte = getbytearray(thumbnail);
                    mru.set_Meter_Photo_Byte(Base64.encodeToString(inputData, Base64.NO_WRAP));
                    //   Log.e("image", photoByte.toString());
                } else {
                    mru.set_Meter_Photo_Byte("");
                }
                if (msg.equalsIgnoreCase("success")) {
                    if (Utiilties.isOnline(SynPendingData.this)) {
                        if (mru.get_APL_BILLING_FLAG().equalsIgnoreCase("N") && mru.get_APL_CONSUMER().equalsIgnoreCase("Y")) {
                            new requestForBillApl().execute(mru);
                        } else {
                            new requestForBill().execute(mru);
                        }

                    } else {
                        Toast.makeText(SynPendingData.this, "Please Turn On Your Data", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    break;
                }

            }
            refresh();
        });
        dataList.setOnItemClickListener((parent, view, position, id) -> {
            // TODO Auto-generated method stub
            Bitmap thumbnail = null;
            Uri file = Uri.parse(mruList.get(position).get_Meter_Photo());
            if (!Uri.EMPTY.equals(file)) {
                byte[] inputData = null;
                try {
                    InputStream iStream = getContentResolver().openInputStream(file);
                    inputData = getBytes1(iStream);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                            thumbnail = mcontext.getContentResolver().loadThumbnail(file, new Size(200, 200), null);
                        } else {
                            thumbnail = MediaStore.Images.Media.getBitmap(mcontext.getContentResolver(), file);
                        }
                        inputData = getbytearray(thumbnail);
                        Log.e("Image", thumbnail.toString());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        Log.e("exception :", thumbnail.toString());
                    }
                }
                // byte[] photoByte = getbytearray(thumbnail);
                mruList.get(position).set_Meter_Photo_Byte(Base64.encodeToString(inputData, Base64.NO_WRAP));

            } else {
                mruList.get(position).set_Meter_Photo_Byte("");
            }

            if (Utiilties.isOnline(SynPendingData.this)) {
                new requestForBill().execute(mruList.get(position));
            } else {
                Toast.makeText(SynPendingData.this, "Please Turn On Your Data", Toast.LENGTH_SHORT).show();
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

    public void loadMruList() {
        try {
            adapter = new ConsumerAdapter1(SynPendingData.this,
                    R.layout.list_row_consumer, mruList);
            dataList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error in loading",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class requestForBill extends AsyncTask<MRUDetails, Void, BillDetails> {
        MRUDetails mru = null;

        public requestForBill() {

        }

        private final ProgressDialog dialog = new ProgressDialog(
                SynPendingData.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SynPendingData.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Please wait. \n  Request for Generate bills...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected BillDetails doInBackground(MRUDetails... param) {
            mru = param[0];
            UserDetails user = CommonPref.getUserDetails(getApplicationContext());
            BillDetails res1 = WebServiceHelper.BillRequest(param[0], user);
            return res1;
        }

        @Override
        protected void onPostExecute(BillDetails result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
                crashlytics.setCustomKey("syspending", "" + result);
                if (result == null) {
                    alertDialog.setTitle("Failed!!");
                    //    msg = "" + result.get_RESPONSE_MESSAGE();
                    alertDialog.setMessage("Error:" + "null value retuned");
                    alertDialog.show();
                } else {
                    if ((result.get_RESPONSE_MESSAGE().toString().trim().equals("Success")) || (result.get_RESPONSE_MESSAGE().toString().trim().equals("Duplicate Bill"))) {
                        msg = "success";
                        try {
                            SQLiteDatabase db = localDBHelper.getReadableDatabase();
                            long c = localDBHelper.insertBillDetails(result);
                            if (c > 0) {
                                if (CommonPref.getUserDetails(SynPendingData.this).get_bill_agency().contains("DATA INGENIOUS")) {
                                    new Thread(() -> {
                                        senddata(result, mru);
                                    }).start();
                                }if(CommonPref.getUserDetails(SynPendingData.this).get_ocr_agency().contains("CORAL")){
                                    new Thread(() -> {
                                        senddata_coral(result, mru);
                                    }).start();
                                }if(CommonPref.getUserDetails(SynPendingData.this).get_ocr_agency().contains("SUJANIX")){
                                    new Thread(() -> {
                                        senddata_SUJANIX(result, mru);
                                    }).start();
                                }
                                localDBHelper.saveBillStatus("G", result.get_ACT_NO());
                            /*    lkdetails = localDBHelper.getmdlkdetails(CommonPref.getUserDetails(SynPendingData.this));
                                if (!mru.get_Previous_read_stat().equalsIgnoreCase("MD")) {
                                    if (result.get_READ_STAT().equalsIgnoreCase("LK")) {
                                        int lkcount = Integer.parseInt(lkdetails.getSub_div_id()) + 1;
                                        localDBHelper.insertlkmdstatus1("current_lk", lkcount, CommonPref.getUserDetails(SynPendingData.this));
                                    } else if (result.get_READ_STAT().equalsIgnoreCase("MD")) {
                                        int lkcount = Integer.parseInt(lkdetails.getCon_id()) + 1;
                                        localDBHelper.insertlkmdstatus1("current_md", lkcount, CommonPref.getUserDetails(SynPendingData.this));
                                    }
                                }*/
                                mruList.clear();
                                mruList = localDBHelper.getUnbilledMRU();
                                if (mruList.size() > 0) {
                                    loadMruList();
                                    //  adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(SynPendingData.this, "No Data Found", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else {
                                Toast.makeText(SynPendingData.this, "Sqlite Error during inserting Data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(SynPendingData.this, "Sqlite Error" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else if (result.get_RESPONSE_MESSAGE().equalsIgnoreCase("Bill Request Pending for Approval")) {
                        Long i = localDBHelper.saveBillreason("PA", mru.get_ACT_NO());
                        alertDialog.setTitle("Info!!");
                        alertDialog.setMessage(result.get_RESPONSE_MESSAGE().toString().trim());
                        alertDialog.show();
                        mruList.clear();
                        mruList = localDBHelper.getUnbilledMRU();
                        if (mruList.size() > 0) {
                            loadMruList();
                            //  adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(SynPendingData.this, "No Data Found", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }else if(result.get_RESPONSE_MESSAGE().toString().trim().contains("AMBIGUITY")){
                        Long i = localDBHelper.saveBillreason("AMBIGUITY", mru.get_ACT_NO());
                        alertDialog.setTitle("Info!!");
                        alertDialog.setMessage(result.get_RESPONSE_MESSAGE().toString().trim());
                        alertDialog.show();
                        mruList.clear();
                        mruList = localDBHelper.getUnbilledMRU();
                        if (mruList.size() > 0) {
                            loadMruList();
                            //  adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(SynPendingData.this, "No Data Found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                        else {
                        msg = result.get_RESPONSE_MESSAGE().toString().trim();
                        alertDialog.setTitle("Failed!!");
                        alertDialog.setMessage(msg);
                        alertDialog.show();

                    }
                }
            }
        }
    }

    private class requestForBillApl extends AsyncTask<MRUDetails, Void, BillDetails> {
        public requestForBillApl() {
        }

        private final ProgressDialog dialog = new ProgressDialog(
                SynPendingData.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SynPendingData.this).create();

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

            BillDetails res1 = WebServiceHelper.BillRequestAPl(param[0], user);

            return res1;
        }

        @Override
        protected void onPostExecute(BillDetails result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
              /*  FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
                crashlytics.setCustomKey("Bill result", ""+result);*/
                if (result == null) {
                    alertDialog.setTitle("Failed!!");
                    alertDialog.setMessage("Error : Return null value");
                    alertDialog.show();

                } else {

                    if ((result.get_RESPONSE_MESSAGE().trim().equals("Success")) || (result.get_RESPONSE_MESSAGE().trim().equals("Duplicate Bill"))) {
                        msg = "success";
                        try {
                            SQLiteDatabase db = localDBHelper.getReadableDatabase();
                            long c = localDBHelper.insertBillDetails(result);
                            if (c > 0) {
                                localDBHelper.saveBillStatus("G", result.get_ACT_NO());
                                mruList.clear();
                                mruList = localDBHelper.getUnbilledMRU();
                                // adapter.notifyDataSetChanged();
                                if (mruList.size() > 0) {
                                    loadMruList();
                                    //  adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(SynPendingData.this, "No Data Found",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                            } else {
                                Toast.makeText(SynPendingData.this, "Sqlite Error during inserting Data",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception ex) {
                            Toast.makeText(SynPendingData.this, "Sqlite Error" + ex.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else if (result.get_RESPONSE_MESSAGE().toString().trim().contains("Bill Request Pending")) {
                        Long i = localDBHelper.saveBillreason("AMBIGUITY", result.get_ACT_NO());
                        alertDialog.setTitle("Info!!");
                        alertDialog.setMessage(result.get_RESPONSE_MESSAGE().toString().trim());
                        alertDialog.show();
                    } else {
                        alertDialog.setTitle("Info!!");
                        alertDialog.setMessage(result.get_RESPONSE_MESSAGE().toString().trim());
                        alertDialog.show();
                    }
                }
            }
        }
    }

    public void refresh() {
        mruList.clear();
        mruList = localDBHelper.getUnbilledMRU();
        // adapter.notifyDataSetChanged();
        if (mruList.size() > 0) {
            loadMruList();

        } else {
            Toast.makeText(SynPendingData.this, "No Data Found", Toast.LENGTH_SHORT).show();
            finish();

        }
    }

    @Override
    protected void onResume() {
        mruList.clear();
        mruList = localDBHelper.getUnbilledMRU();
        // adapter.notifyDataSetChanged();
        if (mruList.size() > 0) {
            loadMruList();
            // adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(SynPendingData.this, "No Data Found", Toast.LENGTH_SHORT).show();
            finish();
        }
        super.onResume();
    }

    public byte[] getbytearray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }
    public void senddata_coral(BillDetails bill,MRUDetails mru) {
        UserDetails user = CommonPref.getUserDetails(SynPendingData.this);
        Uri uri = Uri.parse("content://in.coral.met.provider/bill");
        String data = user.get_UserID() + "," + user.get_SubdivId() + "," + user.get_DivId() + ",Billing," + bill.get_CON_ID() + "," + user.get_MRUNo() + "," + mru.get_Latitude() + "," + mru.get_Longitude() + "," + bill.get_READ_DATE() + "," + bill.get_READ_STAT() + "," + bill.get_CUR_READ();
        ContentValues values = new ContentValues();
        try {
            /*values.put("id", 0);
            values.put("data", data);
            values.put("uploaded", 0);*/
            values.put("consumerId", bill.get_CON_ID());
            values.put("accountId", bill.get_ACT_NO());
            values.put("readingStatus", bill.get_READ_STAT());
            values.put("MRC", user.get_UserID());
            values.put("MRU", user.get_MRUNo());
            values.put("subDiv", user.get_SubdivId());
            values.put("div", user.get_DivId());
            values.put("lat", mru.get_Latitude());
            values.put("lon", mru.get_Longitude());
            values.put("agencyId", user.get_bill_agency());
            values.put("billId", bill.get_BILL_NO());
            values.put("billAmount", "");
            values.put("billDate", bill.get_READ_DATE());
            values.put("billTime", "");
            values.put("billUnits", "");
            values.put("lastPaidDate", "");
            values.put("lastPaidAmount", "");
            values.put("prevReadingStatus", bill.get_PRE_READ_STAT());
            values.put("arrears", "");

            ContentResolver contentResolver = getContentResolver();
            Uri newuri = contentResolver.insert(uri, values);
            Log.e("data sent", "" + data);
            //  android.util.Log.d("NewActivity", "getDataFromIntent: "+newuri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void senddata_SUJANIX(BillDetails bill,MRUDetails mru) {
        UserDetails user = CommonPref.getUserDetails(SynPendingData.this);
        Uri uri = Uri.parse("content://com.sujanix.truereadlink.provider/bill");
    //    String data = user.get_UserID() + "," + user.get_SubdivId() + "," + user.get_DivId() + ",Billing," + bill.get_CON_ID() + "," + user.get_MRUNo() + "," + mru.get_Latitude() + "," + mru.get_Longitude() + "," + bill.get_READ_DATE() + "," + bill.get_READ_STAT() + "," + bill.get_CUR_READ();
        ContentValues values = new ContentValues();
        try {
            /*values.put("id", 0);
            values.put("data", data);
            values.put("uploaded", 0);*/
            values.put("consumerId", bill.get_CON_ID());
            values.put("accountId", bill.get_ACT_NO());
            values.put("readingStatus", bill.get_READ_STAT());
            values.put("MRC", user.get_UserID());
            values.put("MRU", user.get_MRUNo());
            values.put("subDiv", user.get_SubdivId());
            values.put("div", user.get_DivId());
            values.put("lat", mru.get_Latitude());
            values.put("lon", mru.get_Longitude());
            values.put("agencyId", user.get_bill_agency());
            values.put("billId", bill.get_BILL_NO());
            values.put("billAmount", "");
            values.put("billDate", bill.get_READ_DATE());
            values.put("billTime", "");
            values.put("billUnits", "");
            values.put("lastPaidDate", "");
            values.put("lastPaidAmount", "");
            values.put("prevReadingStatus", bill.get_PRE_READ_STAT());
            values.put("arrears", "");

            ContentResolver contentResolver = getContentResolver();
            Uri newuri = contentResolver.insert(uri, values);
         //   Log.e("data sent", "" + data);
            //  android.util.Log.d("NewActivity", "getDataFromIntent: "+newuri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void senddata(BillDetails bill,MRUDetails mru) {
        UserDetails user=CommonPref.getUserDetails(SynPendingData.this);
        Uri uri = Uri.parse("content://com.datainfosys.bpdcl.provider/Log");
        String data=user.get_UserID()+","+user.get_SubdivId()+","+user.get_DivId()+",Billing,"+bill.get_CON_ID()+","+user.get_MRUNo()+","+mru.get_Latitude()+","+mru.get_Longitude()+","+bill.get_READ_DATE()+","+bill.get_READ_STAT()+","+bill.get_CUR_READ();
        ContentValues values = new ContentValues();
        try {
            values.put("id", 0);
            values.put("data",data);
            values.put("uploaded", 0);
            ContentResolver contentResolver = getContentResolver();
            Uri newuri = contentResolver.insert(uri, values);
            Log.e("data sent",""+data);
            android.util.Log.d("NewActivity", "getDataFromIntent: "+newuri);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public byte[] getBytes1(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
