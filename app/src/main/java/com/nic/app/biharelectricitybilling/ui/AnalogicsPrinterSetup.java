package com.nic.app.biharelectricitybilling.ui;


import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.Utiilties;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class AnalogicsPrinterSetup extends Activity {

	private static final int REQUEST_ENABLE_BT = 1;
	private ActionBar actionBar;
	ListView listDevicesFound;
	Button btnScanDevice, backBtnSET;
	TextView stateBluetooth, btaddressTv;
	BluetoothAdapter bluetoothAdapter;
	private BluetoothAdapter mBluetoothAdapter = null;
	static final UUID MY_UUID = UUID.randomUUID();

	String address = "";

	EditText bluetoothTXT;
	public final String DATA_PATH1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
			+ "/";

	ArrayAdapter<String> btArrayAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));

		// For displaying title and subtitle and change text color
		actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>SetUp</font>"));
		btaddressTv = (TextView) findViewById(R.id.btaddTV);
		// ***********************************************************
		try {

			FileInputStream fstream = new FileInputStream(DATA_PATH1
					+ "BTaddress.txt");

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null) {
				btaddressTv.setText(strLine);
			}

			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
			Utiilties.writeIntoLog(Log.getStackTraceString(e));


		}

		// ***********************************************************

		btnScanDevice = (Button) findViewById(R.id.scandevice);
		stateBluetooth = (TextView) findViewById(R.id.bluetoothstate);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		listDevicesFound = (ListView) findViewById(R.id.devicesfound);
		btArrayAdapter = new ArrayAdapter<String>(AnalogicsPrinterSetup.this,
				android.R.layout.simple_list_item_1);
		listDevicesFound.setAdapter(btArrayAdapter);
		bluetoothTXT = (EditText) findViewById(R.id.bluetoothAds);
		CheckBlueToothState();
		btnScanDevice.setOnClickListener(btnScanDeviceOnClickListener);
		registerReceiver(ActionFoundReceiver, new IntentFilter(
				BluetoothDevice.ACTION_FOUND));
		listDevicesFound.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				/*
				 * Toast.makeText(getApplicationContext(),
				 * ""+listDevicesFound.getCount(), Toast.LENGTH_SHORT).show();
				 */
				String selection = (String) (listDevicesFound
						.getItemAtPosition(position));
				Toast.makeText(getApplicationContext(),
						"BLUETOOTH ADDRESS IS SAVED SUCCESSFULLY",
						Toast.LENGTH_SHORT).show();
				address = selection.substring(0, 17);
				CommonPref.setPrinterMacAddress(AnalogicsPrinterSetup.this, address, "");
				CommonPref.setPrinterType(getApplicationContext(), "A");
				bluetoothTXT.setText(address);
				try {
					File myFile = new File(DATA_PATH1 + "BTaddress.txt");
					myFile.createNewFile();
					FileOutputStream fOut = new FileOutputStream(myFile);
					OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
					myOutWriter.append(address);
					myOutWriter.close();
					fOut.close();
				} catch (Exception e) {
					e.printStackTrace();
					Utiilties.writeIntoLog(Log.getStackTraceString(e));

				}
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				Alertmessage();

			}
		});

		// *********************back Button***********************************
		backBtnSET = (Button) findViewById(R.id.backBtnSET);
		backBtnSET.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

		// **************************************************************

	}


	private void CheckBlueToothState() {
		if (bluetoothAdapter == null) {
			stateBluetooth.setText("Bluetooth NOT support");
		} else {
			if (bluetoothAdapter.isEnabled()) {
				if (ContextCompat.checkSelfPermission(AnalogicsPrinterSetup.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
						ActivityCompat.requestPermissions(AnalogicsPrinterSetup.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
						return;
					}
				}
				if (bluetoothAdapter.isDiscovering()) {
					stateBluetooth
							.setText("Bluetooth is currently in device discovery process.");
				} else {
					stateBluetooth.setText("Bluetooth is Enabled.");
					btnScanDevice.setEnabled(true);
				}
			} else {
				stateBluetooth.setText("Bluetooth is NOT Enabled!");
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}

	private OnClickListener btnScanDeviceOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			btArrayAdapter.clear();
			if (ContextCompat.checkSelfPermission(AnalogicsPrinterSetup.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
					ActivityCompat.requestPermissions(AnalogicsPrinterSetup.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
					return;
				}
			}
			bluetoothAdapter.startDiscovery();
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_ENABLE_BT) {
			CheckBlueToothState();
		}
	}

	private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				if (ContextCompat.checkSelfPermission(AnalogicsPrinterSetup.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
				{
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
					{
						ActivityCompat.requestPermissions(AnalogicsPrinterSetup.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
						return;
					}
				}
				btArrayAdapter.add(device.getAddress() + "\n"
						+ device.getName());
				btArrayAdapter.notifyDataSetChanged();
			}
		}
	};

	public void Alertmessage() {

		if (mBluetoothAdapter == null) {

			Toast.makeText(this, "Bluetooth is not available.",
					Toast.LENGTH_LONG).show();
			//finish();
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(this,
					"Please enable your BT and re-run this program.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

	}

}