package com.nic.app.biharelectricitybilling.entity;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.io.Serializable;
import java.util.Hashtable;

public class dconsumer_details implements KvmSerializable, Serializable {

	private static final long serialVersionUID = 1L;

	public static Class<dconsumer_details> BLOCK_CLASS = dconsumer_details.class;

	private String cname = "";
	private String cfname = "";
	private String disconnecteddate = "";
	private String con_id = "";
	private String sub_div_id = "";
	private String msg_str = "";


	public dconsumer_details() {
		super();
	}

	public dconsumer_details(SoapObject obj) {
		this.cname = obj.getProperty("CNAME").toString();
		this.cfname = obj.getProperty("CFHNAME").toString();
		this.disconnecteddate = obj.getProperty("DISCONN_DATE").toString();
		this.con_id = obj.getProperty("CON_ID").toString();
		this.sub_div_id = obj.getProperty("SUB_DIV_ID").toString();
		this.msg_str = obj.getProperty("RESP_MSG").toString();
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

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getCfname() {
		return cfname;
	}

	public void setCfname(String cfname) {
		this.cfname = cfname;
	}

	public String getDisconnecteddate() {
		return disconnecteddate;
	}

	public void setDisconnecteddate(String disconnecteddate) {
		this.disconnecteddate = disconnecteddate;
	}

	public String getCon_id() {
		return con_id;
	}

	public void setCon_id(String con_id) {
		this.con_id = con_id;
	}

	public String getSub_div_id() {
		return sub_div_id;
	}

	public void setSub_div_id(String sub_div_id) {
		this.sub_div_id = sub_div_id;
	}

	public String getMsg_str() {
		return msg_str;
	}

	public void setMsg_str(String msg_str) {
		this.msg_str = msg_str;
	}
}
