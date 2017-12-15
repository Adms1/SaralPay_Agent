package com.adms.saralpayAgent;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adms.saralpayAgent.AsyncTask.CheckOTPStatusAsyncTask;
import com.adms.saralpayAgent.AsyncTask.GenerateOTPByRegisteredNumberAsyncTask;
import com.adms.saralpayAgent.AsyncTask.UpdateCustomerPinAsyncTask;
import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.Utility.Utility;

import java.util.HashMap;

public class ForgotPasswordScreen extends AppCompatActivity{

    private Context mContext = this;
    private Button btnCancel, btnChangePwd, btnSendRegMobNum;
    private EditText edtPinPwd, edtConfirmPinPwd, edtMobileNumber;
    private LinearLayout llEnterMobileNumber, llPin;
    private ProgressDialog progressDialog = null;
    private String receivedOTP = "", customerID = "";
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pwd_screen);

        initViews();
        setListners();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                final String message = intent.getStringExtra("message");
                Utility.ping(mContext, message);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);
    }

    private void initViews() {
        edtConfirmPinPwd = (EditText) findViewById(R.id.edtConfirmPinPwd);
        edtPinPwd = (EditText) findViewById(R.id.edtPinPwd);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnChangePwd = (Button) findViewById(R.id.btnChangePwd);
        edtMobileNumber = (EditText) findViewById(R.id.edtMobileNumber);
        btnSendRegMobNum = (Button) findViewById(R.id.btnSendRegMobNum);
        llEnterMobileNumber = (LinearLayout) findViewById(R.id.llEnterMobileNumber);
        llPin = (LinearLayout) findViewById(R.id.llPin);

        llEnterMobileNumber.setVisibility(View.VISIBLE);
        llPin.setVisibility(View.GONE);

    }

    private void setListners() {
        btnSendRegMobNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("Mobile", edtMobileNumber.getText().toString().trim());

                GenerateOTPByRegisteredNumberAsyncTask generateOTPByRegisteredNumberAsyncTask = new GenerateOTPByRegisteredNumberAsyncTask(hashMap, new OnCompletionListner() {
                    @Override
                    public void OnResponseSuccess(Object output) {
                        HashMap<String, String> hashMapResult = (HashMap<String, String>) output;
                        receivedOTP = hashMapResult.get("OTP");
                        customerID = hashMapResult.get("CustomerID");
                        progressDialog.dismiss();
                        openOTPAlertDialog();

                    }

                    @Override
                    public void OnResponseFail(Object output) {
                        progressDialog.dismiss();
                        Utility.pong(mContext, "This mobile number is not registered with us. Please enter a registered mobile number.");
                    }
                });
                generateOTPByRegisteredNumberAsyncTask.execute();
            }
        });

        edtConfirmPinPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtConfirmPinPwd.getText().length() == 4) {
                    if (edtConfirmPinPwd.getText().toString().equalsIgnoreCase(edtPinPwd.getText().toString())) {
                        //same password
                    } else {
                        edtConfirmPinPwd.setText("");
                        Utility.ping(mContext, mContext.getResources().getString(R.string.strPinDoNotMatch));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtPinPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doFrgtPwd();
                }
                return false;
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFrgtPwd();
            }
        });
    }

    public void doFrgtPwd() {
        if(edtPinPwd.length() < 4 || edtConfirmPinPwd.length() < 4){
            Utility.ping(mContext, "Pin should be of 4 digits");
        }else if (edtPinPwd.getText().toString().equalsIgnoreCase("")) {
            Utility.ping(mContext, mContext.getResources().getString(R.string.strPlEnterPin));

        } else if (edtConfirmPinPwd.getText().toString().equalsIgnoreCase("")) {
            Utility.ping(mContext, "Please confirm Pin");

        } else {

            showProgressDialog();

            final HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("CustomerID", customerID);
            hashMap.put("NewPin", edtConfirmPinPwd.getText().toString());

            UpdateCustomerPinAsyncTask updateCustomerPinAsyncTask = new UpdateCustomerPinAsyncTask(hashMap, new OnCompletionListner() {
                @Override
                public void OnResponseSuccess(Object output) {
                    HashMap<String, String> hashMapResult = (HashMap<String, String>) output;
                    if(hashMapResult.get("Success").equalsIgnoreCase("True")){
                        Utility.ping(mContext, "Password changed successfully.");
                        progressDialog.dismiss();
                        Intent ilogin = new Intent(mContext, LoginScreen.class);
                        ilogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(ilogin);
                        finish();
                    }
                }

                @Override
                public void OnResponseFail(Object output) {
                    Utility.ping(mContext, "There was some problem updating your pin. Please try again");
                    progressDialog.dismiss();
                    llEnterMobileNumber.setVisibility(View.VISIBLE);
                    llPin.setVisibility(View.GONE);
                }
            });
            updateCustomerPinAsyncTask.execute();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    EditText input = null;

    public void openOTPAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Submit OTP");
        builder.setMessage(mContext.getResources().getString(R.string.strOtpMesgText));

        input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final int maxLength = 6;
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        input.setGravity(Gravity.CENTER);
        builder.setView(input);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgressDialog();
                final String m_Text = input.getText().toString();
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("CutomerID", customerID);

                CheckOTPStatusAsyncTask checkOTPStatusAsyncTask = new CheckOTPStatusAsyncTask(hashMap, new OnCompletionListner() {
                    @Override
                    public void OnResponseSuccess(Object output) {
                        HashMap<String, String> result = (HashMap<String, String>) output;
                        if(result.get("OTP").equalsIgnoreCase(m_Text)){
                            alertDialog.dismiss();
                            llEnterMobileNumber.setVisibility(View.GONE);
                            llPin.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        }else {
                            Utility.ping(mContext, "OTP expired or wrong");
                            progressDialog.dismiss();
                            openOTPAlertDialog();
                        }
                    }

                    @Override
                    public void OnResponseFail(Object output) {
                        progressDialog.dismiss();
                        Utility.ping(mContext, "OTP expired or wrong");
                    }
                });
                checkOTPStatusAsyncTask.execute();

            }
        });
        builder.setNegativeButton("Resend", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgressDialog();

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("Mobile", edtMobileNumber.getText().toString().trim());

                GenerateOTPByRegisteredNumberAsyncTask generateOTPByRegisteredNumberAsyncTask = new GenerateOTPByRegisteredNumberAsyncTask(hashMap, new OnCompletionListner() {
                    @Override
                    public void OnResponseSuccess(Object output) {
                        HashMap<String, String> hashMapResult = (HashMap<String, String>) output;
                        receivedOTP = hashMapResult.get("OTP");
                        customerID = hashMapResult.get("CustomerID");
                        progressDialog.dismiss();
                        Utility.ping(mContext, "Code has been resent to your registered number.");
                        openOTPAlertDialog();

                    }

                    @Override
                    public void OnResponseFail(Object output) {
                        progressDialog.dismiss();
                        Utility.pong(mContext, mContext.getResources().getString(R.string.strNumberAlreadyReg));
                    }
                });
                generateOTPByRegisteredNumberAsyncTask.execute();
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                   alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void showProgressDialog(){
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
