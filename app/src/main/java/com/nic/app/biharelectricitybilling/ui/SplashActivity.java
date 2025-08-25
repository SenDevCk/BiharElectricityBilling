package com.nic.app.biharelectricitybilling.ui;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.util.Utiilties;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {
	private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    private boolean isInitDone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MultiDex.install(getApplicationContext());
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			// Call some material design APIs here
			/*if (checkAndRequestPermissions()) {
				//init2();
				if (isInitDone==false) init();
			}*/
			checkAndRequestPermissions();

		} else {
			if (!isInitDone==false) init();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	private void start() {
		new Handler().postDelayed(() -> {
			if(Utiilties.isEmulator() || Debug.isDebuggerConnected()) {
				Toast.makeText(SplashActivity.this, "You Are Using Emulator !", Toast.LENGTH_SHORT).show();
			}else {
				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			}
		},1000);
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@RequiresApi(api = Build.VERSION_CODES.S)
	private boolean checkAndRequestPermissions() {
		int read_media;
		int read_phone = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
		int write_external = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
			read_media = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
		}else {
			read_media = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
		}
		int read_location_fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
		int read_camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
		int bluetooth = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
		int bluetooth_scan = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
		int bluetooth_Connect = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT);
		List<String> listPermissionsNeeded = new ArrayList<>();
		if (bluetooth_scan != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
		}
		if (bluetooth_Connect != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
		}
		if (bluetooth != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(Manifest.permission.BLUETOOTH);
		}
		if (read_phone != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
		}
		if (read_media != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)?Manifest.permission.READ_MEDIA_IMAGES:Manifest.permission.READ_EXTERNAL_STORAGE);
		}
		if (write_external != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}
		if (read_location_fine != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
		}
		if (read_camera != PackageManager.PERMISSION_GRANTED) {
			listPermissionsNeeded.add(Manifest.permission.CAMERA);
		}
		if (!listPermissionsNeeded.isEmpty()) {
			ActivityCompat.requestPermissions(this,
					listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
			return false;
		}
		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_ACCOUNTS:
				if (grantResults.length > 0 &&
						grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					 if(isInitDone==false) init();
				}
				else {
					if (isInitDone==false) init();
					//checkAndRequestPermissions();
				}
				break;
		}
	}

	private void  init(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (Utiilties.isOnline(SplashActivity.this)) {
			if (!prefs.getBoolean("firstTime", false)) {
				start();
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("firstTime", true);
				editor.apply();
			} else {
				start();
			}
		} else if (!prefs.getBoolean("firstTime", false)) {
			final AlertDialog alertDialog = new AlertDialog.Builder(
					SplashActivity.this).create();
			// Setting Dialog Title
			alertDialog.setTitle("Alert Dialog");
			// Setting Dialog Message
			alertDialog.setMessage("Please Enable Net For the First Time");
			// Setting Icon to Dialog
			// alertDialog.setIcon(R.drawable.tick);
			// Setting OK Button
			alertDialog.setButton("OK", (dialog, which) -> {
				// Write your code here to execute after dialog closed
				alertDialog.cancel();
				start();
			});

			// Showing Alert Message
			alertDialog.show();
		}
		else {
			start();
		}
		isInitDone=true;
		//return isInitDone;
	}
}
