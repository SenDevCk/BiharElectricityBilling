package com.nic.app.biharelectricitybilling.Asyntask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

public class ConnectBluetooth extends AsyncTask<Void,Void,String> {
    ProgressDialog pDialog;
    private Activity activity;
    ConnectBluetooth(AppCompatActivity activity){

        this.activity=activity;

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog=new ProgressDialog(activity);
        pDialog.setCancelable(false);
        pDialog.setMessage("Connecting.....");
        pDialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
