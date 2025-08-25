package com.nic.app.biharelectricitybilling.Tvsprinter;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefClass {

    private static String IS_LOGIN = "IS_LOGIN";
    private static final String KEY_ISCHECKOUT = "KEY_ISCHECKOUT";
    private static final String KEY_BT = "KEY_BT";
    private static final String KEY_MAC = "KEY_MAC";


    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;



    public boolean checkLogin() {
        return preferences.getBoolean(IS_LOGIN, false);
    }

    public String getKeyBt() {
        return preferences.getString(KEY_BT, "");
    }

    public void setKeyBt(String Bt) {
        editor.putString(KEY_BT, Bt);
        editor.commit();
    }

    public String getKeyMac() {
        return preferences.getString(KEY_MAC, "");
    }

    public void setKeyMac(String mac) {
        editor.putString(KEY_MAC, KEY_MAC);
        editor.commit();
    }

    public SharedPrefClass(Context context) {
        preferences = context.getSharedPreferences("TvsShared", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();
    }




    public void CreateLoginSession( String BTNAME, String BTMAC) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_BT, BTNAME);
        editor.putString(KEY_MAC, BTMAC);

        editor.commit();
    }
}


