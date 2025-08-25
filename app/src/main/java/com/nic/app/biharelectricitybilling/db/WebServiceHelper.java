package com.nic.app.biharelectricitybilling.db;
import android.util.Base64;
import android.util.Log;
import androidx.annotation.Nullable;

import com.nic.app.biharelectricitybilling.entity.BillDetails;
import com.nic.app.biharelectricitybilling.entity.Block;
import com.nic.app.biharelectricitybilling.entity.Dist;
import com.nic.app.biharelectricitybilling.entity.Establishment_details;
import com.nic.app.biharelectricitybilling.entity.MRUDetails;
import com.nic.app.biharelectricitybilling.entity.MRUDetailsmissingconsumer;
import com.nic.app.biharelectricitybilling.entity.PanchayatData;
import com.nic.app.biharelectricitybilling.entity.Payment_Data_Details;
import com.nic.app.biharelectricitybilling.entity.Report;
import com.nic.app.biharelectricitybilling.entity.Survey_Data;
import com.nic.app.biharelectricitybilling.entity.Survey_Data_Details;
import com.nic.app.biharelectricitybilling.entity.TOLLA;
import com.nic.app.biharelectricitybilling.entity.UserDetails;
import com.nic.app.biharelectricitybilling.entity.VILLAGE;
import com.nic.app.biharelectricitybilling.entity.Versioninfo;
import com.nic.app.biharelectricitybilling.entity.consumer_address;
import com.nic.app.biharelectricitybilling.entity.dconsumer_details;
import com.nic.app.biharelectricitybilling.util.Utiilties;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
public class WebServiceHelper {
     public static final String SERVICENAMESPACE = "http://race/";
    //Live Server
    public static final String SERVICEURL1 ="http://energyservices.bsphcl.co.in/Mywebservice/RacewebService?wsdl";
    //stagging Server
    //public static final String SERVICEURL1 ="http://1.6.61.111/Mywebservice/RacewebService?wsdl";
    private static final String Hello_METHOD ="hello";
    public static final String APPVERSION_METHOD ="CheckVersion";
    public static final String AUTHENTICATE_METHOD ="LoginAuthentication";
    private static final String SOAP_ACTION ="RacewebService";
    private static final String MRU_METHOD ="MruDownload";
    private static final String SURVEY_DOWNLOAD_METHOD = "SurveyMruDownload";
    private static final String DCONSUMER_METHOD = "getDisconnectedConsumerList";
    private static final String OUTSORT_BILL_METHOD = "GetOutsortBill";
    private static final String UPDATE_MRU_METHOD = "UpdateConsumerMobileDtno";
    private static final String GENERATE_METHOD = "GenerateBillED";
    private static final String MISSING_METHOD = "SearchMissingConsumer";
    private static final String MISSING_GENERATE_METHOD = "ConfirmMissingConsumer";
    private static final String MRU_FORCE_CLOSE = "MruForceClose";
    private static final String VALIDATE_FIRST_OTP = "validateFirstOTP";
    private static final String SAVE_PHOTO = "SavePhoto";
    private static final String UPDATE_CON_ADDRESS = "updateConsumerAddress";
    private static final String CONSUMER_SURVAY = "ConsumerSurvey";
    private static final String UPDATE_PAY_REASON = "UpdatePaymentReason";

    //staging server
    public static Versioninfo CheckVersion(String imei, String version) {
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE, APPVERSION_METHOD);
            request.addProperty("imei", imei);
            request.addProperty("version", version);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //  envelope.dotNet = true;
            //shet1231411097
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE,
                    Versioninfo.Versioninfo_CLASS.getSimpleName(),
                    Versioninfo.Versioninfo_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1, 1000 * 60 * 2);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Object result = envelope.getResponse();
            if (result != null) {
                Log.d("", result.toString());
                return new Versioninfo((SoapObject) result);
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
    }
    public static UserDetails Login(String User_ID, String Pwd, String imei) {
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE, AUTHENTICATE_METHOD);
            request.addProperty("userid", User_ID);
            request.addProperty("imei", imei);
            request.addProperty("password", Pwd);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE,
                    UserDetails.USER_CLASS.getSimpleName(),
                    UserDetails.USER_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1, 60*1000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Object result = envelope.getResponse();
            if (result != null) {
                Log.d("", result.toString());
                return new UserDetails((SoapObject) result);
            } else
                return null;
            //java.io.IOException: Cleartext HTTP traffic to 192.168.139.58 not permitted
        } catch (Exception e) {
            e.printStackTrace();
            //Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }

    }
    public static String validateotp(String User_ID, String imei, String otp) {
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE, VALIDATE_FIRST_OTP);
            request.addProperty("userid", User_ID);
            request.addProperty("imei", imei);
            request.addProperty("otp", otp);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE,
                    UserDetails.USER_CLASS.getSimpleName(),
                    UserDetails.USER_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1, 60*1000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Object result = envelope.getResponse();
            if (result != null) {
                Log.d("", result.toString());
                return result.toString();
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
    }
/*    public static String callbypost(String url) {
        InputStream inputStream = null;
        String result = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "text/plain");
            HttpResponse httpresponse = httpClient.execute(httpPost);
            inputStream = httpresponse.getEntity().getContent();
            if (inputStream != null) {
                result = streamtoString(inputStream);
            } else {
                result = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }*/
    public static String sendphoto(String Photo) {
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,
                    SAVE_PHOTO);
            request.addProperty("photo_string", Photo);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE,
                    UserDetails.USER_CLASS.getSimpleName(),
                    UserDetails.USER_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1, 1000 * 60 * 2);
            /*androidHttpTransport.call(SERVICENAMESPACE + AUTHENTICATE_METHOD,envelope);*/
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Object result = envelope.getResponse();
            if (result != null) {
                Log.d("", result.toString());
                return result.toString();
            } else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", "" + e);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
    }
    public static ArrayList<MRUDetails> LoadMRU(String User_ID, String Pwd, String imei) {
        Object result;
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,MRU_METHOD);
            request.addProperty("userid", User_ID);
            request.addProperty("imei", imei);
            request.addProperty("password", Pwd);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE, MRUDetails.MRU_CLASS.getSimpleName(), MRUDetails.MRU_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            result = envelope.bodyIn;
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        int TotalProperty = ((SoapObject) result).getPropertyCount();
        ArrayList<MRUDetails> MruList = new ArrayList<MRUDetails>();
        for (int ii = 0; ii < TotalProperty; ii++) {
            if (((SoapObject) result).getProperty(ii) != null) {
                Object property = ((SoapObject) result).getProperty(ii);
                if (property instanceof SoapObject) {
                    SoapObject final_object = (SoapObject) property;
                    MRUDetails userDetails = new MRUDetails(final_object);
                    MruList.add(userDetails);
                    //String s = property.toString();
                }
            } else
                return MruList;
        }
        return MruList;
    }
    public static ArrayList<dconsumer_details> Loaddconsumer(String subdiv_ID, String bookno) {
        Object result;
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,
                    DCONSUMER_METHOD);
            request.addProperty("sub_div_id", subdiv_ID);
            request.addProperty("book_no", bookno);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE, MRUDetails.MRU_CLASS.getSimpleName(), MRUDetails.MRU_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            result = envelope.bodyIn;
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        int TotalProperty = ((SoapObject) result).getPropertyCount();
        ArrayList<dconsumer_details> MruList = new ArrayList<dconsumer_details>();
        for (int ii = 0; ii < TotalProperty; ii++) {
            if (((SoapObject) result).getProperty(ii) != null) {
                Object property = ((SoapObject) result).getProperty(ii);
                if (property instanceof SoapObject) {
                    SoapObject final_object = (SoapObject) property;
                    dconsumer_details userDetails = new dconsumer_details(final_object);
                    MruList.add(userDetails);
                    //String s = property.toString();
                }
            } else
                return MruList;
        }
        return MruList;
    }

    public static ArrayList<BillDetails> BillRequestOutsort(UserDetails userinfo, Report report) {
        String uplres = "";
        BillDetails res = null;
        Object result;
        ArrayList<BillDetails> MruList;
        //ToDo DomParsing
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE, OUTSORT_BILL_METHOD);
            request.addProperty("userid", userinfo.get_UserID());
            request.addProperty("imei", userinfo.get_IMEI());
            request.addProperty("password", userinfo.get_password());
            for (int i = 0; i < report.getUploadlineArrayList().size(); i++) {
                if (uplres.equalsIgnoreCase("")) {
                    uplres = report.getUploadlineArrayList().get(i);
                } else
                    uplres = uplres + "," + report.getUploadlineArrayList().get(i);
            }
            request.addProperty("ConidList", uplres);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE,
                    BillDetails.BILL_CLASS.getSimpleName(),
                    BillDetails.BILL_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
            } catch (IOException e) {
                e.printStackTrace();
                Utiilties.writeIntoLog(Log.getStackTraceString(e));
            }
            result = envelope.bodyIn;
            int TotalProperty = ((SoapObject) result).getPropertyCount();
            MruList = new ArrayList<BillDetails>();
            for (int ii = 0; ii < TotalProperty; ii++) {
                if (((SoapObject) result).getProperty(ii) != null) {
                    Object property = ((SoapObject) result).getProperty(ii);
                    if (property instanceof SoapObject) {
                        SoapObject final_object = (SoapObject) property;
                        BillDetails userDetails = new BillDetails(final_object);
                        MruList.add(userDetails);
                    }
                } else
                    return MruList;
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        return MruList;
    }

    public static MRUDetails LoadMissingConsumetrMRU(String User_ID, String Pwd, String imei, String subDivId, String searchText) {
        Object result;
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,
                    MISSING_METHOD);
            request.addProperty("userid", User_ID);
            request.addProperty("imei", imei);
            request.addProperty("password", Pwd);
            request.addProperty("sub_div_id", subDivId);
            request.addProperty("searchtext", searchText);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE,
                    MRUDetails.MRU_CLASS.getSimpleName(),
                    MRUDetails.MRU_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            result = envelope.getResponse();
            if (result != null) {
                return new MRUDetails((SoapObject) result);
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
    }

    public static ArrayList<MRUDetailsmissingconsumer> searchconsumerbymeterno(String User_ID, String Pwd, String imei, String subDivId, String searchText) {
        Object result;
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,
                    "searchConsumerByMeterNo");
            request.addProperty("userid", User_ID);
            request.addProperty("imei", imei);
            request.addProperty("password", Pwd);
            request.addProperty("search", searchText);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE, MRUDetails.MRU_CLASS.getSimpleName(), MRUDetails.MRU_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            result = envelope.bodyIn;
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        int TotalProperty = ((SoapObject) result).getPropertyCount();
        ArrayList<MRUDetailsmissingconsumer> MruList = new ArrayList<MRUDetailsmissingconsumer>();
        for (int ii = 0; ii < TotalProperty; ii++) {
            if (((SoapObject) result).getProperty(ii) != null) {
                Object property = ((SoapObject) result).getProperty(ii);
                if (property instanceof SoapObject) {
                    SoapObject final_object = (SoapObject) property;
                    MRUDetailsmissingconsumer userDetails = new MRUDetailsmissingconsumer(final_object);
                    MruList.add(userDetails);
                    //String s = property.toString();
                }
            } else
                return MruList;
        }
        return MruList;
    }

    public static String BillRequestForMissing(MRUDetails mru, UserDetails user) {

        String strReturn = "";
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,
                    MISSING_GENERATE_METHOD);

            request.addProperty("userid", user.get_UserID());
            request.addProperty("imei", user.get_IMEI());
            request.addProperty("password", user.get_password());
            request.addProperty("sub_div_id", user.get_SubdivId());
            request.addProperty("act_no", mru.get_ACT_NO());
            request.addProperty("old_book_no", mru.get_OLD_BOOK_NO());
            request.addProperty("new_book_no", mru.get_NEW_BOOK_NO());
            request.addProperty("cur_read", mru.get_Cur_Read());
            request.addProperty("read_stat", mru.get_Reading_Status());
            request.addProperty("read_date", mru.get_Read_Date());
            request.addProperty("cycle_comp", mru.get_Cycle_Complete());
            request.addProperty("met_photo", mru.get_Meter_Photo_Byte());
            request.addProperty("pow_fact", mru.get_Power_Factor());
            request.addProperty("rec_demand", mru.get_Rec_Demand());
            request.addProperty("bmonth", user.get_BillMonth());
            request.addProperty("byear", user.get_BillYear());
            request.addProperty("latitude", mru.get_Latitude());
            request.addProperty("longitude", mru.get_Longitude());
            request.addProperty("side_act_no", mru.get_SIDE_ACC_NO());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            /*envelope.addMapping(SERVICENAMESPACE,
					BillDetails.BILL_CLASS.getSimpleName(),
					BillDetails.BILL_CLASS);*/
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Object result = envelope.getResponse();
            if (result != null) {
                // Log.d("", result.toString());
                strReturn = result.toString();
                return strReturn;
            } else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }

    }

    public static String GetMrudownloadconfirmation(UserDetails user, String Totalconsumer) {

        String strReturn = "";
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE, "updateMRUDownloadStatus");
            request.addProperty("userid", user.get_UserID());
            request.addProperty("imei", user.get_IMEI());
            request.addProperty("password", user.get_password());
            request.addProperty("totalDownloaded", Totalconsumer);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            /*envelope.addMapping(SERVICENAMESPACE,
					BillDetails.BILL_CLASS.getSimpleName(),
					BillDetails.BILL_CLASS);*/
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            // Object result = envelope.getResponse();
            Object result = envelope.getResponse();
            if (result != null) {
                strReturn = result.toString();
                return strReturn;
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }

    }



    public static ArrayList<Establishment_details> GetAllestablishment_type() {
        Object result;
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,
                    "getAllEstablishments");
         /*   request.addProperty("userid", User_ID);*/

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE, MRUDetails.MRU_CLASS.getSimpleName(), MRUDetails.MRU_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            result = envelope.bodyIn;
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        int TotalProperty = ((SoapObject) result).getPropertyCount();
        ArrayList<Establishment_details> MruList = new ArrayList<Establishment_details>();
        for (int ii = 0; ii < TotalProperty; ii++) {
            if (((SoapObject) result).getProperty(ii) != null) {
                Object property = ((SoapObject) result).getProperty(ii);
                if (property instanceof SoapObject) {
                    SoapObject final_object = (SoapObject) property;
                    Establishment_details userDetails = new Establishment_details(final_object);
                    MruList.add(userDetails);
                    //String s = property.toString();
                }
            } else
                return MruList;
        }
        return MruList;
    }

    @Nullable
    public static String ForceCloseMRU(String User_ID, String Pwd, String imei, String mru) {

        String strReturn = "";

        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE, MRU_FORCE_CLOSE);
            request.addProperty("userid", User_ID);
            request.addProperty("imei", imei);
            request.addProperty("password", Pwd);
            request.addProperty("book_no", mru);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Object result = envelope.getResponse();
            if (result != null) {
                strReturn = result.toString();
                return strReturn;
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }

    }

    public static String UpdateMRU(String Mobile_No, String Dt_No, String Ac_No, UserDetails user) {

        Object result;
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE, UPDATE_MRU_METHOD);
            request.addProperty("userid", user.get_UserID());
            request.addProperty("imei", user.get_IMEI());
            request.addProperty("password", user.get_password());
            request.addProperty("sub_div_id", user.get_SubdivId());
            request.addProperty("act_no", Ac_No);
            request.addProperty("mobile_no", Mobile_No);
            request.addProperty("dt_no", Dt_No);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE,
                    MRUDetails.MRU_CLASS.getSimpleName(),
                    MRUDetails.MRU_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            result = envelope.bodyIn;
            if (result != null) {
                // Log.d("", result.toString());
                return ((SoapObject) result).getProperty(0).toString();
            } else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
    }

    public static String Updatereason(String Ac_No, String reason, UserDetails user) {
        Object result;
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,
                    "updateBillingReason");
            request.addProperty("userid", user.get_UserID());
            request.addProperty("imei", user.get_IMEI());
            request.addProperty("password", user.get_password());
            request.addProperty("conid", Ac_No);
            request.addProperty("reason", reason);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE,
                    MRUDetails.MRU_CLASS.getSimpleName(),
                    MRUDetails.MRU_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            result = envelope.bodyIn;
            if (result != null) {
                // Log.d("", result.toString());
                return ((SoapObject) result).getProperty(0).toString();
            } else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
    }
    public static String Updatedisconnectionremarks(String Ac_No, String reason, UserDetails user) {
        Object result;
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,
                    "updateDisconnectionRemarks");
            request.addProperty("userid", user.get_UserID());
            request.addProperty("imei", user.get_IMEI());
            request.addProperty("password", user.get_password());
            request.addProperty("conid", Ac_No);
            request.addProperty("remarks", reason);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE,
                    MRUDetails.MRU_CLASS.getSimpleName(),
                    MRUDetails.MRU_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            result = envelope.bodyIn;
            if (result != null) {
                // Log.d("", result.toString());
                return ((SoapObject) result).getProperty(0).toString();
            } else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
    }
    public static BillDetails BillRequest(MRUDetails mru, UserDetails user) {
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,
                    GENERATE_METHOD);
            request.addProperty("userid", user.get_UserID());
            request.addProperty("imei", user.get_IMEI());
            request.addProperty("password", user.get_password());
            request.addProperty("sub_div_id", user.get_SubdivId());
            request.addProperty("act_no", mru.get_ACT_NO());
            request.addProperty("book_no", user.get_MRUNo());
            request.addProperty("cur_read", mru.get_Cur_Read());
            request.addProperty("read_stat", mru.get_Reading_Status());
            request.addProperty("read_date", mru.get_Read_Date());
            request.addProperty("cycle_comp", mru.get_Cycle_Complete());
            request.addProperty("met_photo", mru.get_Meter_Photo_Byte());
           // Log.e("image byte",mru.get_Meter_Photo_Byte());
            request.addProperty("pow_fact", mru.get_Power_Factor());
            request.addProperty("rec_demand", mru.get_Rec_Demand());
            request.addProperty("bmonth", user.get_BillMonth());
            request.addProperty("byear", user.get_BillYear());
            request.addProperty("latitude", mru.get_Latitude());
            request.addProperty("longitude", mru.get_Longitude());
            request.addProperty("met_change", mru.get_IsMeterChange());
            request.addProperty("meter_no", mru.get_MeterNo());
            request.addProperty("mnf", mru.get_IsMeterFIxedStatus());
            request.addProperty("prev_read_stat", mru.get_Previous_read_stat());
            request.addProperty("ocr_agency", mru.get_OCR_Agency());
            request.addProperty("gender", mru.get_Gender());
            request.addProperty("purpose", mru.get_purpose());
            request.addProperty("kwh_read",mru.getKwhReading());
            request.addProperty("kvah_read",mru.getKvahReading());
           /*  Log.e("ocr agency",mru.get_OCR_Agency());
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log(request.toString());
            Log.e("bill request",mru.get_Previous_read_stat()+","+mru.get_Reading_Status());*/
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE,
                    BillDetails.BILL_CLASS.getSimpleName(),
                    BillDetails.BILL_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Object result = envelope.getResponse();
          //  crashlytics.log(result.toString());
            if (result != null) {
                // Log.d("", result.toString());
                return new BillDetails((SoapObject) result);
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
    }

    public static BillDetails BillRequestAPl(MRUDetails mru, UserDetails user) {
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,
                    "GenerateAplConsBill");
            request.addProperty("userid", user.get_UserID());
            request.addProperty("imei", user.get_IMEI());
            request.addProperty("password", user.get_password());
            request.addProperty("sub_div_id", user.get_SubdivId());
            request.addProperty("act_no", mru.get_ACT_NO());
            request.addProperty("book_no", user.get_MRUNo());
            request.addProperty("cur_read", mru.get_Cur_Read());
            request.addProperty("read_stat", mru.get_Reading_Status());
            request.addProperty("read_date", mru.get_Read_Date());
            request.addProperty("cycle_comp", mru.get_Cycle_Complete());
            request.addProperty("met_photo", mru.get_Meter_Photo_Byte());
            request.addProperty("pma_photo", mru.get_Pma_Photo_Byte());
            request.addProperty("pow_fact", mru.get_Power_Factor());
            request.addProperty("rec_demand", mru.get_Rec_Demand());
            request.addProperty("bmonth", user.get_BillMonth());
            request.addProperty("byear", user.get_BillYear());
            request.addProperty("latitude", mru.get_Latitude());
            request.addProperty("longitude", mru.get_Longitude());
            request.addProperty("met_change", mru.get_IsMeterChange());
            request.addProperty("meter_no", mru.get_MeterNo());
            request.addProperty("mnf", mru.get_IsMeterFIxedStatus());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE,
                    BillDetails.BILL_CLASS.getSimpleName(),
                    BillDetails.BILL_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Object result = envelope.getResponse();
            if (result != null) {
                // Log.d("", result.toString());
                return new BillDetails((SoapObject) result);
            } else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }

    }

    public static ArrayList<Block> loadBlockList(String distcode, int id) {
        String method = "";

        SoapObject request = new SoapObject(SERVICENAMESPACE,
                "getBlockByDistId");
        request.addProperty("distId", distcode);
        Object res1;
        try {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //  envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            //    envelope.addMapping(NIC_SERVICENAMESPACE, VILLAGE.VILLAGE_CLASS.getSimpleName(), VILLAGE.VILLAGE_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(
                    SERVICEURL1);
            androidHttpTransport.call(SERVICENAMESPACE + "getBlockByDistId",
                    envelope);
            res1 = envelope.bodyIn;
        } catch (Exception e) {
            Log.e("Exception1: ", e.getLocalizedMessage());
            Log.e("Exception2: ", e.getMessage());
            return null;
        }
        // int TotalProperty = res1.getPropertyCount();
        int TotalProperty = ((SoapObject) res1).getPropertyCount();
        // String[] divisionList = new String[TotalProperty];
        ArrayList<Block> villArrayList = new ArrayList<Block>();

        for (int ii = 0; ii < TotalProperty; ii++) {
            if (((SoapObject) res1).getProperty(ii) != null) {
                Object property = ((SoapObject) res1).getProperty(ii);
                if (property instanceof SoapObject) {
                    SoapObject final_object = (SoapObject) property;
                    Block vill = new Block(final_object);
                    villArrayList.add(vill);
                }
            } else
                return villArrayList;
        }
        return villArrayList;
    }

    public static ArrayList<Dist> loadDistList(String distcode, int id) {
        String method = "";
        SoapObject request = new SoapObject(SERVICENAMESPACE,
                "getAllDistrict");
        request.addProperty("distId", "0");
        Object res1;
        try {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //  envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            //    envelope.addMapping(NIC_SERVICENAMESPACE, VILLAGE.VILLAGE_CLASS.getSimpleName(), VILLAGE.VILLAGE_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(
                    SERVICEURL1);
            androidHttpTransport.call(SERVICENAMESPACE + "getAllDistrict",
                    envelope);
            res1 = envelope.bodyIn;
        } catch (Exception e) {
            Log.e("Exception1: ", e.getLocalizedMessage());
            Log.e("Exception2: ", e.getMessage());
            return null;
        }
        // int TotalProperty = res1.getPropertyCount();
        int TotalProperty = ((SoapObject) res1).getPropertyCount();
        // String[] divisionList = new String[TotalProperty];
        ArrayList<Dist> villArrayList = new ArrayList<Dist>();
        for (int ii = 0; ii < TotalProperty; ii++) {
            if (((SoapObject) res1).getProperty(ii) != null) {
                Object property = ((SoapObject) res1).getProperty(ii);
                if (property instanceof SoapObject) {
                    SoapObject final_object = (SoapObject) property;
                    Dist vill = new Dist(final_object);
                    villArrayList.add(vill);
                }
            } else
                return villArrayList;
        }
        return villArrayList;
    }

    public static ArrayList<PanchayatData> loadPanchayatList(String blockcode, int id) {
        SoapObject request = new SoapObject(SERVICENAMESPACE,
                "getPanchayatByBlockId");
        request.addProperty("blockId", blockcode);
        // Log.e("PanchayatCode: ",Panchayat_Code);
        Object res1;
        try {

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //  envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            //    envelope.addMapping(NIC_SERVICENAMESPACE, VILLAGE.VILLAGE_CLASS.getSimpleName(), VILLAGE.VILLAGE_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(
                    SERVICEURL1);
            androidHttpTransport.call(SERVICENAMESPACE + "getPanchayatByBlockId",
                    envelope);
            res1 = envelope.bodyIn;
        } catch (Exception e) {
            Log.e("Exception1: ", e.getLocalizedMessage());
            Log.e("Exception2: ", e.getMessage());
            return null;
        }
        // int TotalProperty = res1.getPropertyCount();
        int TotalProperty = ((SoapObject) res1).getPropertyCount();
        // String[] divisionList = new String[TotalProperty];
        ArrayList<PanchayatData> villArrayList = new ArrayList<PanchayatData>();
        for (int ii = 0; ii < TotalProperty; ii++) {
            if (((SoapObject) res1).getProperty(ii) != null) {
                Object property = ((SoapObject) res1).getProperty(ii);
                if (property instanceof SoapObject) {
                    SoapObject final_object = (SoapObject) property;
                    PanchayatData vill = new PanchayatData(final_object);
                    villArrayList.add(vill);
                }
            } else
                return villArrayList;
        }
        return villArrayList;
    }

    public static ArrayList<VILLAGE> loadVILLAGEList(String Panchayat_Code) {
        SoapObject request = new SoapObject(SERVICENAMESPACE,
                "getVillageByPanchyatId");
        request.addProperty("panchayatId", Panchayat_Code);
        // Log.e("PanchayatCode: ",Panchayat_Code);
        Object res1;
        try {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //  envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE, VILLAGE.VILLAGE_CLASS.getSimpleName(), VILLAGE.VILLAGE_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(
                    SERVICEURL1);
            androidHttpTransport.call(SERVICENAMESPACE + "getVillageByPanchyatId",
                    envelope);
            res1 = envelope.bodyIn;
        } catch (Exception e) {
            Log.e("Exception1: ", e.getLocalizedMessage());
            Log.e("Exception2: ", e.getMessage());
            return null;
        }
        // int TotalProperty = res1.getPropertyCount();
        int TotalProperty = ((SoapObject) res1).getPropertyCount();
        // String[] divisionList = new String[TotalProperty];
        ArrayList<VILLAGE> villArrayList = new ArrayList<VILLAGE>();

        for (int ii = 0; ii < TotalProperty; ii++) {
            if (((SoapObject) res1).getProperty(ii) != null) {
                Object property = ((SoapObject) res1).getProperty(ii);
                if (property instanceof SoapObject) {
                    SoapObject final_object = (SoapObject) property;
                    VILLAGE vill = new VILLAGE(final_object);
                    villArrayList.add(vill);
                }
            } else
                return villArrayList;
        }
        return villArrayList;
    }

    public static ArrayList<TOLLA> loadTOLLAList(String Panchayat_Code) {
        SoapObject request = new SoapObject(SERVICENAMESPACE, "getTolaByVillageId");
        request.addProperty("villageId", Panchayat_Code);
        Log.e("panchayatCode: ", Panchayat_Code);
        Object res1;
        try {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            // envelope.addMapping(SERVICENAMESPACE,TOLLA.TOLLA_CLASS.getSimpleName(),TOLLA.TOLLA_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(
                    SERVICEURL1);
            androidHttpTransport.call(SERVICENAMESPACE + "getTolaByVillageId",
                    envelope);
            res1 = envelope.bodyIn;
        } catch (Exception e) {
            Log.e("Exception1: ", e.getLocalizedMessage());
            Log.e("Exception2: ", e.getMessage());
            return null;
        }
        int TotalProperty = ((SoapObject) res1).getPropertyCount();
        // String[] divisionList = new String[TotalProperty];
        ArrayList<TOLLA> tollaArrayList = new ArrayList<TOLLA>();
        for (int ii = 0; ii < TotalProperty; ii++) {
            if (((SoapObject) res1).getProperty(ii) != null) {
                Object property = ((SoapObject) res1).getProperty(ii);
                if (property instanceof SoapObject) {
                    SoapObject final_object = (SoapObject) property;
                    TOLLA tolla = new TOLLA(final_object);
                    tollaArrayList.add(tolla);
                }
            } else
                return tollaArrayList;
        }
        return tollaArrayList;
    }

    public static String updateaddress(consumer_address address, UserDetails user) {
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,
                    UPDATE_CON_ADDRESS);
            request.addProperty("userid", user.get_UserID());
            request.addProperty("imei", user.get_IMEI());
            request.addProperty("password", user.get_password());
            request.addProperty("subdiv", user.get_SubdivId());
            request.addProperty("conid", address.getConid());
            request.addProperty("distid", address.getDistCode());
            request.addProperty("blockid", address.getBlockCode());
            request.addProperty("panchid", address.getPanchayatcode());
            request.addProperty("villid", address.getVillcode());
            request.addProperty("tolaid", address.getTollcode());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(SERVICENAMESPACE,
                    UserDetails.USER_CLASS.getSimpleName(),
                    UserDetails.USER_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1, 1000 * 60 * 2);
            /*androidHttpTransport.call(SERVICENAMESPACE + AUTHENTICATE_METHOD,envelope);*/
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Object result = envelope.getResponse();
            if (result != null) {
                Log.d("", result.toString());
                return result.toString();
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", "" + e);
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
    }
    public static String streamtoString(InputStream inputstream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
        String result = "";
        String line = "";
        while ((line = reader.readLine()) != null)
            result += line;
        reader.close();
        return result;

    }

    public static ArrayList<Survey_Data> LoadSurveyData(String User_ID, String Pwd, String imei) {
        Object result;
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,SURVEY_DOWNLOAD_METHOD);
            request.addProperty("userid", User_ID);
            request.addProperty("imei", imei);
            request.addProperty("password", Pwd);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
        //    envelope.addMapping(SERVICENAMESPACE, Survey_Data.BILL_CLASS.getSimpleName(), Survey_Data.BILL_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            result = envelope.bodyIn;
        } catch (Exception e) {
            e.printStackTrace();
            Utiilties.writeIntoLog(Log.getStackTraceString(e));
            return null;
        }
        int TotalProperty = ((SoapObject) result).getPropertyCount();
        ArrayList<Survey_Data> MruList = new ArrayList<Survey_Data>();
        for (int ii = 0; ii < TotalProperty; ii++) {
            if (((SoapObject) result).getProperty(ii) != null) {
                Object property = ((SoapObject) result).getProperty(ii);
                if (property instanceof SoapObject) {
                    SoapObject final_object = (SoapObject) property;
                    Survey_Data userDetails = new Survey_Data(final_object);
                    MruList.add(userDetails);
                    //String s = property.toString();
                }
            } else
                return MruList;
        }
        return MruList;
    }
    public static String updateSurveydeatils(Survey_Data_Details data_details, UserDetails user) {
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,
                    CONSUMER_SURVAY);
            request.addProperty("userid", user.get_UserID());
            request.addProperty("imei", user.get_IMEI());
            request.addProperty("password", user.get_password());
            request.addProperty("ACT_NO", data_details.getACT_NO());
            request.addProperty("SUB_DIV_ID", data_details.getSUB_DIV_ID());
            request.addProperty("METER_NO",data_details.getMeter_No());
            request.addProperty("IS_CON_TD", data_details.getIs_consumer_TD());
            request.addProperty("TD_REASON", data_details.getTD_Spinner_string());
            request.addProperty("TD_NEW_CON_NO", data_details.getTD_New_Consumer_No());
            request.addProperty("TD_NEW_METER_NO", data_details.getTD_New_meter_No());
            request.addProperty("TD_ACTUAL_CON_NO", data_details.getTD_Duplicate_Consumer_no());
            request.addProperty("IS_LOW_CONSUMPTION", data_details.getIs_Low_Consumption());
            request.addProperty("LOW_CONSUMPTION_REASON", data_details.getIs_Low_Spiner_value());
            request.addProperty("LOW_MET_READ", data_details.getIs_Low_met_read());
            request.addProperty("IS_MD_FOUND", data_details.getIs_MD_Found());
            request.addProperty("MD_REASON", data_details.getMD_Spinner_value());
            request.addProperty("MD_MET_READ", data_details.getMD_met_read());
            request.addProperty("IS_MTR_MATCHED_WITH_BILL", data_details.getIs_mtr_nomatched_with_bill());
            request.addProperty("MD_METER_NO", data_details.getMd_meter_no());
            request.addProperty("IS_UNPAID_CONSUMER", data_details.getIs_unpaid_consumer());
            if(data_details.getIs_Low_met_img()!=null && data_details.getIs_Low_met_img().length>50){
                request.addProperty("METER_IMAGE", Base64.encodeToString(data_details.getIs_Low_met_img(), Base64.NO_WRAP));
            }else{
                request.addProperty("METER_IMAGE", "NA");
            }
            request.addProperty("UNPAID_MOB_NO", data_details.getUnpaid_mobile_no());
            request.addProperty("IS_UNPAID_TRACED", data_details.getIs_unpaid_traced());
            request.addProperty("IS_CONSUMER_PROPER_MRU", data_details.getIs_consumer_proper_mru());
            request.addProperty("IS_CONSUMER_MISSING", data_details.getIs_consumer_missing());
            request.addProperty("MISSING_CA_NO", data_details.getProper_mru_ca_no());
            request.addProperty("MISSING_MET_NO", data_details.getProper_mru_met_no());
            request.addProperty("MAX_DEEMAND", data_details.getMax_deemand());
            request.addProperty("POWER_FACTOR", data_details.getPower_factor());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            Log.e("data",request.toString());
            envelope.addMapping(SERVICENAMESPACE,
                    UserDetails.USER_CLASS.getSimpleName(),
                    UserDetails.USER_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1, 1000 * 60 * 2);
            /*androidHttpTransport.call(SERVICENAMESPACE + AUTHENTICATE_METHOD,envelope);*/
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Object result = envelope.getResponse();
            if (result != null) {
                return result.toString();
            } else
                return "Error:"+result;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error"+ e;
        }
    }

    public static String updatePaymentdeatils(Payment_Data_Details data_details, UserDetails user) {
        try {
            SoapObject request = new SoapObject(SERVICENAMESPACE,
                    UPDATE_PAY_REASON);
            request.addProperty("con_id", data_details.getACT_NO());
            request.addProperty("sub_div_id", data_details.getSUB_DIV_ID());
            request.addProperty("reason", data_details.getData());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            //envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            Log.e("data",request.toString());
            envelope.addMapping(SERVICENAMESPACE,
                    UserDetails.USER_CLASS.getSimpleName(),
                    UserDetails.USER_CLASS);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(SERVICEURL1, 1000 * 60 * 2);
            /*androidHttpTransport.call(SERVICENAMESPACE + AUTHENTICATE_METHOD,envelope);*/
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Object result = envelope.getResponse();
            if (result != null) {
                return result.toString();
            } else
                return "Error:"+result;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error"+ e;
        }
    }
}
