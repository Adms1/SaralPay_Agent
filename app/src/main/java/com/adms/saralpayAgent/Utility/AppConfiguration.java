package com.adms.saralpayAgent.Utility;

import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * Created by Harsh on 04-Aug-16.
 */
public class AppConfiguration {

    public enum Domain {
        LIVE, LOCAL
    }

    static Domain domain = Domain.LIVE;//only Change this for changing environment

    public static String getUrl(String methodName) {
        String url = "";
        switch (domain) {
            case LIVE:
                url = DOMAIN_LIVE + methodName;
                break;
            case LOCAL:
                url = DOMAIN_LOCAL + methodName;
                break;
            default:
                break;
        }
        return url;
    }


    //Local
    public static String DOMAIN_LOCAL = "http://developer.saralpayonline.com/WebService.asmx/";
    public static String DOMAIN_LIVE = "http://www.saralpayonline.com/WebService.asmx/";


    public static String CreateCustomer = "CreateCustomer";
    public static String GetStateDetail = "GetStateDetail";
    public static String GetCityDetailStateWise = "GetCityDetailStateWise";
    public static String CustomerLogin = "CustomerLogin";
    public static String GeneratePaymentRequest = "GeneratePaymentRequest";
    public static String GetPaymentResponse = "GetPaymentResponse";
    public static String UpdatePaymentRequest = "UpdatePaymentRequest";
    public static String GetBusinessType = "GetBusinessType";
    public static String GetMyPaymentDetail = "GetMyPaymentDetail";
    public static String CustomerDuplicateMobile = "CustomerDuplicateMobile";
    public static String GenerateOTP = "GenerateOTP";
    public static String CheckOTPStatus = "CheckOTPStatus";
    public static String GetCustomerAllDetail = "GetCustomerAllDetail";
    public static String UpdateCustomer = "UpdateCustomer";
    public static String UpgradeCustomerStatus = "UpgradeCustomerStatus";
    public static String Customer_RegistrationDays = "Customer_RegistrationDays";
    public static String Customer_Showpopup = "Customer_Showpopup";
    public static String GenerateOTPByRegisteredNumber = "GenerateOTPByRegisteredNumber";
    public static String UpdateCustomerPin = "UpdateCustomerPin";
    public static String SendSMSToCustomer = "SendSMSToCustomer";
    public static String Check_AppVersion = "Check_AppVersion";
    public static String Get_KYC_CustomerSuccess_Payment = "Get_KYC_CustomerSuccess_Payment";
    public static String GetPlanList = "GetPlanList";
    public static String CreateCustomer_Plan = "CreateCustomer_Plan";

    public static HashMap<String, String> CustomerDetail = new HashMap<>();
    public static Bitmap bmGalaryImage;
    public static String api_key = "";
    public static String secret_key = "";
    public static String serverVersionCode = "";
    public static String planid = "";
    public static String planstatus = "";
    public static String startdate = "";
    public static String enddate = "";

    public static String GetCustomerDataByQRCodeorMobileno = "GetCustomerDataByQRCodeorMobileno";

    //    ForAgentApp
    public static String ExecutiveLogin = "ExecutiveLogin";
    public static String QRCodeDetail = "QRCodeDetail";
    public static String CreateCustomerByAgent = "CreateCustomerByAgent";
    public static String GetCustomerIDByNumber = "GetCustomerIDByNumber";
    public static String AssignQRCodeToCustomer ="AssignQRCodeToCustomer";

    public static HashMap<String, String> ExecutiveDetail = new HashMap<>();
    public static HashMap<String, String> QRDetail = new HashMap<>();
    public static HashMap<String, String> CustomerDetailByMobile = new HashMap<String, String>();
    public static HashMap<String,String> QRAssign=new HashMap<String, String>();
}
