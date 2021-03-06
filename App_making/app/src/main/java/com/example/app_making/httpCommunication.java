package com.example.app_making;

import android.content.Context;
import android.util.Log;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class httpCommunication {



    public String GET(String urlStr){

        final String[] str = {new String()};
        new Thread(new Runnable() {
            @Override
            public void run() {
                str[0] = request(urlStr);
            }
        }).start();

        return str[0];
    }

    public String request(String urlStr) {
        StringBuilder output = new StringBuilder();
        try {
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                int resCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                while (true) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }

                    output.append(line + "\n");
                }
                reader.close();
                conn.disconnect();
            }
        } catch (Exception ex) {
            Log.d("??????, 95", "????????????");
        }

        Log.d("?????? -> " , output.toString());

        return output.toString();
    }

    public String request(String urlStr, String id) {
        StringBuilder output = new StringBuilder();
        try {
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");

                conn.setDoInput(true);

                int resCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                while (true) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }

                    output.append(line + "\n");
                }
                reader.close();
                conn.disconnect();
            }
        } catch (Exception ex) {
            Log.d("??????, 95", "????????????");
        }

        Log.d("?????? -> " , output.toString());

        return output.toString();
    }


    public void POST_BODY_JSON(final String url, Context context){
        //TODO ????????? Request ?????? ??????
        RequestQueue queue = Volley.newRequestQueue(context);

        //TODO ??????????????? ?????? ??????
        JSONObject jsonBodyObj = new JSONObject();
        try{
            //" { \"age\" : 24, \"coin\" : 3, \"name\" : \"jisoo\"} ");
            jsonBodyObj.put("age",24);
            jsonBodyObj.put("coin",3);
            jsonBodyObj.put("name","jisoo");

        }catch (JSONException e){
            e.printStackTrace();
        }
        final String requestBody = String.valueOf(jsonBodyObj.toString());

        Log.d("---","---");
        Log.w("//===========//","================================================");
        Log.d("","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() ????????? : Volley POST_BODY_JSON ?????? ??????]");
        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(url)+"]");
        Log.d("","\n"+"["+"?????? ??? - "+String.valueOf(jsonBodyObj.toString())+"]");
        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf("application/json")+"]");
        Log.w("//===========//","================================================");
        Log.d("---","---");

        //TODO ????????? Response ?????? ??????
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                //TODO ????????? ?????? ?????? ??????
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("---","---");
                        Log.w("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() ????????? : Volley POST_BODY_JSON ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(response.toString())+"]");
                        Log.w("//===========//","================================================");
                        Log.d("---","---");
                    }
                },
                //TODO ????????? ?????? ?????? ?????? ??????
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("---","---");
                        Log.e("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() ????????? : Volley POST_BODY_JSON ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(error.toString())+"]");
                        Log.e("//===========//","================================================");
                        Log.d("---","---");
                    }
                })
                //TODO ????????? ?????? ?????? ??? Body ????????? ????????? ?????? ??????
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
            @Override
            public byte[] getBody() {
                try {
                    if (requestBody != null && requestBody.length()>0 && !requestBody.equals("")){
                        return requestBody.getBytes("utf-8");
                    }
                    else {
                        return null;
                    }
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };

        request.setShouldCache(false);
        queue.add(request);
    }


}
