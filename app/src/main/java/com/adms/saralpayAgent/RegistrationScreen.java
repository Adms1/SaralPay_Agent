package com.adms.saralpayAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.adms.saralpayAgent.AsyncTask.CreateCustomerByAgentAsyncTask;
import com.adms.saralpayAgent.AsyncTask.CustomerDuplicateMobileAsyncTask;
import com.adms.saralpayAgent.AsyncTask.GetStateDetailAsyncTask;
import com.adms.saralpayAgent.AsyncTask.VerifyRegistrationAsyncTask;
import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.Utility.AppConfiguration;
import com.adms.saralpayAgent.Utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;

public class RegistrationScreen extends AppCompatActivity {

    private Context mContext = this;
    private Button btnProeed;
    private EditText edtBusinessName, edtContactName, edtPhoneNumber1, edtCity, edtPinPwd;
    private Spinner spinBusinessType, spinState;
    private WebView wvTermsOfService;
    private HashMap<String, String> regStatus = new HashMap<String, String>();
    private HashMap<String, String> param = new HashMap<String, String>();
    private CreateCustomerByAgentAsyncTask createcustomerByAgentAsyncTask;
    private CheckBox chkTermsAndCondi, chkAcceptPayment;
    private float m_downX;
    private ArrayList<String> stateResult = new ArrayList<String>();
    private ProgressDialog progressDialog;
    String state;
    private GetStateDetailAsyncTask getStateDetailAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        initViews();
        fillState();
        fillBusinessType();
        setListners();
    }

    public void initViews() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        edtPhoneNumber1 = (EditText) findViewById(R.id.edtPhoneNumber1);
        edtPinPwd = (EditText) findViewById(R.id.edtPinPwd);
        edtBusinessName = (EditText) findViewById(R.id.edtBusinessName);
        edtContactName = (EditText) findViewById(R.id.edtContactName);
        edtCity = (EditText) findViewById(R.id.edtCity);
        spinBusinessType = (Spinner) findViewById(R.id.spinBusinessType);
        spinState = (Spinner) findViewById(R.id.spinState);
        btnProeed = (Button) findViewById(R.id.btnProeed);
        chkTermsAndCondi = (CheckBox) findViewById(R.id.chkTermsAndCondi);
        chkAcceptPayment = (CheckBox) findViewById(R.id.chkAcceptPayment);

        wvTermsOfService = (WebView) findViewById(R.id.wvTermsOfService);
        wvTermsOfService.getSettings().setJavaScriptEnabled(true);
        wvTermsOfService.setWebViewClient(new WebViewClient());
        wvTermsOfService.loadUrl("http://saralpayonline.com/terms.aspx");
        removeWVHorizontalScroll();

    }

    public void removeWVHorizontalScroll() {
        wvTermsOfService.setHorizontalScrollBarEnabled(false);
        wvTermsOfService.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getPointerCount() > 1) {
                    //Multi touch detected
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        // save the x
                        m_downX = event.getX();
                    }
                    break;

                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        // set x so that it doesn't move
                        event.setLocation(m_downX, event.getY());
                    }
                    break;

                }

                return false;
            }
        });
    }

    public void showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
    }

    public void fillState() {
        progressDialog.show();

        try {
            final HashMap<String, String> stateParams = new HashMap<>();
            getStateDetailAsyncTask = new GetStateDetailAsyncTask(stateParams, new OnCompletionListner() {
                @Override
                public void OnResponseSuccess(Object output) {
                    stateResult = (ArrayList<String>) output;
                    ArrayAdapter<String> adapterState = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, stateResult);
                    spinState.setAdapter(adapterState);
                    progressDialog.dismiss();
                }

                @Override
                public void OnResponseFail(Object output) {
                    //no state list found
                }
            });
            getStateDetailAsyncTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillBusinessType() {
        ArrayAdapter<String> adapterBusinessType = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, mContext.getResources().getStringArray(R.array.array_business_type));
        spinBusinessType.setAdapter(adapterBusinessType);
    }

    public void setListners() {

        edtPinPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    doRegistration();
                }
                return false;
            }
        });
        spinState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//           state=stateResult.get(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtPhoneNumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtPhoneNumber1.getText().length() == 10) {
                    final ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(RegistrationScreen.this);
                    progressDialog.setMessage("Verifying if mobile number already exists...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("MobileNo", edtPhoneNumber1.getText().toString());
                    CustomerDuplicateMobileAsyncTask customerDuplicateMobileAsyncTask = new CustomerDuplicateMobileAsyncTask(hashMap, new OnCompletionListner() {
                        @Override
                        public void OnResponseSuccess(Object output) {
                            progressDialog.dismiss();
                            Utility.ping(mContext, "This mobile number is already registered");
                            edtPhoneNumber1.setText("");
                        }

                        @Override
                        public void OnResponseFail(Object output) {
                            //nothing happens
                            progressDialog.dismiss();
                            edtPinPwd.requestFocus();
                        }
                    });
                    customerDuplicateMobileAsyncTask.execute();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*if (edtPhoneNumber1.getText().length() < 10) {
                    Utility.ping(mContext, "Phone number be of 10 digits");
                }*/
            }
        });

        btnProeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    new android.app.AlertDialog.Builder(mContext)
                            .setTitle("Confirm Registration")
                            .setIcon(mContext.getResources().getDrawable(R.mipmap.saralpay_icon))
                            .setMessage("Are you sure you want to New Marchant Register?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with manual Entry
                                    doRegistration();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing

                                }
                            })
                            .show();




            }
        });

    }

    public void doRegistration() {
        if (validateInfo()) {

            final ProgressDialog progressDialog;
            progressDialog = new ProgressDialog(RegistrationScreen.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            if (edtContactName.getText().toString().contains(" ")) {
                String[] contactName = edtContactName.getText().toString().split(" ", 2);
                param.put("C_FirstName", contactName[0]);
                param.put("C_LastName", contactName[1]);
            } else {
                param.put("C_FirstName", edtContactName.getText().toString());
                param.put("C_LastName", "");
            }

            param.put("C_Address1", "");
            param.put("C_Address2", "");
            param.put("C_Address3", "");
            param.put("CityName", edtCity.getText().toString());
            param.put("StateName", spinState.getSelectedItem().toString());
            param.put("C_Pincode", "");
            param.put("C_EmailAddress", "");
            param.put("C_Phone1", edtPhoneNumber1.getText().toString());
            param.put("C_Phone2", "");
            param.put("C_ID1cardtype", "");
            param.put("C_ID1number", "");
            param.put("C_AadharCardFront", "");
            param.put("C_AadharCardBack", "");
            param.put("C_ID2cardtype", "");
            param.put("C_ID2cardnumber", "");
            param.put("C_ChequeBookFront", "");
            param.put("C_CompanyName", edtBusinessName.getText().toString());
            param.put("C_BussinessType", String.valueOf(spinBusinessType.getSelectedItemPosition()));
            param.put("C_Bankname", "");
            param.put("C_BranchName", "");//edtBankBranch.getText().toString());//inlcude later
            param.put("C_BankaccountNo", "");
            param.put("C_BankIFCcode", "");
            param.put("C_CreateDate", Utility.getTodaysDate());//todays date
            param.put("C_ApproxDate", Utility.getTodaysDate());//todays date
            param.put("C_Status", "pending");
            param.put("C_TransactionCode", "");
            param.put("C_Passcode", edtPinPwd.getText().toString());
            param.put("ManagerID", AppConfiguration.ExecutiveDetail.get("ManagerID"));
            param.put("ExecutiveID", AppConfiguration.ExecutiveDetail.get("ExecutiveID"));
            param.put("QRCodeID", AppConfiguration.QRDetail.get("QRCodeID"));
            try {
                createcustomerByAgentAsyncTask = new CreateCustomerByAgentAsyncTask(param, new OnCompletionListner() {
                    @Override
                    public void OnResponseSuccess(Object output) {
                        regStatus = (HashMap<String, String>) output;
                        if (regStatus.get("Success").equalsIgnoreCase("True")) {
                            Utility.setPref(mContext, "CustomerID", regStatus.get("CustomerID"));
                            Utility.ping(mContext, "Registration Successfull.");
                            Intent iQRscan = new Intent(mContext, QRScan.class);
                            startActivity(iQRscan);
                            finish();


                        } else {
                            Utility.ping(mContext, "Server not responding. Please try again.");
                        }
                        progressDialog.dismiss();

                    }

                    @Override
                    public void OnResponseFail(Object output) {
                        Utility.ping(mContext, "Server not responding. Please try again.");
                        progressDialog.dismiss();
                    }
                });
                createcustomerByAgentAsyncTask.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean validateInfo() {
        if (!edtBusinessName.getText().toString().equalsIgnoreCase("")) {
            if (spinBusinessType.getSelectedItemPosition() > 0) {
                if (spinState.getSelectedItemPosition() > 0) {
                    if (!edtCity.getText().toString().equalsIgnoreCase("")) {
                        if (!edtContactName.getText().toString().equalsIgnoreCase("")) {
                            if (!edtPhoneNumber1.getText().toString().equalsIgnoreCase("") && edtPhoneNumber1.getText().length() == 10) {
                                if (!edtPinPwd.getText().toString().equalsIgnoreCase("") && edtPinPwd.getText().length() >= 4) {
                                    return true;
//                                    if (chkTermsAndCondi.isChecked() == true) {
//                                        if (chkAcceptPayment.isChecked() == true) {
//                                            return true;
//                                        } else {
//                                            Utility.ping(mContext, "Please Accept the permission for payment.");
//                                        }
//                                    } else {
//                                        Utility.ping(mContext, mContext.getResources().getString(R.string.strAcceptTermsCond));
//                                    }
                                } else {
                                    Utility.ping(mContext, mContext.getResources().getString(R.string.strEnterPin));
                                }
                            } else {
                                Utility.ping(mContext, mContext.getResources().getString(R.string.strEnterPhoneNumber));
                            }
                        } else {
                            Utility.ping(mContext, mContext.getResources().getString(R.string.strEnterContactName));
                        }
                    } else {
                        Utility.ping(mContext, mContext.getResources().getString(R.string.strCity));
                    }
                } else {
                    Utility.ping(mContext, mContext.getResources().getString(R.string.strStateInfo));
                }
            } else {
                Utility.ping(mContext, mContext.getResources().getString(R.string.strEnterBussType));
            }
        } else {
            Utility.ping(mContext, mContext.getResources().getString(R.string.strEnterBussName));
        }


        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent i = new Intent(RegistrationScreen.this,QRScan.class);
        startActivity(i);
    }

}
