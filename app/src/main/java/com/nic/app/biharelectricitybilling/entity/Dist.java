package com.nic.app.biharelectricitybilling.entity;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.io.Serializable;
import java.util.Hashtable;

public class Dist implements KvmSerializable, Serializable {

	private static final long serialVersionUID = 1L;

	public static Class<Dist> BLOCK_CLASS = Dist.class;

	private String DistCode = "";
	private String DistName = "";

	public Dist() {
		super();
	}

	public Dist(SoapObject obj) {
		this.DistCode = obj.getProperty("DISTCODE").toString();
		this.DistName = obj.getProperty("DISTNAME").toString();
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

	public String getDistCode() {
		return DistCode;
	}

	public void setDistCode(String distCode) {
		DistCode = distCode;
	}

	public String getDistName() {
		return DistName;
	}

	public void setDistName(String distName) {
		DistName = distName;
	}
}
