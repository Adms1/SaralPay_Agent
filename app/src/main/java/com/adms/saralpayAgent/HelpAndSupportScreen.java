package com.adms.saralpayAgent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adms.saralpayAgent.AsyncTask.VerifyLoginAsyncTask;
import com.adms.saralpayAgent.Utility.Utility;

import java.util.HashMap;

public class HelpAndSupportScreen extends AppCompatActivity {

    private Context mContext = this;
    private TextView txtEmail, txtPhone;
    private ImageButton btnMenu, btnLogOut;
    private RelativeLayout rlTopBar;
    private HashMap<String, String> hashMapResult = new HashMap<String, String>();
    private VerifyLoginAsyncTask verifyLoginAsyncTask = null;
    private ProgressDialog progressDialog = null;
    private String emailAddress = "support@saralpayonline.com", phoneNumber = "+917575809733";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support_screen);

        initViews();
        setListners();
    }

    private void initViews() {
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtPhone = (TextView) findViewById(R.id.txtPhone);
        btnLogOut = (ImageButton) findViewById(R.id.btnLogOut);
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        rlTopBar = (RelativeLayout) findViewById(R.id.rlTopBar);

        txtEmail.setText(Html.fromHtml("<span COLOR=\"#000000\">Email: </span><u><FONT COLOR=\"#2c96b8\" >" + emailAddress + "</Font></u>"));
        txtPhone.setText(Html.fromHtml("<span COLOR=\"#000000\">Phone: </span><u><FONT COLOR=\"#2c96b8\" >" + phoneNumber + "</Font></u>"));
    }

    private void setListners() {
        txtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"support@saralpayonline.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                Intent mailer = Intent.createChooser(intent, null);
                startActivity(mailer);
            }
        });

        txtPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent iLogin = new Intent(mContext, LoginScreen.class);
                startActivity(iLogin);
                finish();*/
                Utility.openLogOutDialog(HelpAndSupportScreen.this);
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

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
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
                finish();
            } else if(id == R.id.btnPlans){
                Intent i =new Intent(mContext,PlansScreen.class);
                startActivity(i);
                finish();
            }
            else if (id == R.id.btnHelpAndSupport) {
                /*Intent i = new Intent(mContext, HelpAndSupportScreen.class);
                startActivity(i);
                finish();*/
            } else if (id == R.id.btnLogOut) {
                Utility.openLogOutDialog(HelpAndSupportScreen.this);
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
        Button btnMyProfile, btnMyReport, btnHome,btnPlan, btnHelpAndSupport, btnLogOut;
        btnPlan =(Button)view.findViewById(R.id.btnPlans);
        btnMyProfile = (Button) view.findViewById(R.id.btnMyProfile);
        btnMyReport = (Button) view.findViewById(R.id.btnMyReport);
        btnHome = (Button) view.findViewById(R.id.btnHome);
        btnHelpAndSupport = (Button) view.findViewById(R.id.btnHelpAndSupport);
        btnLogOut = (Button) view.findViewById(R.id.btnLogOut);

        if (getIntent().getBooleanExtra("payment_result", false)) {
            btnHome.setVisibility(View.GONE);
        } else {
//            btnMyReport.setVisibility(View.GONE);
        }
        btnHelpAndSupport.setVisibility(View.GONE);
        btnMyProfile.setOnClickListener(onClickListener);
        btnMyReport.setOnClickListener(onClickListener);
        btnHome.setOnClickListener(onClickListener);
        btnPlan.setOnClickListener(onClickListener);
        btnHelpAndSupport.setOnClickListener(onClickListener);
        btnLogOut.setOnClickListener(onClickListener);

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);

        return popupWindow;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
