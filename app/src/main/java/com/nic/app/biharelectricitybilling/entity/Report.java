package com.nic.app.biharelectricitybilling.entity;


import java.util.ArrayList;

public class Report {
    private String _CategoryName = "";
    private String _TotalCon = "";
    private String _Billed;
    private String _Units = "";
    private String _BilledAmt = "";
    private String _Pending = "";
    private String _UnBilled = "";
    private String _ConID = "";
    private String _Confathername = "";
    private String Unmeter="";
    private String _AccountNo = "";
    private String _ConName = "";
    private String _Address = "";
    private String _traced = "";
    private String _last_bill_date = "";
    private ArrayList<String> uploadlineArrayList ;

    public String get_AccountNo() {
        return _AccountNo;
    }

    public void set_AccountNo(String _AccountNo) {
        this._AccountNo = _AccountNo;
    }
    public String getUnmeter() {
        return Unmeter;
    }

    public void setUnmeter(String unmeter) {
        Unmeter = unmeter;
    }
    public String get_ConName() {
        return _ConName;
    }

    public void set_ConName(String _ConName) {
        this._ConName = _ConName;
    }

    public String get_Address() {
        return _Address;
    }

    public void set_Address(String _Address) {
        this._Address = _Address;
    }

    public String get_last_bill_date() {
        return _last_bill_date;
    }

    public void set_last_bill_date(String _last_bill_date) {
        this._last_bill_date = _last_bill_date;
    }

    public String get_CategoryName() {
        return _CategoryName;
    }

    public void set_CategoryName(String _CategoryName) {
        this._CategoryName = _CategoryName;
    }

    public String get_TotalCon() {
        return _TotalCon;
    }

    public void set_TotalCon(String _TotalCon) {
        this._TotalCon = _TotalCon;
    }

    public String get_Billed() {
        return _Billed;
    }

    public void set_Billed(String _Billed) {
        this._Billed = _Billed;
    }

    public String get_Units() {
        return _Units;
    }

    public void set_Units(String _Units) {
        this._Units = _Units;
    }

    public String get_BilledAmt() {
        return _BilledAmt;
    }

    public void set_BilledAmt(String _BilledAmt) {
        this._BilledAmt = _BilledAmt;
    }

    public String get_Pending() {
        return _Pending;
    }

    public void set_Pending(String _Pending) {
        this._Pending = _Pending;
    }

    public String get_UnBilled() {
        return _UnBilled;
    }

    public void set_UnBilled(String _UnBilled) {
        this._UnBilled = _UnBilled;
    }

    public String get_ConID() {
        return _ConID;
    }

    public String get_Confathername() {
        return _Confathername;
    }

    public void set_Confathername(String _Confathername) {
        this._Confathername = _Confathername;
    }

    public void set_ConID(String _ConID) {
        this._ConID = _ConID;
    }
    public ArrayList<String> getUploadlineArrayList() {
        return uploadlineArrayList;
    }

    public void setUploadlineArrayList(ArrayList<String> uploadlineArrayList) {
        this.uploadlineArrayList = uploadlineArrayList;
    }

    public String get_traced() {
        return _traced;
    }

    public void set_traced(String _traced) {
        this._traced = _traced;
    }
}
