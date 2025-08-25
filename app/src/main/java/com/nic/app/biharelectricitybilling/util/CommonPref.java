package com.nic.app.biharelectricitybilling.util;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.nic.app.biharelectricitybilling.entity.UserDetails;


public class CommonPref {

	static Context context;

	CommonPref() {

	}

	CommonPref(Context context) {
		CommonPref.context = context;
	}



	public static void setUserDetails(Context context, UserDetails userInfo) {

		String key = "_USER_DETAILS";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		Editor editor = prefs.edit();

		editor.putString("UserId", userInfo.get_UserID());
		editor.putString("UserName", userInfo.get_UserName());
		editor.putString("UserPassword", userInfo.get_password());
		editor.putString("IMEI", userInfo.get_IMEI());
		editor.putString("LASTVISITED", userInfo.get_LastVisitedOn());
		editor.putString("MOBILENO", userInfo.get_MobileNo());
		editor.putString("SUBDIVID", userInfo.get_SubdivId());
		editor.putString("SUBDIVNAME", userInfo.get_SubdivName());
		editor.putString("DIVID", userInfo.get_DivId());
		editor.putString("DIVNAME", userInfo.get_DivName());
		editor.putString("MRUNO", userInfo.get_MRUNo());
		editor.putString("BILLMONTH", userInfo.get_BillMonth());
		editor.putString("BILLYEAR", userInfo.get_BillYear());
		editor.putString("Billmsg", userInfo.get_billmsg());
		editor.putString("distcode", userInfo.get_distcode());
		editor.putString("distname", userInfo.get_distname());
		editor.putString("ocr_agency", userInfo.get_ocr_agency());
		editor.putString("bill_agency", userInfo.get_bill_agency());
		editor.apply();

	}

	public static void setUserDetails(Context context, String mruno) {
		String key = "_USER_DETAILS";
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("MRUNO",mruno);
		editor.commit();
	}
	public static void settotalconsumer(Context context, String total_consumer,String mmyyyy) {
		String key = "_total_consumer";
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("total",total_consumer);
		editor.putString("mmyyyy",mmyyyy);
		editor.commit();
	}

	public static String gettotalconsumer(Context context) {
		String key = "_total_consumer";
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		String macAddress = prefs.getString("total", "");
		return macAddress;
	}
	public static String getmmyyyy(Context context) {
		String key = "_total_consumer";
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		String macAddress = prefs.getString("mmyyyy", "012015");
		return macAddress;
	}
	public static UserDetails getUserDetails(Context context) {
		String key = "_USER_DETAILS";
		UserDetails userInfo = new UserDetails();
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		userInfo.set_UserID(prefs.getString("UserId", ""));
		userInfo.set_UserName(prefs.getString("UserName", ""));
		userInfo.set_password(prefs.getString("UserPassword", ""));
		userInfo.set_IMEI(prefs.getString("IMEI", ""));
		userInfo.set_LastVisitedOn(prefs.getString("LASTVISITED", ""));
		userInfo.set_MobileNo(prefs.getString("MOBILENO", ""));
		userInfo.set_SubdivId(prefs.getString("SUBDIVID", ""));
		userInfo.set_SubdivName(prefs.getString("SUBDIVNAME", ""));
		userInfo.set_DivId(prefs.getString("DIVID", ""));
		userInfo.set_DivName(prefs.getString("DIVNAME", ""));
		userInfo.set_MRUNo(prefs.getString("MRUNO", ""));
		userInfo.set_BillMonth(prefs.getString("BILLMONTH", ""));
		userInfo.set_BillYear(prefs.getString("BILLYEAR", ""));
		userInfo.set_billmsg(prefs.getString("Billmsg", ""));
		userInfo.set_distcode(prefs.getString("distcode", ""));
		userInfo.set_distname(prefs.getString("distname", ""));

		userInfo.set_ocr_agency(prefs.getString("ocr_agency", ""));
		userInfo.set_bill_agency(prefs.getString("bill_agency", ""));
		return userInfo;
	}

	public static void setPrinterMacAddress(Activity context, String address, String logicalname) {
		String key = "_MAC_ADDRESS";
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("MacAddress", address);
		editor.putString("name", logicalname);
		editor.commit();

	}

	public static void setPrinterMacAddress(Activity context, String address) {
		String key = "_MAC_ADDRESS";
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("MacAddress", address);
		//editor.putString("name", logicalname);
		editor.commit();

	}
	public static String getPrinterMacAddress(Context context) {
		String key = "_MAC_ADDRESS";
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		String macAddress = prefs.getString("MacAddress", "");
		return macAddress;
	}
	public static String getPrinterlogicalname(Context context) {
		String key = "_MAC_ADDRESS";
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		String macAddress = prefs.getString("name", "");
		return macAddress;
	}
	public static void setPrinterType(Context context, String address) {
		String key = "P_Type";
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("PType", address);
		editor.apply();
	}
	public static String getPrinterType(Context context) {
		String key = "P_Type";
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		String Ptype = prefs.getString("PType", "N");
		return Ptype;
	}

	public static void setForceClosing(Context context, String flag) {
		String key = "_FORCE_CLOSE";
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString("Status", flag);
		editor.commit();

	}

	public static String getForceClosing(Context context) {

		String key = "_FORCE_CLOSE";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		String MRUStatus = prefs.getString("Status", "");
		return MRUStatus;
	}

	/*public static void setUserDetailsEmployee(Context context, UserDetailsEmployee userInfo) {

		String key = "_USER_DETAILS_EMPLOYEE";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		Editor editor = prefs.edit();

		editor.putString("MobileNumber", userInfo.getMobileNo());
		editor.putString("UserName", userInfo.getUserName());
		editor.putString("Designation", userInfo.getDesignation());
		editor.putString("EmailId", userInfo.getEmailId());
		editor.putString("OrgCode", userInfo.getOrgCode());
		editor.putString("UserID", userInfo.getUserID());

		editor.commit();

	}
	
	public static UserDetailsEmployee getUserDetailsEmployee(Context context) {

		String key = "_USER_DETAILS_EMPLOYEE";
		UserDetailsEmployee userInfo = new UserDetailsEmployee();
		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		userInfo.setMobileNo(prefs.getString("MobileNumber", ""));
		userInfo.setUserName(prefs.getString("UserName", ""));
		userInfo.setDesignation(prefs.getString("Designation", ""));
		userInfo.setOrgCode(prefs.getString("OrgCode", ""));
		userInfo.setEmailId(prefs.getString("EmailId", ""));
		userInfo.setUserID(prefs.getString("UserID", ""));

		return userInfo;
	}
	*/
	public static void setCheckUpdate(Context context, long dateTime) {

		String key = "_CheckUpdate";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		Editor editor = prefs.edit();

		
		dateTime=dateTime+1*3600000;
		editor.putLong("LastVisitedDate", dateTime);

		editor.commit();

	}

	public static int getCheckUpdate(Context context) {

		String key = "_CheckUpdate";

		SharedPreferences prefs = context.getSharedPreferences(key,
				Context.MODE_PRIVATE);

		long a = prefs.getLong("LastVisitedDate", 0);

		
		if(System.currentTimeMillis()>a)			
			return 1;
		else
			return 0;
	}
}
