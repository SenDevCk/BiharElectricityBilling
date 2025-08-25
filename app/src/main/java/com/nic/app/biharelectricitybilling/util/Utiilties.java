package com.nic.app.biharelectricitybilling.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.MotionEventCompat;

import com.analogics.encryption.EncryptData;
import com.analogics.library.printer.ImageAlignment;
import com.analogics.library.printer.ImageScale;
import com.analogics.library.printer.PrtGraphics2T;
import com.analogics.util.HexSupport;
import com.nic.app.biharelectricitybilling.R;
import com.nic.app.biharelectricitybilling.contentProvider.BillingIMEIProvider;
import com.nic.app.biharelectricitybilling.ui.MainActivity;


public class Utiilties {
    public static String APP_DIR = MainActivity.SDCARD + "/Logfile";
    public static String LOG_FILE_PATH = APP_DIR + "/ myapp_log.txt";
    private static int[][] Floyd16x16 = null;

    /*	static {
            Floyd16x16 = new int[][]{new int[]{0, TransportMediator.FLAG_KEY_MEDIA_NEXT, 32, 160, 8, 136, 40, 168, 2, TransportMediator.KEYCODE_MEDIA_RECORD, 34, 162, 10, 138, 42, 170}, new int[]{192, 64, 224, 96, 200, 72, 232, 104, 194, 66, 226, 98, 202, 74, 234, 106}, new int[]{48, 176, 16, 144, 56, 184, 24, 152, 50, 178, 18, 146, 58, 186, 26, 154}, new int[]{240, 112, 208, 80, 248, 120, 216, 88, 242, 114, 210, 82, 250, 122, 218, 90}, new int[]{12, 140, 44, 172, 4, 132, 36, 164, 14, 142, 46, 174, 6, 134, 38, 166}, new int[]{204, 76, 236, 108, 196, 68, 228, 100, 206, 78, 238, 110, 198, 70, 230, 102}, new int[]{60, 188, 28, 156, 52, 180, 20, 148, 62, 190, 30, 158, 54, 182, 22, 150}, new int[]{252, 124, 220, 92, 244, 116, 212, 84, 254, TransportMediator.KEYCODE_MEDIA_PLAY, 222, 94, 246, 118, 214, 86}, new int[]{3, 131, 35, 163, 11, 139, 43, 171, 1, 129, 33, 161, 9, 137, 41, 169}, new int[]{195, 67, 227, 99, 203, 75, 235, 107, 193, 65, 225, 97, 201, 73, 233, 105}, new int[]{51, 179, 19, 147, 59, 187, 27, 155, 49, 177, 17, 145, 57, 185, 25, 153}, new int[]{243, 115, 211, 83, 251, 123, 219, 91, 241, 113, 209, 81, 249, 121, 217, 89}, new int[]{15, 143, 47, 175, 7, 135, 39, 167, 13, 141, 45, 173, 5, 133, 37, 165}, new int[]{207, 79, 239, 111, 199, 71, 231, 103, 205, 77, 237, 109, 197, 69, 229, 101}, new int[]{63, 191, 31, 159, 55, 183, 23, 151, 61, 189, 29, 157, 53, 181, 21, 149}, new int[]{254, TransportMediator.KEYCODE_MEDIA_PAUSE, 223, 95, 247, 119, 215, 87, 253, 125, 221, 93, 245, 117, 213, 85}};
        }*/
    public Utiilties() {
        // TODO Auto-generated constructor stub
    }

    public static void ShowMessage(Context context, String Title, String Message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(Title);
        alertDialog.setMessage(Message);
        alertDialog.show();
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static void showAlet(final Context context) {

        if (Utiilties.isOnline(context) == false) {
            AlertDialog.Builder ab = new AlertDialog.Builder(context);
            ab.setCancelable(false);
            ab.setMessage(Html
                    .fromHtml("<font color=#000000>Internet Connection is not avaliable..Please Turn ON Network Connection OR Continue With Off-line Mode..\nTo Turn ON Network Connection Press Yes Button else To Continue With Off-Line Mode Press No Button..</font>"));
            ab.setPositiveButton("Turn On Network Connection",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            GlobalVariables.isOffline = false;
                            Intent I = new Intent(
                                    android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                            context.startActivity(I);
                        }
                    });
            ab.setNegativeButton("Continue Offline",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {

                            GlobalVariables.isOffline = true;
                        }
                    });

            ab.create().getWindow().getAttributes().windowAnimations = R.style.AppBaseTheme;

            ab.show();
        } else {

            GlobalVariables.isOffline = false;
            // new CheckUpdate().execute();
        }

    }


    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected() == true);
    }

    public static Bitmap GenerateThumbnail(Bitmap imageBitmap,
                                           int THUMBNAIL_HEIGHT, int THUMBNAIL_WIDTH) {

        Float width = new Float(imageBitmap.getWidth());
        Float height = new Float(imageBitmap.getHeight());
        Float ratio = width / height;
        Bitmap CompressedBitmap = Bitmap.createScaledBitmap(imageBitmap,
                (int) (THUMBNAIL_HEIGHT * ratio), THUMBNAIL_HEIGHT, false);
        return CompressedBitmap;
    }

    public static Bitmap DrawText(Bitmap mBitmap, String displaytext1,
                                  String displaytext2, String displaytext3, String displaytext4) {
        Bitmap bmOverlay = Bitmap.createBitmap(mBitmap.getWidth(),
                mBitmap.getHeight(), Bitmap.Config.ARGB_4444);
        // create a canvas on which to draw
        Canvas canvas = new Canvas(bmOverlay);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(16);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setFakeBoldText(false);
        paint.setShadowLayer(1, 0, 0, Color.BLACK);

        // if the background image is defined in main.xml, omit this line
        canvas.drawBitmap(mBitmap, 0, 0, paint);

        canvas.drawText(displaytext1, 10, mBitmap.getHeight() - 30, paint);
        canvas.drawText(displaytext2, 10, mBitmap.getHeight() - 10, paint);

        canvas.drawText(displaytext3, 10, mBitmap.getHeight() - 50, paint);

        canvas.drawText(displaytext4, 10, mBitmap.getHeight() - 70, paint);
        // set the bitmap into the ImageView
        return bmOverlay;
    }

    public static Object deserialize(byte[] data) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            return is.readObject();
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getDateString() {
        SimpleDateFormat postFormater = new SimpleDateFormat(
                "MMMM dd, yyyy hh:mm a");
        String newDateStr = postFormater.format(Calendar.getInstance()
                .getTime());
        return newDateStr;
    }

    public static String getDateString(String Formats) {
        SimpleDateFormat postFormater = new SimpleDateFormat(Formats);

        String newDateStr = postFormater.format(Calendar.getInstance()
                .getTime());
        return newDateStr;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static void writeIntoLog(String data) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(LOG_FILE_PATH, true);
            BufferedWriter buffer = new BufferedWriter(fw);
            buffer.append(data + "\n");
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Bitmap DrawText(Activity activity, Bitmap mBitmap, String displaytext1,
                                  String displaytext2, String displaytext3, String displaytext4) {
        Bitmap bmOverlay = Bitmap.createBitmap(mBitmap.getWidth(),
                mBitmap.getHeight(), Bitmap.Config.ARGB_4444);
        // create a canvas on which to draw
        Canvas canvas = new Canvas(bmOverlay);

        Paint paint = new Paint();
        paint.setColor(activity.getResources().getColor(R.color.colorAccent));
        paint.setTextSize(40);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setFakeBoldText(false);
        paint.setShadowLayer(1, 0, 0, Color.BLACK);
        // if the background image is defined in main.xml, omit this line
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        canvas.drawText(displaytext1, 10, mBitmap.getHeight() - 100, paint);
        canvas.drawText(displaytext2, 10, mBitmap.getHeight() - 50, paint);
        canvas.drawText(displaytext3, 10, mBitmap.getHeight() - 150, paint);
        canvas.drawText(displaytext4, 10, mBitmap.getHeight() - 200, paint);
        // set the bitmap into the ImageView
        return bmOverlay;
    }

    public static boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void displayPromptForEnablingGPS(final Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Do you want open GPS setting?";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                                //activity.finish();
                                Toast.makeText(activity, "Please enable Gps", Toast.LENGTH_SHORT).show();
                            }
                        });
        builder.create().show();
    }

    public static boolean isfrontCameraAvalable() {
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (Camera.CameraInfo.CAMERA_FACING_FRONT == info.facing) {
                return true;
            }
        }
        return false;
    }

    public static String getCurrentDateWithTime() throws ParseException {

        SimpleDateFormat f = new SimpleDateFormat("MMM d,yyyy HH:mm");
        Date date = null;
        date = f.parse(getDateString());
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM d,yyyy HH:mm a");
        String dateString = formatter.format(date);
        return dateString;
    }

    public static void saveImeiMyApp(Activity activity, String imei, int isvarified) {
        try {
            ContentValues values = new ContentValues();
            values.put(BillingIMEIProvider.name, imei);
            values.put(BillingIMEIProvider.vrified, isvarified);
            Uri uri = activity.getContentResolver().insert(BillingIMEIProvider.CONTENT_URI, values);
            Toast.makeText(activity, uri.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        Boolean app_installed = false;
        try {
            packageManager.getPackageInfo(packageName, packageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            app_installed = false;
        }
        return app_installed;
    }

    public static Cursor getAppContentProviderData(Activity activity, String cus_uri) {
        Cursor cursor = activity.getContentResolver().query(Uri.parse(cus_uri), null, null, null, null);
        return cursor;
    }
	/*public static String getIMEI_forAndroid10(Activity activity) {
		String imei=null;
		int is_imei_var=0;
		if (Utiilties.isPackageInstalled("com.bih.nic.e_wallet", activity.getPackageManager())) {
			Cursor cursor = Utiilties.getAppContentProviderData(activity, "content://com.nic.ewallet.contentProvider.EwalletIMEIProvider/e_imei");
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					imei = cursor.getString(cursor.getColumnIndex(BillingIMEIProvider.name));
					is_imei_var = cursor.getInt(cursor.getColumnIndex(BillingIMEIProvider.vrified));
				}
				if (is_imei_var == 1) {
					Utiilties.saveImeiMyApp(activity, imei, 1);
				} else {
					imei = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
					Utiilties.saveImeiMyApp(activity, imei, 0);
				}
			} else {
				Utiilties.saveImeiMyApp(activity, imei, 0);
			}
		} else {
			imei = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
			if (Utiilties.getAppContentProviderData(activity, BillingIMEIProvider.URL).getCount() <= 0) {
				Utiilties.saveImeiMyApp(activity, imei, 0);
			}
		}
		return imei;
	}*/

    @SuppressLint("Range")
    public static String getIMEI_forAndroid10(Activity activity) {
        Cursor cursor_bill = null, cursor_ewallet = null;
        String imei_ret = null, imei_bill = null;

        try {
            // Check if data exists in BillingIMEIProvider
            if (Utiilties.getAppContentProviderData(activity, BillingIMEIProvider.URL).getCount() <= 0) {
                // Check if e-wallet is installed
                if (Utiilties.isPackageInstalled("com.bih.nic.e_wallet", activity.getPackageManager())) {
                    cursor_ewallet = Utiilties.getAppContentProviderData(activity, "content://com.nic.ewallet.contentProvider.EwalletIMEIProvider/e_imei");
                    if (cursor_ewallet != null && cursor_ewallet.getCount() > 0) {
                        while (cursor_ewallet.moveToNext()) {
                            imei_bill = cursor_ewallet.getString(cursor_ewallet.getColumnIndex("E_IMEI"));
                        }
                        Utiilties.saveImeiMyApp(activity, imei_bill, 0);
                    } else {
                        // Fallback to Android ID if no IMEI found
                        @SuppressLint("HardwareIds")
                        String imei2 = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                        Utiilties.saveImeiMyApp(activity, imei2, 0);
                    }
                } else {
                    // Fallback to Android ID if e-wallet is not installed
                    @SuppressLint("HardwareIds")
                    String imei2 = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                    Utiilties.saveImeiMyApp(activity, imei2, 0);
                }
            }

            // Retrieve IMEI from BillingIMEIProvider
            cursor_bill = Utiilties.getAppContentProviderData(activity, BillingIMEIProvider.URL);
            if (cursor_bill != null && cursor_bill.getCount() > 0) {
                while (cursor_bill.moveToNext()) {
                    imei_ret = cursor_bill.getString(cursor_bill.getColumnIndex("E_IMEI"));
                }
            }

        } finally {
            // Close cursors to avoid memory leaks
            if (cursor_ewallet != null) cursor_ewallet.close();
            if (cursor_bill != null) cursor_bill.close();
        }

        return imei_ret;
    }


    public static UUID makeUuid(String uuidString) {
        String[] parts = {
                uuidString.substring(0, 7),
                uuidString.substring(9, 12),
                uuidString.substring(14, 17),
                uuidString.substring(19, 22),
                uuidString.substring(24, 35)
        };
        long m1 = Long.parseLong(parts[0], 16);
        long m2 = Long.parseLong(parts[1], 16);
        long m3 = Long.parseLong(parts[2], 16);
        long lsb1 = Long.parseLong(parts[3], 16);
        long lsb2 = Long.parseLong(parts[4], 16);
        long msb = (m1 << 32) | (m2 << 16) | m3;
        long lsb = (lsb1 << 48) | lsb2;
        return new UUID(msb, lsb);
    }

    public static Bitmap decodeSampledBitmapFromBitmap(Bitmap bm, int reqWidth) {
        float scaleSize;
        if (bm.getWidth() > reqWidth) {
            scaleSize = ((float) bm.getWidth()) / ((float) reqWidth);
        } else {
            scaleSize = 1.0f;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(1.0f / scaleSize, 1.0f / scaleSize);
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    }

    public static byte[] convert(Bitmap bm) {
        int oldWidth = bm.getWidth();
        int height = bm.getHeight();
        int[] intPixels = new int[(oldWidth * height)];
        bm.getPixels(intPixels, 0, oldWidth, 0, 0, oldWidth, height);
        int newWidth = ((oldWidth - 1) / 8) + 1;
        //LogUtils.m6d(TAG, "newWidth = " + newWidth);
        byte[] bytePixels = new byte[(newWidth * height)];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < oldWidth; j++) {
                int y = (newWidth * i) + (j / 8);
                int z = 7 - (j % 8);
                if ((intPixels[(oldWidth * i) + j] & MotionEventCompat.ACTION_MASK) < Floyd16x16[i & 15][j & 15]) {
                    bytePixels[y] = (byte) (bytePixels[y] | (1 << z));
                }
            }
        }
        return bytePixels;
    }

    public static Bitmap getResizedBitmapImage(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = (float) newWidth / (float) width;
        float scaleHeight = (float) newHeight / (float) height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static byte[] Toarray1(Bitmap bmp) throws InterruptedException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            //Bitmap bmp = BitmapFactory.decodeFile(imagepath);
            Bitmap resizebmp = getResizedBitmapImage(bmp, 255, 384);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resizebmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            InputStream in = new ByteArrayInputStream(stream.toByteArray());
            BufferedInputStream buf = new BufferedInputStream(in);
            byte[] bMapArray = null;

            try {
                bMapArray = new byte[buf.available()];
                buf.read(bMapArray);
            } catch (IOException var18) {
                var18.printStackTrace();
            }

            Bitmap bMap = BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
            new PrtGraphics2T();
            ImageScale ImageScaleret = ImageScale.valueOf("SCALE_ONE_TO_ONE");
            ImageAlignment ImageAlignmentret = ImageAlignment.valueOf("IMAGE_CENTER");
            byte[] data1 = PrtGraphics2T.printImage(bMap, ImageScaleret, ImageAlignmentret);
            byte[] cmddata = PrtGraphics2T.printImageCommand(bMap, ImageScaleret, ImageAlignmentret);
            //	String output = (new EncryptData()).encryptData(address);
            HexSupport hex = new HexSupport();
            outputStream.write(cmddata);
            //outputStream.write(hex.toBytesFromHex(output));
            outputStream.write(data1);
        } catch (IOException var19) {
            var19.printStackTrace();
        }

        byte[] imagedata = outputStream.toByteArray();
        return imagedata;
    }

    public static boolean isEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MODEL.contains("sdk_gphone_x86_64")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.MANUFACTURER.contains("genymotion")
                || Build.MANUFACTURER.contains("Google")
                || Build.MANUFACTURER.contains("google")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("sdk_gphone64_arm64")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");
    }

    public static Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    public static  Bitmap getBitmapForAllVersions(Context context, File file){
        Bitmap bitmap=null;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                //Uri uri = Uri.parse(imageFiles[jcntr].getAbsolutePath());
                Uri uri = getFileUri(context, file);
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), uri));
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                Uri uri = getFileUri(context, file);
                bitmap = getBitmapFromUri(context, uri);
            } else {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return bitmap;
    }
    public static Uri getFileUri(Context context, File photoFile){
        Uri uri= FileProvider.getUriForFile(context,
                "com.nic.app.biharelectricitybilling.fileprovider", photoFile);
        return uri;
    }

    public static Bitmap getBitmapFromUri(Context context,Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
