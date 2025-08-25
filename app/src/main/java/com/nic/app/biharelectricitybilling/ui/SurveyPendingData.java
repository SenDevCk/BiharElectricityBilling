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
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.AppDatabase;
import com.nic.app.biharelectricitybilling.db.DatabaseClientSurvey;
import com.nic.app.biharelectricitybilling.entity.Survey_Data;
import com.nic.app.biharelectricitybilling.entity.Survey_Data_Details;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
public class SurveyPendingData extends Activity {
    List<Survey_Data> mruList;
   Context mcontext=null;
    ListView dataList;
    String msg = "success";
    Button senddata;
    Button Search;
    private ActionBar actionBar;
    AutoCompleteTextView accountno;
    ArrayList<String> accList;
    RecyclerView recyclerView;
    AppDatabase db=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_data_list);
        msg = "success";
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'> Survey Data List </font>"));
        db=DatabaseClientSurvey.getInstance(getApplicationContext()).getAppDatabase();
        recyclerView = findViewById(R.id.recyclerView1);
        mcontext = SurveyPendingData.this;
    }
    public void loadMruList() {
        try {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SurveyPendingData.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            SurveyAdapter adapter = new SurveyAdapter(SurveyPendingData.this, mruList);
            recyclerView.setAdapter(adapter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private class getalldata extends AsyncTask<Survey_Data_Details, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(
                SurveyPendingData.this);
        @Override
        protected Void doInBackground(Survey_Data_Details... data) {
            mruList=new ArrayList<Survey_Data>();
            mruList=    db.SurveyDao().getAll();
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (mruList.size() != 0 ) {
                loadMruList();
            } else {
                Toast.makeText(SurveyPendingData.this, "No Data Found",
                        Toast.LENGTH_SHORT).show();
                finish();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Fetching data please wait");
            this.dialog.show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        new getalldata().execute();
    }
}
