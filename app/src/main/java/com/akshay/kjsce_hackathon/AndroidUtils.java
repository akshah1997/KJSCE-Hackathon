package com.akshay.kjsce_hackathon;

import android.os.AsyncTask;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;


import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by G3n on 06-10-2017.
 */


public class AndroidUtils {

    public static void wolfReq(String s) throws Exception {
        String appid = "JHG2AQ-KEUPARWGKH";
        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory requestFactory =
                httpTransport.createRequestFactory();
        GenericUrl url1 = new
                GenericUrl("http://api.wolframalpha.com/v2/query");
        url1.put("input", s);
        url1.put("output", "JSON");
        url1.put("appid", appid);
        final HttpRequest request = requestFactory.buildGetRequest(url1);
        String resultPassed = "";
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                HttpResponse httpResponse = null;

                try {
                    httpResponse = request.execute();
                    InputStream inputStream = httpResponse.getContent();
                    int ch;
                    StringBuilder result = new StringBuilder();

                    while ((ch = inputStream.read()) != -1) {
                        result.append((char) ch);
                    }

                    httpResponse.disconnect();
                    JSONObject data = new JSONObject(result.toString());
                    result = new StringBuilder(data.getJSONObject("queryresult").getJSONArray("pods").getJSONObject(1).getJSONArray("subpods").getJSONObject(0).getString("plaintext"));
                    Log.d("result", result.toString());
                    return result.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }
}
