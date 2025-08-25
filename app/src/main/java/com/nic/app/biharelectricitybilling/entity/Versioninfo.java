package com.nic.app.biharelectricitybilling.entity;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.Hashtable;


public class Versioninfo implements KvmSerializable {


    public static Class<Versioninfo> Versioninfo_CLASS = Versioninfo.class;
    private String adminTitle;
    private String adminMsg;
    private String updateTile;
    private String updateMsg;
    private String appUrl;
    private String role;
    private String imei;
    private String Appversion;
    private boolean isVerUpdated;
    private boolean isValidDevice;
    private int priority;


    public Versioninfo() {
    }
    public Versioninfo(SoapObject obj) {
        this.adminMsg = obj.getProperty("ADMINMSG").toString();
        this.adminTitle = obj.getProperty("ADMINTITLE").toString();
        this.appUrl = obj.getProperty("APPURL").toString();
        this.isVerUpdated = Boolean.parseBoolean(obj.getProperty("ISUPDATED").toString());
        this.isValidDevice = Boolean.parseBoolean(obj.getProperty("ISVALIDDEVICE").toString());
        this.priority = Integer.valueOf(obj.getProperty("PRIORITY")
                .toString().trim());
        this.role = obj.getProperty("ROLE").toString();
        this.updateTile = obj.getProperty("UPDATETITLE").toString();
        this.updateMsg = obj.getProperty("UPDATEMSG").toString();
        this.Appversion = obj.getProperty("VER").toString();
    }
    public String getAdminMsg() {
        return adminMsg;
    }

    public void setAdminMsg(String adminMsg) {
        this.adminMsg = adminMsg;
    }

    public String getAdminTitle() {
        return adminTitle;
    }

    public void setAdminTitle(String adminTitle) {
        this.adminTitle = adminTitle;
    }

    public String getUpdateTile() {
        return updateTile;
    }

    public void setUpdateTile(String updateTile) {
        this.updateTile = updateTile;
    }

    public String getUpdateMsg() {
        return updateMsg;
    }

    public void setUpdateMsg(String updateMsg) {
        this.updateMsg = updateMsg;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public boolean isVerUpdated() {
        return isVerUpdated;
    }

    public void setVerUpdated(boolean isVerUpdated) {
        this.isVerUpdated = isVerUpdated;
    }

    public boolean isValidDevice() {
        return isValidDevice;
    }

    public void setValidDevice(boolean isValidDevice) {
        this.isValidDevice = isValidDevice;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAppversion() {
        return Appversion;
    }

    public void setAppversion(String appversion) {
        Appversion = appversion;
    }

    @Override
    public Object getProperty(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPropertyCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setProperty(int arg0, Object arg1) {
        // TODO Auto-generated method stub

    }

}
