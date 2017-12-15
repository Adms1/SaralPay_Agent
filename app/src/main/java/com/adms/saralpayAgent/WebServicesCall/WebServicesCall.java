package com.adms.saralpayAgent.WebServicesCall; /**
 * 
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author Harsh Adms
 *
 */
public class WebServicesCall {

	public static String RunScript(String URL,HashMap<String, String> params){
		URL url = null;
		String response = "";

		try {
			url = new URL(URL);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		System.out.println("request URL : "+url);
		System.out.println("request Params : "+params);

		HttpURLConnection conn = null;
		final String USER_AGENT = "Mozilla/5.0";

		try {
			conn = (HttpURLConnection)url.openConnection();
			conn.setReadTimeout(100000);
			conn.setConnectTimeout(150000);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Content-Type", 
					"application/x-www-form-urlencoded");

			conn.setDoInput(true);
			conn.setDoOutput(true);

			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));
			writer.write(getPostDataString(params));
			writer.flush();
			writer.close();
			writer=null;
			os.close();
			os=null;

			int responseCode=conn.getResponseCode();
			System.out.println("Response Code : "+responseCode);
			System.out.println("Response Code : "+conn.getErrorStream());

			if (responseCode == HttpsURLConnection.HTTP_OK) {
				String line;
				BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line=br.readLine()) != null) {
					response+=line;
				}
			}
			else {
				response="";
			}
			
			System.out.println("Response :" +response);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return response;
	}

	private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for(Map.Entry<String, String> entry : params.entrySet()){
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}

		return result.toString();
	}

}
