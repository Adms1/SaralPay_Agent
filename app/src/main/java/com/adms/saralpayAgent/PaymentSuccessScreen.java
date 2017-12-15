package com.adms.saralpayAgent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adms.saralpayAgent.AsyncTask.CreateCustomer_PlanAsyncTask;
import com.adms.saralpayAgent.AsyncTask.SendSMSToCustomerAsyncTask;
import com.adms.saralpayAgent.AsyncTask.UpdatePaymentRequestAsyncTask;
import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.Utility.AppConfiguration;
import com.adms.saralpayAgent.Utility.Utility;

import java.util.HashMap;

import static android.view.View.GONE;

public class PaymentSuccessScreen extends AppCompatActivity {

    private Context mContext = this;
    private TextView txtUserName, txtSucessFail, txtSucessFailDesc, txtTransactionID, txtValue, txtEmail;
    private EditText edtPhoneNumber;
    private RelativeLayout rlTopBar;
    private Button btnSendReceipt, btnNewCharge;
    private ImageButton btnMenu, btnLogOut;
    private ImageView imvSuccessFail;
    private boolean result = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_screen);

        initViews();
        setListners();
    }

    public void initViews() {
        rlTopBar = (RelativeLayout) findViewById(R.id.rlTopBar);
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtSucessFail = (TextView) findViewById(R.id.txtSucessFail);
        txtSucessFailDesc = (TextView) findViewById(R.id.txtSucessFailDesc);
        txtTransactionID = (TextView) findViewById(R.id.txtTransactionID);
        txtValue = (TextView) findViewById(R.id.txtValue);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        btnSendReceipt = (Button) findViewById(R.id.btnSendReceipt);
        btnNewCharge = (Button) findViewById(R.id.btnNewCharge);
        imvSuccessFail = (ImageView) findViewById(R.id.imvSuccessFail);
        btnLogOut = (ImageButton) findViewById(R.id.btnLogOut);
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);

        txtUserName.setText(AppConfiguration.CustomerDetail.get("CompanyName"));

        if (getIntent().getStringExtra("responseCode").equalsIgnoreCase("0")) {
            imvSuccessFail.setImageResource(R.drawable.success_icon);
            txtSucessFail.setText("Success");
            txtSucessFailDesc.setText("Your transaction was successful");
            txtTransactionID.setText(getIntent().getStringExtra("transactionId"));
            txtValue.setText(getIntent().getStringExtra("amount"));
            txtEmail.setText(getIntent().getStringExtra("description"));
            btnSendReceipt.setVisibility(View.VISIBLE);
            btnNewCharge.setText("New Charge");

        } else {
            imvSuccessFail.setImageResource(R.drawable.fail_icon);
            txtSucessFail.setText("Failure");
            txtSucessFailDesc.setText("Your transaction was not successful\nPlease try again with another card.");
            txtTransactionID.setVisibility(GONE);
            txtValue.setVisibility(GONE);
            txtEmail.setVisibility(GONE);
            btnSendReceipt.setVisibility(GONE);
            edtPhoneNumber.setVisibility(GONE);
            btnNewCharge.setText("Try Again");

        }

        updatePaymentStatus();



    }

    public void checkmobile(){
        if(edtPhoneNumber.length()!=10){
            Utility.ping(mContext,"Enter valid mobile no");
        }
    }

    public void updatePaymentStatus() {

        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Updating Status...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        HashMap<String, String> hashMap1 = new HashMap<>();
        if(!AppConfiguration.planid.equalsIgnoreCase("0")) {
            hashMap1.put("CustomerID","48");
        }
        else
        {
            hashMap1.put("CustomerID", AppConfiguration.CustomerDetail.get("CustomerID"));
        }

        if (getIntent().getStringExtra("responseCode").equalsIgnoreCase("0")) {
            hashMap1.put("PaymentStatus", "successful");
            if (!AppConfiguration.planid.equalsIgnoreCase("0")) {
                createcustomerplan();
            }

        } else {
            hashMap1.put("PaymentStatus", "failed");
        }

        hashMap1.put("payment_id", getIntent().getStringExtra("transactionId"));
        hashMap1.put("transaction_id", getIntent().getStringExtra("order_id"));
        hashMap1.put("order_id", "0");
        hashMap1.put("Trans_Type", getIntent().getStringExtra("Trans_Type"));

        UpdatePaymentRequestAsyncTask updatePaymentRequestAsyncTask = new UpdatePaymentRequestAsyncTask(hashMap1);

        try {
            boolean updateResult = updatePaymentRequestAsyncTask.execute().get();//weather true or false he is going ahead
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createcustomerplan() {

        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("CustomerPlan Status...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        HashMap<String, String> hashMap1 = new HashMap<>();
        hashMap1.put("Fk_Customer_ID", AppConfiguration.CustomerDetail.get("CustomerID"));
        hashMap1.put("Fk_Plan_ID", AppConfiguration.planid);


        CreateCustomer_PlanAsyncTask createCustomerPlanAsyncTask = new CreateCustomer_PlanAsyncTask((hashMap1));
        try {
            boolean createplan = createCustomerPlanAsyncTask.execute().get();//weather true or false he is going ahead
            AppConfiguration.planstatus = AppConfiguration.planid;
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void setListners() {

        btnSendReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPhoneNumber.getText().toString().length()==10) {

                    final ProgressDialog progressDialog = new ProgressDialog(mContext);
                    progressDialog.setMessage("Sending Receipt via SMS");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("CustomerID", Utility.getPref(mContext, "CustomerID"));
                    hashMap.put("TransactionID", getIntent().getStringExtra("order_id"));
                    hashMap.put("customermobile", edtPhoneNumber.getText().toString().trim());
                    SendSMSToCustomerAsyncTask sendSMSToCustomerAsyncTask = new SendSMSToCustomerAsyncTask(hashMap, new OnCompletionListner() {
                        @Override
                        public void OnResponseSuccess(Object output) {
                            HashMap<String, String> hashMapResult = (HashMap<String, String>) output;
                            Utility.ping(mContext, "Receipt sent via SMS");
                            progressDialog.dismiss();

                            Intent ihome=new Intent(mContext,HomeScreen.class);
                            startActivity(ihome);
                            finish();
                        }

                        @Override
                        public void OnResponseFail(Object output) {
                            Utility.ping(mContext, "Failer to send Receipt");
                            progressDialog.dismiss();
                        }
                    });
                    sendSMSToCustomerAsyncTask.execute();

                } else {
                    Utility.ping(mContext, "Enter a 10digit mobile number");
                }
            }
        });

        btnNewCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iHome = new Intent(mContext, HomeScreen.class);
                startActivity(iHome);
                finish();
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.openLogOutDialog(PaymentSuccessScreen.this);
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow popupwindow_obj = popupDisplay();
                popupwindow_obj.showAsDropDown(rlTopBar, 0, 0);

            }
        });
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
                Intent i = new Intent(mContext, HomeScreen.class);
                startActivity(i);
                finish();
            } else if (id == R.id.btnPlans) {
                Intent i = new Intent(mContext, PlansScreen.class);
                startActivity(i);
                finish();
            } else if (id == R.id.btnHelpAndSupport) {
                Intent i = new Intent(mContext, HelpAndSupportScreen.class);
                startActivity(i);
                finish();
            } else if (id == R.id.btnLogOut) {
                Utility.openLogOutDialog(PaymentSuccessScreen.this);
            } else if (id == R.id.btnLanguage) {
                Utility.showLanguageDialog(PaymentSuccessScreen.this);

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
        Button btnMyProfile, btnMyReport, btnHome, btnHelpAndSupport, btnLogOut, btnLanguage,btnPlan;
        btnMyProfile = (Button) view.findViewById(R.id.btnMyProfile);
        btnMyReport = (Button) view.findViewById(R.id.btnMyReport);
        btnHome = (Button) view.findViewById(R.id.btnHome);
        btnHelpAndSupport = (Button) view.findViewById(R.id.btnHelpAndSupport);
        btnLogOut = (Button) view.findViewById(R.id.btnLogOut);
        btnLanguage = (Button) view.findViewById(R.id.btnLanguage);
        btnPlan=(Button)view.findViewById(R.id.btnPlans);

        if (getIntent().getBooleanExtra("payment_result", false)) {
            btnHome.setVisibility(GONE);
        } else {
//            btnMyReport.setVisibility(GONE);
        }
        btnMyReport.setVisibility(View.VISIBLE);
        btnMyProfile.setOnClickListener(onClickListener);
        btnMyReport.setOnClickListener(onClickListener);
        btnHome.setOnClickListener(onClickListener);
        btnHelpAndSupport.setOnClickListener(onClickListener);
        btnLogOut.setOnClickListener(onClickListener);
        btnLanguage.setOnClickListener(onClickListener);
        btnPlan.setOnClickListener(onClickListener);

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);

        return popupWindow;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent iHome = new Intent(mContext, HomeScreen.class);
        startActivity(iHome);
        finish();
    }
}
