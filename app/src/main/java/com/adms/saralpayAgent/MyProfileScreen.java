package com.adms.saralpayAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.adms.saralpayAgent.AsyncTask.GetBusinessTypeAsyncTask;
import com.adms.saralpayAgent.AsyncTask.GetCityDetailStateWiseAsyncTask;
import com.adms.saralpayAgent.AsyncTask.GetCustomerAllDetailAsyncTask;
import com.adms.saralpayAgent.AsyncTask.GetStateDetailAsyncTask;
import com.adms.saralpayAgent.AsyncTask.UpdateRegistrationAsyncTask;
import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.Utility.AppConfiguration;
import com.adms.saralpayAgent.Utility.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MyProfileScreen extends AppCompatActivity {

    private Context mContext = this;
    //    private LinearLayout llTabs;
    private View layoutPersonalInfo, layoutBusinessInfo, layoutTermsAndCond, layoutUploadDocs;
    private RelativeLayout rlTopBar;
    private LinearLayout llAllDocsUploaded;
    private ScrollView llDocumentLayout;
    private Button btnProeed;
    private EditText edtFirstName, edtLastName, edtAddress1, edtAddress2, edtAddress3,
            edtPinCode, edtPinPwd, edtPhoneNumber1, edtPhoneNumber2, edtEmailAddress, edtCompanyName, edtBankName,
            edtBankBranch, edtAccountNumber, edtIfscCode;
    private WebView wvTermsOfService;
    private TextView txtPersonalInfo, txtBusinessInfo, txtTermsAndCond, txtUploadDoc, txtOptOut, txtAadharCardFront, txtAadharCardBack, txtChequebook,
            txtAadharCardFrontUpDoc, txtAadharCardBackUpDoc, txtCheckbookUpDoc;
    private ImageView down_arrow_1, down_arrow_2, down_arrow_3, down_arrow_4;
    private CheckBox chkTermsAndCondi, chkAcceptPayment;
    private ImageButton btnLogOut, btnPersonalInfo, btnBusinessInfo, btnTermsAndCond, btnUploadDoc, btnMenu;
    private ImageView imgbtnAadharFront, imgbtnAadharBack, imgbtnChequeBook, imgbtnAadharFrontUplDoc, imgbtnAadharBackUplDoc, imgbtnChequeBookUplDoc;
    private Spinner spinState, spinCity, spinBusinessType;
    private boolean perInfoDone = false, busInfoDone = false, termsAndCondDone = false, uploadDocDone = true, isFromLogin = false;
    private int whichTab;
    private String currentTabNumber = "1";
    private int aadharFrontImgTaken = 0, aadharBackImgTaken = 0, chequeFrontImgTaken = 0;
    private String[] stepNames;
    private HashMap<String, String> regStatus = new HashMap<String, String>();
    private HashMap<String, String> param = new HashMap<String, String>();
    private ArrayList<String> stateResult = new ArrayList<String>();
    private HashMap<String, String> businessTypeResult = new HashMap<String, String>();
    private ArrayList<String> cityResult = new ArrayList<String>();
    private UpdateRegistrationAsyncTask updateRegistrationAsyncTask;
    private GetStateDetailAsyncTask getStateDetailAsyncTask;
    private GetCityDetailStateWiseAsyncTask getCityDetailStateWiseAsyncTask;
    private GetBusinessTypeAsyncTask getBusinessTypeAsyncTask;
    private ProgressDialog progressDialog;
    private GetCustomerAllDetailAsyncTask getCustomerAllDetailAsyncTask;
    private float m_downX;
    private HashMap<String, String> hashCustomerDetail = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        initViews();

        fillStates();
//        fillBusinessType();
        fillBusinessTypeFromStrings();
        fetchCustomerDetail(AppConfiguration.CustomerDetail.get("CustomerID"));
        setListners();

        if (comingFromLogin()) {
            isFromLogin = true;
            termsAndCondDone = true;

            makeLayout("4");

            if (getIntent().getStringExtra("whichImage").equalsIgnoreCase("aadharFront")) {
                if (!getIntent().hasExtra("imageBitmap")) {
                    imgbtnAadharFrontUplDoc.setImageBitmap(AppConfiguration.bmGalaryImage);
                } else {
                    Bitmap bmp = (Bitmap) getIntent().getParcelableExtra("imageBitmap");
                    imgbtnAadharFrontUplDoc.setImageBitmap(bmp);
                }
                aadharFrontImgTaken = 1;
            } else if (getIntent().getStringExtra("whichImage").equalsIgnoreCase("aadharBack")) {
                if (!getIntent().hasExtra("imageBitmap")) {
                    imgbtnAadharBackUplDoc.setImageBitmap(AppConfiguration.bmGalaryImage);
                } else {
                    Bitmap bmp = (Bitmap) getIntent().getParcelableExtra("imageBitmap");
                    imgbtnAadharBackUplDoc.setImageBitmap(bmp);
                }
                aadharBackImgTaken = 1;
            } else if (getIntent().getStringExtra("whichImage").equalsIgnoreCase("chequeFront")) {
                if (!getIntent().hasExtra("imageBitmap")) {
                    imgbtnChequeBookUplDoc.setImageBitmap(AppConfiguration.bmGalaryImage);
                } else {
                    Bitmap bmp = (Bitmap) getIntent().getParcelableExtra("imageBitmap");
                    imgbtnChequeBookUplDoc.setImageBitmap(bmp);
                }
                chequeFrontImgTaken = 1;
            }
        } else {
            termsAndCondDone = true;
            currentTabNumber = "4";
            makeLayout("4");
        }
    }

    public boolean comingFromLogin() {
        if (getIntent().hasExtra("fromWhere")) {
            return true;
        }
        return false;
    }

    public void showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
    }

    public void fetchCustomerDetail(String customerID) {
        showProgressDialog();

        final HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("CutomerID", customerID);

        getCustomerAllDetailAsyncTask = new GetCustomerAllDetailAsyncTask(hashMap, new OnCompletionListner() {
            @Override
            public void OnResponseSuccess(Object output) {
                //fill customer detail
                HashMap<String, String> customerDetail = (HashMap<String, String>) output;
                fillCustomerDetail(customerDetail);
            }

            @Override
            public void OnResponseFail(Object output) {
                Utility.ping(mContext, "Server not responding");
                progressDialog.dismiss();
            }
        });
        getCustomerAllDetailAsyncTask.execute();
    }

    public void fillCustomerDetail(HashMap<String, String> hashMap) {
        hashCustomerDetail = hashMap;

        //.setText(hashCustomerDetail.get("Name"));
        edtFirstName.setText(hashCustomerDetail.get("C_FirstName"));
        edtFirstName.setEnabled(false);

        if (hashCustomerDetail.get("C_LastName").equalsIgnoreCase("")) {
            edtLastName.setEnabled(true);
        } else {
            edtLastName.setEnabled(false);
            edtLastName.setText(hashCustomerDetail.get("C_LastName"));
        }

        edtAddress1.setText(hashCustomerDetail.get("C_Address1"));
        edtAddress2.setText(hashCustomerDetail.get("C_Address2"));
//        spinState.setSelection(Integer.parseInt(hashCustomerDetail.get("FK_StateID")));
//        spinCity.setSelection(Integer.parseInt(hashCustomerDetail.get("Fk_CityID")));
        edtPinCode.setText(hashCustomerDetail.get("C_Pincode"));
        edtEmailAddress.setText(hashCustomerDetail.get("C_EmailAddress"));
        edtPhoneNumber1.setText(hashCustomerDetail.get("C_Phone1"));
        edtPhoneNumber1.setEnabled(false);

        imgbtnAadharFront.setTag("");
        imgbtnAadharBack.setTag("");
        imgbtnChequeBook.setTag("");
        imgbtnAadharFrontUplDoc.setTag("");
        imgbtnAadharBackUplDoc.setTag("");
        imgbtnChequeBookUplDoc.setTag("");

        if (!hashCustomerDetail.get("C_AadharCardFront").equalsIgnoreCase("")) {
            imgbtnAadharFront.setVisibility(View.GONE);
            imgbtnAadharFrontUplDoc.setVisibility(View.GONE);
            txtAadharCardFront.setVisibility(View.GONE);
            txtAadharCardFrontUpDoc.setVisibility(View.GONE);


        }
        if (!hashCustomerDetail.get("C_AadharCardBack").equalsIgnoreCase("")) {
            imgbtnAadharBack.setVisibility(View.GONE);
            imgbtnAadharBackUplDoc.setVisibility(View.GONE);
            txtAadharCardBack.setVisibility(View.GONE);
            txtAadharCardBackUpDoc.setVisibility(View.GONE);


        }
        if (!hashCustomerDetail.get("C_ChequeBookFront").equalsIgnoreCase("")) {
            imgbtnChequeBook.setVisibility(View.GONE);
            imgbtnChequeBookUplDoc.setVisibility(View.GONE);
            txtChequebook.setVisibility(View.GONE);
            txtCheckbookUpDoc.setVisibility(View.GONE);

        }

//        .setText(hashCustomerDetail.get("C_AadharCardFront"));
//        .setText(hashCustomerDetail.get("C_AadharCardBack"));
//        .setText(hashCustomerDetail.get("C_ChequeBookFront"));
        edtCompanyName.setText(hashCustomerDetail.get("C_CompanyName"));
        edtCompanyName.setEnabled(false);
        spinBusinessType.setSelection(Integer.parseInt(hashCustomerDetail.get("C_BussinessType")));
        spinBusinessType.setEnabled(false);
        edtBankName.setText(hashCustomerDetail.get("C_Bankname"));
        edtAccountNumber.setText(hashCustomerDetail.get("C_BankaccountNo"));
        edtIfscCode.setText(hashCustomerDetail.get("C_BankIFCcode"));
        edtBankBranch.setText(hashCustomerDetail.get("C_BranchName"));
//        .setText(hashCustomerDetail.get("C_CreateDate"));
//        .setText(hashCustomerDetail.get("C_ApproxDate"));
//        .setText(hashCustomerDetail.get("C_Status"));
//        .setText(hashCustomerDetail.get("C_TransactionCode"));
        edtPinPwd.setText(hashCustomerDetail.get("C_Passcode"));
        edtPinPwd.setEnabled(false);
//        .setText(hashCustomerDetail.get("C_Percentage"));
//        .setText(hashCustomerDetail.get("C_ABBR"));
//        .setText(hashCustomerDetail.get("FK_ManagerID"));
//        .setText(hashCustomerDetail.get("FK_ExecutiveID"));

        if (hashCustomerDetail.get("C_AadharCardFront").equalsIgnoreCase("") ||
                hashCustomerDetail.get("C_AadharCardBack").equalsIgnoreCase("") ||
                hashCustomerDetail.get("C_ChequeBookFront").equalsIgnoreCase("")) {
            llAllDocsUploaded.setVisibility(View.GONE);
            llDocumentLayout.setVisibility(View.VISIBLE);
            btnProeed.setVisibility(View.VISIBLE);
        } else {
            llAllDocsUploaded.setVisibility(View.VISIBLE);
            llDocumentLayout.setVisibility(View.GONE);
            btnProeed.setVisibility(View.GONE);
        }

        progressDialog.dismiss();
    }

    public void initViews() {

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        stepNames = mContext.getResources().getStringArray(R.array.array_step_names);
//        llTabs = (LinearLayout) findViewById(R.id.llTabs);
        rlTopBar = (RelativeLayout) findViewById(R.id.rlTopBar);
        layoutPersonalInfo = (View) findViewById(R.id.layoutPersonalInfo);
        layoutBusinessInfo = (View) findViewById(R.id.layoutBusinessInfo);
        layoutTermsAndCond = (View) findViewById(R.id.layoutTermsAndCond);
        layoutUploadDocs = (View) findViewById(R.id.layoutUploadDocs);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtAddress1 = (EditText) findViewById(R.id.edtAddress1);
        edtAddress2 = (EditText) findViewById(R.id.edtAddress2);
        edtAddress3 = (EditText) findViewById(R.id.edtAddress3);
        edtPinCode = (EditText) findViewById(R.id.edtPinCode);
        edtPhoneNumber1 = (EditText) findViewById(R.id.edtPhoneNumber1);
        edtPhoneNumber2 = (EditText) findViewById(R.id.edtPhoneNumber2);
        edtPinPwd = (EditText) findViewById(R.id.edtPinPwd);
        edtEmailAddress = (EditText) findViewById(R.id.edtEmailAddress);
        edtCompanyName = (EditText) findViewById(R.id.edtCompanyName);
        spinBusinessType = (Spinner) findViewById(R.id.spinBusinessType);
        edtBankName = (EditText) findViewById(R.id.edtBankName);
        edtBankBranch = (EditText) findViewById(R.id.edtBankBranch);
        edtAccountNumber = (EditText) findViewById(R.id.edtAccountNumber);
        edtIfscCode = (EditText) findViewById(R.id.edtIfscCode);
        chkTermsAndCondi = (CheckBox) findViewById(R.id.chkTermsAndCondi);
        chkAcceptPayment = (CheckBox) findViewById(R.id.chkAcceptPayment);
        spinState = (Spinner) findViewById(R.id.spinState);
        spinCity = (Spinner) findViewById(R.id.spinCity);
        btnProeed = (Button) findViewById(R.id.btnProeed);
        btnLogOut = (ImageButton) findViewById(R.id.btnLogOut);
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        btnPersonalInfo = (ImageButton) findViewById(R.id.btnPersonalInfo);
        btnBusinessInfo = (ImageButton) findViewById(R.id.btnBusinessInfo);
        btnTermsAndCond = (ImageButton) findViewById(R.id.btnTermsAndCond);
        imgbtnAadharFront = (ImageView) findViewById(R.id.imgbtnAadharFront);
        imgbtnAadharBack = (ImageView) findViewById(R.id.imgbtnAadharBack);
        imgbtnChequeBook = (ImageView) findViewById(R.id.imgbtnChequeBook);
        btnUploadDoc = (ImageButton) findViewById(R.id.btnUploadDoc);
        txtPersonalInfo = (TextView) findViewById(R.id.txtPersonalInfo);
        txtBusinessInfo = (TextView) findViewById(R.id.txtBusinessInfo);
        txtTermsAndCond = (TextView) findViewById(R.id.txtTermsAndCond);
        txtUploadDoc = (TextView) findViewById(R.id.txtUploadDoc);
        down_arrow_1 = (ImageView) findViewById(R.id.down_arrow_1);
        down_arrow_2 = (ImageView) findViewById(R.id.down_arrow_2);
        down_arrow_3 = (ImageView) findViewById(R.id.down_arrow_3);
        down_arrow_4 = (ImageView) findViewById(R.id.down_arrow_4);
        imgbtnAadharFrontUplDoc = (ImageView) findViewById(R.id.imgbtnAadharFrontUplDoc);
        imgbtnAadharBackUplDoc = (ImageView) findViewById(R.id.imgbtnAadharBackUplDoc);
        imgbtnChequeBookUplDoc = (ImageView) findViewById(R.id.imgbtnChequeBookUplDoc);
        txtOptOut = (TextView) findViewById(R.id.txtOptOut);
        txtAadharCardFront = (TextView) findViewById(R.id.txtAadharCardFront);
        txtAadharCardBack = (TextView) findViewById(R.id.txtAadharCardBack);
        txtChequebook = (TextView) findViewById(R.id.txtChequebook);
        txtAadharCardFrontUpDoc = (TextView) findViewById(R.id.txtAadharCardFrontUpDoc);
        txtAadharCardBackUpDoc = (TextView) findViewById(R.id.txtAadharCardBackUpDoc);
        txtCheckbookUpDoc = (TextView) findViewById(R.id.txtCheckbookUpDoc);
        llDocumentLayout = (ScrollView) findViewById(R.id.llDocumentLayout);
        llAllDocsUploaded = (LinearLayout) findViewById(R.id.llAllDocsUploaded);

        if (Utility.getPref(mContext, "popupstatus").toString().equalsIgnoreCase("1") || Utility.getPref(mContext, "popupstatus").toString().equalsIgnoreCase("3")) {
            txtOptOut.setVisibility(View.VISIBLE);
        } else {
            txtOptOut.setVisibility(View.GONE);
        }

        if (Utility.getPref(mContext, "popupstatus").toString().equalsIgnoreCase("2")) {
            llAllDocsUploaded.setVisibility(View.GONE);
            llDocumentLayout.setVisibility(View.VISIBLE);
            btnProeed.setVisibility(View.VISIBLE);
        } else {
            llAllDocsUploaded.setVisibility(View.VISIBLE);
            llDocumentLayout.setVisibility(View.GONE);
            btnProeed.setVisibility(View.GONE);
        }

        wvTermsOfService = (WebView) findViewById(R.id.wvTermsOfService);
        wvTermsOfService.getSettings().setJavaScriptEnabled(true);
        wvTermsOfService.setWebViewClient(new WebViewClient());
        wvTermsOfService.loadUrl("http://saralpayonline.com/terms.aspx");
//        wvTermsOfService.getSettings().setLoadWithOverviewMode(true);
//        wvTermsOfService.getSettings().setUseWideViewPort(true);
//        wvTermsOfService.getSettings().setSupportZoom(true);
//        wvTermsOfService.getSettings().setBuiltInZoomControls(true);
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

    public void setListners() {

        txtOptOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showProgressDialog();
                    Utility.openOptOutDialog(MyProfileScreen.this, progressDialog);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /*wvTermsOfService.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                wvTermsOfService.requestDisallowInterceptTouchEvent(true);
                return v.onTouchEvent(event);
            }
        });*/

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow popupwindow_obj = popupDisplay();
                popupwindow_obj.showAsDropDown(rlTopBar, 0, 0);
            }
        });

        /*edtPhoneNumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtPhoneNumber1.getText().length() == 10) {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("MobileNo", edtPhoneNumber1.getText().toString());
                    CustomerDuplicateMobileAsyncTask customerDuplicateMobileAsyncTask = new CustomerDuplicateMobileAsyncTask(hashMap, new OnCompletionListner() {
                        @Override
                        public void OnResponseSuccess(Object output) {
                            Utility.ping(mContext, "This mobile number is already registered");
                            edtPhoneNumber1.setText("");
                        }

                        @Override
                        public void OnResponseFail(Object output) {
                            //nothing happens
                        }
                    });
                    customerDuplicateMobileAsyncTask.execute();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        btnPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (perInfoDone) {
//                    perInfoDone = false;
                    currentTabNumber = "1";
                    makeLayout("1");
                }
            }
        });

        btnBusinessInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (busInfoDone) {
//                    busInfoDone = false;
                    currentTabNumber = "2";
                    makeLayout("2");
                }
            }
        });

        btnTermsAndCond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (termsAndCondDone) {
//                    termsAndCondDone = false;
                    currentTabNumber = "3";
                    makeLayout("3");
                }
            }
        });

        btnUploadDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadDocDone) {

                    currentTabNumber = "4";
                    makeLayout("4");
                }
            }
        });

        spinState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillCity();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnProeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCustomerProfileFromLogin();
                /*if(isFromLogin){
                    updateCustomerProfileFromLogin();
//                    updateCustomerProfile();

                }else {

                    if (!perInfoDone) {
                        if (validatePersonalInfo()) {
//                        llTabs.removeAllViews();
//                        makeTabs("1");
                            updateCustomerProfile();
                        }
                    } else if (!busInfoDone) {
                        if (validateBusinessInfo()) {
//                        llTabs.removeAllViews();
//                        makeTabs("2");
                            updateCustomerProfile();
                        } else {
                            currentTabNumber = "2";
                            makeLayout("2");
                        }
                    } else if (!termsAndCondDone) {
                        if (chkAcceptPayment.isChecked() && chkTermsAndCondi.isChecked()) {
                            termsAndCondDone = true;
//                        llTabs.removeAllViews();
//                        makeTabs("3");
                            currentTabNumber = "4";
                            makeLayout("4");
                        } else {
                            Utility.ping(mContext, "You need to accept the terms and conditions to proceed");
                            currentTabNumber = "3";
                            makeLayout("3");
                        }
                    } else {
                        if (!uploadDocDone) {
                            Utility.ping(mContext, "Documents not attached");
                        } else {
                                updateCustomerProfile();

                        }
                    }*/
//                }
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent iLogin = new Intent(mContext, LoginScreen.class);
                startActivity(iLogin);
                finish();*/
                Utility.openLogOutDialog(MyProfileScreen.this);
            }
        });

        imgbtnAadharFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aadharFront = true;
                aadharBack = false;
                chequebook = false;
                selectImage();
            }
        });

        imgbtnAadharBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aadharFront = false;
                aadharBack = true;
                chequebook = false;
                selectImage();
            }
        });

        imgbtnChequeBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aadharFront = false;
                aadharBack = false;
                chequebook = true;
                selectImage();
            }
        });

        imgbtnAadharFrontUplDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aadharFront = true;
                aadharBack = false;
                chequebook = false;
                selectImage();
            }
        });

        imgbtnAadharBackUplDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aadharFront = false;
                aadharBack = true;
                chequebook = false;
                selectImage();
            }
        });

        imgbtnChequeBookUplDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aadharFront = false;
                aadharBack = false;
                chequebook = true;
                selectImage();
            }
        });

        edtEmailAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                String email = edtEmailAddress.getText().toString();

                if (hasFocus) {
                    //  Toast.makeText(getApplicationContext(), "got the focus", Toast.LENGTH_LONG).show();
                } else {
                    if (!Utility.isValidEmail(email)) {
                        Utility.openInvalidEmailDialog(MyProfileScreen.this, edtEmailAddress);

                    } else {
                        //Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
//                        new isEmailExistsAsyncTask().execute();

                    }
                }
            }
        });
    }

    public void updateCustomerProfile() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(MyProfileScreen.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        param.put("CustomerID", AppConfiguration.CustomerDetail.get("CustomerID"));
        param.put("C_FirstName", edtFirstName.getText().toString());
        param.put("C_LastName", edtLastName.getText().toString());
        param.put("C_Address1", edtAddress1.getText().toString());
        param.put("C_Address2", edtAddress2.getText().toString());
        param.put("C_Address3", "");
        param.put("CityName", cityResult.size() == 0 ? "" : spinCity.getSelectedItem().toString());
        param.put("StateName", stateResult.size() == 0 ? "" : spinState.getSelectedItem().toString());
        param.put("C_Pincode", edtPinCode.getText().toString());
        param.put("C_EmailAddress", edtEmailAddress.getText().toString());
        param.put("C_Phone1", edtPhoneNumber1.getText().toString());
        param.put("C_Phone2", "");
        param.put("C_ID1cardtype", "");
        param.put("C_ID1number", "");

        if (!hashCustomerDetail.get("C_AadharCardFront").equalsIgnoreCase("")) {
            param.put("C_AadharCardFront", "");
        } else {
            if (imgbtnAadharFrontUplDoc.getTag().toString().equalsIgnoreCase("1")) {
                param.put("C_AadharCardFront", Utility.converImageToStirng(imgbtnAadharFrontUplDoc));
            } else {
                param.put("C_AadharCardFront", "");
            }
        }

        if (!hashCustomerDetail.get("C_AadharCardBack").equalsIgnoreCase("")) {
            param.put("C_AadharCardBack", "");
        } else {
            if (imgbtnAadharBackUplDoc.getTag().toString().equalsIgnoreCase("1")) {
                param.put("C_AadharCardBack", Utility.converImageToStirng(imgbtnAadharBackUplDoc));
            } else {
                param.put("C_AadharCardBack", "");
            }

        }

        if (!hashCustomerDetail.get("C_ChequeBookFront").equalsIgnoreCase("")) {
            param.put("C_ChequeBookFront", "");
        } else {
            if (imgbtnChequeBookUplDoc.getTag().toString().equalsIgnoreCase("1")) {
                param.put("C_ChequeBookFront", Utility.converImageToStirng(imgbtnChequeBookUplDoc));
            } else {
                param.put("C_ChequeBookFront", "");
            }

        }

        param.put("C_ID2cardtype", "");
        param.put("C_ID2cardnumber", "");
        param.put("C_CompanyName", edtCompanyName.getText().toString());
        param.put("C_BussinessType", String.valueOf(spinBusinessType.getSelectedItemPosition()));//later integer values as per selection will be passed
        param.put("C_Bankname", edtBankName.getText().toString());
        param.put("C_BranchName", edtBankBranch.getText().toString());//inlcude later
        param.put("C_BankaccountNo", edtAccountNumber.getText().toString());
        param.put("C_BankIFCcode", edtIfscCode.getText().toString());
        param.put("C_CreateDate", Utility.getTodaysDate());//todays date
        param.put("C_ApproxDate", Utility.getTodaysDate());//todays date
        param.put("C_Status", "register");
        param.put("C_TransactionCode", "");
        param.put("C_Passcode", edtPinPwd.getText().toString());
        param.put("ManagerID", "0");
        param.put("ExecutiveID", "0");
//                        param.put("C_Percentage", "0.01");

                        /*new Thread(new Runnable() {
                            @Override
                            public void run() {*/
        try {
            updateRegistrationAsyncTask = new UpdateRegistrationAsyncTask(param, new OnCompletionListner() {
                @Override
                public void OnResponseSuccess(Object output) {
                    regStatus = (HashMap<String, String>) output;
                    if (regStatus.get("Success").equalsIgnoreCase("True")) {
                        Utility.setPref(mContext, "CustomerID", regStatus.get("CustomerID"));

                        /*Intent HomIntent = new Intent(mContext, LoginScreen.class);
                        startActivity(iLogin);*/
                        if (currentTabNumber.equalsIgnoreCase("1")) {
                            currentTabNumber = "2";
                            makeLayout("2");
                            Utility.ping(mContext, "Personal Information Updated Successfully");

                        } else if (currentTabNumber.equalsIgnoreCase("2")) {
                            currentTabNumber = "3";
                            makeLayout("3");
                            Utility.ping(mContext, "Business Information Updated Successfully");

                        } else if (currentTabNumber.equalsIgnoreCase("4")) {
                            currentTabNumber = "4";
                            makeLayout("4");
                            Utility.ping(mContext, "Information Updated Successfully");
                            finish();
                        }
//                      openOTPAlertDialog(regStatus.get("CustomerID"), regStatus.get("OTP"));

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
            updateRegistrationAsyncTask.execute();


//                                    progressDialog.dismiss();
                                    /*runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(regStatus.get("Success").equalsIgnoreCase("True")){
                                                Utility.setPref(mContext, "CutomerID", regStatus.get("CutomerID"));
                                                finish();
                                            }else {
                                                Utility.ping(mContext, "Something went wrong !!!");
                                            }
                                            progressDialog.dismiss();
                                        }
                                    });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCustomerProfileFromLogin() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(MyProfileScreen.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        param.put("CustomerID", AppConfiguration.CustomerDetail.get("CustomerID"));
        param.put("C_FirstName", edtFirstName.getText().toString());
        param.put("C_LastName", edtLastName.getText().toString());
        param.put("C_Address1", edtAddress1.getText().toString());
        param.put("C_Address2", edtAddress2.getText().toString());
        param.put("C_Address3", "");
        param.put("CityName", cityResult.size() == 0 ? "" : spinCity.getSelectedItem().toString());
        param.put("StateName", stateResult.size() == 0 ? "" : spinState.getSelectedItem().toString());
        param.put("C_Pincode", edtPinCode.getText().toString());
        param.put("C_EmailAddress", edtEmailAddress.getText().toString());
        param.put("C_Phone1", edtPhoneNumber1.getText().toString());
        param.put("C_Phone2", "");
        param.put("C_ID1cardtype", "");
        param.put("C_ID1number", "");

        if (!hashCustomerDetail.get("C_AadharCardFront").equalsIgnoreCase("")) {
            param.put("C_AadharCardFront", "");
        } else {
            if (aadharFrontImgTaken == 1) {
                param.put("C_AadharCardFront", Utility.converImageToStirng(imgbtnAadharFrontUplDoc));
            } else {
                param.put("C_AadharCardFront", "");
            }
        }

        if (!hashCustomerDetail.get("C_AadharCardBack").equalsIgnoreCase("")) {
            param.put("C_AadharCardBack", "");
        } else {
            if (aadharBackImgTaken == 1) {
                param.put("C_AadharCardBack", Utility.converImageToStirng(imgbtnAadharBackUplDoc));
            } else {
                param.put("C_AadharCardBack", "");
            }

        }

        if (!hashCustomerDetail.get("C_ChequeBookFront").equalsIgnoreCase("")) {
            param.put("C_ChequeBookFront", "");
        } else {
            if (chequeFrontImgTaken == 1) {
                param.put("C_ChequeBookFront", Utility.converImageToStirng(imgbtnChequeBookUplDoc));
            } else {
                param.put("C_ChequeBookFront", "");
            }

        }

        param.put("C_ID2cardtype", "");
        param.put("C_ID2cardnumber", "");
        param.put("C_CompanyName", edtCompanyName.getText().toString());
        param.put("C_BussinessType", String.valueOf(spinBusinessType.getSelectedItemPosition()));//later integer values as per selection will be passed
        param.put("C_Bankname", edtBankName.getText().toString());
        param.put("C_BranchName", edtBankBranch.getText().toString());//inlcude later
        param.put("C_BankaccountNo", edtAccountNumber.getText().toString());
        param.put("C_BankIFCcode", edtIfscCode.getText().toString());
        param.put("C_CreateDate", Utility.getTodaysDate());//todays date
        param.put("C_ApproxDate", Utility.getTodaysDate());//todays date
        param.put("C_Status", "register");
        param.put("C_TransactionCode", "");
        param.put("C_Passcode", edtPinPwd.getText().toString());
        param.put("ManagerID", "0");
        param.put("ExecutiveID", "0");
//                        param.put("C_Percentage", "0.01");

                        /*new Thread(new Runnable() {
                            @Override
                            public void run() {*/
        try {
            updateRegistrationAsyncTask = new UpdateRegistrationAsyncTask(param, new OnCompletionListner() {
                @Override
                public void OnResponseSuccess(Object output) {
                    regStatus = (HashMap<String, String>) output;
                    if (regStatus.get("Success").equalsIgnoreCase("True")) {
                        Utility.setPref(mContext, "CustomerID", regStatus.get("CustomerID"));

                        Utility.ping(mContext, "Your KYC documents have been uploaded successfully");
                        Intent HomIntent = new Intent(mContext, HomeScreen.class);
                        startActivity(HomIntent);
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
            updateRegistrationAsyncTask.execute();


//                                    progressDialog.dismiss();
                                    /*runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(regStatus.get("Success").equalsIgnoreCase("True")){
                                                Utility.setPref(mContext, "CutomerID", regStatus.get("CutomerID"));
                                                finish();
                                            }else {
                                                Utility.ping(mContext, "Something went wrong !!!");
                                            }
                                            progressDialog.dismiss();
                                        }
                                    });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (aadharFront) {
            imgbtnAadharFront.setImageBitmap(bm);
            imgbtnAadharFrontUplDoc.setImageBitmap(bm);
            imgbtnAadharFront.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharFrontUplDoc.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharFront.setTag("1");
            imgbtnAadharFrontUplDoc.setTag("1");
            aadharFrontImgTaken = 1;
        } else if (aadharBack) {
            imgbtnAadharBack.setImageBitmap(bm);
            imgbtnAadharBackUplDoc.setImageBitmap(bm);
            imgbtnAadharBack.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharBackUplDoc.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharBack.setTag("1");
            imgbtnAadharBackUplDoc.setTag("1");
            aadharBackImgTaken = 1;
        } else {
            imgbtnChequeBook.setImageBitmap(bm);
            imgbtnChequeBookUplDoc.setImageBitmap(bm);
            imgbtnChequeBook.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnChequeBookUplDoc.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnChequeBook.setTag("1");
            imgbtnChequeBookUplDoc.setTag("1");
            chequeFrontImgTaken = 1;
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = null;
        try {
            thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] imageBytes = bytes.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
////            return encodedImage;
            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (aadharFront) {
            imgbtnAadharFront.setImageBitmap(thumbnail);
            imgbtnAadharFrontUplDoc.setImageBitmap(thumbnail);
            imgbtnAadharFront.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharFrontUplDoc.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharFront.setTag("1");
            imgbtnAadharFrontUplDoc.setTag("1");
            aadharFrontImgTaken = 1;
        } else if (aadharBack) {
            imgbtnAadharBack.setImageBitmap(thumbnail);
            imgbtnAadharBackUplDoc.setImageBitmap(thumbnail);
            imgbtnAadharFront.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharFrontUplDoc.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharBack.setTag("1");
            imgbtnAadharBackUplDoc.setTag("1");
            aadharBackImgTaken = 1;
        } else {
            imgbtnChequeBook.setImageBitmap(thumbnail);
            imgbtnChequeBookUplDoc.setImageBitmap(thumbnail);
            imgbtnAadharFront.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharFrontUplDoc.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnChequeBook.setTag("1");
            imgbtnChequeBookUplDoc.setTag("1");
            chequeFrontImgTaken = 1;
        }
    }




    boolean aadharFront = false, aadharBack = false, chequebook = false;
    String userChoosenTask = "";
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileScreen.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(MyProfileScreen.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    public void makeLayout(String tabNumber) {
        if (tabNumber.equalsIgnoreCase("1")) {

            layoutPersonalInfo.setVisibility(View.VISIBLE);
            layoutBusinessInfo.setVisibility(View.GONE);
            layoutTermsAndCond.setVisibility(View.GONE);
            layoutUploadDocs.setVisibility(View.GONE);
            btnPersonalInfo.setImageResource(R.drawable.personal_info_icon);
            btnBusinessInfo.setImageResource(R.drawable.business_info_grey_icon);
            btnTermsAndCond.setImageResource(R.drawable.terms_cond_grey_icon);
            btnUploadDoc.setImageResource(R.drawable.upload_doc_grey_icon);
            txtBusinessInfo.setTextColor(mContext.getResources().getColor(R.color.gray_light));
            txtTermsAndCond.setTextColor(mContext.getResources().getColor(R.color.gray_light));
            txtUploadDoc.setTextColor(mContext.getResources().getColor(R.color.gray_light));
            down_arrow_1.setVisibility(View.VISIBLE);
            down_arrow_2.setVisibility(View.INVISIBLE);
            down_arrow_3.setVisibility(View.INVISIBLE);
            down_arrow_4.setVisibility(View.INVISIBLE);

        } else if (tabNumber.equalsIgnoreCase("2")) {
            if (perInfoDone) {

                layoutPersonalInfo.setVisibility(View.GONE);
                layoutBusinessInfo.setVisibility(View.VISIBLE);
                layoutTermsAndCond.setVisibility(View.GONE);
                layoutUploadDocs.setVisibility(View.GONE);
                btnPersonalInfo.setImageResource(R.drawable.personal_info_icon);
                btnBusinessInfo.setImageResource(R.drawable.business_info_icon);
                btnTermsAndCond.setImageResource(R.drawable.terms_cond_grey_icon);
                btnUploadDoc.setImageResource(R.drawable.upload_doc_grey_icon);
                txtBusinessInfo.setTextColor(mContext.getResources().getColor(R.color.reg_btn_dark_blue));
                txtTermsAndCond.setTextColor(mContext.getResources().getColor(R.color.gray_light));
                txtUploadDoc.setTextColor(mContext.getResources().getColor(R.color.gray_light));
                down_arrow_1.setVisibility(View.INVISIBLE);
                down_arrow_2.setVisibility(View.VISIBLE);
                down_arrow_3.setVisibility(View.INVISIBLE);
                down_arrow_4.setVisibility(View.INVISIBLE);

            }
        } else if (tabNumber.equalsIgnoreCase("3")) {
            if (busInfoDone) {

                layoutPersonalInfo.setVisibility(View.GONE);
                layoutBusinessInfo.setVisibility(View.GONE);
                layoutTermsAndCond.setVisibility(View.VISIBLE);
                layoutUploadDocs.setVisibility(View.GONE);
                btnPersonalInfo.setImageResource(R.drawable.personal_info_icon);
                btnBusinessInfo.setImageResource(R.drawable.business_info_icon);
                btnTermsAndCond.setImageResource(R.drawable.terms_cond_icon);
                btnUploadDoc.setImageResource(R.drawable.upload_doc_grey_icon);
                txtTermsAndCond.setTextColor(mContext.getResources().getColor(R.color.reg_btn_dark_blue));
                txtUploadDoc.setTextColor(mContext.getResources().getColor(R.color.gray_light));
                down_arrow_1.setVisibility(View.INVISIBLE);
                down_arrow_2.setVisibility(View.INVISIBLE);
                down_arrow_3.setVisibility(View.VISIBLE);
                down_arrow_4.setVisibility(View.INVISIBLE);

            }
        } else if (tabNumber.equalsIgnoreCase("4")) {
            if (termsAndCondDone) {

                layoutPersonalInfo.setVisibility(View.GONE);
                layoutBusinessInfo.setVisibility(View.GONE);
                layoutTermsAndCond.setVisibility(View.GONE);
                layoutUploadDocs.setVisibility(View.VISIBLE);
                btnPersonalInfo.setImageResource(R.drawable.personal_info_icon);
                btnBusinessInfo.setImageResource(R.drawable.business_info_icon);
                btnTermsAndCond.setImageResource(R.drawable.terms_cond_icon);
                btnUploadDoc.setImageResource(R.drawable.upload_doc_icon);
                txtUploadDoc.setTextColor(mContext.getResources().getColor(R.color.reg_btn_dark_blue));
                down_arrow_1.setVisibility(View.INVISIBLE);
                down_arrow_2.setVisibility(View.INVISIBLE);
                down_arrow_3.setVisibility(View.INVISIBLE);
                down_arrow_4.setVisibility(View.VISIBLE);
            }
        }
    }

    public void fillStates() {
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

    public void fillBusinessTypeFromStrings() {
        ArrayAdapter<String> adapterBusinessType = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, mContext.getResources().getStringArray(R.array.array_business_type));
        spinBusinessType.setAdapter(adapterBusinessType);
    }

    public void fillBusinessType() {
        try {
            final HashMap<String, String> params = new HashMap<>();
            getBusinessTypeAsyncTask = new GetBusinessTypeAsyncTask(params, new OnCompletionListner() {
                @Override
                public void OnResponseSuccess(Object output) {
                    businessTypeResult = (HashMap<String, String>) output;
                    ArrayList<String> businessTypeList = new ArrayList<>();
                    for (int i = 1; i < businessTypeResult.size(); i++) {
                        businessTypeList.add(businessTypeResult.get(String.valueOf(i)));
                    }

                    ArrayAdapter<String> adapterBusinessType = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, businessTypeList);
                    spinBusinessType.setAdapter(adapterBusinessType);
                }

                @Override
                public void OnResponseFail(Object output) {

                }
            });
            getBusinessTypeAsyncTask.execute();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillCity() {
        final HashMap<String, String> hashCity = new HashMap<>();
        hashCity.put("StateName", spinState.getSelectedItem().toString());

        try {
            getCityDetailStateWiseAsyncTask = new GetCityDetailStateWiseAsyncTask(hashCity, new OnCompletionListner() {
                @Override
                public void OnResponseSuccess(Object output) {
                    cityResult = (ArrayList<String>) output;
                    ArrayAdapter<String> adapterState = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, cityResult);
                    spinCity.setAdapter(adapterState);
                    progressDialog.dismiss();
                }

                @Override
                public void OnResponseFail(Object output) {

                }
            });
            getCityDetailStateWiseAsyncTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean validateBusinessInfo() {
        if (!edtCompanyName.getText().toString().equalsIgnoreCase("")) {
//            if (!edtBusinessType.getText().toString().equalsIgnoreCase("")) {
            if (!edtBankName.getText().toString().equalsIgnoreCase("")) {
                if (!edtBankBranch.getText().toString().equalsIgnoreCase("")) {
                    if (!edtAccountNumber.getText().toString().equalsIgnoreCase("")) {
                        if (!edtIfscCode.getText().toString().equalsIgnoreCase("")) {
                            busInfoDone = true;
                            return true;
                        } else {
                            Utility.ping(mContext, mContext.getResources().getString(R.string.strPleaseIfscCode));
                        }
                    } else {
                        Utility.ping(mContext, mContext.getResources().getString(R.string.strPleaseAccNumber));
                    }
                } else {
                    Utility.ping(mContext, mContext.getResources().getString(R.string.strPleaseBankBranch));
                }
            } else {
                Utility.ping(mContext, mContext.getResources().getString(R.string.strPleaseBankName));
            }
            /*} else {
                Utility.ping(mContext, "Please enter Business Type");
            }*/
        } else {
            Utility.ping(mContext, mContext.getResources().getString(R.string.strPleaseCompName));
        }
        return false;
    }

    public boolean validatePersonalInfo() {
        if (!edtFirstName.getText().toString().equalsIgnoreCase("")) {
            if (!edtLastName.getText().toString().equalsIgnoreCase("")) {
                if (!edtAddress1.getText().toString().equalsIgnoreCase("")) {
//                    if (!edtAddress2.getText().toString().equalsIgnoreCase("")) {
//                        if (!edtAddress3.getText().toString().equalsIgnoreCase("")) {
                    if (!spinState.getSelectedItem().toString().equalsIgnoreCase("")) {
                        if (!spinCity.getSelectedItem().toString().equalsIgnoreCase("")) {
                            if (!edtPinCode.getText().toString().equalsIgnoreCase("")) {
                                if (!edtEmailAddress.getText().toString().equalsIgnoreCase("")) {
                                    if (Utility.isValidEmail(edtEmailAddress.getText().toString())) {
                                        if (!edtPhoneNumber1.getText().toString().equalsIgnoreCase("")) {
//                                                if (!edtPhoneNumber2.getText().toString().equalsIgnoreCase("")) {
                                            if (!edtPinPwd.getText().toString().equalsIgnoreCase("")) {
                                                perInfoDone = true;
                                                return true;
                                            } else {
                                                Utility.ping(mContext, mContext.getResources().getString(R.string.strPleasePwd));
                                            }
                                                /*} else {
                                                    Utility.ping(mContext, "Please enter Phone Number 2");
                                                }*/
                                        } else {
                                            Utility.ping(mContext, mContext.getResources().getString(R.string.strPleasePhoneNum));
                                        }
                                    } else {
                                        Utility.openInvalidEmailDialog(MyProfileScreen.this, edtEmailAddress);
                                    }
                                } else {
                                    Utility.ping(mContext, mContext.getResources().getString(R.string.strPleaseEmailAdd));
                                }
                            } else {
                                Utility.ping(mContext, mContext.getResources().getString(R.string.strPleasePinCode));
                            }
                        } else {
                            Utility.ping(mContext, mContext.getResources().getString(R.string.strPleaseEntCity));
                        }
                    } else {
                        Utility.ping(mContext, mContext.getResources().getString(R.string.strPleaseState));//"Please enter State");
                    }
                        /*} else {
                            Utility.ping(mContext, "Please enter Address 3");
                        }*/
                    /*} else {
                        Utility.ping(mContext, "Please enter Address 2");
                    }*/
                } else {
                    Utility.ping(mContext, mContext.getResources().getString(R.string.strPleaseAdd1));
                }
            } else {
                Utility.ping(mContext, mContext.getResources().getString(R.string.strPleaseLN));
            }
        } else {
            Utility.ping(mContext, mContext.getResources().getString(R.string.strPleaseFN));
        }
        return false;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent iHome = new Intent(mContext, HomeScreen.class);
        startActivity(iHome);
        finish();
        /*if (currentTabNumber.equalsIgnoreCase("4")) {
            btnTermsAndCond.performClick();
        } else if (currentTabNumber.equalsIgnoreCase("3")) {
            btnBusinessInfo.performClick();
        } else if (currentTabNumber.equalsIgnoreCase("2")) {
            btnPersonalInfo.performClick();
        } else {
            Intent iHome = new Intent(mContext, HomeScreen.class);
            startActivity(iHome);
            finish();
        }*/
    }

    EditText input = null;

    public void openOTPAlertDialog(final String customerID, String otpFromServer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Enter OTP");

        input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        int maxLength = 6;
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        input.setGravity(Gravity.CENTER);
        builder.setView(input);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String m_Text = input.getText().toString();

                //Compare OTP from server and SMS
                if (input.getText().toString().equalsIgnoreCase("123")) {
                    dialog.cancel();
                    Intent iLogin = new Intent(mContext, LoginScreen.class);
                    startActivity(iLogin);
                    finish();
                } else {
                    Utility.ping(mContext, "False OTP entered, Please re-enter the OTP sent.");
                }
            }
        });
        builder.setNegativeButton("Resend", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("CutomerID", customerID);
                GenerateOTPAsyncTask generateOTPAsyncTask = new GenerateOTPAsyncTask(hashMap, new OnCompletionListner() {
                    @Override
                    public void OnResponseSuccess(Object output) {
                        HashMap<String, String> resultHashMap = (HashMap<String, String>) output;
                        String resendOTP = resultHashMap.get("OTP");
                        //check if resend otp == sms otp
                    }

                    @Override
                    public void OnResponseFail(Object output) {

                    }
                });
                generateOTPAsyncTask.execute();*/
            }
        });

        builder.show();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btnMyProfile) {
                Intent i = new Intent(mContext, MyProfileScreen.class);
                startActivity(i);
                finish();
            } else if (id == R.id.btnMyReport) {
                Intent i = new Intent(mContext, PaymentReportScreen.class);
                startActivity(i);
                finish();
            } else if (id == R.id.btnHome) {
                Intent iHome = new Intent(mContext, HomeScreen.class);
                startActivity(iHome);
                finish();
            } else if (id == R.id.btnHelpAndSupport) {
                Intent i = new Intent(mContext, HelpAndSupportScreen.class);
                startActivity(i);
                finish();
            } else if (id == R.id.btnLogOut) {
                /*Intent iLogin = new Intent(mContext, LoginScreen.class);
                iLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iLogin);
                finish();*/
                Utility.openLogOutDialog(MyProfileScreen.this);
            } else if (id == R.id.btnLanguage) {
                Utility.showLanguageDialog(MyProfileScreen.this);

            } else if (id == R.id.btnPlans) {
                Intent i = new Intent(mContext, PlansScreen.class);
                startActivity(i);
            }
            popupWindow.dismiss();
        }
    };

    private PopupWindow popupWindow;

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

        btnMyProfile.setVisibility(View.GONE);

        btnHome.setOnClickListener(onClickListener);
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
}
