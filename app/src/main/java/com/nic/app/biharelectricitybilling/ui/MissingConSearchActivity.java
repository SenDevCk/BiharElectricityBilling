package com.nic.app.biharelectricitybilling.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.BillDetails;
import com.nic.app.biharelectricitybilling.entity.ConsumerAdapter;
import com.nic.app.biharelectricitybilling.entity.ConsumerAdaptermissing;
import com.nic.app.biharelectricitybilling.entity.MRUDetails;
import com.nic.app.biharelectricitybilling.entity.MRUDetailsmissingconsumer;
import com.nic.app.biharelectricitybilling.entity.UserDetails;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.Utiilties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class MissingConSearchActivity extends Activity {
    private ActionBar actionBar;
    Button btnSearch;
    TextView lableSearch, lablesubdiv;
    EditText hintSearch;
    String lable, hint;
    LinearLayout linSearch,manlay;
    Boolean flag = false;
    CommonPref commonPref;
    ConsumerAdaptermissing adapter;
    ListView lin_listview;
    RadioGroup radioGroup;
    DataBaseHelper localDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing_con_search);
        lin_listview=(ListView)findViewById(R.id.listConsumermissin) ;
        // Database Opening
        localDBHelper = new DataBaseHelper(MissingConSearchActivity.this);
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
        actionBar.setTitle(Html
                .fromHtml("<font color='#FFFFFF'>Search Consumer</font>"));
        addListenerOnButton();
        lin_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                // ListView Clicked item value
                String conId = ((TextView) view.findViewById(R.id.tv_con_id))
                        .getText().toString();
               // String[] param = {hintSearch.getText().toString().trim()};
                new requestForMissingConsumer().execute(conId);
            }
        });
    }
    public void addListenerOnButton() {
        lableSearch = (TextView) findViewById(R.id.tv_search_for);
        hintSearch = (EditText) findViewById(R.id.et_search_for_hint);
        btnSearch = (Button) findViewById(R.id.btn_search);
        linSearch = (LinearLayout) findViewById(R.id.lin_search);
        manlay= (LinearLayout) findViewById(R.id.lin_manlay);
        lablesubdiv = (TextView) findViewById(R.id.Subdivision);
        radioGroup = (RadioGroup) findViewById(R.id.radioMissingConsumer);
        lablesubdiv.setText(CommonPref.getUserDetails(getApplicationContext()).get_SubdivName());
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    if (!hintSearch.getText().toString().trim().isEmpty() && !lableSearch.getText().toString().trim().equalsIgnoreCase("Meter No")) {
                        String[] param = {hintSearch.getText().toString().trim()};
                        new requestForMissingConsumer().execute(param);
                    }else if(!hintSearch.getText().toString().trim().isEmpty() && lableSearch.getText().toString().trim().equalsIgnoreCase("Meter No")) {
                        String[] param = {hintSearch.getText().toString().trim()};
                        new getMRUListmissing().execute(
                                CommonPref.getUserDetails(getApplicationContext())
                                        .get_UserID(),
                                CommonPref.getUserDetails(getApplicationContext())
                                        .get_password(),
                                CommonPref.getUserDetails(getApplicationContext())
                                        .get_IMEI(),"",hintSearch.getText().toString().trim());
                    }else
                     {
                        Toast.makeText(MissingConSearchActivity.this, "Please Enter Number", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MissingConSearchActivity.this, "Kindly Select Radio Button ", Toast.LENGTH_LONG).show();
                }

            }

        });

    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rb_accountNumber:
                if (checked) {
                    linSearch.setVisibility(View.VISIBLE);
                    lable = "Account Number";
                    hint = "Enter Account Number";
                    lableSearch.setText(lable);
                    hintSearch.setHint(hint);
                    flag = true;
                    break;
                }
            case R.id.rb_consumerId:
                if (checked) {
                    linSearch.setVisibility(View.VISIBLE);
                    lable = "Consumer Number";
                    hint = "Enter Consumer Number";
                    lableSearch.setText(lable);
                    hintSearch.setHint(hint);
                    flag = true;
                    break;
                }

            case R.id.rb_oldId:
                if (checked) {
                    linSearch.setVisibility(View.VISIBLE);
                    lable = "Old Id";
                    hint = "Enter Old Id";
                    lableSearch.setText(lable);
                    hintSearch.setHint(hint);
                    flag = true;
                    break;
                }
            case R.id.rb_meterno:
                if (checked) {
                    linSearch.setVisibility(View.VISIBLE);
                    lable = "Meter No";
                    hint = "Enter Meter No";
                    lableSearch.setText(lable);
                    hintSearch.setHint(hint);
                    flag = true;
                    break;
                }
        }
    }


    private class requestForMissingConsumer extends AsyncTask<String, Void, MRUDetails> {

        public requestForMissingConsumer() {

        }

        private final ProgressDialog dialog = new ProgressDialog(
                MissingConSearchActivity.this);

        private final AlertDialog alertDialog = new AlertDialog.Builder(
                MissingConSearchActivity.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Please wait. \n  Request for Missing Consumer...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected MRUDetails doInBackground(String... param) {
            UserDetails user = CommonPref.getUserDetails(getApplicationContext());
            MRUDetails res1 = WebServiceHelper.LoadMissingConsumetrMRU(user.get_UserID(), user.get_password(), user.get_IMEI(), user.get_SubdivId(), param[0]);
            return res1;
        }
        @Override
        protected void onPostExecute(MRUDetails result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                if (result == null) {
                    alertDialog.setTitle("Failed!!");
                    alertDialog.setMessage("Some Error in Network " + result.get_RESPONSE_MESSAGE());
                    alertDialog.show();
                } else {
                    if ((result.get_RESPONSE_MESSAGE().toString().trim().equals("SAME_MRU")) /*|| (result.get_RESPONSE_MESSAGE().toString().trim().equals("DIFF_MRU"))*/) {
                        try {

                            long c = localDBHelper.updateMissingConInMru(result);
                            if (c > 0) {

                                Toast.makeText(MissingConSearchActivity.this, "Found and Save Locally",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent=null;
                                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                //  intent = new Intent(getBaseContext(), ConsumerActivity4A11specific.class);
                                    intent = new Intent(getBaseContext(), ConsumerActivity4A11new.class);
                                 //   intent = new Intent(getBaseContext(), ConsumerActivity4A11.class);

                                }else{
                                   // intent = new Intent(getBaseContext(), ConsumerActivity.class);
                                    intent = new Intent(getBaseContext(), ConsumerActivity4A11new.class);
                                  //  intent = new Intent(getBaseContext(), ConsumerActivity4A11new.class);

                                }
                                intent.putExtra("POS", "ACT_NO");
                                intent.putExtra("VALUE", result.get_ACT_NO());
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MissingConSearchActivity.this, "Sqlite Error during inserting Data",
                                        Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception ex) {
                            Toast.makeText(MissingConSearchActivity.this, "Sqlite Error" + ex.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else if (result.get_RESPONSE_MESSAGE().toString().trim().equals("DIFF_MRU")) {
                        try {
                            long c = localDBHelper.insertMissingConDiffMru(result);
                            if (c > 0) {
                                Toast.makeText(MissingConSearchActivity.this, "Found different MRU User Save Locally",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getBaseContext(), MissingConsumerActivity.class);
                                intent.putExtra("POS", "ACT_NO");
                                intent.putExtra("VALUE", result.get_ACT_NO());
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MissingConSearchActivity.this, "Sqlite Error during inserting Data",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(MissingConSearchActivity.this, "Sqlite Error" + ex.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else if (result.get_RESPONSE_MESSAGE().contains("Request Accepetd For MRU Change")) {
                        alertDialog.setTitle("Success!!");
                        alertDialog.setMessage(result.get_RESPONSE_MESSAGE().toString().trim());
                        alertDialog.show();

                    } else {
                        alertDialog.setTitle("Failed!!");
                        alertDialog.setMessage(result.get_RESPONSE_MESSAGE().toString().trim());
                        alertDialog.show();
                    }

                }
            }
        }
    }
    private class getMRUListmissing extends AsyncTask<String, Void, ArrayList<MRUDetailsmissingconsumer>> {
        public getMRUListmissing() {
        }

        private final ProgressDialog dialog = new ProgressDialog(
                MissingConSearchActivity.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                MissingConSearchActivity.this).create();

        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Please wait. Searching Consumer...");
            dialog.setCancelable(false);
           dialog.show();
        }

        @Override
        protected ArrayList<MRUDetailsmissingconsumer> doInBackground(String... param) {
            ArrayList<MRUDetailsmissingconsumer> res1 = WebServiceHelper.searchconsumerbymeterno(param[0],param[1],param[2],param[3],param[4]);
            return res1;
        }

        @Override
        protected void onPostExecute(ArrayList<MRUDetailsmissingconsumer> result) {

            //   alertDialog.setTitle("Saving MRU..");
            if (result != null) {
                //      alertDialog.setMessage("Synchronizing..." + String.valueOf(result.size()));
                //     alertDialog.show();
                if (result.size() >0) {
                    for (MRUDetailsmissingconsumer mru : result) {
                        if (mru.get_RESPONSE_MESSAGE().toString().trim().equals("SUCCESS")) {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            loadMruList(result);
                        } else {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error" + mru.get_RESPONSE_MESSAGE().toString().trim(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                }else{
                if (dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Some Internal Error", Toast.LENGTH_LONG).show();

            }

            //   alertDialog.cancel();

        }
    }
    public void loadMruList(ArrayList<MRUDetailsmissingconsumer> list) {
        lin_listview.setVisibility(View.VISIBLE);
        manlay.setVisibility(View.GONE);
        try {
            adapter = new ConsumerAdaptermissing(MissingConSearchActivity.this,
                    R.layout.list_row_consumer, list);
            lin_listview.setAdapter(adapter);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error in loading",
                    Toast.LENGTH_SHORT).show();
            Utiilties.writeIntoLog(Log.getStackTraceString(ex));
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
            //Display alert message when back button has been pressed
            Intent i = new Intent( MissingConSearchActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
