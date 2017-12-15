package com.adms.saralpayAgent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.adms.saralpayAgent.AsyncTask.GetPaymentResponseAsyncTask;
import com.adms.saralpayAgent.Utility.Utility;

import java.util.HashMap;

public class PaymentScreenTrackNPay extends AppCompatActivity {

    private Context mContext = this;
    private WebView wvPayment;
    private String url = "";
    private GetPaymentResponseAsyncTask generatePaymentRequestAsyncTask = null;
    private HashMap<String, String> hashMapResult = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_screen);

        initViews();
        setListners();
    }

    private void initViews(){

        url = getIntent().getStringExtra("paymentUrlRequest");
        wvPayment = (WebView) findViewById(R.id.wvPayment);
        wvPayment.getSettings().setJavaScriptEnabled(true);
        wvPayment.setWebViewClient(new WebViewClient());
        wvPayment.loadUrl(url);

        wvPayment.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.v("TEST", url);
                if(url.contains("orderid")){
                    Toast.makeText(mContext, url, Toast.LENGTH_SHORT).show();
                    String[] splitUrl = url.split("\\?");
                    String order_id = splitUrl[1].split("\\=")[1];

                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("OrderID", order_id);

                    try {
                        generatePaymentRequestAsyncTask = new GetPaymentResponseAsyncTask(hashMap);
                        hashMapResult = generatePaymentRequestAsyncTask.execute().get();

                        if(hashMapResult.get("PaymentStatus").equalsIgnoreCase("completed")){
                            Utility.ping(mContext, "Payment Successfull");
                            Intent iSuccess = new Intent(mContext, PaymentSuccessScreen.class);
                            startActivity(iSuccess);
                        }else {

                            Utility.ping(mContext, "Payment UnSuccessfull");
                            Intent iSuccess = new Intent(mContext, PaymentSuccessScreen.class);
                            startActivity(iSuccess);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    view.loadUrl(url);
                }else{
                    view.loadUrl(url);
                }
                return true;
            }
        });
    }

    private void setListners(){

    }
}
