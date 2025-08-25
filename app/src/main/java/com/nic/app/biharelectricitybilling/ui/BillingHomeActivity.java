package com.nic.app.biharelectricitybilling.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import com.nic.app.biharelectricitybilling.R;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class BillingHomeActivity extends Activity {
	
	private ActionBar actionBar;
	Button btnAcNo,btnConNo,btnMeterNo,btnMobileNo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_billing_home);
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
		// For displaying title and subtitle and change text color
		actionBar.setTitle(Html
				.fromHtml("<font color='#FFFFFF'>Billing</font>"));
		btnAcNo = (Button)findViewById(R.id.btn_ac_no);
		btnConNo = (Button)findViewById(R.id.btn_con_id);
		btnMeterNo = (Button)findViewById(R.id.btn_meter_no);
		btnMobileNo = (Button)findViewById(R.id.btn_mobile_no);
		clicklistner();
	}
	private void clicklistner() {
		btnAcNo.setOnClickListener(v -> {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getBaseContext(), SearchAccountActivity.class);
			intent.putExtra("LABLE", "Account Number :");
			intent.putExtra("HINT", "Enter Account Number");
			startActivity(intent);
			overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
			finish();
		});

		btnConNo.setOnClickListener(v -> {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getBaseContext(), SearchAccountActivity.class);
			intent.putExtra("LABLE", "Consumer Number :");
			intent.putExtra("HINT", "Enter Consumer Number");
			startActivity(intent);
			overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
			finish();
		});
		
		btnMeterNo.setOnClickListener(v -> {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getBaseContext(), SearchAccountActivity.class);
			intent.putExtra("LABLE", "Meter Number :");
			intent.putExtra("HINT", "Enter Meter Number");
			startActivity(intent);
			overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
			finish();

		});

		btnMobileNo.setOnClickListener(v -> {
	   // TODO Auto-generated method stub
	   Intent intent = new Intent(getBaseContext(), SearchAccountActivity.class);
	   intent.putExtra("LABLE", "Mobile Number :");
	   intent.putExtra("HINT", "Enter Mobile Number");
	   startActivity(intent);
		   overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
	   finish();

   });
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK ) {
	        // do something on back.
	    	//Display alert message when back button has been pressed
	    	/*Intent intent = new Intent(getBaseContext(), MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
			//finish();*/
			finish();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}



}
