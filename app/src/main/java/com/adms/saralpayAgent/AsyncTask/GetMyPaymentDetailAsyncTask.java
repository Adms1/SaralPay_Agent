package com.adms.saralpayAgent.AsyncTask;

import android.os.AsyncTask;

import com.adms.saralpayAgent.Utility.AppConfiguration;
import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.Utility.ParseJSON;
import com.adms.saralpayAgent.WebServicesCall.WebServicesCall;

import java.util.ArrayList;
import java.util.HashMap;

public class GetMyPaymentDetailAsyncTask extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
    HashMap<String, String> param = new HashMap<String, String>();
    OnCompletionListner onCompletionListner = null;

    public GetMyPaymentDetailAsyncTask(HashMap<String, String> param, OnCompletionListner onCompletionListner) {
        this.param = param;
        this.onCompletionListner = onCompletionListner;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
            String responseString = null;
        ArrayList<HashMap<String, String>> result = null;
            try {
                responseString = WebServicesCall.RunScript(AppConfiguration.getUrl(AppConfiguration.GetMyPaymentDetail), param);
                result = ParseJSON.parseGetMyPaymentDetailJson(responseString);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            super.onPostExecute(result);
            if(result.size() > 0){
                onCompletionListner.OnResponseSuccess(result);
            }else {
                onCompletionListner.OnResponseFail(result);
            }
        }
    }