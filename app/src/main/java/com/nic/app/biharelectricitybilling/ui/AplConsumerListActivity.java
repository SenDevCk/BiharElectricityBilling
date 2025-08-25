package com.nic.app.biharelectricitybilling.ui;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.analogics.thermalprinter.AnalogicsThermalPrinter;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.entity.BillDetails;
import com.nic.app.biharelectricitybilling.entity.ConsumerAdapterApl;
import com.nic.app.biharelectricitybilling.entity.dconsumer_details;
import com.nic.app.biharelectricitybilling.util.Utiilties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class AplConsumerListActivity extends Activity {
    private ActionBar actionBar;
    DataBaseHelper localDBHelper;
    ConsumerAdapterApl adapter;
    ArrayList<dconsumer_details> mruList;
    ArrayList<String> accList;
    ListView dataList;
    BillDetails bill;
    Button Printall;
    Button Search;
    AutoCompleteTextView accountno;
    static final UUID MY_UUID = UUID.randomUUID();
    protected String btAddressDir = Environment.getExternalStorageDirectory()
            + "";
    BluetoothAdapter bluetoothAdapter;
    String address = null;
    AnalogicsThermalPrinter conn = new AnalogicsThermalPrinter();
    BluetoothAdapter mBluetoothAdapter;
    TextView txttotal;
    BluetoothDevice con_dev = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apl);
        // Database Opening
        localDBHelper = new DataBaseHelper(AplConsumerListActivity.this);
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
                .fromHtml("<font color='#FFFFFF'>List of Temp Disconnected Consumer </font>"));

        accountno = (AutoCompleteTextView) findViewById(R.id.et_accountno);
        Search = (Button) findViewById(R.id.bt_search);
        Printall = (Button) findViewById(R.id.btn_print);
        txttotal = (TextView) findViewById(R.id.total);
        dataList = (ListView) findViewById(R.id.listConsumer);
        mruList = localDBHelper.getdisconnectedconsumer("Is_Pending_Bill", "G");
        int total = mruList.size();
        txttotal.setText("List Of TD Consumer" + " : " + total);
        if (mruList.size() > 0) {
            accList = new ArrayList<>();
            for (dconsumer_details mru : mruList) {
                accList.add(mru.getCon_id());
            }
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, accList);
        accountno.setThreshold(0);
        accountno.setAdapter(adapter);
        loadMruList();
        Search.setOnClickListener(view -> {
            String acc_no = accountno.getText().toString();
            if (acc_no.matches("")) {
                mruList = localDBHelper.getdisconnectedconsumer("Is_Pending_Bill", "G");
                loadMruList();
            } else {
                mruList = localDBHelper.getdisconnectedconsumerbyacc_no("conid", acc_no);
                if (mruList.size() > 0) {
                    loadMruList();
                }
            }
        });
        dataList.setOnItemClickListener((parent, view, position, id) -> {
            // TODO Auto-generated method stub
            String position1 = "", value = "";
            position1 = "conid";
            value=  mruList.get(position).getCon_id().trim();
            Intent intent = new Intent(getBaseContext(), ConsumerActivityForApl.class);
            intent.putExtra("POS", position1);
            intent.putExtra("VALUE", value);
            startActivity(intent);
        });

    }
    public void loadMruList() {
        try {
            adapter = new ConsumerAdapterApl(AplConsumerListActivity.this,
                    R.layout.list_row_dconsumer, mruList);
            dataList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error in loading",
                    Toast.LENGTH_SHORT).show();
            Utiilties.writeIntoLog(Log.getStackTraceString(ex));
        }
    }

    @Override
    protected void onResume() {
        mruList = localDBHelper.getdisconnectedconsumer("Is_Pending_Bill", "G");
        int total = mruList.size();
        txttotal.setText("List Of TD Consumer" + " : " + total);
        loadMruList();
        super.onResume();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
            //Display alert message when back button has been pressed
            Intent i = new Intent(AplConsumerListActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
          //  finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}

