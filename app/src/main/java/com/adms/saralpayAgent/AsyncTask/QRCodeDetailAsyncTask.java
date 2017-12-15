package com.adms.saralpayAgent.AsyncTask;

import android.os.AsyncTask;

import com.adms.saralpayAgent.Interface.OnCompletionListner;
import com.adms.saralpayAgent.Utility.AppConfiguration;
import com.adms.saralpayAgent.Utility.ParseJSON;
import com.adms.saralpayAgent.WebServicesCall.WebServicesCall;

import java.util.HashMap;

/**
 * Created by AndroidPC on 2/16/2017.
 */

public class QRCodeDetailAsyncTask extends AsyncTask<Void, Void, HashMap<String, String>> {
    HashMap<String, String> param = new HashMap<String, String>();
    OnCompletionListner onCompletionListner = null;

    public QRCodeDetailAsyncTask(HashMap<String, String> param, OnCompletionListner onCompletionListner) {
        this.param = param;
        this.onCompletionListner = onCompletionListner;

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
            responseString = WebServicesCall.RunScript(AppConfiguration.getUrl(AppConfiguration.QRCodeDetail), param);
            result = ParseJSON.parseQRCodeDetailJson(responseString);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> result) {
        super.onPostExecute(result);
        if(result.size() > 0){
            onCompletionListner.OnResponseSuccess(result);
        }else {
            onCompletionListner.OnResponseFail(result);
        }
    }
}
