package com.adms.saralpayAgent.AsyncTask;

import android.os.AsyncTask;

import com.adms.saralpayAgent.Utility.AppConfiguration;
import com.adms.saralpayAgent.Utility.ParseJSON;
import com.adms.saralpayAgent.WebServicesCall.WebServicesCall;

import java.util.HashMap;

public class CreateCustomer_PlanAsyncTask extends AsyncTask<Void, Void, Boolean> {
    HashMap<String, String> param = new HashMap<String, String>();

    public CreateCustomer_PlanAsyncTask(HashMap<String, String> param) {
        this.param = param;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String responseString = null;
        boolean result = false;
        try {
            responseString = WebServicesCall.RunScript(AppConfiguration.getUrl(AppConfiguration.CreateCustomer_Plan), param);
            result = ParseJSON.parseCreateCustomerPlanJson(responseString);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return result;
    }


    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }
}