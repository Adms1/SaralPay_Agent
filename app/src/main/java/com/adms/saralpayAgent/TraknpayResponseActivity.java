package com.adms.saralpayAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class TraknpayResponseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traknpay_response);

        Bundle extras = getIntent().getExtras();
        String transactionId = "";
        String responseCode;
        String responseMessage = "";
        if (extras != null) {
            transactionId = extras.getString("transactionId");
            responseCode = extras.getString("responseCode");
            responseMessage = extras.getString("responseMessage");
        }

        TextView responseMessageView = (TextView) findViewById(R.id.responseMessageView);
        responseMessageView.setText(responseMessage);
        TextView transactionIdView = (TextView) findViewById(R.id.transactionIdView);
        transactionIdView.setText("Transaction ID : " + transactionId);
    }


    /**
     * Called when the user clicks the Make Another Payment Button
     */
    public void onBackButtonClicked(View view) {
        Intent intent = new Intent(this, HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
        startActivity(intent);
        finish(); // call this to finish the current activity
    }

    /**
     * Called when the user clicks the android Back Button
     */
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
        startActivity(intent);
        finish(); // call this to finish the current activity
    }
}
