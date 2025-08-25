package com.nic.app.biharelectricitybilling.ui;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.AppDatabase;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.db.DatabaseClientSurvey;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.MRUDetails;
import com.nic.app.biharelectricitybilling.entity.Survey_Data;
import com.nic.app.biharelectricitybilling.entity.Survey_Data_Details;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.Utiilties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class SurveyActivity extends Activity {
    private ActionBar actionBar;
    Button btnDwnldMRU, btnDosurvey, btnUploadservey;
    MRUDetails mru1;
    ProgressDialog progressDialog;//DBPL-653
    int count = 0;
    int surveycount = 0;
    Survey_Data_Details data_details = null;
    List<Survey_Data_Details> surveydatalist = null;
    AlertDialog dialog = null;
    AppDatabase db=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        // Database Opening
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
        // For displaying title and subtitle and change text color
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Survey</font>"));
        mru1 = new MRUDetails();
        db =DatabaseClientSurvey.getInstance(getApplicationContext()).getAppDatabase();
        btnDwnldMRU = (Button) findViewById(R.id.btn_dwnld_data);
        btnDosurvey = (Button) findViewById(R.id.btn_dosurvey);
        btnUploadservey = (Button) findViewById(R.id.btn_upload_survey);
        btnUploadservey.setOnClickListener(view -> {
            if (surveycount > 0) {
                    new getalldata().execute();
            }
        });
        btnDosurvey.setOnClickListener(view -> {
            if (count > 0) {
                Intent dosurveyintent = new Intent(SurveyActivity.this, SurveyPendingData.class);
                startActivity(dosurveyintent);
            } else {
                Toast.makeText(SurveyActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
            }

        });

        btnDwnldMRU.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            if (Utiilties.isOnline(SurveyActivity.this)) {
                //  new CheckUpdate().execute();
                new getMRUList().execute(
                        CommonPref.getUserDetails(SurveyActivity.this)
                                .get_UserID(),
                        CommonPref.getUserDetails(SurveyActivity.this)
                                .get_password(),
                        CommonPref.getUserDetails(SurveyActivity.this)
                                .get_IMEI());
            } else {
                Toast.makeText(SurveyActivity.this, "Please Go Online to Download Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class getMRUList extends AsyncTask<String, Void, ArrayList<Survey_Data>> {
        public getMRUList() {
        }

        private final ProgressDialog dialog = new ProgressDialog(
                SurveyActivity.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SurveyActivity.this).create();

        @Override
        protected void onPreExecute() {
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Please wait. Loading and saving Survey Data...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }
        @Override
        protected ArrayList<Survey_Data> doInBackground(String... param) {
            ArrayList<Survey_Data> res1 = WebServiceHelper.LoadSurveyData(param[0], param[1], param[2]);
            return res1;
        }
        @Override
        protected void onPostExecute(ArrayList<Survey_Data> result) {
            if (result != null) {
                if (result.size() == 1) {
                    if (this.dialog.isShowing())
                        this.dialog.dismiss();
                    for (Survey_Data mru : result) {
                        if (result.get(0).getRESPONSE_MESSAGE().toString().trim().equals("Success")) {
                            Toast.makeText(SurveyActivity.this, result.size() + " Only One Data found please add more consumer", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SurveyActivity.this, result.size() + result.get(0).getRESPONSE_MESSAGE().toString().trim(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    ThreadInsertData insertthread=new ThreadInsertData(result);
                    insertthread.start();
                    if (this.dialog.isShowing())
                        this.dialog.dismiss();
                }
            } else {
                Toast.makeText(SurveyActivity.this, "Error occurred in Network ", Toast.LENGTH_LONG).show();
            }

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // do something on back.
            //Display alert message when back button has been pressed
            Intent i = new Intent(SurveyActivity.this, MainActivity.class);
            i.setFlags(FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    private class UploadDetails extends AsyncTask<Survey_Data_Details, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(
                SurveyActivity.this);
        @Override
        protected String doInBackground(Survey_Data_Details... data) {
            return WebServiceHelper.updateSurveydeatils(data_details, CommonPref.getUserDetails(SurveyActivity.this));
        }
        @Override
        protected void onPostExecute(String result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                if (result != null) {
                    if (result.contains("SUCCESS")) {
                        //  long c = localDBHelper.deleteaddress(address.getConid());
                        Threaddeleterecord t1 = new Threaddeleterecord();
                        t1.start();
                        Threadsurveycount surveythread = new Threadsurveycount();
                        surveythread.start();
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    } else if (result.contains("UPDATED")) {
                        Threaddeleterecord t1 = new Threaddeleterecord();
                        t1.start();
                        Threadsurveycount surveythread = new Threadsurveycount();
                        surveythread.start();
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    }else  if(result.contains("unique constraint")){
                        Threaddeleterecord t1 = new Threaddeleterecord();
                        t1.start();
                        Toast.makeText(getApplicationContext(), "Duplicate Data Found", Toast.LENGTH_LONG).show();
                        Threadsurveycount surveythread = new Threadsurveycount();
                        surveythread.start();
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error in Network" + result, Toast.LENGTH_LONG).show();
                }
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("uploading data please wait");
            this.dialog.show();
        }
    }
    private class getalldata extends AsyncTask<Survey_Data_Details, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(
                SurveyActivity.this);
        @Override
        protected Void doInBackground(Survey_Data_Details... data) {
            surveydatalist = new ArrayList<>();
            surveydatalist = db.SurveyDataDao().getAll();
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
          //  super.onPostExecute(unused);
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if(Utiilties.isOnline(SurveyActivity.this)){
                if(surveydatalist.size()>0){
                    upload_data();
                }else{
                    Toast.makeText(SurveyActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                }
            }else{
                   Toast.makeText(SurveyActivity.this, "Enable Net to upload Data", Toast.LENGTH_SHORT).show();
            }

        }
        @Override
        protected void onPreExecute() {
        //    super.onPreExecute();
            this.dialog.setMessage("Fetching data please wait");
            this.dialog.show();
        }
    }
/*    private class getinserting extends AsyncTask<Survey_Data_Details, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(
                SurveyActivity.this);
        private final AlertDialog alertDialog = new AlertDialog.Builder(
                SurveyActivity.this).create();


        @Override
        protected Void doInBackground(Survey_Data_Details... data) {
            surveydatalist = new ArrayList<>();
            surveydatalist = DatabaseClientSurvey.getInstance(getApplicationContext()).getAppDatabase()
                    .SurveyDataDao()
                    .getAll();
            return null;
        }


        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            surveydatalist.forEach(e -> {
                data_details = e;
                Log.e("datadetails", data_details.toString());
                new UploadDetails().execute(e);
            });

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Fetching data please wait");
            this.dialog.show();
        }
    }*/


    class Threadsurveycount extends Thread {
        @Override
        public void run() {
            btnUploadservey.setText("Upload Survey");
            surveycount = db.SurveyDataDao().getcount();
            btnUploadservey.setText(btnUploadservey.getText().toString() + "  (" + surveycount + ")");
            if(surveycount>=10){
                new getalldata().execute();
            }
        }
    }
    class Threadrecordcount extends Thread {
        @Override
        public void run() {
                btnDosurvey.setText("Survey");
                count = db.SurveyDao().getcount();
                btnDosurvey.setText(btnDosurvey.getText().toString() + "  (" + count + ")");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Threadsurveycount surveythread = new Threadsurveycount();
        surveythread.start();
        Threadrecordcount recordcount = new Threadrecordcount();
        recordcount.start();
    }
    class ThreadInsertData extends Thread{
        ArrayList<Survey_Data> list=new ArrayList<>();
        ThreadInsertData(ArrayList<Survey_Data> result){
            this.list=result;
        }
        @Override
        public void run() {
            list.forEach(e->{
               db.SurveyDao().insert(e);
            });
            finish();

        }
    }
    class Threaddeleterecord extends Thread {
        @Override
        public void run() {
            Log.e("after upload",data_details.getACT_NO());
            db.SurveyDataDao().deletesurveyByact_no(data_details.getACT_NO());

        }

    }
    public void upload_data(){
        surveydatalist.forEach(e->{
            data_details=e;
             Log.e("list data",data_details.toString());
             new UploadDetails().execute(data_details);
         });
    }
}
