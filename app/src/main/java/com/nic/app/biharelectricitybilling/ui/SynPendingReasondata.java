package com.nic.app.biharelectricitybilling.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

public class SynPendingReasondata extends Activity {
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
        dataList = (ListView) findViewById(R.id.listConsumer);
        senddata = (Button) findViewById(R.id.total);
        accountno = (AutoCompleteTextView) findViewById(R.id.et_accountno);
        Search = (Button) findViewById(R.id.bt_search);
        mruList = localDBHelper.getUnbilledreason();
        if (mruList.size() > 0) {
            accList = new ArrayList<>();
            for (MRUDetails mru : mruList) {
                accList.add(mru.get_ACT_NO());
            }
        } else {
            Toast.makeText(SynPendingReasondata.this, "No Data Found",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, accList);
        accountno.setThreshold(0);
        accountno.setAdapter(adapter);
        if (mruList.size() != 0) {
            loadMruList();
        } else {
            Toast.makeText(SynPendingReasondata.this, "No Data Found",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String acc_no = accountno.getText().toString();
                if (acc_no.matches("")) {
                    mruList = localDBHelper.getUnbilledreason();
                    loadMruList();
                } else {
                    mruList = localDBHelper.getMRUByAccNO1reason("ACT_NO", acc_no);
                    if (mruList.size() > 0) {
                        loadMruList();
                    }
                }
            }
        });
        senddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = "success";
                mruList1 = localDBHelper.getAllMRUreason();
                for (MRUDetails mru : mruList1) {
                    if (Utiilties.isOnline(SynPendingReasondata.this)) {
                        // new requestForBill().execute(mru);

                        new updatebillingreason().execute(mru.get_CON_ID(), mru.get_Reason(),mru.get_ACT_NO());
                    } else {
                        Toast.makeText(SynPendingReasondata.this, "Please Turn On Your Data", Toast.LENGTH_SHORT).show();
                        break;
                    }

                }
                refresh();

            }
        });
        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                if (Utiilties.isOnline(SynPendingReasondata.this)) {

                    //  new requestForBill().execute(mruList.get(position));

                    new updatebillingreason().execute(mruList.get(position).get_CON_ID(), mruList.get(position).get_Reason(),mruList.get(position).get_ACT_NO());

                } else {
                    Toast.makeText(SynPendingReasondata.this, "Please Turn On Your Data", Toast.LENGTH_SHORT).show();
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

    public void loadMruList() {
        try {
            adapter = new ConsumerAdapter1(SynPendingReasondata.this,
                    R.layout.list_row_consumer, mruList);
            dataList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error in loading",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class updatebillingreason extends AsyncTask<String, Void, String> {
        String acc_no;

        public updatebillingreason() {

        }

        private final ProgressDialog dialog = new ProgressDialog(
                SynPendingReasondata.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SynPendingReasondata.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog
                    .setMessage("Please wait. \n Updating Reason Of not billing...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... param) {
            acc_no = param[2];
            UserDetails user = CommonPref
                    .getUserDetails(getApplicationContext());
            String res1 = WebServiceHelper.Updatereason(param[0], param[1], user);
            return res1;
        }

        @Override
        protected void onPostExecute(String result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                alertDialog.setTitle("Updating MRU..");
                if (result != null) {
                    alertDialog.setMessage("Synchronizing...");
                    alertDialog.show();
                    if (result.trim().contains("SUCCESS")) {
                        long c = localDBHelper.updatereasonstatus(acc_no, "N");
                        if (c > 0) {
                            Toast.makeText(getApplicationContext(), result,
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Error in Local Database",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                  /*  long c = localDBHelper.updateMru(etMobileNo.getText()
                            .toString(), etDTNo.getText().toString(), tvAcNo
                            .getText().toString(), "Y");
                    Toast.makeText(getApplicationContext(), "Error in Network",
                            Toast.LENGTH_LONG).show();*/
                    finish();
                }

                alertDialog.cancel();
            }

        }
    }

    private class requestForBill extends AsyncTask<MRUDetails, Void, BillDetails> {
        MRUDetails mru = new MRUDetails();

        public requestForBill() {

        }

        private final ProgressDialog dialog = new ProgressDialog(
                SynPendingReasondata.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SynPendingReasondata.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Please wait. \n  Request for Generate bills...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected BillDetails doInBackground(MRUDetails... param) {
            mru = new MRUDetails();
            mru = param[0];
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
                    msg = "" + result.get_RESPONSE_MESSAGE();
                    alertDialog.setMessage("Error:" + msg);
                    alertDialog.show();
                } else {
                    if ((result.get_RESPONSE_MESSAGE().toString().trim().equals("Success")) || (result.get_RESPONSE_MESSAGE().toString().trim().equals("Duplicate Bill"))) {
                        msg = "success";
                        try {
                            SQLiteDatabase db = localDBHelper.getReadableDatabase();
                            long c = localDBHelper.insertBillDetails(result);
                            if (c > 0) {
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
                                mruList = localDBHelper.getUnbilledreason();
                                if (mruList.size() > 0) {
                                    loadMruList();
                                    //  adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(SynPendingReasondata.this, "No Data Found", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else {
                                Toast.makeText(SynPendingReasondata.this, "Sqlite Error during inserting Data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(SynPendingReasondata.this, "Sqlite Error" + ex.getMessage(), Toast.LENGTH_SHORT).show();
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

    private class requestForBillApl extends AsyncTask<MRUDetails, Void, BillDetails> {
        public requestForBillApl() {
        }

        private final ProgressDialog dialog = new ProgressDialog(
                SynPendingReasondata.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SynPendingReasondata.this).create();

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

                if (result == null) {

                    alertDialog.setTitle("Failed!!");
                    alertDialog.setMessage("Error :" + result.get_RESPONSE_MESSAGE());
                    alertDialog.show();

                } else {

                    if ((result.get_RESPONSE_MESSAGE().toString().trim().equals("Success")) || (result.get_RESPONSE_MESSAGE().toString().trim().equals("Duplicate Bill"))) {
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
                                    Toast.makeText(SynPendingReasondata.this, "No Data Found",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                            } else {
                                Toast.makeText(SynPendingReasondata.this, "Sqlite Error during inserting Data",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception ex) {
                            Toast.makeText(SynPendingReasondata.this, "Sqlite Error" + ex.getMessage(),
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

    public void refresh() {
        mruList.clear();
        mruList = localDBHelper.getUnbilledreason();
        // adapter.notifyDataSetChanged();
        if (mruList.size() > 0) {
            loadMruList();

        } else {
            Toast.makeText(SynPendingReasondata.this, "No Data Found", Toast.LENGTH_SHORT).show();
            finish();

        }
    }

    @Override
    protected void onResume() {
        mruList.clear();
        mruList = localDBHelper.getUnbilledreason();
        // adapter.notifyDataSetChanged();
        if (mruList.size() > 0) {
            loadMruList();
            // adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(SynPendingReasondata.this, "No Data Found", Toast.LENGTH_SHORT).show();
            finish();
        }
        super.onResume();
    }
}
