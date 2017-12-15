package com.adms.saralpayAgent.AsyncTask;

import android.os.AsyncTask;

import com.adms.saralpayAgent.Utility.AppConfiguration;
import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.Utility.ParseJSON;
import com.adms.saralpayAgent.WebServicesCall.WebServicesCall;

import java.util.ArrayList;
import java.util.HashMap;

public class GetStateDetailAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {
    HashMap<String, String> param = new HashMap<String, String>();
    OnCompletionListner onCompletionListner = null;

    public GetStateDetailAsyncTask(HashMap<String, String> param, OnCompletionListner onCompletionListner) {
        this.param = param;
        this.onCompletionListner = onCompletionListner;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
        protected ArrayList<String> doInBackground(Void... params) {
            String responseString = null;
        ArrayList<String> result = null;
            try {
                responseString = WebServicesCall.RunScript(AppConfiguration.getUrl(AppConfiguration.GetStateDetail), param);
                result = ParseJSON.parseStateJson(responseString);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            if(result.size() > 0){
                onCompletionListner.OnResponseSuccess(result);
            }else {
                onCompletionListner.OnResponseFail(result);
            }
        }
    }