package com.adms.saralpayAgent.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Harsh on 04-Aug-16.
 */
public class ParseJSON {

    //    ForAgentApp
    public static HashMap<String, String> parseAgentLoginJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("ExecutiveData");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put("ExecutiveID", jsonChildNode.getString("ExecutiveID"));
                    result.put("ManagerID", jsonChildNode.getString("ManagerID"));
                    result.put("Name", jsonChildNode.getString("Name"));
                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseQRCodeDetailJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("QRCodeDetail");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put("QRCodeID", jsonChildNode.getString("QRCodeID"));
                    result.put("AssignedCustomerID", jsonChildNode.getString("AssignedCustomerID"));
                    result.put("AssignedCustomerName",jsonChildNode.getString("AssignedCustomerName"));
                    result.put("AssignedStatus",jsonChildNode.getString("AssignedStatus"));
                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseCreateCustomerByAgentJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            result.put("Success", data_load_basket);
            if (data_load_basket.toString().equals("True")) {
                String customerId = reader.getString("CustomerID");
                result.put("CustomerID", customerId);
                result.put("OTP", reader.getString("OTP"));
                result.put("OTPGenerateDate", reader.getString("OTPGenerateDate"));
                result.put("OTPExpiryDate", reader.getString("OTPExpiryDate"));
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseCreateCustomerByIdNumberJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("QRCodeDetail");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put("CustomerID", jsonChildNode.getString("CustomerID"));
                    result.put("CustomerName", jsonChildNode.getString("CustomerName"));
                    result.put("CompanyName", jsonChildNode.getString("CompanyName"));
                    result.put("TypeofBussiness", jsonChildNode.getString("TypeofBussiness"));
                }
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseAssignQRCodeToCustomerJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            result.put("Success", data_load_basket);
            if (data_load_basket.toString().equals("True")) {
                String Message = reader.getString("Message");
                result.put("Message", Message);
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseLoginJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("CustomerDetail");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put("CustomerID", jsonChildNode.getString("CustomerID"));
                    result.put("CustomerName", jsonChildNode.getString("CustomerName"));
                    result.put("CustomerEmail", jsonChildNode.getString("CustomerEmail"));
                    result.put("CustomerMobile", jsonChildNode.getString("CustomerMobile"));
                    result.put("CompanyName", jsonChildNode.getString("CompanyName"));
                    result.put("TypeofBussiness", jsonChildNode.getString("TypeofBussiness"));
//                    AppConfiguration.api_key = jsonChildNode.getString("C_API_Keys");
//                    AppConfiguration.secret_key = jsonChildNode.getString("C_Secret_Keys");
//                    AppConfiguration.planstatus = jsonChildNode.getString("Fk_Plan_ID");
                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseGetCustomerDataByQRCodeorMobilenoJson(String responseString) {
        HashMap<String, String> resultQRData = new HashMap<>();
        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("CustomerDetail");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    resultQRData.put("CustomerID", jsonChildNode.getString("CustomerID"));
                    resultQRData.put("CustomerName", jsonChildNode.getString("CustomerName"));
                    resultQRData.put("CustomerEmail", jsonChildNode.getString("CustomerEmail"));
                    resultQRData.put("CustomerMobile", jsonChildNode.getString("CustomerMobile"));
                    resultQRData.put("CompanyName", jsonChildNode.getString("CompanyName"));
                    resultQRData.put("TypeofBussiness", jsonChildNode.getString("TypeofBussiness"));
                    AppConfiguration.api_key = jsonChildNode.getString("C_API_Keys");
                    AppConfiguration.secret_key = jsonChildNode.getString("C_Secret_Keys");
                    AppConfiguration.planstatus = jsonChildNode.getString("Fk_Plan_ID");


                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultQRData;
    }

    public static ArrayList<String> parseStateJson(String responseString) {
        ArrayList<String> result = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("StateData");
                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.add(jsonChildNode.getString("StateName"));
                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<String> parseCityJson(String responseString) {
        ArrayList<String> result = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("CityData");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.add(jsonChildNode.getString("CityName"));
                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseVerifyRegistration(String responseString) {
        HashMap<String, String> hashMap = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            hashMap.put("Success", data_load_basket);
            if (data_load_basket.toString().equals("True")) {
                String customerID = reader.getString("CustomerID");
                hashMap.put("CustomerID", customerID);
                hashMap.put("OTP", reader.getString("OTP"));
                hashMap.put("OTPGenerateDate", reader.getString("OTPGenerateDate"));
                hashMap.put("OTPExpiryDate", reader.getString("OTPExpiryDate"));

            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hashMap;
    }

    public static HashMap<String, String> GeneratePaymentJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                result.put("RediredURL", reader.getString("RediredURL"));
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> GeneratePaymentNewJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                result.put("TransactionID", reader.getString("TransactionID"));
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /*{
        "Success": "True",
            "FinalArray": [
        {
            "PaymentStatus": "completed",
                "PaymentID": "MOJO6b17005J35065305",
                "TransactionID": "saral_27_shgk5bl3fps",
                "OrderID": "7e3e465037364697b77a28f5093242ee"
        }
        ]
    }*/

    public static HashMap<String, String> parsePaymentResponseJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("FinalArray");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put("PaymentStatus", jsonChildNode.getString("PaymentStatus"));
                    result.put("PaymentID", jsonChildNode.getString("PaymentID"));
                    result.put("TransactionID", jsonChildNode.getString("TransactionID"));
                    result.put("OrderID", jsonChildNode.getString("OrderID"));
                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parsePaymentResponseNewJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("FinalArray");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put("PaymentStatus", jsonChildNode.getString("PaymentStatus"));
                    result.put("PaymentID", jsonChildNode.getString("PaymentID"));
                    result.put("TransactionID", jsonChildNode.getString("TransactionID"));
                    result.put("OrderID", jsonChildNode.getString("OrderID"));
                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static boolean UpdatePaymentStatusJson(String responseString) {
        boolean result = false;

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                result = true;
                /*JSONArray jsonMainNode = reader.optJSONArray("CustomerDetail");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put("CustomerID", jsonChildNode.getString("CustomerID"));
                    result.put("CustomerName", jsonChildNode.getString("CustomerName"));
                    result.put("CustomerEmail", jsonChildNode.getString("CustomerEmail"));
                    result.put("CustomerMobile", jsonChildNode.getString("CustomerMobile"));
                }*/
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseGetBusinessTypeJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("FinalArray");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put(jsonChildNode.getString("BusinessTypeID"), jsonChildNode.getString("BusinessType"));
                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<HashMap<String, String>> parseGetMyPaymentDetailJson(String responseString) {

        ArrayList<HashMap<String, String>> hashMaps = new ArrayList<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.getJSONArray("FinalArray");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    HashMap<String, String> result = new HashMap<>();
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put("PaymentDate", jsonChildNode.getString("PaymentDate"));
                    result.put("PaymentTime", jsonChildNode.getString("PaymentTime"));
                    result.put("PaymentStatus", jsonChildNode.getString("PaymentStatus"));
                    result.put("Trans_Amount", jsonChildNode.getString("Trans_Amount"));
                    result.put("Trans_Charge", jsonChildNode.getString("Trans_Charge"));
                    result.put("Trans_STaxAmount", jsonChildNode.getString("Trans_STaxAmount"));
                    result.put("Trans_KrishiTaxAmount", jsonChildNode.getString("Trans_KrishiTaxAmount"));
                    result.put("Trans_SwatchBharatAmount", jsonChildNode.getString("Trans_SwatchBharatAmount"));
                    result.put("Trans_FinalAmount", jsonChildNode.getString("Trans_FinalAmount"));
                    result.put("Trans_Description", jsonChildNode.getString("Trans_Description"));
                    result.put("Trans_DepositDate1", jsonChildNode.getString("Trans_DepositDate1"));
                    result.put("Trans_PaymentID", jsonChildNode.getString("Trans_PaymentID"));


                    hashMaps.add(result);
                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hashMaps;
    }

    public static HashMap<String, String> parseMobileValidateJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("Phone1");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put("Phone1", jsonChildNode.getString("Phone1"));
                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseGenerateOTPJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {

                result.put("OTP", reader.getString("OTP"));
                result.put("OTPGenerateDate", reader.getString("OTPGenerateDate"));
                result.put("OTPExpiryDate", reader.getString("OTPExpiryDate"));

            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseCheckOTPStatusJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                result.put("OTP", reader.getString("OTP"));
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseGetCustomerAllDetailJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("ExecutiveData");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put("Name", jsonChildNode.getString("Name"));
                    result.put("C_FirstName", jsonChildNode.getString("C_FirstName"));
                    result.put("C_LastName", jsonChildNode.getString("C_LastName"));
                    result.put("C_Address1", jsonChildNode.getString("C_Address1"));
                    result.put("C_Address2", jsonChildNode.getString("C_Address2"));
                    result.put("Fk_CityID", jsonChildNode.getString("Fk_CityID"));
                    result.put("FK_StateID", jsonChildNode.getString("FK_StateID"));
                    result.put("C_Pincode", jsonChildNode.getString("C_Pincode"));
                    result.put("C_EmailAddress", jsonChildNode.getString("C_EmailAddress"));
                    result.put("C_Phone1", jsonChildNode.getString("C_Phone1"));
                    result.put("C_Phone2", jsonChildNode.getString("C_Phone2"));
                    result.put("C_ID1cardtype", jsonChildNode.getString("C_ID1cardtype"));
                    result.put("C_ID1number", jsonChildNode.getString("C_ID1number"));
                    result.put("C_AadharCardFront", jsonChildNode.getString("C_AadharCardFront"));
                    result.put("C_AadharCardBack", jsonChildNode.getString("C_AadharCardBack"));
                    result.put("C_ID2cardtype", jsonChildNode.getString("C_ID2cardtype"));
                    result.put("C_ID2cardnumber", jsonChildNode.getString("C_ID2cardnumber"));
                    result.put("C_ChequeBookFront", jsonChildNode.getString("C_ChequeBookFront"));
                    result.put("C_CompanyName", jsonChildNode.getString("C_CompanyName"));
                    result.put("C_BussinessType", jsonChildNode.getString("C_BussinessType"));
                    result.put("C_Bankname", jsonChildNode.getString("C_Bankname"));
                    result.put("C_BranchName", jsonChildNode.getString("C_BranchName"));
                    result.put("C_BankaccountNo", jsonChildNode.getString("C_BankaccountNo"));
                    result.put("C_BankIFCcode", jsonChildNode.getString("C_BankIFCcode"));
                    result.put("C_CreateDate", jsonChildNode.getString("C_CreateDate"));
                    result.put("C_ApproxDate", jsonChildNode.getString("C_ApproxDate"));
                    result.put("C_Status", jsonChildNode.getString("C_Status"));
                    result.put("C_TransactionCode", jsonChildNode.getString("C_TransactionCode"));
                    result.put("C_Passcode", jsonChildNode.getString("C_Passcode"));
                    result.put("C_Percentage", jsonChildNode.getString("C_Percentage"));
                    result.put("C_ABBR", jsonChildNode.getString("C_ABBR"));
                    result.put("FK_ManagerID", jsonChildNode.getString("FK_ManagerID"));
                    result.put("FK_ExecutiveID", jsonChildNode.getString("FK_ExecutiveID"));

                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseUpgradeCustomerStatusJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                result.put("status", "true");
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseCustomer_ShowpopupJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("CustomerDetail");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put("PopupStatus", jsonChildNode.getString("PopupStatus"));
                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseCustomer_RegistrationDaysJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("CustomerDetail");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put("NoofDays", jsonChildNode.getString("NoofDays"));
                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseGenerateOTPByRegisteredNumberJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                result.put("CustomerID", reader.getString("CustomerID"));
                result.put("OTP", reader.getString("OTP"));
                result.put("OTPGenerateDate", reader.getString("OTPGenerateDate"));
                result.put("OTPExpiryDate", reader.getString("OTPExpiryDate"));
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseUpdateCustomerPinJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                result.put("Success", data_load_basket);
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseSendSMSToCustomerJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                result.put("Success", data_load_basket);
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseCheck_AppVersionJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("CustomerDetail");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put("VersionNo", jsonChildNode.getString("VersionNo"));

                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<String, String> parseGet_KYC_CustomerSuccess_PaymentJson(String responseString) {
        HashMap<String, String> result = new HashMap<>();

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("CustomerDetail");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result.put("FinalAmount", jsonChildNode.getString("FinalAmount"));
                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<HashMap<String, String>> parseGetPlanListJson(String responseString) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        HashMap<String, String> result = null;

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                JSONArray jsonMainNode = reader.optJSONArray("FinalArray");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    result = new HashMap<>();
                    result.put("Pk_Plan_ID", jsonChildNode.getString("Pk_Plan_ID"));
                    result.put("PlanName", jsonChildNode.getString("PlanName"));
                    result.put("PlanAmount", jsonChildNode.getString("PlanAmount"));
                    list.add(result);
                }
            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean parseCreateCustomerPlanJson(String responseString) {
        boolean result = false;

        try {
            JSONObject reader = new JSONObject(responseString);
            String data_load_basket = reader.getString("Success");
            if (data_load_basket.toString().equals("True")) {
                result = true;

            } else {
                //invalid login
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
