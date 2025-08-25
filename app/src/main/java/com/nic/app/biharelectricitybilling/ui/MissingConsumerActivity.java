package com.nic.app.biharelectricitybilling.ui;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.db.DataBaseHelper;
import com.nic.app.biharelectricitybilling.db.WebServiceHelper;
import com.nic.app.biharelectricitybilling.entity.GPSTracker;
import com.nic.app.biharelectricitybilling.entity.MRUDetails;
import com.nic.app.biharelectricitybilling.entity.UserDetails;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.Utiilties;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
public class MissingConsumerActivity extends Activity {
	DataBaseHelper localDBHelper;
	private ActionBar actionBar;
	String fname;
	Button btnContinue;
	String Unmeter="";
	TextView tvAcNo, tvConNo, tvMeterNo, tvConName, tvAddress, tvCategory,
			tvLoad,tvOldMruNo;
	EditText etMobileNo, etDTNo, etSideAcountNo;
	String stringMobileNo, stringDtNo;
	MRUDetails mruList;
	File myDir=null;
	GPSTracker gps;
	private static String IMAGE_FILE_PATH;
	private static String CROP_IMAGE_FILE_PATH;
	private static String IMAGE_NAME;
	private static final int CAMERA_REQUEST = 1777;
	final int PIC_CROP = 2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_missing_consumer);

		// Database Opening
		localDBHelper = new DataBaseHelper(MissingConsumerActivity.this);
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
				.fromHtml("<font color='#FFFFFF'>Missing Consumer Details </font>"));
		String pos = getIntent().getStringExtra("POS");
		String value = getIntent().getStringExtra("VALUE");

		initialization();
		mruList = localDBHelper.getMissingMRU(pos, value);

		if (mruList == null) {

			AlertDialogForNoResultFound();

		} else  {
			valueInitialization();
		}

		IMAGE_FILE_PATH = "/BEB/FullImage/"
				+ mruList.get_OLD_BOOK_NO() + "/";
		CROP_IMAGE_FILE_PATH = "/BEB/CropImage/"
				+  mruList.get_OLD_BOOK_NO() + "/";

	}

	public void AlertDialogForNoResultFound() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MissingConsumerActivity.this);
		// Setting Dialog Title
		alertDialog.setTitle("Result Not Found!!");
		// Setting Dialog Message
		alertDialog
				.setMessage("No Consumer Details had been found regarding your search.\n Please Try Again...");
		// Setting Icon to Dialog
		// alertDialog.setIcon(R.drawable.bulb_1);
		// Setting Positive "Yes" Button
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Intent intent = new Intent(getBaseContext(),
								BillingHomeActivity.class);
						startActivity(intent);
						finish();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	private void valueInitialization() {
		// TODO Auto-generated method stub
			MRUDetails mru = mruList;
			tvAcNo.setText(mru.get_ACT_NO());
			tvConNo.setText(mru.get_CON_ID());
			tvMeterNo.setText(mru.get_METER_NO());
			tvConName.setText(mru.get_CNAME());
			tvAddress.setText(mru.get_BILL_ADDRESS());
			tvCategory.setText(mru.get_CATEGORY());
			tvLoad.setText(mru.get_LOAD());
			tvOldMruNo.setText(mru.get_OLD_BOOK_NO());
			etMobileNo.setText(mru.get_CONTACT_NUM().trim());
			etDTNo.setText(mru.get_DT_NO().trim());
			stringMobileNo = mru.get_CONTACT_NUM().trim();
			stringDtNo = mru.get_DT_NO().trim();
			IMAGE_NAME = mru.get_CON_ID() + ".jpg";
		    Unmeter=mru.get_METR_UNMETER();
		if(Unmeter.equals("UM")){
			//IsmeterCorrect.setVisibility(View.GONE);
		}else{
			//IsmeterCorrect.setVisibility(View.VISIBLE);
		}


	}

	private void initialization() {
		// TODO Auto-generated method stub

		tvAcNo = (TextView) findViewById(R.id.tv_ac_no);
		tvConNo = (TextView) findViewById(R.id.tv_con_no);
		tvMeterNo = (TextView) findViewById(R.id.tv_meter_no);
		tvConName = (TextView) findViewById(R.id.tv_con_name);
		tvAddress = (TextView) findViewById(R.id.tv_address);
		tvCategory = (TextView) findViewById(R.id.tv_category);
		tvLoad = (TextView) findViewById(R.id.tv_load);
		//IsmeterCorrect=(LinearLayout)findViewById(R.id.ismetercorrect) ;
		tvOldMruNo = (TextView) findViewById(R.id.tv_old_mru);
		etMobileNo = (EditText) findViewById(R.id.et_mobileNo);
		etDTNo = (EditText) findViewById(R.id.et_dtNo);
		etSideAcountNo = (EditText) findViewById(R.id.et_side_ac_no);

		btnContinue = (Button) findViewById(R.id.btn_continue);

		btnContinue.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Unmeter.equals("UM")) {
					if (!(etMobileNo.getText().toString().equals(stringMobileNo))
							|| !(etDTNo.getText().toString().equals(stringDtNo))) {
						// This line will call due to some changes in editbox
						if (!Utiilties.isOnline(MissingConsumerActivity.this)) {
							// Offline code
							long c = localDBHelper.updateMissingConMru(etMobileNo.getText()
											.toString(), etDTNo.getText().toString(),
									tvAcNo.getText().toString(), "Y");
							if (c > 0) {
								Toast.makeText(getApplicationContext(),
										"Update in Local DataBase",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(getApplicationContext(),
										"Error in Local Database",
										Toast.LENGTH_LONG).show();
							}
							//callCameraIntent();
							Intent intent = new Intent(getBaseContext(),
									MeterReadingForMissingActivity.class);
							intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
									.toString().trim());
							intent.putExtra("FLAG", "0");
							startActivity(intent);
							finish();
						} else {
							// Online Code
							new updateDtNo().execute(etMobileNo.getText()
											.toString(), etDTNo.getText().toString(),
									tvAcNo.getText().toString());
						}
					} else {

						Intent intent = new Intent(getBaseContext(),
								MeterReadingForMissingActivity.class);
						intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
								.toString().trim());
						intent.putExtra("FLAG", "0");
						startActivity(intent);
						finish();
						//callCameraIntent();
					}
				}
				else if(Unmeter.equals("MS")){

					if (!(etMobileNo.getText().toString().equals(stringMobileNo))
							|| !(etDTNo.getText().toString().equals(stringDtNo))) {
						// This line will call due to some changes in editbox
						if (!Utiilties.isOnline(MissingConsumerActivity.this)) {
							// Offline code
							long c = localDBHelper.updateMissingConMru(etMobileNo.getText()
											.toString(), etDTNo.getText().toString(),
									tvAcNo.getText().toString(), "Y");
							if (c > 0) {
								Toast.makeText(getApplicationContext(),
										"Update in Local DataBase",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(getApplicationContext(),
										"Error in Local Database",
										Toast.LENGTH_LONG).show();
							}
							callCameraIntent();
							/*Intent intent = new Intent(getBaseContext(),
									MeterReadingStatusActivity.class);
							intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
									.toString().trim());
							intent.putExtra("FLAG", "1");
							startActivity(intent);
							finish();*/
						} else {
							// Online Code
							new updateDtNo().execute(etMobileNo.getText()
											.toString(), etDTNo.getText().toString(),
									tvAcNo.getText().toString());
						}
					} else {

						/*Intent intent = new Intent(getBaseContext(),
								MeterReadingStatusActivity.class);
						intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
								.toString().trim());
						intent.putExtra("FLAG", "1");
						startActivity(intent);
						finish();*/
						callCameraIntent();
					}

				}

			}
		});


	}


	private class updateDtNo extends AsyncTask<String, Void, String> {

		public updateDtNo() {

		}

		private final ProgressDialog dialog = new ProgressDialog(
				MissingConsumerActivity.this);

		private final AlertDialog alertDialog = new AlertDialog.Builder(
				MissingConsumerActivity.this).create();

		@Override
		protected void onPreExecute() {
			this.dialog.setCanceledOnTouchOutside(false);
			this.dialog
					.setMessage("Please wait. \n Updating DT and Mobile Number...");
			this.dialog.setCancelable(false);
			this.dialog.show();
		}

		@Override
		protected String doInBackground(String... param) {
			UserDetails user = CommonPref
					.getUserDetails(getApplicationContext());
			String res1 = WebServiceHelper.UpdateMRU(param[0], param[1],
					param[2], user);

			return res1;
		}

		@Override
		protected void onPostExecute(String result) {

			if (this.dialog.isShowing()) {

				this.dialog.dismiss();
				alertDialog.setTitle("Updating MRU..");
				if (result != null) {
					alertDialog.setMessage("Synchronizing...");
					alertDialog.show();
					if (result.trim().equalsIgnoreCase("Update Successfull")) {
						long c = localDBHelper.updateMissingConMru(etMobileNo.getText()
										.toString(), etDTNo.getText().toString(),
								tvAcNo.getText().toString(), "N");
						if (c > 0) {
							Toast.makeText(getApplicationContext(), result,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(getApplicationContext(),
									"Error in Local Database",
									Toast.LENGTH_LONG).show();
						}
					}

				} else {
					long c = localDBHelper.updateMissingConMru(etMobileNo.getText()
							.toString(), etDTNo.getText().toString(), tvAcNo
							.getText().toString(), "Y");
					Toast.makeText(getApplicationContext(), "Error in Network",
							Toast.LENGTH_LONG).show();
				}

				alertDialog.cancel();
				if (Unmeter.equals("UM")) {
					Intent intent = new Intent(getBaseContext(),
							MeterReadingForMissingActivity.class);
					intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
							.toString().trim());
					intent.putExtra("FLAG", "0");
					startActivity(intent);
					finish();
				} else if (Unmeter.equals("MS")) {
					callCameraIntent();
				}
			}

		}
	}

	public void callCameraIntent() {
			gps = new GPSTracker(MissingConsumerActivity.this);
			// check if GPS enabled
			if (gps.canGetLocation()) {
				/*Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				*//*create instance of File with name img.jpg*//*
				File file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				startActivityForResult(intent, 1);*/
				if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q){
					Intent iCamera = new Intent(getApplicationContext(), CameraActivity.class);
					iCamera.putExtra("KEY_PIC", "6");
					iCamera.putExtra("idname", "meterphoto");
					startActivityForResult(iCamera, 1);
				}
				else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
					/*Intent intent = new Intent(this, ScanActivity.class);
					intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);
					startActivityForResult(intent, 5);*/
				}else {
					Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
					File file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
					startActivityForResult(intent, 1);
				}
			} else {
				// can't get location
				// GPS or Network is not enabled
				// Ask user to enable GPS/network in settings
				gps.showSettingsAlert();
			}
		}

	/**
	 * Retrives the result returned from selecting image, by invoking the method
	 * <code>selectImageFromGallery()</code>
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
			// Get our saved file into a bitmap object:
			if (requestCode == 1) {
				if (resultCode == RESULT_CANCELED) {
				} else if (resultCode == RESULT_OK) {

					if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q) {
						byte[] imgData = data.getByteArrayExtra("CapturedImage");
						Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0,
								imgData.length);
						// Uri selectedImage = data.getData();
						try {
							/*the user's device may not support cropping*/
							cropCapturedImage(getImageUri(getApplicationContext(), bmp));

						} catch (ActivityNotFoundException aNFE) {
							//display an error message if user device doesn't support
							String errorMessage = "Sorry - your device doesn't support the crop action!";
							Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
							toast.show();
						}

					} else {
						//create instance of File with same name we created before to get image from storage
						File file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
						//Crop the captured image using an other intent
						try {
							/*the user's device may not support cropping*/
							cropCapturedImage(Uri.fromFile(file));
						} catch (ActivityNotFoundException aNFE) {
							//display an error message if user device doesn't support
							String errorMessage = "Sorry - your device doesn't support the crop action!";
							Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
							toast.show();
						}
					}
				}
			}
			if (requestCode == 5) {/*
				Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
				Bitmap bitmap1 = null;
				try {
					bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
					getContentResolver().delete(uri, null, null);
					int i=bitmap1.getByteCount();
					Bitmap bitmap2;
					if(i>250000){
						bitmap2 = ShrinkBitmap(bitmap1, 300, 200);
					}else {
						bitmap2=bitmap1;
					}
					SaveImage(bitmap2);

				} catch (IOException e) {
					e.printStackTrace();
				}*/
			}
			if (requestCode == 2) {
				if (resultCode == RESULT_CANCELED) {
				} else if (resultCode == RESULT_OK) {
					//Create an instance of bundle and get the returned data
					Bitmap bitmap = null;
					try {
						Bundle extras = data.getExtras();
						//get the cropped bitmap from extras
						Bitmap thePic = extras.getParcelable("data");
						int i = thePic.getByteCount();
						Bitmap bitmap2;
						if (i > 250000) {
							// bitmap2 = ShrinkBitmap(thePic, 300, 200);
							bitmap2 = getResizedBitmap(thePic, 250);
						} else {
							bitmap2 = thePic;
						}
						SaveImage(bitmap2);
					} catch (Exception ex) {
						try {
							// Utiilties.writeIntoLog(Log.getStackTraceString(ex));
							bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
							int i = bitmap.getByteCount();
							Bitmap bitmap2;
							if (i > 250000) {
								// bitmap2 = ShrinkBitmap(bitmap, 300, 200);
								bitmap2 = getResizedBitmap(bitmap, 250);
							} else {
								bitmap2 = bitmap;
							}
							SaveImage(bitmap2);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

	}
	public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth,
													 int reqHeight) { // BEST QUALITY MATCH
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		// Calculate inSampleSize, Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		int inSampleSize = 1;
		if (height > reqHeight) {
			inSampleSize = Math.round((float) height / (float) reqHeight);
		}
		int expectedWidth = width / inSampleSize;

		if (expectedWidth > reqWidth) {
			// if(Math.round((float)width / (float)reqWidth) > inSampleSize) //
			// If bigger SampSize..
			inSampleSize = Math.round((float) width / (float) reqWidth);
		}
		options.inSampleSize = inSampleSize;
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}
	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

		SimpleDateFormat m_sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String  m_curentDateandTime = m_sdf.format(new Date());

		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, m_curentDateandTime, null);
		return Uri.parse(path);
	}
	public void cropCapturedImage(Uri picUri){
		//call the standard crop action intent
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		//indicate image type and Uri of image
		cropIntent.setDataAndType(picUri, "image/*");
		//set crop properties
		cropIntent.putExtra("crop", "true");
		//indicate aspect of desired crop
		cropIntent.putExtra("aspectX", 1.5);
		cropIntent.putExtra("aspectY", 1);
		//indicate output X and Y
		cropIntent.putExtra("outputX", 150);
		cropIntent.putExtra("outputY", 100);
		//retrieve data on return
		cropIntent.putExtra("return-data", true);
		//start the activity - we handle returning in onActivityResult
		startActivityForResult(cropIntent, 2);
	}
	private void SaveImage(Bitmap finalBitmap) {
		String success = "";
		/*String id=CommonPref.getUserDetails(getApplicationContext())
				.get_MRUNo();*/
		CROP_IMAGE_FILE_PATH = "/BEB/CropImage/" +mruList.get_OLD_BOOK_NO() + "/";
		//String root = Environment.getExternalStorageDirectory().toString();
		//File myDir = new File(root +CROP_IMAGE_FILE_PATH);

		String root=null;

		if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.Q){
			//  root = Environment.getRootDirectory().toString();
			// root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
			root= getApplicationContext().getExternalFilesDir(CROP_IMAGE_FILE_PATH).toString();
			myDir = new File(root);
		}else {
			root = Environment.getExternalStorageDirectory().toString();
			myDir = new File(root+"/"+CROP_IMAGE_FILE_PATH);
		}


		myDir.mkdirs();
		Random generator = new Random();
		int n = 10000;
		n = generator.nextInt(n);
		fname = IMAGE_NAME;
		File file = new File (myDir, fname);
		if (file.exists ()) file.delete ();
		try {
			FileOutputStream out = new FileOutputStream(file);
			finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			success = "OK";
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*if (success.matches("OK")) {
			// create class object
			gps = new GPSTracker(MissingConsumerActivity.this);
			double latitude = 0.0;
			double longitude = 0.0;
			long dateTime = 0;

			// check if GPS enabled
			if (gps.canGetLocation()) {

				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				dateTime = gps.getTime();
				String date = Utiilties.getDate(dateTime, "dd/MM/yyyy");
				if (latitude > 0.0 && longitude > 0.0) {

					long c = localDBHelper.savePhoto(String.valueOf(latitude),
							String.valueOf(longitude), CROP_IMAGE_FILE_PATH
									+ fname, tvAcNo.getText().toString()
									.trim(),date);
					if (c > 0) {
						Intent intent = new Intent(getBaseContext(),
								MeterReadingStatusActivity.class);
						intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
								.toString().trim());
						intent.putExtra("FLAG", "0");
						startActivity(intent);
						finish();
					} else {
						Toast.makeText(getApplicationContext(),
								"Error in Local Database", Toast.LENGTH_LONG)
								.show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Please Wait a while until GPS is stable", Toast.LENGTH_LONG)
							.show();
				}
			} else {
				// can't get location
				// GPS or Network is not enabled
				// Ask user to enable GPS/network in settings
				gps.showSettingsAlert();
			}

		} else {
			Toast.makeText(getApplicationContext(), success, Toast.LENGTH_LONG)
					.show();
		}*/
		if (success.equals("OK")) {
			// create class object
			gps = new GPSTracker(MissingConsumerActivity.this);
			double latitude = 0.0;
			double longitude = 0.0;
			long dateTime = 0;

			// check if GPS enabled
			if (gps.canGetLocation()) {
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				dateTime = gps.getTime();
				String date = Utiilties.getDate(dateTime, "dd/MM/yyyy");
				if (latitude > 0.0 && longitude > 0.0) {
					long c = localDBHelper.savePhotoInMissingConMru(String.valueOf(latitude),
							String.valueOf(longitude), CROP_IMAGE_FILE_PATH
									+ fname, tvAcNo.getText().toString()
									.trim(), date,etSideAcountNo.getText().toString().trim());
					if (c > 0) {
						Intent intent = new Intent(getBaseContext(),
								MeterReadingForMissingActivity.class);
						intent.putExtra("ACCOUNT_NO", tvAcNo.getText()
								.toString().trim());
						intent.putExtra("FLAG", "1");
						startActivity(intent);
						finish();
					} else {
						Toast.makeText(getApplicationContext(),
								"Error in Local Database", Toast.LENGTH_LONG)
								.show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Please Wait a while until GPS is stable", Toast.LENGTH_LONG)
							.show();
				}

			} else {
				// can't get location
				// GPS or Network is not enabled
				// Ask user to enable GPS/network in settings
				gps.showSettingsAlert();
			}

		} else {
			Toast.makeText(getApplicationContext(), success, Toast.LENGTH_LONG).show();
		}


	}
	Bitmap ShrinkBitmap(Bitmap file, int width, int height){
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		Bitmap bitmap = file;
		int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
		int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);
		if (heightRatio > 1 || widthRatio > 1)
		{
			if (heightRatio > widthRatio)
			{
				bmpFactoryOptions.inSampleSize = heightRatio;
			} else {
				bmpFactoryOptions.inSampleSize = widthRatio;
			}
		}

		bmpFactoryOptions.inJustDecodeBounds = false;
		// bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] imageInByte = stream.toByteArray();
		//this gives the size of the compressed image in kb
		long lengthbmp = imageInByte.length / 1024;

		try {
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream("/sdcard/mediaAppPhotos/compressed_new.jpg"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return bitmap;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			// do something on back.
			//Display alert message when back button has been pressed
			Intent i = new Intent(MissingConsumerActivity.this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
		int width = image.getWidth();
		int height = image.getHeight();
		float bitmapRatio = (float) width / (float) height;
		if (bitmapRatio > 1) {
			width = maxSize;
			height = (int) (width / bitmapRatio);
		} else {
			height = maxSize;
			width = (int) (height * bitmapRatio);
		}
		return Bitmap.createScaledBitmap(image, width, height, true);
	}
}


