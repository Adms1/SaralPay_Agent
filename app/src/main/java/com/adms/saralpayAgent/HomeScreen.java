package com.adms.saralpayAgent;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adms.saralpayAgent.AsyncTask.Check_AppVersionAsyncTask;
import com.adms.saralpayAgent.AsyncTask.GeneratePaymentRequestAsyncTask;
import com.adms.saralpayAgent.AsyncTask.GetPaymentResponseAsyncTask;
import com.adms.saralpayAgent.AsyncTask.UpdatePaymentRequestAsyncTask;
import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.Utility.AppConfiguration;
import com.adms.saralpayAgent.Utility.Utility;
import com.instamojo.android.Instamojo;
import com.instamojo.android.activities.PaymentDetailsActivity;
import com.instamojo.android.callbacks.OrderRequestCallBack;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.models.Errors;
import com.instamojo.android.models.Order;
import com.instamojo.android.network.Request;
import com.scdroid.ccid.IReader;
import com.scdroid.ccid.USBReader;
import com.scdroid.smartcard.Card;
import com.scdroid.smartcard.CreditCard;
import com.scdroid.smartcard.SCException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeScreen extends AppCompatActivity {

    private static final HashMap<String, String> env_options = new HashMap<>();

    static {
//        env_options.put("Test", "https://test.instamojo.com");
        env_options.put("Production", "https://api.instamojo.com");

    }

    private Context mContext = this;
    private Button btnCharge, btnCancel;
    private EditText edtAmount, edtNarration;
    private RelativeLayout rlTopBar;
    private String currentEnv = null;
    private String accessToken = null;
    private String transactionID = null;
    private ImageButton btnLogOut, btnMenu;
    private PopupWindow popupWindow;
    private HashMap<String, String> hashMapResult = new HashMap<String, String>();
    private TextView txtUserName;
    private ProgressDialog dialog;
    private String staticMobile = "7575809733";
    private final int MESSAGE_OK = 1;
    private final int MESSAGE_ERROR = 2;
    private final int MESSAGE_STATUS = 3;
    private String cardDetail = "";
    private boolean isCardPresent = false;
    private boolean isVersionCodeUpdated = false;

    Bundle mBundle = new Bundle();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        initViews();
        setListners();
        fn_permission_PhoneState();
//        getVersionCodeUpdated();

    }

    public void showProgressDialog(){
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage("please wait...");
        dialog.setCancelable(false);
    }

    public void initViews() {
        showProgressDialog();

        final ArrayList<String> envs = new ArrayList<>(env_options.keySet());
        currentEnv = envs.get(0);
        Instamojo.setBaseUrl(env_options.get(currentEnv));

        rlTopBar = (RelativeLayout) findViewById(R.id.rlTopBar);
        edtAmount = (EditText) findViewById(R.id.edtAmount);
        edtAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        edtNarration = (EditText) findViewById(R.id.edtNarration);
        btnCharge = (Button) findViewById(R.id.btnCharge);
        btnLogOut = (ImageButton) findViewById(R.id.btnLogOut);
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        txtUserName = (TextView) findViewById(R.id.txtUserName);

        if(AppConfiguration.CustomerDetailByMobile.size() > 0) {

            txtUserName.setText("Payment Portal for\n" + AppConfiguration.CustomerDetailByMobile.get("CompanyName"));
            edtNarration.setText("This charge is for " + AppConfiguration.CustomerDetailByMobile.get("TypeofBussiness") + " at " + AppConfiguration.CustomerDetailByMobile.get("CompanyName") +
                    " on " + Utility.getTodaysDate() + " @ " + Utility.getTime());
        }else {
            Intent ilogin = new Intent(mContext, LoginScreen.class);
            ilogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(ilogin);
            finish();
        }
    }

    public void setListners() {
        /*edtAmount.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                *//*if(s.toString().matches(""))
                {
                    edtAmount.setText("0.00");
                    Selection.setSelection(edtAmount.getText(), 0, 4);
                }*//*

                String test = s.toString();
                test.replace(".","");
                float  f = Float.parseFloat(test);
                f = f/100;
                test = String.valueOf(f);//f.tostring();
                edtAmount.setText(test);
            }
        });*/


        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow popupwindow_obj = popupDisplay();
                popupwindow_obj.showAsDropDown(rlTopBar, 0, 0);
            }
        });

        btnCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(Utility.isNetworkConnected(HomeScreen.this)){
                        chargeBtnLogic();
//                        if(isVersionCodeUpdated){
//                            chargeBtnLogic();
//
//                        }else {
//                            Utility.openVersionDialogCharge(HomeScreen.this);
//
//                        }

                    }else {
                        Utility.ping(mContext, "Network not available");
                    }
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent iLogin = new Intent(mContext, LoginScreen.class);
                startActivity(iLogin);
                finish();*/
                Utility.openLogOutDialog(HomeScreen.this);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtAmount.setText("");
//                edtNarration.setText("");

            }
        });
    }

    public void chargeBtnLogic(){
        AppConfiguration.planid="0";
        if (AppConfiguration.api_key.length() > 5 && AppConfiguration.secret_key.length() > 5) {
            String[] amount = null;
            if(edtAmount.getText().toString().contains("."))
                amount = edtAmount.getText().toString().split("\\.");//to check decimal places

            if (edtAmount.getText().toString().equalsIgnoreCase("")) {
                Utility.ping(mContext, mContext.getResources().getString(R.string.strEnterAmt));
            }else if(amount != null && amount[1].length() > 2){
                Utility.ping(mContext, "Please provide upto 2 decimal places only.");
            } else if (Double.parseDouble(edtAmount.getText().toString()) < 2.00) {
                Utility.ping(mContext, "Amount can't be less than Rs. 2.00");
            }
                    /*else if (Double.parseDouble(edtAmount.getText().toString()) < 2.00) {
                        Utility.ping(mContext, "Amount can't be more than Rs. 100000.00");
                    }*/
            else {
                //SHRENIK IS HERE.....
                    /*if(1==2) {
                        //Insta mojo code.
                        fetchTokenAndTransactionID();
                    }*/
                try {
//                            verifyCard();
                    String cardResult = verifyCardFromUSBReader();
                    if (cardResult.equalsIgnoreCase("NoReader")) {
                        isCardPresent = false;
                        openDialog("No Card Reader Found", "If you have a card reader then please RETRY by inserting a card reader with a valid card or click OK to proceed to Manual Entry.");

                    } else if (cardResult.equalsIgnoreCase("NoCard")) {
                        isCardPresent = false;
                        openDialog("Card Not Found", "Either the card is not inserted or the card you inserted is not supported so please RETRY by inserting a valid card with a chip or click OK to proceed for the manual entry.");

                    } else {
                        isCardPresent = true;
                        Log.v("Carddetail: ", cardResult);
                        generateTrackNPayRequest();
//                            fetchTokenAndTransactionID();
                        //Custom UI

                    }
                } catch (Exception e) {
                    //SendMessage(MESSAGE_ERROR, e.getMessage());
                    SendToast(e.getMessage());
                }
            }
            Instamojo.setLogLevel(Log.DEBUG);
        }else {
            Utility.openInvalidApiKeyDialog(HomeScreen.this);
        }
    }

    public void generateTrackNPayRequest(){
        if (!dialog.isShowing()) {
            dialog.show();
        }

        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("CustomerID", AppConfiguration.CustomerDetail.get("CustomerID"));
        hashMap.put("Name", AppConfiguration.CustomerDetail.get("CustomerName"));
        hashMap.put("Email", "saralpayonline@gmail.com");//AppConfiguration.CustomerDetail.get("CustomerEmail")
        hashMap.put("Mobile", AppConfiguration.CustomerDetail.get("CustomerMobile"));
        hashMap.put("Amount", edtAmount.getText().toString().trim());
        hashMap.put("Description", edtNarration.getText().toString());

        if (isBoolean_permission_phoneState) {
            hashMap.put("IMEINumber", Utility.getIMEI(mContext));
        } else {
            hashMap.put("IMEINumber", "");
        }

        if (isBoolean_permission_location) {
            double[] loc = Utility.getLocation(mContext);
            hashMap.put("Latitude", String.valueOf(loc[0]));
            hashMap.put("Longitude", String.valueOf(loc[1]));
        } else {
            hashMap.put("Latitude", "");
            hashMap.put("Longitude", "");
        }
        if(isCardPresent){
            hashMap.put("Trans_Type", "1"); /* card*/
        }else {
            hashMap.put("Trans_Type", "2"); /* manually */
        }


        try {
            GeneratePaymentRequestAsyncTask generatePaymentRequestAsyncTask = new GeneratePaymentRequestAsyncTask(hashMap, new OnCompletionListner() {
                @Override
                public void OnResponseSuccess(Object output) {
                    dialog.dismiss();
                    hashMapResult = (HashMap<String, String>) output;
                    if (hashMapResult.size() > 0) {
                        transactionID = hashMapResult.get("TransactionID");
                    }
                    if(transactionID != null) {
                        Log.v("CardPresent: ", String.valueOf(isCardPresent));
                        Intent intent = new Intent(HomeScreen.this, TraknpayRequestActivity.class);
                        if (isCardPresent) {
                            intent.putExtra("CardDetails", cardDetail);
                        }
                        intent.putExtra("order_id", transactionID);
                        intent.putExtra("amount", edtAmount.getText().toString());
                        intent.putExtra("mode", "LIVE");
                        intent.putExtra("description", edtNarration.getText().toString());
                        startActivity(intent);
                    }else {
                        Utility.ping(mContext, "Transaction ID not found. Please try again");
                    }

                }

                @Override
                public void OnResponseFail(Object output) {
                    dialog.dismiss();
                    Utility.ping(mContext, "Transaction ID not found. Please try again");
                }
            });
            generatePaymentRequestAsyncTask.execute();

        } catch (Exception e) {
            dialog.dismiss();
            e.printStackTrace();
        }
    }

    public void openDialog(String Title, String Message){
        new AlertDialog.Builder(mContext)
                .setTitle(Title)
                .setMessage(Message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with manual Entry
//                        fetchTokenAndTransactionID();
                        generateTrackNPayRequest();

                    }
                })
                .setNegativeButton("RETRY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing

                    }
                })
                .setIcon(mContext.getResources().getDrawable(R.mipmap.saralpay_icon))
                .show();
    }

    /*private void verifyCard(){

//        chooseReader();
//        SendMessage(1, "usb reader read start......");

    }*/

    /*private void chooseReader()
    {
        final CharSequence[] options = {"Bluetooth Smartcard Reader", "USB Smartcard Reader", "Enter test mode"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your reader");

        builder.setItems(options, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0) {//Bluetooth Smartcard Reader
                    // verifyCardFromBlutoothReader();
                } else if (which == 1) {//USB Smartcard Reader
                    SendMessage(1, "usb reader read start......");
                    verifyCardFromUSBReader();

                } else if (which == 2) {
                    //  showTestMode();
                    // TestBlutoothReader(true);
                }// else if (which == 3) {
                //TestBlutoothReader(false);
                //}



            }
        });

        AlertDialog dlg = builder.create();
        dlg.show();
    }*/

    private String verifyCardFromUSBReader() {


//        SendMessage(0, "Trying to read the card....");
        List<USBReader> ReaderList = null;
        ReaderList = USBReader.getReaders(this);
//        SendMessage(0, "USB Read");
        if (ReaderList.size() == 0) {
//            SendMessage(0, "Please connect USB reader to your phone");
            return "NoReader";
        }

//        SendMessage(0, "Total of " + String.valueOf(ReaderList.size()) + " readers found..");
        final USBReader reader = ReaderList.get(0);
//        SendMessage(0, "Reader selected....");
        try {
            cardDetail = readCardID(reader);
//            SendMessage(MESSAGE_OK, cardDetail);
            /*SendMessage(1,mBundle.getBinder("name").toString());
            SendMessage(1,mBundle.getBinder("number").toString());
            SendMessage(1,mBundle.getBinder("exp").toString());*/
            return cardDetail;

        } catch (SCException scexc) {
//            SendMessage(MESSAGE_ERROR, "Card not found. Please insert a card.");//scexc.getMessage());
            return "NoCard";
        }


        /*new Thread(){
            public void run(){
                try{
                 //   SendMessage(0,"Thread  started.");
                    txtVerifiedCardid = readCardID(reader);
                    //read the card id successfully
                    SendMessage(MESSAGE_OK, txtVerifiedCardid);

                }catch(Exception e){
                    //read the card id successfully fail
                  //  SendMessage(MESSAGE_ERROR, e.getMessage());
                }
            }
        }.start();*/


    }

    private String readCardID(IReader reader) throws SCException {
        String sCardID = "";

		/*
        SendMessage(MESSAGE_STATUS, "Search Bluetooth reader ...");

		List<BTReader> ReaderList = BTReader.getReaders(mBluetoothAdapter);
		if (ReaderList.size() == 0)
			throw new SCException("Bluetooth reader not found!");

		BTReader reader = ReaderList.get(0);
		*/

        //reader.logLevel(USBReader.LOG_APDU); //unmark it if need debug

        try {
            //SendMessage(0,"In read Card ID");

            reader.Open();

//            SendMessage(MESSAGE_STATUS, "Coonnecting Card ...NUCE");

			/*
			STCCard card = null;
			card = (STCCard)reader.ConnectCard("com.telephoenic.stcsimregistration.STCCard", Card.PROTOCOL_ANY);

			if (card == null)
				throw new SCException("STC Card not present!");

			SendMessage(MESSAGE_STATUS, "Reading Card ...");
			sCardID = card.ReadID();
			*/


            CreditCard card = null;
            card = (CreditCard) reader.ConnectCard("com.scdroid.smartcard.CreditCard", Card.PROTOCOL_ANY);

            if (card == null)
                throw new SCException("EMV Card not present!");

            //SendMessage(MESSAGE_STATUS, "Reading Card ...");
            card.ReadCard();
            sCardID = card.CardNumber + "-" + card.CardHolderName + " - " + card.ValidToDate;
            mBundle.putString("name", card.CardHolderName);
            mBundle.putString("number", card.CardNumber);
            mBundle.putString("exp", card.ValidToDate);

            card.Disconnect();

            return sCardID;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SCException(e.getMessage());
        } finally {
            reader.Close();
        }
    }

    protected final void SendMessage(int what, Object obj) {
        SendToast(obj.toString());

        /*Message m = new Message();
        m.what = what;

        if (obj != null)
            m.obj = obj;

        if (mHandler != null)
            mHandler.sendMessage(m);*/


    }

    protected final void SendToast(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            verifyCardResult(msg);
        }
    };

    void verifyCardResult(Message msg) {
    }


    /**
     * Fetch Access token and unique transactionID from developers server
     */
    private void fetchTokenAndTransactionID() {
        if (!dialog.isShowing()) {
            dialog.show();
        }

        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("CustomerID", AppConfiguration.CustomerDetail.get("CustomerID"));
        hashMap.put("Name", AppConfiguration.CustomerDetail.get("CustomerName"));
        hashMap.put("Email", "saralpayonline@gmail.com");//AppConfiguration.CustomerDetail.get("CustomerEmail")
        hashMap.put("Mobile", AppConfiguration.CustomerDetail.get("CustomerMobile"));
        hashMap.put("Amount", edtAmount.getText().toString().trim());
        hashMap.put("Description", edtNarration.getText().toString());

        if (isBoolean_permission_phoneState) {
            hashMap.put("IMEINumber", Utility.getIMEI(mContext));
        } else {
            hashMap.put("IMEINumber", "");
        }

        if (isBoolean_permission_location) {
            double[] loc = Utility.getLocation(mContext);
            hashMap.put("Latitude", String.valueOf(loc[0]));
            hashMap.put("Longitude", String.valueOf(loc[1]));
        } else {
            hashMap.put("Latitude", "");
            hashMap.put("Longitude", "");
        }


        try {
            GeneratePaymentRequestAsyncTask generatePaymentRequestAsyncTask = new GeneratePaymentRequestAsyncTask(hashMap, new OnCompletionListner() {
                @Override
                public void OnResponseSuccess(Object output) {
                    hashMapResult = (HashMap<String, String>) output;
                    if (hashMapResult.size() > 0) {
                        transactionID = hashMapResult.get("TransactionID");
                    }
                }

                @Override
                public void OnResponseFail(Object output) {

                }
            });
            generatePaymentRequestAsyncTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("env", currentEnv.toLowerCase())
                .add("grant_type", "client_credentials")
                .add("client_id", "G0xlhYH2qDJHPHnRCvGRDcmEncDTxQAnAkXGwTmf")//W41kasYJ4Ln9QKXWsW8VcVcU8EYyCxQICahQdGpU
                .add("client_secret", "vh89bI5dCu4jBA9bfHgfftxUDBz344WsRz9CCeSpmWxHgNMve5l8WqOXwUGuAaWTErL75SHkYxuCpoqZaDRZnmAfZWERAGUVQxtuDs9xE4tbWGhZz5psx2gIqnrd32p7")//FmeDfFqsTV0IgWOh9TYe65Ey7j5K7VzZD3VrE3LWJG2mcp74smfHgUC2L7fqKFnSvFVGbmlgiWI3jNds6fcfqr6kbQqjDNxoEMCXVMbfxGe7ync2U5xld96FfvdqjCVC
                .build();


        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://api.instamojo.com/oauth2/token/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        Utility.ping(mContext, "Failed to fetch the Order Tokens");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString;
                String errorMessage = null;
//                String transactionID = null;
                responseString = response.body().string();
                response.body().close();
                try {
                    JSONObject responseObject = new JSONObject(responseString);
                    if (responseObject.has("error")) {
                        errorMessage = responseObject.getString("error");
                    } else {
                        accessToken = responseObject.getString("access_token");
                        Log.v("Access Token: ", accessToken);
//                        transactionID = responseObject.getString("transaction_id");
                    }
                } catch (JSONException e) {
                    errorMessage = "Failed to fetch Order tokens";
                }

                final String finalErrorMessage = errorMessage;
                final String finalTransactionID = transactionID;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        if (finalErrorMessage != null) {
                            Utility.ping(mContext, finalErrorMessage);
                            return;
                        }

                        createOrder(accessToken, finalTransactionID);
                    }
                });

            }
        });
    }

    private void createOrder(String accessToken, String transactionID) {
        try {

            String name = AppConfiguration.CustomerDetail.get("CustomerName");
            final String email = "saralpayonline@gmail.com";//AppConfiguration.CustomerDetail.get("CustomerEmail");
            String phone = staticMobile;//AppConfiguration.CustomerDetail.get("CustomerMobile");
//        String phone = "9016708106";
            String amount = edtAmount.getText().toString();

            String description;
            if (edtNarration.getText().toString().trim().equalsIgnoreCase("")) {
                description = "none";
            } else {
                description = edtNarration.getText().toString();
            }
            //Create the Order
            Order order = new Order(accessToken, transactionID, name, email, phone, amount, description);

            //set webhook
//        order.setWebhook("http://your.server.com/webhook/");
            order.setWebhook("http://www.saralpayonline.com/test.asp");

            //Validate the Order
            if (!order.isValid()) {
                //oops order validation failed. Pinpoint the issue(s).

                if (!order.isValidName()) {
//                nameBox.setError("Buyer name is invalid");
                    Utility.ping(mContext, "Buyer name is invalid");
                }

                if (!order.isValidEmail()) {
//                emailBox.setError("Buyer email is invalid");
                    Utility.ping(mContext, "Buyer email is invalid");
                }

                if (!order.isValidPhone()) {
//                phoneBox.setError("Buyer phone is invalid");
                    Utility.ping(mContext, "Buyer phone is invalid");
                }

                if (!order.isValidAmount()) {
                    edtAmount.setError("Amount is invalid or has more than two decimal places");
                }

                if (!order.isValidDescription()) {
                    edtNarration.setError("Description is invalid");
                }

                if (!order.isValidTransactionID()) {
                    Utility.ping(mContext, "Transaction is Invalid");
                }

                if (!order.isValidRedirectURL()) {
                    Utility.ping(mContext, "Redirection URL is invalid");
                }

                if (!order.isValidWebhook()) {
                    Utility.ping(mContext, "Webhook URL is invalid");
                }

                dialog.dismiss();
                return;
            }

            //Validation is successful. Proceed
            dialog.show();
            Request request = new Request(order, new OrderRequestCallBack() {
                @Override
                public void onFinish(final Order order, final Exception error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            if (error != null) {
                                if (error instanceof Errors.ConnectionError) {
                                    Utility.ping(mContext, "No internet connection");
                                } else if (error instanceof Errors.ServerError) {
                                    Utility.ping(mContext, "Server Error. Try again");
                                } else if (error instanceof Errors.AuthenticationError) {
                                    Utility.ping(mContext, "Access token is invalid or expired. Please Update the token!!");
                                } else if (error instanceof Errors.ValidationError) {
                                    // Cast object to validation to pinpoint the issue
                                    Errors.ValidationError validationError = (Errors.ValidationError) error;

                                    if (!validationError.isValidTransactionID()) {
                                        Utility.ping(mContext, "Transaction ID is not Unique");
                                        return;
                                    }

                                    if (!validationError.isValidRedirectURL()) {
                                        Utility.ping(mContext, "Redirect url is invalid");
                                        return;
                                    }

                                    if (!validationError.isValidWebhook()) {
                                        Utility.ping(mContext, "Webhook url is invalid");
                                        return;
                                    }

                                    if (!validationError.isValidPhone()) {
                                        Utility.ping(mContext, "Buyer's Phone Number is invalid/empty");
                                        return;
                                    }

                                    if (!validationError.isValidEmail()) {
                                        Utility.ping(mContext, "Buyer's Email is invalid/empty");
                                        return;
                                    }

                                    if (!validationError.isValidAmount()) {
                                        edtAmount.setError("Amount is either less than Rs.9 or has more than two decimal places");
                                        return;
                                    }

                                    if (!validationError.isValidName()) {
                                        Utility.ping(mContext, "Buyer's Name is required");
                                        return;
                                    }
                                } else {
                                    Utility.ping(mContext, error.getMessage());
                                }
                                return;
                            }

                            if(isCardPresent){
                                startCustomUI(order);
                            }else {
                                startPreCreatedUI(order);
                            }
                        }
                    });
                }
            });

            request.execute();
        } catch (Exception e) {
            e.printStackTrace();
            Utility.ping(mContext, "Please try again");
        }
    }

    private void startPreCreatedUI(Order order) {
        //Using Pre created UI
        Intent intent = new Intent(getBaseContext(), PaymentDetailsActivity.class);
        intent.putExtra(Constants.ORDER, order);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    private void startCustomUI(Order order) {
        //Custom UI Implementation
        Intent intent = new Intent(getBaseContext(), CustomUIActivity.class);
        intent.putExtra(Constants.ORDER, order);
        intent.putExtra("CardDetails", cardDetail);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    private HttpUrl.Builder getHttpURLBuilder() {
        return new HttpUrl.Builder()
                .scheme("http")
//                .host("api.instamojo.com");//https://test.instamojo.com/oauth2/token/"
                .host("api.instamojo.com");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE){// && data != null) {
            /*String orderID = data.getStringExtra(Constants.ORDER_ID);
            String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
            String paymentID = data.getStringExtra(Constants.PAYMENT_ID);*/

            // Check transactionID, orderID, and orderID for null before using them to check the Payment status.
//            if (orderID != null && transactionID != null && paymentID != null) {
            checkPaymentStatus();//transactionID);
//            } else {
//                Utility.ping(mContext, "Oops!! Payment was cancelled");
//            }
        }else {
            Utility.ping(mContext, "Oops!! Payment was cancelled");
        }
    }

    /**
     * Will check for the transaction status of a particular Transaction
     *
     * //@param transactionID Unique identifier of a transaction ID
     */
    private void checkPaymentStatus(){//final String transactionID) {
        /*if (accessToken == null || transactionID == null) {
            return;
        }*/

        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        Utility.ping(mContext, "checking transaction status");
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("TransactionID", transactionID);

        try {
            boolean resultToPass = false;

            GetPaymentResponseAsyncTask getPaymentResponseAsyncTask = new GetPaymentResponseAsyncTask(hashMap);
            hashMapResult = getPaymentResponseAsyncTask.execute().get();

            if (hashMapResult.get("PaymentStatus").equalsIgnoreCase("successful")) {
                Utility.ping(mContext, mContext.getResources().getString(R.string.strPaymentSuccess));
                resultToPass = true;

                HashMap<String, String> hashMap1 = new HashMap<>();
                hashMap1.put("CustomerID", "saralpayonline@gmail.com");
                hashMap1.put("PaymentStatus", hashMapResult.get("PaymentStatus"));
                hashMap1.put("payment_id", hashMapResult.get("PaymentID"));
                hashMap1.put("transaction_id", hashMapResult.get("TransactionID"));
                hashMap1.put("order_id", hashMapResult.get("OrderID"));

                UpdatePaymentRequestAsyncTask updatePaymentRequestAsyncTask = new UpdatePaymentRequestAsyncTask(hashMap1);
                boolean updateResult = updatePaymentRequestAsyncTask.execute().get();//weather true or false he is going ahead

                dialog.dismiss();
            } else if (hashMapResult.get("PaymentStatus").equalsIgnoreCase("send request")) {

                getPaymentResponseAsyncTask = new GetPaymentResponseAsyncTask(hashMap);
                hashMapResult = getPaymentResponseAsyncTask.execute().get();

            } else {
                resultToPass = false;
                dialog.dismiss();
                Utility.ping(mContext, mContext.getResources().getString(R.string.strPaymentUnSuccess));
            }

            Intent intentSuccessScreen = new Intent(mContext, PaymentSuccessScreen.class);
            intentSuccessScreen.putExtra("payment_result", resultToPass);
            intentSuccessScreen.putExtra("TransactionID", hashMapResult.get("PaymentID"));
            intentSuccessScreen.putExtra("value", edtAmount.getText().toString());
            intentSuccessScreen.putExtra("narration", edtNarration.getText().toString());
            startActivity(intentSuccessScreen);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btnMyProfile) {
                Intent i = new Intent(mContext, MyProfileScreen.class);
                startActivity(i);

            } else if (id == R.id.btnMyReport) {
                Intent i = new Intent(mContext, PaymentReportScreen.class);
                startActivity(i);
            } else if (id == R.id.btnHome) {
                finish();
            } else if (id == R.id.btnHelpAndSupport) {
                Intent i = new Intent(mContext, HelpAndSupportScreen.class);
                startActivity(i);

            } else if (id == R.id.btnLogOut) {
                /*Intent iLogin = new Intent(mContext, LoginScreen.class);
                startActivity(iLogin);
                finish();*/
                Utility.openLogOutDialog(HomeScreen.this);
            } else if (id == R.id.btnLanguage) {
                Utility.showLanguageDialog(HomeScreen.this);

            }else if(id == R.id.btnPlans){
                Intent i = new Intent(mContext, PlansScreen.class);
                startActivity(i);
            }
            popupWindow.dismiss();
        }
    };

    public PopupWindow popupDisplay() {

        popupWindow = new PopupWindow(this);

        // inflate your layout or dynamically add view
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.layout_menu, null);
        Button btnMyProfile, btnMyReport, btnHome, btnHelpAndSupport, btnLogOut, btnLanguage, btnPlans;
        btnMyProfile = (Button) view.findViewById(R.id.btnMyProfile);
        btnMyReport = (Button) view.findViewById(R.id.btnMyReport);
        btnHome = (Button) view.findViewById(R.id.btnHome);
        btnHelpAndSupport = (Button) view.findViewById(R.id.btnHelpAndSupport);
        btnLogOut = (Button) view.findViewById(R.id.btnLogOut);
        btnLanguage = (Button) view.findViewById(R.id.btnLanguage);
        btnPlans = (Button) view.findViewById(R.id.btnPlans);

        btnHome.setVisibility(View.GONE);
        btnMyProfile.setOnClickListener(onClickListener);
        btnMyReport.setOnClickListener(onClickListener);
        btnHome.setOnClickListener(onClickListener);
        btnHelpAndSupport.setOnClickListener(onClickListener);
        btnLogOut.setOnClickListener(onClickListener);
        btnLanguage.setOnClickListener(onClickListener);
        btnPlans.setOnClickListener(onClickListener);

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);

        return popupWindow;
    }

    boolean isBoolean_permission_location = false, isBoolean_permission_phoneState = false;
    public static final int REQUEST_PERMISSIONS_Location = 1;
    public static final int REQUEST_PERMISSIONS_PhoneState = 2;

    private void fn_permission_Location() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(HomeScreen.this, Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_Location);

            }
        } else {
            isBoolean_permission_location = true;
        }
    }

    private void fn_permission_PhoneState() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(HomeScreen.this, Manifest.permission.READ_PHONE_STATE))) {


            } else {
                ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSIONS_PhoneState);

            }
        } else {
            isBoolean_permission_phoneState = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS_Location: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isBoolean_permission_location = true;

                } else {
//                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
            case REQUEST_PERMISSIONS_PhoneState: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isBoolean_permission_phoneState = true;

                } else {
//                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
                fn_permission_Location();
            }
        }
    }

    public void getVersionCodeUpdated(){
        try {
            showProgressDialog();

            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("VersionNo", String.valueOf(pInfo.versionCode));
            Check_AppVersionAsyncTask check_appVersionAsyncTask = new Check_AppVersionAsyncTask(hashMap, new OnCompletionListner() {
                @Override
                public void OnResponseSuccess(Object output) {
                    isVersionCodeUpdated = true;
                    dialog.dismiss();
                }

                @Override
                public void OnResponseFail(Object output) {
                    isVersionCodeUpdated = false;
                    dialog.dismiss();
                }
            });
            check_appVersionAsyncTask.execute();
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
    }
}
