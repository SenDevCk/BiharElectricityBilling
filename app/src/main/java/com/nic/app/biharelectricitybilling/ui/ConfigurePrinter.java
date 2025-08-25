package com.nic.app.biharelectricitybilling.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.Utiilties;

import java.util.ArrayList;
import java.util.HashMap;

import print.ShowMsg;

/**
 * Created by NIC123 on 13/03/16.
 */

public class ConfigurePrinter extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener {

    private Context mContext = null;
    private ArrayList<HashMap<String, String>> mPrinterList = null;
    private SimpleAdapter mPrinterListAdapter = null;
    private FilterOption mFilterOption = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_printer);
        mContext = this;
        Button button = (Button)findViewById(R.id.btnRestart);
        button.setOnClickListener(this);
        mPrinterList = new ArrayList<HashMap<String, String>>();
        mPrinterListAdapter = new SimpleAdapter(this, mPrinterList, R.layout.list_at,
                new String[] { "PrinterName", "Target" },
                new int[] { R.id.PrinterName, R.id.Target });
        ListView list = (ListView)findViewById(R.id.lstReceiveData);
        list.setAdapter(mPrinterListAdapter);
        list.setOnItemClickListener(this);
        mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "start", mContext);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }

        restartDiscovery();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    break;
                }
            }
        }
        mFilterOption = null;
    }

    @Override
    public void onClick(View v) {
        // Do nothing
        if (v.getId() == R.id.btnRestart) {
            Log.e("epson setup", "epson");
            restartDiscovery();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();

        HashMap<String, String> item  = mPrinterList.get(position);
        CommonPref.setPrinterMacAddress(ConfigurePrinter.this,item.get("Target"),"");
        CommonPref.setPrinterType(getApplicationContext(),"E");
        Toast.makeText(getApplicationContext(), item.get("Target"),Toast.LENGTH_LONG).show();
        finish();
    }

    private void restartDiscovery() {
        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    ShowMsg.showException(e, "stop", mContext);
                    return;
                }
            }
        }

        mPrinterList.clear();
        mPrinterListAdapter.notifyDataSetChanged();

        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "stop", mContext);
         //   Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
    }

    private final DiscoveryListener mDiscoveryListener = deviceInfo -> runOnUiThread(new Runnable() {
        @Override
        public synchronized void run() {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("PrinterName", deviceInfo.getDeviceName());
            item.put("Target", deviceInfo.getTarget());
            mPrinterList.add(item);
            mPrinterListAdapter.notifyDataSetChanged();
        }
    });


}

