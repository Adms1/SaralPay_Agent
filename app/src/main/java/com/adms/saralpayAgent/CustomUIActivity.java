package com.adms.saralpayAgent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adms.saralpayAgent.Utility.AppConfiguration;
import com.adms.saralpayAgent.Utility.Utility;
import com.instamojo.android.activities.PaymentActivity;
import com.instamojo.android.callbacks.JusPayRequestCallback;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.models.Card;
import com.instamojo.android.models.Errors;
import com.instamojo.android.models.Order;
import com.instamojo.android.network.Request;

public class CustomUIActivity extends AppCompatActivity {

    private ImageButton btnMenu, btnLogOut;
    private RelativeLayout rlTopBar;
    private TextView txtUserName;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_form);
        Log.v("Customui: ", "Called");
        makeUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //send back the result to Main activity
        if (requestCode == Constants.REQUEST_CODE) {
            setResult(resultCode);
            setIntent(data);
            finish();
        }
    }

    private void makeUI() {
        /*final Order order = getIntent().getParcelableExtra(Constants.ORDER);
        //finish the activity if the order is null or both the debit and netbanking is disabled
        if (order == null || (order.getCardOptions() == null
                && order.getNetBankingOptions() == null)) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }*/

        final AppCompatEditText cardNumber = (AppCompatEditText) findViewById(R.id.card_number);

        final AppCompatEditText cardExpiryDateMonth = (AppCompatEditText) findViewById(R.id.card_expiry_date_Month);

        final AppCompatEditText cardExpiryDateYear = (AppCompatEditText) findViewById(R.id.card_expiry_date_Year);

        final AppCompatEditText cardHoldersName = (AppCompatEditText) findViewById(R.id.card_holder_name);

        final AppCompatEditText cvv = (AppCompatEditText) findViewById(R.id.card_cvv);

        final AppCompatButton btnCancel = (AppCompatButton) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rlTopBar = (RelativeLayout) findViewById(R.id.rlTopBar);

        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        btnLogOut = (ImageButton) findViewById(R.id.btnLogOut);
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtUserName.setText("Payment Portal for\n" + AppConfiguration.CustomerDetail.get("CompanyName"));

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow popupwindow_obj = popupDisplay();
                popupwindow_obj.showAsDropDown(rlTopBar, 0, 0);
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.openLogOutDialog(CustomUIActivity.this);
            }
        });

        AppCompatButton proceed = (AppCompatButton) findViewById(R.id.proceed_with_card);
        View separator = findViewById(R.id.net_banking_separator);
        AppCompatSpinner netBankingSpinner = (AppCompatSpinner) findViewById(R.id.net_banking_spinner);

        /*if (order.getCardOptions() == null) {
            //seems like card payment is not enabled
            findViewById(R.id.card_layout_1).setVisibility(View.GONE);
            findViewById(R.id.card_layout_2).setVisibility(View.GONE);
            proceed.setVisibility(View.GONE);
            separator.setVisibility(View.GONE);
        } else {*/
            String cardDetails = getIntent().getStringExtra("CardDetails");
            String[] details = cardDetails.split("\\-");//card.CardNumber + "-" + card.CardHolderName + " - " + card.ValidToDate
            cardNumber.setText(details[0].trim());
            cardHoldersName.setText(details[1].trim());

            String expiryDate = details[2].trim();
            expiryDate = expiryDate.substring(0, 2) + "/" + expiryDate.substring(2, expiryDate.length());
            cardExpiryDateMonth.setText(expiryDate.split("\\/")[0]);
            cardExpiryDateYear.setText(expiryDate.split("\\/")[1]);

            /*cardNumber.setText("4216280024034993");
            cardHoldersName.setText("Shrenik Diwanji");
            cardExpiryDateMonth.setText("07");
            cardExpiryDateYear.setText("22");*/

            cvv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        Bundle extras = getIntent().getExtras();
                        Intent intent = new Intent(CustomUIActivity.this, TraknpayRequestActivity.class);
                        intent.putExtra("order_id", extras.getString("order_id"));
                        intent.putExtra("amount", extras.getString("amount"));
                        intent.putExtra("mode", "LIVE");
                        intent.putExtra("CardDetails", extras.getString("CardDetails"));
                        intent.putExtra("description", extras.getString("description"));
                        startActivity(intent);
                        /*Card card = new Card();
                        card.setCardNumber(cardNumber.getText().toString());
                        card.setDate(cardExpiryDateMonth.getText().toString() + "/" +cardExpiryDateYear.getText().toString());
                        card.setCardHolderName(cardHoldersName.getText().toString());
                        card.setCvv(cvv.getText().toString());

                        //Validate the card here
                        if (!cardValid(card)) {
                            return false;
                        }

                        //Get order details form Juspay
                        proceedWithCard(order, card);*/
                    }
                    return false;
                }
            });

            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle extras = getIntent().getExtras();
                    Intent intent = new Intent(CustomUIActivity.this, TraknpayRequestActivity.class);
                    intent.putExtra("order_id", extras.getString("order_id"));
                    intent.putExtra("amount", extras.getString("amount"));
                    intent.putExtra("mode", "LIVE");
                    intent.putExtra("CardDetails", extras.getString("CardDetails"));
                    intent.putExtra("description", extras.getString("description"));
                    startActivity(intent);

                    /*Card card = new Card();
                    card.setCardNumber(cardNumber.getText().toString());
                    card.setDate(cardExpiryDateMonth.getText().toString() + "/" +cardExpiryDateYear.getText().toString());
                    card.setCardHolderName(cardHoldersName.getText().toString());
                    card.setCvv(cvv.getText().toString());

                    //Validate the card here
                    if (!cardValid(card)) {
                        return;
                    }

                    //Get order details form Juspay
                    proceedWithCard(order, card);*/
                }
            });
//        }

        /*if (order.getNetBankingOptions() == null) {
            //seems like netbanking is not enabled
            separator.setVisibility(View.GONE);
            netBankingSpinner.setVisibility(View.GONE);
        } else {
            final ArrayList<String> banks = new ArrayList<>();
            banks.addAll(order.getNetBankingOptions().getBanks().keySet());
            Collections.sort(banks);
            banks.add(0, "Select a Bank");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, banks);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            netBankingSpinner.setAdapter(adapter);
            netBankingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        return;
                    }

                    //User selected a Bank. Hence proceed to Juspay
                    String bankCode = order.getNetBankingOptions().getBanks().get(banks.get(position));
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.URL, order.getNetBankingOptions().getUrl());
                    bundle.putString(Constants.POST_DATA, order.
                            getNetBankingOptions().getPostData(order.getAuthToken(), bankCode));
                    startPaymentActivity(bundle);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }*/
    }

    private void proceedWithCard(Order order, Card card) {
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                getString(com.instamojo.android.R.string.please_wait), true, false);
        Request request = new Request(order, card, new JusPayRequestCallback() {
            @Override
            public void onFinish(final Bundle bundle, final Exception error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (error != null) {
                            if (error instanceof Errors.ConnectionError) {
                                Log.e("App", "No internet");
                            } else if (error instanceof Errors.ServerError) {
                                Log.e("App", "Server Error. trya again");
                            } else {
                                Log.e("App", error.getMessage());
                            }
                            return;
                        }
                        startPaymentActivity(bundle);
                    }
                });
            }
        });
        request.execute();
    }

    private boolean cardValid(Card card) {
        if (!card.isCardValid()) {

            if (!card.isCardNameValid()) {
                showErrorToast("Card Holders Name is invalid");
            }

            if (!card.isCardNumberValid()) {
                showErrorToast("Card Number is invalid");
            }

            if (!card.isDateValid()) {
                showErrorToast("Expiry date is invalid");
            }

            if (!card.isCVVValid()) {
                showErrorToast("CVV is invalid");
            }

            return false;
        }

        return true;
    }

    private void startPaymentActivity(Bundle bundle) {
        // Start the payment activity
        //Do not change this unless you know what you are doing
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtras(getIntent());
        intent.putExtra(Constants.PAYMENT_BUNDLE, bundle);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R.id.btnMyProfile){
                Intent i = new Intent(mContext, MyProfileScreen.class);
                startActivity(i);

            }else if(id == R.id.btnMyReport){
                Intent i = new Intent(mContext, PaymentReportScreen.class);
                startActivity(i);
            }else if(id == R.id.btnHome){
                finish();
            }else if(id == R.id.btnHelpAndSupport){
                Intent i = new Intent(mContext, HelpAndSupportScreen.class);
                startActivity(i);
            }else if(id == R.id.btnLogOut){
                Utility.openLogOutDialog(CustomUIActivity.this);
            }else if (id == R.id.btnLanguage) {
                Utility.showLanguageDialog(CustomUIActivity.this);

            }
        }
    };
    public PopupWindow popupDisplay()
    {

        final PopupWindow popupWindow = new PopupWindow(this);

        // inflate your layout or dynamically add view
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.layout_menu, null);
        Button btnMyProfile, btnMyReport, btnHome, btnHelpAndSupport, btnLogOut, btnLanguage;
        btnMyProfile = (Button) view.findViewById(R.id.btnMyProfile);
        btnMyReport = (Button) view.findViewById(R.id.btnMyReport);
        btnHome = (Button) view.findViewById(R.id.btnHome);
        btnHelpAndSupport = (Button) view.findViewById(R.id.btnHelpAndSupport);
        btnLogOut = (Button) view.findViewById(R.id.btnLogOut);
        btnLanguage = (Button) view.findViewById(R.id.btnLanguage);

        if(getIntent().getBooleanExtra("payment_result", false)){
            btnHome.setVisibility(View.GONE);
        }else {
            btnMyReport.setVisibility(View.GONE);
        }

        btnMyProfile.setOnClickListener(onClickListener);
        btnMyReport.setOnClickListener(onClickListener);
        btnHome.setOnClickListener(onClickListener);
        btnHelpAndSupport.setOnClickListener(onClickListener);
        btnLogOut.setOnClickListener(onClickListener);
        btnLanguage.setOnClickListener(onClickListener);

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);

        return popupWindow;
    }
}
