package com.nic.app.biharelectricitybilling.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.nic.app.biharelectricitybilling.entity.BillDetails;
import com.nic.app.biharelectricitybilling.entity.Block;
import com.nic.app.biharelectricitybilling.entity.Dist;
import com.nic.app.biharelectricitybilling.entity.Establishment_details;
import com.nic.app.biharelectricitybilling.entity.MRUDetails;
import com.nic.app.biharelectricitybilling.entity.PanchayatData;
import com.nic.app.biharelectricitybilling.entity.Report;
import com.nic.app.biharelectricitybilling.entity.TOLLA;
import com.nic.app.biharelectricitybilling.entity.UserDetails;
import com.nic.app.biharelectricitybilling.entity.VILLAGE;
import com.nic.app.biharelectricitybilling.entity.consumer_address;
import com.nic.app.biharelectricitybilling.entity.dconsumer_details;
import com.nic.app.biharelectricitybilling.entity.lk_md_details;
import com.nic.app.biharelectricitybilling.util.CommonPref;
import com.nic.app.biharelectricitybilling.util.Utiilties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Helper to the database, manages versions and creation
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    // The Android's default system path of your application database.
    private static String DB_PATH = "";// "/data/data/com.bih.nic.app.biharmunicipalcorporation/databases/";
    private static String DB_NAME = "BEB.sqlite";
    //private static String DB_NAME = "BEB";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        if (Build.VERSION.SDK_INT >= 29) {
            DB_PATH = context.getDatabasePath(DB_NAME).getPath();
        } else if (Build.VERSION.SDK_INT >= 21) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.myContext = context;
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.disableWriteAheadLogging();
        }
    }
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            // do nothing - database already exist
        } else {

            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {
                Utiilties.writeIntoLog(Log.getStackTraceString(e));
                throw new Error("Error copying database");

            }
        }

    }
    public boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        String myPath = null;
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                myPath = DB_PATH;
            } else {
                myPath = DB_PATH + DB_NAME;
            }
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {
        //    Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    public boolean databaseExist() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = null;

        if (Build.VERSION.SDK_INT >= 29) {
            outFileName = DB_PATH;
        } else {
            outFileName = DB_PATH + DB_NAME;
        }
        // Path to the just created empty db
   //     String outFileName = DB_PATH + DB_NAME;

        // Open the empty db as the output stream

        SQLiteDatabase checkDB = SQLiteDatabase.openDatabase(outFileName, null,
                SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READONLY);
        if (checkDB != null && checkDB.isOpen()) {
            checkDB.close(); // Always close before deleting
            File dbFile = new File(outFileName);
            dbFile.delete();
        }

         // Deletes the file
        OutputStream myOutput = new FileOutputStream(outFileName);
        this.getReadableDatabase().close();
        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        // Open the database
        String myPath = null;

        if (Build.VERSION.SDK_INT >= 29) {
            myPath = DB_PATH;
        } else {
            myPath = DB_PATH + DB_NAME;
        }
       // String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("onUpgrade","newVersion > oldVersion "+newVersion +" > "+ oldVersion);
        if (newVersion > oldVersion)
            try {
                copyDataBase();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error", "Error in database copy");
            }
    }

    public long getUserCount() {

        long x = 0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery("Select * from UserDetails", null);
            x = cur.getCount();
            cur.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return x;
    }
    public long insertImageDetails(String accno, String ImagePath, String Isuploaded) {
        long c = 0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            //db.execSQL("Delete from UserDetails");
            ContentValues values = new ContentValues();
            values.put("Acc_No", accno.trim());
            values.put("ImagePath", ImagePath);
            values.put("Uploaded", Isuploaded);
            c = db.insertWithOnConflict("Image_details", "Acc_No", values, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            // TODO: handle exception
        }
        return c;

    }
    public long insertimagedetails( String con_id,String mru_name,String ddmmyyyy, byte[] image) throws SQLiteException{
        SQLiteDatabase database = this.getWritableDatabase();
        long c=0;
        ContentValues cv = new  ContentValues();
        cv.put("con_id",    con_id);
        cv.put("mru_name",    mru_name);
        cv.put("RDMMYYYY",    ddmmyyyy);
        cv.put("Meter_photo",   image);
      c=  database.insert( "Billing_Image", null, cv );
      return c;
    }

    public long insertUserDetails(UserDetails result) {

        long c = 0;
        try {

            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL("Delete from UserDetails");
            ContentValues values = new ContentValues();
            values.put("UserID", result.get_UserID());
            values.put("UserName", result.get_UserName());
            values.put("Password", result.get_password());
            values.put("IMEI", result.get_IMEI());
            values.put("LastVisted", result.get_LastVisitedOn());
            values.put("MobileNo", result.get_MobileNo());
            values.put("SubdivId", result.get_SubdivId());
            values.put("SubdivName", result.get_SubdivName());
            values.put("DivId", result.get_DivId());
            values.put("DivName", result.get_DivName());
            values.put("MRUNo", result.get_MRUNo());
            values.put("Bill_Month", result.get_BillMonth());
            values.put("Bill_Year", result.get_BillYear());
            values.put("distcode", result.get_distcode());
            values.put("distname", result.get_distname());
            values.put("bill_agency", result.get_bill_agency());
            values.put("ocr_agency", result.get_ocr_agency());
            c = db.insert("UserDetails", null, values);

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            // TODO: handle exception
        }
        return c;

    }

    @SuppressLint("Range")
    public UserDetails getUserDetails(String userId, String pass) {
        UserDetails userInfo = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{userId.trim(), pass};
            Cursor cur = db.rawQuery(
                    "Select * from UserDetails WHERE UserID=? and Password=?",
                    params);
            int x = cur.getCount();
            // db1.execSQL("Delete from UserDetail");
            while (cur.moveToNext()) {
                userInfo = new UserDetails();
                userInfo.set_UserID(cur.getString(cur.getColumnIndex("UserID")));
                userInfo.set_UserName(cur.getString(cur
                        .getColumnIndex("UserName")));
                userInfo.set_password(cur.getString(cur
                        .getColumnIndex("Password")));
                userInfo.set_IMEI(cur.getString(cur.getColumnIndex("IMEI")));
                userInfo.set_LastVisitedOn(cur.getString(cur
                        .getColumnIndex("LastVisted")));
                userInfo.set_MobileNo(cur.getString(cur
                        .getColumnIndex("MobileNo")));
                userInfo.set_SubdivId(cur.getString(cur
                        .getColumnIndex("SubdivId")));
                userInfo.set_SubdivName(cur.getString(cur
                        .getColumnIndex("SubdivName")));
                userInfo.set_DivId(cur.getString(cur.getColumnIndex("DivId")));
                userInfo.set_DivName(cur.getString(cur
                        .getColumnIndex("DivName")));
                userInfo.set_MRUNo(cur.getString(cur.getColumnIndex("MRUNo")));
                userInfo.set_BillMonth(cur.getString(cur
                        .getColumnIndex("Bill_Month")));
                userInfo.set_BillYear(cur.getString(cur
                        .getColumnIndex("Bill_Year")));
                userInfo.set_distcode(cur.getString(cur
                        .getColumnIndex("distcode")));
                userInfo.set_distname(cur.getString(cur
                        .getColumnIndex("distname")));
                userInfo.set_bill_agency(cur.getString(cur
                        .getColumnIndex("bill_agency")));
                userInfo.set_ocr_agency(cur.getString(cur
                        .getColumnIndex("ocr_agency")));
                userInfo.set_isAuthenticated(true);

            }
            cur.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            userInfo = null;
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return userInfo;
    }

    public long deletemoiledtno(String Acc_no) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String[] DeleteWhere = new String[2];
            //	DeleteWhere[0] = pid;
            db.execSQL("Delete from mobile_dt_table where Act_no='" + Acc_no + "'");
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return c;
    }


    public long insertMru(ArrayList<MRUDetails> result, String Mruname) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("Delete from MRUDetails");
            db.execSQL("Delete from BillDetails");
            for (MRUDetails mru : result) {
                ContentValues values = new ContentValues();
                values.put("ACT_NO", mru.get_ACT_NO());
                values.put("BILL_ADDRESS", mru.get_BILL_ADDRESS());
                values.put("CATEGORY", mru.get_CATEGORY());
                values.put("CNAME", mru.get_CNAME());
                values.put("Cfathername", mru.get_FHNAME());
                values.put("CONTACT_NUM", mru.get_CONTACT_NUM());
                values.put("CON_ID", mru.get_CON_ID());
                values.put("DT_NO", mru.get_DT_NO());
                values.put("LOAD", mru.get_LOAD());
                values.put("METER_NO", mru.get_METER_NO());
                values.put("METR_UNMETER", mru.get_METR_UNMETER());
                values.put("OLD_CON_ID", mru.get_OLD_CON_ID());
                values.put("PHASE", mru.get_PHASE());
                values.put("RECYCLE_UNIT", mru.get_RECYCLE_UNIT());
                values.put("SECTION_ID", mru.get_SECTION_ID());
                values.put("NEW_BOOK_NO", Mruname.trim());
                values.put("SECTION_NAME", mru.get_SECTION_NAME());
                values.put("PRE_READ", mru.get_PREVIOUS_READ());
                values.put("POW_FACT", mru.get_POW_FACT());
                values.put("REC_DEM", mru.get_REC_DEM());
                values.put("APL_CONSUMER", mru.get_APL_CONSUMER());
                values.put("APL_BILLING_FLAG", mru.get_APL_BILLING_FLAG());
                values.put("is_address_updated", mru.get_Is_address_updated());
                values.put("previous_read_status", mru.get_Previous_read_stat());
                values.put("LK_limit", mru.get_Lk_Limit());
                values.put("gender", mru.get_Gender());
                values.put("purpose", mru.get_purpose());
                values.put("Last_bill_date", mru.get_LAST_BILL_DATE());
                //values.put("bookstatus","P");
                c = db.insert("MRUDetails", null, values);
            }
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }
    public long insertdconsumerdetails(ArrayList<dconsumer_details> result, String Mruname) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("Delete from DconsumerDetails");
            for (dconsumer_details mru : result) {
                ContentValues values = new ContentValues();
                values.put("conid", mru.getCon_id());
                values.put("Conname", mru.getCname());
                values.put("book_no",Mruname);
                values.put("fhname", mru.getCfname());
                values.put("subdivid", mru.getSub_div_id());
                values.put("dconnecteddate", mru.getDisconnecteddate());
               // values.put("isuploaded", "N");zxcx
                c = db.insert("DconsumerDetails", null, values);
            }
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }
    public long updatedconsumerdetails(String conid, String remarks) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("isuploaded", "Y");
                values.put("remarks",remarks);
            String[] whereArgs = new String[]{conid};
            c = db.update("DconsumerDetails", values, "conid=?", whereArgs);
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }
    @SuppressLint("Range")
    public ArrayList<dconsumer_details> getdisconnectedconsumer(String pos, String value) {
        ArrayList<dconsumer_details> mruList = new ArrayList<dconsumer_details>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery("Select * from DconsumerDetails WHERE isuploaded ='N'", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                dconsumer_details mru = new dconsumer_details();
                mru.setCon_id(cur.getString(cur.getColumnIndex("conid")));
                mru.setCname(cur.getString(cur.getColumnIndex("Conname")));
                mru.setCfname(cur.getString(cur.getColumnIndex("fhname")));
                mru.setDisconnecteddate(cur.getString(cur.getColumnIndex("dconnecteddate")));
                mruList.add(mru);
            }
            cur.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;
    }
    @SuppressLint("Range")
    public dconsumer_details getdisconnectedconsumerdetails(String pos, String value) {
        dconsumer_details mru=null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{value.trim()};
            Cursor cur = db.rawQuery("Select * from DconsumerDetails WHERE "+pos +"=?", params);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                mru = new dconsumer_details();
                mru.setCon_id(cur.getString(cur.getColumnIndex("conid")));
                mru.setCname(cur.getString(cur
                        .getColumnIndex("Conname")));
                mru.setCfname(cur.getString(cur.getColumnIndex("fhname")));
                mru.setDisconnecteddate(cur.getString(cur.getColumnIndex("dconnecteddate")));
            }
            cur.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mru;
    }
    @SuppressLint("Range")
    public ArrayList<dconsumer_details> getdisconnectedpendingconsumerdetails(String pos, String value) {
        ArrayList<dconsumer_details> mruList = new ArrayList<dconsumer_details>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery("Select * from DconsumerDetails WHERE isuploaded='Y'", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                dconsumer_details mru = new dconsumer_details();
                mru.setCon_id(cur.getString(cur.getColumnIndex("conid")));
                mru.setCname(cur.getString(cur.getColumnIndex("remarks")));
                mruList.add(mru);
            }
            cur.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;
    }
    public long deletedisremarks(String Acc_no) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("Delete from DconsumerDetails where conid='" + Acc_no + "'");
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return c;
    }
    @SuppressLint("Range")
    public ArrayList<dconsumer_details> getdisconnectedconsumerbyacc_no(String pos, String value) {
        ArrayList<dconsumer_details> mruList = new ArrayList<dconsumer_details>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{value.trim()};
            Cursor cur = db.rawQuery("Select * from DconsumerDetails WHERE isuploaded = 'N' and "+pos+" =?", params);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                dconsumer_details mru = new dconsumer_details();
                mru.setCon_id(cur.getString(cur.getColumnIndex("conid")));
                mru.setCname(cur.getString(cur
                        .getColumnIndex("Conname")));
                mru.setCfname(cur.getString(cur.getColumnIndex("fhname")));
                mru.setDisconnecteddate(cur.getString(cur.getColumnIndex("dconnecteddate")));
                mruList.add(mru);
            }
            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;
    }

    public long updateMrustatus(String MobileNo) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("bookstatus", "P");
            String[] whereArgs = new String[]{MobileNo};
            c = db.update("MRUDetails", values, "NEW_BOOK_NO=?", whereArgs);

            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }

    @SuppressLint("Range")
    public ArrayList<MRUDetails> getMRU(String pos, String value) {
        // PlaceDataSQL placeData = new PlaceDataSQL(MainActivity.this);
        ArrayList<MRUDetails> mruList = new ArrayList<MRUDetails>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String[] params = new String[]{value.trim()};
            Cursor cur = db.rawQuery("Select * from MRUDetails WHERE " + pos
                    + " =? ", params);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                MRUDetails mru = new MRUDetails();
                mru.set_ACT_NO(cur.getString(cur.getColumnIndex("ACT_NO")));
                mru.set_BILL_ADDRESS(cur.getString(cur
                        .getColumnIndex("BILL_ADDRESS")));
                mru.set_CATEGORY(cur.getString(cur.getColumnIndex("CATEGORY")));
                mru.set_CNAME(cur.getString(cur.getColumnIndex("CNAME")));
                mru.set_Cfathername(cur.getString(cur.getColumnIndex("Cfathername")));
                mru.set_CON_ID(cur.getString(cur.getColumnIndex("CON_ID")));
                mru.set_CONTACT_NUM(cur.getString(cur
                        .getColumnIndex("CONTACT_NUM")));
                mru.set_DT_NO(cur.getString(cur.getColumnIndex("DT_NO")));
                mru.set_LOAD(cur.getString(cur.getColumnIndex("LOAD")));
                mru.set_METER_NO(cur.getString(cur.getColumnIndex("METER_NO")));
                mru.set_METR_UNMETER(cur.getString(cur
                        .getColumnIndex("METR_UNMETER")));
                mru.set_OLD_CON_ID(cur.getString(cur
                        .getColumnIndex("OLD_CON_ID")));
                mru.set_PHASE(cur.getString(cur.getColumnIndex("PHASE")));
                mru.set_RECYCLE_UNIT(cur.getString(cur
                        .getColumnIndex("RECYCLE_UNIT")));
                mru.set_SECTION_ID(cur.getString(cur
                        .getColumnIndex("SECTION_ID")));
                mru.set_SECTION_NAME(cur.getString(cur
                        .getColumnIndex("SECTION_NAME")));
                mru.set_PREVIOUS_READ(cur.getString(cur
                        .getColumnIndex("PRE_READ")));
                mru.set_IS_SYNC(cur.getString(cur.getColumnIndex("IS_SYNC")));
                mru.set_Cur_Read(cur.getString(cur.getColumnIndex("Cur_Read")));
                mru.set_Reading_Status(cur.getString(cur
                        .getColumnIndex("Reading_Status")));
                mru.set_Read_Date(cur.getString(cur.getColumnIndex("Read_Date")));
                mru.set_Cycle_Complete(cur.getString(cur
                        .getColumnIndex("Cycle_Complete")));
                mru.set_Power_Factor(cur.getString(cur
                        .getColumnIndex("Power_Factor")));
                mru.set_Rec_Demand(cur.getString(cur
                        .getColumnIndex("Rec_Demand")));
                mru.set_Meter_Photo(cur.getString(cur
                        .getColumnIndex("Meter_Photo")));
                mru.set_Latitude(cur.getString(cur.getColumnIndex("Latitude")));
                mru.set_Longitude(cur.getString(cur.getColumnIndex("Longitude")));
                mru.set_Is_Pending_Bill(cur.getString(cur
                        .getColumnIndex("Is_Pending_Bill")));
                mru.set_POW_FACT(cur.getString(cur.getColumnIndex("POW_FACT")));
                mru.set_REC_DEM(cur.getString(cur.getColumnIndex("REC_DEM")));
                mru.set_isPrinted(cur.getString(cur.getColumnIndex("IsAlredyPrint")));
                mru.set_IsMeterChange(cur.getString(cur.getColumnIndex("IsMeterChange")));
                mru.set_MeterNo(cur.getString(cur.getColumnIndex("MeterNo")));
                mru.set_Previous_read_stat(cur.getString(cur.getColumnIndex("previous_read_status")));
                mru.setKwhReading(cur.getString(cur.getColumnIndex("kwh_read")));
                mru.setKvahReading(cur.getString(cur.getColumnIndex("kvah_read")));

                mruList.add(mru);
            }
            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;
    }
    @SuppressLint("Range")
    public ArrayList<MRUDetails> getMRUAPL(String pos, String value) {
        // PlaceDataSQL placeData = new PlaceDataSQL(MainActivity.this);
        ArrayList<MRUDetails> mruList = new ArrayList<MRUDetails>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{value.trim()};
            Cursor cur = db.rawQuery("Select * from MRUDetails WHERE APL_CONSUMER = 'Y' AND APL_BILLING_FLAG = 'N' AND ifnull(Is_Pending_Bill,'N') !='G' ", null);
            //	Cursor cur = db.rawQuery("Select * from MRUDetails WHERE APL_CONSUMER = 'Y' AND APL_BILLING_FLAG = 'N' ", null);

            int x = cur.getCount();
            while (cur.moveToNext()) {
                MRUDetails mru = new MRUDetails();
                mru.set_ACT_NO(cur.getString(cur.getColumnIndex("ACT_NO")));
                mru.set_BILL_ADDRESS(cur.getString(cur
                        .getColumnIndex("BILL_ADDRESS")));
                mru.set_CATEGORY(cur.getString(cur.getColumnIndex("CATEGORY")));
                mru.set_CNAME(cur.getString(cur.getColumnIndex("CNAME")));
                mru.set_Cfathername(cur.getString(cur.getColumnIndex("Cfathername")));
                mru.set_CON_ID(cur.getString(cur.getColumnIndex("CON_ID")));
                mru.set_APL_CONSUMER(cur.getString(cur.getColumnIndex("APL_CONSUMER")));
                mru.set_APL_BILLING_FLAG(cur.getString(cur.getColumnIndex("APL_BILLING_FLAG")));
                mru.set_Is_Pending_Bill(cur.getString(cur.getColumnIndex("Is_Pending_Bill")));
                mru.set_CONTACT_NUM(cur.getString(cur.getColumnIndex("CONTACT_NUM")));
                mru.set_DT_NO(cur.getString(cur.getColumnIndex("DT_NO")));
                mru.set_LOAD(cur.getString(cur.getColumnIndex("LOAD")));
                mru.set_METER_NO(cur.getString(cur.getColumnIndex("METER_NO")));
                mru.set_METR_UNMETER(cur.getString(cur
                        .getColumnIndex("METR_UNMETER")));
                mru.set_OLD_CON_ID(cur.getString(cur
                        .getColumnIndex("OLD_CON_ID")));
                mru.set_PHASE(cur.getString(cur.getColumnIndex("PHASE")));
                mru.set_RECYCLE_UNIT(cur.getString(cur
                        .getColumnIndex("RECYCLE_UNIT")));
                mru.set_SECTION_ID(cur.getString(cur
                        .getColumnIndex("SECTION_ID")));
                mru.set_SECTION_NAME(cur.getString(cur
                        .getColumnIndex("SECTION_NAME")));
                mru.set_PREVIOUS_READ(cur.getString(cur
                        .getColumnIndex("PRE_READ")));
                mru.set_IS_SYNC(cur.getString(cur.getColumnIndex("IS_SYNC")));
                mru.set_Cur_Read(cur.getString(cur.getColumnIndex("Cur_Read")));
                mru.set_Reading_Status(cur.getString(cur
                        .getColumnIndex("Reading_Status")));
                mru.set_Read_Date(cur.getString(cur.getColumnIndex("Read_Date")));
                mru.set_Cycle_Complete(cur.getString(cur
                        .getColumnIndex("Cycle_Complete")));
                mru.set_Power_Factor(cur.getString(cur
                        .getColumnIndex("Power_Factor")));
                mru.set_Rec_Demand(cur.getString(cur
                        .getColumnIndex("Rec_Demand")));
                mru.set_Meter_Photo(cur.getString(cur
                        .getColumnIndex("Meter_Photo")));
                mru.set_Latitude(cur.getString(cur.getColumnIndex("Latitude")));
                mru.set_Longitude(cur.getString(cur.getColumnIndex("Longitude")));

                mru.set_POW_FACT(cur.getString(cur.getColumnIndex("POW_FACT")));
                mru.set_REC_DEM(cur.getString(cur.getColumnIndex("REC_DEM")));
                mru.set_isPrinted(cur.getString(cur.getColumnIndex("IsAlredyPrint")));
                mru.set_IsMeterChange(cur.getString(cur.getColumnIndex("IsMeterChange")));
                mru.set_MeterNo(cur.getString(cur.getColumnIndex("MeterNo")));
                mru.setKwhReading(cur.getString(cur.getColumnIndex("kwh_read")));
                mru.setKvahReading(cur.getString(cur.getColumnIndex("kvah_read")));
                mruList.add(mru);
            }
            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;
    }

    @SuppressLint("Range")
    public ArrayList<MRUDetails> getMRU2(String pos, String value) {
        // PlaceDataSQL placeData = new PlaceDataSQL(MainActivity.this);
        ArrayList<MRUDetails> mruList = new ArrayList<MRUDetails>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{value.trim()};
            Cursor cur = db.rawQuery("Select * from MRUDetails WHERE " + pos
                    + " =? ", params);
            while (cur.moveToNext()) {
                MRUDetails mru = new MRUDetails();
                mru.set_ACT_NO(cur.getString(cur.getColumnIndex("ACT_NO")));
                mru.set_BILL_ADDRESS(cur.getString(cur
                        .getColumnIndex("BILL_ADDRESS")));
                mru.set_CATEGORY(cur.getString(cur.getColumnIndex("CATEGORY")));
                mru.set_CNAME(cur.getString(cur.getColumnIndex("CNAME")));
                mru.set_CON_ID(cur.getString(cur.getColumnIndex("CON_ID")));
                mru.set_Cfathername(cur.getString(cur.getColumnIndex("Cfathername")));
                mru.set_CONTACT_NUM(cur.getString(cur
                        .getColumnIndex("CONTACT_NUM")));
                mru.set_DT_NO(cur.getString(cur.getColumnIndex("DT_NO")));
                mru.set_LOAD(cur.getString(cur.getColumnIndex("LOAD")));
                mru.set_METER_NO(cur.getString(cur.getColumnIndex("METER_NO")));
                mru.set_METR_UNMETER(cur.getString(cur
                        .getColumnIndex("METR_UNMETER")));
                mru.set_APL_BILLING_FLAG(cur.getString(cur.getColumnIndex("APL_BILLING_FLAG")));
                mru.set_APL_CONSUMER(cur.getString(cur.getColumnIndex("APL_CONSUMER")));
                mru.set_OLD_CON_ID(cur.getString(cur
                        .getColumnIndex("OLD_CON_ID")));
                mru.set_PHASE(cur.getString(cur.getColumnIndex("PHASE")));
                mru.set_RECYCLE_UNIT(cur.getString(cur
                        .getColumnIndex("RECYCLE_UNIT")));
                mru.set_SECTION_ID(cur.getString(cur
                        .getColumnIndex("SECTION_ID")));
                mru.set_SECTION_NAME(cur.getString(cur
                        .getColumnIndex("SECTION_NAME")));
                mru.set_PREVIOUS_READ(cur.getString(cur
                        .getColumnIndex("PRE_READ")));
                mru.set_IS_SYNC(cur.getString(cur.getColumnIndex("IS_SYNC")));
                mru.set_Cur_Read(cur.getString(cur.getColumnIndex("Cur_Read")));
                mru.set_Reading_Status(cur.getString(cur
                        .getColumnIndex("Reading_Status")));
                mru.set_Read_Date(cur.getString(cur.getColumnIndex("Read_Date")));
                mru.set_Cycle_Complete(cur.getString(cur
                        .getColumnIndex("Cycle_Complete")));
                mru.set_Power_Factor(cur.getString(cur
                        .getColumnIndex("Power_Factor")));
                mru.set_Rec_Demand(cur.getString(cur
                        .getColumnIndex("Rec_Demand")));
                mru.set_Meter_Photo(cur.getString(cur
                        .getColumnIndex("Meter_Photo")));
                mru.set_Latitude(cur.getString(cur.getColumnIndex("Latitude")));
                mru.set_Longitude(cur.getString(cur.getColumnIndex("Longitude")));
                mru.set_Is_Pending_Bill(cur.getString(cur
                        .getColumnIndex("Is_Pending_Bill")));
                mru.set_POW_FACT(cur.getString(cur.getColumnIndex("POW_FACT")));
                mru.set_REC_DEM(cur.getString(cur.getColumnIndex("REC_DEM")));
                mru.set_IsMeterChange(cur.getString(cur.getColumnIndex("IsMeterChange")));
                mru.set_MeterNo(cur.getString(cur.getColumnIndex("MeterNo")));
                mru.set_Previous_read_stat(cur.getString(cur.getColumnIndex("previous_read_status")));
                mru.set_Is_address_updated(cur.getString(cur.getColumnIndex("is_address_updated")));
                mru.set_Lk_Limit(cur.getString(cur.getColumnIndex("LK_limit")));
                mru.set_Gender(cur.getString(cur.getColumnIndex("gender")));
                mru.set_purpose(cur.getString(cur.getColumnIndex("purpose")));
                mru.setKwhReading(cur.getString(cur.getColumnIndex("kwh_read")));
                mru.setKvahReading(cur.getString(cur.getColumnIndex("kvah_read")));
                mruList.add(mru);
            }
            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;

    }

    public int getmobileno(String value) {
        // PlaceDataSQL placeData = new PlaceDataSQL(MainActivity.this);
        int count = 0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{value.trim()};
            Cursor cur = db.rawQuery("Select * from MRUDetails WHERE CONTACT_NUM" + " =? ", params);
            count = cur.getCount();
            cur.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return count;

    }
    @SuppressLint("Range")
    public ArrayList<MRUDetails> getMRUByAccNO(String pos, String value) {

        // PlaceDataSQL placeData = new PlaceDataSQL(MainActivity.this);
        ArrayList<MRUDetails> mruList = new ArrayList<MRUDetails>();

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            String[] params = new String[]{value.trim()};
            Cursor cur = db.rawQuery("Select * from MRUDetails WHERE " + pos
                    + "=?" + "And Is_Pending_Bill ='G'", params);
            int x = cur.getCount();

            while (cur.moveToNext()) {

                MRUDetails mru = new MRUDetails();
                mru.set_ACT_NO(cur.getString(cur.getColumnIndex("ACT_NO")));
                mru.set_BILL_ADDRESS(cur.getString(cur
                        .getColumnIndex("BILL_ADDRESS")));
                mru.set_CATEGORY(cur.getString(cur.getColumnIndex("CATEGORY")));
                mru.set_CNAME(cur.getString(cur.getColumnIndex("CNAME")));
                mru.set_CON_ID(cur.getString(cur.getColumnIndex("CON_ID")));
                mru.set_CONTACT_NUM(cur.getString(cur
                        .getColumnIndex("CONTACT_NUM")));
                mru.set_DT_NO(cur.getString(cur.getColumnIndex("DT_NO")));
                mru.set_LOAD(cur.getString(cur.getColumnIndex("LOAD")));
                mru.set_METER_NO(cur.getString(cur.getColumnIndex("METER_NO")));
                mru.set_METR_UNMETER(cur.getString(cur
                        .getColumnIndex("METR_UNMETER")));
                mru.set_OLD_CON_ID(cur.getString(cur
                        .getColumnIndex("OLD_CON_ID")));
                mru.set_PHASE(cur.getString(cur.getColumnIndex("PHASE")));
                mru.set_RECYCLE_UNIT(cur.getString(cur
                        .getColumnIndex("RECYCLE_UNIT")));
                mru.set_SECTION_ID(cur.getString(cur
                        .getColumnIndex("SECTION_ID")));
                mru.set_SECTION_NAME(cur.getString(cur
                        .getColumnIndex("SECTION_NAME")));
                mru.set_PREVIOUS_READ(cur.getString(cur
                        .getColumnIndex("PRE_READ")));
                mru.set_IS_SYNC(cur.getString(cur.getColumnIndex("IS_SYNC")));
                mru.set_Cur_Read(cur.getString(cur.getColumnIndex("Cur_Read")));
                mru.set_Reading_Status(cur.getString(cur
                        .getColumnIndex("Reading_Status")));
                mru.set_Read_Date(cur.getString(cur.getColumnIndex("Read_Date")));
                mru.set_Cycle_Complete(cur.getString(cur
                        .getColumnIndex("Cycle_Complete")));
                mru.set_Power_Factor(cur.getString(cur
                        .getColumnIndex("Power_Factor")));
                mru.set_Rec_Demand(cur.getString(cur
                        .getColumnIndex("Rec_Demand")));
                mru.set_Meter_Photo(cur.getString(cur
                        .getColumnIndex("Meter_Photo")));
                mru.set_Latitude(cur.getString(cur.getColumnIndex("Latitude")));
                mru.set_Longitude(cur.getString(cur.getColumnIndex("Longitude")));
                mru.set_Is_Pending_Bill(cur.getString(cur
                        .getColumnIndex("Is_Pending_Bill")));
                mru.set_POW_FACT(cur.getString(cur.getColumnIndex("POW_FACT")));
                mru.set_REC_DEM(cur.getString(cur.getColumnIndex("REC_DEM")));
                mru.set_IsMeterChange(cur.getString(cur.getColumnIndex("IsMeterChange")));
                mru.set_MeterNo(cur.getString(cur.getColumnIndex("MeterNo")));
                mru.set_Previous_read_stat(cur.getString(cur.getColumnIndex("previous_read_status")));
                mru.setKwhReading(cur.getString(cur.getColumnIndex("kwh_read")));
                mru.setKvahReading(cur.getString(cur.getColumnIndex("kvah_read")));
                mruList.add(mru);
            }

            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;

    }

    @SuppressLint("Range")
    public ArrayList<MRUDetails> getMRUByAccNO1(String pos, String value) {

        // PlaceDataSQL placeData = new PlaceDataSQL(MainActivity.this);
        ArrayList<MRUDetails> mruList = new ArrayList<MRUDetails>();

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            String[] params = new String[]{value.trim()};
            Cursor cur = db.rawQuery("Select * from MRUDetails WHERE " + pos
                    + "=?" + "And Is_Pending_Bill ='P'", params);
            int x = cur.getCount();

            while (cur.moveToNext()) {

                MRUDetails mru = new MRUDetails();
                mru.set_ACT_NO(cur.getString(cur.getColumnIndex("ACT_NO")));
                mru.set_BILL_ADDRESS(cur.getString(cur
                        .getColumnIndex("BILL_ADDRESS")));
                mru.set_CATEGORY(cur.getString(cur.getColumnIndex("CATEGORY")));
                mru.set_CNAME(cur.getString(cur.getColumnIndex("CNAME")));
                mru.set_CON_ID(cur.getString(cur.getColumnIndex("CON_ID")));
                mru.set_CONTACT_NUM(cur.getString(cur
                        .getColumnIndex("CONTACT_NUM")));
                mru.set_DT_NO(cur.getString(cur.getColumnIndex("DT_NO")));
                mru.set_LOAD(cur.getString(cur.getColumnIndex("LOAD")));
                mru.set_METER_NO(cur.getString(cur.getColumnIndex("METER_NO")));
                mru.set_METR_UNMETER(cur.getString(cur
                        .getColumnIndex("METR_UNMETER")));
                mru.set_OLD_CON_ID(cur.getString(cur
                        .getColumnIndex("OLD_CON_ID")));
                mru.set_PHASE(cur.getString(cur.getColumnIndex("PHASE")));
                mru.set_RECYCLE_UNIT(cur.getString(cur
                        .getColumnIndex("RECYCLE_UNIT")));
                mru.set_SECTION_ID(cur.getString(cur
                        .getColumnIndex("SECTION_ID")));
                mru.set_SECTION_NAME(cur.getString(cur
                        .getColumnIndex("SECTION_NAME")));
                mru.set_PREVIOUS_READ(cur.getString(cur
                        .getColumnIndex("PRE_READ")));
                mru.set_IS_SYNC(cur.getString(cur.getColumnIndex("IS_SYNC")));
                mru.set_Cur_Read(cur.getString(cur.getColumnIndex("Cur_Read")));
                mru.set_Reading_Status(cur.getString(cur
                        .getColumnIndex("Reading_Status")));
                mru.set_Read_Date(cur.getString(cur.getColumnIndex("Read_Date")));
                mru.set_Cycle_Complete(cur.getString(cur
                        .getColumnIndex("Cycle_Complete")));
                mru.set_Power_Factor(cur.getString(cur
                        .getColumnIndex("Power_Factor")));
                mru.set_Rec_Demand(cur.getString(cur
                        .getColumnIndex("Rec_Demand")));
                mru.set_Meter_Photo(cur.getString(cur
                        .getColumnIndex("Meter_Photo")));
                mru.set_Latitude(cur.getString(cur.getColumnIndex("Latitude")));
                mru.set_Longitude(cur.getString(cur.getColumnIndex("Longitude")));
                mru.set_Is_Pending_Bill(cur.getString(cur
                        .getColumnIndex("Is_Pending_Bill")));
                mru.set_POW_FACT(cur.getString(cur.getColumnIndex("POW_FACT")));
                mru.set_REC_DEM(cur.getString(cur.getColumnIndex("REC_DEM")));
                mru.set_IsMeterChange(cur.getString(cur.getColumnIndex("IsMeterChange")));
                mru.set_MeterNo(cur.getString(cur.getColumnIndex("MeterNo")));
                mru.set_OCR_Agency(cur.getString(cur.getColumnIndex("ocr_agency")));
                mru.set_Gender(cur.getString(cur.getColumnIndex("gender")));
                mru.set_Previous_read_stat(cur.getString(cur.getColumnIndex("previous_read_status")));
                mru.setKwhReading(cur.getString(cur.getColumnIndex("kwh_read")));
                mru.setKvahReading(cur.getString(cur.getColumnIndex("kvah_read")));
                mruList.add(mru);
            }

            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;

    }
    @SuppressLint("Range")
    public ArrayList<MRUDetails> getMRUByAccNO1reason(String pos, String value) {

        // PlaceDataSQL placeData = new PlaceDataSQL(MainActivity.this);
        ArrayList<MRUDetails> mruList = new ArrayList<MRUDetails>();

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            String[] params = new String[]{value.trim()};
            Cursor cur = db.rawQuery("Select * from MRUDetails WHERE " + pos
                    + "=?" + "And Is_Pending_Bill ='R'", params);
            int x = cur.getCount();

            while (cur.moveToNext()) {

                MRUDetails mru = new MRUDetails();
                mru.set_ACT_NO(cur.getString(cur.getColumnIndex("ACT_NO")));
                mru.set_BILL_ADDRESS(cur.getString(cur
                        .getColumnIndex("BILL_ADDRESS")));
                mru.set_CATEGORY(cur.getString(cur.getColumnIndex("CATEGORY")));
                mru.set_CNAME(cur.getString(cur.getColumnIndex("CNAME")));
                mru.set_CON_ID(cur.getString(cur.getColumnIndex("CON_ID")));
                mru.set_CONTACT_NUM(cur.getString(cur
                        .getColumnIndex("CONTACT_NUM")));
                mru.set_DT_NO(cur.getString(cur.getColumnIndex("DT_NO")));
                mru.set_LOAD(cur.getString(cur.getColumnIndex("LOAD")));
                mru.set_METER_NO(cur.getString(cur.getColumnIndex("METER_NO")));
                mru.set_METR_UNMETER(cur.getString(cur
                        .getColumnIndex("METR_UNMETER")));
                mru.set_OLD_CON_ID(cur.getString(cur
                        .getColumnIndex("OLD_CON_ID")));
                mru.set_PHASE(cur.getString(cur.getColumnIndex("PHASE")));
                mru.set_RECYCLE_UNIT(cur.getString(cur
                        .getColumnIndex("RECYCLE_UNIT")));
                mru.set_SECTION_ID(cur.getString(cur
                        .getColumnIndex("SECTION_ID")));
                mru.set_SECTION_NAME(cur.getString(cur
                        .getColumnIndex("SECTION_NAME")));
                mru.set_PREVIOUS_READ(cur.getString(cur
                        .getColumnIndex("PRE_READ")));
                mru.set_IS_SYNC(cur.getString(cur.getColumnIndex("IS_SYNC")));
                mru.set_Cur_Read(cur.getString(cur.getColumnIndex("Cur_Read")));
                mru.set_Reading_Status(cur.getString(cur
                        .getColumnIndex("Reading_Status")));
                mru.set_Read_Date(cur.getString(cur.getColumnIndex("Read_Date")));
                mru.set_Cycle_Complete(cur.getString(cur
                        .getColumnIndex("Cycle_Complete")));
                mru.set_Power_Factor(cur.getString(cur
                        .getColumnIndex("Power_Factor")));
                mru.set_Rec_Demand(cur.getString(cur
                        .getColumnIndex("Rec_Demand")));
                mru.set_Meter_Photo(cur.getString(cur
                        .getColumnIndex("Meter_Photo")));
                mru.set_Latitude(cur.getString(cur.getColumnIndex("Latitude")));
                mru.set_Longitude(cur.getString(cur.getColumnIndex("Longitude")));
                mru.set_Is_Pending_Bill(cur.getString(cur
                        .getColumnIndex("Is_Pending_Bill")));
                mru.set_POW_FACT(cur.getString(cur.getColumnIndex("POW_FACT")));
                mru.set_REC_DEM(cur.getString(cur.getColumnIndex("REC_DEM")));
                mru.set_IsMeterChange(cur.getString(cur.getColumnIndex("IsMeterChange")));
                mru.set_MeterNo(cur.getString(cur.getColumnIndex("MeterNo")));
                mru.set_Reason(cur.getString(cur.getColumnIndex("reason")));
                mru.set_Previous_read_stat(cur.getString(cur.getColumnIndex("previous_read_status")));
                mru.setKwhReading(cur.getString(cur.getColumnIndex("kwh_read")));
                mru.setKvahReading(cur.getString(cur.getColumnIndex("kvah_read")));
                mruList.add(mru);
            }

            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;

    }

    @SuppressLint("Range")
    public ArrayList<MRUDetails> getAllMRU() {

        ArrayList<MRUDetails> mruList = new ArrayList<MRUDetails>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{"P"};
            Cursor cur = db.rawQuery("Select * from MRUDetails where Is_Pending_Bill ='P' limit 10", null);

            int x = cur.getCount();

            while (cur.moveToNext()) {

                MRUDetails mru = new MRUDetails();
                mru.set_ACT_NO(cur.getString(cur.getColumnIndex("ACT_NO")));
                mru.set_BILL_ADDRESS(cur.getString(cur
                        .getColumnIndex("BILL_ADDRESS")));
                mru.set_CATEGORY(cur.getString(cur.getColumnIndex("CATEGORY")));
                mru.set_CNAME(cur.getString(cur.getColumnIndex("CNAME")));
                mru.set_CON_ID(cur.getString(cur.getColumnIndex("CON_ID")));
                mru.set_CONTACT_NUM(cur.getString(cur
                        .getColumnIndex("CONTACT_NUM")));
                mru.set_DT_NO(cur.getString(cur.getColumnIndex("DT_NO")));
                mru.set_LOAD(cur.getString(cur.getColumnIndex("LOAD")));
                mru.set_METER_NO(cur.getString(cur.getColumnIndex("METER_NO")));
                mru.set_METR_UNMETER(cur.getString(cur
                        .getColumnIndex("METR_UNMETER")));
                mru.set_OLD_CON_ID(cur.getString(cur
                        .getColumnIndex("OLD_CON_ID")));
                mru.set_PHASE(cur.getString(cur.getColumnIndex("PHASE")));
                mru.set_RECYCLE_UNIT(cur.getString(cur
                        .getColumnIndex("RECYCLE_UNIT")));
                mru.set_SECTION_ID(cur.getString(cur
                        .getColumnIndex("SECTION_ID")));
                mru.set_SECTION_NAME(cur.getString(cur
                        .getColumnIndex("SECTION_NAME")));
                mru.set_PREVIOUS_READ(cur.getString(cur
                        .getColumnIndex("PRE_READ")));

                mru.set_IS_SYNC(cur.getString(cur.getColumnIndex("IS_SYNC")));
                mru.set_Cur_Read(cur.getString(cur.getColumnIndex("Cur_Read")));
                mru.set_Reading_Status(cur.getString(cur
                        .getColumnIndex("Reading_Status")));
                mru.set_Read_Date(cur.getString(cur.getColumnIndex("Read_Date")));
                mru.set_Cycle_Complete(cur.getString(cur
                        .getColumnIndex("Cycle_Complete")));
                mru.set_Power_Factor(cur.getString(cur
                        .getColumnIndex("Power_Factor")));
                mru.set_Rec_Demand(cur.getString(cur
                        .getColumnIndex("Rec_Demand")));
                mru.set_Meter_Photo(cur.getString(cur
                        .getColumnIndex("Meter_Photo")));
                mru.set_Latitude(cur.getString(cur.getColumnIndex("Latitude")));
                mru.set_Longitude(cur.getString(cur.getColumnIndex("Longitude")));
                mru.set_Is_Pending_Bill(cur.getString(cur
                        .getColumnIndex("Is_Pending_Bill")));
                mru.set_POW_FACT(cur.getString(cur.getColumnIndex("POW_FACT")));
                mru.set_REC_DEM(cur.getString(cur.getColumnIndex("REC_DEM")));
                mru.set_IsMeterChange(cur.getString(cur.getColumnIndex("IsMeterChange")));
                mru.set_MeterNo(cur.getString(cur.getColumnIndex("MeterNo")));
                mru.set_Previous_read_stat(cur.getString(cur.getColumnIndex("previous_read_status")));
               mru.set_OCR_Agency(cur.getString(cur.getColumnIndex("ocr_agency")));
                mru.setKwhReading(cur.getString(cur.getColumnIndex("kwh_read")));
                mru.setKvahReading(cur.getString(cur.getColumnIndex("kvah_read")));
                mruList.add(mru);
            }

            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;

    }
    @SuppressLint("Range")
    public ArrayList<MRUDetails> getAllMRUreason() {
        ArrayList<MRUDetails> mruList = new ArrayList<MRUDetails>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{"R"};
            Cursor cur = db.rawQuery("Select * from MRUDetails where Is_Pending_Bill ='R' limit 10", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                MRUDetails mru = new MRUDetails();
                mru.set_ACT_NO(cur.getString(cur.getColumnIndex("ACT_NO")));
                mru.set_BILL_ADDRESS(cur.getString(cur
                        .getColumnIndex("BILL_ADDRESS")));
                mru.set_CATEGORY(cur.getString(cur.getColumnIndex("CATEGORY")));
                mru.set_CNAME(cur.getString(cur.getColumnIndex("CNAME")));
                mru.set_CON_ID(cur.getString(cur.getColumnIndex("CON_ID")));
                mru.set_CONTACT_NUM(cur.getString(cur
                        .getColumnIndex("CONTACT_NUM")));
                mru.set_DT_NO(cur.getString(cur.getColumnIndex("DT_NO")));
                mru.set_LOAD(cur.getString(cur.getColumnIndex("LOAD")));
                mru.set_METER_NO(cur.getString(cur.getColumnIndex("METER_NO")));
                mru.set_METR_UNMETER(cur.getString(cur
                        .getColumnIndex("METR_UNMETER")));
                mru.set_OLD_CON_ID(cur.getString(cur
                        .getColumnIndex("OLD_CON_ID")));
                mru.set_PHASE(cur.getString(cur.getColumnIndex("PHASE")));
                mru.set_RECYCLE_UNIT(cur.getString(cur
                        .getColumnIndex("RECYCLE_UNIT")));
                mru.set_SECTION_ID(cur.getString(cur
                        .getColumnIndex("SECTION_ID")));
                mru.set_SECTION_NAME(cur.getString(cur
                        .getColumnIndex("SECTION_NAME")));
                mru.set_PREVIOUS_READ(cur.getString(cur
                        .getColumnIndex("PRE_READ")));

                mru.set_IS_SYNC(cur.getString(cur.getColumnIndex("IS_SYNC")));
                mru.set_Cur_Read(cur.getString(cur.getColumnIndex("Cur_Read")));
                mru.set_Reading_Status(cur.getString(cur
                        .getColumnIndex("Reading_Status")));
                mru.set_Read_Date(cur.getString(cur.getColumnIndex("Read_Date")));
                mru.set_Cycle_Complete(cur.getString(cur
                        .getColumnIndex("Cycle_Complete")));
                mru.set_Power_Factor(cur.getString(cur
                        .getColumnIndex("Power_Factor")));
                mru.set_Rec_Demand(cur.getString(cur
                        .getColumnIndex("Rec_Demand")));
                mru.set_Meter_Photo(cur.getString(cur
                        .getColumnIndex("Meter_Photo")));
                mru.set_Latitude(cur.getString(cur.getColumnIndex("Latitude")));
                mru.set_Longitude(cur.getString(cur.getColumnIndex("Longitude")));
                mru.set_Is_Pending_Bill(cur.getString(cur
                        .getColumnIndex("Is_Pending_Bill")));
                mru.set_POW_FACT(cur.getString(cur.getColumnIndex("POW_FACT")));
                mru.set_REC_DEM(cur.getString(cur.getColumnIndex("REC_DEM")));
                mru.set_IsMeterChange(cur.getString(cur.getColumnIndex("IsMeterChange")));
                mru.set_MeterNo(cur.getString(cur.getColumnIndex("MeterNo")));
                mru.set_Reason(cur.getString(cur.getColumnIndex("reason")));
                mru.set_Previous_read_stat(cur.getString(cur.getColumnIndex("previous_read_status")));
                mru.setKwhReading(cur.getString(cur.getColumnIndex("kwh_read")));
                mru.setKvahReading(cur.getString(cur.getColumnIndex("kvah_read")));
                mruList.add(mru);
            }

            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;

    }
    public ArrayList<Uri> getAllMRUuri() {
        ArrayList<Uri> mruList = new ArrayList<Uri>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{"R"};
            Cursor cur = db.rawQuery("Select Meter_Photo from MRUDetails where Is_Pending_Bill ='G' ", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                MRUDetails mru = new MRUDetails();
            @SuppressLint("Range") Uri photo= Uri.parse(cur.getString(cur
                        .getColumnIndex("Meter_Photo")));
                mruList.add(photo);
            }
            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;

    }

    @SuppressLint("Range")
    public ArrayList<MRUDetails> getUnbilledMRU() {

        ArrayList<MRUDetails> mruList = new ArrayList<MRUDetails>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{"P"};
            Cursor cur = db.rawQuery("Select * from MRUDetails where Is_Pending_Bill ='P'", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                MRUDetails mru = new MRUDetails();
                mru.set_ACT_NO(cur.getString(cur.getColumnIndex("ACT_NO")));
                mru.set_BILL_ADDRESS(cur.getString(cur
                        .getColumnIndex("BILL_ADDRESS")));
                mru.set_CATEGORY(cur.getString(cur.getColumnIndex("CATEGORY")));
                mru.set_CNAME(cur.getString(cur.getColumnIndex("CNAME")));
                mru.set_CON_ID(cur.getString(cur.getColumnIndex("CON_ID")));
                mru.set_CONTACT_NUM(cur.getString(cur
                        .getColumnIndex("CONTACT_NUM")));
                mru.set_DT_NO(cur.getString(cur.getColumnIndex("DT_NO")));
                mru.set_LOAD(cur.getString(cur.getColumnIndex("LOAD")));
                mru.set_APL_BILLING_FLAG(cur.getString(cur.getColumnIndex("APL_BILLING_FLAG")));
                mru.set_APL_CONSUMER(cur.getString(cur.getColumnIndex("APL_CONSUMER")));
                mru.set_METER_NO(cur.getString(cur.getColumnIndex("METER_NO")));
                mru.set_METR_UNMETER(cur.getString(cur
                        .getColumnIndex("METR_UNMETER")));
                mru.set_OLD_CON_ID(cur.getString(cur
                        .getColumnIndex("OLD_CON_ID")));
                mru.set_PHASE(cur.getString(cur.getColumnIndex("PHASE")));
                mru.set_RECYCLE_UNIT(cur.getString(cur
                        .getColumnIndex("RECYCLE_UNIT")));
                mru.set_SECTION_ID(cur.getString(cur
                        .getColumnIndex("SECTION_ID")));
                mru.set_SECTION_NAME(cur.getString(cur
                        .getColumnIndex("SECTION_NAME")));
                mru.set_PREVIOUS_READ(cur.getString(cur
                        .getColumnIndex("PRE_READ")));
                mru.set_IS_SYNC(cur.getString(cur.getColumnIndex("IS_SYNC")));
                mru.set_Cur_Read(cur.getString(cur.getColumnIndex("Cur_Read")));
                mru.set_Reading_Status(cur.getString(cur
                        .getColumnIndex("Reading_Status")));
                mru.set_Read_Date(cur.getString(cur.getColumnIndex("Read_Date")));
                mru.set_Cycle_Complete(cur.getString(cur
                        .getColumnIndex("Cycle_Complete")));
                mru.set_Power_Factor(cur.getString(cur
                        .getColumnIndex("Power_Factor")));
                mru.set_Rec_Demand(cur.getString(cur
                        .getColumnIndex("Rec_Demand")));
                mru.set_Meter_Photo(cur.getString(cur
                        .getColumnIndex("Meter_Photo")));
                mru.set_Latitude(cur.getString(cur.getColumnIndex("Latitude")));
                mru.set_Longitude(cur.getString(cur.getColumnIndex("Longitude")));
                mru.set_Is_Pending_Bill(cur.getString(cur
                        .getColumnIndex("Is_Pending_Bill")));
                mru.set_POW_FACT(cur.getString(cur.getColumnIndex("POW_FACT")));
                mru.set_REC_DEM(cur.getString(cur.getColumnIndex("REC_DEM")));
                mru.set_IsMeterChange(cur.getString(cur.getColumnIndex("IsMeterChange")));
                mru.set_MeterNo(cur.getString(cur.getColumnIndex("MeterNo")));
                mru.set_OCR_Agency(cur.getString(cur.getColumnIndex("ocr_agency")));
                mru.set_Gender(cur.getString(cur.getColumnIndex("gender")));
                mru.set_purpose(cur.getString(cur.getColumnIndex("purpose")));
                mru.set_Previous_read_stat(cur.getString(cur.getColumnIndex("previous_read_status")));
                mru.setKwhReading(cur.getString(cur.getColumnIndex("kwh_read")));
                mru.setKvahReading(cur.getString(cur.getColumnIndex("kvah_read")));
                mruList.add(mru);
            }

            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;
    }
    @SuppressLint("Range")
    public ArrayList<MRUDetails> getUnbilledreason() {
        ArrayList<MRUDetails> mruList = new ArrayList<MRUDetails>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{"R"};
            Cursor cur = db.rawQuery("Select * from MRUDetails where Is_Pending_Bill ='R' ", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                MRUDetails mru = new MRUDetails();
                mru.set_ACT_NO(cur.getString(cur.getColumnIndex("ACT_NO")));
                mru.set_BILL_ADDRESS(cur.getString(cur
                        .getColumnIndex("BILL_ADDRESS")));
                mru.set_CNAME(cur.getString(cur.getColumnIndex("CNAME")));
                mru.set_CON_ID(cur.getString(cur.getColumnIndex("CON_ID")));
                mru.set_Is_Pending_Bill(cur.getString(cur
                        .getColumnIndex("Is_Pending_Bill")));
                mru.set_Reason(cur.getString(cur.getColumnIndex("reason")));
                mru.setKwhReading(cur.getString(cur.getColumnIndex("kwh_read")));
                mru.setKvahReading(cur.getString(cur.getColumnIndex("kvah_read")));
                mruList.add(mru);
            }

            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;

    }


    @SuppressLint("Range")
    public ArrayList<Establishment_details> getestablishment_details(String tariff) {
        ArrayList<Establishment_details> mruList = new ArrayList<Establishment_details>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();
           String[] params = new String[]{tariff};
            Cursor cur = db.rawQuery("Select * from Establishment_deatils where tariff_Id=?", params);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                Establishment_details mru = new Establishment_details();
                mru.set_Establishment(cur.getString(cur.getColumnIndex("Establishment_type")));
                mru.set_Tariff_Id(cur.getString(cur.getColumnIndex("tariff_Id")));
                mruList.add(mru);
            }

            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;

    }


  /*  public ArrayList<Report> getuntracedconsumercount() {
        String count="0";
        ArrayList<Report> mruList = new ArrayList<Report>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{"R"};
            Cursor cur = db.rawQuery("select  count(Id) as total,sum(case when Is_Pending_Bill='R' or Is_Pending_Bill='' then 1 else 0 end) as traced from MRuDetails", null);
            int x = cur.getCount();
            count=""+count;
            while (cur.moveToNext()) {
                Report rpt = new Report();
                rpt.set_TotalCon(cur.getString(cur.getColumnIndex("total")));
                rpt.set_traced(cur.getString(cur.getColumnIndex("traced")));
                mruList.add(rpt);
            }

            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return mruList;

    }*/
    public long updateMru(String MobileNo, String DtNo, String AcNo, String IsSync,String gender,String purpose) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("CONTACT_NUM", MobileNo);
            values.put("DT_NO", DtNo);
            values.put("gender", gender);
            values.put("purpose", purpose);
            values.put("IS_SYNC", IsSync);
            String[] whereArgs = new String[]{AcNo};
            c = db.update("MRUDetails", values, "ACT_NO=?", whereArgs);
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;

        }
        return c;
    }

    public long saveMeterNo(String IsMeterChange, String MeterNo, String AcNo) {

        long c = -1;
        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("IsMeterChange", IsMeterChange);
            values.put("MeterNo", MeterNo);
            String[] whereArgs = new String[]{AcNo};
            c = db.update("MRUDetails", values, "ACT_NO=?", whereArgs);
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }

    public long savePhoto(String latitude, String longitude, String photoPath,
                          String AcNo, String Date,String gender,String purpose) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("Latitude", latitude);
            values.put("Longitude", longitude);
            values.put("Meter_Photo", photoPath);
            values.put("gender", gender);
            values.put("purpose", purpose);
            values.put("Read_Date", Date);
            String[] whereArgs = new String[]{AcNo};
            c = db.update("MRUDetails", values, "CON_ID=?", whereArgs);
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }

    public long savePhotoAPL(String latitude, String longitude, String photoPath,
                             String AcNo, String Date) {

        long c = -1;
        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("Pma_Photo", photoPath);
            String[] whereArgs = new String[]{AcNo};
            c = db.update("MRUDetails", values, "ACT_NO=?", whereArgs);
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }

    public long saveMeterStatus(String readStatus, String reading, String PF,
                                String maxDemand, String isRecycle, String AcNo, String isPending, String UnmeterStatus ,String ocr_agency,String kvah,String kwh) {
        long c = -1;
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Reading_Status", readStatus);
            values.put("Cur_Read", reading);
            values.put("Power_Factor", PF);
            values.put("Rec_Demand", maxDemand);
            values.put("Cycle_Complete", isRecycle);
            values.put("Is_Pending_Bill", isPending);
            values.put("UnmeterStaus", UnmeterStatus);
            values.put("ocr_agency", ocr_agency);
            values.put("kvah_read", kvah);
            values.put("kwh_read", kwh);
            String[] whereArgs = new String[]{AcNo};
            c = db.update("MRUDetails", values, "ACT_NO=?", whereArgs);
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }

    public long saveBillStatus(String value, String AcNo) {

        long c = -1;
        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("Is_Pending_Bill", value);

            String[] whereArgs = new String[]{AcNo};
            c = db.update("MRUDetails", values, "ACT_NO=?", whereArgs);

            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }
    public long updatereasonstatus(String AcNo, String IsSync) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Is_Pending_Bill", "U");
            String[] whereArgs = new String[]{AcNo};
            c = db.update("MRUDetails", values, "ACT_NO=?", whereArgs);
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;

        }
        return c;
    }
    public long saveBillreason(String value, String AcNo) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Is_Pending_Bill", "R");
            values.put("reason", value);
            String[] whereArgs = new String[]{AcNo};
            c = db.update("MRUDetails", values, "ACT_NO=?", whereArgs);
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }
    public int getSizeOfMRU() {

        int x = 0;
        try {

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery("Select * from MRUDetails", null);
            x = cur.getCount();
            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return x;

    }

    @SuppressLint("Range")
    public String getIsPendingBill(String value) {

        String x = "";
        try {
            String[] params = new String[]{value.trim()};
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery("Select * from MRUDetails WHERE ACT_NO = ?", params);
            while (cur.moveToNext()) {
                x = cur.getString(cur.getColumnIndex("Is_Pending_Bill"));
            }
            if (x == null) {
                x = "";
            }
            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return x;
    }
    public long insertBillDetails(BillDetails result) {
        long c = 0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("ACT_NO", result.get_ACT_NO());
            values.put("AFTER_DATE", result.get_AFTER_DATE());
            values.put("AREA_TYPE", result.get_AREA_TYPE());
            values.put("AVG_UNIT", result.get_AVG_UNIT());
            values.put("BILLED_MONTH", result.get_BILLED_MONTH());
            values.put("BILLED_UNIT", result.get_BILLED_UNIT());
            values.put("BILL_ADDRESS", result.get_BILL_ADDRESS());
            values.put("BILL_DT", result.get_BILL_DT());
            values.put("BILL_NO", result.get_BILL_NO());
            values.put("BILL_TYPE", result.get_BILL_TYPE());
            values.put("BMONTH", result.get_BMONTH());
            values.put("BOOK_NO", result.get_BOOK_NO());
            values.put("BYEAR", result.get_BYEAR());
            values.put("BY_DATE", result.get_BY_DATE());
            values.put("CATEGORY", result.get_CATEGORY());
            values.put("CNAME", result.get_CNAME());
            values.put("COMPANY", result.get_COMPANY());
            values.put("CONC_LOAD", result.get_CONC_LOAD());
            values.put("CON_DEM", result.get_CON_DEM());
            values.put("CON_ID", result.get_CON_ID());
            values.put("CUR_READ", result.get_CUR_READ());
            values.put("DEM_CURR", result.get_DEM_CURR());
            values.put("DIV_NAME", result.get_DIV_NAME());
            values.put("DPS_ARREAR", result.get_DPS_ARREAR());
            values.put("DPS_CURR", result.get_DPS_CURR());
            values.put("EC_ARREAR", result.get_EC_ARREAR());
            values.put("EC_CURR", result.get_EC_CURR());
            values.put("ED_ARREAR", result.get_EDAREAR());
            values.put("ED_CURRENT", result.get_ED_CURRENT());
            values.put("EXC_DEM_CURR", result.get_EXC_DEM_CURR());
            values.put("FIX_CURR", result.get_FIX_CURR());
            values.put("GROSS_AMT", result.get_GROSS_AMT());
            values.put("INCENTIVE", result.get_INCENTIVE());
            values.put("INTR_SEC_DEP", result.get_INTR_SEC_DEP());
            values.put("LAST_PAID_AMT", result.get_LAST_PAID_AMT());
            values.put("LAST_PAY_DATE", result.get_LAST_PAY_DATE());
            values.put("LAST_RCPT_NO", result.get_LAST_RCPT_NO());
            values.put("METER_NO", result.get_METER_NO());
            values.put("MET_OWNER", result.get_MET_OWNER());
            values.put("MET_RENT", result.get_MET_RENT());
            values.put("MMC_UNIT", result.get_MMC_UNIT());
            values.put("MULTI_FACT", result.get_MULTI_FACT());
            values.put("NET_AMT", result.get_NET_AMT());
            values.put("OLD_CON_ID", result.get_OLD_CON_ID());
            values.put("OTH_ARREAR", result.get_OTH_ARREAR());
            values.put("OTH_CURR", result.get_OTH_CURR());
            values.put("PHASE", result.get_PHASE());
            values.put("POLE_NO", result.get_POLE_NO());
            values.put("POW_FACT", result.get_POW_FACT());
            values.put("PREV_READ", result.get_PREV_READ());
            values.put("PRE_READ_DATE", result.get_PRE_READ_DATE());
            values.put("PRE_READ_STAT", result.get_PRE_READ_STAT());
            values.put("PROMPT_AMT", result.get_PROMPT_AMT());
            values.put("READ_DATE", result.get_READ_DATE());
            values.put("READ_STAT", result.get_READ_STAT());
            values.put("REBATE", result.get_REBATE());
            values.put("REBATE_ON_MMC", result.get_REBATE_ON_MMC());
            values.put("REC_DEMAND", result.get_REC_DEMAND());
            values.put("SANC_LOAD", result.get_SANC_LOAD());
            values.put("SECTION_NAME", result.get_SECTION_NAME());
            values.put("SEC_DEP", result.get_SEC_DEP());
            values.put("SHUN_CURR", result.get_SHUN_CURR());
            values.put("SUB_DIV_NAME", result.get_SUB_DIV_NAME());
            values.put("UPTO_DATE", result.get_UPTO_DATE());
            values.put("BILL_TIME", result.get_BILL_TIME());
            values.put("UNITS_CONS", result.get_UNITS_CONS());
            values.put("SubTotalA", result.get_SubTotalA());
            values.put("SubTotalB",result.get_SubTotalB());
            values.put("SubTotalC", result.get_SubTotalC());
            values.put("AMBI_Flag", result.get_AMBI_Flag());
            values.put("GOV_SUBSIDY", result.get_GOV_SUBSIDY());
            values.put("OLD_CONSUMPTION", result.get_OLD_CONSUMPTION());
            values.put("ONLINE_REBATE", result.get_ONLINE_REBATE());
            values.put("KEPT_AMT", result.get_KEPT_AMOUNT());
            values.put("KEPT_INST_FLAG", result.get_KEPT_INST_FLAG());
            values.put("KEPT_PAY_AMT", result.get_KEPT_PAY_AMT());
            values.put("KEPT_INST_AMT", result.get_KEPT_INST_AMT());
            values.put("cgst_amount", result.get_CGST_AMT());
            values.put("sgst_amount", result.get_SGCT_AMT());
            values.put("oth_cgst", result.getOTH_CGST_AMT());
            values.put("oth_sgst", result.getOTH_SGCT_AMT());
            values.put("go_green", result.get_Go_Green());
            values.put("remission_charge", result.get_Remission_charge());
            values.put("quterly_rebate_posted", result.getQUARTERLY_REBATE_POSTED());
            String[] whereArgs = new String[]{result.get_ACT_NO()};
            c = db.update("BillDetails", values, "ACT_NO=?", whereArgs);
            Log.e(""+c,values.toString());
            if (!(c > 0)) {
                c = db.insert("BillDetails", null, values);
                Log.e(""+c,values.toString());
            }

            db.close();
        } catch (Exception e) {
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
            Log.e(""+c,e.toString());
            // TODO: handle exception
        }
        return c;

    }


    @SuppressLint("Range")
    public BillDetails getBillDetails(String pos, String value) {
        BillDetails bill = new BillDetails();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{value.trim()};
            Cursor cur = db.rawQuery("Select * from BillDetails WHERE " + pos
                    + "=?", params);
            int x = cur.getCount();

            while (cur.moveToNext()) {


                bill.set_ACT_NO(cur.getString(cur.getColumnIndex("ACT_NO")));
                bill.set_AFTER_DATE(cur.getString(cur.getColumnIndex("AFTER_DATE")));
                bill.set_AREA_TYPE(cur.getString(cur.getColumnIndex("AREA_TYPE")));
                bill.set_AVG_UNIT(cur.getString(cur.getColumnIndex("AVG_UNIT")));

                bill.set_BILL_ADDRESS(cur.getString(cur.getColumnIndex("BILL_ADDRESS")));
                bill.set_BILL_DT(cur.getString(cur.getColumnIndex("BILL_DT")));
                bill.set_BILL_NO(cur.getString(cur.getColumnIndex("BILL_NO")));
                bill.set_BILL_TIME(cur.getString(cur.getColumnIndex("BILL_TIME")));
                bill.set_BILL_TYPE(cur.getString(cur.getColumnIndex("BILL_TYPE")));
                bill.set_BILLED_MONTH(cur.getString(cur.getColumnIndex("BILLED_MONTH")));
                bill.set_BILLED_UNIT(cur.getString(cur.getColumnIndex("BILLED_UNIT")));
                bill.set_BMONTH(cur.getString(cur.getColumnIndex("BMONTH")));
                bill.set_BOOK_NO(cur.getString(cur.getColumnIndex("BOOK_NO")));
                bill.set_BY_DATE(cur.getString(cur.getColumnIndex("BY_DATE")));
                bill.set_BYEAR(cur.getString(cur.getColumnIndex("BYEAR")));

                bill.set_CATEGORY(cur.getString(cur.getColumnIndex("CATEGORY")));
                bill.set_CNAME(cur.getString(cur.getColumnIndex("CNAME")));
                bill.set_COMPANY(cur.getString(cur.getColumnIndex("COMPANY")));
                bill.set_CON_DEM(cur.getString(cur.getColumnIndex("CON_DEM")));
                bill.set_CON_ID(cur.getString(cur.getColumnIndex("CON_ID")));
                bill.set_CONC_LOAD(cur.getString(cur.getColumnIndex("CONC_LOAD")));
                bill.set_CUR_READ(cur.getString(cur.getColumnIndex("CUR_READ")));
                bill.set_DEM_CURR(cur.getString(cur.getColumnIndex("DEM_CURR")));
                bill.set_DIV_NAME(cur.getString(cur.getColumnIndex("DIV_NAME")));
                bill.set_DPS_ARREAR(cur.getString(cur.getColumnIndex("DPS_ARREAR")));
                bill.set_DPS_CURR(cur.getString(cur.getColumnIndex("DPS_CURR")));
                bill.set_EDAREAR(cur.getString(cur.getColumnIndex("ED_ARREAR")));
                bill.set_EC_ARREAR(cur.getString(cur.getColumnIndex("EC_ARREAR")));
                bill.set_EC_CURR(cur.getString(cur.getColumnIndex("EC_CURR")));
                bill.set_ED_CURRENT(cur.getString(cur.getColumnIndex("ED_CURRENT")));
                bill.set_EXC_DEM_CURR(cur.getString(cur.getColumnIndex("EXC_DEM_CURR")));
                bill.set_FIX_CURR(cur.getString(cur.getColumnIndex("FIX_CURR")));
                bill.set_GROSS_AMT(cur.getString(cur.getColumnIndex("GROSS_AMT")));
                bill.set_INCENTIVE(cur.getString(cur.getColumnIndex("INCENTIVE")));
                bill.set_INTR_SEC_DEP(cur.getString(cur.getColumnIndex("INTR_SEC_DEP")));
                bill.set_LAST_PAID_AMT(cur.getString(cur.getColumnIndex("LAST_PAID_AMT")));
                bill.set_LAST_PAY_DATE(cur.getString(cur.getColumnIndex("LAST_PAY_DATE")));
                bill.set_LAST_RCPT_NO(cur.getString(cur.getColumnIndex("LAST_RCPT_NO")));

                bill.set_MET_OWNER(cur.getString(cur.getColumnIndex("MET_OWNER")));
                bill.set_MET_RENT(cur.getString(cur.getColumnIndex("MET_RENT")));
                bill.set_METER_NO(cur.getString(cur.getColumnIndex("METER_NO")));
                bill.set_MMC_UNIT(cur.getString(cur.getColumnIndex("MMC_UNIT")));
                bill.set_MULTI_FACT(cur.getString(cur.getColumnIndex("MULTI_FACT")));
                bill.set_NET_AMT(cur.getString(cur.getColumnIndex("NET_AMT")));
                bill.set_OLD_CON_ID(cur.getString(cur.getColumnIndex("OLD_CON_ID")));
                bill.set_OTH_ARREAR(cur.getString(cur.getColumnIndex("OTH_ARREAR")));
                bill.set_OTH_CURR(cur.getString(cur.getColumnIndex("OTH_CURR")));
                bill.set_POW_FACT(cur.getString(cur.getColumnIndex("POW_FACT")));
                bill.set_PHASE(cur.getString(cur.getColumnIndex("PHASE")));

                bill.set_POLE_NO(cur.getString(cur.getColumnIndex("POLE_NO")));
                bill.set_PRE_READ_DATE(cur.getString(cur.getColumnIndex("PRE_READ_DATE")));
                bill.set_PRE_READ_STAT(cur.getString(cur.getColumnIndex("PRE_READ_STAT")));
                bill.set_PREV_READ(cur.getString(cur.getColumnIndex("PREV_READ")));
                bill.set_PROMPT_AMT(cur.getString(cur.getColumnIndex("PROMPT_AMT")));
                bill.set_READ_DATE(cur.getString(cur.getColumnIndex("READ_DATE")));
                bill.set_READ_STAT(cur.getString(cur.getColumnIndex("READ_STAT")));
                bill.set_REBATE(cur.getString(cur.getColumnIndex("REBATE")));
                bill.set_REBATE_ON_MMC(cur.getString(cur.getColumnIndex("REBATE_ON_MMC")));
                bill.set_REC_DEMAND(cur.getString(cur.getColumnIndex("REC_DEMAND")));

                bill.set_SANC_LOAD(cur.getString(cur.getColumnIndex("SANC_LOAD")));
                bill.set_SEC_DEP(cur.getString(cur.getColumnIndex("SEC_DEP")));
                bill.set_SECTION_NAME(cur.getString(cur.getColumnIndex("SECTION_NAME")));
                bill.set_SHUN_CURR(cur.getString(cur.getColumnIndex("SHUN_CURR")));
                bill.set_SUB_DIV_NAME(cur.getString(cur.getColumnIndex("SUB_DIV_NAME")));
                bill.set_UNITS_CONS(cur.getString(cur.getColumnIndex("UNITS_CONS")));
                bill.set_UPTO_DATE(cur.getString(cur.getColumnIndex("UPTO_DATE")));
                bill.set_IsAlredyPrint(cur.getString(cur.getColumnIndex("IsAlredyPrint")));
                bill.set_SubTotalA(cur.getString(cur.getColumnIndex("SubTotalA")));
                bill.set_SubTotalB(cur.getString(cur.getColumnIndex("SubTotalB")));
                bill.set_SubTotalC(cur.getString(cur.getColumnIndex("SubTotalC")));
                bill.set_AMBI_Flag(cur.getString(cur.getColumnIndex("AMBI_Flag")));
                bill.set_GOV_SUBSIDY(cur.getString(cur.getColumnIndex("GOV_SUBSIDY")));
                bill.set_ONLINE_REBATE(cur.getString(cur.getColumnIndex("ONLINE_REBATE")));
                bill.set_OLD_CONSUMPTION(cur.getString(cur.getColumnIndex("OLD_CONSUMPTION")));
                bill.set_KEPT_AMOUNT(cur.getString(cur.getColumnIndex("KEPT_AMT")));
                bill.set_KEPT_INST_FLAG(cur.getString(cur.getColumnIndex("KEPT_INST_FLAG")));
                bill.set_KEPT_PAY_AMT(cur.getString(cur.getColumnIndex("KEPT_PAY_AMT")));
                bill.set_KEPT_INST_AMT(cur.getString(cur.getColumnIndex("KEPT_INST_AMT")));
                bill.set_CGST_AMT(cur.getString(cur.getColumnIndex("cgst_amount")));
                bill.set_SGCT_AMT(cur.getString(cur.getColumnIndex("sgst_amount")));
                bill.setOTH_CGST_AMT(cur.getString(cur.getColumnIndex("oth_cgst")));
                bill.setOTH_SGCT_AMT(cur.getString(cur.getColumnIndex("oth_sgst")));
                bill.set_Remission_charge(cur.getString(cur.getColumnIndex("remission_charge")));
                bill.setQUARTERLY_REBATE_POSTED(cur.getString(cur.getColumnIndex("quterly_rebate_posted")));
            }
            cur.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
            return null;
        }
        return bill;
    }

    public long updateBillDetails(String AcNo, String value) {

        long c = -1;
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("IsAlredyPrint", value);
            String[] whereArgs = new String[]{AcNo};
            c = db.update("BillDetails", values, "ACT_NO=?", whereArgs);
            c = db.update("MRUDetails", values, "ACT_NO=?", whereArgs);

            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }


    @SuppressLint("Range")
    public ArrayList<Report> getSummaryReport() {

        ArrayList<Report> reportList = new ArrayList<Report>();

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cur = db.rawQuery("select M.category , count(*) as Total ,\n" +
                    "(select count(Is_Pending_Bill) from MRuDetails where Is_Pending_Bill='G' and M.category=MRuDetails.cATEGORY ) as Billed ,\n" +
                    "(select count(Is_Pending_Bill) from MRuDetails where (Is_Pending_Bill is null or Is_Pending_Bill='' or Is_Pending_Bill='P')  and M.category=MRuDetails.cATEGORY  group by MRuDetails.cATEGORY ) as Pending,\n" +
                    "(select sum(UNITS_CONS) from Billdetails as B where  B.category= M.category ) as Unit,\n" +
                    "(select sum(PROMPT_AMT) from Billdetails as B where  B.category= M.category ) as Amount\n" +
                    "from MRuDetails as M group by M.category", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                Report rpt = new Report();
                rpt.set_CategoryName(cur.getString(cur.getColumnIndex("CATEGORY")));
                rpt.set_TotalCon(cur.getString(cur.getColumnIndex("Total")));
                rpt.set_Billed(cur.getString(cur.getColumnIndex("Billed")));
                rpt.set_Units(cur.getString(cur.getColumnIndex("Unit")));
                rpt.set_BilledAmt(cur.getString(cur.getColumnIndex("Amount")));
                rpt.set_Pending(cur.getString(cur.getColumnIndex("Pending")));
                reportList.add(rpt);
            }
            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return reportList;

    }

    @SuppressLint("Range")
    public ArrayList<Report> getUnBilledReport() {

        ArrayList<Report> reportList = new ArrayList<Report>();

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cur = db.rawQuery("select  count(Id) as total,sum(case when Is_Pending_Bill='G' then 1 else 0 end) as billed ,\n" +
                    "sum(case when Is_Pending_Bill is null  or Is_Pending_Bill='' or Is_Pending_Bill='R' or Is_Pending_Bill='U' then 1 else 0 end) as unbilled\n" +
                    " from MRuDetails", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                Report rpt = new Report();
                rpt.set_TotalCon(cur.getString(cur.getColumnIndex("total")));
                rpt.set_Billed(cur.getString(cur.getColumnIndex("billed")));
                rpt.set_UnBilled(cur.getString(cur.getColumnIndex("unbilled")));
                reportList.add(rpt);
            }
            cur.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return reportList;

    }
    @SuppressLint("Range")
    public ArrayList<Report> getUntracedReport() {

        ArrayList<Report> reportList = new ArrayList<Report>();

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cur = db.rawQuery("select  count(Id) as total,sum(case when Is_Pending_Bill='G' then 1 else 0 end) as billed ,\n" +
                    "sum(case when Is_Pending_Bill is null  or Is_Pending_Bill='' then 1 else 0 end) as unbilled\n ," +
                    "sum(case when Is_Pending_Bill='R' or Is_Pending_Bill='U' then 1 else 0 end) as traced\n ," +
                    "sum(case when Is_Pending_Bill='P' then 1 else 0 end) as pending\n" +
                    " from MRuDetails", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                Report rpt = new Report();
                rpt.set_TotalCon(cur.getString(cur.getColumnIndex("total")));
                rpt.set_Billed(cur.getString(cur.getColumnIndex("billed")));
                rpt.set_UnBilled(cur.getString(cur.getColumnIndex("unbilled")));
                rpt.set_traced(cur.getString(cur.getColumnIndex("traced")));
                rpt.set_Pending(cur.getString(cur.getColumnIndex("pending")));
                reportList.add(rpt);
            }
            cur.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
            return null;
        }
        return reportList;

    }
    @SuppressLint("Range")
    public ArrayList<Report> getUnBilledList() {
        ArrayList<Report> reportList = new ArrayList<Report>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery("Select ACT_NO,CNAME,BILL_ADDRESS,METR_UNMETER,Cfathername,CATEGORY from MRUDetails where  Is_Pending_Bill is null or Is_Pending_Bill='' or Is_Pending_Bill='R' or Is_Pending_Bill='U' ", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                Report rpt = new Report();
                rpt.set_AccountNo(cur.getString(cur.getColumnIndex("ACT_NO")));
                rpt.set_Address(cur.getString(cur.getColumnIndex("BILL_ADDRESS")));
                rpt.set_ConName(cur.getString(cur.getColumnIndex("CNAME")));
                rpt.set_Confathername(cur.getString(cur.getColumnIndex("Cfathername")));
                rpt.setUnmeter(cur.getString(cur.getColumnIndex("METR_UNMETER")));
                rpt.set_CategoryName(cur.getString(cur.getColumnIndex("CATEGORY")));
                reportList.add(rpt);
            }

            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return reportList;

    }
    @SuppressLint("Range")
    public ArrayList<Report> getUnBilledListdatewise() {
        ArrayList<Report> reportList = new ArrayList<Report>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery("Select ACT_NO,CNAME,BILL_ADDRESS,METR_UNMETER,Cfathername,Last_bill_date from MRUDetails where  Is_Pending_Bill is null or Is_Pending_Bill='' or Is_Pending_Bill='R' or Is_Pending_Bill='U' order by datetime(Last_bill_date) asc", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                Report rpt = new Report();
                rpt.set_AccountNo(cur.getString(cur.getColumnIndex("ACT_NO")));
                rpt.set_Address(cur.getString(cur.getColumnIndex("BILL_ADDRESS")));
                rpt.set_ConName(cur.getString(cur.getColumnIndex("CNAME")));
                rpt.set_Confathername(cur.getString(cur.getColumnIndex("Cfathername")));
                rpt.setUnmeter(cur.getString(cur.getColumnIndex("METR_UNMETER")));
                rpt.set_last_bill_date(cur.getString(cur.getColumnIndex("Last_bill_date")).substring(0,10));
                reportList.add(rpt);
            }
            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return reportList;

    }
    @SuppressLint("Range")
    public ArrayList<Report> getUntracedList() {
        ArrayList<Report> reportList = new ArrayList<Report>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery("Select ACT_NO,CNAME,BILL_ADDRESS,METR_UNMETER,Cfathername from MRUDetails where  Is_Pending_Bill is null or Is_Pending_Bill='' ", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                Report rpt = new Report();
                rpt.set_AccountNo(cur.getString(cur.getColumnIndex("ACT_NO")));
                rpt.set_Address(cur.getString(cur.getColumnIndex("BILL_ADDRESS")));
                rpt.set_ConName(cur.getString(cur.getColumnIndex("CNAME")));
                rpt.set_Confathername(cur.getString(cur.getColumnIndex("Cfathername")));
                rpt.setUnmeter(cur.getString(cur.getColumnIndex("METR_UNMETER")));
                reportList.add(rpt);
            }
            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return reportList;

    }

    @SuppressLint("Range")
    public ArrayList<Report> getNetworkFailureList() {

        ArrayList<Report> reportList = new ArrayList<Report>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cur = db.rawQuery("Select ACT_NO,CNAME,BILL_ADDRESS from MRUDetails where Is_Pending_Bill='P' ", null);
            int x = cur.getCount();

            while (cur.moveToNext()) {
                Report rpt = new Report();
                rpt.set_AccountNo(cur.getString(cur.getColumnIndex("ACT_NO")));
                rpt.set_Address(cur.getString(cur.getColumnIndex("BILL_ADDRESS")));
                rpt.set_ConName(cur.getString(cur.getColumnIndex("CNAME")));
                reportList.add(rpt);
            }

            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return reportList;

    }


    @SuppressLint("Range")
    public ArrayList<Report> getOutSortList() {
        ArrayList<Report> reportList = new ArrayList<Report>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery("Select ACT_NO,CON_ID,CNAME,BILL_ADDRESS from BillDetails where AMBI_Flag='4X' or AMBI_Flag='NE' or AMBI_Flag='LK' or AMBI_Flag='X' ", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                Report rpt = new Report();
                rpt.set_AccountNo(cur.getString(cur.getColumnIndex("ACT_NO")));
                rpt.set_Address(cur.getString(cur.getColumnIndex("BILL_ADDRESS")));
                rpt.set_ConName(cur.getString(cur.getColumnIndex("CNAME")));
                rpt.set_ConID(cur.getString(cur.getColumnIndex("CON_ID")));
                reportList.add(rpt);
            }
            cur.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return reportList;
    }
    @SuppressLint("Range")
    public ArrayList<Report> getpdList() {
        ArrayList<Report> reportList = new ArrayList<Report>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery("Select ACT_NO,CON_ID,CNAME,BILL_ADDRESS from MRUDetails where Is_Pending_Bill='R' and reason IN('PA','AMBIGUITY')", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                Report rpt = new Report();
                rpt.set_AccountNo(cur.getString(cur.getColumnIndex("ACT_NO")));
                rpt.set_Address(cur.getString(cur.getColumnIndex("BILL_ADDRESS")));
                rpt.set_ConName(cur.getString(cur.getColumnIndex("CNAME")));
                rpt.set_ConID(cur.getString(cur.getColumnIndex("CON_ID")));
                reportList.add(rpt);
            }
            cur.close();
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return reportList;
    }
    public long updateMissingConInMru(MRUDetails mru) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("ACT_NO", mru.get_ACT_NO());
            values.put("BILL_ADDRESS", mru.get_BILL_ADDRESS());
            values.put("CATEGORY", mru.get_CATEGORY());
            values.put("CNAME", mru.get_CNAME());
            values.put("CONTACT_NUM", mru.get_CONTACT_NUM());
            values.put("CON_ID", mru.get_CON_ID());
            values.put("DT_NO", mru.get_DT_NO());
            values.put("LOAD", mru.get_LOAD());
            values.put("METER_NO", mru.get_METER_NO());
            values.put("METR_UNMETER", mru.get_METR_UNMETER());
            values.put("OLD_CON_ID", mru.get_OLD_CON_ID());
            values.put("PHASE", mru.get_PHASE());
            values.put("RECYCLE_UNIT", mru.get_RECYCLE_UNIT());
            values.put("SECTION_ID", mru.get_SECTION_ID());
            values.put("SECTION_NAME", mru.get_SECTION_NAME());
            values.put("PRE_READ", mru.get_PREVIOUS_READ());
            values.put("previous_read_status", mru.get_Previous_read_stat());
            values.put("POW_FACT", mru.get_POW_FACT());
            values.put("REC_DEM", mru.get_REC_DEM());
            values.put("is_address_updated", mru.get_Is_address_updated());
            values.put("OLD_BOOK_NO", mru.get_OLD_BOOK_NO());
            values.put("NEW_BOOK_NO", mru.get_NEW_BOOK_NO());
            values.put("gender",    mru.get_Gender());
            String[] whereArgs = new String[]{mru.get_ACT_NO()};
            c = db.update("MRUDetails", values, "ACT_NO=?", whereArgs);
            if (!(c > 0)) {
                c = db.insert("MRUDetails", null, values);
            }
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }
    public long insertMissingConDiffMru(MRUDetails mru) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("ACT_NO", mru.get_ACT_NO());
            values.put("BILL_ADDRESS", mru.get_BILL_ADDRESS());
            values.put("CATEGORY", mru.get_CATEGORY());
            values.put("CNAME", mru.get_CNAME());
            values.put("CONTACT_NUM", mru.get_CONTACT_NUM());
            values.put("CON_ID", mru.get_CON_ID());
            values.put("DT_NO", mru.get_DT_NO());
            values.put("LOAD", mru.get_LOAD());
            values.put("METER_NO", mru.get_METER_NO());
            values.put("METR_UNMETER", mru.get_METR_UNMETER());
            values.put("OLD_CON_ID", mru.get_OLD_CON_ID());
            values.put("PHASE", mru.get_PHASE());
            values.put("RECYCLE_UNIT", mru.get_RECYCLE_UNIT());
            values.put("SECTION_ID", mru.get_SECTION_ID());
            values.put("SECTION_NAME", mru.get_SECTION_NAME());
            values.put("PRE_READ", mru.get_PREVIOUS_READ());
            values.put("POW_FACT", mru.get_POW_FACT());
            values.put("REC_DEM", mru.get_REC_DEM());
            values.put("BOOK_NO", mru.get_OLD_BOOK_NO());
            values.put("NEW_BOOK_NO", mru.get_NEW_BOOK_NO());
            values.put("RESPONSE_MESSAGE", mru.get_RESPONSE_MESSAGE());
            String[] whereArgs = new String[]{mru.get_ACT_NO()};
            c = db.update("MRUDetailsForDiffUser", values, "ACT_NO=?", whereArgs);
            if (!(c > 0)) {
                c = db.insert("MRUDetailsForDiffUser", null, values);
            }
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }

    public long insertmobiledtnumber(String Mobileno, String dtno, String acc_no) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Act_no", acc_no.trim());
            values.put("Mobile_no", Mobileno.trim());
            values.put("Dt_no", dtno.trim());
            String[] whereArgs = new String[]{acc_no.trim()};
            c = db.update("mobile_dt_table", values, "Act_no=?", whereArgs);
            if (!(c > 0)) {
                c = db.insert("mobile_dt_table", null, values);
            }
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }


    public long updateMissingConMru(String MobileNo, String DtNo, String AcNo, String IsSync) {

        long c = -1;
        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("CONTACT_NUM", MobileNo);
            values.put("DT_NO", DtNo);
            values.put("IS_SYNC", IsSync);

            String[] whereArgs = new String[]{AcNo};
            c = db.update("MRUDetailsForDiffUser", values, "ACT_NO=?", whereArgs);

            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }


    public long savePhotoInMissingConMru(String latitude, String longitude, String photoPath,
                                         String AcNo, String Date, String sideAccNo) {

        long c = -1;
        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("Latitude", latitude);
            values.put("Longitude", longitude);
            values.put("Meter_Photo", photoPath);
            values.put("Read_Date", Date);
            values.put("SideAccountNo", sideAccNo);
            String[] whereArgs = new String[]{AcNo};
            c = db.update("MRUDetailsForDiffUser", values, "ACT_NO=?", whereArgs);
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }
    @SuppressLint("Range")
    public MRUDetails getMissingMRU(String pos, String value) {
        // PlaceDataSQL placeData = new PlaceDataSQL(MainActivity.this);
        MRUDetails mru = new MRUDetails();
        try {

            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{value.trim()};
            Cursor cur = db.rawQuery("Select * from MRUDetailsForDiffUser WHERE " + pos
                    + "=?", params);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                mru.set_ACT_NO(cur.getString(cur.getColumnIndex("ACT_NO")));
                mru.set_BILL_ADDRESS(cur.getString(cur.getColumnIndex("BILL_ADDRESS")));
                mru.set_OLD_BOOK_NO(cur.getString(cur.getColumnIndex("BOOK_NO")));
                mru.set_CATEGORY(cur.getString(cur.getColumnIndex("CATEGORY")));
                mru.set_CNAME(cur.getString(cur.getColumnIndex("CNAME")));
                mru.set_CON_ID(cur.getString(cur.getColumnIndex("CON_ID")));
                mru.set_CONTACT_NUM(cur.getString(cur.getColumnIndex("CONTACT_NUM")));
                mru.set_DT_NO(cur.getString(cur.getColumnIndex("DT_NO")));
                mru.set_LOAD(cur.getString(cur.getColumnIndex("LOAD")));
                mru.set_METER_NO(cur.getString(cur.getColumnIndex("METER_NO")));
                mru.set_METR_UNMETER(cur.getString(cur.getColumnIndex("METR_UNMETER")));
                mru.set_NEW_BOOK_NO(cur.getString(cur.getColumnIndex("NEW_BOOK_NO")));
                mru.set_OLD_CON_ID(cur.getString(cur.getColumnIndex("OLD_CON_ID")));
                mru.set_PHASE(cur.getString(cur.getColumnIndex("PHASE")));
                mru.set_RECYCLE_UNIT(cur.getString(cur.getColumnIndex("RECYCLE_UNIT")));
                mru.set_SECTION_ID(cur.getString(cur.getColumnIndex("SECTION_ID")));
                mru.set_SECTION_NAME(cur.getString(cur.getColumnIndex("SECTION_NAME")));
                mru.set_PREVIOUS_READ(cur.getString(cur.getColumnIndex("PRE_READ")));
                mru.set_IS_SYNC(cur.getString(cur.getColumnIndex("IS_SYNC")));
                mru.set_Cur_Read(cur.getString(cur.getColumnIndex("Cur_Read")));
                mru.set_Reading_Status(cur.getString(cur.getColumnIndex("Reading_Status")));
                mru.set_Read_Date(cur.getString(cur.getColumnIndex("Read_Date")));
                mru.set_Cycle_Complete(cur.getString(cur.getColumnIndex("Cycle_Complete")));
                mru.set_Power_Factor(cur.getString(cur.getColumnIndex("Power_Factor")));
                mru.set_Rec_Demand(cur.getString(cur.getColumnIndex("Rec_Demand")));
                mru.set_Meter_Photo(cur.getString(cur.getColumnIndex("Meter_Photo")));
                mru.set_Latitude(cur.getString(cur.getColumnIndex("Latitude")));
                mru.set_Longitude(cur.getString(cur.getColumnIndex("Longitude")));
                mru.set_Is_Pending_Bill(cur.getString(cur.getColumnIndex("Is_Pending_Bill")));
                mru.set_POW_FACT(cur.getString(cur.getColumnIndex("POW_FACT")));
                mru.set_REC_DEM(cur.getString(cur.getColumnIndex("REC_DEM")));
                mru.set_IsMeterChange(cur.getString(cur.getColumnIndex("IsMeterChange")));
                mru.set_MeterNo(cur.getString(cur.getColumnIndex("MeterNo")));
                mru.set_SIDE_ACC_NO(cur.getString(cur.getColumnIndex("SideAccountNo")));
            }
            cur.close();
            db.close();

        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return mru;

    }

    public long saveMeterStatusForMissing(String readStatus, String reading, String PF,
                                          String maxDemand, String isRecycle, String AcNo, String isPending) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Reading_Status", readStatus);
            values.put("Cur_Read", reading);
            values.put("Power_Factor", PF);
            values.put("Rec_Demand", maxDemand);
            values.put("Cycle_Complete", isRecycle);
            values.put("Is_Pending_Bill", isPending);
            String[] whereArgs = new String[]{AcNo};
            c = db.update("MRUDetailsForDiffUser", values, "ACT_NO=?", whereArgs);
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return c;
        }
        return c;
    }
    @SuppressLint("Range")
    public ArrayList<Block> getallBlock(String distName) {
        ArrayList<Block> blockList = new ArrayList<Block>();
        try {
//CREATE TABLE `Block` ( `DistCode` TEXT, `BlockCode` TEXT, `BlockName` TEXT, `BlockNameHN` TEXT )
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{distName};
            Cursor cur = db
                    .rawQuery(
                            "SELECT Distinct BlockCode,BlockName from Block WHERE DistCode = ? ORDER BY BlockName ",
                            params);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                Block block = new Block();
                block.setBlockCode(cur.getString(cur.getColumnIndex("BlockCode")));
                block.setBlockName(cur.getString(cur.getColumnIndex("BlockName")));
                blockList.add(block);
            }
            cur.close();
            db.close();
            this.getReadableDatabase().close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return blockList;
    }
    @SuppressLint("Range")
    public ArrayList<Dist> getallDist(String distName) {
        ArrayList<Dist> blockList = new ArrayList<Dist>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] params = new String[]{distName};
            Cursor cur = db
                    .rawQuery(
                            "SELECT Distinct DistCode,DistName from Dist  ORDER BY DistName ",
                            null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                Dist block = new Dist();
                block.setDistCode(cur.getString(cur.getColumnIndex("DistCode")));
                block.setDistName(cur.getString(cur.getColumnIndex("DistName")));
                blockList.add(block);
            }
            cur.close();
            db.close();
            this.getReadableDatabase().close();

        } catch (Exception e) {
            // TODO: handle exception

        }
        return blockList;

    }


    @SuppressLint("Range")
    public ArrayList<PanchayatData> getPanchayatLocal(String blkId) {
        ArrayList<PanchayatData> pdetail = new ArrayList<PanchayatData>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery("SELECT * FROM  Panchayat where BlockCode='" + blkId + "' order by PanchayatName", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                PanchayatData panchayat = new PanchayatData();
                panchayat.setPcode(cur.getString(cur.getColumnIndex("PanchayatCode")));
                panchayat.setPname((cur.getString(cur.getColumnIndex("PanchayatName"))));
                pdetail.add(panchayat);
            }
            cur.close();
            db.close();
            this.getReadableDatabase().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdetail;
    }

    @SuppressLint("Range")
    public ArrayList<VILLAGE> getVILLAGE(String panId) {

        ArrayList<VILLAGE> VILLAGEArrayList = new ArrayList<VILLAGE>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery("Select VillageCode, VillageName from Village where PanchayatCode='" + panId + "'", null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                VILLAGE vil = new VILLAGE();
                vil.setVILLAGEID(cur.getString(cur.getColumnIndex("VillageCode")));
                vil.setVILLAGEName(cur.getString(cur.getColumnIndex("VillageName")));
                VILLAGEArrayList.add(vil);
            }
            cur.close();
            db.close();
            this.getReadableDatabase().close();

        } catch (Exception e) {
            // TODO: handle exception
            Log.e("Error", e.getLocalizedMessage());
            Log.e("Error", e.getMessage());
        }
        return VILLAGEArrayList;
    }

    @SuppressLint("Range")
    public ArrayList<TOLLA> getTOLLA(String villageid) {

        // PlaceDataSQL placeData = new PlaceDataSQL(MainActivity.this);
        ArrayList<TOLLA> TOLLAArrayList = new ArrayList<TOLLA>();

        try {

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery("Select distinct TolaID, TolaName from Tola where VillageID='" + villageid + "'", null);

            int x = cur.getCount();

            while (cur.moveToNext()) {

                TOLLA tol = new TOLLA();

                tol.setTOLLAID(cur.getString(cur.getColumnIndex("TolaID")));
                tol.setTOLLAName(cur.getString(cur.getColumnIndex("TolaName")));
                TOLLAArrayList.add(tol);
            }

            cur.close();
            db.close();
            this.getReadableDatabase().close();

        } catch (Exception e) {
            // TODO: handle exception

        }
        return TOLLAArrayList;
    }

    public void insertVILLAGE(ArrayList<VILLAGE> result, String panid) {

        long c = -1;
        try {

            SQLiteDatabase db = this.getWritableDatabase();

            // db.execSQL("Delete from RANGE");
            for (VILLAGE bpiu : result) {

                ContentValues values = new ContentValues();
                values.put("VillageCode", bpiu.getVILLAGEID().trim());
                values.put("VillageName", bpiu.getVILLAGEName().trim());
                values.put("PanchayatCode", panid);
                String[] whereArgs = new String[]{bpiu.getVILLAGEID()};
                c = db.update("Village", values, "VillageCode=?", whereArgs);
                if (!(c > 0)) {
                    c = db.insert("Village", null, values);
                }
            }
            db.close();
            this.getWritableDatabase().close();

        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for VILLAGE");
            // TODO: handle exception
        }
        // return plantationList;
    }

    public void insertPanchayat(ArrayList<PanchayatData> result, String panid) {
        long c = -1;
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            // db.execSQL("Delete from RANGE");
            for (PanchayatData bpiu : result) {
                ContentValues values = new ContentValues();
                values.put("PanchayatCode", bpiu.getPcode().trim());
                values.put("PanchayatName", bpiu.getPname().trim());
                values.put("BlockCode", panid);
                values.put("Total_ward", bpiu.getTotal_ward().trim());
                values.put("Areatype", bpiu.getArea_type().trim());
                String[] whereArgs = new String[]{bpiu.getPcode()};
                c = db.update("Panchayat", values, "PanchayatCode=?", whereArgs);
                if (!(c > 0)) {
                    c = db.insert("Panchayat", null, values);
                }
            }
            db.close();
            this.getWritableDatabase().close();
        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for VILLAGE");
            // TODO: handle exception
        }
        // return plantationList;
    }

    public void insertBlock(ArrayList<Block> result, String distcode, int id) {
        long c = -1;
        String tablename = "";
        tablename = "Block";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            for (Block bpiu : result) {
                ContentValues values = new ContentValues();
                values.put("BlockCode", bpiu.getBlockCode().trim());
                values.put("BlockName", bpiu.getBlockName().trim());
                values.put("DistCode", distcode);
                String[] whereArgs = new String[]{distcode};
                c = db.update(tablename, values, "BlockCode=?", whereArgs);
                if (!(c > 0)) {
                    c = db.insert("Block", null, values);
                }
            }
            db.close();
            this.getWritableDatabase().close();

        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for VILLAGE");
            // TODO: handle exception
        }
        // return plantationList;
    }

    public void insertDist(ArrayList<Dist> result, String distcode, int id) {
        long c = -1;
        String tablename = "";
        tablename = "Dist";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            for (Dist bpiu : result) {
                ContentValues values = new ContentValues();
                values.put("DistCode", bpiu.getDistCode().trim());
                values.put("DistName", bpiu.getDistName().trim());
                String[] whereArgs = new String[]{distcode};
                //  c = db.update(tablename, values, "DistCode=?", whereArgs);
                // if (!(c > 0)) {
                c = db.insert(tablename, null, values);
                //}
            }
            db.close();
            this.getWritableDatabase().close();

        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            // TODO: handle exception
        }
        // return plantationList;
    }

    public long insertaddress(consumer_address conadd) {
        long c = -1;
        String tablename = "consumer_address";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Conid", conadd.getConid().trim());
            values.put("distcode", conadd.getDistCode().trim());
            values.put("blockcode", conadd.getBlockCode().trim());
            values.put("panchayatcode", conadd.getPanchayatcode().trim());
            values.put("vill_code", conadd.getVillcode().trim());
            values.put("toll_code", conadd.getTollcode().trim());
            String[] whereArgs = new String[]{conadd.getConid()};
            c = db.update(tablename, values, "Conid=?", whereArgs);
            if (!(c > 0)) {
                c = db.insert(tablename, null, values);
            }

            db.close();
            this.getWritableDatabase().close();

        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            // TODO: handle exception
        }
        return c;
    }

    public long deleteaddress(String Acc_no) {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String[] DeleteWhere = new String[2];
            //	DeleteWhere[0] = pid;
            db.execSQL("Delete from consumer_address where Conid='" + Acc_no + "'");
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return c;
    }

    public void insertTOLLA(ArrayList<TOLLA> result, String panid) {

        long c = -1;
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            // db.execSQL("Delete from RANGE");
            for (TOLLA tolla : result) {
                ContentValues values = new ContentValues();
                // values.put("PanchayatID", panid);
                values.put("VillageID", panid);
                values.put("TolaID", tolla.getTOLLAID().trim());
                values.put("TolaName", tolla.getTOLLAName().trim());
                String[] whereArgs = new String[]{tolla.getTOLLAID().trim()};
                c = db.update("Tola", values, "TolaID=?", whereArgs);
                if (!(c > 0)) {
                    c = db.insert("Tola", null, values);
                }
            }
            db.close();
            this.getWritableDatabase().close();

        } catch (Exception e) {
            //  Log.e("ERROR 1", e.getLocalizedMessage());
            // Log.e("ERROR 2", e.getMessage());
            //   Log.e("ERROR 3", " WRITING DATA in LOCAL DB for TOLA");
            // TODO: handle exception
        }
        // return plantationList;
    }


    public void insertestablishment(ArrayList<Establishment_details> result, String panid) {

        long c = -1;
        try {

            SQLiteDatabase db = this.getWritableDatabase();
             db.execSQL("Delete from Establishment_deatils");
            for (Establishment_details purpose : result) {
                ContentValues values = new ContentValues();
                values.put("tariff_Id", purpose.get_Tariff_Id().trim());
                values.put("Establishment_type", purpose.get_Establishment().trim());
               /* String[] whereArgs = new String[]{tolla.getTOLLAID().trim()};
                c = db.update("Establishment_deatils", values, "TolaID=?", whereArgs);*/

                    c = db.insert("Establishment_deatils", null, values);

            }
            db.close();
            this.getWritableDatabase().close();

        } catch (Exception e) {
            //  Log.e("ERROR 1", e.getLocalizedMessage());
            // Log.e("ERROR 2", e.getMessage());
            //   Log.e("ERROR 3", " WRITING DATA in LOCAL DB for TOLA");
            // TODO: handle exception
        }
        // return plantationList;
    }


    public void insertlkmdstatus(String[] stringarray, UserDetails user) {
        long c = -1;
        try {
            SQLiteDatabase db1;
            // db.execSQL("Delete from RANGE");
            ContentValues values = new ContentValues();
            // values.put("PanchayatID", panid);
            String[] whereArgs = new String[]{stringarray[4], stringarray[5]};
            if (getmdlkdetailsprevious(user, stringarray[4], stringarray[5]) == 0) {
                db1 = this.getWritableDatabase();
                db1.execSQL("delete from lk_md_per");
                values.put("previous_md", stringarray[2]);
                values.put("previous_lk", stringarray[3]);
                values.put("tot", stringarray[1]);
                values.put("mru_name", stringarray[4]);
                values.put("RDMMYYYY", stringarray[5]);
                values.put("current_md", "0");
                values.put("current_lk", "0");

                //  c = db1.update("lk_md_per", values, "mru_name=? and RDMMYYYY=?", whereArgs);
                //  if (!(c > 0)) {
                c = db1.insert("lk_md_per", null, values);
                //  }
                db1.close();

                this.getWritableDatabase().close();
            } else {
                db1 = this.getWritableDatabase();
                db1.execSQL("delete from lk_md_per");
                ContentValues values1 = new ContentValues();
                values1.put("previous_md", stringarray[2]);
                values1.put("previous_lk", stringarray[3]);
                values1.put("tot", stringarray[1]);
                values1.put("mru_name", stringarray[4]);
                values1.put("RDMMYYYY", stringarray[5]);
                values1.put("current_md", "0");
                values1.put("current_lk", "0");
                c = db1.update("lk_md_per", values1, "mru_name=? and RDMMYYYY=?", whereArgs);
                if (!(c > 0)) {
                    c = db1.insert("lk_md_per", null, values1);
                }
                db1.close();
                this.getWritableDatabase().close();
            }

        } catch (Exception e) {

            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for TOLA");
            // TODO: handle exception
        }
    }

    public void insertlkmdstatus1(String column, int value, UserDetails user) {
        long c = -1;
        try {
            SQLiteDatabase db1;
            ContentValues values = new ContentValues();
            values.put(column, "" + value);
            String[] whereArgs = new String[]{user.get_MRUNo(), user.get_BillMonth() + user.get_BillYear()};
            db1 = this.getWritableDatabase();
            c = db1.update("lk_md_per", values, "mru_name=? and RDMMYYYY=?", whereArgs);
            if (!(c > 0)) {
                c = db1.insert("lk_md_per", null, values);
            }
            db1.close();
            this.getWritableDatabase().close();
        } catch (Exception e) {
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            // TODO: handle exception
        }
    }

    @SuppressLint("Range")
    public lk_md_details getmdlkdetails(UserDetails user) {
        // PlaceDataSQL placeData = new PlaceDataSQL(MainActivity.this);
        lk_md_details lk_md_details1 = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String quary = "SELECT * from lk_md_per where mru_name=? AND RDMMYYYY=?";
            // String quary = "SELECT * from lk_md_per ";
            //Cursor cur = db.rawQuery("SELECT * from lk_md_per where mru_name='" + user.get_MRUNo() + "' AND RDMMYYYY='" + user.get_BillMonth() + user.get_BillYear() + "'", null);
            Cursor cur = db.rawQuery(quary, new String[]{user.get_MRUNo().trim(), (user.get_BillMonth() + user.get_BillYear()).trim()});
            // Cursor cur = db.rawQuery(quary, null);
            int x = cur.getCount();
            while (cur.moveToNext()) {
                lk_md_details1 = new lk_md_details();
                lk_md_details1.setLk_count(cur.getString(cur.getColumnIndex("previous_lk")));
                lk_md_details1.setMd_count(cur.getString(cur.getColumnIndex("previous_md")));
                lk_md_details1.setLk_count_current(cur.getString(cur.getColumnIndex("current_lk")));
                lk_md_details1.setMd_count_current(cur.getString(cur.getColumnIndex("current_md")));
             /*   String mruname = cur.getString(cur.getColumnIndex("mru_name"));
                String mruname1 = cur.getString(cur.getColumnIndex("RDMMYYYY"));*/

            }
            cur.close();
            db.close();
            this.getReadableDatabase().close();

        } catch (Exception e) {
            // TODO: handle exception
        }
        return lk_md_details1;
    }

    public int getmdlkdetailsprevious(UserDetails user, String mruno, String bmonthyear) {
        // PlaceDataSQL placeData = new PlaceDataSQL(MainActivity.this);
        int i = 0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cur = db.rawQuery("Select  mru_name, RDMMYYYY from lk_md_per where mru_name='" + mruno + "' AND RDMMYYYY='" + bmonthyear + "'", null);
            int x = cur.getCount();
            if (x == 0) {
                i = 0;
            } else {
                i = 1;
            }
            cur.close();
            db.close();
            //  this.getReadableDatabase().close();
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("ERROR 1", e.getLocalizedMessage());
            Log.e("ERROR 2", e.getMessage());
            Log.e("ERROR 3", " WRITING DATA in LOCAL DB for TOLA");

        }
        return i;
    }

    public long deletemklkdeatils() {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("Delete from lk_md_per");
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            //    Utiilties.writeIntoLog(Log.getStackTraceString(e));
        }
        return c;
    }

    public long deleteblock() {
        long c = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from Dist");
            db.execSQL("delete from Block");
            db.execSQL("delete from sqlite_sequence where name='Block'");
            db.execSQL("delete from Panchayat");
            db.execSQL("delete from sqlite_sequence where name='Panchayat'");
            db.execSQL("delete from Village");
            db.execSQL("delete from sqlite_sequence where name='Village'");
            db.execSQL("delete from Tola");
            db.execSQL("delete from sqlite_sequence where name='Tola'");
            db.close();
            this.getWritableDatabase().close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return c;
    }

}