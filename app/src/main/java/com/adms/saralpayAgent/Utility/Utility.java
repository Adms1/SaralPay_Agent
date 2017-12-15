package com.adms.saralpayAgent.Utility;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adms.saralpayAgent.AsyncTask.UpgradeCustomerStatusAsyncTask;
import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.LoginScreen;
import com.adms.saralpayAgent.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Harsh on 04-Aug-16.
 */
public class Utility {
    public static final String MyPREFERENCES = "MyPrefs";
    public static SharedPreferences sharedpreferences;
    private static final int MEGABYTE = 1024 * 1024;
    public static String parentFolderName = "Skool 360 Shilaj";
    public static String childAnnouncementFolderName = "Announcement";
    public static String childCircularFolderName = "Circular";

    public static boolean isNetworkConnected(Context ctxt) {
        ConnectivityManager cm = (ConnectivityManager) ctxt
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public static String getLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getString("Locale", "");
    }

    public static void ping(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void pong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void setPref(Context context, String key, String value) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPref(Context context, String key) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String value = sharedpreferences.getString(key, "");
        return value;
    }

    public static boolean isFileExists(String fileName, String moduleName) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        if (moduleName.equalsIgnoreCase("announcement"))
            return new File(extStorageDirectory, parentFolderName + "/" + childAnnouncementFolderName + "/" + fileName).isFile();
        else
            return new File(extStorageDirectory, parentFolderName + "/" + childCircularFolderName + "/" + fileName).isFile();
    }

    public static File createFile(String fileName, String moduleName) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = null;

        if (moduleName.equalsIgnoreCase("announcement"))
            folder = new File(extStorageDirectory, parentFolderName + "/" + childAnnouncementFolderName);
        else
            folder = new File(extStorageDirectory, parentFolderName + "/" + childCircularFolderName);

        folder.mkdirs();

        File pdfFile = new File(folder, fileName);

        try {
            pdfFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pdfFile;
    }

    public static void downloadFile(String fileUrl, String fileName, String moduleName) {
        try {

            File directoryPath = createFile(fileName, moduleName);

            fileUrl = fileUrl.replace(" ", "%20");
            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(directoryPath);
            int totalSize = urlConnection.getContentLength();

            byte[] buffer = new byte[MEGABYTE];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bufferLength);
            }
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateTransactionID() {
        Calendar calendar = Calendar.getInstance();
        StringBuilder builer = new StringBuilder();
        builer.append("A00");
        builer.append(String.valueOf(calendar.get(Calendar.YEAR)).toString().substring(2, 4));
        builer.append(String.valueOf(calendar.get(Calendar.MONTH) + 1).toString());
        builer.append(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)).toString());
        builer.append(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)).toString());
        builer.append(String.valueOf(calendar.get(Calendar.MINUTE) < 10 ? "0" + calendar.get(Calendar.MINUTE) : calendar.get(Calendar.MINUTE)).toString());
        builer.append(String.valueOf(calendar.get(Calendar.SECOND)).toString());

        return builer.toString();
    }

    public static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(new Date()).replace("am", "AM").replace("pm", "PM");
    }

    public static String converImageToStirng(ImageView iv1) {
        iv1.setDrawingCacheEnabled(true);
        iv1.measure(View.MeasureSpec.makeMeasureSpec(
                iv1.getLayoutParams().width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(
                        iv1.getLayoutParams().height,
                        View.MeasureSpec.EXACTLY));
        /*iv1.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));*/
        iv1.layout(0, 0, iv1.getMeasuredWidth(), iv1.getMeasuredHeight());

        iv1.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(iv1.getDrawingCache());
        iv1.setDrawingCacheEnabled(false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] image = stream.toByteArray();
        System.out.println("byte array:" + image);

//        System.out.println("string:" + Base64.encodeToString(image, 0));
        return Base64.encodeToString(image, 0);
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(false);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static String getIMEI(Context context) {
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;

    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static String getTodaysDate() {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH) + 1;
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        return dd + "/" + mm + "/" + yy;
    }

    /*public static String addNumberOfDays(String date, int daysToADD) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();

        try{
            c.setTime(sdf.parse(date));
        }catch (Exception e){
            e.printStackTrace();
        }

        c.add(Calendar.DATE, daysToADD);  // number of days to add
        String dt = sdf.format(c.getTime());  // dt is now the new date
        return dt;
    }

    public static long daysBetween(Date startDate, Date endDate) {
        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);

        long daysBetween = 0;
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }*/

    public static double[] getLocation(Context context) {
        double[] locationArray = new double[2];
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);

        if (isLocationEnabled(context)) {
            try {
                Location location = locationManager.getLastKnownLocation(provider);
                double latValue = location.getLatitude();
                double longValue = location.getLongitude();
                if (latValue != 0.0 && longValue != 0.0) {
                    locationArray[0] = latValue;
                    locationArray[1] = longValue;
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return locationArray;
    }

    public static String currentLocale, language_code_hindi = "hi", language_code_english = "en";

    public static void showLanguageDialog(final Activity activity) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_choose_language, null);
        dialogBuilder.setView(dialogView);

        Button btnCancel;
        TextView txtChangeLanguage;
        RadioGroup rdgChooseLang;
        btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        rdgChooseLang = (RadioGroup) dialogView.findViewById(R.id.rdgChooseLang);
        txtChangeLanguage = (TextView) dialogView.findViewById(R.id.txtChangeLanguage);


        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        currentLocale = Utility.getLocale(activity);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        rdgChooseLang.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Change locale settings in the app.
                Locale myLocale;
                SharedPreferences prefs = activity.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                if (checkedId == R.id.rdbEnglish) {
                    myLocale = new Locale(language_code_english);
                    editor.putString("Locale", language_code_english);

                } else {
                    myLocale = new Locale(language_code_hindi);
                    editor.putString("Locale", language_code_hindi);
                }
                Locale.setDefault(myLocale);
                Configuration config = new Configuration();
                config.locale = myLocale;
                activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
                editor.commit();
                activity.onConfigurationChanged(config);

            }
        });

        if (currentLocale.equalsIgnoreCase("hi")) {
            rdgChooseLang.check(R.id.rdbHindi);

        } else {
            rdgChooseLang.check(R.id.rdbEnglish);
        }
    }

    public static void openLogOutDialog(final Activity context) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Logout")
                .setIcon(context.getResources().getDrawable(R.mipmap.saralpay_icon))
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with manual Entry
                        AppConfiguration.CustomerDetail.clear();
                        Intent iLogin = new Intent(context, LoginScreen.class);
                        iLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(iLogin);
                        context.finish();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing

                    }
                })
                .show();
    }

    public static void openExecutiveLogOutDialog(final Activity context) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Logout")
                .setIcon(context.getResources().getDrawable(R.mipmap.saralpay_icon))
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with manual Entry
                        AppConfiguration.ExecutiveDetail.clear();
                        Intent iLogin = new Intent(context, LoginScreen.class);
                        iLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(iLogin);
                        context.finish();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing

                    }
                })
                .show();
    }


    public static void openOptOutDialog(final Activity mContext, final ProgressDialog progressDialog) {
        new android.app.AlertDialog.Builder(mContext)
                .setTitle("Opt Out")
                .setIcon(mContext.getResources().getDrawable(R.mipmap.saralpay_icon))
                .setMessage("Are you sure, you want to opt out")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("CustomerID", Utility.getPref(mContext, "CustomerID"));
                            hashMap.put("C_UpgradeStatus", "0");
                            hashMap.put("C_UpgradeDate", Utility.getTodaysDate());
                            UpgradeCustomerStatusAsyncTask upgradeCustomerStatusAsyncTask = new UpgradeCustomerStatusAsyncTask(hashMap, new OnCompletionListner() {
                                @Override
                                public void OnResponseSuccess(Object output) {
//                            Utility.ping(mContext, "Congratulation, You have been upgraded to Premium Membership");
//                            Intent iLogin = new Intent(mContext, LoginScreen.class);
//                            startActivity(iLogin);
                                    Utility.ping(mContext, "You have successfuly Opted Out. You can no longer login");
                                    Utility.setPref(mContext, "membershipStatus", "0");
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void OnResponseFail(Object output) {
                                    progressDialog.dismiss();
                                    Utility.ping(mContext, "Server not responding, Please try again");
                                }
                            });
                            upgradeCustomerStatusAsyncTask.execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();

                    }
                })
                .show();
    }

    public static void openInvalidEmailDialog(final Activity aContext, final EditText editText) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(aContext);
        builder.setCancelable(true);
//                        builder.setTitle("WaterWorks");
//                        builder.setIcon(getResources().getDrawable(R.drawable.alerticon));
        builder.setIcon(aContext.getResources().getDrawable(R.mipmap.saralpay_icon));
        builder.setMessage("Invalid Email Address");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        editText.requestFocus();
                    }
                });

        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }

    public static void openInvalidApiKeyDialog(final Activity context) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Account Alert")
                .setIcon(context.getResources().getDrawable(R.mipmap.saralpay_icon))
                .setMessage("We are currently setting up your account. Please contact customer support at +91 75758 09733 if you have any questions.")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void openVersionDialog(final Activity context) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Saral Pay Update")
                .setIcon(context.getResources().getDrawable(R.mipmap.saralpay_icon))
                .setMessage("Please update to a new version of the app.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adms.saralpay"));
                        context.startActivity(i);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        Utility.pong(context, "You wont be able to login without updating to a newer version");
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void openVersionDialogCharge(final Activity context) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Saral Pay Update")
                .setIcon(context.getResources().getDrawable(R.mipmap.saralpay_icon))
                .setMessage("Please update to a new version of the app.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adms.saralpay"));
                        context.startActivity(i);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AppConfiguration.CustomerDetail.clear();
                        Intent iLogin = new Intent(context, LoginScreen.class);
                        iLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(iLogin);
                        context.finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
