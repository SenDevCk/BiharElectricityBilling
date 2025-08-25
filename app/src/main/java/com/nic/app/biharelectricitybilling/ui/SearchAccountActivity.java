package com.nic.app.biharelectricitybilling.ui;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.util.CommonPref;

public class SearchAccountActivity extends Activity {

	private ActionBar actionBar;
	Button btnSearch;
	TextView lableSearch, SubdivName, mruName, DivName;
	EditText hintSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back12));
		// For displaying title and subtitle and change text color
		actionBar.setTitle(Html
				.fromHtml("<font color='#FFFFFF'>Search Consumer</font>"));
		String lable = getIntent().getStringExtra("LABLE");
		final String hint = getIntent().getStringExtra("HINT");
		lableSearch = (TextView) findViewById(R.id.tv_search_for);
		SubdivName = (TextView) findViewById(R.id.tv_subdiv);
		DivName = (TextView) findViewById(R.id.tv_div);
		mruName = (TextView) findViewById(R.id.mruName);
		hintSearch = (EditText) findViewById(R.id.et_search_for_hint);
		btnSearch = (Button) findViewById(R.id.btn_search);
		lableSearch.setText(lable);
		if(lable.matches("Account Number :") || lable.matches("Consumer Number :") ||lable.matches("Meter Number :")){
			//hintSearch.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
		}else if(lable.matches("Mobile Number :")){
			//hintSearch.setFilters(new InputFilter[] {new InputFilter});
		//	hintSearch.setInputType(InputType.TYPE_CLASS_NUMBER);
	}
		hintSearch.setHint(hint);
		SubdivName.setText(CommonPref.getUserDetails(getApplicationContext()).get_SubdivName());
		DivName.setText(CommonPref.getUserDetails(getApplicationContext()).get_DivName());
		mruName.setText(CommonPref.getUserDetails(getApplicationContext()).get_MRUNo());
		
		if(lableSearch.getText().equals("Account Number :")){
			//hintSearch.setInputType(InputType.TYPE_CLASS_TEXT);
		}
		btnSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String position = "", value = "";
				value = hintSearch.getText().toString();
				
				if(lableSearch.getText().equals("Account Number :")){
					position = "ACT_NO";
					hintSearch.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
				}
				else if(lableSearch.getText().equals("Consumer Number :")){
					position = "CON_ID";
					lableSearch.setInputType(InputType.TYPE_CLASS_NUMBER);

				}
				else if(lableSearch.getText().equals("Meter Number :")){
					position = "METER_NO";
				}
				else if(lableSearch.getText().equals("Mobile Number :")){
					lableSearch.setInputType(InputType.TYPE_CLASS_NUMBER);
					position = "CONTACT_NUM";
				}
				if(!(value.isEmpty())|| !(value.equals(""))){
					Intent intent=null;
					if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
						//intent = new Intent(getBaseContext(), ConsumerActivity.class);
				//	intent = new Intent(getBaseContext(), ConsumerActivity4A11specific.class);
						intent = new Intent(getBaseContext(), ConsumerActivity4A11new.class);

					}else{
					//	intent = new Intent(getBaseContext(), ConsumerActivity.class);
						intent = new Intent(getBaseContext(), ConsumerActivity4A11new.class);
					//	intent = new Intent(getBaseContext(), ConsumerActivity4A11new.class);
					}
				intent.putExtra("POS", position);
				intent.putExtra("VALUE", value);
				startActivity(intent);
				overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_down);
				finish();
				}else{
					Toast.makeText(getApplicationContext(), "Please Enter Valid Value", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
