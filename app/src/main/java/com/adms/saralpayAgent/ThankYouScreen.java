package com.adms.saralpayAgent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.adms.saralpayAgent.AsyncTask.UpgradeCustomerStatusAsyncTask;
import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.Utility.Utility;

import java.util.HashMap;

public class ThankYouScreen extends AppCompatActivity{

    private Context mContext = this;
    private Button btnUpgrade1, btnUpgrade2, btnLogin;
    private HashMap<String, String> hashMapResult = new HashMap<String, String>();
    private UpgradeCustomerStatusAsyncTask upgradeCustomerStatusAsyncTask = null;
    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou_screen);

        initViews();
        setListners();
    }

    private void initViews(){
        btnUpgrade1 = (Button) findViewById(R.id.btnUpgrade1);
        btnUpgrade2 = (Button) findViewById(R.id.btnUpgrade2);
        btnLogin = (Button) findViewById(R.id.btnLogin);
    }

    public void showProgressDialog(){
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void setListners(){

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                    upgradeCustomerStatusAsyncTask = new UpgradeCustomerStatusAsyncTask(hashMap, new OnCompletionListner() {
                        @Override
                        public void OnResponseSuccess(Object output) {
                            Utility.ping(mContext, "Congratulation, You have been upgraded to Premium Membership");
//                            Intent iLogin = new Intent(mContext, LoginScreen.class);
//                            startActivity(iLogin);
                            Utility.setPref(mContext, "membershipStatus", "1");
                            progressDialog.dismiss();
                            finish();
                        }

                        @Override
                        public void OnResponseFail(Object output) {
                            progressDialog.dismiss();
                            Utility.setPref(mContext, "membershipStatus", "");
                            Utility.ping(mContext, "Server not responding, Please try again");
                        }
                    });
                    upgradeCustomerStatusAsyncTask.execute();
                }catch (Exception e){
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
                    upgradeCustomerStatusAsyncTask = new UpgradeCustomerStatusAsyncTask(hashMap, new OnCompletionListner() {
                        @Override
                        public void OnResponseSuccess(Object output) {
                            Utility.ping(mContext, "Congratulation, You have been upgraded to Premium Membership");
//                            Intent iLogin = new Intent(mContext, LoginScreen.class);
//                            startActivity(iLogin);
                            Utility.setPref(mContext, "membershipStatus", "1");
                            progressDialog.dismiss();
                            finish();
                        }

                        @Override
                        public void OnResponseFail(Object output) {
                            progressDialog.dismiss();
                            Utility.setPref(mContext, "membershipStatus", "");
                            Utility.ping(mContext, "Server not responding, Please try again");
                        }
                    });
                    upgradeCustomerStatusAsyncTask.execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
