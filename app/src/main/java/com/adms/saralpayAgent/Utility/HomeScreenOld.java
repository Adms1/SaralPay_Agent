package com.adms.saralpayAgent.Utility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.adms.saralpayAgent.AsyncTask.GeneratePaymentRequestAsyncTask;
import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.PaymentScreenInstaMojo;
import com.adms.saralpayAgent.R;

import java.util.HashMap;

public class HomeScreenOld extends AppCompatActivity {

    private Context mContext = this;
    private Button btnCharge;
    private EditText edtAmount, edtNarration;
    private HashMap<String, String> hashMapResult = new HashMap<String, String>();
    private GeneratePaymentRequestAsyncTask generatePaymentRequestAsyncTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        initViews();
        setListners();
    }

    public void initViews(){
        edtAmount = (EditText) findViewById(R.id.edtAmount);
        edtNarration = (EditText) findViewById(R.id.edtNarration);
        btnCharge = (Button) findViewById(R.id.btnCharge);
    }

    public void setListners(){
        btnCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {
                if(edtAmount.getText().toString().equalsIgnoreCase("")){
                    Utility.ping(mContext, "Please enter Amount");
                }else if(edtNarration.getText().toString().equalsIgnoreCase("")){
                    Utility.ping(mContext, "Please enter Narration");
                }else {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("CustomerID", AppConfiguration.CustomerDetail.get("CustomerID"));
                    hashMap.put("Name", AppConfiguration.CustomerDetail.get("CustomerName"));
                    hashMap.put("Email", AppConfiguration.CustomerDetail.get("CustomerEmail"));
                    hashMap.put("Mobile", AppConfiguration.CustomerDetail.get("CustomerMobile"));
                    hashMap.put("Amount", edtAmount.getText().toString().trim());
                    hashMap.put("Description", edtNarration.getText().toString());

                    try {
                        generatePaymentRequestAsyncTask = new GeneratePaymentRequestAsyncTask(hashMap, new OnCompletionListner() {
                            @Override
                            public void OnResponseSuccess(Object output) {
                                hashMapResult = (HashMap<String, String>) output;
                                if(hashMapResult.size() > 0){
                                    Intent iPayment = new Intent(mContext, PaymentScreenInstaMojo.class);
                                    iPayment.putExtra("paymentUrlRequest", hashMapResult.get("RediredURL"));
                                    startActivity(iPayment);
                                }
                            }

                            @Override
                            public void OnResponseFail(Object output) {

                            }
                        });
                        generatePaymentRequestAsyncTask.execute();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }


}
