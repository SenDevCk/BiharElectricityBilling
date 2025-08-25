package com.nic.app.biharelectricitybilling.util;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
public  class imageutils {
    public static Uri saveImage(Bitmap bitmap, Context context, String folderName,String filename,String subfoldername) throws FileNotFoundException {
        Uri uri=null;
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName+"/"+subfoldername);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.IS_PENDING, true);
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                saveImageToStream(bitmap, context.getContentResolver().openOutputStream(uri));
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                context.getContentResolver().update(uri, values, null, null);
            }
        } else {
           File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),folderName+File.separator+subfoldername);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File   imageFile = new File(dir.getAbsolutePath()
                    + File.separator
                    + filename+".jpg");
          //  Toast.makeText(context, ""+imageFile, Toast.LENGTH_SHORT).show();
            saveImageToStream(bitmap, new FileOutputStream(imageFile));
            if (imageFile.getAbsolutePath() != null) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());
                uri=  context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
             //   Toast.makeText(context, ""+imageFile.getAbsolutePath()+","+uri, Toast.LENGTH_SHORT).show();
          /*      if (uri != null) {
                    saveImageToStream(bitmap, context.getContentResolver().openOutputStream(uri));
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    context.getContentResolver().update(uri, values, null, null);
                }*/
            }
        }
        return uri;
    }
    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values;
    }

    private static void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void delete_Alluri(ArrayList<Uri> arrayList,Context context){
        for(int i=0;i<arrayList.size();i++) {
          //  Log.e("uri path",arrayList.get(i).getPath());
            File fdelete = new File(arrayList.get(i).getPath());
            if (fdelete.exists()) {
                fdelete.delete();
                if(fdelete.exists()){
                    context.deleteFile(fdelete.getName());
                }
            }
        }
    }
    public boolean copyFileFromUri(Context context, Uri fileUri)
    {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try
        {
            ContentResolver content = context.getContentResolver();
            inputStream = content.openInputStream(fileUri);

            File root = Environment.getExternalStorageDirectory();
            if(root == null){
                Log.d("error", "Failed to get root");
            }

            // create a directory
            File saveDirectory = new File(Environment.getExternalStorageDirectory()+File.separator+ "directory_name" +File.separator);
            // create direcotory if it doesn't exists
            saveDirectory.mkdirs();

            outputStream = new FileOutputStream( saveDirectory + "filename.extension"); // filename.png, .mp3, .mp4 ...
            if(outputStream != null){
                Log.e( "TAG", "Output Stream Opened successfully");
            }

            byte[] buffer = new byte[1000];
            int bytesRead = 0;
            while ( ( bytesRead = inputStream.read( buffer, 0, buffer.length ) ) >= 0 )
            {
                outputStream.write( buffer, 0, buffer.length );
            }
        } catch ( Exception e ){
            Log.e( "", "Exception occurred " + e.getMessage());
        } finally{

        }
        return true;
    }
}
