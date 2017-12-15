package com.adms.saralpayAgent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.adms.saralpayAgent.AsyncTask.CreateCustomer_PlanAsyncTask;
import com.adms.saralpayAgent.AsyncTask.GeneratePaymentRequestAsyncTask;
import com.adms.saralpayAgent.AsyncTask.GetPlanListAsyncTask;
import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.Utility.AppConfiguration;
import com.adms.saralpayAgent.Utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.GONE;

public class PlansScreen extends AppCompatActivity {

    private Context mContext = this;
    private Button btnReedem, btnFree, btnGold, btnSilver, btnBronze, btnRplus;
    private ImageButton btnLogOut, btnMenu;
    private ProgressDialog progressDialog = null;
    private RelativeLayout rlTopBar;
    private LinearLayout linearBronze, linearSilver, linearGold, linearFree, linearRplus;
    private ArrayList<HashMap<String, String>> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plannewscreen);


        initViews();
        setListners();
    }

    private void initViews() {

        btnReedem = (Button) findViewById(R.id.btnReedem);
        btnFree = (Button) findViewById(R.id.btnFree);
        btnGold = (Button) findViewById(R.id.btnGold);
        btnSilver = (Button) findViewById(R.id.btnSilver);
        btnBronze = (Button) findViewById(R.id.btnBronze);
        btnRplus = (Button) findViewById(R.id.btnRplus);
        rlTopBar = (RelativeLayout) findViewById(R.id.rlTopBar);
        btnLogOut = (ImageButton) findViewById(R.id.btnLogOut);
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        linearBronze = (LinearLayout) findViewById(R.id.linearBronze);
        linearSilver = (LinearLayout) findViewById(R.id.linearSilver);
        linearGold = (LinearLayout) findViewById(R.id.linearGold);
        linearFree = (LinearLayout) findViewById(R.id.linearFree);
        linearRplus = (LinearLayout) findViewById(R.id.linearRplus);

        checkcondition();
        showProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        GetPlanListAsyncTask getPlanListAsyncTask = new GetPlanListAsyncTask(hashMap, new OnCompletionListner() {
            @Override
            public void OnResponseSuccess(Object output) {
                list = (ArrayList<HashMap<String, String>>) output;
                progressDialog.dismiss();

            }

            @Override
            public void OnResponseFail(Object output) {
                progressDialog.dismiss();
            }
        });
        getPlanListAsyncTask.execute();
    }

    public void checkcondition() {
        if (AppConfiguration.planstatus.equalsIgnoreCase("0")) {
            btnMenu.setVisibility(GONE);
        } else {
            switch (AppConfiguration.planstatus) {
                case "2":
                    linearFree.setVisibility(GONE);
                    break;

                case "3":
                    linearFree.setVisibility(GONE);
                    linearBronze.setVisibility(GONE);
                    break;
                case "4":
                    linearFree.setVisibility(GONE);
                    linearBronze.setVisibility(GONE);
                    linearSilver.setVisibility(GONE);
                    break;
                case "5":
                    linearBronze.setVisibility(GONE);
                    linearSilver.setVisibility(GONE);
                    linearFree.setVisibility(GONE);
                    linearGold.setVisibility(GONE);
                    break;
                case "6":
                    linearFree.setVisibility(GONE);
                    linearRplus.setVisibility(GONE);

                default:

            }
        }
    }

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void setListners() {

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.openLogOutDialog(PlansScreen.this);
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow popupwindow_obj = popupDisplay();
                popupwindow_obj.showAsDropDown(rlTopBar, 0, 0);

            }
        });

        btnFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                builder.setCancelable(true);
//                        builder.setTitle("WaterWorks");
//                        builder.setIcon(getResources().getDrawable(R.drawable.alerticon));
                builder.setIcon(mContext.getResources().getDrawable(R.mipmap.saralpay_icon));
                builder.setTitle("Alert");
                builder.setMessage("Are you sure you want to purchase this plan?");
                builder.setInverseBackgroundForced(true);
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                try {
                                    AppConfiguration.planid = "2";
                                    createcustomerplan();
                                    Intent i = new Intent(mContext, HomeScreen.class);
                                    startActivity(i);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        });
//        btnBronze.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
//                builder.setCancelable(true);
//                builder.setIcon(mContext.getResources().getDrawable(R.mipmap.saralpay_icon));
//                builder.setTitle("Alert");
//                builder.setMessage("are you sure you want to purchase this plan?");
//                builder.setInverseBackgroundForced(true);
//                builder.setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                try {
//                                    generateTrackNPayRequest(list.get(2).get("PlanAmount"), list.get(2).get("Pk_Plan_ID"), list.get(2).get("PlanName"));
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//
//                    }
//                });
//
//                android.app.AlertDialog alert = builder.create();
//
//
//                alert.show();
//
//            }
//        });
//
//        btnSilver.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                {
//                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
//                    builder.setCancelable(true);
////                        builder.setTitle("WaterWorks");
////                        builder.setIcon(getResources().getDrawable(R.drawable.alerticon));
//                    builder.setIcon(mContext.getResources().getDrawable(R.mipmap.saralpay_icon));
//                    builder.setTitle("Alert");
//                    builder.setMessage("are you sure you want to purchase this plan?");
//                    builder.setInverseBackgroundForced(true);
//                    builder.setPositiveButton("OK",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    try {
//                                        generateTrackNPayRequest(list.get(3).get("PlanAmount"), list.get(3).get("Pk_Plan_ID"), list.get(3).get("PlanName"));
//
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//
//                                }
//                            });
//                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // do nothing
//
//                        }
//                    });
//
//                    android.app.AlertDialog alert = builder.create();
//
//
//                    alert.show();
//                }
//
//            }
//        });
//
//        btnGold.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
//                builder.setCancelable(true);
////                        builder.setTitle("WaterWorks");
////                        builder.setIcon(getResources().getDrawable(R.drawable.alerticon));
//                builder.setIcon(mContext.getResources().getDrawable(R.mipmap.saralpay_icon));
//                builder.setTitle("Alert");
//                builder.setMessage("are you sure you want to purchase this plan?");
//                builder.setInverseBackgroundForced(true);
//                builder.setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                try {
//                                    generateTrackNPayRequest(list.get(4).get("PlanAmount"), list.get(4).get("Pk_Plan_ID"), list.get(4).get("PlanName"));
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//
//                    }
//                });
//
//                android.app.AlertDialog alert = builder.create();
//                alert.show();
//
//
//            }
//        });

        btnRplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                builder.setCancelable(true);
//                        builder.setTitle("WaterWorks");
//                        builder.setIcon(getResources().getDrawable(R.drawable.alerticon));
                builder.setIcon(mContext.getResources().getDrawable(R.mipmap.saralpay_icon));
                builder.setTitle("Alert");
                builder.setMessage("Are you sure you want to purchase this plan?");
                builder.setInverseBackgroundForced(true);
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                try {
                                    generateTrackNPayRequest(list.get(1).get("PlanAmount"), list.get(1).get("Pk_Plan_ID"), list.get(1).get("PlanName"));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing

                    }
                });

                android.app.AlertDialog alert = builder.create();


                alert.show();
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
                finish();

            } else if (id == R.id.btnPlans) {
                Intent i = new Intent(mContext, PlansScreen.class);
                startActivity(i);
            } else if (id == R.id.btnHelpAndSupport) {
                Intent i = new Intent(mContext, HelpAndSupportScreen.class);
                startActivity(i);
                finish();
            } else if (id == R.id.btnLogOut) {
                Utility.openLogOutDialog(PlansScreen.this);
            } else if (id == R.id.btnLanguage) {
                Utility.showLanguageDialog(PlansScreen.this);

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
        Button btnMyProfile, btnMyReport, btnHome, btnHelpAndSupport, btnLogOut, btnLanguage, btnPlan;
        btnMyProfile = (Button) view.findViewById(R.id.btnMyProfile);
        btnMyReport = (Button) view.findViewById(R.id.btnMyReport);
        btnHome = (Button) view.findViewById(R.id.btnHome);
        btnHelpAndSupport = (Button) view.findViewById(R.id.btnHelpAndSupport);
        btnLogOut = (Button) view.findViewById(R.id.btnLogOut);
        btnLanguage = (Button) view.findViewById(R.id.btnLanguage);
        btnPlan = (Button) view.findViewById(R.id.btnPlans);

        if (getIntent().getBooleanExtra("payment_result", false)) {
            btnHome.setVisibility(GONE);
        } else {
//            btnMyReport.setVisibility(GONE);
        }
        btnPlan.setVisibility(GONE);
        btnPlan.setOnClickListener(onClickListener);
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
            AppConfiguration.planstatus = "2";//free plan id
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String transactionID;

    public void generateTrackNPayRequest(final String amount, final String id, final String planName) {
        showProgressDialog();
        AppConfiguration.planid = id;
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("CustomerID", "48");/*AppConfiguration.CustomerDetail.get("CustomerID")*/
        hashMap.put("Name", "admsplan");/*/AppConfiguration.CustomerDetail.get("CustomerName")*/
        hashMap.put("Email", "saralpayonline@gmail.com");//AppConfiguration.CustomerDetail.get("CustomerEmail")
        hashMap.put("Mobile", "9016708106");/*AppConfiguration.CustomerDetail.get("CustomerMobile")*/
        hashMap.put("Amount", amount);
        hashMap.put("Description", "plan");
        hashMap.put("IMEINumber", "");
        hashMap.put("Latitude", "");
        hashMap.put("Longitude", "");
        hashMap.put("Trans_Type", "2"); /* manually */


        try {
            GeneratePaymentRequestAsyncTask generatePaymentRequestAsyncTask = new GeneratePaymentRequestAsyncTask(hashMap, new OnCompletionListner() {
                @Override
                public void OnResponseSuccess(Object output) {
                    progressDialog.dismiss();
                    HashMap<String, String> hashMapResult = (HashMap<String, String>) output;
                    if (hashMapResult.size() > 0) {
                        transactionID = hashMapResult.get("TransactionID");
                    }
                    if (transactionID != null) {
                        Intent intent = new Intent(PlansScreen.this, TraknpayRequestPlansActivity.class);
                        intent.putExtra("order_id", transactionID);
                        intent.putExtra("amount", amount);
                        intent.putExtra("mode", "LIVE");
                        intent.putExtra("planid", id);
                        intent.putExtra("description", planName);
                        startActivity(intent);
                    } else {
                        Utility.ping(mContext, "Transaction ID not found. Please try again");
                    }

                }

                @Override
                public void OnResponseFail(Object output) {
                    progressDialog.dismiss();
                    Utility.ping(mContext, "Transaction ID not found. Please try again");
                }
            });
            generatePaymentRequestAsyncTask.execute();

        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
