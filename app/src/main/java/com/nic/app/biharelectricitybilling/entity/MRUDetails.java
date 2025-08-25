package com.nic.app.biharelectricitybilling.entity;
import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
public class MRUDetails implements KvmSerializable {
	public static Class<MRUDetails> MRU_CLASS = MRUDetails.class;
	private String _ACT_NO = "";
	private String _BILL_ADDRESS = "";
	private String _CATEGORY="";
	private String _CNAME = "";
	private String _CONTACT_NUM = "";
	private String _CON_ID = "";
	private String _DT_NO = "";
	private String _LOAD;
	private String _METER_NO="";
	private String _METR_UNMETER = "";
	private String _OLD_CON_ID = "";
	private String _PHASE = "";
	private String _RECYCLE_UNIT = "";
	private String _IsMeterFixedStatus = "";
	private String _SECTION_ID = "";
	private String _SECTION_NAME = "";
	private String _RESPONSE_MESSAGE = "";
	private String _PREVIOUS_READ = "";
	private String _IS_SYNC = "";
	private String _Cur_Read = "";
	private String _Cfathername = "";
	private String _Reading_Status = "";
	private String _Read_Date="";
	private String _Cycle_Complete = "";
	private String _Power_Factor = "";
	private String _Rec_Demand = "";
	private String _Meter_Photo = "";
	private String _Latitude;
	private String _Longitude="";
	private String _Is_Pending_Bill = "";
	private String _Meter_Photo_Byte = "";
	private String _Pma_Photo_Byte = "";
	private String _IsMeterChange = "";
	private String _MeterNo = "";
	private String _POW_FACT = "";
	private String _REC_DEM = "";
	private String _OLD_BOOK_NO = "";
	private String _NEW_BOOK_NO = "";
	private String _SIDE_ACC_NO = "";
	private String _isPrinted="";
	private String _FHNAME = "";
	private String _APL_BILLING_FLAG = "";
	private String _APL_CONSUMER="";
	private String _Is_address_updated="";
	private String _Previous_read_stat="";
	private String _Lk_Limit="";
	private String _Reason="";
	private String _LAST_BILL_DATE="";
	private String _OCR_Agency="";
	private String _Gender="";
	private String _purpose="";

	private String kwhReading="";
	private String kvahReading="";
	public MRUDetails() {
	}

	@SuppressWarnings("deprecation")
	
	public MRUDetails(SoapObject obj) {

		this._ACT_NO = obj.getProperty("ACT_NO").toString();
		this._BILL_ADDRESS = obj.getProperty("BILL_ADDRESS").toString();
		this._CATEGORY = obj.getProperty("CATEGORY").toString();
		this._CNAME = obj.getProperty("CNAME").toString();
		this._CONTACT_NUM = obj.getProperty("CONTACT_NUM").toString();
		this._CON_ID = obj.getProperty("CON_ID").toString();
		this._DT_NO = obj.getProperty("DT_NO").toString();
		this._FHNAME = obj.getProperty("CFHNAME").toString();
		this._APL_BILLING_FLAG = obj.getProperty("APL_BILL_FLAG").toString();
		this._APL_CONSUMER = obj.getProperty("APL_CONSUMER").toString();
		this._LOAD = obj.getProperty("LOAD").toString();
		this._METER_NO = obj.getProperty("METER_NO").toString();
		this._METR_UNMETER = obj.getProperty("METR_UNMETER").toString();
		this._OLD_CON_ID = obj.getProperty("OLD_CON_ID").toString();
		this._PHASE = obj.getProperty("PHASE").toString();
		this._RECYCLE_UNIT = obj.getProperty("RECYCLE_UNIT").toString();
		this._SECTION_ID = obj.getProperty("SECTION_ID").toString();
		this._SECTION_NAME = obj.getProperty("SECTION_NAME").toString();
		this._RESPONSE_MESSAGE = obj.getProperty("RESPONSE_MESSAGE").toString();
		this._PREVIOUS_READ = obj.getProperty("PRE_READ").toString();
		this._POW_FACT = obj.getProperty("POW_FACT").toString();
		this._REC_DEM = obj.getProperty("REC_DEM").toString();
		this._Is_address_updated = obj.getProperty("IS_ADD_UPDATED").toString();
		this._Previous_read_stat = obj.getProperty("PREV_READ_STAT").toString();
		this._Lk_Limit = obj.getProperty("LK_LIMIT").toString();
		this._LAST_BILL_DATE = obj.getProperty("LAST_BILL_DATE").toString();
		 this._Gender = obj.getProperty("GENDER").toString();
		 this._purpose = obj.getProperty("ESTABLISHMENT_TYPE").toString();
		//this._OCR_Agency = obj.getProperty("OCR_AGENCY").toString();
		if(obj.getProperty("RESPONSE_MESSAGE").toString().trim().equals("SAME_MRU") || obj.getProperty("RESPONSE_MESSAGE").toString().trim().equals("DIFF_MRU")){
			this._OLD_BOOK_NO = obj.getProperty("BOOK_NO").toString();
			this._NEW_BOOK_NO = obj.getProperty("NEW_BOOK_NO").toString();
		}
		

	}

	public String get_SIDE_ACC_NO() {
		return _SIDE_ACC_NO;
	}

	public void set_SIDE_ACC_NO(String _SIDE_ACC_NO) {
		this._SIDE_ACC_NO = _SIDE_ACC_NO;
	}

	public String get_OLD_BOOK_NO() {
		return _OLD_BOOK_NO;
	}

	public void set_OLD_BOOK_NO(String _OLD_BOOK_NO) {
		this._OLD_BOOK_NO = _OLD_BOOK_NO;
	}

	public String get_NEW_BOOK_NO() {
		return _NEW_BOOK_NO;
	}

	public void set_NEW_BOOK_NO(String _NEW_BOOK_NO) {
		this._NEW_BOOK_NO = _NEW_BOOK_NO;
	}


	public String get_PREVIOUS_READ() {
		return _PREVIOUS_READ;
	}

	public void set_PREVIOUS_READ(String _PREVIOUS_READ) {
		this._PREVIOUS_READ = _PREVIOUS_READ;
	}

	public String get_ACT_NO() {
		return _ACT_NO;
	}

	public void set_ACT_NO(String _ACT_NO) {
		this._ACT_NO = _ACT_NO;
	}

	public String get_BILL_ADDRESS() {
		return _BILL_ADDRESS;
	}

	public void set_BILL_ADDRESS(String _BILL_ADDRESS) {
		this._BILL_ADDRESS = _BILL_ADDRESS;
	}

	
	public String get_CATEGORY() {
		return _CATEGORY;
	}

	public void set_CATEGORY(String _CATEGORY) {
		this._CATEGORY = _CATEGORY;
	}

	public String get_CNAME() {
		return _CNAME;
	}

	public void set_CNAME(String _CNAME) {
		this._CNAME = _CNAME;
	}

	public String get_Pma_Photo_Byte() {
		return _Pma_Photo_Byte;
	}

	public void set_Pma_Photo_Byte(String _Pma_Photo_Byte) {
		this._Pma_Photo_Byte = _Pma_Photo_Byte;
	}

	public String get_CONTACT_NUM() {
		return _CONTACT_NUM;
	}

	public void set_CONTACT_NUM(String _CONTACT_NUM) {
		this._CONTACT_NUM = _CONTACT_NUM;
	}

	public String get_CON_ID() {
		return _CON_ID;
	}

	public void set_CON_ID(String _CON_ID) {
		this._CON_ID = _CON_ID;
	}

	public String get_purpose() {
		return _purpose;
	}

	public void set_purpose(String _purpose) {
		this._purpose = _purpose;
	}

	public String get_DT_NO() {
		return _DT_NO;
	}

	public void set_DT_NO(String _DT_NO) {
		this._DT_NO = _DT_NO;
	}

	public String get_LOAD() {
		return _LOAD;
	}

	public void set_LOAD(String _LOAD) {
		this._LOAD = _LOAD;
	}

	public String get_METER_NO() {
		return _METER_NO;
	}

	public void set_METER_NO(String _METER_NO) {
		this._METER_NO = _METER_NO;
	}

	public String get_METR_UNMETER() {
		return _METR_UNMETER;
	}

	public void set_METR_UNMETER(String _METR_UNMETER) {
		this._METR_UNMETER = _METR_UNMETER;
	}

	public String get_OLD_CON_ID() {
		return _OLD_CON_ID;
	}

	public void set_OLD_CON_ID(String _OLD_CON_ID) {
		this._OLD_CON_ID = _OLD_CON_ID;
	}

	public String get_PHASE() {
		return _PHASE;
	}

	public void set_PHASE(String _PHASE) {
		this._PHASE = _PHASE;
	}

	public String get_RECYCLE_UNIT() {
		return _RECYCLE_UNIT;
	}

	public void set_RECYCLE_UNIT(String _RECYCLE_UNIT) {
		this._RECYCLE_UNIT = _RECYCLE_UNIT;
	}

	public String get_SECTION_ID() {
		return _SECTION_ID;
	}

	public void set_SECTION_ID(String _SECTION_ID) {
		this._SECTION_ID = _SECTION_ID;
	}

	public String get_SECTION_NAME() {
		return _SECTION_NAME;
	}

	public void set_SECTION_NAME(String _SECTION_NAME) {
		this._SECTION_NAME = _SECTION_NAME;
	}

	public String get_Gender() {
		return _Gender;
	}

	public void set_Gender(String _Gender) {
		this._Gender = _Gender;
	}

	public String get_RESPONSE_MESSAGE() {
		return _RESPONSE_MESSAGE;
	}

	public void set_RESPONSE_MESSAGE(String _RESPONSE_MESSAGE) {
		this._RESPONSE_MESSAGE = _RESPONSE_MESSAGE;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getProperty(int index) {
		return null;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable arg1,
			PropertyInfo propertyInfo) {
	}
	@Override
	public void setProperty(int index, Object obj) {
	}
	public String get_IS_SYNC() {
		return _IS_SYNC;
	}

	public void set_IS_SYNC(String _IS_SYNC) {
		this._IS_SYNC = _IS_SYNC;
	}

	public String get_POW_FACT() {
		return _POW_FACT;
	}

	public void set_POW_FACT(String _POW_FACT) {
		this._POW_FACT = _POW_FACT;
	}

	public String get_REC_DEM() {
		return _REC_DEM;
	}

	public void set_REC_DEM(String _REC_DEM) {
		this._REC_DEM = _REC_DEM;
	}

	public String get_IsMeterChange() {
		return _IsMeterChange;
	}

	public void set_IsMeterChange(String _IsMeterChange) {
		this._IsMeterChange = _IsMeterChange;
	}

	public String get_MeterNo() {
		return _MeterNo;
	}

	public void set_MeterNo(String _MeterNo) {
		this._MeterNo = _MeterNo;
	}

	public String get_Meter_Photo_Byte() {
		return _Meter_Photo_Byte;
	}

	public void set_Meter_Photo_Byte(String _Meter_Photo_Byte) {
		this._Meter_Photo_Byte = _Meter_Photo_Byte;
	}

	public String get_Cur_Read() {
		return _Cur_Read;
	}

	public void set_Cur_Read(String _Cur_Read) {
		this._Cur_Read = _Cur_Read;
	}

	public String get_Reading_Status() {
		return _Reading_Status;
	}

	public void set_Reading_Status(String _Reading_Status) {
		this._Reading_Status = _Reading_Status;
	}

	public String get_Read_Date() {
		return _Read_Date;
	}

	public void set_Read_Date(String _Read_Date) {
		this._Read_Date = _Read_Date;
	}

	public String get_Cycle_Complete() {
		return _Cycle_Complete;
	}

	public void set_Cycle_Complete(String _Cycle_Complete) {
		this._Cycle_Complete = _Cycle_Complete;
	}

	public String get_Power_Factor() {
		return _Power_Factor;
	}

	public void set_Power_Factor(String _Power_Factor) {
		this._Power_Factor = _Power_Factor;
	}

	public String get_Rec_Demand() {
		return _Rec_Demand;
	}

	public void set_Rec_Demand(String _Rec_Demand) {
		this._Rec_Demand = _Rec_Demand;
	}

	public String get_Meter_Photo() {
		return _Meter_Photo;
	}

	public void set_Meter_Photo(String _Meter_Photo) {
		this._Meter_Photo = _Meter_Photo;
	}

	public String get_Latitude() {
		return _Latitude;
	}
	public String get_IsMeterFIxedStatus() {
		return _IsMeterFixedStatus;
	}

	public void set_IsMeterFIxedStatus(String _MeterStatus) {
		this._IsMeterFixedStatus = _MeterStatus;
	}

	public String get_LAST_BILL_DATE() {
		return _LAST_BILL_DATE;
	}

	public void set_LAST_BILL_DATE(String _LAST_BILL_DATE) {
		this._LAST_BILL_DATE = _LAST_BILL_DATE;
	}

	public void set_Latitude(String _Latitude) {
		this._Latitude = _Latitude;
	}

	public String get_Longitude() {
		return _Longitude;
	}

	public void set_Longitude(String _Longitude) {
		this._Longitude = _Longitude;
	}

	public String get_Is_Pending_Bill() {
		return _Is_Pending_Bill;
	}

	public void set_Is_Pending_Bill(String _Is_Pending_Bill) {
		this._Is_Pending_Bill = _Is_Pending_Bill;
	}
	public String get_isPrinted() {
		return _isPrinted;
	}

	public String get_Cfathername() {
		return _Cfathername;
	}

	public void set_Cfathername(String _Cfathername) {
		this._Cfathername = _Cfathername;
	}

	public void set_isPrinted(String _isPrinted) {
		this._isPrinted = _isPrinted;
	}

	public String get_APL_BILLING_FLAG() {
		return _APL_BILLING_FLAG;
	}

	public void set_APL_BILLING_FLAG(String _APL_BILLING_FLAG) {
		this._APL_BILLING_FLAG = _APL_BILLING_FLAG;
	}

	public String get_APL_CONSUMER() {
		return _APL_CONSUMER;
	}

	public void set_APL_CONSUMER(String _APL_CONSUMER) {
		this._APL_CONSUMER = _APL_CONSUMER;
	}

	public String get_FHNAME() {
		return _FHNAME;
	}

	public void set_FHNAME(String _FHNAME) {
		this._FHNAME = _FHNAME;
	}

	public String get_Is_address_updated() {
		return _Is_address_updated;
	}

	public void set_Is_address_updated(String _Is_address_updated) {
		this._Is_address_updated = _Is_address_updated;
	}

	public String get_Previous_read_stat() {
		return _Previous_read_stat;
	}

	public void set_Previous_read_stat(String _Previous_read_stat) {
		this._Previous_read_stat = _Previous_read_stat;
	}

	public String get_IsMeterFixedStatus() {
		return _IsMeterFixedStatus;
	}

	public void set_IsMeterFixedStatus(String _IsMeterFixedStatus) {
		this._IsMeterFixedStatus = _IsMeterFixedStatus;
	}

	public String get_Lk_Limit() {
		return _Lk_Limit;
	}

	public void set_Lk_Limit(String _Lk_Limit) {
		this._Lk_Limit = _Lk_Limit;
	}

	public String get_Reason() {
		return _Reason;
	}

	public void set_Reason(String _Reason) {
		this._Reason = _Reason;
	}

	public String get_OCR_Agency() {
		return _OCR_Agency;
	}

	public void set_OCR_Agency(String _OCR_Agency) {
		this._OCR_Agency = _OCR_Agency;
	}

	public String getKwhReading() {
		return kwhReading;
	}

	public void setKwhReading(String kwhReading) {
		this.kwhReading = kwhReading;
	}

	public String getKvahReading() {
		return kvahReading;
	}

	public void setKvahReading(String kvahReading) {
		this.kvahReading = kvahReading;
	}
}
