package com.adms.saralpayAgent;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adms.saralpayAgent.AsyncTask.GetMyPaymentDetailAsyncTask;
import com.adms.saralpayAgent.Utility.AppConfiguration;
import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.Utility.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.adms.saralpayAgent.R.id.txtDtDep;

public class PaymentReportScreen extends AppCompatActivity {

    private Context mContext = this;
    private ListView listPaymentReport;
    private RelativeLayout rlTopBar;
    private TextView txtStartDate, txtEndDate, txtNoResults;
    private ImageView imgEndDate, imgStartDate;
    private ImageButton btnLogOut, btnMenu;
    private boolean isStartDate = false;
    DatePicker sdateInput, edateInput;
    String startdate, enddate;
    CustomAdapter customAdapter;
    Calendar c, end;
    String currentDateTimeString;
    private Button btnShow;
    private HashMap<String, String> hashMapResult = new HashMap<String, String>();
    private GetMyPaymentDetailAsyncTask getMyPaymentDetailAsyncTask = null;
    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_report_screen);

        initViews();
        setListners();
    }

    private void initViews() {
        rlTopBar = (RelativeLayout) findViewById(R.id.rlTopBar);
        listPaymentReport = (ListView) findViewById(R.id.listPaymentReport);
        txtStartDate = (TextView) findViewById(R.id.txtStartDate);
        txtEndDate = (TextView) findViewById(R.id.txtEndDate);
        txtNoResults = (TextView) findViewById(R.id.txtNoResults);
        btnShow = (Button) findViewById(R.id.btnShow);
        imgStartDate = (ImageView) findViewById(R.id.imgStartDate);
        imgEndDate = (ImageView) findViewById(R.id.imgEndDate);
        btnLogOut = (ImageButton) findViewById(R.id.btnLogOut);
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
//        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//        String currentDateTimeString = df.format(new Date());
//        txtStartDate.setText("15/12/2016");
//        txtEndDate.setText(currentDateTimeString);
//        btnShow.setOnClickListener(null);

    }

    private void setListners() {
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
                Utility.openLogOutDialog(PaymentReportScreen.this);
            }
        });

        imgStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDate = true;
                showCalendarDialog();
            }
        });

        imgEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDate = false;
                showCalendarDialog();
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showData();
//                Calendar endDate = (Calendar) c.clone();
//
//                c.set(sdateInput.getYear(), sdateInput.getMonth(), sdateInput.getDayOfMonth(), 0, 0, 0);
//                endDate.set(edateInput.getYear(), edateInput.getMonth(), edateInput.getDayOfMonth(), 0, 0, 0);
//
//                if (c.after(endDate))
//                {
//                    new AlertDialog.Builder(mContext)
//                            .setTitle("Confirmation")
//                            .setMessage("Please Enter Valid End Date.")
//                            .setNeutralButton("Back", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    // do nothing - it will just close when clicked
//                                }
//                            }).show();
//                }
//                else{
//
//                }


            }
        });

        txtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDate = true;
                startdate=txtStartDate.getText().toString();
                Utility.setPref(mContext, "startdate", startdate);
                showCalendarDialog();
            }
        });

        txtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isStartDate = false;
                Utility.setPref(mContext, "enddate", txtEndDate.getText().toString());
                showCalendarDialog();
            }
        });
    }


    public void showData() {

        if (txtStartDate.getText().toString().equalsIgnoreCase("start date")) {
            Utility.ping(mContext, "Please enter a start date");

        } else if (txtEndDate.getText().toString().equalsIgnoreCase("end date")) {
            Utility.ping(mContext, "Please enter an end date");

        } else {

            progressDialog = new ProgressDialog(PaymentReportScreen.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("CustomerID", AppConfiguration.CustomerDetail.get("CustomerID"));
            hashMap.put("FromDate", txtStartDate.getText().toString());
            hashMap.put("ToDate", txtEndDate.getText().toString());

            getMyPaymentDetailAsyncTask = new GetMyPaymentDetailAsyncTask(hashMap, new OnCompletionListner() {
                @Override
                public void OnResponseSuccess(Object output) {
                    ArrayList<HashMap<String, String>> listData = (ArrayList<HashMap<String, String>>) output;

                    listPaymentReport.setVisibility(View.VISIBLE);
                    txtNoResults.setVisibility(View.GONE);

                    CustomAdapter customAdapter = new CustomAdapter(listData);
                    listPaymentReport.setAdapter(customAdapter);

                    progressDialog.dismiss();
                }

                @Override
                public void OnResponseFail(Object output) {
                    listPaymentReport.setVisibility(View.GONE);
                    txtNoResults.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            });
            getMyPaymentDetailAsyncTask.execute();
        }
    }

    class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            // getCalender();
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;

            if (isStartDate) {

                txtStartDate.setText(new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mDay).append("/").append(mMonth + 1).append("/")
                        .append(mYear).append(" "));
            } else {
                txtEndDate.setText(new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mDay).append("/").append(mMonth + 1).append("/")
                        .append(mYear).append(" "));
            }
        }
    }

    public void showCalendarDialog() {
       /* Calendar*/
        c = Calendar.getInstance();
        end = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        System.out.println("the selected " + mDay);
        DatePickerDialog dialog = new DatePickerDialog(PaymentReportScreen.this,
                new mDateSetListener(), mYear, mMonth, mDay);
        dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        c.add(Calendar.DATE, -6);

        dialog.show();

    }

    public class CustomAdapter extends ArrayAdapter {
        ArrayList<HashMap<String, String>> listData = new ArrayList<>();

        // View lookup cache
        private class ViewHolder {
            TextView txtDate, txtStatus, txtAmount, txtCharge, txtTaxAmount, txtFinalAmount, txtDtDep;
            LinearLayout llHeader;
        }

        public CustomAdapter(ArrayList<HashMap<String, String>> data) {
            super(mContext, R.layout.layout_payment_report_row, data);
            this.listData = data;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.layout_payment_report_row, parent, false);
                viewHolder.llHeader = (LinearLayout) convertView.findViewById(R.id.llHeader);
                viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
                viewHolder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
                viewHolder.txtAmount = (TextView) convertView.findViewById(R.id.txtAmount);
                viewHolder.txtCharge = (TextView) convertView.findViewById(R.id.txtCharge);
                viewHolder.txtTaxAmount = (TextView) convertView.findViewById(R.id.txtTaxAmount);
                viewHolder.txtFinalAmount = (TextView) convertView.findViewById(R.id.txtFinalAmount);
                viewHolder.txtDtDep = (TextView) convertView.findViewById(txtDtDep);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position == 0) {
                viewHolder.llHeader.setVisibility(View.VISIBLE);
            } else {
                viewHolder.llHeader.setVisibility(View.GONE);
            }
            try {
                viewHolder.txtDate.setText(listData.get(position).get("PaymentDate"));
                viewHolder.txtStatus.setText(listData.get(position).get("PaymentStatus"));
                viewHolder.txtAmount.setText(listData.get(position).get("Trans_Amount"));
//                viewHolder.txtCharge.setText(listData.get(position).get("Trans_Charge"));
//                viewHolder.txtTaxAmount.setText(listData.get(position).get("Trans_STaxAmount"));
                viewHolder.txtFinalAmount.setText(listData.get(position).get("Trans_FinalAmount"));
                viewHolder.txtDtDep.setText(listData.get(position).get("Trans_PaymentID"));

            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }
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
                finish();
            } else if (id == R.id.btnHelpAndSupport) {
                Intent i = new Intent(mContext, HelpAndSupportScreen.class);
                startActivity(i);
                finish();
            } else if (id == R.id.btnLogOut) {
                Utility.openLogOutDialog(PaymentReportScreen.this);
            } else if (id == R.id.btnLanguage) {
                Utility.showLanguageDialog(PaymentReportScreen.this);
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


        btnMyReport.setVisibility(View.GONE);
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

    @Override
    protected void onResume() {
        super.onResume();
//        progressDialog = new ProgressDialog(PaymentReportScreen.this);
//        progressDialog.setMessage("Please wait...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("CustomerID", AppConfiguration.CustomerDetail.get("CustomerID"));
//        hashMap.put("FromDate", txtStartDate.getText().toString());
//        hashMap.put("ToDate", txtEndDate.getText().toString());
//
//        getMyPaymentDetailAsyncTask = new GetMyPaymentDetailAsyncTask(hashMap, new OnCompletionListner() {
//            @Override
//            public void OnResponseSuccess(Object output) {
//                ArrayList<HashMap<String, String>> listData = (ArrayList<HashMap<String, String>>) output;
//
//                listPaymentReport.setVisibility(View.VISIBLE);
//                txtNoResults.setVisibility(View.GONE);
//
//                CustomAdapter customAdapter = new CustomAdapter(listData);
//                listPaymentReport.setAdapter(customAdapter);
//
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void OnResponseFail(Object output) {
//                listPaymentReport.setVisibility(View.GONE);
//                txtNoResults.setVisibility(View.VISIBLE);
//                progressDialog.dismiss();
//            }
//        });
//        getMyPaymentDetailAsyncTask.execute();
//    }
//        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//        String currentDateTimeString = df.format(new Date());
//        txtStartDate.setText("15/12/2016");
//        txtEndDate.setText(currentDateTimeString);
//        btnShow.setOnClickListener(null);
//        showData();
    }


}
