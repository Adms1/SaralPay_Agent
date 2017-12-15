package com.adms.saralpayAgent;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adms.saralpayAgent.AsyncTask.AssignQRCodeToCustomerAsyncTask;
import com.adms.saralpayAgent.AsyncTask.GetCustomerDataByQRCodeorMobilenoAsyncTask;
import com.adms.saralpayAgent.AsyncTask.GetCustomerIDByNumberAsyncTask;
import com.adms.saralpayAgent.AsyncTask.QRCodeDetailAsyncTask;
import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.Utility.AppConfiguration;
import com.adms.saralpayAgent.Utility.Utility;
import com.google.zxing.Result;

import org.w3c.dom.Text;

import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Megha on 02/14/2017.
 */
public class QRScan extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    EditText phonenumber;
    TextView textView1, textView2, CustomerNameTxt, CustomerCompanyTxt;
    Button scanbtn, barcodebtn, buttonScanhide;
    ImageButton btnLogOut;
    String phonestr;
    Context mContext = this;
    private ProgressDialog progressDialog = null;
    int flag = 0;
    String resultQR;
    private QRCodeDetailAsyncTask qrcodedetailAsyncTask = null;
    private HashMap<String, String> hashMapResultQR = new HashMap<String, String>();
    private GetCustomerIDByNumberAsyncTask getcustomerIDByNumberAsyncTask = null;
    private HashMap<String, String> hashmapResultCustomerByNumber = new HashMap<String, String>();
    private AssignQRCodeToCustomerAsyncTask assignQRCodeToCustomerAsyncTask = null;
    private HashMap<String, String> hashMapAssignQR = new HashMap<String, String>();
    RadioGroup radioGroup;
    RadioButton newrb, oldrb;
    LinearLayout linearBarcode, linearMobile, linearDisplayDetail, linearDisplayDetail2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        AppConfiguration.QRDetail.put("QRCodeID", "0");
        AppConfiguration.CustomerDetailByMobile.put("CustomerID", "0");
        findViewById();
        setListener();
        fn_permission_CameraState();
    }

    public void findViewById() {
        phonenumber = (EditText) findViewById(R.id.edtPhoneNumber);
        scanbtn = (Button) findViewById(R.id.buttonScan);
        btnLogOut = (ImageButton) findViewById(R.id.btnLogOut);
        barcodebtn = (Button) findViewById(R.id.btnBarcodeScan);
        newrb = (RadioButton) findViewById(R.id.radioNew);
        oldrb = (RadioButton) findViewById(R.id.radioold);
        textView1 = (TextView) findViewById(R.id.textView3);
        CustomerNameTxt = (TextView) findViewById(R.id.CustomerNameTxt);
        CustomerCompanyTxt = (TextView) findViewById(R.id.CustomerCompanyTxt);
        textView2 = (TextView) findViewById(R.id.txt2);
        linearBarcode = (LinearLayout) findViewById(R.id.linearBarcode);
        linearMobile = (LinearLayout) findViewById(R.id.linearMobile);
        linearDisplayDetail = (LinearLayout) findViewById(R.id.linearDisplayCustomerDetail);
        linearDisplayDetail2 = (LinearLayout) findViewById(R.id.linearDisplayCustomerDetail2);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                AppConfiguration.QRDetail.put("QRCodeID", "0");
                AppConfiguration.CustomerDetailByMobile.put("CustomerID", "0");
                if (checkedId == R.id.radioNew) {
                    flag = 0;
                    linearBarcode.setVisibility(View.VISIBLE);
                    linearMobile.setVisibility(View.GONE);
                    linearDisplayDetail.setVisibility(View.GONE);
                    linearDisplayDetail2.setVisibility(View.GONE);
                    scanbtn.setVisibility(View.GONE);
                } else {
                    flag = 1;
                    linearBarcode.setVisibility(View.VISIBLE);
                    linearMobile.setVisibility(View.VISIBLE);
                    scanbtn.setVisibility(View.VISIBLE);
                }

            }
        });
    }


    public void setListener() {
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.openExecutiveLogOutDialog(QRScan.this);
            }
        });
//        phonenumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    getCustomerDetailByMobileNumber();
//                }
//                return false;
//            }
//        });
        phonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (phonenumber.getText().length() == 10) {
                    getCustomerDetailByMobileNumber();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonestr = phonenumber.getText().toString();
                if (!phonestr.equalsIgnoreCase("")) {
                    if (phonenumber.getText().length() == 10) {
                        if (!AppConfiguration.QRDetail.get("QRCodeID").equalsIgnoreCase("0")) {
                            new android.app.AlertDialog.Builder(mContext)
                                    .setTitle("Alert")
                                    .setIcon(mContext.getResources().getDrawable(R.mipmap.saralpay_icon))
                                    .setMessage("Are you sure you want to Add QRCode?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with manual Entry
                                            getAsignQRCodeToCustomer();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                            setContentView(R.layout.activity_qrscan);
                                            findViewById();
                                            setListener();
                                            oldrb.setChecked(true);
                                            phonenumber.setText(Utility.getPref(mContext, "MobileNo"));
                                            CustomerNameTxt.setText(AppConfiguration.CustomerDetailByMobile.get("CustomerName"));
                                            CustomerCompanyTxt.setText(AppConfiguration.CustomerDetailByMobile.get("CompanyName"));
                                        }
                                    })
                                    .show();
                        } else {
                            Utility.ping(mContext, mContext.getResources().getString(R.string.strQRcodeScan));
                        }
                    } else {
                        Utility.ping(mContext, mContext.getResources().getString(R.string.strwrongNumber));
                    }
                } else {
                    Utility.ping(mContext, mContext.getResources().getString(R.string.strnumberblank));
                }
            }
        });
        barcodebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = Utility.checkPermission(QRScan.this);
                if (result) {
                    mScannerView = new ZXingScannerView(QRScan.this);
                    setContentView(mScannerView);
                    mScannerView.setResultHandler(QRScan.this);
                    mScannerView.startCamera();
                }
            }
        });
    }

    boolean isBoolean_permission_cameraStare = false;
    public static final int REQUEST_PERMISSIONS_CameraState = 1;

    private void fn_permission_CameraState() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(QRScan.this, Manifest.permission.CAMERA))) {


            } else {
                ActivityCompat.requestPermissions(QRScan.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSIONS_CameraState);

            }
        } else {
            isBoolean_permission_cameraStare = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS_CameraState: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isBoolean_permission_cameraStare = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
                }
                fn_permission_CameraState();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mScannerView.stopCamera();
        } catch (Exception e) {
//            Toast.makeText(QRScan.this, "stopCamera", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void handleResult(Result result) {
        //Do anything with result here :D
        Log.w("handleResult", result.getText());
        if (result != null) {
            if (flag == 0) {
                resultQR = result.getText();
                getCustomerDetailByQRScan();
                mScannerView.stopCamera();
                setContentView(R.layout.activity_qrscan);
                findViewById();
                setListener();
                newrb.setChecked(true);
            } else if (flag == 1) {
                resultQR = result.getText();
                getCustomerDetailByQRScanOldCustomer();
                mScannerView.stopCamera();
//                setContentView(R.layout.activity_qrscan);
//                findViewById();
//                setListener();
//                oldrb.setChecked(true);
//                phonenumber.setText(Utility.getPref(mContext, "MobileNo"));
//                CustomerNameTxt.setText(AppConfiguration.CustomerDetailByMobile.get("CustomerName"));
//                CustomerCompanyTxt.setText(AppConfiguration.CustomerDetailByMobile.get("CompanyName"));
            }
        } else {
            Toast.makeText(QRScan.this, "No scan data received!", Toast.LENGTH_LONG).show();
        }
    }

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void getCustomerDetailByQRScan() {
        showProgressDialog();
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("QRCode", resultQR);
        try {
            qrcodedetailAsyncTask = new QRCodeDetailAsyncTask(hashMap, new OnCompletionListner() {
                @Override
                public void OnResponseSuccess(Object output) {
                    progressDialog.dismiss();
                    hashMapResultQR = (HashMap<String, String>) output;
                    AppConfiguration.QRDetail = hashMapResultQR;
                    Utility.setPref(mContext, "QRCodeID", AppConfiguration.QRDetail.get("QRCodeID").toString());
                    Utility.setPref(mContext, "AssignedCustomerID", AppConfiguration.QRDetail.get("AssignedCustomerID").toString());
                    Utility.setPref(mContext, "AssignedCustomerName", AppConfiguration.QRDetail.get("AssignedCustomerName").toString());
                    Utility.setPref(mContext, "AssignedStatus", AppConfiguration.QRDetail.get("AssignedStatus").toString());

                    if (AppConfiguration.QRDetail.get("AssignedStatus").toString().equalsIgnoreCase("0")) {
                        Intent i = new Intent(QRScan.this, RegistrationScreen.class);
                        startActivity(i);
                    } else {
                        new android.app.AlertDialog.Builder(mContext)
                                .setTitle("Alert")
                                .setIcon(mContext.getResources().getDrawable(R.mipmap.saralpay_icon))
                                .setMessage("All Ready Assigned" + " " + AppConfiguration.QRDetail.get("AssignedCustomerName").toString())
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .show();
                    }


                }

                @Override
                public void OnResponseFail(Object output) {
                    progressDialog.dismiss();
                    Utility.ping(mContext, mContext.getResources().getString(R.string.strQRUnSucc));
//                    AppConfiguration.QRDetail.put("QRCodeID", "0");

//                    if (AppConfiguration.QRDetail.get("AssignedStatus").toString().equalsIgnoreCase("0")) {
//
//                    } else {
//                        new android.app.AlertDialog.Builder(mContext)
//                                .setTitle("Alert")
//                                .setIcon(mContext.getResources().getDrawable(R.mipmap.saralpay_icon))
//                                .setMessage(AppConfiguration.QRDetail.get("AssignedCustomerName").toString())
//                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // do nothing
//                                    }
//                                })
//                                .show();
//                    }
                }
            });
            qrcodedetailAsyncTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCustomerDetailByQRScanOldCustomer() {
        showProgressDialog();
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("QRCode", resultQR);
        try {
            qrcodedetailAsyncTask = new QRCodeDetailAsyncTask(hashMap, new OnCompletionListner() {
                @Override
                public void OnResponseSuccess(Object output) {
                    progressDialog.dismiss();

                    hashMapResultQR = (HashMap<String, String>) output;
                    AppConfiguration.QRDetail = hashMapResultQR;
                    Utility.setPref(mContext, "QRCodeID", AppConfiguration.QRDetail.get("QRCodeID").toString());
                    Utility.setPref(mContext, "AssignedCustomerID", AppConfiguration.QRDetail.get("AssignedCustomerID").toString());
                    Utility.setPref(mContext, "AssignedCustomerName", AppConfiguration.QRDetail.get("AssignedCustomerName").toString());
                    Utility.setPref(mContext, "AssignedStatus", AppConfiguration.QRDetail.get("AssignedStatus").toString());

                    if (AppConfiguration.QRDetail.get("AssignedStatus").toString().equalsIgnoreCase("0")) {
                        scanbtn.performClick();
                    } else {
                        new android.app.AlertDialog.Builder(mContext)
                                .setTitle("Alert")
                                .setIcon(mContext.getResources().getDrawable(R.mipmap.saralpay_icon))
                                .setMessage("All Ready Assigned" + " " + AppConfiguration.QRDetail.get("AssignedCustomerName").toString())
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                        setContentView(R.layout.activity_qrscan);
                                        findViewById();
                                        setListener();
                                        oldrb.setChecked(true);
                                        phonenumber.setText(Utility.getPref(mContext, "MobileNo"));
                                        CustomerNameTxt.setText(AppConfiguration.CustomerDetailByMobile.get("CustomerName"));
                                        CustomerCompanyTxt.setText(AppConfiguration.CustomerDetailByMobile.get("CompanyName"));
                                    }
                                }).show();
                    }
                }

                @Override
                public void OnResponseFail(Object output) {
                    progressDialog.dismiss();
                    Utility.ping(mContext, mContext.getResources().getString(R.string.strQRUnSucc));
//                    if (AppConfiguration.QRDetail.get("AssignedStatus").toString().equalsIgnoreCase("0")) {
//
//                    } else {
//                        new android.app.AlertDialog.Builder(mContext)
//                                .setTitle("Alert")
//                                .setIcon(mContext.getResources().getDrawable(R.mipmap.saralpay_icon))
//                                .setMessage(AppConfiguration.QRDetail.get("AssignedCustomerName").toString())
//                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // do nothing
//                                    }
//                                })
//                                .show();
//                    }
                }
            });
            qrcodedetailAsyncTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCustomerDetailByMobileNumber() {
        phonestr = phonenumber.getText().toString();
        if (!phonestr.equalsIgnoreCase("")) {
            if (phonenumber.getText().length() == 10) {
                showProgressDialog();
                final HashMap<String, String> hashMap = new HashMap<String, String>();
                Utility.setPref(mContext, "MobileNo", phonestr);
                hashMap.put("Mobile", phonestr);
                try {
                    getcustomerIDByNumberAsyncTask = new GetCustomerIDByNumberAsyncTask(hashMap, new OnCompletionListner() {
                        @Override
                        public void OnResponseSuccess(Object output) {
                            progressDialog.dismiss();

                            hashmapResultCustomerByNumber = (HashMap<String, String>) output;
                            AppConfiguration.CustomerDetailByMobile = hashmapResultCustomerByNumber;
                            Utility.setPref(mContext, "CustomerID", AppConfiguration.CustomerDetailByMobile.get("CustomerID").toString());
                            Utility.setPref(mContext, "CustomerName", AppConfiguration.CustomerDetailByMobile.get("CustomerName").toString());
                            Utility.setPref(mContext, "CompanyName", AppConfiguration.CustomerDetailByMobile.get("CompanyName").toString());
                            Utility.setPref(mContext, "TypeofBussiness", AppConfiguration.CustomerDetailByMobile.get("TypeofBussiness").toString());

                            linearDisplayDetail.setVisibility(View.VISIBLE);
                            linearDisplayDetail2.setVisibility(View.VISIBLE);
                            CustomerNameTxt.setText(AppConfiguration.CustomerDetailByMobile.get("CustomerName"));
                            CustomerCompanyTxt.setText(AppConfiguration.CustomerDetailByMobile.get("CompanyName"));

                        }

                        @Override
                        public void OnResponseFail(Object output) {
                            progressDialog.dismiss();

                            AppConfiguration.CustomerDetailByMobile.put("CustomerID", "0");
                            Utility.ping(mContext, mContext.getResources().getString(R.string.strMobileUnSucc));
                        }
                    });
                    getcustomerIDByNumberAsyncTask.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getAsignQRCodeToCustomer() {
        phonestr = phonenumber.getText().toString();
        try {
            showProgressDialog();
            final HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("QRCodeID", AppConfiguration.QRDetail.get("QRCodeID"));
            hashMap.put("CustomerID", AppConfiguration.CustomerDetailByMobile.get("CustomerID"));
            try {
                assignQRCodeToCustomerAsyncTask = new AssignQRCodeToCustomerAsyncTask(hashMap, new OnCompletionListner() {
                    @Override
                    public void OnResponseSuccess(Object output) {
                        progressDialog.dismiss();

                        hashMapAssignQR = (HashMap<String, String>) output;
                        AppConfiguration.QRAssign = hashMapAssignQR;
                        Toast.makeText(QRScan.this, AppConfiguration.QRAssign.get("Message").toString(), Toast.LENGTH_LONG).show();
//                        Intent iQr = new Intent(QRScan.this, HomeScreen.class);
//                        startActivity(iQr);
                    }

                    @Override
                    public void OnResponseFail(Object output) {
                        progressDialog.dismiss();
                        Utility.ping(mContext, mContext.getResources().getString(R.string.strMobileUnSucc));
                    }
                });
                assignQRCodeToCustomerAsyncTask.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(QRScan.this, QRScan.class);
        startActivity(i);
//        if (flag == 0)
//        {
//            linearBarcode.setVisibility(View.VISIBLE);
//            linearMobile.setVisibility(View.GONE);
//            scanbtn.setVisibility(View.GONE);
//        }
//        else if(flag==1)
//        {
//            linearBarcode.setVisibility(View.VISIBLE);
//            linearMobile.setVisibility(View.VISIBLE);
//            scanbtn.setVisibility(View.VISIBLE);
//        }

    }
}
//================= Unused code ========================

//compile 'com.journeyapps:zxing-android-embedded:3.4.0'
//        //View objects
//        buttonScan = (Button) findViewById(R.id.buttonScan);
//        textViewName = (TextView) findViewById(R.id.textViewName);
//
//
//        //intializing scan object
////        qrScan = new IntentIntegrator(this);
//
//        //attaching onclick listener
//        textViewName.setOnClickListener(this);
//    }
//}
//Getting the scan results
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result != null) {
//            //if qrcode has nothing in it
//            if (result.getContents() == null)
//            {
//                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
//            }
//            else
//            {
//                //if qr contains data
//                try
//                {
//                    //converting the data to json
//                    JSONObject obj = new JSONObject(result.getContents());
//                    //setting values to textviews
////                    textViewName.setText(result.getContents());
////                    textViewAddress.setText(obj.getString("address"));
//                }
//                catch (JSONException e)
//                {
//                    e.printStackTrace();
//                    //if control comes here
//                    //that means the encoded format not matches
//                    //in this case you can display whatever data is available on the qrcode
//                    //to a toast
//                    textViewName.setText(result.getContents());
//                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//        else
//        {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//    @Override
//    public void onClick(View view) {
//        //initiating the qr code scan
//        qrScan.initiateScan();
//    }
//}
