package com.nic.app.biharelectricitybilling.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;


public class UserDetails implements KvmSerializable,Serializable {
	public static Class<UserDetails> USER_CLASS = UserDetails.class;
	public boolean _isAuthenticated = false;
	private String _UserName = "";
	private String _password = "";
	private String _UserID = "";
	private String _MobileNo = "";
	private String _SubdivId = "";
	private String _SubdivName = "";
	private String _MRUNo = "";
	private String _DivId = "";
	private String _DivName = "";
	private String _BillMonth = "";
	private String _BillYear = "";
	private String _LastVisitedOn;
	private String _IMEI = "";
	private String _msg = "";
	private String _distname = "";
	private String _billmsg = "";
	private String _bill_agency = "";
	private String _ocr_agency = "";
	private String _distcode = "";
	public UserDetails() {
	}

	@SuppressWarnings("deprecation")
	public UserDetails(SoapObject obj) {
		this._isAuthenticated = Boolean.parseBoolean(obj.getProperty("authenticated").toString());
		this._UserName = obj.getProperty("username").toString();
		this._IMEI = obj.getProperty("imeino").toString();
		this._LastVisitedOn = obj.getProperty("lastvisited").toString();
		this._MobileNo = obj.getProperty("mobileno").toString();
		this._SubdivId = obj.getProperty("subdiv").toString();
		this._UserID = obj.getProperty("userid").toString();
		this._SubdivName = obj.getProperty("suddivname").toString();
		this._MRUNo = obj.getProperty("bookno").toString();
		this._DivId = obj.getProperty("divid").toString();
		this._DivName = obj.getProperty("divname").toString();
		this._BillMonth = obj.getProperty("bmonth").toString();
		this._BillYear = obj.getProperty("byear").toString();
		this._msg = obj.getProperty("msg").toString();
		this._billmsg = obj.getProperty("bill_msg").toString();
		this._distname = obj.getProperty("distname").toString();
		this._distcode = obj.getProperty("distid").toString();
		this._bill_agency = obj.getProperty("billAgency").toString();
		this._ocr_agency = obj.getProperty("ocrAgency").toString();
	}
	
	public String get_BillMonth() {
		return _BillMonth;
	}

	public void set_BillMonth(String _BillMonth) {
		this._BillMonth = _BillMonth;
	}

	public String get_BillYear() {
		return _BillYear;
	}

	public void set_BillYear(String _BillYear) {
		this._BillYear = _BillYear;
	}

	public String get_MRUNo() {
		return _MRUNo;
	}

	public void set_MRUNo(String _MRUNo) {
		this._MRUNo = _MRUNo;
	}

	public String get_DivId() {
		return _DivId;
	}

	public void set_DivId(String _DivId) {
		this._DivId = _DivId;
	}

	public String get_DivName() {
		return _DivName;
	}

    public String get_billmsg() {
        return _billmsg;
    }

    public void set_billmsg(String _billmsg) {
        this._billmsg = _billmsg;
    }

    public void set_DivName(String _DivName) {
		this._DivName = _DivName;
	}

	public String get_SubdivName() {
		return _SubdivName;
	}

	public void set_SubdivName(String _SubdivName) {
		this._SubdivName = _SubdivName;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 8;
	}

	@Override
	public Object getProperty(int index) {
		Object object = null;
		switch (index) {
		case 0: {
			object = this._isAuthenticated;
			break;
		}
		case 1: {
			object = this._UserName;
			break;
		}
		
		case 2: {
			object = this._LastVisitedOn;
			break;
		}
		
		case 3: {
			object = this._UserID;
			break;
		}
		
		case 4: {
			object = this.get_IMEI();
			break;

		}

		}
		return object;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable arg1,
			PropertyInfo propertyInfo) {
		switch (index) {
		case 0: {
			propertyInfo.name = "isAuthenticated";
			propertyInfo.type = PropertyInfo.BOOLEAN_CLASS;
			break;
		}
		case 1: {
			propertyInfo.name = "UserName";
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			break;
		}
		
		case 2: {
			propertyInfo.name = "LastVisitedOn";
			propertyInfo.type = Date.class;
			break;
		}
		
		case 3: {
			propertyInfo.name = "UserID";
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			break;
		}
		
		case 4: {
			propertyInfo.name = "IMEI";
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			break;
		}
		}
	}

	@Override
	public void setProperty(int index, Object obj) {
		switch (index) {
		case 0: {
			this._isAuthenticated = Boolean.parseBoolean(obj.toString());
			break;
		}
		case 1: {
			this._UserName = obj.toString();
			break;
		}
		
		case 2: {
			this._LastVisitedOn = obj.toString();
			break;
		}
		
		case 3: {
			this._UserID = obj.toString();
			break;
		}
		
		case 4: {
			this.set_IMEI(obj.toString());
			break;
		}
		}
	}

	/*public boolean getAuthenticated() {
		return _isAuthenticated;
	}

	public void setAuthenticated(boolean _isAuthenticated) {
		this._isAuthenticated = _isAuthenticated;
	}*/

	public String get_UserName() {
		return _UserName;
	}

	public void set_UserName(String _UserName) {
		this._UserName = _UserName;
	}

	public String get_LastVisitedOn() {
		return _LastVisitedOn;
	}

	public void set_LastVisitedOn(String _LastVisitedOn) {
		this._LastVisitedOn = _LastVisitedOn;
	}

	


	public String get_UserID() {
		return _UserID;
	}

	public void set_UserID(String _UserID) {
		this._UserID = _UserID;
	}

	public String get_IMEI() {
		return _IMEI;
	}

	public void set_IMEI(String _IMEI) {
		this._IMEI = _IMEI;
	}

	public String get_password() {
		return _password;
	}

	public void set_password(String _password) {
		this._password = _password;
	}
	
	public boolean is_isAuthenticated() {
		return _isAuthenticated;
	}

	public void set_isAuthenticated(boolean _isAuthenticated) {
		this._isAuthenticated = _isAuthenticated;
	}

	public String get_msg() {
		return _msg;
	}

	public void set_msg(String _msg) {
		this._msg = _msg;
	}

	public String get_MobileNo() {
		return _MobileNo;
	}

	public void set_MobileNo(String _MobileNo) {
		this._MobileNo = _MobileNo;
	}

	public String get_SubdivId() {
		return _SubdivId;
	}

	public void set_SubdivId(String _SubdivId) {
		this._SubdivId = _SubdivId;
	}

	public String get_distname() {
		return _distname;
	}

	public void set_distname(String _distname) {
		this._distname = _distname;
	}

	public String get_distcode() {
		return _distcode;
	}

	public void set_distcode(String _distcode) {
		this._distcode = _distcode;
	}

	public String get_bill_agency() {
		return _bill_agency;
	}

	public void set_bill_agency(String _bill_agency) {
		this._bill_agency = _bill_agency;
	}

	public String get_ocr_agency() {
		return _ocr_agency;
	}

	public void set_ocr_agency(String _ocr_agency) {
		this._ocr_agency = _ocr_agency;
	}

	@Override
	public String toString() {
		return "UserDetails{" +
				"_isAuthenticated=" + _isAuthenticated +
				", _UserName='" + _UserName + '\'' +
				", _password='" + _password + '\'' +
				", _UserID='" + _UserID + '\'' +
				", _MobileNo='" + _MobileNo + '\'' +
				", _SubdivId='" + _SubdivId + '\'' +
				", _SubdivName='" + _SubdivName + '\'' +
				", _MRUNo='" + _MRUNo + '\'' +
				", _DivId='" + _DivId + '\'' +
				", _DivName='" + _DivName + '\'' +
				", _BillMonth='" + _BillMonth + '\'' +
				", _BillYear='" + _BillYear + '\'' +
				", _LastVisitedOn='" + _LastVisitedOn + '\'' +
				", _IMEI='" + _IMEI + '\'' +
				", _msg='" + _msg + '\'' +
				", _distname='" + _distname + '\'' +
				", _billmsg='" + _billmsg + '\'' +
				", _bill_agency='" + _bill_agency + '\'' +
				", _ocr_agency='" + _ocr_agency + '\'' +
				", _distcode='" + _distcode + '\'' +
				'}';
	}
}
