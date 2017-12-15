package com.adms.saralpayAgent;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;

import com.adms.saralpayAgent.Utility.Utility;


public class Termsandcondition extends AppCompatActivity {
    private WebView wvTermsOfService;
    private CheckBox chkTermsAndCondi, chkAcceptPayment;
    private float m_downX;
    private Button btnproceedt;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsandcondition);

        initViews();

    }

    public void initViews() {
        chkTermsAndCondi = (CheckBox) findViewById(R.id.chkTermsAndCondi);
        chkAcceptPayment = (CheckBox) findViewById(R.id.chkAcceptPayment);
        btnproceedt = (Button) findViewById(R.id.btnproceedt);
        wvTermsOfService = (WebView) findViewById(R.id.wvTermsOfService);
        wvTermsOfService.getSettings().setJavaScriptEnabled(true);
        wvTermsOfService.setWebViewClient(new WebViewClient());
        wvTermsOfService.loadUrl("http://saralpayonline.com/terms.aspx");
        removeWVHorizontalScroll();
        clickListener();
    }

    public void clickListener() {
        btnproceedt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkTermsAndCondi.isChecked() == true) {
                    if (chkAcceptPayment.isChecked() == true) {
                        Utility.ping(mContext, "Registration Successful");
                        Intent iqrScan = new Intent(mContext, QRScan.class);
                        startActivity(iqrScan);
                        finish();
                    } else {
                        Utility.ping(mContext, "Please Accept the permission for payment.");
                    }
                } else {
                    Utility.ping(mContext, mContext.getResources().getString(R.string.strAcceptTermsCond));
                }
            }
        });
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


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//

    }
}
