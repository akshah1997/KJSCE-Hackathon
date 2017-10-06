package com.akshay.kjsce_hackathon;

import android.util.Log;
import android.widget.Toast;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;


import java.io.IOException;
import java.io.InputStream;

/**
 * Created by G3n on 06-10-2017.
 */

public class AndroidUtils {
    public static void main(String args[])
    {
        wolfReq();
    }
    public static void wolfReq()
    {
        String appid="JHG2AQ-KEUPARWGKH";
        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory requestFactory =
                httpTransport.createRequestFactory();
        GenericUrl url1 = new
                GenericUrl("http://api.wolframalpha.com/v2/query");
    url1.put("input","Who is the president of America");
        url1.put("appid",appid);
        HttpRequest request = null;
        try {
            request = requestFactory.buildGetRequest(url1);
            HttpResponse httpResponse = request.execute();
            InputStream inputStream = httpResponse.getContent();
            int ch;
            String result = "";
            while ((ch = inputStream.read()) != -1) {
                result+=(char)ch;
            }
            httpResponse.disconnect();
            Log.d("abc",result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
