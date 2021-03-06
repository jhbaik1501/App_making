package com.example.app_making.Time;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.app_making.MainActivity;
import com.example.app_making.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TimeActivity extends FragmentActivity {

    Button button;
//    TextView editText;
    RecyclerView recyclerView;
    TimeAdapter adapter;
    Button group1;
    Button group_rank_btn;





    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        button = findViewById(R.id.Save_button);
        recyclerView = findViewById(R.id.RecyclerView);

        group_rank_btn = findViewById(R.id.group_rank);
        group1 = findViewById(R.id.group1);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        adapter = new TimeAdapter();
        recyclerView.setAdapter(adapter);

        String filename = "id.txt";
        Long temp = (long) 0;
        final Long id;
        FileInputStream infs;
        try {
            infs= openFileInput(filename);
            byte txt[]=new byte[500];
            infs.read(txt);
            infs.close();
            String rep = (new String(txt)).trim() ;
            temp = Long.valueOf(Integer.parseInt( rep ));
            Log.d("* : ", rep);
            GET("http://10.0.2.2:8080/api/TimeList");
        } catch (FileNotFoundException e) {
            Log.d("?????? -> ", "id.txt??? ??????");
        } catch (IOException e) {
            Log.d("?????? -> ", "id.txt??? ????????? ??????");
            e.printStackTrace();
        }
        id = temp;
        Log.d("ID -> ", id + ", "  + temp);


        GET_GROUP("http://10.0.2.2:8080/api/UserGroup", id );

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "?????????????????????.", Toast.LENGTH_SHORT).show();
            }
        });


        group1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                int groupid = Integer.parseInt(group1.getText().toString().replaceAll("[^0-9]", ""));

                AlertDialog.Builder builder = new AlertDialog.Builder(TimeActivity.this);
                builder.setTitle("????????? ?????? ?????? ?????????");
                builder.setPositiveButton("?????????", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LinearLayout dialogView;
                        dialogView = (LinearLayout) View.inflate(TimeActivity.this, R.layout.dialog, null);
                        AlertDialog.Builder dlg = new AlertDialog.Builder(TimeActivity.this);
                        dlg.setView(dialogView);
                        dlg.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText1 = dialogView.findViewById(R.id.editText1);
                                EditText editText2 = dialogView.findViewById(R.id.editText2);

                                String name = editText1.getText().toString();
                                String password = editText2.getText().toString();

                                if(name.isEmpty() || password.isEmpty() ) Toast.makeText(getApplicationContext(),"?????? ?????????????????????.", Toast.LENGTH_LONG).show();
                                else {
                                    POST_BODY_JSON_CREATE("http://10.0.2.2:8080/api/createGroup", getApplicationContext(), name, password, Integer.valueOf(id.toString()) );
                                }
                            }
                        });
                        dlg.setNegativeButton("??????", null);
                        dlg.show();

                    }
                });
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LinearLayout dialogView;
                        dialogView = (LinearLayout) View.inflate(TimeActivity.this, R.layout.dialog, null);
                        AlertDialog.Builder dlg = new AlertDialog.Builder(TimeActivity.this);
                        dlg.setView(dialogView);
                        dlg.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText1 = dialogView.findViewById(R.id.editText1);
                                EditText editText2 = dialogView.findViewById(R.id.editText2);

                                String group_id = editText1.getText().toString();
                                String password = editText2.getText().toString();

                                group_id = group_id.replaceAll("[^0-9]", "");
                                if( group_id.isEmpty() || password.isEmpty() ) Toast.makeText(getApplicationContext(),"?????? ?????????????????????.", Toast.LENGTH_LONG).show();

                                else {
                                    POST_BODY_JSON_JOIN("http://10.0.2.2:8080/api/joinGroup", getApplicationContext(), Integer.parseInt(group_id), password, Integer.valueOf(id.toString()) );
                                }

                            }
                        });
                        dlg.setNegativeButton("??????", null);
                        dlg.show();

                    }

                });

//                AlertDialog alertDialog = builder.create();
                builder.show();

//                if(groupid != 0 ) {
//                    GET_GROUP_TIME("http://10.0.2.2:8080/api/UserGroupTime", groupid);
//                }
//                else{
//
//                }

            }
        });





        group_rank_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupId=group1.getText().toString().replaceAll("[^0-9]", "");
                if(groupId.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "?????? ???????????? ???????????? !!!", Toast.LENGTH_SHORT).show();
                }
                else{

                    adapter.items.clear();
                    adapter.notifyDataSetChanged();
                    GET_GROUPTIME("http://10.0.2.2:8080/api/UserGroupTime", Integer.parseInt(groupId));

                }
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.items.clear();
                adapter.notifyDataSetChanged();
                GET("http://10.0.2.2:8080/api/TimeList");
            }
        });
    }

    public void AdapterAdd(String name, String time, int id){
        Log.d(" adapter add -> ", name + " " + time + " " + id );
        adapter.addItem(new Time(name, time, id));
    }



    @Override
    protected void onUserLeaveHint() { //??? ??? ?????? ????????? ???
        super.onUserLeaveHint();
        restart(this);
    }

    private void restart(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
        Log.d("RSTART", "============\n\nonKeyDown: HOME \n\n=================");
    }

    public void GET_GROUPTIME(final String url, int groupId){
        // ?????? id??? ??????????????? ?????? ?????? , ????????? ????????? id ?????? ???????????????.
        //TODO ????????? Request ?????? ??????
        RequestQueue queue = Volley.newRequestQueue(this);

        //TODO ??????????????? ?????? ??????
        Map<String, String> params = new HashMap<>(); //TODO {userId=1} ??????

        params.put("id", String.valueOf(groupId));
//        params.put("userId", String.valueOf(id));

        //TODO ?????? Url ??? Data ?????? ??????
        String dataParse = "";
        String getUrl = "";
        dataParse = String.valueOf(params.toString());
        dataParse = dataParse.replaceAll("[{]","");
        dataParse = dataParse.replaceAll("[}]","");
        dataParse = dataParse.replaceAll("[,]","&");
        getUrl = url + "?" + dataParse;
        getUrl = getUrl.replaceAll(" ","");
        Log.d("---","---");
        Log.w("//===========//","================================================");
        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(url)+"]");
        Log.d("","\n"+"["+"?????? ??? - "+String.valueOf(params)+"]");
        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(getUrl)+"]");
        Log.w("//===========//","================================================");
        Log.d("---","---");

        //TODO ????????? Response ?????? ??????


        final String requestBody = "";

        StringRequest request = new StringRequest(Request.Method.GET, getUrl,
                //TODO ????????? ?????? ?????? ??????
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("---","---");
                        Log.w("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(response.toString())+"]");
                        Log.w("//===========//","================================================");
                        Log.d("---","---");


                        recyclerView.setAdapter(adapter);
//                        editText.setText(response);

                        String[] split = response.split(",");

                        for(int i=0; i<split.length; i+= 2){
                            String s1 = split[i];
                            String s2 = split[i + 1];

                            s1= s1.replace("[", "");
                            s1= s1.replace("\"", "");
                            s1= s1.replace("]", "");

                            s2= s2.replace("[", "");
                            s2= s2.replace("\"", "");
                            s2= s2.replace("]", "");

                            AdapterAdd(s1, s2, i/2 + 1);
                        }



                        recyclerView.setAdapter(adapter);
                    }
                },
                //TODO ????????? ?????? ?????? ?????? ??????
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("---","---");
                        Log.e("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(error.toString())+"]");
                        Log.e("//===========//","================================================");
                        Log.d("---","---");



                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    if (requestBody != null && requestBody.length() > 0 && !requestBody.equals("")) {
                        return requestBody.getBytes("utf-8");
                    } else {
                        return null;
                    }
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };

        Log.d(" RequestBody ", requestBody);
        request.setShouldCache(false);
        queue.add(request);

    }


    public void GET(final String url){
        // ?????? id??? ??????????????? ?????? ?????? , ????????? ????????? id ?????? ???????????????.
        //TODO ????????? Request ?????? ??????
        RequestQueue queue = Volley.newRequestQueue(this);

        //TODO ??????????????? ?????? ??????
        Map<String, String> params = new HashMap<>(); //TODO {userId=1} ??????

//        params.put("userId", "1");
//        params.put("userId", String.valueOf(id));

        //TODO ?????? Url ??? Data ?????? ??????
        String dataParse = "";
        String getUrl = "";
        dataParse = String.valueOf(params.toString());
        dataParse = dataParse.replaceAll("[{]","");
        dataParse = dataParse.replaceAll("[}]","");
        dataParse = dataParse.replaceAll("[,]","&");
        getUrl = url ; // + "?" + dataParse;
        getUrl = getUrl.replaceAll(" ","");
        Log.d("---","---");
        Log.w("//===========//","================================================");
        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(url)+"]");
        Log.d("","\n"+"["+"?????? ??? - "+String.valueOf(params)+"]");
        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(getUrl)+"]");
        Log.w("//===========//","================================================");
        Log.d("---","---");

        //TODO ????????? Response ?????? ??????


        final String requestBody = "";

        StringRequest request = new StringRequest(Request.Method.GET, getUrl,
                //TODO ????????? ?????? ?????? ??????
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("---","---");
                        Log.w("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(response.toString())+"]");
                        Log.w("//===========//","================================================");
                        Log.d("---","---");


                        recyclerView.setAdapter(adapter);
//                        editText.setText(response);

                        String[] split = response.split(",");

                        for(int i=0; i<split.length; i+= 2){
                            String s1 = split[i];
                            String s2 = split[i + 1];

                            s1= s1.replace("[", "");
                            s1= s1.replace("\"", "");
                            s1= s1.replace("]", "");

                            s2= s2.replace("[", "");
                            s2= s2.replace("\"", "");
                            s2= s2.replace("]", "");

                            AdapterAdd(s1, s2, i/2 + 1);
                        }



                        recyclerView.setAdapter(adapter);
                    }
                },
                //TODO ????????? ?????? ?????? ?????? ??????
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("---","---");
                        Log.e("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(error.toString())+"]");
                        Log.e("//===========//","================================================");
                        Log.d("---","---");



                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    if (requestBody != null && requestBody.length() > 0 && !requestBody.equals("")) {
                        return requestBody.getBytes("utf-8");
                    } else {
                        return null;
                    }
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };

        Log.d(" RequestBody ", requestBody);
        request.setShouldCache(false);
        queue.add(request);

    }



    public void GET_GROUP(final String url, Long id){
        // ?????? id??? ??????????????? ?????? ?????? , ????????? ????????? id ?????? ???????????????.
        //TODO ????????? Request ?????? ??????
        RequestQueue queue = Volley.newRequestQueue(this);

        //TODO ??????????????? ?????? ??????
        Map<String, String> params = new HashMap<>(); //TODO {userId=1} ??????

        params.put("id", String.valueOf(id));
//        params.put("userId", String.valueOf(id));

        //TODO ?????? Url ??? Data ?????? ??????
        String dataParse = "";
        String getUrl = "";
        dataParse = String.valueOf(params.toString());
        dataParse = dataParse.replaceAll("[{]","");
        dataParse = dataParse.replaceAll("[}]","");
        dataParse = dataParse.replaceAll("[,]","&");
        getUrl = url + "?" + dataParse;
        getUrl = getUrl.replaceAll(" ","");
        Log.d("---","---");
        Log.w("//===========//","================================================");
        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(url)+"]");
        Log.d("","\n"+"["+"?????? ??? - "+String.valueOf(params)+"]");
        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(getUrl)+"]");
        Log.w("//===========//","================================================");
        Log.d("---","---");

        //TODO ????????? Response ?????? ??????


        final String requestBody = "";

        StringRequest request = new StringRequest(Request.Method.GET, getUrl,
                //TODO ????????? ?????? ?????? ??????
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("---","---");
                        Log.w("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(response.toString())+"]");
                        Log.w("//===========//","================================================");
                        Log.d("---","---");



                        String[] split = response.split(",");
                        if(split.length != 1){
                            for(int i=0; i<split.length; i+= 3){
                                String s1 = split[i];
                                String s2 = split[i + 1];
                                String s3 = split[i + 2];

                                s1 = s1.replaceAll("[^0-9]", "");
                                if(i == 0)
                                    group1.setText("?????? : " + s1);
                            }
                        }

                    }
                },
                //TODO ????????? ?????? ?????? ?????? ??????
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("---","---");
                        Log.e("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(error.toString())+"]");
                        Log.e("//===========//","================================================");
                        Log.d("---","---");



                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    if (requestBody != null && requestBody.length() > 0 && !requestBody.equals("")) {
                        return requestBody.getBytes("utf-8");
                    } else {
                        return null;
                    }
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };

        Log.d(" RequestBody ", requestBody);
        request.setShouldCache(false);
        queue.add(request);

    }


    public void GET_GROUP_TIME(final String url, int id){
        // ?????? id??? ??????????????? ?????? ?????? , ????????? ????????? id ?????? ???????????????.
        //TODO ????????? Request ?????? ??????
        RequestQueue queue = Volley.newRequestQueue(this);

        //TODO ??????????????? ?????? ??????
        Map<String, String> params = new HashMap<>(); //TODO {userId=1} ??????

        params.put("id", String.valueOf(id));
//        params.put("userId", String.valueOf(id));

        //TODO ?????? Url ??? Data ?????? ??????
        String dataParse = "";
        String getUrl = "";
        dataParse = String.valueOf(params.toString());
        dataParse = dataParse.replaceAll("[{]","");
        dataParse = dataParse.replaceAll("[}]","");
        dataParse = dataParse.replaceAll("[,]","&");
        getUrl = url + "?" + dataParse;
        getUrl = getUrl.replaceAll(" ","");
        Log.d("---","---");
        Log.w("//===========//","================================================");
        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(url)+"]");
        Log.d("","\n"+"["+"?????? ??? - "+String.valueOf(params)+"]");
        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(getUrl)+"]");
        Log.w("//===========//","================================================");
        Log.d("---","---");

        //TODO ????????? Response ?????? ??????


        final String requestBody = "";

        StringRequest request = new StringRequest(Request.Method.GET, getUrl,
                //TODO ????????? ?????? ?????? ??????
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("---","---");
                        Log.w("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(response.toString())+"]");
                        Log.w("//===========//","================================================");
                        Log.d("---","---");



                        String[] split = response.split(",");

                        for(int i=0; i<split.length; i+= 3){
                            String s1 = split[i];
                            String s2 = split[i + 1];
                            String s3 = split[i + 2];

                            s1 = s1.replaceAll("[^0-9]", "");
                            if(i == 0)
                                group1.setText("?????? : " + s1);

                        }

                    }
                },
                //TODO ????????? ?????? ?????? ?????? ??????
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("---","---");
                        Log.e("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyGET() ????????? : Volley GET ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(error.toString())+"]");
                        Log.e("//===========//","================================================");
                        Log.d("---","---");



                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    if (requestBody != null && requestBody.length() > 0 && !requestBody.equals("")) {
                        return requestBody.getBytes("utf-8");
                    } else {
                        return null;
                    }
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };

        Log.d(" RequestBody ", requestBody);
        request.setShouldCache(false);
        queue.add(request);

    }

    public void POST_BODY_JSON_JOIN(final String url, Context context, int groupId, String password, int user_id){

        Log.d(" groupId, pw, userid ->" , groupId + ", " + password + ", " + user_id);

        //TODO ????????? Request ?????? ??????
        RequestQueue queue = Volley.newRequestQueue(context);

        //TODO ??????????????? ?????? ??????
        JSONObject jsonBodyObj = new JSONObject();
        try{
            //" { \"age\" : 24, \"coin\" : 3, \"name\" : \"jisoo\"} ");
            jsonBodyObj.put("group_id", groupId);
            jsonBodyObj.put("password", password);
            jsonBodyObj.put("user_id", user_id);
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

                        String str = "";
                        int possible = 1;
                        Log.d("---","---");
                        Log.w("//===========//","================================================");
                        Log.d("","\n"+"[A_Main > getRequestVolleyPOST_BODY_JSON() ????????? : Volley POST_BODY_JSON ?????? ??????]");
                        Log.d("","\n"+"["+"?????? ?????? - "+String.valueOf(response.toString())+"]");
                        Log.w("//===========//","================================================");
                        Log.d("---","---");




                        String fail = "";


                        try {
                            fail = String.valueOf(response.get("fail"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(fail.equals("success")){
                            Toast.makeText(getApplicationContext(), "?????? ?????? ?????? : " + fail, Toast.LENGTH_LONG).show();
                            group1.setText("?????? : " + groupId);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "?????? ??????. ?????? : " + fail, Toast.LENGTH_LONG).show();
                        }


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

    public void POST_BODY_JSON_CREATE(final String url, Context context, String groupname, String password, int user_id){
        //TODO ????????? Request ?????? ??????
        RequestQueue queue = Volley.newRequestQueue(context);

        //TODO ??????????????? ?????? ??????
        JSONObject jsonBodyObj = new JSONObject();
        try{
            //" { \"age\" : 24, \"coin\" : 3, \"name\" : \"jisoo\"} ");
            jsonBodyObj.put("groupname", groupname);
            jsonBodyObj.put("password", password);
            jsonBodyObj.put("user_id", user_id);
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

                        int id = 0;
                        String name = "";
                        String password = "";


                        try {
                            id = (int) response.get("id");
                            name = String.valueOf(response.get("name"));
                            password = String.valueOf(response.get("password"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getApplicationContext(), "???????????? " + id + "\n ???????????????" + password, Toast.LENGTH_LONG).show();
                        group1.setText("?????? : " + id);
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
