package com.adms.saralpayAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adms.saralpayAgent.AsyncTask.Check_AppVersionAsyncTask;
import com.adms.saralpayAgent.AsyncTask.Customer_RegistrationDaysAsyncTask;
import com.adms.saralpayAgent.AsyncTask.Customer_ShowpopupAsyncTask;
import com.adms.saralpayAgent.AsyncTask.ExecutiveLoginAsyncTask;
import com.adms.saralpayAgent.AsyncTask.GetCustomerAllDetailAsyncTask;
import com.adms.saralpayAgent.AsyncTask.Get_KYC_CustomerSuccess_PaymentAsyncTask;
import com.adms.saralpayAgent.AsyncTask.UpgradeCustomerStatusAsyncTask;
import com.adms.saralpayAgent.AsyncTask.VerifyLoginAsyncTask;
import com.adms.saralpayAgent.Utility.AppConfiguration;
import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.Utility.Utility;
import com.crittercism.app.Crittercism;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class LoginScreen extends AppCompatActivity {

    private Context mContext = this;
    private Button btnRegister, btnLogin;
    private EditText edtPhoneNumber, edtPinPwd;
    private TextView txtForgotPwd, txtForNewUsers;
    private ImageView imgLoginScreenLogo;
    private TextView txtVersionCode;
    private HashMap<String, String> hashMapResult = new HashMap<String, String>();
    private ExecutiveLoginAsyncTask executiveLoginAsyncTask = null;
    private ProgressDialog progressDialog = null;
    private String PopStatusResult = "";
    private String remainingDays = "";
    private int versionCode = 0;
    private boolean isVersionCodeUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        Crittercism.initialize(mContext, "fe6964b25f264aa6a7c481dc3e9255da00555300");
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(Intent.ACTION_SEND.equals(action)&&type!=null){
            if("text/plain".equals(type)){
                handleSendText(intent);
            }
        }


        initViews();

        //open comments when for use language dialog
        /*if(Utility.getPref(mContext, "LangCount").equalsIgnoreCase("")){
            Utility.showLanguageDialog(this);
            Utility.setPref(mContext, "LangCount", "dontComeAgain");
        }*/
//        getVersionCodeUpdated();
        setListners();

    }

    private void initViews() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        txtVersionCode = (TextView) findViewById(R.id.txtVersionCode);
        txtVersionCode.setText(pInfo.versionName.toString().trim());

        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);

        if (Utility.getPref(mContext, "phone").equalsIgnoreCase("")) {

        } else {
//            edtPhoneNumber.setText(Utility.getPref(mContext, "phone"));
        }

        edtPinPwd = (EditText) findViewById(R.id.edtPinPwd);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtForgotPwd = (TextView) findViewById(R.id.txtForgotPwd);
        imgLoginScreenLogo = (ImageView) findViewById(R.id.imgLoginScreenLogo);
        txtForNewUsers = (TextView) findViewById(R.id.txtForNewUsers);
    }

    private void setListners() {
        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtPhoneNumber.getText().length() == 10) {
                    edtPinPwd.requestFocus();
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

                    if (Utility.isNetworkConnected(LoginScreen.this)) {

//                        if (isVersionCodeUpdated) {
                            if (edtPhoneNumber.getText().length() == 10) {
                                if (edtPinPwd.getText().length() >= 4) {
                                    doLogin();
                                } else {
                                    Utility.ping(mContext, "Enter a 4 digit pin");
                                }

                            } else {
                                Utility.ping(mContext, "Enter a 10 digit phone number");
                            }

//                        } else {
//                            Utility.openVersionDialog(LoginScreen.this);
//
//                        }

                    } else {
                        Utility.ping(mContext, "Network not available");
                    }
                }
                return false;
            }
        });

        /*imgLoginScreenLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                *//*edtPhoneNumber.setText("9016708106");
                edtPinPwd.setText("1234");*//*
                return false;
            }
        });*/

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iRegister = new Intent(mContext, RegistrationScreen.class);
                startActivity(iRegister);
//                finish();
            }
        });

        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iForgotPwd = new Intent(mContext, ForgotPasswordScreen.class);
                startActivity(iForgotPwd);

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utility.isNetworkConnected(LoginScreen.this)) {

//                    if (isVersionCodeUpdated) {
                        if (edtPhoneNumber.getText().length() == 10) {
                            if (edtPinPwd.getText().length() >= 4) {
                                doLogin();
                            } else {
                                Utility.ping(mContext, "Enter a 4 digit pin");
                            }

                        } else {
                            Utility.ping(mContext, "Enter a 10 digit phone number");
                        }

//                    } else {
//                        Utility.openVersionDialog(LoginScreen.this);
//
//                    }

                } else {
                    Utility.ping(mContext, "Network not available");
                }
            }
        });
    }
    public void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            Toast.makeText(LoginScreen.this," " + sharedText,Toast.LENGTH_LONG).show();

        }
    }
    public void checkPopupStatus() {

        showProgressDialog();


        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("CustomerID", AppConfiguration.CustomerDetail.get("CustomerID"));

        Customer_ShowpopupAsyncTask customer_showpopupAsyncTask = new Customer_ShowpopupAsyncTask(hashMap, new OnCompletionListner() {
            @Override
            public void OnResponseSuccess(Object output) {
                HashMap<String, String> hashMapResult = (HashMap<String, String>) output;
                PopStatusResult = hashMapResult.get("PopupStatus").toString();
                Utility.setPref(mContext, "popupstatus", PopStatusResult);
                progressDialog.dismiss();

                if (PopStatusResult.equalsIgnoreCase("0")) {
                    Intent iHome = new Intent(mContext, HomeScreen.class);
                    startActivity(iHome);
                    finish();

                } else if (PopStatusResult.equalsIgnoreCase("1")) {
                    progressDialog.show();
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("CustomerID", AppConfiguration.CustomerDetail.get("CustomerID"));

                    Customer_RegistrationDaysAsyncTask customer_registrationDaysAsyncTask = new Customer_RegistrationDaysAsyncTask(hashMap, new OnCompletionListner() {
                        @Override
                        public void OnResponseSuccess(Object output) {
                            HashMap<String, String> hashMapResult = (HashMap<String, String>) output;
                            remainingDays = hashMapResult.get("NoofDays");
                            showRemainingDaysDialog();
                        }

                        @Override
                        public void OnResponseFail(Object output) {
                            showRemainingDaysDialog();
                        }
                    });
                    customer_registrationDaysAsyncTask.execute();

                } else if (PopStatusResult.equalsIgnoreCase("2")) {
                    KYCDocumentsProcessing();
                } else {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("CustomerID", AppConfiguration.CustomerDetail.get("CustomerID"));

                    Customer_RegistrationDaysAsyncTask customer_registrationDaysAsyncTask = new Customer_RegistrationDaysAsyncTask(hashMap, new OnCompletionListner() {
                        @Override
                        public void OnResponseSuccess(Object output) {
                            HashMap<String, String> hashMapResult = (HashMap<String, String>) output;
                            remainingDays = hashMapResult.get("NoofDays");
                            showRemainingDaysDialog();
                        }

                        @Override
                        public void OnResponseFail(Object output) {
                            showRemainingDaysDialog();
                        }
                    });
                    customer_registrationDaysAsyncTask.execute();
                }
            }

            @Override
            public void OnResponseFail(Object output) {
                progressDialog.dismiss();
                Utility.ping(mContext, "Server not responding. Please try again.");
            }
        });
        customer_showpopupAsyncTask.execute();
    }

    public void doLogin() {

        if (edtPhoneNumber.getText().toString().equalsIgnoreCase("")) {
            Utility.ping(mContext, mContext.getResources().getString(R.string.strPleasePhoneNum));//"Please enter Phone Number");
        } else if (edtPinPwd.getText().toString().equalsIgnoreCase("")) {
            Utility.ping(mContext, mContext.getResources().getString(R.string.strPleasePwd));
        } else {

            showProgressDialog();

            final HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("UserID", edtPhoneNumber.getText().toString());
            hashMap.put("Password", edtPinPwd.getText().toString());

                    /*new Thread(new Runnable() {
                        @Override
                        public void run() {*/
            try {
                executiveLoginAsyncTask = new ExecutiveLoginAsyncTask(hashMap, new OnCompletionListner() {
                    @Override
                    public void OnResponseSuccess(Object output) {
                        progressDialog.dismiss();
                        Utility.ping(mContext, mContext.getResources().getString(R.string.strLoginSucc));//"Login Successful");
                        hashMapResult = (HashMap<String, String>) output;
                        AppConfiguration.ExecutiveDetail = hashMapResult;
                        Utility.setPref(mContext, "ExecutiveID", AppConfiguration.ExecutiveDetail.get("ExecutiveID").toString());
                        Utility.setPref(mContext, "ManagerID", AppConfiguration.ExecutiveDetail.get("ManagerID").toString());
                        Utility.setPref(mContext, "Name", AppConfiguration.ExecutiveDetail.get("Name").toString());

                        Intent iqrscan = new Intent(mContext, QRScan.class);
                        startActivity(iqrscan);
                    }

                    @Override
                    public void OnResponseFail(Object output) {
                        progressDialog.dismiss();
                        Utility.ping(mContext, mContext.getResources().getString(R.string.strLoginUnSucc));
                    }
                });
                executiveLoginAsyncTask.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    EditText input = null;

    public void openOTPAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Submit OTP");

        input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        int maxLength = 6;
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        input.setGravity(Gravity.CENTER);
        builder.setView(input);

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();

            }
        });
        builder.setNegativeButton("Resend", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("CutomerID", AppConfiguration.CustomerDetail.get("CustomerID"));
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

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void showRemainingDaysDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_after_login_popup, null);
        dialogBuilder.setView(dialogView);

        Button btnUpgrade1, btnUpgrade2;
        TextView txtDays;
        ImageView btnClose;
        btnUpgrade1 = (Button) dialogView.findViewById(R.id.btnUpgrade1);
        btnUpgrade2 = (Button) dialogView.findViewById(R.id.btnUpgrade2);
        btnClose = (ImageView) dialogView.findViewById(R.id.btnClose);
        txtDays = (TextView) dialogView.findViewById(R.id.txtDays);
        String daysText = "";
        if (Integer.parseInt(remainingDays) > 1) {
            daysText = "You have " + remainingDays + " DAYS OF FREE TRIAL remaining";
        } else {
            daysText = "You have " + remainingDays + " DAY OF FREE TRIAL remaining";
        }

        txtDays.setText(daysText);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (PopStatusResult.equalsIgnoreCase("3")) {
                    KYCDocumentsProcessing();
                } else {
                    Intent i = new Intent(mContext, HomeScreen.class);
                    startActivity(i);
                    finish();

                }
            }
        });

        btnUpgrade1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showProgressDialog();
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("CustomerID", Utility.getPref(mContext, "CustomerID"));
                    hashMap.put("C_UpgradeStatus", "1");
                    hashMap.put("C_UpgradeDate", Utility.getTodaysDate());
                    UpgradeCustomerStatusAsyncTask upgradeCustomerStatusAsyncTask = new UpgradeCustomerStatusAsyncTask(hashMap, new OnCompletionListner() {
                        @Override
                        public void OnResponseSuccess(Object output) {
                            Utility.ping(mContext, mContext.getResources().getString(R.string.strCongPremUpgrade));
//                            Intent iLogin = new Intent(mContext, LoginScreen.class);
//                            startActivity(iLogin);
//                            Utility.setPref(mContext, "membershipStatus", "1");
                            progressDialog.dismiss();
                            alertDialog.dismiss();
                            if (PopStatusResult.equalsIgnoreCase("3")) {
                                KYCDocumentsProcessing();
                            } else {
                                Intent i = new Intent(mContext, HomeScreen.class);
                                startActivity(i);
                                finish();
                            }
                        }

                        @Override
                        public void OnResponseFail(Object output) {
                            progressDialog.dismiss();
                            Utility.setPref(mContext, "membershipStatus", "");
                            Utility.ping(mContext, "Server not responding, Please try again");
                        }
                    });
                    upgradeCustomerStatusAsyncTask.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnUpgrade2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showProgressDialog();
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("CustomerID", Utility.getPref(mContext, "CustomerID"));
                    hashMap.put("C_UpgradeStatus", "1");
                    hashMap.put("C_UpgradeDate", Utility.getTodaysDate());
                    UpgradeCustomerStatusAsyncTask upgradeCustomerStatusAsyncTask = new UpgradeCustomerStatusAsyncTask(hashMap, new OnCompletionListner() {
                        @Override
                        public void OnResponseSuccess(Object output) {
                            Utility.ping(mContext, mContext.getResources().getString(R.string.strCongPremUpgrade));
//                            Intent iLogin = new Intent(mContext, LoginScreen.class);
//                            startActivity(iLogin);
//                            Utility.setPref(mContext, "membershipStatus", "1");
                            progressDialog.dismiss();
                            alertDialog.dismiss();
                            if (PopStatusResult.equalsIgnoreCase("3")) {
                                KYCDocumentsProcessing();
                            } else {
                                Intent i = new Intent(mContext, HomeScreen.class);
                                startActivity(i);
                                finish();
                            }
                        }

                        @Override
                        public void OnResponseFail(Object output) {
                            progressDialog.dismiss();
                            Utility.setPref(mContext, "membershipStatus", "");
                            Utility.ping(mContext, "Server not responding, Please try again");
                        }
                    });
                    upgradeCustomerStatusAsyncTask.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    HashMap<String, String> customerDetail = new HashMap<>();
    private GetCustomerAllDetailAsyncTask getCustomerAllDetailAsyncTask;

    public void fetchCustomerDetail(final String customerID) {
        showProgressDialog();

        final HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("CutomerID", customerID);

        getCustomerAllDetailAsyncTask = new GetCustomerAllDetailAsyncTask(hashMap, new OnCompletionListner() {
            @Override
            public void OnResponseSuccess(Object output) {
                //fill customer detail
                customerDetail = (HashMap<String, String>) output;

                HashMap<String, String> hashMap1 = new HashMap<>();
                hashMap1.put("FK_CutomerID", customerID);
                Get_KYC_CustomerSuccess_PaymentAsyncTask Get_KYC_CustomerSuccess_PaymentAsyncTask = new Get_KYC_CustomerSuccess_PaymentAsyncTask(hashMap1, new OnCompletionListner() {
                    @Override
                    public void OnResponseSuccess(Object output) {
                        HashMap<String, String> hashmapResult = (HashMap<String, String>) output;
                        showKYCDialog(hashmapResult.get("FinalAmount"));
                        progressDialog.dismiss();
                    }

                    @Override
                    public void OnResponseFail(Object output) {
                        progressDialog.dismiss();
                    }
                });
                Get_KYC_CustomerSuccess_PaymentAsyncTask.execute();

            }

            @Override
            public void OnResponseFail(Object output) {
                Utility.ping(mContext, "Server not responding");
                progressDialog.dismiss();
            }
        });
        getCustomerAllDetailAsyncTask.execute();
    }

    public void KYCDocumentsProcessing() {

        fetchCustomerDetail(AppConfiguration.CustomerDetail.get("CustomerID"));
    }

    public void showKYCDialog(String remainingAmount) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_upload_kyc_popup, null);
        dialogBuilder.setView(dialogView);

        Button btnUplAadharFront, btnUplAadharBack, btnUplChequeFront;
        ImageView btnClose;
        TextView txtTotalAmount;
        btnUplAadharFront = (Button) dialogView.findViewById(R.id.btnUplAadharFront);
        btnUplAadharBack = (Button) dialogView.findViewById(R.id.btnUplAadharBack);
        btnUplChequeFront = (Button) dialogView.findViewById(R.id.btnUplChequeFront);
        btnClose = (ImageView) dialogView.findViewById(R.id.btnClose);
        txtTotalAmount = (TextView) dialogView.findViewById(R.id.txtTotalAmount);

        txtTotalAmount.setText("Rs " + remainingAmount + " Balance ");

        if (!customerDetail.get("C_AadharCardFront").equalsIgnoreCase("")) {
            btnUplAadharFront.setVisibility(View.GONE);

        }
        if (!customerDetail.get("C_AadharCardBack").equalsIgnoreCase("")) {
            btnUplAadharBack.setVisibility(View.GONE);

        }
        if (!customerDetail.get("C_ChequeBookFront").equalsIgnoreCase("")) {
            btnUplChequeFront.setVisibility(View.GONE);

        }

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent i = new Intent(mContext, HomeScreen.class);
                startActivity(i);
                finish();
            }
        });

        btnUplAadharFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    whichImage = "aadharFront";
                    selectImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnUplAadharBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    whichImage = "aadharBack";
                    selectImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnUplChequeFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    whichImage = "chequeFront";
                    selectImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    String userChoosenTask = "", whichImage = "";
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreen.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(LoginScreen.this);

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
                AppConfiguration.bmGalaryImage = bm;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent i = new Intent(mContext, MyProfileScreen.class);
        i.putExtra("fromWhere", "Login");
        i.putExtra("whichImage", whichImage);
//        i.putExtra("imageBitmap", bm);
        startActivity(i);
        finish();

        /*if (aadharFront) {
            imgbtnAadharFront.setImageBitmap(bm);
            imgbtnAadharFrontUplDoc.setImageBitmap(bm);
            imgbtnAadharFront.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharFrontUplDoc.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharFront.setTag("1");
            imgbtnAadharFrontUplDoc.setTag("1");
        } else if (aadharBack) {
            imgbtnAadharBack.setImageBitmap(bm);
            imgbtnAadharBackUplDoc.setImageBitmap(bm);
            imgbtnAadharBack.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharBackUplDoc.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharBack.setTag("1");
            imgbtnAadharBackUplDoc.setTag("1");
        } else {
            imgbtnChequeBook.setImageBitmap(bm);
            imgbtnChequeBookUplDoc.setImageBitmap(bm);
            imgbtnChequeBook.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnChequeBookUplDoc.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnChequeBook.setTag("1");
            imgbtnChequeBookUplDoc.setTag("1");
        }*/
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

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

        Intent i = new Intent(mContext, MyProfileScreen.class);
        i.putExtra("fromWhere", "Login");
        i.putExtra("whichImage", whichImage);
        i.putExtra("imageBitmap", thumbnail);
        startActivity(i);
        finish();

        /*if (aadharFront) {
            imgbtnAadharFront.setImageBitmap(thumbnail);
            imgbtnAadharFrontUplDoc.setImageBitmap(thumbnail);
            imgbtnAadharFront.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharFrontUplDoc.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharFront.setTag("1");
            imgbtnAadharFrontUplDoc.setTag("1");
        } else if (aadharBack) {
            imgbtnAadharBack.setImageBitmap(thumbnail);
            imgbtnAadharBackUplDoc.setImageBitmap(thumbnail);
            imgbtnAadharFront.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharFrontUplDoc.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharBack.setTag("1");
            imgbtnAadharBackUplDoc.setTag("1");
        } else {
            imgbtnChequeBook.setImageBitmap(thumbnail);
            imgbtnChequeBookUplDoc.setImageBitmap(thumbnail);
            imgbtnAadharFront.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnAadharFrontUplDoc.setScaleType(ImageView.ScaleType.FIT_XY);
            imgbtnChequeBook.setTag("1");
            imgbtnChequeBookUplDoc.setTag("1");
        }*/
    }

    public void getVersionCodeUpdated() {
        showProgressDialog();

        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("VersionNo", String.valueOf(versionCode));
        Check_AppVersionAsyncTask check_appVersionAsyncTask = new Check_AppVersionAsyncTask(hashMap, new OnCompletionListner() {
            @Override
            public void OnResponseSuccess(Object output) {
                isVersionCodeUpdated = true;
                progressDialog.dismiss();
            }

            @Override
            public void OnResponseFail(Object output) {
                isVersionCodeUpdated = false;
                progressDialog.dismiss();
            }
        });
        check_appVersionAsyncTask.execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (Utility.currentLocale.equalsIgnoreCase("en")) {
            imgLoginScreenLogo.setImageResource(R.drawable.logo);
        } else if (Utility.currentLocale.equalsIgnoreCase("hi")) {
            imgLoginScreenLogo.setImageResource(R.drawable.logo_hindi);
        }

        edtPhoneNumber.setHint(mContext.getResources().getString(R.string.strPhoneNumber));
        edtPinPwd.setHint(mContext.getResources().getString(R.string.strPinPwd));
        btnLogin.setText(mContext.getResources().getString(R.string.strLogin));
        txtForgotPwd.setText(mContext.getResources().getString(R.string.strForgotPwd));
        btnRegister.setText(mContext.getResources().getString(R.string.strREGISTER));
        txtForNewUsers.setText(mContext.getResources().getString(R.string.strForNewUsers));
    }
}
