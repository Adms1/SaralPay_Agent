package com.adms.saralpayAgent.AsyncTask;

import android.os.AsyncTask;

import com.adms.saralpayAgent.Utility.AppConfiguration;
import com.adms.saralpayAgent.Utility.ParseJSON;
import com.adms.saralpayAgent.WebServicesCall.WebServicesCall;

import java.util.HashMap;

public class GetPaymentResponseAsyncTask extends AsyncTask<Void, Void, HashMap<String, String>> {
    HashMap<String, String> param = new HashMap<String, String>();

    public GetPaymentResponseAsyncTask(HashMap<String, String> param) {
        this.param = param;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
        protected HashMap<String, String> doInBackground(Void... params) {
            String responseString = null;
            HashMap<String, String> result = null;
            try {
                responseString = WebServicesCall.RunScript(AppConfiguration.getUrl(AppConfiguration.GetPaymentResponse), param);
                result = ParseJSON.parsePaymentResponseJson(responseString);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            super.onPostExecute(result);
        }
    }