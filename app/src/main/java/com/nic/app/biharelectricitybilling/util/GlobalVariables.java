package com.nic.app.biharelectricitybilling.util;


import android.location.Location;

import com.nic.app.biharelectricitybilling.entity.UserDetails;

public class GlobalVariables {

	public static UserDetails LoggedUser;
	public static boolean isOffline = false;
	public static boolean isOfflineGPS = false;
	
	public static String MunicipalCorporationId="";
	
	public static String WardId="";
	public static String AreaId="";
	public static String UserId="";
	public static Location glocation=null;
	public static String Last_Visited="";
	//public final static String TargetURL = "http://eservices.bih.nic.in/bcd/mUserReport.aspx";
	
	//public static int fyearBack ;
	//public static String fySelectd="";
	public static boolean isFinishing_MRS=false;
}
